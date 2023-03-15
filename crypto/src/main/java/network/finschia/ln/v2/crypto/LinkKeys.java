package network.finschia.ln.v2.crypto;

import static network.finschia.ln.v2.crypto.SecureRandomUtils.secureRandom;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

/**
 * Implementation from
 * https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/Keys.java
 *
 *
 * Crypto key utilities.
 */
public final class LinkKeys {

    public static final int PRIVATE_KEY_SIZE = 32;
    public static final double MIN_BOUNCY_CASTLE_VERSION = 1.46;

    static {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        final Provider newProvider = new BouncyCastleProvider();

        if (newProvider.getVersion() < MIN_BOUNCY_CASTLE_VERSION) {
            final String message = String.format(
                    "The version of BouncyCastle should be %f or newer", MIN_BOUNCY_CASTLE_VERSION);
            throw new RuntimeCryptoException(message);
        }

        if (provider != null) {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        }

        Security.addProvider(newProvider);
    }

    private LinkKeys() {
    }

    public static byte[] createPrivateKey()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        final ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecGenParameterSpec, secureRandom());
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();
        final BigInteger d = ((ECPrivateKey) keyPair.getPrivate()).getD();
        return BigIntegers.asUnsignedByteArray(PRIVATE_KEY_SIZE, d);
    }

    public static byte[] getPublicKey(byte[] privateKey, boolean compressed) {
        final ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        final ECPoint pointQ = spec.getG().multiply(new BigInteger(1, privateKey));
        return pointQ.getEncoded(compressed);
    }

}
