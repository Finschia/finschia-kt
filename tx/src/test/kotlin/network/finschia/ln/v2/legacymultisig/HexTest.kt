package network.finschia.ln.v2.legacymultisig

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HexTest {
    fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    @Test
    fun fromHex() {
        // simple
        Assertions.assertArrayEquals(byteArrayOfInts(), fromHex(""))
        Assertions.assertArrayEquals(byteArrayOfInts(0x00), fromHex("00"))
        Assertions.assertArrayEquals(byteArrayOfInts(0x01), fromHex("01"))
        Assertions.assertArrayEquals(byteArrayOfInts(0x10), fromHex("10"))
        Assertions.assertArrayEquals(byteArrayOfInts(0x11), fromHex("11"))
        Assertions.assertArrayEquals(byteArrayOfInts(0x11, 0x22, 0x33), fromHex("112233"))
        Assertions.assertArrayEquals(byteArrayOfInts(0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef), fromHex("0123456789abcdef"))

        // capital letters
        Assertions.assertArrayEquals(byteArrayOfInts(0xaa), fromHex("AA"))
        Assertions.assertArrayEquals(byteArrayOfInts(0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff), fromHex("aAbBcCdDeEfF"))

        // error
        assertThrows<IllegalStateException> { fromHex("a") }
        assertThrows<IllegalStateException> { fromHex("aaa") }
        assertThrows<IllegalStateException> { fromHex("a!") }
        assertThrows<IllegalStateException> { fromHex("a ") }
        assertThrows<IllegalStateException> { fromHex("aa ") }
        assertThrows<IllegalStateException> { fromHex(" aa") }
        assertThrows<IllegalStateException> { fromHex("a a") }
        assertThrows<IllegalStateException> { fromHex("gg") }
    }

    @Test
    fun toHex() {
        Assertions.assertEquals("", toHex(byteArrayOfInts()))
        Assertions.assertEquals("00", toHex(byteArrayOfInts(0x00)))
        Assertions.assertEquals("01", toHex(byteArrayOfInts(0x01)))
        Assertions.assertEquals("10", toHex(byteArrayOfInts(0x10)))
        Assertions.assertEquals("11", toHex(byteArrayOfInts(0x11)))
        Assertions.assertEquals("112233", toHex(byteArrayOfInts(0x11, 0x22, 0x33)))
        Assertions.assertEquals("0123456789abcdef", toHex(byteArrayOfInts(0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef)))
    }
}
