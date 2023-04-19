package network.finschia.sdk.base

import network.finschia.sdk.account.HDWallet

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TxTest {
    val mnemonic = "mind flame tobacco sense move hammer drift crime ring globe art gaze cinnamon helmet cruise special produce notable negative wait path scrap recall have"
    val hdWallet = HDWallet.loadFromMnemonic(mnemonic)

    @Test fun bankSend() {
        // alice sends 10000tcony to bob
        val aliceKey = hdWallet.getKeyWallet(0)
        val bobKey = hdWallet.getKeyWallet(1)

        val fromAddress = aliceKey.address.toBech32("link")
        val toAddress = bobKey.address.toBech32("link")
        val amount = cosmos.base.v1beta1.coin {
            amount = "10000"
            denom = "tcony"
        }

        val msgSend = cosmos.bank.v1beta1.msgSend {
            this.fromAddress = fromAddress
            this.toAddress = toAddress
            this.amount += amount
        }

        val msgAny = com.google.protobuf.any {
            typeUrl = "/cosmos.bank.v1beta1.MsgSend"
            value = msgSend.toByteString()
        }

        val tx = Tx.newBuilder()
            .setChainId("test")
            .addMessage(msgAny)
            .addSigner(aliceKey, 1)
            .setFee(cosmos.tx.v1beta1.fee { gasLimit = 10000 })
            .setMemo("")
            .build()

        tx.sign(aliceKey, 0)

        // simd tx bank send link146asaycmtydq45kxc8evntqfgepagygelel00h link1twsfmuj28ndph54k4nw8crwu8h9c8mh3rtx705 10000tcony --gas 10000 --account-number 0
        val simdHash = "AE3A1EAD915AC7286E72BA91C0C8F7BCE05EE05552536A792574B499984EDBF0"
        assertEquals(simdHash, tx.hash(),
                     "the result hash must be identical to simd's")

        // sign again
        tx.sign(aliceKey, 0)
        assertEquals(simdHash, tx.hash(),
                     "sign operation must be idempotent")

        // bob signs
        tx.sign(bobKey, 1)
        assertEquals(simdHash, tx.hash(),
                     "sign by the irrelevent must have no effect")
    }
}
