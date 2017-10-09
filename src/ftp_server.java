

import java.io.*; 
import java.net.*;
import java.util.*;
//fixme
class ftp_server {
	public static final int LISTEN_PORT=6603;
	public static final int COMMAND_PORT=6605;


    public static void main(String argv[]) throws Exception {

    	
		String fromClient;
		String clientCommand;
		byte[] data;
		Socket dataSocket;
	
		ServerSocket welcomeSocket = new ServerSocket(LISTEN_PORT);
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
				DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
				//begin new code.
				
				File localStorage = new File("./");
				File[] localFiles = localStorage.listFiles();
				
//				String[] fileNames = new String[localFiles.length];
				
				for (File f:localFiles)
					dataOutToClient.writeBytes(f.getName());


				//Resume given code.
			dataSocket.close();    
			
			System.out.println("Data Socket closed");
			
			}
			//begin new code.

			
			
			
			
			
			
			//end new code. 
			if(clientCommand.equals("retr:")){
				// begin new code.
				
				dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
							
				
				try{
					String fileName=tokens.nextToken();
					File f=new File(fileName);
					
					
				}catch(Exception e){
					e.printStackTrace(System.out);
				}
				
				
				
				//end new code.
			}
		}
    }
}