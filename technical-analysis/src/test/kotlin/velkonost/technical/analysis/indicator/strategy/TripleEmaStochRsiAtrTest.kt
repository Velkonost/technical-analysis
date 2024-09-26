package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.TripleEmaStochRsiAtr
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class TripleEmaStochRsiAtrTest {
    private fun generateTestData(
        size: Int,
        closeValues: List<BigDecimal>,
        ema8Values: List<BigDecimal>,
        ema14Values: List<BigDecimal>,
        ema50Values: List<BigDecimal>,
        fastkValues: List<BigDecimal>,
        fastdValues: List<BigDecimal>
    ): Map<String, DataColumn<BigDecimal>> {
        val close = DataColumn.create("close", closeValues)
        val ema8 = DataColumn.create("ema8", ema8Values)
        val ema14 = DataColumn.create("ema14", ema14Values)
        val ema50 = DataColumn.create("ema50", ema50Values)
        val fastk = DataColumn.create("fastk", fastkValues)
        val fastd = DataColumn.create("fastd", fastdValues)

        return mapOf(
            "close" to close,
            "ema8" to ema8,
            "ema14" to ema14,
            "ema50" to ema50,
            "fastk" to fastk,
            "fastd" to fastd
        )
    }
    @Test
    fun testLongSignal() {
        // Scenario: Conditions for a Buy Signal are met
        val size = 10
        val closeValues = MutableList(size) { BigDecimal("100") }
        val ema8Values = MutableList(size) { BigDecimal("90") }
        val ema14Values = MutableList(size) { BigDecimal("80") }
        val ema50Values = MutableList(size) { BigDecimal("70") }
        val fastkValues = MutableList(size) { BigDecimal("40") }
        val fastdValues = MutableList(size) { BigDecimal("45") }

        // Simulate fastk crossing above fastd at the last index
        fastkValues[size - 1] = BigDecimal("50")
        fastdValues[size - 1] = BigDecimal("40")
        fastkValues[size - 2] = BigDecimal("35")
        fastdValues[size - 2] = BigDecimal("45")

        val data = generateTestData(size, closeValues, ema8Values, ema14Values, ema50Values, fastkValues, fastdValues)

        val strategy = TripleEmaStochRsiAtr(
            close = data["close"]!!,
            ema50 = data["ema50"]!!,
            ema14 = data["ema14"]!!,
            ema8 = data["ema8"]!!,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            currentPos = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Expected a Long signal when buy conditions are met.")
    }

    @Test
    fun testShortSignal() {
        // Scenario: Conditions for a Sell Signal are met
        val size = 10
        val closeValues = MutableList(size) { BigDecimal("100") }
        val ema8Values = MutableList(size) { BigDecimal("110") }
        val ema14Values = MutableList(size) { BigDecimal("120") }
        val ema50Values = MutableList(size) { BigDecimal("130") }
        val fastkValues = MutableList(size) { BigDecimal("60") }
        val fastdValues = MutableList(size) { BigDecimal("55") }

        // Simulate fastk crossing below fastd at the last index
        fastkValues[size - 1] = BigDecimal("50")
        fastdValues[size - 1] = BigDecimal("60")
        fastkValues[size - 2] = BigDecimal("65")
        fastdValues[size - 2] = BigDecimal("55")

        val data = generateTestData(size, closeValues, ema8Values, ema14Values, ema50Values, fastkValues, fastdValues)

        val strategy = TripleEmaStochRsiAtr(
            close = data["close"]!!,
            ema50 = data["ema50"]!!,
            ema14 = data["ema14"]!!,
            ema8 = data["ema8"]!!,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            currentPos = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision, "Expected a Short signal when sell conditions are met.")
    }

    @Test
    fun testNoSignal() {
        // Scenario: No conditions for either Buy or Sell Signal are met
        val size = 10
        val closeValues = MutableList(size) { BigDecimal("100") }
        val ema8Values = MutableList(size) { BigDecimal("100") }
        val ema14Values = MutableList(size) { BigDecimal("100") }
        val ema50Values = MutableList(size) { BigDecimal("100") }
        val fastkValues = MutableList(size) { BigDecimal("50") }
        val fastdValues = MutableList(size) { BigDecimal("50") }

        val data = generateTestData(size, closeValues, ema8Values, ema14Values, ema50Values, fastkValues, fastdValues)

        val strategy = TripleEmaStochRsiAtr(
            close = data["close"]!!,
            ema50 = data["ema50"]!!,
            ema14 = data["ema14"]!!,
            ema8 = data["ema8"]!!,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            currentPos = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Expected no signal when no conditions are met.")
    }

    @Test
    fun testInsufficientData() {
        // Scenario: Insufficient data to evaluate the strategy
        val size = 4 // Less than the required 5 for the strategy (currentPos = 4 requires accessing indices 0-4)
        val closeValues = MutableList(size) { BigDecimal("100") }
        val ema8Values = MutableList(size) { BigDecimal("90") }
        val ema14Values = MutableList(size) { BigDecimal("80") }
        val ema50Values = MutableList(size) { BigDecimal("70") }
        val fastkValues = MutableList(size) { BigDecimal("40") }
        val fastdValues = MutableList(size) { BigDecimal("45") }

        val data = generateTestData(size, closeValues, ema8Values, ema14Values, ema50Values, fastkValues, fastdValues)

        val strategy = TripleEmaStochRsiAtr(
            close = data["close"]!!,
            ema50 = data["ema50"]!!,
            ema14 = data["ema14"]!!,
            ema8 = data["ema8"]!!,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            currentPos = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Expected no signal due to insufficient data.")
    }

    @Test
    fun testEdgeCaseBuySignal() {
        // Scenario: Edge case where buy conditions are just met
        val size = 10
        val closeValues = MutableList(size) { BigDecimal("100") }
        val ema8Values = MutableList(size) { BigDecimal("90") }
        val ema14Values = MutableList(size) { BigDecimal("80") }
        val ema50Values = MutableList(size) { BigDecimal("70") }
        val fastkValues = MutableList(size) { BigDecimal("40") }
        val fastdValues = MutableList(size) { BigDecimal("45") }

        // Set up previous indices to just meet the buy conditions
        for (i in 0 until size - 1) {
            closeValues[i] = BigDecimal("100")
            ema8Values[i] = BigDecimal("90")
            ema14Values[i] = BigDecimal("80")
            ema50Values[i] = BigDecimal("70")
            fastkValues[i] = BigDecimal("40")
            fastdValues[i] = BigDecimal("45")
        }

        // At the last index, fastk crosses above fastd
        closeValues[size - 1] = BigDecimal("101")
        ema8Values[size - 1] = BigDecimal("91")
        ema14Values[size - 1] = BigDecimal("81")
        ema50Values[size - 1] = BigDecimal("71")
        fastkValues[size - 1] = BigDecimal("46")
        fastdValues[size - 1] = BigDecimal("44")

        val data = generateTestData(size, closeValues, ema8Values, ema14Values, ema50Values, fastkValues, fastdValues)

        val strategy = TripleEmaStochRsiAtr(
            close = data["close"]!!,
            ema50 = data["ema50"]!!,
            ema14 = data["ema14"]!!,
            ema8 = data["ema8"]!!,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            currentPos = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Expected a Long signal at the edge case for buy conditions.")
    }

    @Test
    fun testEdgeCaseSellSignal() {
        // Scenario: Edge case where sell conditions are just met
        val size = 10
        val closeValues = MutableList(size) { BigDecimal("100") }
        val ema8Values = MutableList(size) { BigDecimal("110") }
        val ema14Values = MutableList(size) { BigDecimal("120") }
        val ema50Values = MutableList(size) { BigDecimal("130") }
        val fastkValues = MutableList(size) { BigDecimal("60") }
        val fastdValues = MutableList(size) { BigDecimal("55") }

        // Set up previous indices to just meet the sell conditions
        for (i in 0 until size - 1) {
            closeValues[i] = BigDecimal("100")
            ema8Values[i] = BigDecimal("110")
            ema14Values[i] = BigDecimal("120")
            ema50Values[i] = BigDecimal("130")
            fastkValues[i] = BigDecimal("60")
            fastdValues[i] = BigDecimal("55")
        }

        // At the last index, fastk crosses below fastd
        closeValues[size - 1] = BigDecimal("99")
        ema8Values[size - 1] = BigDecimal("109")
        ema14Values[size - 1] = BigDecimal("119")
        ema50Values[size - 1] = BigDecimal("129")
        fastkValues[size - 1] = BigDecimal("54")
        fastdValues[size - 1] = BigDecimal("56")

        val data = generateTestData(size, closeValues, ema8Values, ema14Values, ema50Values, fastkValues, fastdValues)

        val strategy = TripleEmaStochRsiAtr(
            close = data["close"]!!,
            ema50 = data["ema50"]!!,
            ema14 = data["ema14"]!!,
            ema8 = data["ema8"]!!,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            currentPos = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision, "Expected a Short signal at the edge case for sell conditions.")
    }

    @Test
    fun testRapidChangesToBuySignal() {
        // Scenario: Rapid changes leading to a Buy Signal
        val size = 10
        val closeValues = MutableList(size) { BigDecimal("100") }
        val ema8Values = MutableList(size) { BigDecimal("90") }
        val ema14Values = MutableList(size) { BigDecimal("80") }
        val ema50Values = MutableList(size) { BigDecimal("70") }
        val fastkValues = MutableList(size) { BigDecimal("40") }
        val fastdValues = MutableList(size) { BigDecimal("45") }

        // Simulate rapid increase where fastk crosses above fastd at the last index
        for (i in 0 until size - 1) {
            closeValues[i] = BigDecimal("100")
            ema8Values[i] = BigDecimal("90")
            ema14Values[i] = BigDecimal("80")
            ema50Values[i] = BigDecimal("70")
            fastkValues[i] = BigDecimal("40")
            fastdValues[i] = BigDecimal("45")
        }

        // At the last index, fastk rapidly increases and crosses above fastd
        closeValues[size - 1] = BigDecimal("105")
        ema8Values[size - 1] = BigDecimal("95")
        ema14Values[size - 1] = BigDecimal("85")
        ema50Values[size - 1] = BigDecimal("75")
        fastkValues[size - 1] = BigDecimal("50")
        fastdValues[size - 1] = BigDecimal("40")

        val data = generateTestData(size, closeValues, ema8Values, ema14Values, ema50Values, fastkValues, fastdValues)

        val strategy = TripleEmaStochRsiAtr(
            close = data["close"]!!,
            ema50 = data["ema50"]!!,
            ema14 = data["ema14"]!!,
            ema8 = data["ema8"]!!,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            currentPos = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Expected a Long signal after rapid changes leading to buy conditions.")
    }

    @Test
    fun testRapidChangesToSellSignal() {
        // Scenario: Rapid changes leading to a Sell Signal
        val size = 10
        val closeValues = MutableList(size) { BigDecimal("100") }
        val ema8Values = MutableList(size) { BigDecimal("110") }
        val ema14Values = MutableList(size) { BigDecimal("120") }
        val ema50Values = MutableList(size) { BigDecimal("130") }
        val fastkValues = MutableList(size) { BigDecimal("60") }
        val fastdValues = MutableList(size) { BigDecimal("55") }

        // Simulate rapid decrease where fastk crosses below fastd at the last index
        for (i in 0 until size - 1) {
            closeValues[i] = BigDecimal("100")
            ema8Values[i] = BigDecimal("110")
            ema14Values[i] = BigDecimal("120")
            ema50Values[i] = BigDecimal("130")
            fastkValues[i] = BigDecimal("60")
            fastdValues[i] = BigDecimal("55")
        }

        // At the last index, fastk rapidly decreases and crosses below fastd
        closeValues[size - 1] = BigDecimal("95")
        ema8Values[size - 1] = BigDecimal("105")
        ema14Values[size - 1] = BigDecimal("115")
        ema50Values[size - 1] = BigDecimal("125")
        fastkValues[size - 1] = BigDecimal("50")
        fastdValues[size - 1] = BigDecimal("60")

        val data = generateTestData(size, closeValues, ema8Values, ema14Values, ema50Values, fastkValues, fastdValues)

        val strategy = TripleEmaStochRsiAtr(
            close = data["close"]!!,
            ema50 = data["ema50"]!!,
            ema14 = data["ema14"]!!,
            ema8 = data["ema8"]!!,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            currentPos = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision, "Expected a Short signal after rapid changes leading to sell conditions.")
    }
}