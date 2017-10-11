/*****************************************
*	Alfredo Ceballos
*	CS 380 - Computer Networks
*	Exercise 2
*	Professor Nima Davarpanah
*****************************************/
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.*;
import java.util.zip.CRC32;


public class Ex2Client {
	
	public static void main(String[] args) {
		// Byte array to hold 100 bytes
		byte[] serverSeq = new byte[100];
		byte first4Bits, second4Bits;
		try(Socket socket = new Socket("18.221.102.182", 38102)) {
			
			// Checking that connection went through
			String address = socket.getInetAddress().getHostAddress();
			System.out.printf("Connected to %s%n", address);
			
			// Creating client input stream to receive messages from server	
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			// Creating client output stream to send messages to server
			OutputStream os = socket.getOutputStream();
			PrintStream out = new PrintStream(os, true, "UTF-8");
			
		} catch(Exception e) {
			System.out.println("Something went wrong...");
		}
	}
}