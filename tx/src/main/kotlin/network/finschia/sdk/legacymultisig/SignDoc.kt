package network.finschia.sdk.legacymultisig

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/79396bfaa49831127ccbbbfdbb1185df14230c63/packages/amino/src/signdoc.ts
 */

@Serializable
data class AminoMsg(
    @SerialName("type") val type: String,
    @SerialName("value") val value: AminoMsgValue,
)

@Serializable
data class AminoMsgValue(
    @SerialName("amount") val amount: List<Coin>,
    @SerialName("from_address") val fromAddress: String,
    @SerialName("to_address") val toAddress: String,
)

@Serializable
data class StdFee(
    @SerialName("amount") val amount: List<Coin>,
    @SerialName("gas") val gas: String,
)

@Serializable
data class StdSignDoc(
    @SerialName("account_number") val accountNumber: String,
    @SerialName("sequence") val sequence: String,
    @SerialName("timeout_height") val timeoutHeight: String?,
    @SerialName("chain_id") val chainId: String,
    @SerialName("memo") val memo: String,
    @SerialName("fee") val fee: StdFee,
    @SerialName("msgs") val msgs: List<AminoMsg>,
)

fun JsonElement.sort(): JsonElement {
    return when (this) {
        is JsonObject -> JsonObject(this.jsonObject.toMap().map { it.key to it.value.sort() }.sortedBy { it.first }.toMap())
        is JsonArray -> JsonArray(this.jsonArray.map { it.sort() })
        else -> this
    }
}

fun JsonElement.removeNull(): JsonElement {
    return when (this) {
        is JsonObject -> JsonObject(this.jsonObject.toMap().filterValues { it != JsonNull }.map { it.key to it.value.removeNull() }.toMap())
        is JsonArray -> JsonArray(this.jsonArray.filter { it != JsonNull }.map { it.removeNull() })
        is JsonNull -> error("unexpected token")
        else -> this
    }
}
