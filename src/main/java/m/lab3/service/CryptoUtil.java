package m.lab3.service;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtil {
  private static final String PRIVATE_KEY_PATH = "src/main/resources/keys/private_key.pem";
  private static final String PUBLIC_KEY_PATH = "src/main/resources/keys/public_key.pem";

  private static final PrivateKey privateKey;
  private static final PublicKey publicKey;

  static {
    privateKey = loadPrivateKey();
    publicKey = loadPublicKey();
  }

  @SneakyThrows
  private static PrivateKey loadPrivateKey() {
    String key = Files.readString(Paths.get(PRIVATE_KEY_PATH))
            .replaceAll("-----BEGIN PRIVATE KEY-----", "")
            .replaceAll("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
    byte[] decoded = Base64.getDecoder().decode(key);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    return KeyFactory.getInstance("RSA").generatePrivate(spec);
  }

  @SneakyThrows
  private static PublicKey loadPublicKey() {
    String key = Files.readString(Paths.get(PUBLIC_KEY_PATH))
            .replaceAll("-----BEGIN PUBLIC KEY-----", "")
            .replaceAll("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
    byte[] decoded = Base64.getDecoder().decode(key);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    return KeyFactory.getInstance("RSA").generatePublic(spec);
  }

  public static String getPublicKeyBase64() {
    return Base64.getEncoder().encodeToString(publicKey.getEncoded());
  }

  public static String decryptRsa(String encryptedMessage) {
    try {
      byte[] decoded = Base64.getDecoder().decode(encryptedMessage);
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] decrypted = cipher.doFinal(decoded);
      return new String(decrypted);
    } catch (Exception e) {
      throw new RuntimeException("Failed to decrypt message", e);
    }
  }

  public static String encryptRsa(String message) {
    try {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] encrypted = cipher.doFinal(message.getBytes());
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      throw new RuntimeException("Failed to encrypt message", e);
    }
  }

  // 3DES (2 ключі)
  public static String decryptTripleDes(String encryptedMessage, String sessionKey, String iV) {
    try {
      byte[] decoded = Base64.getDecoder().decode(encryptedMessage);
      Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, CryptoUtil.getKey(sessionKey), CryptoUtil.getIV(iV));
      byte[] decrypted = cipher.doFinal(decoded);
      return new String(decrypted);
    } catch (Exception e) {
      throw new RuntimeException("Failed to decrypt message", e);
    }
  }

  public static String encryptTripleDes(String message, String sessionKey, String iV) {
    try {
      Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, CryptoUtil.getKey(sessionKey), CryptoUtil.getIV(iV));
      byte[] encrypted = cipher.doFinal(message.getBytes());
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      throw new RuntimeException("Failed to encrypt message", e);
    }
  }

  private static Key getKey(String sessionKey) {
    byte[] keyBytes = Base64.getDecoder().decode(sessionKey);

    // 2-key 3DES: з 16 байтів треба зробити 24 байти, дублюючи перші 8 байтів
    byte[] fullKey = new byte[24];
    System.arraycopy(keyBytes, 0, fullKey, 0, 16);
    System.arraycopy(keyBytes, 0, fullKey, 16, 8); // дублювання першого ключа K1
    return new SecretKeySpec(fullKey, "DESede");
  }

  private static IvParameterSpec getIV(String iV) {
    byte[] ivBytes = Base64.getDecoder().decode(iV);
    return new IvParameterSpec(ivBytes);
  }
}
