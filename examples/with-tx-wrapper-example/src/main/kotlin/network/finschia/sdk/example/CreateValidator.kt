package network.finschia.sdk.example

import com.google.protobuf.kotlin.toByteString
import io.grpc.ManagedChannelBuilder
import network.finschia.sdk.account.HDWallet
import network.finschia.sdk.base.Tx
import java.util.*

suspend fun main() {
    val port = 9090
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

    val mnemonic =
        "mind flame tobacco sense move hammer drift crime ring globe art gaze cinnamon helmet cruise special produce notable negative wait path scrap recall have"
    val hdWallet = HDWallet.loadFromMnemonic(mnemonic)
    val aliceKey = hdWallet.getKeyWallet(0)

    val aliceAddress = aliceKey.address.toBech32("link")
    println(aliceAddress)
    val aliceOperatorAddress = aliceKey.address.toBech32("linkvaloper")
    val amount = cosmos.base.v1beta1.coin {
        amount = "10000"
        denom = "stake"
    }

    val pubKey = "bA6ozZ+fYrhU0KrRoJjFIKBWnOzd86G5dGJSCvlyuhA="
    val pubKeyBytes = Base64.getDecoder().decode(pubKey).toByteString()
    val header = ByteArray(2)
    header[0] = 0x0a
    header[1] = pubKeyBytes.size().toByte()
    val pubKeyVal = header + pubKeyBytes.toByteArray()
    val pk = com.google.protobuf.any {
        typeUrl = "/cosmos.crypto.ed25519.PubKey"
        value = pubKeyVal.toByteString()
    }

    val msgCreateValidator = cosmos.staking.v1beta1.msgCreateValidator {
        description = cosmos.staking.v1beta1.description {
            moniker = "sim"
        }
        commission = cosmos.staking.v1beta1.commissionRates {
            rate = "200000000000000000"
            maxRate = "200000000000000000"
            maxChangeRate = "1"
        }
        minSelfDelegation = "1"
        delegatorAddress = aliceAddress
        validatorAddress = aliceOperatorAddress
        pubkey = pk
        value = amount
    }

    val tx = Tx.newBuilder()
        .setChainId("sim")
        .addMessage(com.google.protobuf.any {
            typeUrl = "/cosmos.staking.v1beta1.MsgCreateValidator"
            value = msgCreateValidator.toByteString()
        })
        .addSigner(aliceKey, 2)
        .setFee(cosmos.tx.v1beta1.fee { gasLimit = 100000 })
        .setMemo("")
        .build()

    tx.sign(aliceKey, 0)

    val result = TxClient(channel).use { it.broadcastTx(tx) }
    println(result)
}
