package network.link.ln.v2.legacymultisig

import network.link.ln.v2.crypto.Bech32Utils
import org.bitcoinj.core.Bech32
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/41884540d2927538fb45adcb36badd79bdfd3e55/packages/amino/src/addresses.spec.ts
 */

class AddressTest {
    @Test
    fun pubkeyToRawAddressForSecp256k1() {
        val pubkey = AminoPubKey<Any>(
            type = "tendermint/PubKeySecp256k1",
            value = "AtQaCqFnshaZQp6rIkvAPyzThvCvXSDO+9AzbxVErqJP",
        )
        val rawAddress = pubkeyToRawAddress(pubkey)
        val bech32data = Bech32Utils.convertBits(rawAddress, 0, rawAddress.size, 8, 5, true)
        Assertions.assertArrayEquals(
            Bech32.decode("cosmos1h806c7khnvmjlywdrkdgk2vrayy2mmvf9rxk2r").data, bech32data
        )
    }

    @Test
    fun pubkeyToRawAddressForEd25519() {
        val pubkey = AminoPubKey<Any>(
            type = "tendermint/PubKeyEd25519",
            value = Base64.getEncoder().encodeToString(fromHex("12ee6f581fe55673a1e9e1382a0829e32075a0aa4763c968bc526e1852e78c95")),
        )
        val rawAddress = pubkeyToRawAddress(pubkey)
        val bech32data = Bech32Utils.convertBits(rawAddress, 0, rawAddress.size, 8, 5, true)
        Assertions.assertArrayEquals(
            Bech32.decode("cosmos1pfq05em6sfkls66ut4m2257p7qwlk448h8mysz").data, bech32data
        )
    }

    @Test
    fun pubkeyToRawAddressForMultisig() {
        val pubkey = AminoPubKey<Any>(
            type = "tendermint/PubKeyMultisigThreshold",
            value = MultisigThresholdPubkeyValue(
                threshold = "2",
                pubkeys = listOf(
                    AminoPubKey(
                        type = "tendermint/PubKeySecp256k1",
                        value = "A4y1mO5UEw00+OCBjneHqgYTmg4tACbK22YrVc8WhZpn",
                    ),
                    AminoPubKey(
                        type = "tendermint/PubKeySecp256k1",
                        value = "ApBvG9lRbIzTtSY5MiyAG/hyTB+l6HjA4yub1sC7iw9o",
                    ),
                    AminoPubKey(
                        type = "tendermint/PubKeySecp256k1",
                        value = "A8yTUZ1htobabw6M/5Qx41a0X5EGPtb4H3nd2JiFiADz",
                    )
                )
            )
        )
        val rawAddress = pubkeyToRawAddress(pubkey)
        Assertions.assertArrayEquals(
            fromHex("0892a77fab2fa7e192c3b7b2741e6682f3abb72f"), rawAddress
        )
    }

    @Test
    fun pubkeyToAddressForSecp256k1() {
        val pubkey = AminoPubKey<Any>(
            type = "tendermint/PubKeySecp256k1",
            value = "AtQaCqFnshaZQp6rIkvAPyzThvCvXSDO+9AzbxVErqJP",
        )
        Assertions.assertEquals(
            "cosmos1h806c7khnvmjlywdrkdgk2vrayy2mmvf9rxk2r", pubkeyToAddress(pubkey, "cosmos")
        )
    }

    @Test
    fun pubkeyToAddressForEd25519() {
        val pubkey = AminoPubKey<Any>(
            type = "tendermint/PubKeyEd25519",
            value = Base64.getEncoder().encodeToString(fromHex("12ee6f581fe55673a1e9e1382a0829e32075a0aa4763c968bc526e1852e78c95")),
        )
        Assertions.assertEquals(
            "cosmos1pfq05em6sfkls66ut4m2257p7qwlk448h8mysz", pubkeyToAddress(pubkey, "cosmos")
        )
    }

    @Test
    fun pubkeyToAddressForMultisig() {
        val pubkey = AminoPubKey<Any>(
            type = "tendermint/PubKeyMultisigThreshold",
            value = MultisigThresholdPubkeyValue(
                threshold = "2",
                pubkeys = listOf(
                    AminoPubKey(
                        type = "tendermint/PubKeySecp256k1",
                        value = "A4y1mO5UEw00+OCBjneHqgYTmg4tACbK22YrVc8WhZpn",
                    ),
                    AminoPubKey(
                        type = "tendermint/PubKeySecp256k1",
                        value = "ApBvG9lRbIzTtSY5MiyAG/hyTB+l6HjA4yub1sC7iw9o",
                    ),
                    AminoPubKey(
                        type = "tendermint/PubKeySecp256k1",
                        value = "A8yTUZ1htobabw6M/5Qx41a0X5EGPtb4H3nd2JiFiADz",
                    )
                )
            )
        )
        Assertions.assertEquals(
            "wasm1pzf2wlat97n7rykrk7e8g8nxste6hde0r8jqsy", pubkeyToAddress(pubkey, "wasm")
        )
    }
}
