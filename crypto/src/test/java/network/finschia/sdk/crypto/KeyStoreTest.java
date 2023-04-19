package network.finschia.sdk.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import com.google.common.io.CharStreams;

import kotlin.text.Charsets;

public class KeyStoreTest {
    private static final String TEST_KEY_STORE_FILE = "keystores/test_keystore.txt";
    private static final String INVALID_KDF_KEYSTORE = "keystores/invalid_kdf_keystore.txt";
    private static final String INVALID_ARMOR_KEYSTORE = "keystores/invalid_armor_keystore.txt";
    private static final String INVALID_SALT_KEYSTORE = "keystores/invalid_salt_keystore.txt";
    private static final String TEST_PRIVATE_KEY = "M5AzgP8Ztnk5jGoNV9gZx7qUknHUV2BOjcoYGjkS7AI=";
    private static final String TEST_PASSWORD = "1234567890";
    private static final String EXPECTED_SALT = "72af54430d345eba1570952c04a561a7";
    private static final String EXPECTED_ARMORED_PRIVATE_KEY =
            "J4jSBlsjNAtJKsME1nFGIqvxID5jAsdPnZ2zWYYuTBpX4P8KGIfTUqGj/6v+hjbFQQ+yOD8dF4OJbWGHp3ncOrgXitsZtQZ2m23CynA=";
    private static Reader keyStoreData, invalidKDFKeyStore, invalidArmorKeystore, invalidSaltKeystore;

    @BeforeEach
    void setup() {
        keyStoreData = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(TEST_KEY_STORE_FILE)));
        invalidKDFKeyStore = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(INVALID_KDF_KEYSTORE)));
        invalidArmorKeystore = new InputStreamReader(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream(INVALID_ARMOR_KEYSTORE)));
        invalidSaltKeystore = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(INVALID_SALT_KEYSTORE)));
    }

    @AfterEach
    void teardown() throws IOException {
        keyStoreData.close();
        invalidKDFKeyStore.close();
        invalidArmorKeystore.close();
        invalidSaltKeystore.close();
    }

    @Test
    public void testLoadFromReader() {
        final KeyStore keyStore = KeyStore.load(keyStoreData);

        assertEquals(KeyStore.KDF_TYPE, keyStore.kdf);
        assertEquals(EXPECTED_SALT, Hex.toHexString(keyStore.salt));
        assertEquals(EXPECTED_ARMORED_PRIVATE_KEY, Base64.toBase64String(keyStore.armoredPrivateKey));
        assertEquals(TEST_PRIVATE_KEY, Base64.toBase64String(keyStore.getPrivateKey(TEST_PASSWORD)));
    }

    @Test
    public void testLoadFromInvalidReader() {
        assertThrows(RuntimeException.class, () -> KeyStore.load(invalidKDFKeyStore));
        assertThrows(RuntimeException.class, () -> KeyStore.load(invalidArmorKeystore));
        assertThrows(RuntimeException.class, () -> KeyStore.load(invalidSaltKeystore));
    }

    @Test
    public void testLoadFromInputStream() throws IOException {
        final KeyStore keyStore;
        try (InputStream in = new ByteArrayInputStream(
                CharStreams.toString(keyStoreData).getBytes(Charsets.US_ASCII))) {
            keyStore = KeyStore.load(in);
        }

        assertEquals(KeyStore.KDF_TYPE, keyStore.kdf);
        assertEquals(EXPECTED_SALT, Hex.toHexString(keyStore.salt));
        assertEquals(EXPECTED_ARMORED_PRIVATE_KEY, Base64.toBase64String(keyStore.armoredPrivateKey));
        assertEquals(TEST_PRIVATE_KEY, Base64.toBase64String(keyStore.getPrivateKey(TEST_PASSWORD)));
    }

    @Test
    public void testLoadFromInvalidInputStream() throws IOException {
        try (InputStream in = new ByteArrayInputStream(
                CharStreams.toString(invalidKDFKeyStore).getBytes(Charsets.UTF_8))) {

            assertThrows(RuntimeException.class, () -> KeyStore.load(in));
        }
    }

    @Test
    public void testLoadFromPrivateKey() {
        final KeyStore keyStore = KeyStore.createFromPrivateKey(Base64.decode(TEST_PRIVATE_KEY), TEST_PASSWORD);

        assertEquals(KeyStore.KDF_TYPE, keyStore.kdf);
        assertEquals(EXPECTED_SALT.length(), Hex.toHexString(keyStore.salt).length());
        assertNotNull(Base64.toBase64String(keyStore.armoredPrivateKey));
        assertEquals(TEST_PRIVATE_KEY, Base64.toBase64String(keyStore.getPrivateKey(TEST_PASSWORD)));
    }

    @Test
    public void testExportAsString() {
        final KeyStore keyStore = KeyStore.load(keyStoreData);

        final KeyStore keyStoreUsingExportedData = KeyStore.load(new StringReader(keyStore.export()));

        assertTrue(new ReflectionEquals(keyStore).matches(keyStoreUsingExportedData));
    }

    @Test
    public void testExportAsWriter() throws IOException {
        final KeyStore keyStore = KeyStore.load(keyStoreData);
        try (StringWriter out = new StringWriter()) {

            keyStore.export(out);
            final KeyStore keyStoreUsingExportedData = KeyStore.load(new StringReader(out.toString()));

            assertTrue(new ReflectionEquals(keyStore).matches(keyStoreUsingExportedData));
        }
    }

    @Test
    public void testExportAsOutputStream() throws IOException {
        final KeyStore keyStore = KeyStore.load(keyStoreData);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            keyStore.export(out);
            final KeyStore keyStoreUsingExportedData = KeyStore.load(
                    new ByteArrayInputStream(out.toByteArray()));

            assertTrue(new ReflectionEquals(keyStore).matches(keyStoreUsingExportedData));
        }
    }

}
