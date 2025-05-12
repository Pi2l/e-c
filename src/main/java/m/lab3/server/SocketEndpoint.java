package m.lab3.server;

import lombok.RequiredArgsConstructor;
import m.lab3.service.RsaKeyService;
import m.lab3.service.SocketMessageService;

@RequiredArgsConstructor
public class SocketEndpoint {

  private final SocketMessageService messageService;

  public void onInit(String message, SessionInfo sessionInfo) {
    System.out.printf("Initializing[%s]: %s%n", sessionInfo.getClientId(), message);
    messageService.sendMessage( RsaKeyService.getPublicKeyBase64() );
  }

  public void processMessage(String message, SessionInfo sessionInfo) {
    System.out.printf("Processing[%s]: %s", sessionInfo.getClientId(), message);
    messageService.sendMessage("Processing: " + message);
  }

  public void onClose(SessionInfo sessionInfo) {
    System.out.printf("Closing[%s]", sessionInfo.getClientId());
    messageService.sendMessage("Closing: ");
  }
}
