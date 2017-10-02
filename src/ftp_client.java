

import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
class ftp_client { 

    public static void main(String argv[]) throws Exception {
        String sentence; 
        String modifiedSentence; 
        boolean isOpen = true;
        int number=1;
        boolean notEnd = true;
		String statusCode;
		boolean clientgo = true;
	    
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);

		if(sentence.startsWith("connect")){
			String serverName = tokens.nextToken(); // pass the connect command
			serverName = tokens.nextToken();
			int port1 = Integer.parseInt(tokens.nextToken());
			System.out.println("You are connected to " + serverName);
			
			Socket ControlSocket= new Socket(serverName, port1);
			
			while(isOpen && clientgo){
			  
				DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
				DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
				  
				sentence = inFromUser.readLine();
			   
				if(sentence.equals("list:")){
					
					port1 = port1 +2;
					System.out.println(port1);
					ServerSocket welcomeData = new ServerSocket(port1);
					outToServer.writeBytes(port1 + " " + sentence + " " + '\n');

					Socket dataSocket = welcomeData.accept(); 
					DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
					
					while(notEnd){
						modifiedSentence = inData.readUTF();
						// fixme 
					}
				
					welcomeData.close();
					dataSocket.close();
					System.out.println("\nWhat would you like to do next: \n retr: file.txt || stor: file.txt  || close");
				}
				
				else if(sentence.startsWith("retr: ")){
					// fixme
				}
			}
		}
	}
}