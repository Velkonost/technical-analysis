package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.StochRsiMacd
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class StochRsiMacdTest {
    @Test
    fun `Standard Long Signal`() {
        // Conditions:
        // - fastd < 20, fastk < 20 at current index
        // - RSI > 50
        // - MACD crosses above MACD signal at current index

        val fastdValues = listOf(
            BigDecimal("25"),
            BigDecimal("18"),
            BigDecimal("15"),
            BigDecimal("12"),
            BigDecimal("17")  // Index 4 (currentIndex)
        )
        val fastkValues = listOf(
            BigDecimal("28"),
            BigDecimal("20"),
            BigDecimal("18"),
            BigDecimal("10"),
            BigDecimal("15")
        )
        val rsiValues = listOf(
            BigDecimal("48"),
            BigDecimal("49"),
            BigDecimal("50"),
            BigDecimal("52"),
            BigDecimal("55")
        )
        val macdValues = listOf(
            BigDecimal("-0.5"),
            BigDecimal("-0.3"),
            BigDecimal("-0.1"),
            BigDecimal("0.1"),
            BigDecimal("0.2")
        )
        val macdSignalValues = listOf(
            BigDecimal("-0.4"),
            BigDecimal("-0.2"),
            BigDecimal("0.0"),
            BigDecimal("0.05"),
            BigDecimal("0.15")
        )

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Standard Short Signal`() {
        // Conditions:
        // - fastd > 80, fastk > 80 at current index
        // - RSI < 50
        // - MACD crosses below MACD signal at current index

        val fastdValues = listOf(
            BigDecimal("75"),
            BigDecimal("82"),
            BigDecimal("85"),
            BigDecimal("88"),
            BigDecimal("83")  // Index 4 (currentIndex)
        )
        val fastkValues = listOf(
            BigDecimal("78"),
            BigDecimal("84"),
            BigDecimal("87"),
            BigDecimal("90"),
            BigDecimal("85")
        )
        val rsiValues = listOf(
            BigDecimal("52"),
            BigDecimal("51"),
            BigDecimal("49"),
            BigDecimal("47"),
            BigDecimal("45")
        )
        val macdValues = listOf(
            BigDecimal("0.5"),
            BigDecimal("0.3"),
            BigDecimal("0.2"),
            BigDecimal("0.1"),
            BigDecimal("-0.1")
        )
        val macdSignalValues = listOf(
            BigDecimal("0.4"),
            BigDecimal("0.25"), // Index 1 (Adjusted)
            BigDecimal("0.15"), // Index 2 (Adjusted)
            BigDecimal("0.05"), // Index 3 (Adjusted)
            BigDecimal("0.0")
        )

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `No Signal - Conditions Not Met`() {
        // Conditions are not met for either Long or Short signal

        val fastdValues = listOf(
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )
        val fastkValues = listOf(
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )
        val rsiValues = listOf(
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )
        val macdValues = listOf(
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0")
        )
        val macdSignalValues = listOf(
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0")
        )

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Insufficient Data`() {
        // Less than 4 data points

        val fastdValues = listOf(
            BigDecimal("20"),
            BigDecimal("25"),
            BigDecimal("30")
        )
        val fastkValues = listOf(
            BigDecimal("22"),
            BigDecimal("27"),
            BigDecimal("35")
        )
        val rsiValues = listOf(
            BigDecimal("45"),
            BigDecimal("48"),
            BigDecimal("51")
        )
        val macdValues = listOf(
            BigDecimal("-0.3"),
            BigDecimal("-0.1"),
            BigDecimal("0.1")
        )
        val macdSignalValues = listOf(
            BigDecimal("-0.4"),
            BigDecimal("-0.2"),
            BigDecimal("0.0")
        )

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 2
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Boundary Conditions - Index Out of Bounds`() {
        // currentIndex is out of bounds

        val fastdValues = listOf<BigDecimal>()
        val fastkValues = listOf<BigDecimal>()
        val rsiValues = listOf<BigDecimal>()
        val macdValues = listOf<BigDecimal>()
        val macdSignalValues = listOf<BigDecimal>()

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Long Signal with Conditions at Earlier Indices`() {
        // Corrected Conditions:
        // - fastd[actualIndex - 3] < 20, fastk[actualIndex - 3] < 20
        // - RSI > 50
        // - MACD crosses above MACD signal at current index
        // - macd[actualIndex - 2] < macdSignal[actualIndex - 2]

        val fastdValues = listOf(
            BigDecimal("25"),
            BigDecimal("18"), // Index 1 (fastd < 20)
            BigDecimal("22"),
            BigDecimal("30"),
            BigDecimal("35")
        )
        val fastkValues = listOf(
            BigDecimal("28"),
            BigDecimal("17"), // Index 1 (fastk < 20)
            BigDecimal("25"),
            BigDecimal("32"),
            BigDecimal("38")
        )
        val rsiValues = listOf(
            BigDecimal("48"),
            BigDecimal("49"),
            BigDecimal("51"),
            BigDecimal("53"),
            BigDecimal("55")
        )
        val macdValues = listOf(
            BigDecimal("-0.5"),
            BigDecimal("-0.3"),
            BigDecimal("-0.35"), // Index 2 (Adjusted)
            BigDecimal("0.1"),
            BigDecimal("0.2")
        )
        val macdSignalValues = listOf(
            BigDecimal("-0.6"),
            BigDecimal("-0.4"),
            BigDecimal("-0.3"),
            BigDecimal("0.0"),
            BigDecimal("0.1")
        )

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Short Signal with Conditions at Earlier Indices`() {
        // Conditions:
        // - fastd[actualIndex - 2] > 80, fastk[actualIndex - 2] > 80
        // - RSI < 50
        // - MACD crosses below MACD signal at current index

        val fastdValues = listOf(
            BigDecimal("85"),
            BigDecimal("83"), // Index 1 (fastd > 80)
            BigDecimal("78"),
            BigDecimal("75"),
            BigDecimal("70")
        )
        val fastkValues = listOf(
            BigDecimal("88"),
            BigDecimal("86"), // Index 1 (fastk > 80)
            BigDecimal("80"),
            BigDecimal("77"),
            BigDecimal("72")
        )
        val rsiValues = listOf(
            BigDecimal("49"),
            BigDecimal("48"),
            BigDecimal("47"),
            BigDecimal("46"),
            BigDecimal("45")
        )
        val macdValues = listOf(
            BigDecimal("0.3"),
            BigDecimal("0.2"),
            BigDecimal("0.1"),
            BigDecimal("-0.05"),
            BigDecimal("-0.1")
        )
        val macdSignalValues = listOf(
            BigDecimal("0.25"),
            BigDecimal("0.15"), // Index 1 (Adjusted)
            BigDecimal("0.05"), // Index 2 (Adjusted)
            BigDecimal("-0.05"),
            BigDecimal("-0.08") // Index 4 (Adjusted)
        )
        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `Conditions Exactly at Thresholds`() {
        // Values are exactly at thresholds, should not trigger signals due to strict inequalities

        val fastdValues = listOf(
            BigDecimal("20"),
            BigDecimal("20"),
            BigDecimal("20"),
            BigDecimal("20"),
            BigDecimal("20")
        )
        val fastkValues = listOf(
            BigDecimal("20"),
            BigDecimal("20"),
            BigDecimal("20"),
            BigDecimal("20"),
            BigDecimal("20")
        )
        val rsiValues = listOf(
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )
        val macdValues = listOf(
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0")
        )
        val macdSignalValues = listOf(
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0")
        )

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        // Since the conditions use strict inequalities, no signal should be triggered
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Overlapping Signals`() {
        // Both Long and Short conditions are partially met, but no full condition is satisfied

        val fastdValues = listOf(
            BigDecimal("19"), // Index 0 (fastd < 20)
            BigDecimal("81"), // Index 1 (fastd > 80)
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")  // Index 4 (currentIndex)
        )
        val fastkValues = listOf(
            BigDecimal("19"), // Index 0 (fastk < 20)
            BigDecimal("81"), // Index 1 (fastk > 80)
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )
        val rsiValues = listOf(
            BigDecimal("49"),
            BigDecimal("51"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )
        val macdValues = listOf(
            BigDecimal("-0.1"),
            BigDecimal("0.1"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0")
        )
        val macdSignalValues = listOf(
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0"),
            BigDecimal("0.0")
        )

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        // No full condition for Long or Short is satisfied
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `MACD Crossover without StochRSI Confirmation`() {
        // MACD crossover occurs but StochRSI conditions are not met

        val fastdValues = listOf(
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )
        val fastkValues = listOf(
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50"),
            BigDecimal("50")
        )
        val rsiValues = listOf(
            BigDecimal("55"),
            BigDecimal("56"),
            BigDecimal("57"),
            BigDecimal("58"),
            BigDecimal("59")
        )
        val macdValues = listOf(
            BigDecimal("-0.1"),
            BigDecimal("-0.05"),
            BigDecimal("0.0"),
            BigDecimal("0.05"),
            BigDecimal("0.1")
        )
        val macdSignalValues = listOf(
            BigDecimal("-0.2"),
            BigDecimal("-0.1"),
            BigDecimal("0.0"),
            BigDecimal("0.05"),
            BigDecimal("0.1")
        )

        val strategy = StochRsiMacd(
            fastd = DataColumn.createValueColumn("fastd", fastdValues),
            fastk = DataColumn.createValueColumn("fastk", fastkValues),
            rsi = DataColumn.createValueColumn("rsi", rsiValues),
            macd = DataColumn.createValueColumn("macd", macdValues),
            macdSignal = DataColumn.createValueColumn("macdSignal", macdSignalValues),
            currentIndex = 4
        )

        val decision = strategy.calculate()
        // MACD crossover occurred but StochRSI conditions are not met
        assertEquals(StrategyDecision.Nothing, decision)
    }
}