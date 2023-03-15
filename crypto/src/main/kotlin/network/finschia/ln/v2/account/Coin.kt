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

import java.math.BigInteger

/**
 * data class of coin type
 *
 * @property denom denomination name of the coin
 * @property amount amount of the coin
 */
data class Coin(val denom: String, val amount: BigInteger) {
    override fun toString(): String {
        return amount.toString() + denom
    }
}

/**
 * data class of collection coin type
 *
 * @property tokenId tokenId
 * @property amount amount
 */
data class CollectionCoin(val tokenId: String, val amount: BigInteger) {
    override fun toString(): String {
        return "$amount:$tokenId"
    }
}
