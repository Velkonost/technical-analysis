package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.GoldenCross
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class GoldenCrossTest {
    @Test
    fun `Standard Long Signal`() {
        // Conditions:
        // - close > ema100
        // - rsi > 50
        // - ema20 crossed above ema50 within the last 3 periods

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("102"),
            BigDecimal("105"),
            BigDecimal("107"),
            BigDecimal("110")
        )
        val ema100Values = listOf(
            BigDecimal("95"),
            BigDecimal("96"),
            BigDecimal("97"),
            BigDecimal("98"),
            BigDecimal("99")
        )
        val ema50Values = listOf(
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102")
        )
        val ema20Values = listOf(
            BigDecimal("97"),
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("103"), // Index 3 (Crossed above ema50)
            BigDecimal("104")
        )
        val rsiValues = listOf(
            BigDecimal("45"),
            BigDecimal("48"),
            BigDecimal("52"),
            BigDecimal("55"),
            BigDecimal("60")
        )

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Standard Short Signal`() {
        // Conditions:
        // - close < ema100
        // - rsi < 50
        // - ema20 crossed below ema50 within the last 3 periods

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("98"),
            BigDecimal("95"),
            BigDecimal("93"),
            BigDecimal("90")
        )
        val ema100Values = listOf(
            BigDecimal("105"),
            BigDecimal("104"),
            BigDecimal("103"),
            BigDecimal("102"),
            BigDecimal("101")
        )
        val ema50Values = listOf(
            BigDecimal("102"),
            BigDecimal("101"),
            BigDecimal("100"),
            BigDecimal("99"),
            BigDecimal("98")
        )
        val ema20Values = listOf(
            BigDecimal("103"),
            BigDecimal("102"),
            BigDecimal("101"), // Index 2 (Crossed below ema50)
            BigDecimal("97"),
            BigDecimal("96")
        )
        val rsiValues = listOf(
            BigDecimal("55"),
            BigDecimal("52"),
            BigDecimal("48"),
            BigDecimal("45"),
            BigDecimal("40")
        )

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `No Signal - Conditions Not Met`() {
        // Conditions for neither Long nor Short are met

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100")
        )
        val ema100Values = listOf(
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100")
        )
        val ema50Values = listOf(
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100")
        )
        val ema20Values = listOf(
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100")
        )
        val rsiValues = listOf(
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Insufficient Data`() {
        // Less than 4 data points

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102")
        )
        val ema100Values = listOf(
            BigDecimal("95"),
            BigDecimal("96"),
            BigDecimal("97")
        )
        val ema50Values = listOf(
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("100")
        )
        val ema20Values = listOf(
            BigDecimal("97"),
            BigDecimal("98"),
            BigDecimal("99")
        )
        val rsiValues = listOf(
            BigDecimal("45"),
            BigDecimal("48"),
            BigDecimal("52")
        )

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = 2
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Boundary Conditions - Index Out of Bounds`() {
        // currentIndex is out of bounds

        val closeValues = listOf<BigDecimal>()
        val ema100Values = listOf<BigDecimal>()
        val ema50Values = listOf<BigDecimal>()
        val ema20Values = listOf<BigDecimal>()
        val rsiValues = listOf<BigDecimal>()

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `EMA Cross Up Occurred Earlier`() {
        // EMA20 crossed above EMA50 at currentIndex - 3

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("102"),
            BigDecimal("105"),
            BigDecimal("107"),
            BigDecimal("110")
        )
        val ema100Values = listOf(
            BigDecimal("95"),
            BigDecimal("96"),
            BigDecimal("97"),
            BigDecimal("98"),
            BigDecimal("99")
        )
        val ema50Values = listOf(
            BigDecimal("98"),
            BigDecimal("99"),
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102")
        )
        val ema20Values = listOf(
            BigDecimal("97"),  // Index 0 (Crossed above here)
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("104")
        )
        val rsiValues = listOf(
            BigDecimal("45"),
            BigDecimal("48"),
            BigDecimal("52"),
            BigDecimal("55"),
            BigDecimal("60")
        )

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = 3
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `EMA Cross Down Occurred Earlier`() {
        // EMA20 crossed below EMA50 at currentIndex - 3

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("98"),
            BigDecimal("95"),
            BigDecimal("93"),
            BigDecimal("90")
        )
        val ema100Values = listOf(
            BigDecimal("105"),
            BigDecimal("104"),
            BigDecimal("103"),
            BigDecimal("102"),
            BigDecimal("101")
        )
        val ema50Values = listOf(
            BigDecimal("102"),
            BigDecimal("101"),
            BigDecimal("100"),
            BigDecimal("99"),
            BigDecimal("98")
        )
        val ema20Values = listOf(
            BigDecimal("103"), // Index 0 (Crossed below here)
            BigDecimal("100"),
            BigDecimal("99"),
            BigDecimal("97"),
            BigDecimal("96")
        )
        val rsiValues = listOf(
            BigDecimal("55"),
            BigDecimal("52"),
            BigDecimal("48"),
            BigDecimal("45"),
            BigDecimal("40")
        )

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = 3
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `RSI Exactly at Threshold`() {
        // RSI is exactly at 50, should not trigger a signal

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100")
        )
        val ema100Values = listOf(
            BigDecimal("95"),
            BigDecimal("95"),
            BigDecimal("95"),
            BigDecimal("95"),
            BigDecimal("95")
        )
        val ema50Values = listOf(
            BigDecimal("97"),
            BigDecimal("97"),
            BigDecimal("97"),
            BigDecimal("97"),
            BigDecimal("97")
        )
        val ema20Values = listOf(
            BigDecimal("96"),
            BigDecimal("96"),
            BigDecimal("96"),
            BigDecimal("98"),  // Index 3 (Crossed above ema50)
            BigDecimal("98")
        )
        val rsiValues = listOf(
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Close Exactly at EMA100`() {
        // Close price is exactly at EMA100, should not trigger a signal

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100")
        )
        val ema100Values = listOf(
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100"),
            BigDecimal("100")
        )
        val ema50Values = listOf(
            BigDecimal("99"),
            BigDecimal("99"),
            BigDecimal("99"),
            BigDecimal("99"),
            BigDecimal("99")
        )
        val ema20Values = listOf(
            BigDecimal("98"),
            BigDecimal("98"),
            BigDecimal("98"),
            BigDecimal("100"), // Index 3 (Crossed above ema50)
            BigDecimal("100")
        )
        val rsiValues = listOf(
            BigDecimal("51"),
            BigDecimal("52"),
            BigDecimal("53"),
            BigDecimal("54"),
            BigDecimal("55")
        )

        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `EMA20 Equals EMA50`() {
        // EMA20 equals EMA50, should not trigger a crossover signal

        val closeValues = listOf(
            BigDecimal("100"),
            BigDecimal("101"),
            BigDecimal("102"),
            BigDecimal("103"),
            BigDecimal("104")
        )
        val ema100Values = listOf(
            BigDecimal("95"),
            BigDecimal("95"),
            BigDecimal("95"),
            BigDecimal("95"),
            BigDecimal("95")
        )
        val ema50Values = listOf(
            BigDecimal("99"),
            BigDecimal("99"),
            BigDecimal("99"),
            BigDecimal("99"),
            BigDecimal("99")
        )
        val ema20Values = listOf(
            BigDecimal("99"),  // Index 0 (Equals ema50)
            BigDecimal("99"),  // Index 1 (Equals ema50)
            BigDecimal("99"),  // Index 2 (Equals ema50)
            BigDecimal("99.5"),// Index 3 (Crosses above ema50)
            BigDecimal("100")
        )
        val rsiValues = listOf(
            BigDecimal("55"),
            BigDecimal("56"),
            BigDecimal("57"),
            BigDecimal("58"),
            BigDecimal("59")
        )


        val strategy = GoldenCross(
            close = DataColumn.createValueColumn("close", closeValues),
            ema100 = DataColumn.createValueColumn("ema100", ema100Values),
            ema50 = DataColumn.createValueColumn("ema50", ema50Values),
            ema20 = DataColumn.createValueColumn("ema20", ema20Values),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
        )

        val decision = strategy.calculate()
        // Since EMA20 equals EMA50, there is no strict crossover
        assertEquals(StrategyDecision.Nothing, decision)
    }
}