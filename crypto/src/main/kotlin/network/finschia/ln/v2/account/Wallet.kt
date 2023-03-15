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

/**
 * Wallet class signs the message using own keys
 */
interface Wallet {

    /**
     * the public key derived from the private key
     * (wrapped with TypedValue)
     */
    val pubKey: PubKey

    /**
     * the address corresponding the key of the wallet
     */
    val address: Address

    /**
     * Signs the input message to generate a signature
     *
     * @param message to sign
     * @return signature bytes
     */
    fun sign(message: ByteArray): ByteArray
}
