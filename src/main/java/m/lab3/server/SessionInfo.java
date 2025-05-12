package m.lab3.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class SessionInfo {

  private String clientId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SessionInfo that)) return false;

    return Objects.equals(clientId, that.clientId);
  }

  @Override
  public String toString() {
    return clientId;
  }
}
