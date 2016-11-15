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


import java.io.*;
import java.net.*;
import java.util.*;


// public class EchoServer {
// 	public static void main(String[] args) {
// 		try {
// 			int i = 1;
// 			ServerSocket s = new ServerSocket(8189);
// 			while (true) {
// 				Socket incoming = s.accept();
// 				System.out.println("Spawning " + i);
// 				Runnable r = new ThreadedEchoHandler(incoming);
// 				Thread t = new Thread(r);
// 				t.start();
// 				i++;
// 			}// end while-loop
// 		}// end try block
// 		catch (IOException e) {
// 			e.printStackTrace();
// 		}// end catch block
// 	}// end main(...)
// }
// /*
// This class handles the client input for one server socket
// connection.
// */
// class ThreadedEchoHandler implements Runnable {
// 	private Socket incoming;
// 	/*
// 	Constructs a handler.
// 	@param i the incoming socket
// 	*/
// 	public ThreadedEchoHandler(Socket i) {
// 		incoming = i;
// 	}

// 	public void run() {
// 		try {
// 			try {
// 				InputStream inStream = incoming.getInputStream();
// 				OutputStream outStream = incoming.getOutputStream();

// 				Scanner in = new Scanner(inStream);
// 				PrintWriter out = new PrintWriter(outStream, true/* autoFlush */);

// 				out.println( "Hello! Enter BYE to exit." );
// 				// echo client input
// 				boolean done = false;
// 				while (!done && in.hasNextLine()) {
// 					String line = in.nextLine();
// 					out.println("Echo: " + line);
// 					if (line.trim().equals("BYE"))
// 					done = true;
// 				}// end while-loop
// 			}// end try block
// 			finally {
// 				incoming.close();
// 			}// end finally block
// 		}// end try block
// 		catch (IOException e) {
// 			e.printStackTrace();
// 		}// end catch block
// 	}
// }// end class ThreadedEchoHandler








public class EchoServer {
	public static void main(String[] args) throws IOException {
		// establish server socket
		try (ServerSocket s = new ServerSocket(4688)) {
			// wait for client connection
			try (Socket incoming = s.accept()) {
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();

				try (Scanner in = new Scanner(inStream)) {
					// auto-flush
					PrintWriter out = new PrintWriter(outStream, true);
					out.println("Hello! Enter BYE to exit.");

					// echo client input
					boolean done = false;
					while(!done && in.hasNextLine()) {
						String line = in.nextLine();
						out.println("Echo: " + line);

						if(line.trim().equals("BYE"))
							done = true;
					}// end while-loop
				}// end try block
			}// end try block
		}// end try block
	}// end main(...)
}// end class EchoServer