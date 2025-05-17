package m.lab3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
  DOCUMENT("ДОК"),
  ENCRYPTION("ШИФ");

  private String identifier;
}
