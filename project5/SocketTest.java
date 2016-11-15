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


public class SocketTest {
	public static void main(String args[]) throws IOException {
		try (Socket s = new Socket("localhost", 4688)) {
			InputStream inStream = s.getInputStream();
			OutputStream outStream = s.getOutputStream();
			Scanner in = new Scanner(inStream);


			PrintWriter out = new PrintWriter(outStream, true);
			out.println("hello");

			while(in.hasNextLine()) {
				String line = in.nextLine();
				System.out.println(line);
			}// end while-loop
		}// end try block
	}// end main(...)
}// end class SocketTest