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

package network.finschia.sdk.common

/**
 * Wraps an object with type to recognize struct to unmarshal.
 *
 * @property type
 * @property value
 */
data class TypedValue<T : Any?>(val type: String, val value: T) {

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypedValue<*>

        if (type != other.type) return false

        if (value === other.value) return true
        if (value == null) return other.value == null
        if (other.value == null) return false

        if (value.javaClass != other.value.javaClass) return false

        return when (value) {
            is Array<*> -> value.contentEquals(other.value as Array<*>)
            is ByteArray -> value.contentEquals(other.value as ByteArray)
            is ShortArray -> value.contentEquals(other.value as ShortArray)
            is IntArray -> value.contentEquals(other.value as IntArray)
            is LongArray -> value.contentEquals(other.value as LongArray)
            is FloatArray -> value.contentEquals(other.value as FloatArray)
            is DoubleArray -> value.contentEquals(other.value as DoubleArray)
            is CharArray -> value.contentEquals(other.value as CharArray)
            else -> value == other.value
        }
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + when (value) {
            is Array<*> -> value.contentHashCode()
            is ByteArray -> value.contentHashCode()
            is ShortArray -> value.contentHashCode()
            is IntArray -> value.contentHashCode()
            is LongArray -> value.contentHashCode()
            is FloatArray -> value.contentHashCode()
            is DoubleArray -> value.contentHashCode()
            is CharArray -> value.contentHashCode()
            else -> value.hashCode()
        }
        return result
    }
}
