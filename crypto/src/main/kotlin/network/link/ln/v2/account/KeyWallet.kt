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

import network.link.ln.v2.account.PubKey.Companion.createFromPrivateKey
import network.link.ln.v2.crypto.ECDSASignature
import network.link.ln.v2.crypto.KeyStore
import network.link.ln.v2.crypto.LinkKeys
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException

/**
 * An implementation of Wallet which uses of the key pair.
 *
 * @property privateKey raw private key
 */
class KeyWallet private constructor(val privateKey: ByteArray) : Wallet {
    /**
     * @see Wallet.pubKey
     */
    override val pubKey: PubKey = createFromPrivateKey(privateKey)

    /**
     * address derived from the public key
     * @see Wallet.address
     */
    override val address: Address = Address(pubKey)

    /**
     * @see Wallet.sign
     */
    override fun sign(message: ByteArray): ByteArray {
        val signature = ECDSASignature(privateKey)
        val sig = signature.generateSignature(message)
        return signature.recoverableSerialize(sig, message)
    }

    fun exportKeyStore(passphrase: String): String =
        KeyStore.createFromPrivateKey(privateKey, passphrase).export()


    fun exportKeyStore(keyStoreData: OutputStream, passphrase: String) {
        KeyStore.createFromPrivateKey(privateKey, passphrase).export(keyStoreData)
    }

    fun exportKeyStore(keyStoreData: Writer, passphrase: String) {
        KeyStore.createFromPrivateKey(privateKey, passphrase).export(keyStoreData)
    }

    companion object {
        /**
         * Creates a new KeyWallet with generating a new key pair.
         *
         * @return new KeyWallet
         */
        @JvmStatic
        @Throws(
            InvalidAlgorithmParameterException::class,
            NoSuchAlgorithmException::class,
            NoSuchProviderException::class
        )
        fun create(): KeyWallet = KeyWallet(LinkKeys.createPrivateKey())

        /**
         * Loads a key wallet from the input [privateKey]
         *
         * @return KeyWallet
         */
        @JvmStatic
        fun loadFromPrivateKey(privateKey: ByteArray): KeyWallet = KeyWallet(privateKey)

        @JvmStatic
        fun loadFromKeyStore(keyStoreData: Reader, passphrase: String): KeyWallet =
            KeyWallet(KeyStore.load(keyStoreData).getPrivateKey(passphrase))

        @JvmStatic
        fun loadFromKeyStore(keyStoreData: InputStream, passphrase: String): KeyWallet =
            KeyWallet(KeyStore.load(keyStoreData).getPrivateKey(passphrase))
    }
}
