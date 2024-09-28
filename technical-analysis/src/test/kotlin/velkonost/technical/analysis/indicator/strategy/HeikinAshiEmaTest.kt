package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.HeikinAshiEma
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class HeikinAshiEmaTest {
    private fun generateTestData(
        openStreamHValues: List<BigDecimal>,
        closeHValues: List<BigDecimal>,
        fastdValues: List<BigDecimal>,
        fastkValues: List<BigDecimal>,
        ema200Values: List<BigDecimal>
    ): Map<String, DataColumn<BigDecimal>> {
        val openStreamH = DataColumn.create("openStreamH", openStreamHValues)
        val closeH = DataColumn.create("closeH", closeHValues)
        val fastd = DataColumn.create("fastd", fastdValues)
        val fastk = DataColumn.create("fastk", fastkValues)
        val ema200 = DataColumn.create("ema200", ema200Values)

        return mapOf(
            "openStreamH" to openStreamH,
            "closeH" to closeH,
            "fastd" to fastd,
            "fastk" to fastk,
            "ema200" to ema200
        )
    }

    @Test
    fun testShortSignal() {
        // Scenario: Conditions for a Short Signal are met
        val size = 15
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.85") }
        val fastkValues = MutableList(size) { BigDecimal("0.85") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        // Simulate overbought conditions with fastk and fastd above 0.8 for the last 10 candles
        for (i in (size - 10) until (size - 4)) { // i=5..10
            closeHValues[i] = BigDecimal("102") // Above EMA200 initially
            openStreamHValues[i] = BigDecimal("101")
            fastkValues[i] = BigDecimal("0.85")
            fastdValues[i] = BigDecimal("0.85")
        }

        // Simulate a crossover at j=4 (i.e., currentIndex -4=11)
        val crossoverIndex = size - 4 // 11
        fastkValues[crossoverIndex] = BigDecimal("0.85") // fastk > fastd
        fastdValues[crossoverIndex] = BigDecimal("0.80")

        // At crossoverIndex +1=12, fastk < fastd
        fastkValues[crossoverIndex + 1] = BigDecimal("0.75") // fastk < fastd=0.85
        fastdValues[crossoverIndex + 1] = BigDecimal("0.85")

        // After the crossover, fastk and fastd stay overbought
        fastkValues[size - 2] = BigDecimal("0.85") // >= shortThreshold
        fastdValues[size - 2] = BigDecimal("0.85") // >= shortThreshold
        fastkValues[size - 1] = BigDecimal("0.85") // >= shortThreshold
        fastdValues[size - 1] = BigDecimal("0.85") // >= shortThreshold

        // Simulate Heikin-Ashi price crossing below the 200 EMA
        closeHValues[crossoverIndex -1] = BigDecimal("101")  // i=10, closeH=101 > ema200=100
        closeHValues[crossoverIndex] = BigDecimal("98")  // i=11, closeH=98 < ema200=100
        closeHValues[size - 1] = BigDecimal("97")  // i=14, closeH=97 < openStreamH=99

        openStreamHValues[size - 1] = BigDecimal("99")

        // **Crucial Correction:** Set closeH[12] = 101 > ema200[12] = 100
        closeHValues[12] = BigDecimal("101") // currentIndex -2 =12 > ema200=100

        // Set closeH[13] = 98 < ema200=100
        closeHValues[13] = BigDecimal("98") // currentIndex -1 =13 < ema200=100

        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = -99,
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size - 1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Short, result, "Expected a SHORT signal when sell conditions are met.")
    }

    @Test
    fun testLongSignal() {
        // Scenario: Conditions for a Long Signal are met
        val size = 15
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.1") }
        val fastkValues = MutableList(size) { BigDecimal("0.1") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        // Simulate oversold conditions with fastk and fastd below 0.2 for the last 10 candles
        for (i in (size - 10) until (size - 4)) { // i=5..10
            closeHValues[i] = BigDecimal("98") // Below EMA200 initially
            openStreamHValues[i] = BigDecimal("99")
            fastkValues[i] = BigDecimal("0.1")
            fastdValues[i] = BigDecimal("0.1")
        }

        // Simulate a crossover at j=4 (i.e., currentIndex -4=11)
        val crossoverIndex = size - 4 //11
        fastkValues[crossoverIndex] = BigDecimal("0.15") // fastk < fastd
        fastdValues[crossoverIndex] = BigDecimal("0.25")

        // At crossoverIndex +1=12, fastk > fastd
        fastkValues[crossoverIndex +1] = BigDecimal("0.25") // fastk > fastd=0.15
        fastdValues[crossoverIndex +1] = BigDecimal("0.15")

        // After the crossover, fastk and fastd stay oversold
        fastkValues[size - 2] = BigDecimal("0.15") // <= longThreshold
        fastdValues[size - 2] = BigDecimal("0.15") // <= longThreshold
        fastkValues[size - 1] = BigDecimal("0.15") // <= longThreshold
        fastdValues[size - 1] = BigDecimal("0.15") // <= longThreshold

        // Simulate Heikin-Ashi price crossing above the 200 EMA
        closeHValues[crossoverIndex -1] = BigDecimal("99")  // i=10, closeH=99 < ema200=100
        closeHValues[crossoverIndex] = BigDecimal("101")  // i=11, closeH=101 > ema200=100
        closeHValues[size - 1] = BigDecimal("102")  // i=14, closeH=102 > openStreamH=100

        openStreamHValues[size -1] = BigDecimal("100")

        // **Crucial Correction:** Set closeH[12] = 101 > ema200[12] = 100
        closeHValues[12] = BigDecimal("101") // currentIndex -2 =12 < ema200=100 ?
        // Wait, for LONG signal:
        // closeH[currentIndex -2] < ema200[currentIndex -2] =100
        // Thus, set closeH[12]=99 <100

        closeHValues[12] = BigDecimal("99") // currentIndex -2=12 < ema200=100

        // Set closeH[13] =101 > ema200=100
        closeHValues[13] = BigDecimal("101") // currentIndex -1=13 > ema200=100

        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = -99,
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size -1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Long, result, "Expected a LONG signal when buy conditions are met.")
    }

    @Test
    fun testNoSignal() {
        // Scenario: No conditions for either Buy or Sell Signal are met
        val size = 15
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.5") }
        val fastkValues = MutableList(size) { BigDecimal("0.5") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        // No crossing occurs
        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = -99,
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size -1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, result, "Expected NO signal when conditions are not met.")
    }

    @Test
    fun testInsufficientData() {
        // Scenario: Insufficient data to evaluate the strategy
        val size = 3 // Less than required (currentIndex -4 needs at least 5 data points)
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.9") }
        val fastkValues = MutableList(size) { BigDecimal("0.9") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = -99,
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size -1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, result, "Expected NO signal due to insufficient data.")
    }

    @Test
    fun testEdgeCaseBuySignal() {
        // Scenario: Edge case where buy conditions are just met
        val size = 15
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.1") }
        val fastkValues = MutableList(size) { BigDecimal("0.1") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        // Simulate oversold conditions with fastk and fastd below 0.2 for the last 10 candles
        for (i in (size - 10) until (size - 4)) { // i=5..10
            closeHValues[i] = BigDecimal("98") // Below EMA200 initially
            openStreamHValues[i] = BigDecimal("99")
            fastkValues[i] = BigDecimal("0.1")
            fastdValues[i] = BigDecimal("0.1")
        }

        // Simulate a crossover at j=4 (i.e., currentIndex -4=11)
        val crossoverIndex = size - 4 //11
        fastkValues[crossoverIndex] = BigDecimal("0.15") // fastk < fastd
        fastdValues[crossoverIndex] = BigDecimal("0.25")

        // At crossoverIndex +1=12, fastk > fastd
        fastkValues[crossoverIndex + 1] = BigDecimal("0.25") // fastk > fastd=0.15
        fastdValues[crossoverIndex + 1] = BigDecimal("0.15")

        // After the crossover, fastk and fastd stay oversold
        fastkValues[size - 2] = BigDecimal("0.15") // <= longThreshold
        fastdValues[size - 2] = BigDecimal("0.15") // <= longThreshold
        fastkValues[size - 1] = BigDecimal("0.15") // <= longThreshold
        fastdValues[size - 1] = BigDecimal("0.15") // <= longThreshold

        // Simulate Heikin-Ashi price crossing above the 200 EMA
        closeHValues[crossoverIndex - 1] = BigDecimal("99")  // i=10, closeH=99 < ema200=100
        closeHValues[crossoverIndex] = BigDecimal("101")  // i=11, closeH=101 > ema200=100
        closeHValues[size - 1] = BigDecimal("102")  // i=14, closeH=102 > openStreamH=100

        openStreamHValues[size - 1] = BigDecimal("100")

        // **Crucial Correction:** Set closeH[12] =99 < ema200=100
        closeHValues[12] = BigDecimal("99") // currentIndex -2 =12 < ema200=100

        // Set closeH[13] =101 > ema200=100
        closeHValues[13] = BigDecimal("101") // currentIndex -1 =13 > ema200=100

        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = -99,
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size - 1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Long, result, "Expected a LONG signal when buy conditions are met.")
    }

    @Test
    fun testEdgeCaseSellSignal() {
        // Scenario: Edge case where sell conditions are just met
        val size = 15
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.9") }
        val fastkValues = MutableList(size) { BigDecimal("0.9") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        // Simulate overbought conditions with fastk and fastd above 0.8 for the last 10 candles
        for (i in (size - 10) until (size - 4)) { // i=5..10
            closeHValues[i] = BigDecimal("102") // Above EMA200 initially
            openStreamHValues[i] = BigDecimal("101")
            fastkValues[i] = BigDecimal("0.85")
            fastdValues[i] = BigDecimal("0.85")
        }

        // Simulate a crossover at j=4 (i.e., currentIndex -4=11)
        val crossoverIndex = size -4 //11
        fastkValues[crossoverIndex] = BigDecimal("0.85") // fastk > fastd
        fastdValues[crossoverIndex] = BigDecimal("0.80")

        // At crossoverIndex +1=12, fastk < fastd
        fastkValues[crossoverIndex +1] = BigDecimal("0.75") // fastk < fastd=0.85
        fastdValues[crossoverIndex +1] = BigDecimal("0.85")

        // After the crossover, fastk and fastd stay overbought
        fastkValues[size - 2] = BigDecimal("0.85") // >= shortThreshold
        fastdValues[size - 2] = BigDecimal("0.85") // >= shortThreshold
        fastkValues[size - 1] = BigDecimal("0.85") // >= shortThreshold
        fastdValues[size - 1] = BigDecimal("0.85") // >= shortThreshold

        // Simulate Heikin-Ashi price crossing below the 200 EMA
        closeHValues[crossoverIndex -1] = BigDecimal("101")  // i=10, closeH=101 > ema200=100
        closeHValues[crossoverIndex] = BigDecimal("98")  // i=11, closeH=98 < ema200=100
        closeHValues[size - 1] = BigDecimal("97")  // i=14, closeH=97 < openStreamH=99

        openStreamHValues[size -1] = BigDecimal("99")

        // **Crucial Correction:** Set closeH[12] =101 > ema200=100
        closeHValues[12] = BigDecimal("101") // currentIndex -2 =12 > ema200=100

        // Set closeH[13] =98 < ema200=100
        closeHValues[13] = BigDecimal("98") // currentIndex -1 =13 < ema200=100

        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = -99,
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size -1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Short, result, "Expected a SHORT signal at the edge case for sell conditions.")
    }

    @Test
    fun testRapidChangesToLongSignal() {
        // Scenario: Rapid changes leading to a Long Signal
        val size = 15
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.1") }
        val fastkValues = MutableList(size) { BigDecimal("0.1") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        // Simulate oversold conditions with fastk and fastd below 0.2 for the last 10 candles
        for (i in (size - 10) until (size - 4)) { // i=5..10
            closeHValues[i] = BigDecimal("98") // Below EMA200 initially
            openStreamHValues[i] = BigDecimal("99")
            fastkValues[i] = BigDecimal("0.1")
            fastdValues[i] = BigDecimal("0.1")
        }

        // Simulate a crossover at j=4 (i.e., currentIndex -4=11)
        val crossoverIndex = size -4 //11
        fastkValues[crossoverIndex] = BigDecimal("0.15") // fastk < fastd
        fastdValues[crossoverIndex] = BigDecimal("0.25")

        // At crossoverIndex +1=12, fastk > fastd to simulate the crossover
        fastkValues[crossoverIndex +1] = BigDecimal("0.25") // fastk > fastd=0.15
        fastdValues[crossoverIndex +1] = BigDecimal("0.15")

        // After the crossover, fastk and fastd stay oversold (below 0.2)
        fastkValues[size - 2] = BigDecimal("0.15") // <= longThreshold
        fastdValues[size - 2] = BigDecimal("0.15") // <= longThreshold
        fastkValues[size - 1] = BigDecimal("0.15") // <= longThreshold
        fastdValues[size -1] = BigDecimal("0.15") // <= longThreshold

        // Simulate Heikin-Ashi price crossing above the 200 EMA
        closeHValues[crossoverIndex -1] = BigDecimal("99")  // i=10, closeH=99 < ema200=100
        closeHValues[crossoverIndex] = BigDecimal("101")    // i=11, closeH=101 > ema200=100
        closeHValues[size -1] = BigDecimal("102")           // i=14, closeH=102 > openStreamH=100 (bullish candle)

        openStreamHValues[size -1] = BigDecimal("100")

        // Ensure that closeH[12] =99 < ema200=100
        closeHValues[12] = BigDecimal("99") // currentIndex -2 =12 < ema200=100

        // Set closeH[13] =101 > ema200=100
        closeHValues[13] = BigDecimal("101") // currentIndex -1 =13 > ema200=100

        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = -99,
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size -1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Long, result, "Expected a LONG signal after rapid changes leading to buy conditions.")
    }

    @Test
    fun testRapidChangesToShortSignal() {
        // Scenario: Rapid changes leading to a Short Signal
        val size = 15
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.85") }
        val fastkValues = MutableList(size) { BigDecimal("0.85") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        // Simulate overbought conditions with fastk and fastd above 0.8 for the last 10 candles
        for (i in (size - 10) until (size - 4)) { // i=5..10
            closeHValues[i] = BigDecimal("102") // Above EMA200 initially
            openStreamHValues[i] = BigDecimal("101")
            fastkValues[i] = BigDecimal("0.85")
            fastdValues[i] = BigDecimal("0.85")
        }

        // Simulate a crossover at j=4 (i.e., currentIndex -4=11)
        val crossoverIndex = size - 4 //11
        fastkValues[crossoverIndex] = BigDecimal("0.85") // fastk > fastd
        fastdValues[crossoverIndex] = BigDecimal("0.80")

        // At crossoverIndex +1=12, fastk < fastd
        fastkValues[crossoverIndex + 1] = BigDecimal("0.75") // fastk < fastd=0.85
        fastdValues[crossoverIndex + 1] = BigDecimal("0.85")

        // After the crossover, fastk and fastd stay overbought
        fastkValues[size - 2] = BigDecimal("0.85") // >= shortThreshold
        fastdValues[size - 2] = BigDecimal("0.85") // >= shortThreshold
        fastkValues[size - 1] = BigDecimal("0.85") // >= shortThreshold
        fastdValues[size - 1] = BigDecimal("0.85") // >= shortThreshold

        // Simulate Heikin-Ashi price crossing below the 200 EMA
        closeHValues[crossoverIndex -1] = BigDecimal("101")  // i=10, closeH=101 > ema200=100
        closeHValues[crossoverIndex] = BigDecimal("98")  // i=11, closeH=98 < ema200=100
        closeHValues[size - 1] = BigDecimal("97")  // i=14, closeH=97 < openStreamH=99

        openStreamHValues[size - 1] = BigDecimal("99")

        // **Crucial Correction:** Set closeH[12] =101 > ema200=100
        closeHValues[12] = BigDecimal("101") // currentIndex -2 =12 > ema200=100

        // Set closeH[13] =98 < ema200=100
        closeHValues[13] = BigDecimal("98") // currentIndex -1 =13 < ema200=100

        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = -99,
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size -1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Short, result, "Expected a SHORT signal after rapid changes leading to sell conditions.")
    }

    @Test
    fun testClosePosition() {
        // Scenario: Close existing position based on Heikin-Ashi candle
        val size = 15
        val openStreamHValues = MutableList(size) { BigDecimal("100") }
        val closeHValues = MutableList(size) { BigDecimal("100") }
        val fastdValues = MutableList(size) { BigDecimal("0.5") }
        val fastkValues = MutableList(size) { BigDecimal("0.5") }
        val ema200Values = MutableList(size) { BigDecimal("100") }

        // Assume currentPos is 1 and the latest candle is bearish
        closeHValues[size - 1] = BigDecimal("94") // < openStreamH[size -1] =100

        val data = generateTestData(
            openStreamHValues,
            closeHValues,
            fastdValues,
            fastkValues,
            ema200Values
        )

        val strategy = HeikinAshiEma(
            openStreamH = data["openStreamH"]!!,
            closeH = data["closeH"]!!,
            currentPos = 1, // Existing position
            closePos = 0,
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            ema200 = data["ema200"]!!,
            currentIndex = size -1
        )

        val result = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, result, "Expected NOTHING when closing a SHORT position.")
    }

}