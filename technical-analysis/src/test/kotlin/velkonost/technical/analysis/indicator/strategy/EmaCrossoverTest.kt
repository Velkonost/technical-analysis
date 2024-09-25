package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.EmaCrossover
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class EmaCrossoverTest {
    @Test
    fun `Standard Long Signal`() {
        // EMA Short crosses above EMA Long
        val emaShortValues = listOf(BigDecimal("99"), BigDecimal("99"), BigDecimal("101"))
        val emaLongValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))

        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues),
            currentIndex = -1  // Last index
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Standard Short Signal`() {
        // EMA Short crosses below EMA Long
        val emaShortValues = listOf(BigDecimal("101"), BigDecimal("101"), BigDecimal("99"))
        val emaLongValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))

        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues),
            currentIndex = -1  // Last index
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `No Signal - No Crossover`() {
        // EMAs do not cross
        val emaShortValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))
        val emaLongValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))

        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues)
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `No Signal - EMAs Equal`() {
        // EMAs are equal and remain equal
        val emaValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))

        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaValues)
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Boundary Conditions - Index Out of Bounds`() {
        // currentIndex is out of bounds
        val emaShortValues = listOf<BigDecimal>()
        val emaLongValues = listOf<BigDecimal>()

        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues)
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Insufficient Data`() {
        // Only one data point, insufficient for crossover
        val emaShortValues = listOf(BigDecimal("100"))
        val emaLongValues = listOf(BigDecimal("100"))

        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues)
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Repeated Crossovers`() {
        // Multiple crossovers, test at specific index
        val emaShortValues = listOf(
            BigDecimal("99"), BigDecimal("101"), BigDecimal("99"), BigDecimal("101"), BigDecimal("99")
        )
        val emaLongValues = listOf(
            BigDecimal("100"), BigDecimal("100"), BigDecimal("100"), BigDecimal("100"), BigDecimal("100")
        )

        // Test at index 3 (should be a Long signal)
        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues),
            currentIndex = 3
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Long Signal at the Beginning of Data`() {
        // Crossover occurs at the beginning
        val emaShortValues = listOf(BigDecimal("99"), BigDecimal("101"), BigDecimal("102"))
        val emaLongValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))

        // Test at index 1
        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues),
            currentIndex = 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Short Signal at the End of Data`() {
        // Crossover occurs at the last index
        val emaShortValues = listOf(BigDecimal("101"), BigDecimal("100.5"), BigDecimal("99"))
        val emaLongValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))

        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues),
            currentIndex = -1  // Last index
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `Fluctuating EMAs with No Crossover`() {
        // EMAs fluctuate but do not cross over
        val emaShortValues = listOf(BigDecimal("99"), BigDecimal("100.5"), BigDecimal("100"))
        val emaLongValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))

        val strategy = EmaCrossover(
            emaShort = DataColumn.createValueColumn("emaShort", emaShortValues),
            emaLong = DataColumn.createValueColumn("emaLong", emaLongValues)
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }
}