package network.finschia.sdk.example

import io.grpc.ManagedChannelBuilder
import network.finschia.sdk.account.HDWallet
import network.finschia.sdk.base.Tx

suspend fun main() {
    val port = 9090
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

    val mnemonic = "mind flame tobacco sense move hammer drift crime ring globe art gaze cinnamon helmet cruise special produce notable negative wait path scrap recall have"
    val hdWallet = HDWallet.loadFromMnemonic(mnemonic)

    // alice sends 10000tcony to bob
    // AND bob refunds
    val aliceKey = hdWallet.getKeyWallet(0)
    val bobKey = hdWallet.getKeyWallet(1)

    val aliceAddress = aliceKey.address.toBech32("link")
    val bobAddress = bobKey.address.toBech32("link")
    val amount = cosmos.base.v1beta1.coin {
        amount = "10000"
        denom = "tcony"
    }

    val msgSend = cosmos.bank.v1beta1.msgSend {
        fromAddress = aliceAddress
        toAddress = bobAddress
        this.amount += amount
    }
    val msgRefund = cosmos.bank.v1beta1.msgSend {
        fromAddress = bobAddress
        toAddress = aliceAddress
        this.amount += amount
    }

    val tx = Tx.newBuilder()
        .setChainId("sim")
        .addMessage(com.google.protobuf.any {
            typeUrl = "/cosmos.bank.v1beta1.MsgSend"
            value = msgSend.toByteString()
        })
        .addMessage(com.google.protobuf.any {
            typeUrl = "/cosmos.bank.v1beta1.MsgSend"
            value = msgRefund.toByteString()
        })
        .addSigner(aliceKey, 1)
        .addSigner(bobKey, 0)
        .setFee(cosmos.tx.v1beta1.fee { gasLimit = 100000 })
        .setMemo("")
        .build()

    tx.sign(aliceKey, 0)
    tx.sign(bobKey, 1)

    val result = TxClient(channel).use { it.broadcastTx(tx) }
    println(result)
}
