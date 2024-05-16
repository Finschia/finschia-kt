package network.finschia.sdk.example

import io.grpc.ManagedChannelBuilder
import network.finschia.sdk.account.HDWallet
import network.finschia.sdk.base.Tx

suspend fun main() {
    // Change me for your environment
    val chainId = "sim"
    val hrpPrefix = "link"
    val host = "localhost"
    val port = 9090
    val fromDenom = "cony"
    val toDenom = "peb"
    val conyAmount = "1"
    val swapRateFromConyToPeb = "148079656000000"
    val amountToTransfer = swapRateFromConyToPeb.toBigInteger().multiply(conyAmount.toBigInteger())
    val kaiaAddress = "0xf7bAc63fc7CEaCf0589F25454Ecf5C2ce904997c"
    val gasLimit: Long = 150000
    val seqNum: Long = 14

    // Initialize gRPC channel
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()

    // Sender's info
    val mnemonic = "mind flame tobacco sense move hammer drift crime ring globe art gaze cinnamon helmet cruise special produce notable negative wait path scrap recall have"
    val hdWallet = HDWallet.loadFromMnemonic(mnemonic)
    val aliceKey = hdWallet.getKeyWallet(0)
    val aliceAddress = aliceKey.address.toBech32(hrpPrefix)

    // Build MsgSwap
    val amountFromDenom = cosmos.base.v1beta1.coin {
        amount = conyAmount
        denom = fromDenom
    }
    val msgSwap = lbm.fswap.v1.msgSwap {
        this.fromCoinAmount = amountFromDenom
        this.toDenom = toDenom
        this.fromAddress = aliceAddress
    }

    // Build MsgTransfer
    val msgTransfer = lbm.fbridge.v1.msgTransfer {
        this.amount = amountToTransfer.toString()
        this.sender = aliceAddress
        this.receiver = kaiaAddress
    }

    // Build Tx with two messages
    val tx = Tx.newBuilder()
            .setChainId(chainId)
            .addMessage(com.google.protobuf.any {
                typeUrl = "/lbm.fswap.v1.MsgSwap"
                value = msgSwap.toByteString()
            })
            .addMessage(com.google.protobuf.any {
                typeUrl = "/lbm.fbridge.v1.MsgTransfer"
                value = msgTransfer.toByteString()
            })
            .addSigner(aliceKey, seqNum)
            .setFee(cosmos.tx.v1beta1.fee { this.gasLimit = gasLimit })
            .setMemo("")
            .build()

    // Sign Tx
    tx.sign(aliceKey, 0)

    // Send Tx
    val resultTxHash = TxClient(channel).use {
        it.broadcastTx(tx)
    }

    println(resultTxHash)
}
