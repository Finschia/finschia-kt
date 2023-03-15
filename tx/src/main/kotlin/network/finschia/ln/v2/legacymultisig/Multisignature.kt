package network.finschia.ln.v2.legacymultisig

import com.google.protobuf.ByteString
import org.bitcoinj.core.Bech32
import java.util.Base64
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/41884540d2927538fb45adcb36badd79bdfd3e55/packages/stargate/src/multisignature.ts
 */

fun makeCompactBitArray(bits: List<Boolean>): cosmos.crypto.multisig.v1beta1.Multisig.CompactBitArray {
    val byteCount = (ceil(bits.size.toDouble() / 8)).toInt()
    val extraBits = (bits.size - floor(bits.size.toDouble() / 8) * 8).toInt()
    val bytes = ByteArray(byteCount) // zero-filled

    bits.forEachIndexed { index, element ->
        val bytePos = floor(index.toDouble() / 8).toInt()
        val bitPos = index % 8
        1.shl(8 - 1 - bitPos)
        if (element) bytes[bytePos] = (bytes[bytePos].toInt() or 1.shl(8 - 1 - bitPos)).toByte()
    }

    return cosmos.crypto.multisig.v1beta1.compactBitArray{
        this.elems = ByteString.copyFrom(bytes)
        this.extraBitsStored = extraBits
    }
}

fun makeMultisignedTx(multisigPubkey: AminoPubKey<MultisigThresholdPubkeyValue>, multiSigAccSeq: Int, fee: StdFee, bodyBytes: ByteString, signatures: Map<String, ByteString>, ): cosmos.tx.v1beta1.TxOuterClass.TxRaw {
    val addresses = signatures.toList().map { it.first }
    val prefix = Bech32.decode(addresses[0]).hrp

    val signers: MutableList<Boolean> = MutableList(multisigPubkey.value.pubkeys.size){ false }
    val signaturesList : MutableList<ByteString> = mutableListOf()
    multisigPubkey.value.pubkeys.forEachIndexed { index, element ->
        val signerAddress = pubkeyToAddress(element, prefix)
        val signature = signatures[signerAddress]
        if (signature != null) {
            signers[index] = true
            signaturesList += signature
        }
    }

    val signerInfo = cosmos.tx.v1beta1.signerInfo {
        this.publicKey = com.google.protobuf.any {
            this.typeUrl = "/cosmos.crypto.multisig.LegacyAminoPubKey"
            this.value = cosmos.crypto.multisig.legacyAminoPubKey {
                this.threshold = multisigPubkey.value.threshold.toInt()
                this.publicKeys += multisigPubkey.value.pubkeys.map {
                    com.google.protobuf.any {
                        this.typeUrl = "/cosmos.crypto.secp256k1.PubKey"
                        this.value = cosmos.crypto.secp256k1.pubKey {
                            this.key = ByteString.copyFrom(Base64.getDecoder().decode(it.value))
                        }.toByteString()
                    }
                }
            }.toByteString()
        }
        this.modeInfo = cosmos.tx.v1beta1.modeInfo {
            this.multi = cosmos.tx.v1beta1.ModeInfoKt.multi {
                this.bitarray = makeCompactBitArray(signers)
                this.modeInfos += signaturesList.map {
                    cosmos.tx.v1beta1.modeInfo {
                        this.single = cosmos.tx.v1beta1.ModeInfoKt.single {
                            this.mode = cosmos.tx.signing.v1beta1.Signing.SignMode.SIGN_MODE_LEGACY_AMINO_JSON
                        }
                    }
                }
            }
        }
        this.sequence = multiSigAccSeq.toLong()
    }

    val authInfo = cosmos.tx.v1beta1.authInfo {
        this.signerInfos += signerInfo
        this.fee = cosmos.tx.v1beta1.fee {
            this.amount += fee.amount.map {
                cosmos.base.v1beta1.coin {
                    this.denom = it.denom
                    this.amount = it.amount
                }
            }
            this.gasLimit = fee.gas.toLong()
        }
    }

    val authInfoBytes = authInfo.toByteString()

    return cosmos.tx.v1beta1.txRaw {
        this.bodyBytes = bodyBytes
        this.authInfoBytes = authInfoBytes
        this.signatures += cosmos.crypto.multisig.v1beta1.multiSignature {
            this.signatures += signaturesList
        }.toByteString()
    }
}
