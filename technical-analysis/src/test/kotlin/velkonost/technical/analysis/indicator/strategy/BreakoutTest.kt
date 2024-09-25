package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.Breakout
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class BreakoutTest {

    @Test
    fun `Standard Long Signal`() {
        // Close price and volume are above their respective max values
        val closeValues = listOf(BigDecimal("100"), BigDecimal("105"), BigDecimal("110"))
        val volumeValues = listOf(BigDecimal("1000"), BigDecimal("1500"), BigDecimal("2000"))
        val maxCloseValues = listOf(BigDecimal("95"), BigDecimal("95"), BigDecimal("95"))
        val minCloseValues = listOf(BigDecimal("90"), BigDecimal("90"), BigDecimal("90"))
        val maxVolValues = listOf(BigDecimal("1500"), BigDecimal("1500"), BigDecimal("1500"))

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVolume", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Standard Short Signal`() {
        // Close price and volume are below their respective min values
        val closeValues = listOf(BigDecimal("100"), BigDecimal("95"), BigDecimal("90"))
        val volumeValues = listOf(BigDecimal("1000"), BigDecimal("1500"), BigDecimal("2000"))
        val maxCloseValues = listOf(BigDecimal("105"), BigDecimal("105"), BigDecimal("105"))
        val minCloseValues = listOf(BigDecimal("95"), BigDecimal("95"), BigDecimal("95"))
        val maxVolValues = listOf(BigDecimal("1500"), BigDecimal("1500"), BigDecimal("1500"))

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVolume", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `No Signal - Conditions Not Met`() {
        // Close price and volume do not meet any conditions
        val closeValues = listOf(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"))
        val volumeValues = listOf(BigDecimal("1000"), BigDecimal("1000"), BigDecimal("1000"))
        val maxCloseValues = listOf(BigDecimal("105"), BigDecimal("105"), BigDecimal("105"))
        val minCloseValues = listOf(BigDecimal("95"), BigDecimal("95"), BigDecimal("95"))
        val maxVolValues = listOf(BigDecimal("2000"), BigDecimal("2000"), BigDecimal("2000"))

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVol", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Invert Flag True - Fakeout Strategy`() {
        // Invert flag is true, logic is inverted
        val closeValues = listOf(BigDecimal("100"), BigDecimal("105"), BigDecimal("110"))
        val volumeValues = listOf(BigDecimal("1000"), BigDecimal("1500"), BigDecimal("2000"))
        val maxCloseValues = listOf(BigDecimal("95"), BigDecimal("95"), BigDecimal("95"))
        val minCloseValues = listOf(BigDecimal("90"), BigDecimal("90"), BigDecimal("90"))
        val maxVolValues = listOf(BigDecimal("1500"), BigDecimal("1500"), BigDecimal("1500"))

        // We need to modify the Breakout class to accept an invert parameter
        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVolume", maxVolValues),
            currentIndex = -1,
            invert = true  // Added invert parameter
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision)
    }

    @Test
    fun `Boundary Conditions - Index Out of Bounds`() {
        // currentIndex is out of bounds
        val closeValues = listOf<BigDecimal>()
        val volumeValues = listOf<BigDecimal>()
        val maxCloseValues = listOf<BigDecimal>()
        val minCloseValues = listOf<BigDecimal>()
        val maxVolValues = listOf<BigDecimal>()

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVol", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Insufficient Data`() {
        // Only one data point
        val closeValues = listOf(BigDecimal("100"))
        val volumeValues = listOf(BigDecimal("1000"))
        val maxCloseValues = listOf(BigDecimal("95"))
        val minCloseValues = listOf(BigDecimal("90"))
        val maxVolValues = listOf(BigDecimal("1500"))

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVolume", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Equal Close and MaxClose Values`() {
        // Close equals MaxClose, volume equals MaxVol
        val closeValues = listOf(BigDecimal("100"), BigDecimal("105"), BigDecimal("110"))
        val volumeValues = listOf(BigDecimal("1000"), BigDecimal("1500"), BigDecimal("2000"))
        val maxCloseValues = listOf(BigDecimal("110"), BigDecimal("110"), BigDecimal("110"))
        val minCloseValues = listOf(BigDecimal("90"), BigDecimal("90"), BigDecimal("90"))
        val maxVolValues = listOf(BigDecimal("2000"), BigDecimal("2000"), BigDecimal("2000"))

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVolume", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Close and Volume Exactly at Max Values`() {
        // Close equals MaxClose, volume equals MaxVol
        val closeValues = listOf(BigDecimal("100"), BigDecimal("105"), BigDecimal("110"))
        val volumeValues = listOf(BigDecimal("1000"), BigDecimal("1500"), BigDecimal("2000"))
        val maxCloseValues = listOf(BigDecimal("110"), BigDecimal("110"), BigDecimal("110"))
        val minCloseValues = listOf(BigDecimal("90"), BigDecimal("90"), BigDecimal("90"))
        val maxVolValues = listOf(BigDecimal("2000"), BigDecimal("2000"), BigDecimal("2000"))

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVolume", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision)
    }

    @Test
    fun `Close and Volume Below Max Values`() {
        // Close and volume are below their respective max values
        val closeValues = listOf(BigDecimal("100"), BigDecimal("105"), BigDecimal("109"))
        val volumeValues = listOf(BigDecimal("1000"), BigDecimal("1500"), BigDecimal("1999"))
        val maxCloseValues = listOf(BigDecimal("110"), BigDecimal("110"), BigDecimal("110"))
        val minCloseValues = listOf(BigDecimal("90"), BigDecimal("90"), BigDecimal("90"))
        val maxVolValues = listOf(BigDecimal("2000"), BigDecimal("2000"), BigDecimal("2000"))

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVolume", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }

    @Test
    fun `Close Above Max Close but Volume Below Max Volume`() {
        // Close is above MaxClose but volume is below MaxVol
        val closeValues = listOf(BigDecimal("100"), BigDecimal("105"), BigDecimal("111"))
        val volumeValues = listOf(BigDecimal("1000"), BigDecimal("1500"), BigDecimal("1999"))
        val maxCloseValues = listOf(BigDecimal("110"), BigDecimal("110"), BigDecimal("110"))
        val minCloseValues = listOf(BigDecimal("90"), BigDecimal("90"), BigDecimal("90"))
        val maxVolValues = listOf(BigDecimal("2000"), BigDecimal("2000"), BigDecimal("2000"))

        val strategy = Breakout(
            close = DataColumn.createValueColumn("close", closeValues),
            volume = DataColumn.createValueColumn("volume", volumeValues),
            maxClose = DataColumn.createValueColumn("maxClose", maxCloseValues),
            minClose = DataColumn.createValueColumn("minClose", minCloseValues),
            maxVolume = DataColumn.createValueColumn("maxVolume", maxVolValues),
            currentIndex = -1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision)
    }
}