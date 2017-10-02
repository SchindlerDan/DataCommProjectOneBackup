

import java.io.*; 
import java.net.*;
import java.util.*;
// fixme
class ftp_server {
    public static void main(String argv[]) throws Exception {


		String fromClient;
		String clientCommand;
		byte[] data;
		Socket dataSocket;
	
		ServerSocket welcomeSocket = new ServerSocket(12000);
		String frstln;
	  
		while(true){
			Socket connectionSocket = welcomeSocket.accept();
			DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
			fromClient = inFromClient.readLine();
			
			StringTokenizer tokens = new StringTokenizer(fromClient);
		
			frstln = tokens.nextToken();
			int port = Integer.parseInt(frstln);
			clientCommand = tokens.nextToken();
			
			if(clientCommand.equals("list:")){ 
				dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataOutputStream  dataOutToClient = 
				new DataOutputStream(dataSocket.getOutputStream());
				// FIXME 
			

			dataSocket.close();
			System.out.println("Data Socket closed");
			
			}
			// fixme
			 
			if(clientCommand.equals("retr:")){
				// fixme
			}
		}
    }
}