package network.finschia.sdk.legacymultisig

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/tree/3348c2854aea73f538454843f2e93167ff15ca85/packages/encoding/src
 */

fun toHex(data: ByteArray): String {
    return data.joinToString(separator = "") {
        "%02x".format(it)
    }
}

fun fromHex(hexString: String): ByteArray {
    if (hexString.length % 2 != 0) {
        error("hex string length must be a multiple of 2");
    }

    val array = ByteArray(hexString.length / 2)
    for (index in 0 until array.count()) {
        val j = index * 2
        val hexByteAsString = hexString.substring(j,  j + 2)
        if (!("^[0-9a-fA-f]{2}$".toRegex().matches(hexByteAsString))) {
            error("hex string contains invalid characters")
        }
        array[index] = hexByteAsString.toInt(16).toByte()
    }
    return array
}
