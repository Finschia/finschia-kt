package network.link.ln.v2.account;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HDWalletTest {
    private static final String MNEMONIC =
            "draft hope about adapt later town private cruise taxi cable canal cliff chapter base cousin depend begin differ discover foster hood pretty music liberty";
    private static final String PRIVATE_KEY = "M5AzgP8Ztnk5jGoNV9gZx7qUknHUV2BOjcoYGjkS7AI=";
    private static final String PUBLIC_KEY =
            "linkpub1addwnpepqfhvrlqk2meckst945el0gsgz9ukmz3fjhv27fa8kf6tj65pf03f7wyzhwk";
    private static final String ADDRESS = "link12mgtlqzq8htt4d6mmly52sm8d230cfwtqf0se6";

    @Test
    void testLoadFromMnemonic() {
        final HDWallet hdWallet = HDWallet.loadFromMnemonic(MNEMONIC);
        final KeyWallet wallet = hdWallet.getKeyWallet();

        assertEquals(PRIVATE_KEY, Base64.getEncoder().encodeToString(wallet.getPrivateKey()));
        assertEquals(new PubKey(PUBLIC_KEY), wallet.getPubKey());
        assertEquals(Address.of(ADDRESS), wallet.getAddress());
    }

    @Test
    void testLoadFromMnemonicWithIndex() {
        final HDWallet hdWallet = HDWallet.loadFromMnemonic(MNEMONIC);
        final KeyWallet wallet1 = hdWallet.getKeyWallet(0, 0);
        final KeyWallet wallet2 = hdWallet.getKeyWallet(0, 1);
        final KeyWallet wallet3 = hdWallet.getKeyWallet(1, 0);

        assertFalse(Arrays.equals(wallet1.getPrivateKey(), wallet2.getPrivateKey()));
        assertNotEquals(wallet1.getPubKey(), wallet2.getPubKey());
        assertNotEquals(wallet1.getAddress(), wallet2.getAddress());

        assertFalse(Arrays.equals(wallet1.getPrivateKey(), wallet3.getPrivateKey()));
        assertNotEquals(wallet1.getPubKey(), wallet3.getPubKey());
        assertNotEquals(wallet1.getAddress(), wallet3.getAddress());
    }

    @Test
    void testCreate() throws Exception {
        final HDWallet hdWallet1 = HDWallet.create();
        final HDWallet hdWallet2 = HDWallet.loadFromMnemonic(hdWallet1.getMnemonic());
        final KeyWallet wallet1 = hdWallet1.getKeyWallet();
        final KeyWallet wallet2 = hdWallet2.getKeyWallet();

        assertEquals(hdWallet1.getMnemonic(), hdWallet2.getMnemonic());
        assertArrayEquals(wallet1.getPrivateKey(), wallet2.getPrivateKey());
        assertEquals(wallet1.getPubKey(), wallet2.getPubKey());
        assertEquals(wallet1.getAddress(), wallet2.getAddress());
    }

    @Test
    void testSignMessage() {
        final String message = "G1eQ4dSk3HGi5Jicd9do/y0EzX+M0zTGSP0ajGe9/9Q=";
        final String expected =
                "CfNbzAFDD6WE0jZG+YOXEpSH6SqptfKL+FVzXffJQw1T7xMYj7A+uCXZDCrJZzbX3pgSQimJyyX5D0wCGf1QJgA=";

        final HDWallet hdWallet = HDWallet.loadFromMnemonic(MNEMONIC);
        final KeyWallet wallet = hdWallet.getKeyWallet();

        final byte[] sign = wallet.sign(Base64.getDecoder().decode(message));

        assertArrayEquals(Base64.getDecoder().decode(expected), sign);
    }

    @Test
    void testSignMessageNullException() throws Exception {
        final HDWallet hdWallet = HDWallet.loadFromMnemonic(MNEMONIC);
        final KeyWallet wallet = hdWallet.getKeyWallet();

        final byte[] message = null;
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> wallet.sign(message));
    }

    @Test
    void testVerifyWallerSignature() throws Exception {
        final String message = "Hello World!";
        final HDWallet hdWallet = HDWallet.loadFromMnemonic(MNEMONIC);
        final KeyWallet wallet = hdWallet.getKeyWallet();

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
        Assertions.assertTrue(isVerify);
    }
}
