package m.lab3.server;

import lombok.RequiredArgsConstructor;
import m.lab3.model.DocumentSegment;
import m.lab3.model.SecureSegment;
import m.lab3.model.ClientInfo;
import m.lab3.model.Segment;
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
    ClientInfo clientInfo = SESSION_INFO.get(session.getClientId());

    String decryptedMessage;
    if (clientInfo == null) {
      decryptedMessage = CryptoUtil.decryptRsa(message);
    } else {
      decryptedMessage = CryptoUtil.decryptTripleDes(message, clientInfo.getSessionKey(), clientInfo.getKeyIV());
    }

    Segment segment;
    try {
      segment = messageProcessor.processMessage(decryptedMessage);
    } catch (RuntimeException e) {
      sendError(e.getMessage(), clientInfo);
      return;
    }

    if (segment instanceof SecureSegment secureSegment) {
      String clientId = session.getClientId();
      SESSION_INFO.put(clientId, ClientInfo.builder().clientId(clientId)
              .sessionKey(secureSegment.getKey()).keyIV(secureSegment.getIv()).build());
    } else if (segment instanceof DocumentSegment documentSegment) {
      documentSegment = null;// TODO: form json from documentSegment
    }

  }

  public void onClose(Session sessionInfo) {
    System.out.printf("Closing[%s]", sessionInfo.getClientId());
    messageService.sendMessage("Closing: ");
  }

  private void sendError(String message, ClientInfo clientInfo) {
    if (clientInfo != null) {
      messageService.sendMessage(CryptoUtil.encryptTripleDes(message, clientInfo.getSessionKey(), clientInfo.getKeyIV()));
      return;
    }
    messageService.sendMessage(CryptoUtil.encryptRsa(message));
  }
}
