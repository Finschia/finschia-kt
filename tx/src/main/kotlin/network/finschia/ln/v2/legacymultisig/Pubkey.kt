package network.finschia.ln.v2.legacymultisig

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/3348c2854aea73f538454843f2e93167ff15ca85/packages/amino/src/pubkeys.ts
 */

data class AminoPubKey<T> internal constructor(val type: String, val value: T)
fun AminoPubKey(type: String, value: SinglePubkeyValue) = AminoPubKey<SinglePubkeyValue>(type, value)
fun AminoPubKey(type: String, value: MultisigThresholdPubkeyValue) = AminoPubKey<MultisigThresholdPubkeyValue>(type, value)
typealias SinglePubkeyValue = String
data class MultisigThresholdPubkeyValue(val threshold: String, val pubkeys: List<AminoPubKey<SinglePubkeyValue>>)

fun isMultisigThresholdPubkey(pubkey: AminoPubKey<*>): Boolean {
    return pubkey.type == "tendermint/PubKeyMultisigThreshold" && pubkey.value is MultisigThresholdPubkeyValue
}
fun isEd25519Pubkey(pubkey: AminoPubKey<*>): Boolean {
    return pubkey.type == "tendermint/PubKeyEd25519" && pubkey.value is SinglePubkeyValue
}
fun isSecp256k1Pubkey(pubkey: AminoPubKey<*>): Boolean {
    return pubkey.type == "tendermint/PubKeySecp256k1" && pubkey.value is SinglePubkeyValue
}
