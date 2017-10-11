

import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
import java.net.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



class ftp_client { 

    public static void main(String argv[]) throws Exception {
        String sentence; 
        String modifiedSentence; 
        boolean isOpen = true;
        int number=1;
        final int commandPort = 6603, dataPort = 6605;
        boolean notEnd = true;
        
        
		String statusCode;
		boolean clientgo = true;
	    
		System.out.print("Enter a command: ");
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        sentence = inFromUser.readLine();
        
        
        StringTokenizer tokens = new StringTokenizer(sentence);

		if(sentence.startsWith("connect")){
			String serverName = tokens.nextToken(); // pass the connect command
			serverName = tokens.nextToken();
			//int port1 = Integer.parseInt(tokens.nextToken());
			System.out.println("You are connected to " + serverName);
			
			Socket ControlSocket= new Socket(serverName, commandPort);
			
			while(isOpen && clientgo){
			  
				DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
				DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
				System.out.print("Enter a command: ");
				sentence = inFromUser.readLine();
			   
				if(sentence.equals("list:")){
					
					//port1 = port1 +2;
					//System.out.println(dataPort);
					ServerSocket welcomeData = new ServerSocket(dataPort);
					outToServer.writeBytes(dataPort + " " + sentence + " " + '\n');

					Socket dataSocket = welcomeData.accept(); 
					BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
					String string;
					while((string = inData.readLine()) != null){
						System.out.println(string);
						
						}
					
				
					welcomeData.close();
					dataSocket.close();
					System.out.println("\nWhat would you like to do next: \n retr: file.txt || stor: file.txt  || close");
				}
				
				else if(sentence.startsWith("retr:")){
					// fixme
					//port1 += 2;
					ServerSocket serverRequest = new ServerSocket(dataPort);
					outToServer.writeBytes(dataPort + " " + sentence + " " + '\n');
					Socket input = serverRequest.accept();
					
					DataInputStream stream = new DataInputStream(new BufferedInputStream(input.getInputStream()));
					int code = stream.readInt();
					if(code == 404) {
						System.out.println("File Not Found");
					}
					//FIXME possible issue with filenames and substring method
					File save = new File("./Files/" + sentence.substring(6));
					if(code == 200) {
						if(save.exists()) {
					
						System.out.println("File already exists. Overwrite? (yes/no)");
						}
						if(inFromUser.readLine().toLowerCase().startsWith("yes") || !save.exists()) {
				
					FileOutputStream saver = new FileOutputStream(save);
					
					byte[] buffer = new byte[1024];
					int bytes = 0;
					while((bytes = stream.read(buffer)) != -1){
						saver.write(buffer, 0, bytes); 
					}
					
					System.out.println("File \"" + save.getName() + "\" has been retrieved");
					saver.close();
					input.close();
					}
					}
					stream.close();
					serverRequest.close();
				
				}
				
				else if(sentence.startsWith("stor")){
					//port1 += 2;
					
					ServerSocket output = new ServerSocket(dataPort);
					Socket client = output.accept();
					Socket dataSocket= new Socket(serverName, dataPort);
					try{
						
					
						
					DataOutputStream streamOutput = new DataOutputStream(dataSocket.getOutputStream());
					
					byte[] buffer = new byte[1024];
					
					//Sends the sentence (command) to the server
					streamOutput.writeBytes(sentence);
					
					//gets the path for the file we want to send
					Path path = Paths.get("./" + sentence.substring(6));
					
					File file = new File("./" + sentence.substring(6));
					
					FileInputStream taker = new FileInputStream(file);
					
					
					int bytes = 0;
					
					//writes the bytes from our file into the byte array
					buffer = Files.readAllBytes(path);
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
					

					outToServer.writeBytes(dataPort + " quit " + '\n');
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