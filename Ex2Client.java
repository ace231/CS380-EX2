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
import java.nio.ByteBuffer;


public class Ex2Client {
	
	public static void main(String[] args) {
		// Byte array to hold 100 bytes
		byte[] serverSeq = new byte[100];
		// int variables to hold incoming bytes
		int first4Bits, second4Bits;
		
		try(Socket socket = new Socket("18.221.102.182", 38102)) {
			
			// Checking that connection went through
			String address = socket.getInetAddress().getHostAddress();
			System.out.printf("Connected to server: %s%n", address);
			
			// Creating client input stream to receive messages from server
			InputStream is = socket.getInputStream();
			
			// Creating client output stream to send messages to server
			OutputStream os = socket.getOutputStream();
			//PrintStream out = new PrintStream(os, true, "UTF-8");
			
			int arrIndex = 0;
			System.out.println("Received bytes:");
			for(int i = 0; i < 100; i++) {
				
				// First 4 bits read and saved into temp
				first4Bits = is.read();
				int temp = first4Bits;
				String byteString = Integer.toHexString(first4Bits);
				
				// Temp bit shifted 4 times to allow room for next 4 bits
				temp = temp << 4;
				
				// Second 4 bits read in
				second4Bits = is.read();
				byteString = byteString + Integer.toHexString(second4Bits);
				temp = temp | second4Bits;
				
				if(arrIndex % 10 == 0) {System.out.print(" ");}
				System.out.print(byteString);
				serverSeq[arrIndex] =  (byte)(temp);
				arrIndex++;
				if(arrIndex % 10 == 0) {System.out.println();}
			}
			
			// New CRC32 object created using byte array from server.
			// An offset of 0 is used and the constructor is told the byte
			// array length is 100
			CRC32 crcCode = new CRC32();
			crcCode.update(serverSeq, 0, 100);
			
			// CRC32 returned as a long value then printed
			long crcVal = crcCode.getValue();
			System.out.println("Generated CRC32: " + crcVal);
			
			// Byte array of size 4 created to hold 32 bit CRC value
			byte[] crcToServer = new byte[4];
			/*
			 * ByteBuffer used to easily input 32 bit CRC into a byte array.
			 * However the getValue method for CRC32 objects returns a long value,
			 * which is 64 bits in size. To get around this the CRC long value
			 * is cast into an int value, and then shoved into the ByteBuffer.
			 * That data is then trasnferred to the crcToServer byte array with
			 * the array() method of the ByteBuffer class
			 */
			ByteBuffer crcBytes = ByteBuffer.allocate(4);
			crcBytes.putInt((int)(crcVal));
			crcToServer = crcBytes.array();
			os.write(crcToServer);	// Sending byte array to server
			
			is = socket.getInputStream();
			int response = is.read();
			
			// Checking response
			if(response == 0) {
				System.out.println("Response BAD...");
			} else if(response == 1) {
				System.out.println("Response good.");
			}
			
		} catch(Exception e) {
			System.out.println("Something went wrong...");
		} // End of try catch
		
	}// End of main

}