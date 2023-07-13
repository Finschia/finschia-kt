package network.finschia.sdk.example

import com.google.protobuf.ByteString
import io.grpc.ManagedChannelBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import network.finschia.sdk.account.Address
import network.finschia.sdk.account.HDWallet
import network.finschia.sdk.account.KeyWallet
import network.finschia.sdk.legacymultisig.*
import org.bouncycastle.jcajce.provider.digest.SHA256

class MultisigMsgSend {
    companion object {
        fun addressFromMultiPubKey(
            pubKeyList: List<ByteArray>,
            threshold: Int,
            addressPrefix: String = "link"
        ): String {
            val pubKeys = pubKeyList.map { encodeSecp256k1Pubkey(it) }
            val multisigPubKey = createMultisigThresholdPubkey(pubKeys, threshold, txSigLimit = pubKeyList.size)
            val multisigAddr = pubkeyToAddress(multisigPubKey, addressPrefix)
            return multisigAddr
        }

        fun createMsgSend(
            senderAddress: String,
            recipientAddress: String,
            amounts: List<cosmos.base.v1beta1.CoinOuterClass.Coin>
        ): cosmos.bank.v1beta1.Tx.MsgSend {
            val msg = cosmos.bank.v1beta1.msgSend {
                fromAddress = senderAddress
                toAddress = recipientAddress
                amount += amounts
            }
            return msg
        }

        fun convertMsgSendToAminoMsg(msgSend: cosmos.bank.v1beta1.Tx.MsgSend): AminoMsg {
            val coins = msgSend.amountList.map {
                Coin(
                    denom = it.denom,
                    amount = it.amount.toString()
                )
            }
            val jsonAminoMsgSend = AminoMsgSendValue(
                fromAddress = msgSend.fromAddress,
                amount = coins,
                toAddress = msgSend.toAddress,
            )

            return AminoMsg(
                type = "cosmos-sdk/MsgSend",
                value = Json.encodeToJsonElement(jsonAminoMsgSend)
            )
        }

        fun generateTxBody(
            sendMsg: cosmos.bank.v1beta1.Tx.MsgSend,
            timeoutHeight: Int
        ): cosmos.tx.v1beta1.TxOuterClass.TxBody {
            return cosmos.tx.v1beta1.txBody {
                this.messages += com.google.protobuf.any {
                    this.typeUrl = "/cosmos.bank.v1beta1.MsgSend"
                    this.value = sendMsg.toByteString()
                }
                this.timeoutHeight = timeoutHeight.toLong()
            }
        }

        fun getSignDigest(signDoc: StdSignDoc): ByteArray {
            return SHA256.Digest()
                .digest(Json.encodeToJsonElement(signDoc).removeNull().sort().toString().toByteArray())
        }

        fun generateSignDoc(
            sendMsgs: List<cosmos.bank.v1beta1.Tx.MsgSend>,
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
                    amount = listOf(Coin(amount = "20", denom = "cony")),
                    gas = gasLimit.toString(),
                ),
                msgs = sendMsgs.map { convertMsgSendToAminoMsg(it) },
            )
        }
    }
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
    //
    // You need to input `txSigLimit`. `txSigLimit` means the upper limit of multi-sig public key number.
    // `txSigLimit` is different for each chain. Please check your chain `txSigLimit` with reference to
    // https://github.com/Finschia/finschia-sdk/blob/main/x/auth/spec/07_client.md#params-1.
    //
    // `pubKeylist` size should not exceed the upper limit. If it exceeds the upper limit, you will not
    // be able to remit payment from the address generated by its `pubKeylist`. This means that the assets
    // cannot be retrieved from the address.
    //
    // For more information:
    // https://hub.cosmos.network/main/governance/params-change/Auth.html#txsiglimit
    // https://docs.cosmos.network/v0.45/modules/auth/03_antehandlers.html
    // https://docs.cosmos.network/v0.45/modules/auth/07_params.html
    val multiSigPubKey = createMultisigThresholdPubkey(pubKeyList, threshold, txSigLimit = 7)
    // multi-sig address
    val multiSigAddress = pubkeyToAddress(multiSigPubKey, accountPrefix)
    val multiSigAccNum = 9
    var multiSigAccSeq = 0
    val timeoutHeight = 0

    // receiver address
    val recipientAddress = Address(hdWallet.getKeyWallet(pubKeyNum + 1).pubKey).toBech32(accountPrefix)
    // remittance amount
    val fundAmount = 1
    val baseDenom = "cony"

    // scenario description
    println(
        "scenario: " +
                "$multiSigAddress ($threshold of $pubKeyNum multi-sig address, acc num: $multiSigAccNum, acc seq: $multiSigAccSeq) sends $fundAmount$baseDenom to $recipientAddress on $chainId chain. " +
                "Gas limit is set to $gasLimit and timeout height is ${if (timeoutHeight <= 0) "not set" else "set to $timeoutHeight"}."
    )

    //-----------------------------------------
    // step 2: generate `MsgSend` unsigned tx
    //-----------------------------------------
    // generate sendMsg
    val msgSend = MultisigMsgSend.createMsgSend(
        multiSigAddress,
        recipientAddress,
        listOf(cosmos.base.v1beta1.coin {
            this.amount = fundAmount.toString()
            this.denom = baseDenom
        })
    )

    // generate unsigned tx body(amino type)
    val txBody = MultisigMsgSend.generateTxBody(msgSend, timeoutHeight)

    // generate amino signDoc
    val unsignedSignDoc = MultisigMsgSend.generateSignDoc(
        listOf(msgSend),
        multiSigAccNum,
        multiSigAccSeq,
        timeoutHeight,
        gasLimit,
        chainId
    )

    //-----------------------------------------
    // step 3: generate signature digest
    //-----------------------------------------
    // generate sign digest
    val signDigest = MultisigMsgSend.getSignDigest(unsignedSignDoc)

    //-----------------------------------------
    // step 4: sign
    //-----------------------------------------
    val signerToSigs: Map<String, ByteString> = signers.map {
        it.address.toBech32(accountPrefix) to ByteString.copyFrom(it.sign(signDigest).copyOfRange(0, 64))
    }.toMap()

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
