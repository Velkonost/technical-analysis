package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.CandleWick
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class CandleWickTest {
    @Test
    fun `Standard Short Signal`() {
        // Corrected test data with large wick size
        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("101"),
            BigDecimal("100")
        )
        val openValues = listOf(
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("103"),
            BigDecimal("101")
        )
        val highValues = listOf(
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("103"),
            BigDecimal("125"),  // Adjusted
            BigDecimal("102")
        )
        val lowValues = listOf(
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("99")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `Standard Long Signal`() {
        // Corrected test data with large wick size
        val closeValues = listOf(
            BigDecimal("103"),
            BigDecimal("102"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("103")
        )
        val openValues = listOf(
            BigDecimal("104"),
            BigDecimal("103"),
            BigDecimal("102"),
            BigDecimal("100"),
            BigDecimal("102")
        )
        val highValues = listOf(
            BigDecimal("105"),
            BigDecimal("104"),
            BigDecimal("103"),
            BigDecimal("105"),
            BigDecimal("104")
        )
        val lowValues = listOf(
            BigDecimal("102"),
            BigDecimal("101"),
            BigDecimal("100"),
            BigDecimal("80"),  // Adjusted
            BigDecimal("101")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `No Signal - Conditions Not Met`() {
        // Conditions for neither Long nor Short are met
        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("100")
        )
        val openValues = listOf(
            BigDecimal("101"),
            BigDecimal("100"),
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("101")
        )
        val highValues = listOf(
            BigDecimal("102"),
            BigDecimal("101"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("103")
        )
        val lowValues = listOf(
            BigDecimal("99"),
            BigDecimal("98"),
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("98")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Insufficient Data`() {
        // Less than 5 data points
        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102")
        )
        val openValues = listOf(
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("101")
        )
        val highValues = listOf(
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("103")
        )
        val lowValues = listOf(
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("100")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 2
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Boundary Conditions - Index Out of Bounds`() {
        // currentIndex is out of bounds
        val closeValues = listOf<BigDecimal>()
        val openValues = listOf<BigDecimal>()
        val highValues = listOf<BigDecimal>()
        val lowValues = listOf<BigDecimal>()

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Wick Size Not Large Enough`() {
        // The wick is not large enough compared to the body
        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("101"),  // Index 3 (Bearish candle)
            BigDecimal("100")
        )
        val openValues = listOf(
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("101")
        )
        val highValues = listOf(
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("103"),
            BigDecimal("103"),  // Index 3 (Wick not large)
            BigDecimal("102")
        )
        val lowValues = listOf(
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("99")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Close Prices Not Consistently Increasing or Decreasing`() {
        // Close prices do not consistently increase or decrease over the required periods
        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("102"),
            BigDecimal("101"),  // Index 2 (Breaks increasing sequence)
            BigDecimal("100"),
            BigDecimal("99")
        )
        val openValues = listOf(
            BigDecimal("99"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("101"),
            BigDecimal("100")
        )
        val highValues = listOf(
            BigDecimal("101"),
            BigDecimal("103"),
            BigDecimal("103"),
            BigDecimal("102"),
            BigDecimal("100")
        )
        val lowValues = listOf(
            BigDecimal("98"),
            BigDecimal("100"),
            BigDecimal("99"),
            BigDecimal("99"),
            BigDecimal("98")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Equal Open and Close Prices`() {
        // Open and close prices are equal, so the candle has no body
        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("102"),  // Index 3 (Open equals close)
            BigDecimal("101")
        )
        val openValues = listOf(
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("102")
        )
        val highValues = listOf(
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("103"),
            BigDecimal("105"),
            BigDecimal("103")
        )
        val lowValues = listOf(
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Current Close Equal to Previous Close`() {
        // Current close is equal to previous close
        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("99"),
            BigDecimal("98"),
            BigDecimal("97"),   // Index 3 (Bullish candle with wick)
            BigDecimal("97")    // Index 4 (Current close equal to previous close)
        )
        val openValues = listOf(
            BigDecimal("101"),
            BigDecimal("100"),
            BigDecimal("99"),
            BigDecimal("95"),
            BigDecimal("96")
        )
        val highValues = listOf(
            BigDecimal("102"),
            BigDecimal("101"),
            BigDecimal("100"),
            BigDecimal("103"),
            BigDecimal("98")
        )
        val lowValues = listOf(
            BigDecimal("99"),
            BigDecimal("98"),
            BigDecimal("97"),
            BigDecimal("94"),
            BigDecimal("95")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Data with Negative Prices - Invalid Data`() {
        // Negative prices, invalid data
        val closeValues = listOf(
            BigDecimal("-100"),
            BigDecimal("-101"),
            BigDecimal("-102"),
            BigDecimal("-103"),
            BigDecimal("-104")
        )
        val openValues = listOf(
            BigDecimal("-99"),
            BigDecimal("-100"),
            BigDecimal("-101"),
            BigDecimal("-102"),
            BigDecimal("-103")
        )
        val highValues = listOf(
            BigDecimal("-98"),
            BigDecimal("-99"),
            BigDecimal("-100"),
            BigDecimal("-101"),
            BigDecimal("-102")
        )
        val lowValues = listOf(
            BigDecimal("-101"),
            BigDecimal("-102"),
            BigDecimal("-103"),
            BigDecimal("-104"),
            BigDecimal("-105")
        )

        val strategy = CandleWick(
            close = DataColumn.createValueColumn("close", closeValues),
            open = DataColumn.createValueColumn("open", openValues),
            high = DataColumn.createValueColumn("high", highValues),
            low = DataColumn.createValueColumn("low", lowValues),
            currentIndex = 4
        )

        // Assuming the strategy should return Nothing for invalid data
        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }
}