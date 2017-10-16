
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

	private static void printInfo(){

				System.out.println("Possible Commands Include:");
				System.out.println("\tlist:\n\t\tLists All Available Files on the Server.");
				System.out.println("\tretr: <fileName>\n\t\tRetrieves a Specific File From the Server.");
				System.out.println("\tstor: <fileName>\n\t\tStores/Sends a Specified File (by name) to the Server to Store.");
				System.out.println("\tquit:\n\t\tEnds the Connection and Exits the Program.");
	}



	public static void main(String argv[]) throws Exception {
		String sentence;
		boolean isOpen = true;
		final int COMMAND_PORT = 6603, DATA_PORT = 6605;
		boolean clientgo = true;

		System.out.println("Enter the IP of your desired server: ");

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		sentence = inFromUser.readLine();

		StringTokenizer tokens = new StringTokenizer(sentence);

		if (sentence.startsWith("connect")) {
			String serverName = tokens.nextToken(); // pass the connect command
			serverName = tokens.nextToken();
			// int port1 = Integer.parseInt(tokens.nextToken());
			System.out.println("You are connected to " + serverName);
			printInfo();
			System.out.print("Enter a command: ");
			
			Socket ControlSocket = new Socket(serverName, COMMAND_PORT);

			while (isOpen && clientgo) {

				DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
				DataInputStream inFromServer = new DataInputStream(
						new BufferedInputStream(ControlSocket.getInputStream()));
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
					System.out.println("\nList Successful.");
					System.out.println("What would you like to do next?");
					printInfo();
				}

				else if (sentence.startsWith("retr:")) {
					boolean flag = true;
					ServerSocket serverRequest = new ServerSocket(DATA_PORT);
					outToServer.writeBytes(DATA_PORT + " " + sentence + " " + '\n');
					Socket input = serverRequest.accept();

					DataInputStream stream = new DataInputStream(new BufferedInputStream(input.getInputStream()));
					int code = stream.readInt();
					if (code == 404) {
						System.out.println("404 ERROR: File Not Found");
					}
					String fName=sentence.substring(6);
					File save = new File("./Files/" + fName);
					
					save.createNewFile();
					if (code == 200) {

						BufferedReader br = new BufferedReader(new FileReader(save.getName()));
						if (br.readLine() != null) {

							System.out.println("File already exists. Overwrite? (yes/no)");
							if (inFromUser.readLine().toLowerCase().startsWith("no"))
								flag = false;
						}
						if (flag) {

							FileOutputStream saver = new FileOutputStream("./Files/" + fName);

							byte[] buffer = new byte[1024];
							int bytes = 0;
							while ((bytes = stream.read(buffer)) != -1) {
								saver.write(buffer, 0, bytes);
							}

							System.out.println("File \"" + save.getName() + "\" has been retrieved");
							saver.close();
							input.close();
						}
						else{
							//THIS CREATES A TEMP FILE INSTEAD OF OVERWRITING.	
							FileOutputStream saver = new FileOutputStream("./Files/" +fName+"01");

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
					printInfo();
					System.out.print("Enter a Command: ");
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
					System.out.println("Goodbye!");

				} else {
					System.out.println("Invalid command");
					System.out.println("Please Try Again\n");
					printInfo();
					System.out.print("Enter a Command: ");
				}

			}
		}
	}
}
