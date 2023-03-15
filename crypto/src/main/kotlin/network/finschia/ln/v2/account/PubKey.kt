/*
 * Copyright 2019 LINK Network.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.finschia.ln.v2.account

import network.finschia.ln.v2.account.Type.Companion.DEFAULT_BECH32_HRP_PREFIX
import network.finschia.ln.v2.account.Type.Companion.toHrpPrefix
import network.finschia.ln.v2.common.TypedValue
import network.finschia.ln.v2.crypto.Amino
import network.finschia.ln.v2.crypto.Bech32Utils
import network.finschia.ln.v2.crypto.LinkKeys
import org.bitcoinj.core.Bech32
import org.bouncycastle.util.encoders.Hex

/**
 * PubKey represents the account's public key.
 *
 * There are three ways to initiate a address
 * - with a private key bytes: `createFromPrivateKey(byte[] pubKeyBytes)`
 * - with an public key raw body: `new PubKey(byte[] body)`
 * - with a stringified public key: `new PubKey(String bech32pubKey)`
 */
class PubKey {

    /**
     * type of the public key
     */
    val type: Type

    /**
     * encoding type of the public key
     */
    val encodingType: String = PUB_KEY_TYPE_SECP256K1

    /**
     * raw-bytes body of the public key
     */
    val body: ByteArray

    /**
     * initiates an public key using public key [type] and raw [body]
     */
    constructor(type: Type, body: ByteArray) {
        this.type = type
        this.body = body
        checkBodySize()
    }

    /**
     * initiates an account public key using raw public key [body]
     */
    constructor(body: ByteArray) : this(Type.ACCOUNT, body)

    /**
     * initiates a public key using [bech32pubKey]
     */
    @JvmOverloads
    constructor(bech32pubKey: String, hrpPrefix: String = DEFAULT_BECH32_HRP_PREFIX) {
        val bech32Data = Bech32.decode(bech32pubKey)

        require(bech32Data.hrp.startsWith(hrpPrefix)) {
            "Illegal HRP prefix('${bech32Data.hrp}' does not start with '$hrpPrefix')"
        }

        require(bech32Data.hrp.endsWith(BECH32_HRP_SUFFIX)) {
            "Illegal HRP suffix('${bech32Data.hrp}' does not end with '$BECH32_HRP_SUFFIX')"
        }

        type =
            Type(bech32Data.hrp.substring(hrpPrefix.length, bech32Data.hrp.length - BECH32_HRP_SUFFIX.length))

        // the output of the bech32Data.data is 64 bytes length and each byte holds the 5 bits
        // using `convertBits` trims bits so you can get amino encoded body that is 39 bytes.
        // remove amino prefix that is 5 bytes and a redundant last 1 byte that added during bech32 process
        // and the body will finally 33 bytes.
        val aminoEncodedBody = Bech32Utils.convertBits(
            bech32Data.data, 0, bech32Data.data.size, 5, 8, true
        )
        body = Amino.removeAminoPrefix(aminoEncodedBody, PUBLIC_KEY_SIZE_IN_BYTE)
        checkBodySize()
    }

    private fun checkBodySize() {
        val bodySize = body.size
        require(bodySize == PUBLIC_KEY_SIZE_IN_BYTE) {
            "public key is $PUBLIC_KEY_SIZE_IN_BYTE bytes, but input is $bodySize bytes"
        }
    }

    /**
     *  Returns a TypedValue of the pubKey
     */
    fun asTypedValue() = TypedValue(encodingType, body)

    /**
     * returns bech32 formatted string of the public key
     *
     * @return <a href="https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki">bech32</a> formatted string
     */
    @JvmOverloads
    fun toBech32(hrpPrefix: String = DEFAULT_BECH32_HRP_PREFIX): String {
        val encodedBody: ByteArray = Amino.addAminoPrefix(encodingType, body)

        // using `convertBits`, makes each byte hold 5 bits for `Bech32` class to recognize.
        val bech32data = Bech32Utils.convertBits(
            encodedBody, 0, encodedBody.size, 8, 5, true
        )

        val hrp = "${hrpPrefix}${type.toHrpPrefix()}$BECH32_HRP_SUFFIX"
        return Bech32.encode(hrp, bech32data)
    }

    /**
     * returns hex string of the public key
     *
     * @return hex string
     */
    fun toHexString(): String = Hex.toHexString(body)

    /**
     * stringifies the public key
     *
     * @return stringified public key
     */
    override fun toString(): String = toBech32()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PubKey
        return type == other.type && body.contentEquals(other.body)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }

    companion object {
        private const val BECH32_HRP_SUFFIX = "pub"
        private const val PUB_KEY_TYPE_SECP256K1 = "tendermint/PubKeySecp256k1"
        internal const val PUBLIC_KEY_SIZE_IN_BYTE = 33

        /**
         * initiates a public key using private key bytes
         *
         * @param privateKey private key bytes
         * @return new public key corresponding to the given private key
         */
        @JvmStatic
        fun createFromPrivateKey(privateKey: ByteArray): PubKey =
            PubKey(LinkKeys.getPublicKey(privateKey, true))
    }
}
