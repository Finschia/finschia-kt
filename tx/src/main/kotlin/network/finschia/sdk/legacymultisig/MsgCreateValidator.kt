package network.finschia.sdk.legacymultisig

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/79396bfaa49831127ccbbbfdbb1185df14230c63/packages/amino/src/signdoc.ts
 */

@Serializable
data class Description(
    @SerialName("moniker") val moniker: String,
    @SerialName("identity") val identity: String,
    @SerialName("website") val website: String,
    @SerialName("security_contact") val securityContact: String,
    @SerialName("details") val details: String
)

@Serializable
data class CommissionRates(
    @SerialName("rate") val rate: String,
    @SerialName("max_rate") val maxRate: String,
    @SerialName("max_change_rate") val maxChangeRate: String
)

@Serializable
data class PubKey(
    @SerialName("type_url") val typeUrl: String,
    @SerialName("value") val value: ByteArray,
)

@Serializable
data class AminoSinglePubKey(
    @SerialName("type") val type: String,
    @SerialName("value") val value: String,
)

@Serializable
data class AminoMsgCreateValidator(
    @SerialName("description") val description: Description,
    @SerialName("commission") val commission: CommissionRates,
    @SerialName("min_self_delegation") val minSelfDelegation: String,
    @SerialName("delegator_address") val delegatorAddress: String,
    @SerialName("validator_address") val validatorAddress: String,
    @SerialName("pubkey") val pubkey: AminoSinglePubKey,
    @SerialName("value") val value: Coin,
)

@Serializable
data class AminoMsg(
    @SerialName("type") val type: String,
    @SerialName("value") val value: AminoMsgCreateValidator,    // todo: Can't use Any type?
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