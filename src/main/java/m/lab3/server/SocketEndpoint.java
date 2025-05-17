package m.lab3.server;

import lombok.RequiredArgsConstructor;
import m.lab3.model.SecureSegment;
import m.lab3.model.ClientInfo;
import m.lab3.service.CryptoUtil;
import m.lab3.service.MessageProcessor;
import m.lab3.service.SocketMessageService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class SocketEndpoint {

  private static final Map<String, ClientInfo> SESSION_INFO = new ConcurrentHashMap<>();
  private final SocketMessageService messageService;
  private final MessageProcessor messageProcessor;

  public void onInit(String message, Session session) {
    System.out.printf("Initializing[%s]: %s%n", session.getClientId(), message);
    messageService.sendMessage( CryptoUtil.getPublicKeyBase64() );
  }

  public void processMessage(String message, Session session) {
    System.out.printf("Processing[%s]: %s", session.getClientId(), message);

    if (!SESSION_INFO.containsKey(session.getClientId())) {
      String decryptedMessage = CryptoUtil.decryptRsa(message);
      SecureSegment segment;
      try {
        segment = messageProcessor.processSecureMessage(decryptedMessage);
      } catch (RuntimeException e) {
        messageService.sendMessage(e.getMessage());
        return;
      }

      String clientId = session.getClientId();
      SESSION_INFO.put(clientId, ClientInfo.builder().clientId(clientId)
              .sessionKey(segment.getKey()).keyIV(segment.getIv()).build());
    } else {
      // decrypt message
      // check message type
      // process message
      // send response
    }
  }

  public void onClose(Session sessionInfo) {
    System.out.printf("Closing[%s]", sessionInfo.getClientId());
    messageService.sendMessage("Closing: ");
  }
}
