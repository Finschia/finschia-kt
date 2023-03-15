package network.finschia.ln.v2.account;

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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import com.google.common.io.CharStreams;

import kotlin.text.Charsets;

class KeyWalletTest {

    private static final String PRIVATE_KEY = "M5AzgP8Ztnk5jGoNV9gZx7qUknHUV2BOjcoYGjkS7AI";
    private static final String PUBLIC_KEY =
            "linkpub1addwnpepqfhvrlqk2meckst945el0gsgz9ukmz3fjhv27fa8kf6tj65pf03f7wyzhwk";
    private static final String ADDRESS = "link12mgtlqzq8htt4d6mmly52sm8d230cfwtqf0se6";
    private static final String TEST_KEY_STORE_FILE = "keystores/test_keystore.txt";
    private static final String TEST_KEY_STORE_PASSPHRASE = "1234567890";
    private static Reader keyStoreData;

    @BeforeEach
    public void setup() throws IOException {
        keyStoreData = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(TEST_KEY_STORE_FILE)));
    }

    @AfterEach
    public void teardown() throws IOException {
        keyStoreData.close();
    }

    @Test
    void testLoadFromPrivateKey() {
        final Wallet wallet = KeyWallet.loadFromPrivateKey(Base64.getDecoder().decode(PRIVATE_KEY));
        assertEquals(new PubKey(PUBLIC_KEY), wallet.getPubKey());
        assertEquals(Address.of(ADDRESS), wallet.getAddress());
    }

    @Test
    void testLoadFromReaderKeyStore() {
        final Wallet wallet = KeyWallet.loadFromKeyStore(keyStoreData, TEST_KEY_STORE_PASSPHRASE);
        assertEquals(new PubKey(PUBLIC_KEY), wallet.getPubKey());
        assertEquals(Address.of(ADDRESS), wallet.getAddress());
    }

    @Test
    void testLoadFromInputStreamKeyStore() throws IOException {
        try (InputStream in = new ByteArrayInputStream(
                CharStreams.toString(keyStoreData).getBytes(Charsets.US_ASCII))) {
            final Wallet wallet = KeyWallet.loadFromKeyStore(in, TEST_KEY_STORE_PASSPHRASE);
            assertEquals(new PubKey(PUBLIC_KEY), wallet.getPubKey());
            assertEquals(Address.of(ADDRESS), wallet.getAddress());
        }

    }

    @Test
    public void testExportKeyStoreAsString() {
        final KeyWallet keyWallet = KeyWallet.loadFromKeyStore(keyStoreData, TEST_KEY_STORE_PASSPHRASE);

        final KeyWallet keyWalletUsingExportedData =
                KeyWallet.loadFromKeyStore(
                        new StringReader(keyWallet.exportKeyStore(TEST_KEY_STORE_PASSPHRASE)),
                        TEST_KEY_STORE_PASSPHRASE);

        assertTrue(new ReflectionEquals(keyWallet).matches(keyWalletUsingExportedData));
    }

    @Test
    public void testExportAsWriter() throws IOException {
        final KeyWallet keyWallet = KeyWallet.loadFromKeyStore(keyStoreData, TEST_KEY_STORE_PASSPHRASE);
        try (StringWriter out = new StringWriter()) {

            keyWallet.exportKeyStore(out, TEST_KEY_STORE_PASSPHRASE);
            final KeyWallet keyWalletUsingExportedData =
                    KeyWallet.loadFromKeyStore(
                            new StringReader(out.toString()),
                            TEST_KEY_STORE_PASSPHRASE);

            assertTrue(new ReflectionEquals(keyWallet).matches(keyWalletUsingExportedData));
        }
    }

    @Test
    public void testExportAsOutputStream() throws IOException {
        final KeyWallet keyWallet = KeyWallet.loadFromKeyStore(keyStoreData, TEST_KEY_STORE_PASSPHRASE);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            keyWallet.exportKeyStore(out, TEST_KEY_STORE_PASSPHRASE);
            final KeyWallet keyWalletUsingExportedData = KeyWallet.loadFromKeyStore(
                    new ByteArrayInputStream(out.toByteArray()), TEST_KEY_STORE_PASSPHRASE);

            assertTrue(new ReflectionEquals(keyWallet).matches(keyWalletUsingExportedData));
        }
    }

    @Test
    void testCreate() throws Exception {
        final Wallet wallet = KeyWallet.create();
        assertNotNull(wallet);
    }

    @Test
    void testSignMessage() {
        final String message = "G1eQ4dSk3HGi5Jicd9do/y0EzX+M0zTGSP0ajGe9/9Q=";
        final String expected =
                "CfNbzAFDD6WE0jZG+YOXEpSH6SqptfKL+FVzXffJQw1T7xMYj7A+uCXZDCrJZzbX3pgSQimJyyX5D0wCGf1QJgA=";

        final Wallet wallet = KeyWallet.loadFromPrivateKey(Base64.getDecoder().decode(PRIVATE_KEY));
        final byte[] sign = wallet.sign(Base64.getDecoder().decode(message));
        assertEquals(expected, Base64.getEncoder().encodeToString(sign));
    }

    @Test
    void testSignMessageNullException() throws Exception {
        final Wallet wallet = KeyWallet.create();
        final byte[] message = null;
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> wallet.sign(message));
    }

    @Test
    void testVerifyWallerSignature() throws Exception {
        final String message = "Hello World!";
        final Wallet wallet = KeyWallet.create();

        final byte[] hash = new SHA3.Digest256().digest(message.getBytes(StandardCharsets.UTF_8));
        final byte[] signature = wallet.sign(hash);

        final byte[] pub = wallet.getPubKey().getBody();
        final byte[] sigr = Arrays.copyOfRange(signature, 0, 32);
        final byte[] sigs = Arrays.copyOfRange(signature, 32, 32 * 2);

        final ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        final ECDomainParameters domain = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN());
        final ECPublicKeyParameters publicKeyParams =
                new ECPublicKeyParameters(spec.getCurve().decodePoint(pub), domain);

        final ECDSASigner signer = new ECDSASigner();
        signer.init(false, publicKeyParams);
        final boolean isVerify = signer.verifySignature(hash, new BigInteger(1, sigr),
                                                        new BigInteger(1, sigs));
        assertTrue(isVerify);
    }
}
