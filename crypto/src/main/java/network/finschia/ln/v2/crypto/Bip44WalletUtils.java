package network.finschia.ln.v2.crypto;

import static network.finschia.ln.v2.crypto.LinkKeys.PRIVATE_KEY_SIZE;
import static network.finschia.ln.v2.crypto.SecureRandomUtils.secureRandom;
import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;

import org.bouncycastle.util.BigIntegers;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.MnemonicUtils;

/**
 * Implementation from
 * https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/crypto/Bip44WalletUtils.java
 * Generates a BIP-44 compatible wallet on top of BIP-39 generated seed.
 */
public final class Bip44WalletUtils {

    private Bip44WalletUtils() {}

    public static String generateMnemonic() {
        final byte[] initialEntropy = new byte[16];
        secureRandom().nextBytes(initialEntropy);
        return MnemonicUtils.generateMnemonic(initialEntropy);
    }

    public static byte[] generatePrivateKey(String mnemonic, int accountNumber, int index) {
        final byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
        final Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        final Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, accountNumber, index);
        return BigIntegers.asUnsignedByteArray(PRIVATE_KEY_SIZE, bip44Keypair.getPrivateKey());
    }

    static Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master, int accountNumber, int index) {
        final int LINK_COIN_TYPE = 438;

        // m / Purpose' / coin_type' / Account' / Change / address_index
        // m / 44'/438'/0'/0/0
        final int[] path = {
                44 | HARDENED_BIT, LINK_COIN_TYPE | HARDENED_BIT, accountNumber | HARDENED_BIT,
                0, index
        };
        return Bip32ECKeyPair.deriveKeyPair(master, path);
    }
}
