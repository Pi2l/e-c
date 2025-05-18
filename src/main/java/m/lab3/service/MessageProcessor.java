package m.lab3.service;

import m.lab3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MessageProcessor {
  private static final String MESSAGE_DELIMITER = "'";

  public Segment processMessage(String message) {
    List<SegmentPart> parts = parseSegment(message);
    MessageType messageType = getMessageType(parts);

    if (messageType == MessageType.ENCRYPTION) {
      return processSecureMessage(parts);
    } else if (messageType == MessageType.DOCUMENT) {
      return processDocumentMessage(parts);
    } else {
      throw new RuntimeException("Unknown message type");
    }
  }

  private DocumentSegment processDocumentMessage(List<SegmentPart> parts) {
    List<SegmentPart> filteredParts = validateSegmentsCount(parts);

    DocumentSegment documentSegment = new DocumentSegment();
    for (SegmentPart part : filteredParts) {
      BiConsumer<DocumentSegment, String> setter = DocumentSegment.SEGMENT_SETTERS.get(part.getKey().trim());
      if (setter != null) {
        setter.accept(documentSegment, part.getValue());
      } else {
        System.out.println("Unknown segment key: " + part.getKey());
      }
    }
    return documentSegment;
  }

  private SecureSegment processSecureMessage(List<SegmentPart> parts) {
    List<SegmentPart> filteredParts = validateSegmentsCount(parts);

    SecureSegment secureSegment = new SecureSegment();
    for (SegmentPart part : filteredParts) {
      BiConsumer<SecureSegment, String> setter = SecureSegment.SEGMENT_SETTERS.get(part.getKey());
      if (setter != null) {
        setter.accept(secureSegment, part.getValue());
      } else {
        System.out.println("Unknown segment key: " + part.getKey());
      }
    }
    return secureSegment;
  }

  private List<SegmentPart> validateSegmentsCount(List<SegmentPart> parts) {
    List<SegmentPart> filteredParts = new ArrayList<>(parts);
    parts.stream().filter(part -> SegmentKey.HEADER.equals(part.getKey()))
            .findFirst()
            .ifPresent(part -> filteredParts.remove(part));
    SegmentPart messageEnd = parts.stream()
            .filter(part -> SegmentKey.MESSAGE_END.equals(part.getKey()))
            .findFirst().orElseThrow(() -> new RuntimeException("No message ending found"));
    if (messageEnd.getValue() == null) {
      throw new RuntimeException("Message ending is null");
    }
    int expectedCount;
    try {
      expectedCount = Integer.parseInt(messageEnd.getValue());
    } catch (NumberFormatException e) {
      throw new RuntimeException("Message ending is not a number");
    }
    if (expectedCount != filteredParts.size()) {
      throw new RuntimeException("Some segments are missing");
    }
    parts.stream().filter(part -> SegmentKey.START.equals(part.getKey()))
            .findFirst().ifPresent(part -> filteredParts.remove(part));
    parts.stream().filter(part -> SegmentKey.END.equals(part.getKey()))
            .findFirst().ifPresent(part -> filteredParts.remove(part));
    filteredParts.remove(messageEnd);
    return filteredParts;
  }

  private List<SegmentPart> parseSegment(String message) {
    List<SegmentPart> parts = new ArrayList<>();
    String[] segments = message.split(MESSAGE_DELIMITER);
    for (String segment : segments) {
      String[] keyValue = segment.split("\\+", 2);
      if (keyValue.length == 2) {
        SegmentPart part = new SegmentPart();
        part.setKey(keyValue[0]);
        part.setValue(keyValue[1]);
        parts.add(part);
      } else if (keyValue.length == 1) {
        SegmentPart part = new SegmentPart();
        part.setKey(keyValue[0]);
        part.setValue("");
        parts.add(part);
      } else {
        System.out.println("Invalid segment: " + segment);
      }
    }
    return parts;
  }

  private MessageType getMessageType(List<SegmentPart> parts) {
    if (parts.stream().anyMatch(segment -> segment.getKey().equals(MessageType.ENCRYPTION.getIdentifier()))){
      return MessageType.ENCRYPTION;
    } else {
      return MessageType.DOCUMENT;
    }
  }

  private List<SegmentPart> getFilteredParts(String message, MessageType type) {
    List<SegmentPart> parts = parseSegment(message);
    MessageType messageType = getMessageType(parts);

    if (messageType != type) {
      System.out.println("Message is not %s message".formatted(type.name()));
      throw new RuntimeException("Invalid %S message".formatted(type.name()));
    }

    return validateSegmentsCount(parts);
  }

}
