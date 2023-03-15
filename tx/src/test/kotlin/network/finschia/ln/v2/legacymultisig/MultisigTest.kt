package network.finschia.ln.v2.legacymultisig

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/41884540d2927538fb45adcb36badd79bdfd3e55/packages/amino/src/multisig.spec.ts
 */

class MultisigTest {
    @Test
    fun compareArraysForEqualArrays() {
        Assertions.assertEquals(0, compareArrays(byteArrayOf(), byteArrayOf()))
        Assertions.assertEquals(0, compareArrays(byteArrayOf(1), byteArrayOf(1)))
        Assertions.assertEquals(0, compareArrays(byteArrayOf(3, 2, 1), byteArrayOf(3, 2, 1)))
    }

    @Test
    fun compareArraysForGreaterLeftThanRight() {
        Assertions.assertEquals(1, compareArrays(byteArrayOf(5, 5, 5), byteArrayOf(5, 5, 4)))
        Assertions.assertEquals(1, compareArrays(byteArrayOf(5, 5, 5), byteArrayOf(5, 4, 5)))
        Assertions.assertEquals(1, compareArrays(byteArrayOf(5, 5, 5), byteArrayOf(4, 5, 5)))
        Assertions.assertEquals(1, compareArrays(byteArrayOf(5, 5, 5), byteArrayOf(5, 5)))
        Assertions.assertEquals(1, compareArrays(byteArrayOf(5, 5, 5), byteArrayOf(5)))
        Assertions.assertEquals(1, compareArrays(byteArrayOf(5, 5, 5), byteArrayOf()))

        // left or right precedence
        Assertions.assertEquals(1, compareArrays(byteArrayOf(5, 5, 4), byteArrayOf(4, 5, 5)))

        // magnitude is more important than length
        Assertions.assertEquals(1, compareArrays(byteArrayOf(6), byteArrayOf(5, 5)))
    }

    @Test
    fun compareArraysForLessLeftThanRight() {
        Assertions.assertEquals(-1, compareArrays(byteArrayOf(5, 5, 4), byteArrayOf(5, 5, 5)))
        Assertions.assertEquals(-1, compareArrays(byteArrayOf(5, 4, 5), byteArrayOf(5, 5, 5)))
        Assertions.assertEquals(-1, compareArrays(byteArrayOf(4, 5, 5), byteArrayOf(5, 5, 5)))
        Assertions.assertEquals(-1, compareArrays(byteArrayOf(5, 5), byteArrayOf(5, 5, 5)))
        Assertions.assertEquals(-1, compareArrays(byteArrayOf(5), byteArrayOf(5, 5, 5)))
        Assertions.assertEquals(-1, compareArrays(byteArrayOf(), byteArrayOf(5, 5, 5)))

        // left or right precedence
        Assertions.assertEquals(-1, compareArrays(byteArrayOf(4, 5, 5), byteArrayOf(5, 5, 4)))

        // magnitude is more important than length
        Assertions.assertEquals(-1, compareArrays(byteArrayOf(5, 5), byteArrayOf(6)))
    }

    @Test
    fun compareArraysForSortEnable() {
        val value = listOf(
            byteArrayOf(2),
            byteArrayOf(1),
            byteArrayOf(2, 5),
            byteArrayOf(3),
            byteArrayOf(),
        ).sortedWith { a, b ->
            compareArrays(a, b)
        }
        val expected = listOf(
            byteArrayOf(),
            byteArrayOf(1),
            byteArrayOf(2),
            byteArrayOf(2, 5),
            byteArrayOf(3),
        )
        Assertions.assertEquals(expected.size, value.size)
        expected.forEachIndexed { i, e ->
            Assertions.assertArrayEquals(e, value[i])
        }
    }

    @Test
    fun createMultisigThresholdPubkeyForSorting() {
        Assertions.assertEquals(testgroup1, createMultisigThresholdPubkey(listOf(test1, test2, test3), 2, txSigLimit = 7))
        Assertions.assertEquals(testgroup2, createMultisigThresholdPubkey(listOf(test1, test2, test3), 1, txSigLimit = 7))
        Assertions.assertEquals(testgroup3, createMultisigThresholdPubkey(listOf(test3, test1), 2, txSigLimit = 7))

        Assertions.assertEquals(testgroup1, createMultisigThresholdPubkey(listOf(test1, test2, test3), 2, false, txSigLimit = 7))
        Assertions.assertEquals(testgroup2, createMultisigThresholdPubkey(listOf(test1, test2, test3), 1, false, txSigLimit = 7))
        Assertions.assertEquals(testgroup3, createMultisigThresholdPubkey(listOf(test3, test1), 2, false, txSigLimit = 7))
    }

    @Test
    fun createMultisigThresholdPubkeyForNoSort() {
        Assertions.assertEquals(testgroup4, createMultisigThresholdPubkey(listOf(test3, test1), 2, true, txSigLimit = 7))
    }

    @Test
    fun createMultisigThresholdPubkeyForLargerPubKeyNum() {
        val error1 = assertThrows<IllegalStateException> {
            Assertions.assertEquals(testgroup1, createMultisigThresholdPubkey(listOf(test1, test2, test3), 2, txSigLimit = 2))
        }
        Assertions.assertEquals(
            "Tx signature limit m = 2 exceeds number of public keys n = 3", error1.message
        )

        val error2 = assertThrows<IllegalStateException> {
            Assertions.assertEquals(testgroup1, createMultisigThresholdPubkey(listOf(test1, test2, test3), 2, txSigLimit = 0))
        }
        Assertions.assertEquals(
            "Tx signature limit m = 0 exceeds number of public keys n = 3", error2.message
        )
    }

    @Test
    fun createMultisigThresholdPubkeyForLargerThreshold() {
        val error1 = assertThrows<IllegalStateException> {
            Assertions.assertEquals(testgroup1, createMultisigThresholdPubkey(listOf(test1, test2, test3), 4, txSigLimit = 7))
        }
        Assertions.assertEquals(
            "Threshold m = 4 exceeds number of keys n = 3", error1.message
        )

        val error2 = assertThrows<IllegalStateException> {
            Assertions.assertEquals(testgroup1, createMultisigThresholdPubkey(listOf(test1, test2, test3), 75, txSigLimit = 7))
        }
        Assertions.assertEquals(
            "Threshold m = 75 exceeds number of keys n = 3", error2.message
        )
    }
}
