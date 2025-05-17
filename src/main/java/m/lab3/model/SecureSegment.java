package m.lab3.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.function.BiConsumer;

/*
* Сегменти повідомлення захисту мають приблизно такий вигляд:
  ЗАГ+0001' – заголовок повідомлення та його номер;
  ПОЧ' – початок повідомлення;
  ДЧП+20100910:1030:24' – дата, час та період дії повідомлення: дата –
  рік (2010), місяць (09), день (10): час (10 годин 30 хвилин): період дії
  повідомлення (24 години);
  ОПП+0002' – яке повідомлення зашифровано цим ключем;
  ШИФ+DES' – алгоритм шифрування, який використано для захисту
  повідомлення №0002;
  КЛШ+13FA78BB0FA96C4D' – ключ шифрування;
  ВІН+32AFBC9832F2D5CA2' вектор ініціалізації;
  КІП' – кінець повідомлення;
  КІН+0008' – кінець повідомлення+кількість сегментів у ньому*/
@Getter
@Setter
public class SecureSegment {
  public static final Map<String, BiConsumer<SecureSegment, String>> SEGMENT_SETTERS = Map.of(
          "ЗАГ", SecureSegment::setHeader,
          "ПОЧ", SecureSegment::setStart,
          "ДЧП", SecureSegment::setStartDateTime,
          "ОПП", SecureSegment::setMessageNumber,
          "ШИФ", SecureSegment::setCryptoAlgorithm,
          "КЛШ", SecureSegment::setKey,
          "ВІН", SecureSegment::setIv,
          "КІП", SecureSegment::setEnd,
          "КІН", SecureSegment::setEndMessage
  );
  private String header;
  private String start;
  private String startDateTime;
  private String messageNumber;
  private String cryptoAlgorithm;
  private String key;
  private String iv;
  private String end;
  private String endMessage;

}
