package network.link.ln.v2.legacymultisig

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/3348c2854aea73f538454843f2e93167ff15ca85/packages/amino/src/pubkeys.spec.ts
 */


class PubkeyTest {
    val pubkeyEd25519 = AminoPubKey(
        type = "tendermint/PubKeyEd25519",
        value = "YZHlYxP5R6olj3Tj3f7VgkQE5VaOvv9G0jKATqdQsqI=",
    )
    val pubkeySecp256k1 = AminoPubKey(
        type = "tendermint/PubKeySecp256k1",
        value = "AtQaCqFnshaZQp6rIkvAPyzThvCvXSDO+9AzbxVErqJP",
    )
    val pubkeyMultisigThreshold = AminoPubKey(
        type = "tendermint/PubKeyMultisigThreshold",
        value = MultisigThresholdPubkeyValue(
            threshold = "3",
            pubkeys = listOf(
                AminoPubKey(
                    type = "tendermint/PubKeySecp256k1",
                    value = "A4KZH7VSRwW/6RTExROivRYKsQP63LnGcBlXFo+eKGpQ",
                ),
                AminoPubKey(
                    type = "tendermint/PubKeySecp256k1",
                    value = "A8/Cq4VigOnDgl6RSdcx97fjrdCo/qwAX6C34n7ZDZLs",
                ),
                AminoPubKey(
                    type = "tendermint/PubKeySecp256k1",
                    value = "ApKgZuwy03xgdRnXqG6yEHATomsWDOPacy7nbpsuUCSS",
                ),
                AminoPubKey(
                    type = "tendermint/PubKeySecp256k1",
                    value = "Aptm8E3WSSFS0RTAIUW+bLi/slYnTEE+h4qPTG28CHfq",
                ),
            ),
        ),
    )

    @Test
    fun isEd25519PubkeyForAllPubkey() {
        Assertions.assertTrue(isEd25519Pubkey(pubkeyEd25519))
        Assertions.assertFalse(isEd25519Pubkey(pubkeySecp256k1))
        Assertions.assertFalse(isEd25519Pubkey(pubkeyMultisigThreshold))
    }

    @Test
    fun isSecp256k1PubkeyForAllPubkey() {
        Assertions.assertFalse(isSecp256k1Pubkey(pubkeyEd25519))
        Assertions.assertTrue(isSecp256k1Pubkey(pubkeySecp256k1))
        Assertions.assertFalse(isSecp256k1Pubkey(pubkeyMultisigThreshold))
    }

    @Test
    fun isMultisigThresholdPubkeyForAllPubkey() {
        Assertions.assertFalse(isMultisigThresholdPubkey(pubkeyEd25519))
        Assertions.assertFalse(isMultisigThresholdPubkey(pubkeySecp256k1))
        Assertions.assertTrue(isMultisigThresholdPubkey(pubkeyMultisigThreshold))
    }
}
