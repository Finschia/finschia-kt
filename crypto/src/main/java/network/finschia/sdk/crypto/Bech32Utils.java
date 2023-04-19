package network.finschia.sdk.crypto;

import java.io.ByteArrayOutputStream;

/**
 * Implementation from
 * https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/SegwitAddress.java
 */
public final class Bech32Utils {
    public static byte[] convertBits(final byte[] in, final int inStart, final int inLen,
                                     final int fromBits, final int toBits, final boolean pad) {
        int acc = 0;
        int bits = 0;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (int i = 0; i < inLen; i++) {
            final int value = in[i + inStart] & 0xff;
            acc = ((acc << fromBits) | value) & max_acc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                out.write((acc >>> bits) & maxv);
            }
        }
        if (pad) {
            if (bits > 0) { out.write((acc << (toBits - bits)) & maxv); }
        } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new IllegalArgumentException("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }

    private Bech32Utils() {}
}
