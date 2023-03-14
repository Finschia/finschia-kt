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
package network.link.ln.v2.account

import network.link.ln.v2.account.Type.ACCOUNT
import network.link.ln.v2.account.Type.Companion.DEFAULT_BECH32_HRP_PREFIX
import network.link.ln.v2.account.Type.Companion.toHrpPrefix
import network.link.ln.v2.crypto.Bech32Utils
import org.bitcoinj.core.Bech32
import org.bouncycastle.jcajce.provider.digest.RIPEMD160
import org.bouncycastle.jcajce.provider.digest.SHA256
import org.bouncycastle.util.encoders.Hex

/**
 * Address represents the account's address.
 *
 * There are three ways to initiate a address
 * - with a public key: `Address(pubKey: PubKey)`, `Address.of(PubKey pubKey)`
 * - with an address raw body: `Address(body: ByteArray)`, `Address.of(byte[] body)`
 * - with a stringified address: `Address(bech32Address: String)`, `Address.of(String bech32Address)`
 *
 * @property type type of the address
 * @property body raw-bytes body of the address
 */
class Address private constructor(val type: Type, val body: ByteArray) {

    /**
     * returns bech32 formatted string of the address
     *
     * @return <a href="https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki">bech32</a> formatted string
     */
    @JvmOverloads
    fun toBech32(hrpPrefix: String = DEFAULT_BECH32_HRP_PREFIX): String {
        if (body.isEmpty()) return ""

        // using `convertBits`, makes each byte hold 5 bits for `Bech32` class to recognize.
        val bech32data = Bech32Utils.convertBits(body, 0, body.size, 8, 5, true)

        return Bech32.encode("${hrpPrefix}${type.toHrpPrefix()}", bech32data)
    }

    /**
     * returns hex string of the address
     *
     * @return hex string
     */
    fun toHexString(): String {
        return Hex.toHexString(body)
    }

    /**
     * stringifies the address
     *
     * @return stringified address
     */
    override fun toString(): String {
        return toBech32()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address
        return type == other.type && body.contentEquals(other.body)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }

    companion object {
        private const val ADDRESS_SIZE_IN_BYTE = 20

        /**
         * An empty address
         */
        @JvmField
        val EMPTY_ADDRESS = Address(ACCOUNT, byteArrayOf())

        /**
         * initiates an account address using address [type] and address raw [body]
         *
         * @param type address type
         * @param body address body
         * @return new address corresponding to the given [body]
         */
        @JvmStatic
        @JvmName("of")
        operator fun invoke(type: Type, body: ByteArray): Address {
            require(body.size == ADDRESS_SIZE_IN_BYTE) {
                "Illegal body size(required $ADDRESS_SIZE_IN_BYTE, given ${body.size})"
            }

            return Address(type, body)
        }

        /**
         * initiates an account address using address raw [body]
         *
         * @param body address body
         * @return new address corresponding to the given [body]
         */
        @JvmStatic
        @JvmName("of")
        operator fun invoke(body: ByteArray): Address = invoke(ACCOUNT, body)

        /**
         * initiates an address from [bech32Address]
         *
         * @param bech32Address Bech32 address
         * @return new address corresponding to the given [bech32Address]
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("of")
        operator fun invoke(bech32Address: String, hrpPrefix: String = DEFAULT_BECH32_HRP_PREFIX): Address {
            val bech32Data = Bech32.decode(bech32Address)

            require(bech32Data.hrp.startsWith(hrpPrefix)) {
                "Illegal HRP prefix('${bech32Data.hrp}' does not start with '$hrpPrefix')"
            }

            val type = Type(bech32Data.hrp.substring(hrpPrefix.length))
            // the output of the bech32Data.data is 32 bytes length and each byte holds the 5 bits
            // using `convertBits` trims bits so the body will finally 20 bytes.
            val body = Bech32Utils.convertBits(bech32Data.data, 0, bech32Data.data.size, 5, 8, true)

            return invoke(type, body)
        }

        /**
         * initiates an address using public key bytes
         *
         * @param pubKey public key object
         * @return new address corresponding to the given public key
         */
        @JvmStatic
        @JvmName("of")
        operator fun invoke(pubKey: PubKey): Address {

            // ripemd160(sha256(pubkey)) -> 20 length of bytes
            val body = RIPEMD160.Digest().digest(SHA256.Digest().digest(pubKey.body))

            return invoke(pubKey.type, body)
        }
    }
}
