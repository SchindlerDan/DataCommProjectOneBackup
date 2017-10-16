
import java.io.*;
import java.net.*;
import java.util.*;

class ftp_server {
	public static final int LISTEN_PORT = 6603;
	public static final int COMMAND_PORT = 6605;

	public static void main(String argv[]) throws Exception {

		ServerSocket welcomeSocket;

		welcomeSocket = new ServerSocket(LISTEN_PORT);

		do {
			Socket connectionSocket;

			connectionSocket = welcomeSocket.accept();

			ClientHandler handler = new ClientHandler(connectionSocket);
			handler.start();

		} while (true);
	}
}

class ClientHandler extends Thread {
	private Socket dataSocket;
	private Socket connectionSocket;
	private String fromClient;
	private String clientCommand;
	private String frstln;

	public ClientHandler(Socket socket) {
		connectionSocket = socket;
	}

	public void run() {
		boolean running = true;
		while(running){
		
		try {
			BufferedReader inFromClient;

			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			fromClient = inFromClient.readLine();

			StringTokenizer tokens = new StringTokenizer(fromClient);

			frstln = tokens.nextToken();
			int port = Integer.parseInt(frstln);
			clientCommand = tokens.nextToken();

			if (clientCommand.equals("list:")) {
				dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataOutputStream dataOut = new DataOutputStream(dataSocket.getOutputStream());
				// begin new code.

				File localStorage = new File("./ServerFiles"); 
				File[] localFiles = localStorage.listFiles();

				for (File f : localFiles) {
					dataOut.writeBytes(f.getName() + '\n');
					System.out.print(f.getName());
				}

				// Resume given code.
				dataSocket.close();
				// dataOut.close();
				System.out.println("Data Socket closed");

			}
			// begin new code.

			// end new code.
			if (clientCommand.equals("retr:")) {
				// begin new code.

				dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataOutputStream dataOut = new DataOutputStream(dataSocket.getOutputStream());
				try {
					String fileName = tokens.nextToken();
					System.out.println(fileName);
					File f = new File(fileName);
					if (f.exists()) {
						dataOut.writeInt(200);
						FileInputStream fileIn = new FileInputStream(f);

						byte[] fileBytes = new byte[(int) f.length()];

						fileIn.read(fileBytes);
						dataOut.write(fileBytes);

						fileIn.close();

						System.out.println("Data Stream closed");
					} else

						dataOut.writeInt(404);
					dataOut.close();
					System.out.println("Data Socket closed");
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}

				// end new code.
			}
			if (clientCommand.equals("stor:")) {
				boolean flag = true;
				dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataInputStream dataIn = new DataInputStream((dataSocket.getInputStream()));
				DataOutputStream dataOut = new DataOutputStream((dataSocket.getOutputStream()));

				String fileName = tokens.nextToken();
				// FIXME possible issue with filenames and substring method
				File save = new File("./ServerFiles/" + fileName);

				// BufferedReader br = new BufferedReader( new
				// FileReader(save.getName()));
				if (!save.createNewFile()) {

					dataOut.writeInt(350);
					flag = dataIn.readBoolean();

				} else {
					dataOut.writeInt(200);
				}
				System.out.println(flag);
				if (flag) {

					FileOutputStream saver = new FileOutputStream(save);

					byte[] buffer = new byte[1024];
					int bytes = 0;
					while ((bytes = dataIn.read(buffer)) != -1) {
						saver.write(buffer, 0, bytes);
					}

					System.out.println("File \"" + save.getName() + "\" has been retrieved");
					saver.close();
				}
				// br.close();
				dataIn.close();
				dataOut.close();
				dataSocket.close();
			}

			if (clientCommand.equals("quit")) {

				connectionSocket.close();
				running = false;
			}
		} catch (Exception e) {
		}
		}

	}
}
