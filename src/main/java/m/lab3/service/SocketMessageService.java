package m.lab3.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketMessageService implements AutoCloseable {
  private final BufferedReader reader;
  private final PrintWriter writer;
  private final Socket socket;

  public SocketMessageService(Socket socket) throws IOException {
    this.socket = socket;
    this.reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
    this.writer = new PrintWriter(socket.getOutputStream(), true);
  }

  public String receiveMessage() throws IOException {
    return reader.readLine();
  }

  public void sendMessage(String message) {
    writer.println(message);
  }

  @Override
  public void close() throws IOException {
    socket.close();
  }

}
