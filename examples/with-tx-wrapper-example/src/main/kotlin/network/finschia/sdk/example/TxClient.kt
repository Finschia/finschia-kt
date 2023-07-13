package network.finschia.sdk.example

import cosmos.tx.v1beta1.ServiceOuterClass
import io.grpc.ManagedChannel
import network.finschia.sdk.base.Tx
import java.io.Closeable
import java.util.concurrent.TimeUnit

class TxClient(private val channel: ManagedChannel): Closeable {
    private val stub = cosmos.tx.v1beta1.ServiceGrpcKt.ServiceCoroutineStub(channel)

    suspend fun broadcastTx(tx: Tx): String {
        val request = cosmos.tx.v1beta1.broadcastTxRequest {
            txBytes = tx.raw().toByteString()
            mode = ServiceOuterClass.BroadcastMode.BROADCAST_MODE_SYNC
        }
        val response = stub.broadcastTx(request)
        return response.getTxResponse().getTxhash()
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}
