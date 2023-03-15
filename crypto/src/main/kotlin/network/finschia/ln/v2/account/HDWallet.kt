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

import network.finschia.ln.v2.crypto.Bip44WalletUtils.generateMnemonic
import network.finschia.ln.v2.crypto.Bip44WalletUtils.generatePrivateKey
import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException

/**
 * An implementation of BIP-44 HD Wallet which uses of the key pair.
 *
 * @property mnemonic raw private key
 */
class HDWallet private constructor(val mnemonic: String) {

    @JvmOverloads
    fun getKeyWallet(lbmAccountNumber: Int = 0, index: Int = 0): KeyWallet =
        KeyWallet.loadFromPrivateKey(generatePrivateKey(mnemonic, lbmAccountNumber, index))

    companion object {

        /**
         * Creates a new HDWallet with generating a new mnemonic.
         *
         * @return new HDWallet
         */
        @JvmStatic
        @Throws(
            InvalidAlgorithmParameterException::class,
            NoSuchAlgorithmException::class,
            NoSuchProviderException::class
        )
        fun create(): HDWallet = HDWallet(generateMnemonic())

        /**
         * Loads a HD wallet from the input [mnemonic]
         *
         * @return HDWallet
         */
        @JvmStatic
        fun loadFromMnemonic(mnemonic: String): HDWallet = HDWallet(mnemonic)
    }
}
