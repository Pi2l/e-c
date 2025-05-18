package m.lab3.server;

import lombok.RequiredArgsConstructor;
import m.lab3.model.DocumentSegment;
import m.lab3.model.SecureSegment;
import m.lab3.model.ClientInfo;
import m.lab3.model.Segment;
import m.lab3.service.CryptoUtil;
import m.lab3.service.DocumentService;
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
    System.out.printf("Processing[%s]: %s%n", session.getClientId(), message);
    ClientInfo clientInfo = SESSION_INFO.get(session.getClientId());

    String decryptedMessage;
    if (clientInfo == null) {
      decryptedMessage = CryptoUtil.decryptRsa(message);
    } else {
      decryptedMessage = CryptoUtil.decryptTripleDes(message, clientInfo.getSessionKey(), clientInfo.getKeyIV());
    }

    System.out.printf("Decrypted[%s]: %s%n", session.getClientId(), decryptedMessage);

    Segment segment;
    try {
      segment = messageProcessor.processMessage(decryptedMessage);
    } catch (RuntimeException e) {
      sendMessage(e.getMessage(), clientInfo);
      return;
    }

    if (segment instanceof SecureSegment secureSegment) {
      String clientId = session.getClientId();
      SESSION_INFO.put(clientId, ClientInfo.builder().clientId(clientId)
              .sessionKey(secureSegment.getKey()).keyIV(secureSegment.getIv()).build());
    } else if (segment instanceof DocumentSegment documentSegment) {
      String json = DocumentService.getJson(documentSegment);
      sendMessage(json, clientInfo);
    }

  }

  public void onClose(Session session) {
    System.out.printf("Closing[%s]", session.getClientId());
    sendMessage("Closing: ", SESSION_INFO.get(session.getClientId()));
  }

  private void sendMessage(String message, ClientInfo clientInfo) {
    if (clientInfo != null) {
      messageService.sendMessage(CryptoUtil.encryptTripleDes(message, clientInfo.getSessionKey(), clientInfo.getKeyIV()));
      return;
    }
    messageService.sendMessage(message);
  }
}
