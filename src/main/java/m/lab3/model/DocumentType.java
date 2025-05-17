package m.lab3.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocumentType {
  PAYMENT_ORDER("ОПЛ"),
  INVOICE("НАК");
  private final String code;
}
