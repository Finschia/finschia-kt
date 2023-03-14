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

/**
 * Account interface represents the account information such as address, account number, sequence number
 */
interface Account {

    /**
     * the address of the account
     */
    val address: Address

    /**
     * the account number of the account
     */
    val number: Long

    /**
     * the sequence of the account
     */
    val sequence: Long
}
