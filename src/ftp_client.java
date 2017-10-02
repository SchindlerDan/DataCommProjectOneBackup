

import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
import java.net.*;
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
						System.out.println(modifiedSentence);
						// fixme  note, add final string from server to trigger 'notEnd' being changed
						if(modifiedSentence == null){
							notEnd = false;
						}
					}
				
					welcomeData.close();
					dataSocket.close();
					System.out.println("\nWhat would you like to do next: \n retr: file.txt || stor: file.txt  || close");
				}
				
				else if(sentence.startsWith("retr: ")){
					// fixme
					port1 += 2;
					ServerSocket serverRequest = new ServerSocket(port1);
					Socket input = serverRequest.accept();
					
					DataInputStream stream = new DataInputStream(new BufferedInputStream(input.getInputStream()));
					
					
					//FIXME possible issue with filenames and substring method
					File save = new File(sentence.substring(6));
					FileOutputStream saver = new FileOutputStream(sentence.substring(6));
					
					byte[] buffer = new byte[1024];
					int bytes = 0;
					
					while((bytes = stream.read(buffer)) != -1){
						saver.write(buffer);
					}
					
					serverRequest.close();
					saver.close();
					input.close();
					stream.close();
					
				}
				
				else if(sentence.startsWith("stor: ")){
					port1 += 2;
					
					ServerSocket output = new ServerSocket(port1);
					Socket client = output.accept();
					Socket dataSocket= new Socket(serverName, port1);
					try{
						
					
						
					DataOutputStream streamOutput = new DataOutputStream(dataSocket.getOutputStream());
					FileInputStream taker = new FileInputStream(sentence.substring(6));
					byte[] buffer = new byte[1024];
					
					streamOutput.writeBytes(sentence);
					
					
					
					
					int bytes = 0;
					
					while((bytes = taker.read(buffer)) != -1){
						streamOutput.write(buffer);
					}
					
					taker.close();
					streamOutput.close();
					}catch(FileNotFoundException e){
						System.out.println("Specified file was not found");
					}
					
					client.close();
					output.close();
					dataSocket.close();
					
					
				}
				else if(sentence.startsWith("quit")){
					
					inFromUser.close();
					ControlSocket.close();
					outToServer.close();
					inFromServer.close();
					
					clientgo = false;
					
				}
				
				
				
				
				
				
				
				
			}
		}
	}
}