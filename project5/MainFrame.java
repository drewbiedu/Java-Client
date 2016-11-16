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


public class MainFrame extends JFrame {
	private ChatClient client;
	private JLabel serverStatus;
	private JLabel serverStatusLabel;
	private JPanel buttonPanel;
	private JPanel errorPanel;
	private JPanel textPanel;
	private JTextArea display;
	private JTextArea errorLog;
	private JTextArea message;
	// private PrintWriter out;
	private String username;

	final int defaultRows = 10;	// default height for text areas
	final int defaultCols = 45;	// default width for text areas

	/**
	 * Desc: default constructor for MainFrame
	 * @param: PrintWriter pWriter
	 * @return: nothing
	 */
	public MainFrame(ChatClient c) {
		client = c;
		ChatListener l = new ChatListener();
		client.addReceivedListener(l);
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

 		String tempStat;
 		Color tempColor;

 		// check the server status
 		//if(client.isConnected()) {
 			tempStat = "Connected";
 			tempColor = Color.GREEN;
 		//}
 		// else {
 		// 	tempStat = "Disconnected";
 		// 	tempColor = Color.RED;
 		// }

 		serverStatus = new JLabel(tempStat);
 		serverStatus.setForeground(tempColor);
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


	class ChatListener implements MessageReceivedListener {
		@Override
		public void messageReceived(String msg) {
			printMessage(msg);
		}// end messageReceived
	}// end ConsoleListener


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


	public String getServerStatus() {
		return serverStatus.getText();
	}// end getServerStatus()


	/**
	 * Desc: prints the client message to the screen
	 * @param: PrintWriter pWriter String status
	 * @return: void
	 */
	public void printMessage(String message) {
		display.append(message);
	}// end printMessage(...)


	private class SendListener implements ActionListener {
		/**
		 * Desc: sends the message across the chat server
		 * @param: ActionEvent event
		 * @return: void
		 */
		public void actionPerformed(ActionEvent event) {
			String currentMessage = message.getText();

			try {
				// make sure the server is connected
				if(!(client.isConnected()))
					printError("\nYou aren't currently connected to a server!");

				// check for valid message
				else if(currentMessage.equals(""))
					printError("\nCannot send blank message!");
				else {
					// send message to the PrintWriter
					// printMessage(getOut(), currentMessage);
					client.writeMessage("\n" + currentMessage);
				}
			}// end try block
			catch(Exception e) {
				printError("\nError sending message!");
			}// end catch block

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
			if(!(client.isConnected()))
				printError("You are currently not connected to a server - please try connecting first");
			else {
				updateServerStatus("disconnect");
			}

			try {
				client.disconnect();
				printError("The server has disconnected.");
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("failed");
				System.exit(0);
			}
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