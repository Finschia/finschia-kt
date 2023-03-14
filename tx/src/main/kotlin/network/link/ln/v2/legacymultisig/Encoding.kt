package network.link.ln.v2.legacymultisig

import java.util.Base64

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/3348c2854aea73f538454843f2e93167ff15ca85/packages/amino/src/encoding.ts
 */

fun encodeSecp256k1Pubkey(pubkey: ByteArray): AminoPubKey<SinglePubkeyValue> {
    if (pubkey.size != 33 || (pubkey[0] != 0x02.toByte() && pubkey[0] != 0x03.toByte())) {
        error("Public key must be compressed secp256k1, i.e. 33 bytes starting with 0x02 or 0x03")
    }
    return AminoPubKey(
        type = "tendermint/PubKeySecp256k1",
        value = Base64.getEncoder().encodeToString(pubkey)
    )
}

// As discussed in https://github.com/binance-chain/javascript-sdk/issues/163
// Prefixes listed here: https://github.com/tendermint/tendermint/blob/d419fffe18531317c28c29a292ad7d253f6cafdf/docs/spec/blockchain/encoding.md#public-key-cryptography
// Last bytes is varint-encoded length prefix
val pubkeyAminoPrefixSecp256k1 = fromHex("eb5ae987" + "21" /* fixed length */)
val pubkeyAminoPrefixEd25519 = fromHex("1624de64" + "20" /* fixed length */)
// See https://github.com/tendermint/tendermint/commit/38b401657e4ad7a7eeb3c30a3cbf512037df3740diff-c51228ee09e0ee67c26b17e1dc412efbae8d6ecc8acd108c2fe33782240de4ecR56
val pubkeyAminoPrefixMultisigThreshold = fromHex("22c1f7e2" /* variable length not included */)

fun encodeUvarint(value: String): Byte {
    val checked = value.toInt()
    if (checked > 127) {
        error("Encoding numbers > 127 is not supported here. Please tell those lazy finschia-kt maintainers to port the binary.PutUvarint implementation from the Go standard library and write some tests.",)
    }
    return checked.toByte()
}

fun encodeAminoPubkey(pubkey: AminoPubKey<*>): ByteArray {
    if (isMultisigThresholdPubkey(pubkey)) {
        val value = pubkey.value as MultisigThresholdPubkeyValue
        var out = pubkeyAminoPrefixMultisigThreshold
        out += 0x08.toByte()
        out += encodeUvarint(value.threshold)
        value.pubkeys.map { encodeAminoPubkey(it)}.forEach {
            out += 0x12.toByte()
            out += encodeUvarint(it.size.toString())
            out += it
        }
        return out
    } else if (isEd25519Pubkey(pubkey)) {
        val value = pubkey.value as SinglePubkeyValue
        var out = pubkeyAminoPrefixEd25519
        out +=  Base64.getDecoder().decode(value)
        return out
    } else if (isSecp256k1Pubkey(pubkey)) {
        val value = pubkey.value as SinglePubkeyValue
        var out = pubkeyAminoPrefixSecp256k1
        out += Base64.getDecoder().decode(value)
        return out
    } else {
        error("Unsupported pubkey type")
    }
}
