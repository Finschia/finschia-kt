package network.link.ln.v2.legacymultisig

import cosmos.crypto.multisig.v1beta1.compactBitArray
import com.google.protobuf.ByteString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/41884540d2927538fb45adcb36badd79bdfd3e55/packages/stargate/src/multisignature.spec.ts
 */

class MultisignatureTest {
    fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    @Test
    fun makeCompactBitArrayForZeroBitsOfDifferentLengths() {
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts())
            extraBitsStored = 0
        }, makeCompactBitArray(listOf()))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000))
            extraBitsStored = 1
        }, makeCompactBitArray(listOf(false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000))
            extraBitsStored = 3
        }, makeCompactBitArray(listOf(false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000))
            extraBitsStored = 4
        }, makeCompactBitArray(listOf(false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000))
            extraBitsStored = 5
        }, makeCompactBitArray(listOf(false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000))
            extraBitsStored = 6
        }, makeCompactBitArray(listOf(false, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000))
            extraBitsStored = 7
        }, makeCompactBitArray(listOf(false, false, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000))
            extraBitsStored = 0
        }, makeCompactBitArray(listOf(false, false, false, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000, 0b00000000))
            extraBitsStored = 1
        }, makeCompactBitArray(listOf(false, false, false, false, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, false, false, false, false, false, false, false, false)))
    }

    @Test
    fun makeCompactBitArrayForOneBitsOfDifferentLengths() {
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts())
            extraBitsStored = 0
        }, makeCompactBitArray(listOf()))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b10000000))
            extraBitsStored = 1
        }, makeCompactBitArray(listOf(true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(true, true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11100000))
            extraBitsStored = 3
        }, makeCompactBitArray(listOf(true, true, true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11110000))
            extraBitsStored = 4
        }, makeCompactBitArray(listOf(true, true, true, true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11111000))
            extraBitsStored = 5
        }, makeCompactBitArray(listOf(true, true, true, true, true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11111100))
            extraBitsStored = 6
        }, makeCompactBitArray(listOf(true, true, true, true, true, true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11111110))
            extraBitsStored = 7
        }, makeCompactBitArray(listOf(true, true, true, true, true, true, true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11111111))
            extraBitsStored = 0
        }, makeCompactBitArray(listOf(true, true, true, true, true, true, true, true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11111111, 0b10000000))
            extraBitsStored = 1
        }, makeCompactBitArray(listOf(true, true, true, true, true, true, true, true, true)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b11111111, 0b11000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(true, true, true, true, true, true, true, true, true, true)))
    }

    @Test
    fun makeCompactBitArrayForOneBitInDifferentPlaces() {
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b10000000, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(true, false, false, false, false, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b01000000, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, true, false, false, false, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00100000, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, true, false, false, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00010000, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, false, true, false, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00001000, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, false, false, true, false, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000100, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, false, false, false, true, false, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000010, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, false, false, false, false, true, false, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000001, 0b00000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, false, false, false, false, false, true, false, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000, 0b10000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, false, false, false, false, false, false, true, false)))
        Assertions.assertEquals(compactBitArray{
            elems = ByteString.copyFrom(byteArrayOfInts(0b00000000, 0b01000000))
            extraBitsStored = 2
        }, makeCompactBitArray(listOf(false, false, false, false, false, false, false, false, false, true)))
    }
}
