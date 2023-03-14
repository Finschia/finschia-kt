package network.link.ln.v2.base

import network.link.ln.v2.account.Wallet
import network.link.ln.v2.account.KeyWallet

import cosmos.crypto.secp256k1.Keys.PubKey
import cosmos.tx.signing.v1beta1.Signing.SignMode

import com.google.protobuf.Any
import com.google.protobuf.ByteString
import cosmos.tx.v1beta1.TxOuterClass.TxBody
import cosmos.tx.v1beta1.TxOuterClass.AuthInfo
import cosmos.tx.v1beta1.TxOuterClass.Fee

import cosmos.tx.v1beta1.TxOuterClass.TxRaw

import org.bouncycastle.jcajce.provider.digest.SHA256
import org.bouncycastle.util.encoders.Hex

class Tx(
    private val chainId: String,
    private val txBody: TxBody,
    private val authInfo: AuthInfo,
    private val pubKeys: List<ByteString>,
) {
    private var signatures = HashMap<ByteString, ByteString>()

    fun raw() = TxRaw.newBuilder()
        .setBodyBytes(txBody.toByteString())
        .setAuthInfoBytes(authInfo.toByteString())
        .addAllSignatures(pubKeys.map { signatures[it] })
        .build()

    fun hash() = Hex.toHexString(SHA256.Digest().digest(raw().toByteArray())).uppercase()

    fun signDoc(cosmosAccountNumber: Long) = cosmos.tx.v1beta1.signDoc {
        chainId = this@Tx.chainId
        bodyBytes = txBody.toByteString()
        authInfoBytes = authInfo.toByteString()
        accountNumber = cosmosAccountNumber
    }

    fun sign(wallet: Wallet, cosmosAccountNumber: Long) {
        val pubKey = ByteString.copyFrom(wallet.pubKey.body)
        if (pubKey in pubKeys) {
            val sig = wallet.sign(SHA256.Digest().digest(signDoc(cosmosAccountNumber).toByteArray()))
            signatures[pubKey] = ByteString.copyFrom(sig.copyOfRange(0, 64))
        }
    }

    companion object {
        fun newBuilder() = Builder()
    }
    class Builder {
        private var chainId = ""
        private var txBodyBuilder = TxBody.newBuilder()
        private var authInfoBuilder = AuthInfo.newBuilder()

        private var pubKeys: MutableList<ByteString> = mutableListOf()

        fun build() = Tx(
            chainId,
            txBodyBuilder.build(),
            authInfoBuilder.build(),
            pubKeys,
        )

        fun addMessage(msg: Any) = this.apply {
            txBodyBuilder.addMessages(msg)
        }
        fun addSigner(wallet: KeyWallet, sequence: Long) = this.apply {
            val pubKey = ByteString.copyFrom(wallet.pubKey.body)
            if (pubKey !in pubKeys) {
                pubKeys.add(pubKey)

                val cryptoPubKey = PubKey.newBuilder()
                    .setKey(pubKey)
                    .build()
                val anyKey = com.google.protobuf.any {
                    typeUrl = "/cosmos.crypto.secp256k1.PubKey"
                    value = cryptoPubKey.toByteString()
                }

                val signerInfo = cosmos.tx.v1beta1.signerInfo {
                    publicKey = anyKey
                    modeInfo = cosmos.tx.v1beta1.modeInfo {
                        single = cosmos.tx.v1beta1.ModeInfoKt.single {
                            mode = SignMode.SIGN_MODE_DIRECT
                        }
                    }
                    this.sequence = sequence
                }
                authInfoBuilder.addSignerInfos(signerInfo)
            }
        }
        fun setChainId(id: String) = this.apply {
            chainId = id
        }
        fun setFee(fee: Fee) = this.apply {
            authInfoBuilder.setFee(fee)
        }
        fun setMemo(memo: String) = this.apply {
            txBodyBuilder.setMemo(memo)
        }
    }
}
