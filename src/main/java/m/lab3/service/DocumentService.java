package m.lab3.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import m.lab3.model.DocumentSegment;

public class DocumentService {

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static String getJson(DocumentSegment documentSegment) {
    try {
      return objectMapper.writeValueAsString(documentSegment);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
