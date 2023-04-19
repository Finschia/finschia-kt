package network.finschia.sdk.crypto;

import java.io.IOException;
import java.io.OutputStream;
import java.util.TreeMap;

import org.bouncycastle.bcpg.CRC24;
import org.bouncycastle.util.Strings;

/**
 * Implementation from org.bouncycastle.bcpg.ArmoredOutputStream
 * Output stream that writes data in ASCII Armored format.
 * <p>
 * Note 1: close() needs to be called on an ArmoredOutputStream to write the final checksum.
 * flush() will not do this as other classes assume it is always fine to call flush()
 * - it is not though if the checksum gets output.
 * Note 2: as multiple PGP blobs are often written to the same stream,
 * close() does not close the underlying stream.
 * </p>
 */
public class TendermintArmoredOutputStream extends OutputStream {
    private static final byte[] encodingTable =
            {
                    (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
                    (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
                    (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
                    (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
                    (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
                    (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
                    (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
                    (byte) 'v',
                    (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
                    (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
                    (byte) '7', (byte) '8', (byte) '9',
                    (byte) '+', (byte) '/'
            };

    /**
     * encode the input data producing a base 64 encoded byte array.
     */
    private static void encode(
            OutputStream out,
            int[] data,
            int len)
            throws IOException {
        final int d1;
        final int d2;
        final int d3;

        switch (len) {
            case 0:        /* nothing left to do */
                break;
            case 1:
                d1 = data[0];

                out.write(encodingTable[(d1 >>> 2) & 0x3f]);
                out.write(encodingTable[(d1 << 4) & 0x3f]);
                out.write('=');
                out.write('=');
                break;
            case 2:
                d1 = data[0];
                d2 = data[1];

                out.write(encodingTable[(d1 >>> 2) & 0x3f]);
                out.write(encodingTable[((d1 << 4) | (d2 >>> 4)) & 0x3f]);
                out.write(encodingTable[(d2 << 2) & 0x3f]);
                out.write('=');
                break;
            case 3:
                d1 = data[0];
                d2 = data[1];
                d3 = data[2];

                out.write(encodingTable[(d1 >>> 2) & 0x3f]);
                out.write(encodingTable[((d1 << 4) | (d2 >>> 4)) & 0x3f]);
                out.write(encodingTable[((d2 << 2) | (d3 >>> 6)) & 0x3f]);
                out.write(encodingTable[d3 & 0x3f]);
                break;
            default:
                throw new IOException("unknown length in encode");
        }
    }

    final OutputStream out;
    final int[] buf = new int[3];
    int bufPtr;
    final CRC24 crc = new CRC24();
    int chunkCount;

    boolean start = true;

    String nl = Strings.lineSeparator();

    static final String headerStart = "-----BEGIN TENDERMINT PRIVATE KEY";
    static final String headerTail = "-----";
    static final String footerStart = "-----END TENDERMINT PRIVATE KEY";
    static final String footerTail = "-----";

    final TreeMap<String, String> headers = new TreeMap<>();

    /**
     * Constructs an armored output stream
     *
     * @param out the OutputStream to wrap.
     */
    public TendermintArmoredOutputStream(
            OutputStream out) {
        this.out = out;

        if (nl == null) {
            nl = "\r\n";
        }

    }

    /**
     * Set an additional header entry. A null value will clear the entry for name.
     *
     * @param name the name of the header entry.
     * @param value the value of the header entry.
     */
    public void setHeader(
            String name,
            String value) {
        if (value == null) {
            headers.remove(name);
        } else {
            headers.put(name, value);
        }
    }

    private void writeHeaderEntry(
            String name,
            String value)
            throws IOException {
        for (int i = 0; i != name.length(); i++) {
            out.write(name.charAt(i));
        }

        out.write(':');
        out.write(' ');

        for (int i = 0; i != value.length(); i++) {
            out.write(value.charAt(i));
        }

        for (int i = 0; i != nl.length(); i++) {
            out.write(nl.charAt(i));
        }
    }

    @Override
    public void write(
            int b)
            throws IOException {
        if (start) {
            writeString(headerStart);
            writeString(headerTail);
            writeString(nl);

            final Object[] hashkeys = headers.keySet().toArray();
            for (Object o : hashkeys) {
                final String key = (String) o;
                writeHeaderEntry(key, headers.get(key));
            }

            writeString(nl);
            start = false;
        }

        if (bufPtr == 3) {
            encode(out, buf, bufPtr);
            bufPtr = 0;
            if ((++chunkCount & 0xf) == 0) {
                for (int i = 0; i != nl.length(); i++) {
                    out.write(nl.charAt(i));
                }
            }
        }

        crc.update(b);
        buf[bufPtr++] = b & 0xff;
    }

    @Override
    public void flush()
            throws IOException {
    }

    /**
     * <b>Note</b>: close() does not close the underlying stream. So it is possible to write
     * multiple objects using armoring to a single stream.
     */
    @Override
    public void close()
            throws IOException {
        encode(out, buf, bufPtr);

        writeString(nl);
        out.write('=');

        final int crcV = crc.getValue();

        buf[0] = (crcV >> 16) & 0xff;
        buf[1] = (crcV >> 8) & 0xff;
        buf[2] = crcV & 0xff;

        encode(out, buf, 3);

        writeString(nl);
        writeString(footerStart);
        writeString(footerTail);
        writeString(nl);

        out.flush();

        start = true;
    }

    private void writeString(String content) throws IOException {
        for (int i = 0; i != content.length(); i++) {
            out.write(content.charAt(i));
        }
    }
}
