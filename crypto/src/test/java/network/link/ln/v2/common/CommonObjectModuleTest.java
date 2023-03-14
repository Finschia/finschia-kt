package network.link.ln.v2.common;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import network.link.ln.v2.account.Address;
import network.link.ln.v2.account.Coin;
import network.link.ln.v2.account.PubKey;

@SuppressWarnings("unused")
class CommonObjectModuleTest {

    private ObjectMapper mapper;

    @BeforeEach
    @SuppressWarnings("KotlinInternalInJava")
    void setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new CommonMapperModule());
    }

    @Test
    void testLongOrBiggerInteger() throws IOException {

        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, 1L);
        assertEquals("\"1\"", writer.toString());

        writer = new StringWriter();
        mapper.writeValue(writer, Long.valueOf(1));
        assertEquals("\"1\"", writer.toString());

        writer = new StringWriter();
        mapper.writeValue(writer, BigInteger.ONE);
        assertEquals("\"1\"", writer.toString());

    }

    @Test
    void testAddress() throws IOException {
        final SecureRandom random = new SecureRandom();
        final byte[] addressByte = new byte[20];
        random.nextBytes(addressByte);

        final Address address = Address.of(addressByte);

        final StringWriter writer = new StringWriter();
        mapper.writeValue(writer, address);
        assertEquals('"' + address.toString() + '"', writer.toString());
    }

    enum ToStringEnum {
        A,
        B,
        C;

        @Override
        public String toString() {
            return name().toLowerCase() + "-string";
        }
    }

    @Test
    void testEnum() throws IOException {

        final StringWriter writer = new StringWriter();
        mapper.writeValue(writer, ToStringEnum.A);
        assertEquals('"' + ToStringEnum.A.toString() + '"', writer.toString());
    }

    public static class Sample {
        public String keyIsCamelCase = "value";
    }

    @Test
    void testSnakeCase() throws IOException {
        final Sample sample = new Sample();

        final StringWriter writer = new StringWriter();
        mapper.writeValue(writer, sample);
        assertEquals("{\"key_is_camel_case\":\"value\"}", writer.toString());
    }

    @Test
    void testKotlinDataClass() throws IOException {
        final Coin coin = new Coin("link", BigInteger.ONE);

        final StringWriter writer = new StringWriter();
        mapper.writeValue(writer, coin);
        assertEquals("{\"denom\":\"link\",\"amount\":\"1\"}", writer.toString());
    }

    @Test
    @SuppressWarnings("KotlinInternalInJava")
    void testCustomizedAddressHrp() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CommonMapperModule("tlink"));

        final byte[] raw = new byte[20];
        final SecureRandom random = new SecureRandom();
        random.nextBytes(raw);
        final Address testAddress = Address.of(raw);

        final StringWriter writer = new StringWriter();
        mapper.writeValue(writer, testAddress);

        assertThat(writer.toString()).startsWith("\"tlink");
    }

    @Test
    @SuppressWarnings("KotlinInternalInJava")
    void testCustomizedPubKeyHrp() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CommonMapperModule("tlink"));

        final byte[] raw = new byte[33];
        final SecureRandom random = new SecureRandom();
        random.nextBytes(raw);
        final PubKey testPubKey = new PubKey(raw);

        final StringWriter writer = new StringWriter();
        mapper.writeValue(writer, testPubKey);

        assertThat(writer.toString()).startsWith("\"tlink");
    }

}
