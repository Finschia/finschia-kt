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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import network.link.ln.v2.crypto.LinkKeys;

public class LinkKeysTest {

    @Test
    public void testCreatePrivateKey() throws Exception {
        final byte[] privateKey = LinkKeys.createPrivateKey();

        assertNotNull(privateKey);
        assertEquals(32, privateKey.length);
    }

    @Test
    public void testGetCompressedPublicKey() throws Exception {
        final String privateKey = "M5AzgP8Ztnk5jGoNV9gZx7qUknHUV2BOjcoYGjkS7AI=";
        final byte[] publicKey = LinkKeys.getPublicKey(Base64.getDecoder().decode(privateKey), true);

        assertEquals("Am7B/BZW84tBZa0z96IIEXltiimV2K8np7J0uWqBS+Kf",
                     Base64.getEncoder().encodeToString(publicKey));
    }

    @Test
    public void testGetUncompressedPublicKey() throws Exception {
        final String privateKey = "M5AzgP8Ztnk5jGoNV9gZx7qUknHUV2BOjcoYGjkS7AI=";
        final byte[] publicKey = LinkKeys.getPublicKey(Base64.getDecoder().decode(privateKey), false);

        assertEquals("BG7B/BZW84tBZa0z96IIEXltiimV2K8np7J0uWqBS+KfhIyHaVGUTcZUCaLPqwXR1aFPcDYaAfMmzq+w0kDyNkY=",
                     Base64.getEncoder().encodeToString(publicKey));
    }

}
