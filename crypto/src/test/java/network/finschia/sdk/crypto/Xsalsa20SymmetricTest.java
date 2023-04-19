package network.finschia.sdk.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class Xsalsa20SymmetricTest {
    static final byte[] TEST_KEY = "0123456789abcdef0123456789abcdef".getBytes();
    static final byte[] INVALID_LENGTH_KEY = "0123456789abcdef0123456789abcdef1".getBytes();
    static final byte[] INVALID_KEY = "0123456789abcdef0123456789abc111".getBytes();
    static final byte[] TEST_MESSAGE = "TEST_MESSAGE".getBytes();

    @Test
    public void testEncryptSymmetric() {
        final byte[] encryptedData = Xsalsa20Symmetric.encryptSymmetric(TEST_MESSAGE, TEST_KEY);
        assertNotNull(encryptedData);
        assertEquals(52, encryptedData.length);
    }

    @Test
    public void testDecryptSymmetric() {
        final byte[] encryptedData = Xsalsa20Symmetric.encryptSymmetric(TEST_MESSAGE, TEST_KEY);
        final byte[] decryptedData = Xsalsa20Symmetric.decryptSymmetric(encryptedData, TEST_KEY);
        assertArrayEquals(TEST_MESSAGE, decryptedData);
    }

    @Test
    public void testDecryptSymmetricByInvalidSecretLength() {
        final byte[] encryptedData = Xsalsa20Symmetric.encryptSymmetric(TEST_MESSAGE, TEST_KEY);
        assertThrows(IllegalArgumentException.class,
                     () -> Xsalsa20Symmetric.decryptSymmetric(encryptedData, INVALID_LENGTH_KEY));
    }

    @Test
    public void testDecryptSymmetricByInvalidSecret() {
        final byte[] encryptedData = Xsalsa20Symmetric.encryptSymmetric(TEST_MESSAGE, TEST_KEY);
        assertThrows(RuntimeException.class,
                     () -> Xsalsa20Symmetric.decryptSymmetric(encryptedData, INVALID_KEY));
    }
}
