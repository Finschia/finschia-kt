/**
 * Copyright 2023 Finschia Foundation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package network.finschia.sdk.example

import network.finschia.sdk.legacymultisig.*
import com.google.protobuf.ByteString
import com.google.protobuf.kotlin.toByteString
import cosmos.base.v1beta1.CoinOuterClass
import cosmos.staking.v1beta1.Tx
import io.grpc.ManagedChannelBuilder
import kotlinx.serialization.json.*
import network.finschia.sdk.account.HDWallet
import network.finschia.sdk.account.KeyWallet
import network.finschia.sdk.base.protoDecimalToJson
import org.bouncycastle.jcajce.provider.digest.SHA256
import java.util.*

class MultiSigMsgCreateValidator {
    companion object {
        fun createMsgCreateValidator(
            deliAddr: String,
            opAddr: String,
            amount: CoinOuterClass.Coin,
            pubKey: String,
        ): Tx.MsgCreateValidator {
            val pubKeyBytes = Base64.getDecoder().decode(pubKey).toByteString()
            val pk = com.google.protobuf.any {
                typeUrl = "/cosmos.crypto.ed25519.PubKey"
                value = pubKeyBytes.toPubKeyProtoValue()
            }

            val msgCreateValidator = cosmos.staking.v1beta1.msgCreateValidator {
                description = cosmos.staking.v1beta1.description {
                    moniker = "sim"
                }
                commission = cosmos.staking.v1beta1.commissionRates {
                    rate = "100000000000000000"             // 0.1 * 10^18
                    maxRate = "200000000000000000"          // 0.2 * 10^18
                    maxChangeRate = "1"                     // 0.000000000000000001 * 10^18
                }
                minSelfDelegation = "1"
                delegatorAddress = deliAddr
                validatorAddress = opAddr
                pubkey = pk
                value = amount
            }
            return msgCreateValidator
        }

        private fun ByteString.toPubKeyProtoValue(): ByteString {
            val header = ByteArray(2)
            header[0] = 0x0a
            header[1] = this.size().toByte()
            val v = header + this.toByteArray()
            return v.toByteString()
        }

        fun generateTxBody(
            createValidator: Tx.MsgCreateValidator,
            timeoutHeight: Int
        ): cosmos.tx.v1beta1.TxOuterClass.TxBody {
            return cosmos.tx.v1beta1.txBody {
                this.messages += com.google.protobuf.any {
                    this.typeUrl = "/cosmos.staking.v1beta1.MsgCreateValidator"
                    this.value = createValidator.toByteString()
                }
                this.timeoutHeight = timeoutHeight.toLong()
            }
        }

        fun generateSignDoc(
            sendMsgs: List<Tx.MsgCreateValidator>,
            accNum: Int,
            accSeq: Int,
            timeoutHeight: Int = 0,
            gasLimit: Int,
            chainId: String
        ): StdSignDoc {
            return StdSignDoc(
                accountNumber = accNum.toString(),
                sequence = accSeq.toString(),
                timeoutHeight = if (timeoutHeight <= 0) null else timeoutHeight.toString(),
                chainId = chainId,
                memo = "",
                fee = StdFee(
                    amount = listOf(
                        Coin(
                            denom = "cony",
                            amount = "100"
                        )
                    ),
                    gas = gasLimit.toString(),
                ),
                msgs = sendMsgs.map { toAminoMsg(it) },
            )
        }

        fun toAminoMsg(msg: Tx.MsgCreateValidator): AminoMsg {
            val desc = Description(
                moniker = msg.description.moniker?.takeIf { it.isNotEmpty() },
                identity = msg.description.identity?.takeIf { it.isNotEmpty() },
                website = msg.description.website?.takeIf { it.isNotEmpty() },
                securityContact = msg.description.securityContact?.takeIf { it.isNotEmpty() },
                details = msg.description.details?.takeIf { it.isNotEmpty() },
            )

            val commissionRate = CommissionRates(
                rate = protoDecimalToJson(msg.commission.rate),
                maxRate = protoDecimalToJson(msg.commission.maxRate),
                maxChangeRate = protoDecimalToJson(msg.commission.maxChangeRate)
            )

            val pkByte = msg.pubkey.value.toByteArray()
            val pkBody = pkByte.copyOfRange(2, pkByte.size)

            val pubk = AminoSinglePubKey(
                type = "tendermint/PubKeyEd25519",
                value = Base64.getEncoder().encodeToString(pkBody)
            )

            val coin = Coin(
                denom = msg.value.denom,
                amount = msg.value.amount,
            )

            return AminoMsg(
                type = "cosmos-sdk/MsgCreateValidator",
                value = AminoMsgCreateValidator(
                    description = desc,
                    commission = commissionRate,
                    minSelfDelegation = msg.minSelfDelegation,
                    delegatorAddress = msg.delegatorAddress,
                    validatorAddress = msg.validatorAddress,
                    pubkey = pubk,
                    value = coin,
                ).toAminoMsgValue()
            )
        }
    }
}

fun getSignDigest(signDoc: StdSignDoc): ByteArray {
    return SHA256.Digest()
        .digest(Json.encodeToJsonElement(signDoc).removeNull().sort().toString().toByteArray())
}

suspend fun main() {
    //-----------------------------------------
    // Step 1: setup GRPC and initialize scenario data
    //-----------------------------------------
    val port = 9090
    val host = "localhost"
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
    val chainId = "sim"
    val gasLimit = 200000
    val accountPrefix = "link"
    val operPrefix = "linkvaloper"

    // set up HD wallet
    val mnemonic =
        "mind flame tobacco sense move hammer drift crime ring globe art gaze cinnamon helmet cruise special produce notable negative wait path scrap recall have"
    val hdWallet = HDWallet.loadFromMnemonic(mnemonic)

    // multi-sig threshold
    val threshold = 2
    // number of multi-sig public keys
    val pubKeyNum = 5
    // wallet list with pubKeyNum wallets
    val wallets: List<KeyWallet> = List(pubKeyNum) { 0 }.mapIndexed() { index, _ ->
        hdWallet.getKeyWallet(index = index)
    }
    // public key list with pubKeyNum public keys
    val pubKeyList = wallets.map {
        encodeSecp256k1Pubkey(it.pubKey.body)
    }
    // signer wallet list with threshold wallets
    val signers = wallets.filterIndexed { index, _ -> index < threshold }

    // multi-sig public key
    val multiSigPubKey = createMultisigThresholdPubkey(pubKeyList, threshold, txSigLimit = 7)
    // multi-sig address
    val multiSigAddress = pubkeyToAddress(multiSigPubKey, accountPrefix)
    val operatorAddress = pubkeyToAddress(multiSigPubKey, operPrefix)

    val multiSigAccNum = 9
    val multiSigAccSeq = 0
    val timeoutHeight = 0

    // scenario description
    println(
        "scenario: " +
                "$multiSigAddress ($threshold of $pubKeyNum multi-sig address, acc num: $multiSigAccNum, acc seq: $multiSigAccSeq) sends createValidator tx to $chainId chain. " +
                "Gas limit is set to $gasLimit and timeout height is ${if (timeoutHeight <= 0) "not set" else "set to $timeoutHeight"}."
    )

    //-----------------------------------------
    // step 2: generate `MsgCreateValidator` unsigned tx
    //-----------------------------------------
    // generate MsgCreateValidator
    val pubKey = "bA6ozZ+fYrhU0KrRoJjFIKBWnOzd86G5dGJSCvlyuhA="
    val amount = cosmos.base.v1beta1.coin {
        amount = "10000"
        denom = "stake"
    }
    val msgCreVal =
        MultiSigMsgCreateValidator.createMsgCreateValidator(multiSigAddress, operatorAddress, amount, pubKey)

    // generate unsigned tx body(amino type)
    val txBody = MultiSigMsgCreateValidator.generateTxBody(msgCreVal, timeoutHeight)

    // generate amino signDoc
    val unsignedSignDoc = MultiSigMsgCreateValidator.generateSignDoc(
        listOf(msgCreVal),
        multiSigAccNum,
        multiSigAccSeq,
        timeoutHeight,
        gasLimit,
        chainId
    )
    println("UnsignedSignDoc: ${Json.encodeToJsonElement(unsignedSignDoc).removeNull().sort().toString()}")

    //-----------------------------------------
    // step 3: generate signature digest
    //-----------------------------------------
    // generate sign digest
    val signDigest = getSignDigest(unsignedSignDoc)
    println("signDigest: ${toHex(signDigest)}")

    //-----------------------------------------
    // step 4: sign
    //-----------------------------------------
    val signerToSigs: Map<String, ByteString> = signers.map {
        it.address.toBech32(accountPrefix) to ByteString.copyFrom(it.sign(signDigest).copyOfRange(0, 64))
    }.toMap()

    println(txBody.toByteString().toString())
    //-----------------------------------------
    // step 5: generate signed tx
    //-----------------------------------------
    val signedTx = makeMultisignedTx(
        multiSigPubKey,
        multiSigAccSeq,
        unsignedSignDoc.fee,
        txBody.toByteString(),
        signerToSigs
    )

    //-----------------------------------------
    // step 6: broadcast the signed tx
    //-----------------------------------------
    val result = TxClient(channel).use { it.broadcastTx(signedTx) }
    println("result: $result")
}
