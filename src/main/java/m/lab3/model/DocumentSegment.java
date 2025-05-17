package m.lab3.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

//ЗАГ+0002' – заголовок повідомлення та його номер;
//ПОЧ' – початок повідомлення;
//ДЧП+20100910:1048:24' – дата, час та період дії повідомлення: дата –
//рік (2010), місяць (09), день (10): час (10 годин 48 хвилин): період дії
//повідомлення (24 години);
//ДОК+ОПЛ' – документ – платіжне доручення;
//ПЛА+ТОВ «Кооператор»' – назва платника;
//БКВ+Чернівецьке відділення КБ «Приватбанк»' – банк платника;
//МФВ+356032' – МФО банку платника;
//РХВ+2600123456789' – банківський рахунок платника;
//ОТР+ТОВ «Калинівський ринок»' – назва отримувача;
//БКО+ЧФ АКБ «Укрексімбанк»' – банк отримувача;
//МФО+356026' – МФО банку отримувача;
//РХО+2600987654321' – банківський рахунок отримувача;
//ОПП+Оплата за товар по рахунку №23 від 10.09.2010 р.' – призначення
//платежу;
//ВАЛ+грн.' – валюта оплати;
//СУМ+10000' – сума оплати;
//КІП' – Кінець повідомлення;
//КІН+0016' – кінець пакета+загальна кількість сегментів у ньому (для
//перевірки.
@Getter
@Setter
public class DocumentSegment extends Segment {
  public static final Map<String, BiConsumer<DocumentSegment, String>> SEGMENT_SETTERS = new HashMap<>();
  static {
    SEGMENT_SETTERS.put("ЗАГ", DocumentSegment::setHeader);
    SEGMENT_SETTERS.put("ПОЧ", DocumentSegment::setStart);
    SEGMENT_SETTERS.put("ДЧП", DocumentSegment::setStartDateTime);
    SEGMENT_SETTERS.put("ДОК", DocumentSegment::setDocumentType);
    SEGMENT_SETTERS.put("ПЛА", DocumentSegment::setPayerName);
    SEGMENT_SETTERS.put("БКВ", DocumentSegment::setPayerBankName);
    SEGMENT_SETTERS.put("МФВ", DocumentSegment::setPayerBankMFO);
    SEGMENT_SETTERS.put("РХВ", DocumentSegment::setPayerAccount);
    SEGMENT_SETTERS.put("ОТР", DocumentSegment::setRecipientName);
    SEGMENT_SETTERS.put("БКО", DocumentSegment::setRecipientBankName);
    SEGMENT_SETTERS.put("МФО", DocumentSegment::setRecipientBankMFO);
    SEGMENT_SETTERS.put("РХО", DocumentSegment::setRecipientAccount);
    SEGMENT_SETTERS.put("ОПП", DocumentSegment::setPaymentPurpose);
    SEGMENT_SETTERS.put("ВАЛ", DocumentSegment::setCurrency);
    SEGMENT_SETTERS.put("СУМ", DocumentSegment::setAmount);
    SEGMENT_SETTERS.put("КІП", DocumentSegment::setEnd);
    SEGMENT_SETTERS.put("КІН", DocumentSegment::setEndMessage);
  }

  private String startDateTime;
  private DocumentType documentType;

  private String payerName;
  private String payerBankName;
  private String payerBankMFO;
  private String payerAccount;

  private String recipientName;
  private String recipientBankName;
  private String recipientBankMFO;
  private String recipientAccount;

  private String paymentPurpose;
  private String currency;
  private String amount;

  public void setDocumentType(String documentType) {
    this.documentType = DocumentType.valueOf(documentType);
  }
}