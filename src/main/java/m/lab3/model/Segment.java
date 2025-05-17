package m.lab3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Segment {
  protected String header;
  protected String start;
  protected String end;
  protected String endMessage;

  protected DocumentType type;
}
