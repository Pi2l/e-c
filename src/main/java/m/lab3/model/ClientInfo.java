package m.lab3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientInfo {
  private String clientId;
  private String sessionKey;
  private String keyIV;
  private String messageNumber;
}
