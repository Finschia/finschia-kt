package network.finschia.ln.v2.legacymultisig

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/3348c2854aea73f538454843f2e93167ff15ca85/packages/amino/src/testutils.spec.ts
 */

val test1 = AminoPubKey(
    type = "tendermint/PubKeySecp256k1",
    value = "A4y1mO5UEw00+OCBjneHqgYTmg4tACbK22YrVc8WhZpn",
)

val test2 = AminoPubKey(
    type = "tendermint/PubKeySecp256k1",
    value = "ApBvG9lRbIzTtSY5MiyAG/hyTB+l6HjA4yub1sC7iw9o",
)

val test3 = AminoPubKey(
    type = "tendermint/PubKeySecp256k1",
    value = "A8yTUZ1htobabw6M/5Qx41a0X5EGPtb4H3nd2JiFiADz",
)

// 2/3 multisig
val testgroup1 = AminoPubKey(
    type = "tendermint/PubKeyMultisigThreshold",
    value = MultisigThresholdPubkeyValue(
        threshold = "2",
        pubkeys = listOf(test1, test2, test3),
    )
)
val testgroup1PubkeyBech32 =
"wasmpub1ytql0csgqgfzd666axrjzquvkkvwu4qnp5603cyp3emc02sxzwdqutgqym9dke3t2h83dpv6vufzd666axrjzq5sdudaj5tv3nfm2f3exgkgqxlcwfxplf0g0rqwx2um6mqthzc0dqfzd666axrjzq7vjdge6cdksmdx7r5vl72rrc6kk30ezp376mup77wamzvgtzqq7v7aysdd"

val testgroup2 = AminoPubKey(
    type = "tendermint/PubKeyMultisigThreshold",
    value = MultisigThresholdPubkeyValue(
        threshold = "1",
        pubkeys = listOf(test1, test2, test3),
    )
)
val testgroup2PubkeyBech32 =
"wasmpub1ytql0csgqyfzd666axrjzquvkkvwu4qnp5603cyp3emc02sxzwdqutgqym9dke3t2h83dpv6vufzd666axrjzq5sdudaj5tv3nfm2f3exgkgqxlcwfxplf0g0rqwx2um6mqthzc0dqfzd666axrjzq7vjdge6cdksmdx7r5vl72rrc6kk30ezp376mup77wamzvgtzqq7vc4ejke"

// 2/2 multisig
val testgroup3 = AminoPubKey(
    type = "tendermint/PubKeyMultisigThreshold",
    value = MultisigThresholdPubkeyValue(
        threshold = "2",
        pubkeys = listOf(test1, test3),
    )
)

val testgroup3PubkeyBech32 =
"wasmpub1ytql0csgqgfzd666axrjzquvkkvwu4qnp5603cyp3emc02sxzwdqutgqym9dke3t2h83dpv6vufzd666axrjzq7vjdge6cdksmdx7r5vl72rrc6kk30ezp376mup77wamzvgtzqq7vzjhugu"

// 2/2 multisig with custom sorting
val testgroup4 = AminoPubKey(
    type = "tendermint/PubKeyMultisigThreshold",
    value = MultisigThresholdPubkeyValue(
        threshold = "2",
        pubkeys = listOf(test3, test1),
    )
)

val testgroup4PubkeyBech32 =
"wasmpub1ytql0csgqgfzd666axrjzq7vjdge6cdksmdx7r5vl72rrc6kk30ezp376mup77wamzvgtzqq7vfzd666axrjzquvkkvwu4qnp5603cyp3emc02sxzwdqutgqym9dke3t2h83dpv6vujvg56k"
