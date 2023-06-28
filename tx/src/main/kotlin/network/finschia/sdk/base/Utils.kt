/**
 * Copyright 2023 Finschia Foundation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package network.finschia.sdk.base

import java.math.BigDecimal

/**
 * Implement with reference to
 * https://github.com/Finschia/finschia-js/blob/v0.8.0/packages/finschia/src/utils.ts
 */

fun protoDecimalToJson(decimal: String): String {
    val parsed = BigDecimal(decimal).divide(BigDecimal("1000000000000000000"))  // divide 10^18
    return parsed.setScale(18).toPlainString()
}

fun jsonDecimalToProto(decimal: String): String {
    val parsed = BigDecimal(decimal).multiply(BigDecimal("1000000000000000000"))
    return parsed.setScale(0).toPlainString()
}
