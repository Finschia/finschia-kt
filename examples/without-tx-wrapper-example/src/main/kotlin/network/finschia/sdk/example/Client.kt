package network.finschia.sdk.example

import com.google.protobuf.Message
import com.google.protobuf.Any
import com.google.protobuf.ByteString

import cosmos.tx.v1beta1.ServiceOuterClass.BroadcastMode
import network.finschia.sdk.account.HDWallet
import network.finschia.sdk.account.KeyWallet
import network.finschia.sdk.account.Address

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

import java.io.Closeable
import java.util.concurrent.TimeUnit

import cosmos.crypto.secp256k1.Keys.PubKey
import cosmos.tx.signing.v1beta1.Signing.SignMode

import cosmos.tx.v1beta1.TxOuterClass.TxBody
import cosmos.tx.v1beta1.TxOuterClass.AuthInfo
import cosmos.tx.v1beta1.TxOuterClass.Fee
import cosmos.tx.v1beta1.TxOuterClass.TxRaw
import cosmos.tx.v1beta1.TxOuterClass.Tx

import org.bouncycastle.jcajce.provider.digest.SHA256
import org.bouncycastle.util.encoders.Hex

class TxClient(private val channel: ManagedChannel): Closeable {
    private val stub = cosmos.tx.v1beta1.ServiceGrpcKt.ServiceCoroutineStub(channel)

    suspend fun broadcastTx(txBytes: ByteString): String {
        val request = cosmos.tx.v1beta1.broadcastTxRequest {
            this.txBytes = txBytes
            mode = BroadcastMode.BROADCAST_MODE_SYNC
        }
        val response = stub.broadcastTx(request).txResponse
        if (response.code != 0) {
            throw IllegalArgumentException(response.rawLog)
        }
        return response.txhash
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

class Utility {
    companion object {
        fun msgToAny(message: com.google.protobuf.Message) =
            com.google.protobuf.any {
                typeUrl = msgTypeUrl(message)
                value = message.toByteString()
            }
        private fun msgTypeUrl(message: com.google.protobuf.Message) =
            "/" + message.getDescriptorForType().getFullName()

        fun rawFromTx(tx: Tx) = cosmos.tx.v1beta1.txRaw {
            bodyBytes = tx.body.toByteString()
            authInfoBytes = tx.authInfo.toByteString()
            signatures += tx.getSignaturesList()
        }
        fun rawToTx(raw: TxRaw) = cosmos.tx.v1beta1.tx {
            body = TxBody.parseFrom(raw.bodyBytes)
            authInfo = AuthInfo.parseFrom(raw.authInfoBytes)
            signatures += raw.getSignaturesList()
        }
    }
}

class ConcreteScenarios {
    companion object {
        private const val CHAIN_ID = "sim"
        private const val BASE_DENOM = "stake"

        fun createBankMsgSendSingleDirect(
            senderAddress: String,
            recipientAddress: String,
            sendAmount: Long,
            senderPubKey: PubKey,
            senderSequence: Long,
            timeoutHeight: Long,
            memo: String,
        ): ByteString {
            val coin = cosmos.base.v1beta1.coin {
                amount = sendAmount.toString()
                denom = BASE_DENOM
            }
            val msg = cosmos.bank.v1beta1.msgSend {
                fromAddress = senderAddress
                toAddress = recipientAddress
                amount += coin
            }

            // Step 1: build a tx
            val tx = cosmos.tx.v1beta1.tx {
                body = cosmos.tx.v1beta1.txBody {
                    messages += Utility.msgToAny(msg)
                    this.memo = memo
                    this.timeoutHeight = timeoutHeight
                }
                authInfo = cosmos.tx.v1beta1.authInfo {
                    signerInfos += cosmos.tx.v1beta1.signerInfo {
                        publicKey = Utility.msgToAny(senderPubKey)
                        modeInfo = cosmos.tx.v1beta1.modeInfo {
                            single = cosmos.tx.v1beta1.ModeInfoKt.single {
                                mode = SignMode.SIGN_MODE_DIRECT
                            }
                        }
                        sequence = senderSequence
                    }
                    fee = cosmos.tx.v1beta1.fee {
                        gasLimit = 1000000
                    }
                }
            }

            return Utility.rawFromTx(tx).toByteString()
        }

        fun signSingleDirect(
            txBytes: ByteString,
            wallet: KeyWallet,
            accountNumber: Long,
        ): ByteString {
            val unsigned = TxRaw.parseFrom(txBytes)

            val signDoc = cosmos.tx.v1beta1.signDoc {
                bodyBytes = unsigned.bodyBytes
                authInfoBytes = unsigned.authInfoBytes
                chainId = CHAIN_ID
                this.accountNumber = accountNumber
            }

            val digest = SHA256.Digest().digest(signDoc.toByteArray())
            val signature = ByteString.copyFrom(wallet.sign(digest).copyOfRange(0, 64))

            return cosmos.tx.v1beta1.txRaw {
                bodyBytes = unsigned.bodyBytes
                authInfoBytes = unsigned.authInfoBytes
                signatures += signature
            }.toByteString()
        }
    }
}

suspend fun main() {
    val port = 9090
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
    val client = TxClient(channel)

    val mnemonic = "mind flame tobacco sense move hammer drift crime ring globe art gaze cinnamon helmet cruise special produce notable negative wait path scrap recall have"
    val hdWallet = HDWallet.loadFromMnemonic(mnemonic)

    val aliceWallet = hdWallet.getKeyWallet(0)
    val bobWallet = hdWallet.getKeyWallet(1)

    val alicePubKey = cosmos.crypto.secp256k1.pubKey {
        key = ByteString.copyFrom(aliceWallet.pubKey.body)
    }

    val accountPrefix = "link"
    val aliceAddress = Address(aliceWallet.pubKey).toBech32(accountPrefix)
    val bobAddress = Address(bobWallet.pubKey).toBech32(accountPrefix)

    // invalid bech32 address will throw
    // val check = Address("link18w9fvd5alkegkm9q8dzhym3kmeshugvydpdyc9")
    val check = Address("link18w9fvd5alkegkm9q8dzhym3kmeshugvydpdyc8")

    // Step 1: create a unsigned tx
    val unsignedBytes = ConcreteScenarios.createBankMsgSendSingleDirect(
        aliceAddress, // sender address
        bobAddress,   // recipient address
        10000,        // amount in base denom
        alicePubKey,  // sender public key
        1,            // sender sequence
        100000,       // timeout height
        "",           // memo
    )

    // Step 2: attach the signature
    val signedBytes = ConcreteScenarios.signSingleDirect(
        unsignedBytes, // unsigned tx bytes
        aliceWallet,   // signer wallet
        0,             // signer account number
    )

    // Step 3: broadcast the tx
    // you must use your own client, not this example's.
    val result = client.broadcastTx(signedBytes)
    println(result)
}
