package m.lab3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Segment {
  @com.fasterxml.jackson.annotation.JsonIgnore
  protected String header;

  @com.fasterxml.jackson.annotation.JsonIgnore
  protected String start;

  @com.fasterxml.jackson.annotation.JsonIgnore
  protected String end;

  @com.fasterxml.jackson.annotation.JsonIgnore
  protected String endMessage;

  @com.fasterxml.jackson.annotation.JsonIgnore
  protected DocumentType type;
}
