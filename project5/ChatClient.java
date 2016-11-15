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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.util.Scanner;


public class ChatClient {
	private static PrintWriter out;
	private static String user;

	/**
	 * Desc: main function
	 * @param: String[] args
	 * @return: void
	 */
	public static void main(String[] args) {
		user = "Anonymous";
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

		// build the GUI
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new MainFrame(out, user);
				frame.setTitle("Project 5 - Chat Client");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}// end run()
		});// end EventQueue thread

		Thread t = new ChatSocket(out, user, port);
		t.start();
	}// end main(...)
}// end class JIMachine


class ChatSocket extends Thread implements Runnable {
	InputStream inStream;
	OutputStream outStream;
	String username;
	int port;

	public ChatSocket(PrintWriter out, String username, int port) {
		this.username = username;
	}// end ChatSocket(...)

	public void run() {
		try (Socket s = new Socket("localhost", port)) {
			inStream = s.getInputStream();
			outStream = s.getOutputStream();
			Scanner in = new Scanner(inStream);

			PrintWriter out = new PrintWriter(outStream, true);
			out.println("connect " + username);
			// chatGUI.SendListener(out);
			// out.println("hello");

			while(in.hasNextLine()) {
				String line = in.nextLine();
				System.out.println(username + " " + line);	
			}// end while-loop
		}// end try block
		catch(IOException e) {
			// chatGUI.printError("The server has closed: " + e);
		}// end catch block
	}// end run()
}// end class ChatSocket


class MainFrame extends JFrame {
	private JLabel serverStatus;
	private JLabel serverStatusLabel;
	private JPanel buttonPanel;
	private JPanel errorPanel;
	private JPanel textPanel;
	private JTextArea display;
	private JTextArea errorLog;
	private JTextArea message;
	private PrintWriter out;
	private String username;

	int defaultRows = 10;
	int defaultCols = 45;

	/**
	 * Desc: default constructor for MainFrame
	 * @param: PrintWriter pWriter
	 * @return: nothing
	 */
	public MainFrame(PrintWriter pWriter, String username) {
		this.out = pWriter;
		this.username = username;
		// event listeners
		ClearLogListener clearLogCommand = new ClearLogListener();
		SendListener sendCommand = new SendListener();
		DisconnectListener disconnectCommand = new DisconnectListener();
 		CloseListener closeCommand = new CloseListener();

		Toolkit kit = Toolkit.getDefaultToolkit();
 		Dimension screenSize = kit.getScreenSize();

 		// set the dimensions and location of the GUI
 		setSize(screenSize.width / 2, screenSize.height / 2);
 		//setLocationByPlatform(true);
 		setLocationRelativeTo(null);


 		// ------------------------- errorPanel ------------------------- //
 		errorPanel = new JPanel();
 		errorPanel.setBorder(new TitledBorder(new EtchedBorder(), "Error Log"));
 		errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));

 		// create the error log text area
 		errorLog = new JTextArea(10, 20);
 		errorLog.setLineWrap(true);
 		errorLog.setWrapStyleWord(true);
 		errorLog.setEditable(false);
 		JScrollPane errorScroll = new JScrollPane(errorLog);
 		// add the error scroll to errorPanel
 		errorPanel.add(errorScroll);

 		// add clear button
 		JButton clearButton = new JButton("Clear Log");
 		clearButton.addActionListener(clearLogCommand);
 		clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
 		errorPanel.add(clearButton);
 		add(errorPanel, BorderLayout.WEST);

 		
 		// ------------------------- textPanel ------------------------- //
 		textPanel = new JPanel();
 		textPanel.setBorder(new TitledBorder(new EtchedBorder(), "Chat ChatClient"));
 		// textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

 		// message display
 		display = new JTextArea(defaultRows, defaultCols);
 		display.setEditable(false);
 		JScrollPane displayScroll = new JScrollPane(display);
 		// add the display scroll to textPanel
 		textPanel.add(displayScroll);

 		// server status
 		serverStatusLabel = new JLabel("Server status: ", JLabel.CENTER);
 		textPanel.add(serverStatusLabel);
 		serverStatus = new JLabel("Connected");
 		serverStatus.setForeground(Color.GREEN);
 		textPanel.add(serverStatus);

 		// user message
 		message = new JTextArea(4, defaultCols);
 		message.setEditable(true);
 		JScrollPane messageScroll = new JScrollPane(message);
 		// add the message scroll to textPanel
 		textPanel.add(messageScroll);


 		// add textPanel to MainFrame
 		add(textPanel, BorderLayout.CENTER);


 		// ------------------------- buttonPanel ------------------------- //
 		buttonPanel = new JPanel();

		// create menu buttons
		addButton("Send", sendCommand);
 		addButton("Disconnect", disconnectCommand);
 		addButton("Quit", closeCommand);

		add(buttonPanel, BorderLayout.SOUTH);

		// make the frame visible
		setVisible(true);

		// set focus to the message text area
		message.requestFocusInWindow();
	}// end MainFrame()


	/**
	 * Desc: getter for out
	 * @param: none
	 * @return: PrintWriter
	 */
	public PrintWriter getOut() {
		return out;
	}// end getOut()


	/**
	 * Desc: adds a button to the MainFrame panel
	 * @param: String label, ActionListener listener
	 * @return: void
	 */
	private void addButton(String label, ActionListener listener) {
		JButton button = new JButton(label);
		button.addActionListener(listener);
		buttonPanel.add(button);
	}// end addButton(...)


	/**
	 * Desc: adds a button to the MainFrame panel
	 * @param: String error
	 * @return: void
	 */
	private void printError(String error) {
		String separator = "---------------------------------------------------\n";
		
		errorLog.append(error + "\n");
		errorLog.append(separator);
	}// end printError(...)


	/**
	 * Desc: updates the server status text
	 * @param: String status
	 * @return: void
	 */
	private void updateServerStatus(String status) {
		if(status.equals("disconnect")) {
			serverStatus.setForeground(Color.RED);
			serverStatus.setText("Disconnected");
		}
		else if(status.equals("connect")) {
			serverStatus.setForeground(Color.GREEN);
			serverStatus.setText("Connected");
		}
	}// end updateServerStatus(...)


	/**
	 * Desc: prints the client message to the screen
	 * @param: PrintWriter pWriter String status
	 * @return: void
	 */
	public synchronized void printMessage(PrintWriter pWriter, String message) {
		if(pWriter != null)
			pWriter.println(username + message);
		else
			errorLog.setText("pWriter is NULL");
	}// end printMessage(...)


	private class ClearLogListener implements ActionListener {
		/**
		 * Desc: clears the error log
		 * @param: ActionEvent e
		 * @return: void
		 */
		public void actionPerformed(ActionEvent e) {
			errorLog.setText("");
		}// end actionPerformed(ActionEvent e)
	}// end class ClearListener


	private class SendListener implements ActionListener {
		/**
		 * Desc: sends the message across the chat server
		 * @param: ActionEvent event
		 * @return: void
		 */
		public void actionPerformed(ActionEvent event) {
			String currentMessage = message.getText();

			// check for valid message
			if(currentMessage.equals("")) {
				printError("Cannot send blank message!");
			}
			else {
				// send message to the PrintWriter
				printMessage(getOut(), currentMessage);
			}

			// clear the message field
			message.setText("");

			// set focus back to the message text area
			message.requestFocusInWindow();
			return;
		}// end actionPerformed(ActionEvent event)
	}// end class SendListener


	private class DisconnectListener implements ActionListener {
		/**
		 * Desc: disconnects the user from the chat server
		 * @param: ActionEvent e
		 * @return: void
		 */
		public void actionPerformed(ActionEvent e) {
			if(serverStatus.getText().equals("Disconnected"))
				printError("You are currently not connected to a server - please try connecting first");

			updateServerStatus("disconnect");
		}// end actionPerformed(ActionEvent e)
	}// end class DisconnectListener


	private class CloseListener implements ActionListener {
		/**
		 * Desc: exits the GUI
		 * @param: ActionEvent e
		 * @return: void
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}// end actionPerformed(ActionEvent e)
	}// end class CloseListener
}// end class MainFrame