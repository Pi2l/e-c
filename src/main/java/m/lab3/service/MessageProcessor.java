package m.lab3.service;

import m.lab3.model.MessageType;
import m.lab3.model.SecureSegment;
import m.lab3.model.SegmentKey;
import m.lab3.model.SegmentPart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MessageProcessor {
  private static final String MESSAGE_DELIMITER = "'";

  public SecureSegment processSecureMessage(String message) {
    List<SegmentPart> parts = parseSegment(message);
    MessageType messageType = getMessageType(parts);

    if (messageType != MessageType.ENCRYPTION) {
      System.out.println("Message is not encryption");
      return null;
    }

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

  // приклад повідомлення: ЗАГ+0002'ПОЧ'ДЧП+20100910:1048:24'СУМ+10000'КІП'КІН+0016
  private List<SegmentPart> parseSegment(String message) {
    List<SegmentPart> parts = new ArrayList<>();
    String[] segments = message.split(MESSAGE_DELIMITER);
    for (String segment : segments) {
      String[] keyValue = segment.split("\\+");
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
}
