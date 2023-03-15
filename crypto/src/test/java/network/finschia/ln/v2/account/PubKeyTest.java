package network.finschia.ln.v2.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.SecureRandom;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

class PubKeyTest {

    @Test
    void testInitiate() {
        final byte[] pubKeyByte = new byte[33];
        final SecureRandom random = new SecureRandom();
        random.nextBytes(pubKeyByte);

        final PubKey pubKey = new PubKey(pubKeyByte);
        assertNotNull(pubKey);
    }

    @Test
    void testInitiateWithInvalidBody() {
        final SecureRandom random = new SecureRandom();

        assertThrows(IllegalArgumentException.class, () -> {
            final byte[] pubKeyByte = new byte[34];
            random.nextBytes(pubKeyByte);
            new PubKey(pubKeyByte);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            final byte[] pubKeyByte = new byte[32];
            random.nextBytes(pubKeyByte);
            new PubKey(pubKeyByte);
        });
    }

    @Test
    void testInitiateWithBech32() {
        final String bech32PubKey =
                "linkpub1addwnpepqfhvrlqk2meckst945el0gsgz9ukmz3fjhv27fa8kf6tj65pf03f7wyzhwk";
        final String hexPubKey = "026ec1fc1656f38b4165ad33f7a20811796d8a2995d8af27a7b274b96a814be29f";

        final PubKey pubKey = new PubKey(bech32PubKey);

        assertEquals(bech32PubKey, pubKey.toBech32());
        assertEquals(bech32PubKey, pubKey.toString());
        assertEquals(hexPubKey, pubKey.toHexString());
    }

    @Test
    void testInitiateWithBech32ForValOperPubKey() {
        final String bech32PubKey =
                "linkvaloperpub1addwnpepqwzylaqhr7lxknsjrqx8vtqxrk23zzanq67r98qy6tqmh7dh26xp6yul4z6";
        final String hexPubKey = "03844ff4171fbe6b4e12180c762c061d95110bb306bc329c04d2c1bbf9b7568c1d";

        final PubKey pubKey = new PubKey(bech32PubKey);

        assertEquals(bech32PubKey, pubKey.toBech32());
        assertEquals(bech32PubKey, pubKey.toString());
        assertEquals(hexPubKey, pubKey.toHexString());
    }

    @Test
    void testInitiateWithBech32ForValConsPubKey() {
        final String bech32PubKey =
                "linkvalconspub1addwnpepqwzylaqhr7lxknsjrqx8vtqxrk23zzanq67r98qy6tqmh7dh26xp6znt9x0";
        final String hexPubKey = "03844ff4171fbe6b4e12180c762c061d95110bb306bc329c04d2c1bbf9b7568c1d";

        final PubKey pubKey = new PubKey(bech32PubKey);

        assertEquals(bech32PubKey, pubKey.toBech32());
        assertEquals(bech32PubKey, pubKey.toString());
        assertEquals(hexPubKey, pubKey.toHexString());
    }

    @Test
    void testInitiateWithBech32InvalidHrp() {
        assertThrows(IllegalArgumentException.class,
                     () -> new PubKey("bc1qqwkdw5f3wnetruanlrdr7fvzyefayrxcuwdvxh3cq8sgvalca9vqtln477"));
    }

    @Test
    void testInitiateWithBech32InvalidBody() {
        assertThrows(IllegalArgumentException.class,
                     () -> new PubKey("linkpub1qwkdw5f3wnetruanlrdr7fvzyefayrxcuwdvxh3cq8sgvalca9vpzgcjm6qkw"));
    }

    @Test
    void testEquals() {
        final PubKey pubKeyByBody = new PubKey(Type.ACCOUNT,
                                               Hex.decode(
                                                       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef12"));

        final PubKey pubKeyByBech32 = new PubKey(
                "linkpub1addwnpepqy352euf40x77qfrg4ncn27dauqjx3t83x4ummcpydzk0zdtehh3yymd46v");
        assertEquals(pubKeyByBech32, pubKeyByBody);
        assertEquals(pubKeyByBody.hashCode(), pubKeyByBech32.hashCode());
    }
}
