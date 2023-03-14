package network.link.ln.v2.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import com.google.common.primitives.Bytes;

public class AminoTest {
    static final String TEST_BODY_TYPE = "tendermint/PubKeySecp256k1";
    static final byte[] TEST_BODY = Hex.decode(
            "026ec1fc1656f38b4165ad33f7a20811796d8a2995d8af27a7b274b96a814be29f");
    static final byte[] TEST_AMINO_PREFIX = Hex.decode("eb5ae98721");
    static final byte[] TEST_ENCODED_BODY = Bytes.concat(TEST_AMINO_PREFIX, TEST_BODY);
    static final byte[] TEST_BYTE_ARRAY = { 0, 0, 0, 1, 2, 3 };

    @Test
    public void testMakeAminoPrefix() {
        final byte[] actualPrefix = Amino.makeAminoPrefix(TEST_BODY_TYPE, TEST_BODY);

        assertArrayEquals(TEST_AMINO_PREFIX, actualPrefix);
    }

    @Test
    public void testMakeAminoPrefixOfEmptyArray() {
        final byte[] emptyBytes = "".getBytes();
        final byte[] emptyBytesTestPrefix = Hex.decode("eb5ae98700");

        final byte[] actualPrefix = Amino.makeAminoPrefix(TEST_BODY_TYPE, emptyBytes);

        assertArrayEquals(emptyBytesTestPrefix, actualPrefix);
    }

    // TODO: Amino encoding is only possible for privateKey and publicKey.
    //  We have to implement amino encoder for java.
    @Test
    public void testMakeAminoPrefixWithNotSecp256k1Key() {
        final byte[] longerBytes = Bytes.concat(TEST_BODY, new byte[] { 1 });

        assertThrows(IllegalArgumentException.class, () -> Amino.makeAminoPrefix(TEST_BODY_TYPE, longerBytes));
    }

    @Test
    public void testAddAminoPrefix() {
        final byte[] encodedBody = Amino.addAminoPrefix(TEST_BODY_TYPE, TEST_BODY);
        assertArrayEquals(TEST_ENCODED_BODY, encodedBody);
    }

    @Test
    public void testRemoveAminoPrefix() {
        byte[] body = Amino.removeAminoPrefix(TEST_ENCODED_BODY);
        assertArrayEquals(TEST_BODY, body);

        final int substringLength = 10;
        body = Amino.removeAminoPrefix(TEST_ENCODED_BODY, substringLength);
        assertArrayEquals(Arrays.copyOfRange(TEST_BODY, 0, substringLength), body);
    }

    @Test
    public void testGetNonZeroValueIndex() {
        assertEquals(3, Amino.getNonZeroValueIndex(TEST_BYTE_ARRAY));
    }

    @Test
    public void testGetNonZeroValueIndexOfEmpty() {
        assertThrows(IllegalArgumentException.class,
                     () -> Amino.getNonZeroValueIndex("".getBytes()));
    }

    @Test
    public void testInvalidRemoveZeroValue() {
        assertThrows(IllegalArgumentException.class,
                     () -> Amino.getNonZeroValueIndex(TEST_BYTE_ARRAY, -1, TEST_BYTE_ARRAY.length));

        assertThrows(IllegalArgumentException.class,
                     () -> Amino.getNonZeroValueIndex(TEST_BYTE_ARRAY, 0, TEST_BYTE_ARRAY.length + 1));
    }
}
