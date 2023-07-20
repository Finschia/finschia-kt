package network.finschia.sdk.legacymultisig

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/79396bfaa49831127ccbbbfdbb1185df14230c63/packages/amino/src/signdoc.ts
 */

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

fun AminoMsgCreateValidator.toAminoMsgValue(): AminoMsgValue {
    return Json.encodeToJsonElement(this)
}

@Serializable
data class Description(
    @SerialName("moniker") val moniker: String?,
    @SerialName("identity") val identity: String?,
    @SerialName("website") val website: String?,
    @SerialName("security_contact") val securityContact: String?,
    @SerialName("details") val details: String?
)

@Serializable
data class CommissionRates(
    @SerialName("rate") val rate: String,
    @SerialName("max_rate") val maxRate: String,
    @SerialName("max_change_rate") val maxChangeRate: String
)

@Serializable
data class AminoSinglePubKey(
    @SerialName("type") val type: String,
    @SerialName("value") val value: String,
)
