package m.lab3.server;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class SimpleServer {
  private static final int THREAD_POOL_SIZE = 10;
  private final int port;

  public void start() throws IOException {
    try (ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
         ServerSocket serverSocket = new ServerSocket(port)) {

      while (true) {
        Socket clientSocket = serverSocket.accept();
        threadPool.execute(() -> new ClientHandler(clientSocket).run());
      }
    }
  }
}
