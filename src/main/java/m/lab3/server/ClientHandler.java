package m.lab3.server;

import lombok.RequiredArgsConstructor;
import m.lab3.service.SocketMessageService;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ClientHandler {

  private static final Set<SessionInfo> SESSION_INFOS = ConcurrentHashMap.newKeySet();

  private final Socket clientSocket;

  public void run() {
    try ( SocketMessageService messageService = new SocketMessageService(clientSocket) ) {
      SocketEndpoint endpoint = new SocketEndpoint( messageService );

      var clientId = new SessionInfo(getClientId());
      System.out.println("ClientId: " + clientId);

      String msg;
      while ((msg = messageService.receiveMessage()) != null) {
        if (msg.isEmpty()) {
          break;
        }

        if (!SESSION_INFOS.contains(clientId)) {
          endpoint.onInit(msg, clientId);
          SESSION_INFOS.add(clientId);
          System.out.println("Client connected: " + clientId);
        } else if (!clientSocket.isClosed()) {
          endpoint.processMessage(msg, clientId);
        } else {
          endpoint.onClose(clientId);
          SESSION_INFOS.remove(clientId);
          System.out.println("Client disconnected: " + clientId);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("Error closing client socket: " + e.getMessage());
      }
    }
  }

  private String getClientId() {
    return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
  }
}
