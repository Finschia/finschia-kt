package network.finschia.sdk.example

import network.finschia.sdk.base.Tx
import cosmos.tx.v1beta1.ServiceOuterClass.BroadcastMode
import network.finschia.sdk.account.HDWallet

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

import java.io.Closeable
import java.util.concurrent.TimeUnit

class TxClient(private val channel: ManagedChannel): Closeable {
    private val stub = cosmos.tx.v1beta1.ServiceGrpcKt.ServiceCoroutineStub(channel)

    suspend fun broadcastTx(tx: Tx): String {
        val request = cosmos.tx.v1beta1.broadcastTxRequest {
            txBytes = tx.raw().toByteString()
            mode = BroadcastMode.BROADCAST_MODE_SYNC
        }
        val response = stub.broadcastTx(request)
        return response.getTxResponse().getTxhash()
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

suspend fun main() {
    val port = 9090
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
    val client = TxClient(channel)

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

    val result = client.broadcastTx(tx)
    println(result)
}
