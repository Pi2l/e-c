package m.lab3.service;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaKeyService {
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

}
