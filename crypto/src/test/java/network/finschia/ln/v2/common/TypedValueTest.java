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

package network.finschia.ln.v2.common;

import static com.google.common.truth.Truth.assertThat;

import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class TypedValueTest {
    private final SecureRandom random = new SecureRandom();

    @Test
    void testPrimitiveTypeEquals() {

        final String type = "int type";
        final int value = random.nextInt();
        final TypedValue<Integer> typedInt1 = new TypedValue<>(type, value);
        final TypedValue<Integer> typedInt2 = new TypedValue<>(type, value);

        assertThat(typedInt1).isEqualTo(typedInt2);
        assertThat(typedInt1.hashCode()).isEqualTo(typedInt2.hashCode());
    }

    @Test
    void testStringTypeEquals() {

        final String type = "str type";
        final String value = "str value";
        final TypedValue<String> typedString1 = new TypedValue<>(type, value);
        final TypedValue<String> typedString2 = new TypedValue<>(type, value);

        assertThat(typedString1).isEqualTo(typedString2);
        assertThat(typedString1.hashCode()).isEqualTo(typedString2.hashCode());
    }

    @Test
    void testArrayTypeEquals() {

        final String type = "str array type";
        final String[] value = { "str1", "str2", "str3" };
        final TypedValue<String[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<String[]> typedValue2 = new TypedValue<>(type, Arrays.copyOf(value, value.length));

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    void testByteArrayTypeEquals() {

        final String type = "byte array type";
        final byte[] value = new byte[random.nextInt(100)];
        random.nextBytes(value);
        final TypedValue<byte[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<byte[]> typedValue2 = new TypedValue<>(type, Arrays.copyOf(value, value.length));

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    void testShortArrayTypeEquals() {

        final String type = "short array type";
        final short[] value = { 100, 101, 102 };
        final TypedValue<short[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<short[]> typedValue2 = new TypedValue<>(type, Arrays.copyOf(value, value.length));

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    void testIntArrayTypeEquals() {

        final String type = "int array type";
        final int[] value = { 100, 101, 102 };
        final TypedValue<int[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<int[]> typedValue2 = new TypedValue<>(type, Arrays.copyOf(value, value.length));

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    void testLongArrayTypeEquals() {

        final String type = "long array type";
        final long[] value = { 100L, 101L, 102L };
        final TypedValue<long[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<long[]> typedValue2 = new TypedValue<>(type, Arrays.copyOf(value, value.length));

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    void testFloatArrayTypeEquals() {

        final String type = "float array type";
        final float[] value = { 100.0f, 101.0f, 102.0f };
        final TypedValue<float[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<float[]> typedValue2 = new TypedValue<>(type, Arrays.copyOf(value, value.length));

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    void testDoubleArrayTypeEquals() {

        final String type = "double array type";
        final double[] value = { 100.0, 101.0, 102.0 };
        final TypedValue<double[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<double[]> typedValue2 = new TypedValue<>(type, Arrays.copyOf(value, value.length));

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    void testCharArrayTypeEquals() {

        final String type = "char array type";
        final char[] value = { 'a', 'b', 'c', 'd', 'e' };
        final TypedValue<char[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<char[]> typedValue2 = new TypedValue<>(type, Arrays.copyOf(value, value.length));

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testNullValueEquals() {

        final String type = "type";
        final TypedValue<Object> typedValue1 = new TypedValue<>(type, null);
        final TypedValue<Object> typedValue2 = new TypedValue<>(type, null);

        assertThat(typedValue1).isEqualTo(typedValue2);
        assertThat(typedValue1.hashCode()).isEqualTo(typedValue2.hashCode());
    }

    @Test
    void testEqualsDifferentObject() {

        final String type = "byte array type";
        final byte[] value = new byte[random.nextInt(100)];
        random.nextBytes(value);
        final TypedValue<byte[]> typedValue = new TypedValue<>(type, value);

        assertThat(typedValue).isNotEqualTo(value);
    }

    @Test
    void testEqualsDifferentTypeString() {

        final String type = "byte array type";
        final String type1 = "byte array type1";
        final byte[] value = new byte[random.nextInt(100)];
        random.nextBytes(value);
        final TypedValue<byte[]> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<byte[]> typedValue2 = new TypedValue<>(type1, value);

        assertThat(typedValue1).isNotEqualTo(typedValue2);
    }

    @Test
    void testEqualsNullValueVsNonNullValue() {

        final String type = "byte array type";
        final byte[] value = new byte[random.nextInt(100)];
        random.nextBytes(value);
        @SuppressWarnings("ConstantConditions")
        final TypedValue<byte[]> typedValue2 = new TypedValue<>(type, null);
        final TypedValue<byte[]> typedValue1 = new TypedValue<>(type, value);

        assertThat(typedValue1).isNotEqualTo(typedValue2);
    }

    @Test
    void testEqualsNonNullValueVsNullValue() {

        final String type = "byte array type";
        final byte[] value = new byte[random.nextInt(100)];
        random.nextBytes(value);
        final TypedValue<byte[]> typedValue1 = new TypedValue<>(type, value);

        @SuppressWarnings("ConstantConditions")
        final TypedValue<byte[]> typedValue2 = new TypedValue<>(type, null);

        assertThat(typedValue1).isNotEqualTo(typedValue2);
    }

    @Test
    void testEqualsDifferentValue() {

        final String type = "str type";
        final String value1 = "str value1";
        final String value2 = "str value2";
        final TypedValue<String> typedValue1 = new TypedValue<>(type, value1);
        final TypedValue<String> typedValue2 = new TypedValue<>(type, value2);

        assertThat(typedValue1).isNotEqualTo(typedValue2);
    }

    @Test
    void testEqualsDifferentValueType() {

        final String type = "str type";
        final String value = "str value";
        final TypedValue<String> typedValue1 = new TypedValue<>(type, value);
        final TypedValue<char[]> typedValue2 = new TypedValue<>(type, value.toCharArray());

        assertThat(typedValue1).isNotEqualTo(typedValue2);
    }
}
