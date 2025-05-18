package m.lab3;

import m.lab3.client.SimpleClient;

public class Client {
  public static void main(String[] args) {
    String host = "localhost";
    int port = 8080;

    SimpleClient client = new SimpleClient();
    client.start(host, port);
  }
}