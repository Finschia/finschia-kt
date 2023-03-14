package network.link.ln.v2.legacymultisig

import network.link.ln.v2.crypto.Bech32Utils
import org.bitcoinj.core.Bech32
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/3348c2854aea73f538454843f2e93167ff15ca85/packages/amino/src/encoding.spec.ts
 */

class EncodingTest {
    @Test
    fun encodeSecp256k1PubkeyForCompressedPubkey() {
        val pubkey = Base64.getDecoder().decode("AtQaCqFnshaZQp6rIkvAPyzThvCvXSDO+9AzbxVErqJP")
        Assertions.assertEquals(
            AminoPubKey(
                type = "tendermint/PubKeySecp256k1",
                value = "AtQaCqFnshaZQp6rIkvAPyzThvCvXSDO+9AzbxVErqJP",
            ),
            encodeSecp256k1Pubkey(pubkey)
        )
    }

    @Test
    fun encodeSecp256k1PubkeyForUncompressedPubkey() {
        val pubkey = Base64.getDecoder().decode("BE8EGB7ro1ORuFhjOnZcSgwYlpe0DSFjVNUIkNNQxwKQE7WHpoHoNswYeoFkuYpYSKK4mzFzMV/dB0DVAy4lnNU=")
        val error = assertThrows<IllegalStateException> { encodeSecp256k1Pubkey(pubkey) }
        Assertions.assertEquals(
            "Public key must be compressed secp256k1, i.e. 33 bytes starting with 0x02 or 0x03", error.message
        )
    }

    @Test
    fun encodeAminoPubkeyForSecp256k1() {
        val pubkey = AminoPubKey(
            type = "tendermint/PubKeySecp256k1",
            value = "A08EGB7ro1ORuFhjOnZcSgwYlpe0DSFjVNUIkNNQxwKQ",
        )
        val expected = Bech32.decode("cosmospub1addwnpepqd8sgxq7aw348ydctp3n5ajufgxp395hksxjzc6565yfp56scupfqhlgyg5").data
        val encodedPubkey = encodeAminoPubkey(pubkey)
        val bech32data = Bech32Utils.convertBits(encodedPubkey, 0, encodedPubkey.size, 8, 5, true)
        Assertions.assertArrayEquals(
            expected, bech32data
        )
    }

    @Test
    fun encodeAminoPubkeyForEd25519() {
        val pubkey = AminoPubKey(
            type = "tendermint/PubKeyEd25519",
            value = "YZHlYxP5R6olj3Tj3f7VgkQE5VaOvv9G0jKATqdQsqI=",
        )
        val expected = Bech32.decode("coralvalconspub1zcjduepqvxg72ccnl9r65fv0wn3amlk4sfzqfe2k36l073kjx2qyaf6sk23qw7j8wq").data
        val encodedPubkey = encodeAminoPubkey(pubkey)
        val bech32data = Bech32Utils.convertBits(encodedPubkey, 0, encodedPubkey.size, 8, 5, true)
        Assertions.assertArrayEquals(
            expected, bech32data
        )
    }

    @Test
    fun encodeAminoPubkeyForMultisig() {
        val encodedPubkey1 = encodeAminoPubkey(testgroup1)
        val bech32data1 = Bech32Utils.convertBits(encodedPubkey1, 0, encodedPubkey1.size, 8, 5, true)
        Assertions.assertEquals(testgroup1PubkeyBech32, Bech32.encode("wasmpub", bech32data1))

        val encodedPubkey2 = encodeAminoPubkey(testgroup2)
        val bech32data2 = Bech32Utils.convertBits(encodedPubkey2, 0, encodedPubkey2.size, 8, 5, true)
        Assertions.assertEquals(testgroup2PubkeyBech32, Bech32.encode("wasmpub", bech32data2))

        val encodedPubkey3 = encodeAminoPubkey(testgroup3)
        val bech32data3 = Bech32Utils.convertBits(encodedPubkey3, 0, encodedPubkey3.size, 8, 5, true)
        Assertions.assertEquals(testgroup3PubkeyBech32, Bech32.encode("wasmpub", bech32data3))

        val encodedPubkey4 = encodeAminoPubkey(testgroup4)
        val bech32data4 = Bech32Utils.convertBits(encodedPubkey4, 0, encodedPubkey4.size, 8, 5, true)
        Assertions.assertEquals(testgroup4PubkeyBech32, Bech32.encode("wasmpub", bech32data4))
    }
}
