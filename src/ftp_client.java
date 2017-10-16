
import java.io.*;
import java.net.*;
import java.util.*;
/*
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.*;
import java.lang.*;
import javax.swing.*;
import java.net.*;
import java.nio.*;
import java.nio.file.Files;
*/

class ftp_client {

	public static void main(String argv[]) throws Exception {
		String sentence;
		boolean isOpen = true;
		final int COMMAND_PORT = 6603, DATA_PORT = 6605;
		boolean clientgo = true;

		System.out.print("Enter a command: ");

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		sentence = inFromUser.readLine();

		StringTokenizer tokens = new StringTokenizer(sentence);

		if (sentence.startsWith("connect")) {
			String serverName = tokens.nextToken(); // pass the connect command
			serverName = tokens.nextToken();
			// int port1 = Integer.parseInt(tokens.nextToken());
			System.out.println("You are connected to " + serverName);

			Socket ControlSocket = new Socket(serverName, COMMAND_PORT);

			while (isOpen && clientgo) {

				DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
				DataInputStream inFromServer = new DataInputStream(
						new BufferedInputStream(ControlSocket.getInputStream()));
				System.out.print("Enter a command: ");
				sentence = inFromUser.readLine();

				if (sentence.equals("list:")) {

					// port1 = port1 +2;
					// System.out.println(DATA_PORT);
					ServerSocket welcomeData = new ServerSocket(DATA_PORT);
					outToServer.writeBytes(DATA_PORT + " " + sentence + " " + '\n');

					Socket dataSocket = welcomeData.accept();
					BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
					String string;
					while ((string = inData.readLine()) != null) {
						System.out.println(string);

					}

					welcomeData.close();
					dataSocket.close();
					System.out
							.println("\nWhat would you like to do next: \n retr: file.txt || stor: file.txt  || close");
				}

				else if (sentence.startsWith("retr:")) {
					boolean flag = true;
					ServerSocket serverRequest = new ServerSocket(DATA_PORT);
					outToServer.writeBytes(DATA_PORT + " " + sentence + " " + '\n');
					Socket input = serverRequest.accept();

					DataInputStream stream = new DataInputStream(new BufferedInputStream(input.getInputStream()));
					int code = stream.readInt();
					if (code == 404) {
						System.out.println("File Not Found");
					}
					// FIXME possible issue with filenames and substring method
					File save = new File("./Files/" + sentence.substring(6));

					save.createNewFile();
					if (code == 200) {

						BufferedReader br = new BufferedReader(new FileReader(save.getName()));
						if (br.readLine() != null) {

							System.out.println("File already exists. Overwrite? (yes/no)");
							if (inFromUser.readLine().toLowerCase().startsWith("no"))
								flag = false;
						}
						if (flag) {

							FileOutputStream saver = new FileOutputStream("./Files/" + sentence.substring(6));

							byte[] buffer = new byte[1024];
							int bytes = 0;
							while ((bytes = stream.read(buffer)) != -1) {
								saver.write(buffer, 0, bytes);
							}

							System.out.println("File \"" + save.getName() + "\" has been retrieved");
							saver.close();
							input.close();
						}
						br.close();
					}
					stream.close();
					serverRequest.close();

				}

				else if (sentence.startsWith("stor:")) {
					// port1 += 2;
					ServerSocket serverRequest = new ServerSocket(DATA_PORT);
					outToServer.writeBytes(DATA_PORT + " " + sentence + " " + '\n');
					Socket server = serverRequest.accept();
					try {

						DataOutputStream dataOut = new DataOutputStream((server.getOutputStream()));
						DataInputStream dataIn = new DataInputStream((server.getInputStream()));

						//byte[] buffer = new byte[1024];

						// Sends the sentence (command) to the server
						// dataOut.writeBytes(sentence);

						int found = dataIn.readInt();
						if (found == 350) {
							System.out.println("File already exists. Overwrite? (yes/no)");
							if (inFromUser.readLine().toLowerCase().startsWith("no")) {
								dataOut.writeBoolean(false);
							} else {
								dataOut.writeBoolean(true);
							}

						}
						// gets the path for the file we want to send
						//Path path = Paths.get("./Files/" + sentence.substring(6));

						File file = new File("./Files/" + sentence.substring(6));

						FileInputStream fileIn = new FileInputStream(file);

						byte[] fileBytes = new byte[(int) file.length()];

						fileIn.read(fileBytes);
						dataOut.write(fileBytes);

						fileIn.close();

						dataIn.close();
						dataOut.close();
					} catch (FileNotFoundException e) {
						System.out.println("Specified file was not found");
					}

					serverRequest.close();
					server.close();

				} else if (sentence.startsWith("quit")) {

					outToServer.writeBytes(DATA_PORT + " quit " + '\n');
					inFromUser.close();
					ControlSocket.close();
					outToServer.close();
					inFromServer.close();

					clientgo = false;

				} else {
					System.out.println("Invalid command");
				}

			}
		}
	}
}