package network.finschia.sdk.example

import io.grpc.ManagedChannel
import java.io.Closeable
import java.util.concurrent.TimeUnit

class TxClient(private val channel: ManagedChannel) : Closeable {
    private val stub = cosmos.tx.v1beta1.ServiceGrpcKt.ServiceCoroutineStub(channel)

    suspend fun broadcastTx(tx: cosmos.tx.v1beta1.TxOuterClass.TxRaw): cosmos.base.abci.v1beta1.Abci.TxResponse {
        val request = cosmos.tx.v1beta1.broadcastTxRequest {
            this.txBytes = tx.toByteString()
            this.mode = cosmos.tx.v1beta1.ServiceOuterClass.BroadcastMode.BROADCAST_MODE_SYNC
        }
        val response = stub.broadcastTx(request)
        return response.txResponse
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}