/*
 * Copyright 2020 LINK Network.
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

package network.link.ln.v2.common

import network.link.ln.v2.account.Address
import network.link.ln.v2.account.PubKey
import network.link.ln.v2.account.Type.Companion.DEFAULT_BECH32_HRP_PREFIX
import java.lang.reflect.Type
import java.math.BigInteger

/**
 * A collection of converters that converts the specific types to strings.
 */
interface ToStringConverters : Iterable<Pair<Type, Converter<Any, String>>> {

    /**
     * Gets a converter of the given type
     */
    fun get(type: Type): Converter<Any, String>?

    companion object {

        /**
         * Creates a default ToStringConverters
         */
        @JvmName("create")
        @JvmOverloads
        operator fun invoke(hrpPrefix: String = DEFAULT_BECH32_HRP_PREFIX) = object : ToStringConverters {

            /**
             * supported types
             */
            private val types: Set<Type> = setOf(
                Address::class.java,
                PubKey::class.java,
                Double::class.javaPrimitiveType ?: Double::class.java,
                Double::class.javaObjectType,
                Long::class.javaPrimitiveType ?: Long::class.java,
                Long::class.javaObjectType,
                BigInteger::class.java,
                Enum::class.java
            )

            private val addressToStringConverter: Converter<Address, String> =
                object : Converter<Address, String> {
                    override fun convert(from: Address): String {
                        return from.toBech32(hrpPrefix)
                    }
                }

            private val pubKeyToStringConverter: Converter<PubKey, String> =
                object : Converter<PubKey, String> {
                    override fun convert(from: PubKey): String {
                        return from.toBech32(hrpPrefix)
                    }
                }

            private val defaultToStringConverter: Converter<Any, String> = object : Converter<Any, String> {
                override fun convert(from: Any): String {
                    return from.toString()
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun get(type: Type): Converter<Any, String>? {
                return when {
                    type == Address::class.java -> addressToStringConverter
                    type == PubKey::class.java -> pubKeyToStringConverter
                    types.contains(type) -> defaultToStringConverter
                    else -> null
                } as Converter<Any, String>?
            }

            override fun iterator() =
                object : Iterator<Pair<Type, Converter<Any, String>>> {
                    val iterator = types.iterator()

                    override fun hasNext() = iterator.hasNext()
                    override fun next() = iterator.next().let { it to requireNotNull(get(it)) }
                }
        }
    }

}

