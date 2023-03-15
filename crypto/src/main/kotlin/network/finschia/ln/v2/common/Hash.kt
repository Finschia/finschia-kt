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
package network.finschia.ln.v2.common

import org.bouncycastle.util.encoders.Hex

/**
 * Hash represents the hash value such as tx hash, block hash
 */
class Hash {

    /**
     * raw-bytes of the Hash
     */
    val value: ByteArray

    /**
     * initiates an empty hash
     */
    private constructor() : this(byteArrayOf())

    /**
     * initiates with hash bytes
     */
    constructor(value: ByteArray) {
        this.value = value
    }

    /**
     * initiates with a hash string
     */
    constructor(value: String) : this(Hex.decode(value))

    fun isEmpty(): Boolean = value.isEmpty()

    /**
     * stringifies the address
     *
     * @return stringified address
     */
    override fun toString(): String {
        // From the cosmos's manner, hashes are represented as upper-cased hex-string.
        // The SDK follows it.
        return Hex.toHexString(value).uppercase()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hash
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}
