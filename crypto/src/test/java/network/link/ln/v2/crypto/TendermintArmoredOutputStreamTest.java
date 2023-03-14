package network.link.ln.v2.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

public class TendermintArmoredOutputStreamTest {
    static final String LINE_SEPARATOR = System.lineSeparator();
    static final String EXPECTED_KEY_STORE = "-----BEGIN TENDERMINT PRIVATE KEY-----" + LINE_SEPARATOR
            + "header1: value1" + LINE_SEPARATOR
            + "header2: value2" + LINE_SEPARATOR + LINE_SEPARATOR
            + "dGVzdENvbnRlbnQ=" + LINE_SEPARATOR
            + "=0Bo8" + LINE_SEPARATOR
            + "-----END TENDERMINT PRIVATE KEY-----" + LINE_SEPARATOR;

    @Test
    public void testWrite() throws IOException {
        final OutputStream output = new ByteArrayOutputStream();
        final TendermintArmoredOutputStream out = new TendermintArmoredOutputStream(output);
        out.setHeader("header1", "value1");
        out.setHeader("header2", "value2");
        out.write("testContent".getBytes());
        out.close();

        assertEquals(EXPECTED_KEY_STORE, output.toString());
    }
}
