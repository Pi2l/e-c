package m.lab3.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Base64;

import m.lab3.service.CryptoUtil;
import m.lab3.service.SocketMessageService;

public class SimpleClient {

    private static final SecureRandom secureRandom = new SecureRandom();
    
    public void start(String host, int port) {
        try (Socket socket = new Socket(host, port);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            SocketMessageService messageService = new SocketMessageService(socket)) {

        messageService.sendMessage("getPublicKey");
        String publicKey = messageService.receiveMessage();
        System.out.println("Public Key: " + publicKey);

        String key = getKey();
        String iv = getIv();
        
        String messageWithKey = "ЗАГ+0001'ПОЧ'ДЧП+20100910:1030:24'ОПП+0002'ШИФ+3DES2Key'КЛШ+%s'ВІН+%s'КІП'КІН+0008'"
                                    .formatted(key, iv);
        messageService.sendMessage(encryptRsa(messageWithKey, publicKey));
        

        String userInput;
        while (true) {
            System.out.print("You: ");
            userInput = consoleReader.readLine();

            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("Closing connection...");
                break;
            }

            messageService.sendMessage(CryptoUtil.encryptTripleDes(userInput, key, iv));
            String serverResponse = messageService.receiveMessage();
            System.out.println("Server: " + CryptoUtil.decryptTripleDes(serverResponse, key, iv));
        }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private String encryptRsa(String message, String publicKeyStr) {
      try {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(publicKeyStr);
        java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(keyBytes);
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
        java.security.PublicKey publicKey = keyFactory.generatePublic(spec);

        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        return java.util.Base64.getEncoder().encodeToString(encryptedBytes);
      } catch (Exception e) {
        throw new RuntimeException("RSA encryption failed", e);
      }
    }

    private String getKey() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String getIv() {
        byte[] bytes = new byte[8];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
}
