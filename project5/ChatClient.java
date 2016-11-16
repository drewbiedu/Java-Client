/**
 * Drew Adams
 * CS 3250, sec 601
 * Project 5
 * 11/15/2016
 * Description: 
 * I declare that the following source code is my work.
 * I understand and can explain everything I have written, if asked.
 * I understand that copying any source code, in whole or in part,
 * that is not in my textbook nor provided or expressly permitted by the instructor, 
 * constitutes cheating. I will receive a zero on this project for 
 * poor academic performance if I am found in violation of this policy.
 */

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;


interface MessageReceivedListener {
	void messageReceived(String msg);
}// end MessageReceivedListener


class ConsoleListener implements MessageReceivedListener {
	@Override
	public void messageReceived(String msg) {
		System.out.println("Message Recieved: " + msg);
	}// end messageReceived
}// end ConsoleListener


public class ChatClient {
	private String user = "Anonymous";
	private String host = "localhost";
	private int port = 4688;	
	private List<MessageReceivedListener> listeners;	
	//Don't buffer any more than 10 messages at a time.
	private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
	private ChatSocket socket;

	ExecutorService pool = Executors.newFixedThreadPool(1);


	public ChatClient() throws UnknownHostException, IOException {
		listeners = new ArrayList<MessageReceivedListener>();
		socket = new ChatSocket();
	}// end ChatClient()

		
	public ChatClient(String username, int portNum) throws UnknownHostException, IOException {
		this();
		user = username;
		port = portNum;
	}// end ChatClient(String username, int portNum)

	
	public void addReceivedListener(MessageReceivedListener listener) {
		listeners.add(listener);
	}// end addReceivedListener(...)

	
	public void connect() {
		pool.submit(socket);
	}// end connect()

	
	public boolean isConnected() {
		return socket.isConnected();
	}// end isConnected()


	public void disconnect() throws InterruptedException {
		queue.offer("BYE");
		pool.shutdown();
		socket.disconnect();
		pool.awaitTermination(5, TimeUnit.SECONDS);
	}// end disconnect()

	
	public synchronized void writeMessage(String message) throws IOException {
		//System.out.println(message);
		queue.offer(message);
	}// end writeMessage(String message)

		
	class ChatSocket extends Thread implements Runnable {
		boolean disconnect = false;
		Socket so;
		
		public ChatSocket() {

		}// end ChatSocket()
		
		private synchronized void disconnect() {
			disconnect = true;
		}// end disconnect()
		
		private synchronized boolean isConnected() {
			if (so == null || so.isClosed()) {
				return false;
			}
			return true;
		}// end isConnected()


		@Override
		public void run() {
			try {
				so = new Socket("localhost", port);
				BufferedReader in = new BufferedReader(new InputStreamReader(so.getInputStream()));
	            PrintWriter writer = new PrintWriter(so.getOutputStream(), true);
				
				while (!disconnect) {
					
					String message = queue.poll();
					if (message != null) {
						writer.println(message);
					}
					
					String line = in.readLine();
					if (line != null) {
						for(MessageReceivedListener listener : listeners) {
							listener.messageReceived(line);
						}
					}
				}// end while-loop
				writer.println("Disconnecting from server... ... ...");
				
				// check if there are any messages remaining in the message queue
				if (queue.size() > 0) {
					System.out.println("Some messages were not delivered!");
				}

				writer.println("Disconnected");
				so.close();
			}// end try block
			catch (IOException e) {
				e.printStackTrace();
			}// end catch block
		}// end run()
	}// end class ChatSocket


	/**
	 * Desc: main function
	 * 
	 * @param: String[] args
	 * @return: void
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		String user = "Anonymous";
		int port = 4688;

		try {
			if(args.length > 0) {
				user = args[0];

				if(args.length > 1)
					port = Integer.parseInt(args[1]);
			}
		}// end try block
		catch(Exception e) {
			System.exit(0);
		}// end catch block

		final String tempUser = user;
		final int tempPort = port;

		//final ChatClient c = new ChatClient();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ChatClient c = new ChatClient(tempUser, tempPort);
					JFrame frame = new MainFrame(c);
					frame.setTitle("Project 5 - Chat Client");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					c.connect();
				}// end try block
				catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}// end catch block				
			}// end run()
		});// end EventQueue thread
	}// end main(String[] args)
}// end class ChatClient