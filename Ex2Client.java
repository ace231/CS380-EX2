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
	// use System.out.println(String.format("0x%08X", 234));
	public static void main(String[] args) {
		// Byte array to hold 100 bytes
		byte[] serverSeq = new byte[100];
		int first4Bits, second4Bits;
		
		try(Socket socket = new Socket("18.221.102.182", 38102)) {
			
			// Checking that connection went through
			String address = socket.getInetAddress().getHostAddress();
			System.out.printf("Connected to server: %s%n", address);
			
			// Creating client input stream to receive messages from server	
			InputStream is = socket.getInputStream();
			
			// Creating client output stream to send messages to server
			OutputStream os = socket.getOutputStream();
			PrintStream out = new PrintStream(os, true, "UTF-8");
			
			int arrIndex = 0;
			System.out.printf("Received bytes:\n  ");
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
				
				System.out.print(byteString);
				serverSeq[arrIndex] =  (byte)(temp);
				arrIndex++;
				if(arrIndex % 10 == 0) {System.out.print("\n  ");}
			}
			
			CRC32 crcCode = new CRC32();
			crcCode.update(serverSeq, 0, 100);
			String crcStr = Long.toHexString(crcCode.getValue());
			System.out.println("Generated CRC32: " + crcStr);
			
			byte[] crcToServer = new byte[4];
			ByteBuffer crcBytes = ByteBuffer.allocate(4);
			crcBytes.putInt((int)(crcCode.getValue()));
			crcToServer = crcBytes.array();
			os.write(crcToServer);
			
			is = socket.getInputStream();
			int response = is.read();
			if(response == 0) {
				System.out.println("BAD RESPONSE.");
			} else if(response == 1) {
				System.out.println("Good response.");
			}
			
		} catch(Exception e) {
			System.out.println("Something went wrong...");
		}
	}
}