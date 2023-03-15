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

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import network.finschia.ln.v2.account.Type

/**
 * Predefined basic JSON serializing and deserializing rules for Jackson-databind
 */
internal class CommonMapperModule(private val toStringConverters: ToStringConverters) : SimpleModule() {

    /**
     * Creates a new CommonMapperModule
     *
     * @param hrpPrefix prefix of the bech32 HRP
     */
    @JvmOverloads
    constructor(hrpPrefix: String = Type.DEFAULT_BECH32_HRP_PREFIX) : this(ToStringConverters(hrpPrefix))

    override fun setupModule(context: SetupContext) {
        toStringConverters.forEach {
            addSerializer(it.first as Class<*>, object : JsonSerializer<Any>() {
                override fun serialize(value: Any, gen: JsonGenerator, serializers: SerializerProvider) {
                    gen.writeString(it.second.convert(value))
                }
            })
        }
        setNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        super.setupModule(context)

        KotlinModule().setupModule(context)
    }
}
