package network.link.ln.v2.legacymultisig

import org.junit.jupiter.api.Test
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows

/**
 * Implement with reference to
 * https://github.com/cosmos/cosmjs/blob/79396bfaa49831127ccbbbfdbb1185df14230c63/packages/amino/src/signdoc.spec.ts
 */

class SignDocTest {
    @Test
    fun jsonElementSortForNonObjectsUnchanged() {
        Assertions.assertEquals("true", Json.encodeToString(Json.parseToJsonElement("true").sort()))
        Assertions.assertEquals("false", Json.encodeToString(Json.parseToJsonElement("false").sort()))
        Assertions.assertEquals("\"aabbccdd\"", Json.encodeToString(Json.parseToJsonElement("\"aabbccdd\"").sort()))
        Assertions.assertEquals("75", Json.encodeToString(Json.parseToJsonElement("75").sort()))
        Assertions.assertEquals("null", Json.encodeToString(Json.parseToJsonElement("null").sort()))
        Assertions.assertEquals("[5,6,7,1]", Json.encodeToString(Json.parseToJsonElement("[5,6,7,1]").sort()))
        Assertions.assertEquals("[5,[\"a\",\"b\"],true,null,1]", Json.encodeToString(Json.parseToJsonElement("[5,[\"a\",\"b\"],true,null,1]").sort()))
    }

    @Test
    fun jsonElementSortForObjectKeys() {
        // already sorted
        Assertions.assertEquals("{}", Json.encodeToString(Json.parseToJsonElement("{}").sort()))
        Assertions.assertEquals("{\"a\":3}", Json.encodeToString(Json.parseToJsonElement("{\"a\":3}").sort()))
        Assertions.assertEquals("{\"a\":3,\"b\":2,\"c\":1}", Json.encodeToString(Json.parseToJsonElement("{\"a\":3,\"b\":2,\"c\":1}").sort()))

        // not yet sorted
        Assertions.assertEquals("{\"a\":3,\"b\":2,\"c\":1}", Json.encodeToString(Json.parseToJsonElement("{\"b\":2,\"a\":3,\"c\":1}").sort()))
        Assertions.assertEquals("{\"a\":true,\"aa\":true,\"aaa\":true}", Json.encodeToString(Json.parseToJsonElement("{\"aaa\":true,\"aa\":true,\"a\":true}").sort()))
    }

    @Test
    fun jsonElementSortForNestedObjects() {
        // already sorted
        Assertions.assertEquals(
            "{\"x\":{\"y\":{\"z\":null}}}",
            Json.encodeToString(Json.parseToJsonElement("{\"x\":{\"y\":{\"z\":null}}}").sort())
        )

        // not yet sorted
        Assertions.assertEquals(
            "{\"a\":true,\"b\":{\"x\":true,\"y\":true,\"z\":true},\"c\":true}",
            Json.encodeToString(Json.parseToJsonElement("{\"b\":{\"z\":true,\"x\":true,\"y\":true},\"a\":true,\"c\":true}").sort())
        )
    }

    @Test
    fun jsonElementSortForObjectsInArrays() {
        // already sorted
        Assertions.assertEquals(
            "[1,2,{\"x\":{\"y\":{\"z\":null}}},4]",
            Json.encodeToString(Json.parseToJsonElement("[1,2,{\"x\":{\"y\":{\"z\":null}}},4]").sort())
        )

        // not yet sorted
        Assertions.assertEquals(
            "[1,2,{\"a\":true,\"b\":{\"x\":true,\"y\":true,\"z\":true},\"c\":true},4]",
            Json.encodeToString(Json.parseToJsonElement("[1,2,{\"b\":{\"z\":true,\"x\":true,\"y\":true},\"a\":true,\"c\":true},4]").sort())
        )
    }

    @Test
    fun jsonElementRemoveNullForNonObjects() {
        // non null
        Assertions.assertEquals("true", Json.encodeToString(Json.parseToJsonElement("true").removeNull()))
        Assertions.assertEquals("false", Json.encodeToString(Json.parseToJsonElement("false").removeNull()))
        Assertions.assertEquals("\"aabbccdd\"", Json.encodeToString(Json.parseToJsonElement("\"aabbccdd\"").removeNull()))
        Assertions.assertEquals("75", Json.encodeToString(Json.parseToJsonElement("75").removeNull()))
        Assertions.assertEquals("[5,6,7,1]", Json.encodeToString(Json.parseToJsonElement("[5,6,7,1]").removeNull()))
        Assertions.assertEquals("[5,[\"a\",\"b\"],true,1]", Json.encodeToString(Json.parseToJsonElement("[5,[\"a\",\"b\"],true,null,1]").removeNull()))

        // null
        val error = assertThrows<IllegalStateException> { Json.parseToJsonElement("null").removeNull() }
        Assertions.assertEquals("unexpected token", error.message)
    }

    @Test
    fun jsonElementRemoveNullForObject() {
        // already removed null
        Assertions.assertEquals("{}", Json.encodeToString(Json.parseToJsonElement("{}").removeNull()))
        Assertions.assertEquals("{\"a\":3}", Json.encodeToString(Json.parseToJsonElement("{\"a\":3}").removeNull()))
        Assertions.assertEquals("{\"a\":3,\"b\":2,\"c\":1}", Json.encodeToString(Json.parseToJsonElement("{\"a\":3,\"b\":2,\"c\":1}").removeNull()))

        // not yet removed null
        Assertions.assertEquals("{\"a\":3,\"c\":1}", Json.encodeToString(Json.parseToJsonElement("{\"a\":3,\"b\":null,\"c\":1}").removeNull()))
        Assertions.assertEquals("{\"aa\":true,\"aaa\":true}", Json.encodeToString(Json.parseToJsonElement("{\"a\":null,\"aa\":true,\"aaa\":true}").removeNull()))
    }

    @Test
    fun jsonElementRemoveNullForNestedObjects() {
        // already removed null
        Assertions.assertEquals(
            "{\"x\":{\"y\":{\"z\":true}}}",
            Json.encodeToString(Json.parseToJsonElement("{\"x\":{\"y\":{\"z\":true}}}").removeNull())
        )

        // not yet removed null
        Assertions.assertEquals(
            "{\"a\":true,\"b\":{\"x\":true,\"z\":true},\"c\":true}",
            Json.encodeToString(Json.parseToJsonElement("{\"a\":true,\"b\":{\"x\":true,\"y\":null,\"z\":true},\"c\":true}").removeNull())
        )
    }

    @Test
    fun jsonElementRemoveNullForObjectsInArrays() {
        // already removed null
        Assertions.assertEquals(
            "[1,2,{\"x\":{\"y\":{\"z\":true}}},4]",
            Json.encodeToString(Json.parseToJsonElement("[1,2,{\"x\":{\"y\":{\"z\":true}}},4]").removeNull())
        )

        // not yet removed null
        Assertions.assertEquals(
            "[1,2,{\"a\":true,\"b\":{\"x\":true,\"z\":true},\"c\":true},4]",
            Json.encodeToString(Json.parseToJsonElement("[1,2,{\"a\":true,\"b\":{\"x\":true,\"y\":null,\"z\":true},\"c\":true},4]").removeNull())
        )
    }
}
