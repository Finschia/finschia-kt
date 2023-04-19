package network.finschia.sdk.crypto;

import java.util.Arrays;
import java.util.Optional;

import com.codahale.xsalsa20poly1305.SecretBox;

public final class Xsalsa20Symmetric {
    private static final int NONCE_LEN = 24;
    private static final int SECRET_KEY_LEN = 32;

    private Xsalsa20Symmetric() {}

    static byte[] encryptSymmetric(byte[] plaintext, byte[] secretKey) {
        checkSecretKeyLength(secretKey);

        final byte[] nonce = new byte[NONCE_LEN];
        SecureRandomUtils.secureRandom().nextBytes(nonce);

        final SecretBox box = new SecretBox(secretKey);
        final byte[] res = box.seal(nonce, plaintext);

        final byte[] out = new byte[nonce.length + res.length];
        System.arraycopy(nonce, 0, out, 0, nonce.length);
        System.arraycopy(res, 0, out, nonce.length, res.length);
        return out;
    }

    static byte[] decryptSymmetric(byte[] cipherText, byte[] secretKey) {
        checkSecretKeyLength(secretKey);

        final byte[] nonce = Arrays.copyOfRange(cipherText, 0, NONCE_LEN);
        final byte[] cipher = Arrays.copyOfRange(cipherText, NONCE_LEN, cipherText.length);

        final SecretBox box = new SecretBox(secretKey);

        final Optional<byte[]> plaintext = box.open(nonce, cipher);
        if (plaintext.isPresent()) {
            return plaintext.get();
        } else {
            throw new RuntimeException("Ciphertext decryption failed");
        }
    }

    private static void checkSecretKeyLength(byte[] secretKey) {
        if (secretKey.length != SECRET_KEY_LEN) {
            throw new IllegalArgumentException(String.format("Secret must be 32 bytes long, got len %d",
                                                             secretKey.length));
        }
    }
}
