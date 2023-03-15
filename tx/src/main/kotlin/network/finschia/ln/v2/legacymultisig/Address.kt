package network.finschia.ln.v2.legacymultisig

import network.finschia.ln.v2.crypto.Bech32Utils
import org.bitcoinj.core.Bech32
import org.bouncycastle.jcajce.provider.digest.RIPEMD160
import org.bouncycastle.jcajce.provider.digest.SHA256
import java.util.Base64

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/41884540d2927538fb45adcb36badd79bdfd3e55/packages/amino/src/addresses.ts
 */

fun rawEd25519PubkeyToRawAddress(pubkeyData: ByteArray): ByteArray {
    if (pubkeyData.size != 32) {
        error("Invalid Ed25519 pubkey length: ${pubkeyData.size}")
    }
    return SHA256.Digest().digest(pubkeyData).copyOfRange(0, 20)
}

fun rawSecp256k1PubkeyToRawAddress(pubkeyData: ByteArray): ByteArray {
    if (pubkeyData.size != 33) {
        error("Invalid Secp256k1 pubkey length (compressed): ${pubkeyData.size}")
    }
    return RIPEMD160.Digest().digest(SHA256.Digest().digest(pubkeyData))
}

fun pubkeyToRawAddress(pubkey: AminoPubKey<*>): ByteArray {
    if (isSecp256k1Pubkey(pubkey)) {
        val value = pubkey.value as SinglePubkeyValue
        val pubkeyData = Base64.getDecoder().decode(value)
        return rawSecp256k1PubkeyToRawAddress(pubkeyData)
    } else if (isEd25519Pubkey(pubkey)) {
        val value = pubkey.value as SinglePubkeyValue
        val pubkeyData = Base64.getDecoder().decode(value)
        return rawEd25519PubkeyToRawAddress(pubkeyData)
    } else if (isMultisigThresholdPubkey(pubkey)) {
        // https://github.com/tendermint/tendermint/blob/38b401657e4ad7a7eeb3c30a3cbf512037df3740/crypto/multisig/threshold_pubkey.go#L71-L74
        val pubkeyData = encodeAminoPubkey(pubkey)
        return SHA256.Digest().digest(pubkeyData).copyOfRange(0, 20)
    } else {
        error("Unsupported public key type")
    }
}

fun pubkeyToAddress(pubkey: AminoPubKey<*>, prefix: String): String {
    val rawAddress = pubkeyToRawAddress(pubkey)
    val bech32data = Bech32Utils.convertBits(rawAddress, 0, rawAddress.size, 8, 5, true)
    return Bech32.encode(prefix, bech32data)
}
