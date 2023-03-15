package network.finschia.ln.v2.legacymultisig

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/79396bfaa49831127ccbbbfdbb1185df14230c63/packages/amino/src/coins.ts
 */

@Serializable
data class Coin(
    @SerialName("denom") val denom: String,
    @SerialName("amount") val amount: String,
)
