package velkonost.technical.analysis.indicator.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.volatility.keltnerChannel.KeltnerChannelPband
import java.math.BigDecimal

class KeltnerChannelPbandCornerCasesTest {

    @Test
    fun `Zero width channel returns zeros`() {
        val high = DataColumn.createValueColumn("high", listOf(BigDecimal("10"), BigDecimal("10")))
        val low = DataColumn.createValueColumn("low", listOf(BigDecimal("10"), BigDecimal("10")))
        val close = DataColumn.createValueColumn("close", listOf(BigDecimal("10"), BigDecimal("10")))

        val indicator = KeltnerChannelPband(high, low, close)
        val result = indicator.calculate().toList()
        assertEquals(listOf(BigDecimal.ZERO, BigDecimal.ZERO), result)
    }
}


