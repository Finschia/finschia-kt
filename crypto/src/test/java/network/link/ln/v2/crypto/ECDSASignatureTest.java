/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package network.link.ln.v2.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static network.link.ln.v2.crypto.ECDSASignature.publicFromPoint;
import static network.link.ln.v2.crypto.ECDSASignature.publicKeyFromPrivate;
import static network.link.ln.v2.crypto.ECDSASignature.publicPointFromPrivate;

import java.math.BigInteger;
import java.util.Base64;

import org.bouncycastle.math.ec.ECPoint;
import org.junit.jupiter.api.Test;

import network.link.ln.v2.crypto.ECDSASignature;

public class ECDSASignatureTest {
    public static final String PRIVATE_KEY_STRING =
            "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";
    static final String PUBLIC_KEY_STRING =
            "506bc1dc099358e5137292f4efdd57e400f29ba5132aa5d12b18dac1c1f6aab"
            + "a645c0b7b58158babbfa6c6cd5a48aa7340a8749176b120e8516216787a13dc76";
    static final BigInteger PRIVATE_KEY = new BigInteger(PRIVATE_KEY_STRING, 16);
    static final BigInteger PUBLIC_KEY = new BigInteger(PUBLIC_KEY_STRING, 16);
    private static final byte[] TEST_MESSAGE = "A test message".getBytes();

    @Test
    public void testGenerateSignature() {
        final ECDSASignature signature = new ECDSASignature(PRIVATE_KEY.toByteArray());
        final BigInteger[] sig = signature.generateSignature(TEST_MESSAGE);
        final byte[] serializedSig = signature.recoverableSerialize(sig, TEST_MESSAGE);
        assertEquals("vv720Z3C2i1eOU0tK8aLXNHE+GmhmystbbzM6EcC7dZ9g5EQEblDkvz3Abbd7knk9Q086Sswba5TgAfkIo+poQA="
                , Base64.getEncoder().encodeToString(serializedSig));
    }

    @Test
    public void testPublicKeyFromPrivateKey() {
        assertEquals(PUBLIC_KEY, publicKeyFromPrivate(PRIVATE_KEY));
    }

    @Test
    public void testPublicKeyFromPrivatePoint() {
        final ECPoint point = publicPointFromPrivate(PRIVATE_KEY);
        assertEquals(PUBLIC_KEY, publicFromPoint(point.getEncoded(false)));
    }
}
