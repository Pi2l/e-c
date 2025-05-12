package m.lab3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PersistentClient {
  public static void main(String[] args) {
    String host = "localhost";
    int port = 8080;

    try (Socket socket = new Socket(host, port);
         BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
         BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         PrintWriter serverWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

      System.out.println("Connected to the server. Type your messages below:");

      String userInput;
      while (true) {
        System.out.print("You: ");
        userInput = consoleReader.readLine();

        if ("exit".equalsIgnoreCase(userInput)) {
          System.out.println("Closing connection...");
          break;
        }

        serverWriter.println(userInput); // Send message to the server
        String serverResponse = serverReader.readLine(); // Read response from the server
        System.out.println("Server: " + serverResponse);
      }

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}