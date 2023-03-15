package network.finschia.ln.v2.account;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.SecureRandom;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void testInitiate() {
        final byte[] addressByte = new byte[20];
        final SecureRandom random = new SecureRandom();
        random.nextBytes(addressByte);

        final Address address = Address.of(addressByte);
        assertNotNull(address);
    }

    @Test
    void testInitiateWithInvalidBody() {
        final SecureRandom random = new SecureRandom();

        assertThrows(IllegalArgumentException.class, () -> {
            final byte[] addressByte = new byte[19];
            random.nextBytes(addressByte);
            Address.of(addressByte);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            final byte[] addressByte = new byte[21];
            random.nextBytes(addressByte);
            Address.of(addressByte);
        });
    }

    @Test
    void testInitiateWithBech32() {
        final Address address = Address.of("link18w9fvd5alkegkm9q8dzhym3kmeshugvydpdyc8");
        assertThat(address).isNotNull();
        assertThat(address.getType()).isEqualTo(Type.ACCOUNT);
        assertThat(address.getBody()).hasLength(20);
    }

    @Test
    void testInitiateWithBech32ForValOperAddress() {
        final Address address = Address.of("linkvaloper1qy352euf40x77qfrg4ncn27daufrg4ncz0jqj8");
        assertThat(address).isNotNull();
        assertThat(address.getType()).isEqualTo(Type.VALIDATOR_OPERATOR);
        assertThat(address.getBody()).hasLength(20);
    }

    @Test
    void testInitiateWithBech32ForValConsAddress() {
        final Address address = Address.of("linkvalcons1qy352euf40x77qfrg4ncn27daufrg4nckupu7x");
        assertThat(address).isNotNull();
        assertThat(address.getType()).isEqualTo(Type.VALIDATOR_CONSENSUS);
        assertThat(address.getBody()).hasLength(20);
    }

    @Test
    void testInitiateWithBech32InvalidHrp() {
        assertThrows(IllegalArgumentException.class,
                     () -> Address.of("bc1qqwkdw5f3wnetruanlrdr7fvzyefayrxcuwdvxh3cq8sgvalca9vqtln477"));
    }

    @Test
    void testInitiateWithBech32InvalidBody() {
        assertThrows(IllegalArgumentException.class,
                     () -> Address.of("link1qwkdw5f3wnetruanlrdr7fvzyefayrxcuwdvxh3cq8sgvalca9vpzgcjm6qkw"));
    }

    @Test
    void testEquals() {
        final Address addressByBody = Address.of(Hex.decode("0123456789abcdef0123456789abcdef12345678"));
        final Address addressByBech32 = Address.of("link1qy352euf40x77qfrg4ncn27daufrg4ncsmsau5");
        assertEquals(addressByBody, addressByBech32);
        assertEquals(addressByBody.hashCode(), addressByBech32.hashCode());
    }

    @Test
    void testEqualsDifferentTypeAndSameBody() {
        final Address addressByBody = Address.of(Type.VALIDATOR_CONSENSUS,
                                                 Hex.decode("0123456789abcdef0123456789abcdef12345678"));
        final Address addressByBech32 = Address.of("link1qy352euf40x77qfrg4ncn27daufrg4ncsmsau5");
        assertNotEquals(addressByBody, addressByBech32);
    }

    @Test
    void testVariousHrpBech32() {
        assertDoesNotThrow(() -> Address.of("tlink1qy352euf40x77qfrg4ncn27daufrg4nc5vpd93", "tlink"));
        assertDoesNotThrow(() -> Address.of("testlink1qy352euf40x77qfrg4ncn27daufrg4ncx0tqnz", "testlink"));
        assertDoesNotThrow(() -> Address.of("bc1qy352euf40x77qfrg4ncn27daufrg4ncdk08kx", "bc"));
        assertDoesNotThrow(() -> Address.of("ln1qy352euf40x77qfrg4ncn27daufrg4nc0mtk67", "ln"));
    }

    @Test
    void testInitiateWithCustomHrpBech32() {
        final Address address = Address.of("tlink1qy352euf40x77qfrg4ncn27daufrg4nc5vpd93", "tlink");
        assertThat(address).isNotNull();
        assertThat(address.getType()).isEqualTo(Type.ACCOUNT);
        assertThat(address.getBody()).hasLength(20);
    }

    @Test
    void testInitiateWithCustomHrpBech32ForValOperAddress() {
        final Address address = Address.of("tlinkvaloper1qy352euf40x77qfrg4ncn27daufrg4ncfufyj4", "tlink");
        assertThat(address).isNotNull();
        assertThat(address.getType()).isEqualTo(Type.VALIDATOR_OPERATOR);
        assertThat(address.getBody()).hasLength(20);
    }

    @Test
    void testInitiateWithCustomHrpBech32ForValConsAddress() {
        final Address address = Address.of("tlinkvalcons1qy352euf40x77qfrg4ncn27daufrg4nca06c75", "tlink");
        assertThat(address).isNotNull();
        assertThat(address.getType()).isEqualTo(Type.VALIDATOR_CONSENSUS);
        assertThat(address.getBody()).hasLength(20);
    }

    @Test
    void testToHexString() {
        final String hex = "0123456789abcdef0123456789abcdef12345678";
        final Address addressByBody = Address.of(Hex.decode(hex));
        assertEquals(hex, addressByBody.toHexString());
    }

    @Test
    void testEmptyAddess() {
        final Address emptyAddress = Address.EMPTY_ADDRESS;
        assertEquals("", emptyAddress.toBech32());
        assertEquals("", emptyAddress.toHexString());
    }
}
