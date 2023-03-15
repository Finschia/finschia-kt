package network.finschia.ln.v2.crypto;

import java.util.Arrays;

import org.bitcoinj.core.VarInt;
import org.bouncycastle.jcajce.provider.digest.SHA256;

import com.google.common.primitives.Bytes;

// TODO: Amino encoding is only possible for privateKey and publicKey.
//  We have to implement amino encoder for java.
public final class Amino {
    private static final int DISAMBIGUATION_BYTE_LENGTH = 3;
    private static final int TYPE_PREFIX_LENGTH = 4;
    private static final int SECP256K1_KEY_SIZE_PREFIX_LENGTH = 1;
    private static final int SECP256K1_KEY_AMINO_PREFIX_LEN =
            TYPE_PREFIX_LENGTH + SECP256K1_KEY_SIZE_PREFIX_LENGTH;
    private static final int SECP256K1_KEY_SIZE = 33;

    public static byte[] addAminoPrefix(String name, byte[] body) {
        final byte[] prefix = makeAminoPrefix(name, body);
        return Bytes.concat(prefix, body);
    }

    private Amino() {}

    static byte[] makeAminoPrefix(String name, byte[] body) {
        final byte[] hash = new SHA256.Digest().digest(name.getBytes());
        final int disambiguationByteStart = getNonZeroValueIndex(hash);
        final int typePrefixStart = getNonZeroValueIndex(hash,
                                                         disambiguationByteStart + DISAMBIGUATION_BYTE_LENGTH,
                                                         hash.length);
        if (body.length > SECP256K1_KEY_SIZE) {
            throw new IllegalArgumentException(String.format(
                    "The body size is %d bytes. Currently, amino encoding is only supported for secp256k1 keys.",
                    body.length));
        }
        final byte[] sizePrefix = new VarInt(body.length).encode();

        return Bytes.concat(Arrays.copyOfRange(hash, typePrefixStart, typePrefixStart + TYPE_PREFIX_LENGTH),
                            sizePrefix);
    }

    public static byte[] removeAminoPrefix(byte[] bytes) {
        return Arrays.copyOfRange(bytes, SECP256K1_KEY_AMINO_PREFIX_LEN, bytes.length);
    }

    public static byte[] removeAminoPrefix(byte[] bytes, int bytesLen) {
        return Arrays.copyOfRange(bytes, SECP256K1_KEY_AMINO_PREFIX_LEN,
                                  SECP256K1_KEY_AMINO_PREFIX_LEN + bytesLen);
    }

    static int getNonZeroValueIndex(byte[] hash) {
        return getNonZeroValueIndex(hash, 0, hash.length);
    }

    static int getNonZeroValueIndex(byte[] hash, int start, int end) {
        if (start < 0 || end > hash.length) {
            throw new IllegalArgumentException(String.format("Invalid index (start: %d, end: %d, length: %d)"
                    , start, end, hash.length));
        }

        for (int i = start; i < end; i++) {
            if (hash[i] != 0x00) {
                return i;
            }
        }
        throw new IllegalArgumentException("input hash is empty");
    }
}
