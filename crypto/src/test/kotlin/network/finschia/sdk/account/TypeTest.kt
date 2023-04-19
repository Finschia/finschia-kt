package network.finschia.sdk.account

import com.google.common.truth.Truth.assertThat
import network.finschia.sdk.account.Type.ACCOUNT
import network.finschia.sdk.account.Type.Companion.toHrpPrefix
import network.finschia.sdk.account.Type.VALIDATOR_CONSENSUS
import network.finschia.sdk.account.Type.VALIDATOR_OPERATOR
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class TypeTest {

    @Test
    fun testInvoke() {
        assertThat(Type("")).isEqualTo(ACCOUNT)
        assertThat(Type("valcons")).isEqualTo(VALIDATOR_CONSENSUS)
        assertThat(Type("valoper")).isEqualTo(VALIDATOR_OPERATOR)
    }

    @Test
    fun testInvokeUnknownHrpType() {
        assertThrows(IllegalArgumentException::class.java) {
            Type("account")
        }
    }

    @Test
    fun testToHrpPrefix() {
        assertThat(ACCOUNT.toHrpPrefix()).isEqualTo("")
        assertThat(VALIDATOR_CONSENSUS.toHrpPrefix()).isEqualTo("valcons")
        assertThat(VALIDATOR_OPERATOR.toHrpPrefix()).isEqualTo("valoper")
    }

}
