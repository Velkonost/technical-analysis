package velkonost.technical.analysis.indicator.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.volume.MFIIndicator
import java.math.BigDecimal

class MFIIndicatorCornerCasesTest {

    @Test
    fun `Insufficient window returns 50s`() {
        val high = DataColumn.createValueColumn("high", listOf(BigDecimal("10"), BigDecimal("11"), BigDecimal("12")))
        val low = DataColumn.createValueColumn("low", listOf(BigDecimal("9"), BigDecimal("10"), BigDecimal("11")))
        val close = DataColumn.createValueColumn("close", listOf(BigDecimal("9.5"), BigDecimal("10.5"), BigDecimal("11.5")))
        val volume = DataColumn.createValueColumn("volume", listOf(BigDecimal("100"), BigDecimal("110"), BigDecimal("120")))

        val indicator = MFIIndicator(high, low, close, volume, window = 14)
        val result = indicator.calculate().toList()
        // Все значения до заполнения окна 14 — 50
        assertEquals(listOf(BigDecimal("50"), BigDecimal("50"), BigDecimal("50")), result)
    }
}


