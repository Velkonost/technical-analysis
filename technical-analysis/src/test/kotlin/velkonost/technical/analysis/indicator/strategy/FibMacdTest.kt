package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.FibMacd
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class FibMacdTest {

    @Test
    fun testUptrendWithValidLongSignal() {
        val size = 200
        val currentIndex = size - 1
        val period = 100

        // Initialize data columns
        val close = MutableList(size) { BigDecimal("150") }
        val open = MutableList(size) { BigDecimal("150") }
        val high = MutableList(size) { BigDecimal("153") }
        val low = MutableList(size) { BigDecimal("147") }
        val macdSignal = MutableList(size) { BigDecimal("0") }
        val macd = MutableList(size) { BigDecimal("0") }
        val ema200 = MutableList(size) { BigDecimal("140") }

        // Simulate an uptrend with minimal fluctuations
        for (i in period until size) {
            close[i] = close[i - 1] + BigDecimal("1")  // Uptrend
            open[i] = close[i - 1]
            high[i] = close[i] + BigDecimal("2")
            low[i] = close[i] - BigDecimal("2")
            ema200[i] = ema200[i - 1] + BigDecimal("0.5")
        }

        // Introduce a clear trough at index 110
        low[110] = BigDecimal("130")   // Lower than surrounding lows
        high[110] = close[110] + BigDecimal("2")
        close[110] = low[110] + BigDecimal("2")
        open[110] = low[110] + BigDecimal("1")

        // Adjust surrounding lows
        low[108] = BigDecimal("135")
        low[109] = BigDecimal("134")
        low[111] = BigDecimal("134")
        low[112] = BigDecimal("135")

        // Introduce a peak at index 120
        high[120] = BigDecimal("170")  // Higher than surrounding highs
        low[120] = close[120] - BigDecimal("2")
        close[120] = high[120] - BigDecimal("2")
        open[120] = high[120] - BigDecimal("3")

        // Adjust surrounding highs
        high[118] = BigDecimal("165")
        high[119] = BigDecimal("166")
        high[121] = BigDecimal("166")
        high[122] = BigDecimal("165")

        // Simulate MACD crossover upwards
        macdSignal[currentIndex - 2] = BigDecimal("-0.5")
        macd[currentIndex - 2] = BigDecimal("-1")
        macdSignal[currentIndex - 1] = BigDecimal("0")
        macd[currentIndex - 1] = BigDecimal("-0.5")
        macdSignal[currentIndex] = BigDecimal("0.5")
        macd[currentIndex] = BigDecimal("1")

        // Simulate a Bullish Engulfing Candle
        close[currentIndex - 2] = BigDecimal("190")
        open[currentIndex - 2] = BigDecimal("195")
        close[currentIndex - 1] = BigDecimal("200")
        open[currentIndex - 1] = BigDecimal("190")
        close[currentIndex] = BigDecimal("210")
        open[currentIndex] = BigDecimal("200")

        // Adjust high and low for the engulfing candles
        high[currentIndex - 2] = close[currentIndex - 2] + BigDecimal("2")
        low[currentIndex - 2] = close[currentIndex - 2] - BigDecimal("2")
        high[currentIndex - 1] = close[currentIndex - 1] + BigDecimal("2")
        low[currentIndex - 1] = close[currentIndex - 1] - BigDecimal("2")
        high[currentIndex] = close[currentIndex] + BigDecimal("2")
        low[currentIndex] = close[currentIndex] - BigDecimal("2")

        // Initialize DataColumns
        val closeColumn = DataColumn.create("close", close)
        val openColumn = DataColumn.create("open", open)
        val highColumn = DataColumn.create("high", high)
        val lowColumn = DataColumn.create("low", low)
        val macdSignalColumn = DataColumn.create("macdSignal", macdSignal)
        val macdColumn = DataColumn.create("macd", macd)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = FibMacd(
            close = closeColumn,
            open = openColumn,
            high = highColumn,
            low = lowColumn,
            macdSignal = macdSignalColumn,
            macd = macdColumn,
            ema200 = ema200Column,
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert the expected outcome
        assertEquals(StrategyDecision.Long, result, "Expected a LONG trade direction.")
    }

    @Test
    fun testDowntrendWithValidShortSignal() {
        // Scenario: Downtrend with a valid SHORT signal

        val size = 200
        val currentIndex = size - 1
        val period = 100

        // Initialize data columns
        val close = MutableList(size) { BigDecimal("200") }
        val open = MutableList(size) { BigDecimal("200") }
        val high = MutableList(size) { BigDecimal("200") }
        val low = MutableList(size) { BigDecimal("200") }
        val macdSignal = MutableList(size) { BigDecimal("0") }
        val macd = MutableList(size) { BigDecimal("0") }
        val ema200 = MutableList(size) { BigDecimal("205") } // EMA200 above price for downtrend

        // Simulate a downtrend
        for (i in period until size) {
            close[i] = close[i - 1] - BigDecimal("2")
            open[i] = close[i - 1]
            high[i] = close[i] + BigDecimal("3")
            low[i] = close[i] - BigDecimal("3")
            ema200[i] = ema200[i - 1] - BigDecimal("0.5")
        }

        // Introduce a peak at index 110
        high[110] = BigDecimal("220") // Higher than surrounding highs
        low[110] = close[110] - BigDecimal("2")
        close[110] = high[110] - BigDecimal("2")
        open[110] = high[110] - BigDecimal("3")

        // Introduce a trough at index 120
        low[120] = BigDecimal("180") // Lower than surrounding lows
        high[120] = close[120] + BigDecimal("2")
        close[120] = low[120] + BigDecimal("2")
        open[120] = low[120] + BigDecimal("1")

        // Adjust MACD values for crossover downwards
        macdSignal[currentIndex - 2] = BigDecimal("1.5")
        macd[currentIndex - 2] = BigDecimal("2")
        macdSignal[currentIndex - 1] = BigDecimal("1")
        macd[currentIndex - 1] = BigDecimal("1.5")
        macdSignal[currentIndex] = BigDecimal("1")
        macd[currentIndex] = BigDecimal("0.5") // Adjusted to be less than macdSignal

        // Simulate a Bearish Engulfing Candle
        close[currentIndex - 2] = BigDecimal("60")
        open[currentIndex - 2] = BigDecimal("55")
        close[currentIndex - 1] = BigDecimal("50")
        open[currentIndex - 1] = BigDecimal("60")
        close[currentIndex] = BigDecimal("40")
        open[currentIndex] = BigDecimal("50")

        // Adjust high and low for the engulfing candles
        high[currentIndex - 2] = close[currentIndex - 2] + BigDecimal("2")
        low[currentIndex - 2] = close[currentIndex - 2] - BigDecimal("2")
        high[currentIndex - 1] = close[currentIndex - 1] + BigDecimal("2")
        low[currentIndex - 1] = close[currentIndex - 1] - BigDecimal("2")
        high[currentIndex] = close[currentIndex] + BigDecimal("2")
        low[currentIndex] = close[currentIndex] - BigDecimal("2")

        // Initialize DataColumns
        val closeColumn = DataColumn.create("close", close)
        val openColumn = DataColumn.create("open", open)
        val highColumn = DataColumn.create("high", high)
        val lowColumn = DataColumn.create("low", low)
        val macdSignalColumn = DataColumn.create("macdSignal", macdSignal)
        val macdColumn = DataColumn.create("macd", macd)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = FibMacd(
            close = closeColumn,
            open = openColumn,
            high = highColumn,
            low = lowColumn,
            macdSignal = macdSignalColumn,
            macd = macdColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert the expected outcome
        assertEquals(StrategyDecision.Short, result, "Expected a SHORT trade direction.")
    }

    @Test
    fun testDowntrendWithoutSignal() {
        // Scenario: Downtrend but conditions for SHORT signal are not met

        val size = 200
        val currentIndex = size - 1
        val period = 100

        // Initialize data columns
        val close = MutableList(size) { BigDecimal("200") }
        val open = MutableList(size) { BigDecimal("200") }
        val high = MutableList(size) { BigDecimal("200") }
        val low = MutableList(size) { BigDecimal("200") }
        val macdSignal = MutableList(size) { BigDecimal("0") }
        val macd = MutableList(size) { BigDecimal("0") }
        val ema200 = MutableList(size) { BigDecimal("205") }

        // Simulate a downtrend
        for (i in period until size) {
            close[i] = close[i - 1] - BigDecimal("1")
            open[i] = close[i - 1]
            high[i] = close[i] + BigDecimal("2")
            low[i] = close[i] - BigDecimal("2")
            ema200[i] = ema200[i - 1] - BigDecimal("0.5")
        }

        // No MACD crossover
        macdSignal[currentIndex - 2] = BigDecimal("0.5")
        macd[currentIndex - 2] = BigDecimal("0.5")
        macdSignal[currentIndex - 1] = BigDecimal("0.5")
        macd[currentIndex - 1] = BigDecimal("0.5")
        macdSignal[currentIndex] = BigDecimal("0.5")
        macd[currentIndex] = BigDecimal("0.5")

        // No Bearish Engulfing Candle
        close[currentIndex - 2] = BigDecimal("50")
        open[currentIndex - 2] = BigDecimal("55")
        close[currentIndex - 1] = BigDecimal("55")
        open[currentIndex - 1] = BigDecimal("50")
        close[currentIndex] = BigDecimal("55")
        open[currentIndex] = BigDecimal("50")

        // Initialize DataColumns
        val closeColumn = DataColumn.create("close", close)
        val openColumn = DataColumn.create("open", open)
        val highColumn = DataColumn.create("high", high)
        val lowColumn = DataColumn.create("low", low)
        val macdSignalColumn = DataColumn.create("macdSignal", macdSignal)
        val macdColumn = DataColumn.create("macd", macd)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = FibMacd(
            close = closeColumn,
            open = openColumn,
            high = highColumn,
            low = lowColumn,
            macdSignal = macdSignalColumn,
            macd = macdColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert the expected outcome
        assertEquals(StrategyDecision.Nothing, result, "Expected no trade signal.")
    }

    @Test
    fun testInsufficientData() {
        // Scenario: Insufficient data to calculate peaks and troughs

        val size = 50 // Less than the required period
        val currentIndex = size - 1
        val period = 100

        // Initialize data columns
        val close = MutableList(size) { BigDecimal("100") }
        val open = MutableList(size) { BigDecimal("100") }
        val high = MutableList(size) { BigDecimal("100") }
        val low = MutableList(size) { BigDecimal("100") }
        val macdSignal = MutableList(size) { BigDecimal("0") }
        val macd = MutableList(size) { BigDecimal("0") }
        val ema200 = MutableList(size) { BigDecimal("95") }

        // Initialize DataColumns
        val closeColumn = DataColumn.create("close", close)
        val openColumn = DataColumn.create("open", open)
        val highColumn = DataColumn.create("high", high)
        val lowColumn = DataColumn.create("low", low)
        val macdSignalColumn = DataColumn.create("macdSignal", macdSignal)
        val macdColumn = DataColumn.create("macd", macd)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = FibMacd(
            close = closeColumn,
            open = openColumn,
            high = highColumn,
            low = lowColumn,
            macdSignal = macdSignalColumn,
            macd = macdColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert the expected outcome
        assertEquals(StrategyDecision.Nothing, result, "Expected no trade signal due to insufficient data.")
    }

    @Test
    fun testNoMacdCrossover() {
        // Scenario: Uptrend with all conditions met except for the MACD crossover

        val size = 200
        val currentIndex = size - 1
        val period = 100

        // Initialize data columns
        val close = MutableList(size) { BigDecimal("150") }
        val open = MutableList(size) { BigDecimal("150") }
        val high = MutableList(size) { BigDecimal("153") }
        val low = MutableList(size) { BigDecimal("147") }
        val macdSignal = MutableList(size) { BigDecimal("0") }
        val macd = MutableList(size) { BigDecimal("0") }
        val ema200 = MutableList(size) { BigDecimal("140") }

        // Simulate an uptrend
        for (i in period until size) {
            close[i] = close[i - 1] + BigDecimal("1")
            open[i] = close[i - 1]
            high[i] = close[i] + BigDecimal("2")
            low[i] = close[i] - BigDecimal("2")
            ema200[i] = ema200[i - 1] + BigDecimal("0.5")
        }

        // Introduce a clear trough before the peak
        low[110] = BigDecimal("130")
        high[110] = close[110] + BigDecimal("2")
        close[110] = low[110] + BigDecimal("2")
        open[110] = low[110] + BigDecimal("1")

        // Adjust surrounding lows
        low[108] = BigDecimal("135")
        low[109] = BigDecimal("134")
        low[111] = BigDecimal("134")
        low[112] = BigDecimal("135")

        // Introduce a peak at index 120
        high[120] = BigDecimal("170")
        low[120] = close[120] - BigDecimal("2")
        close[120] = high[120] - BigDecimal("2")
        open[120] = high[120] - BigDecimal("3")

        // Adjust surrounding highs
        high[118] = BigDecimal("165")
        high[119] = BigDecimal("166")
        high[121] = BigDecimal("166")
        high[122] = BigDecimal("165")

        // Ensure recent low is between Fibonacci levels
        low[currentIndex - 2] = BigDecimal("175") // Between Fibonacci levels

        // Do NOT simulate MACD crossover upwards
        macdSignal[currentIndex - 2] = BigDecimal("-0.5")
        macd[currentIndex - 2] = BigDecimal("-0.5")
        macdSignal[currentIndex - 1] = BigDecimal("0")
        macd[currentIndex - 1] = BigDecimal("0")
        macdSignal[currentIndex] = BigDecimal("0.5")
        macd[currentIndex] = BigDecimal("0.5") // MACD equal to signal line

        // Simulate a Bullish Engulfing Candle
        close[currentIndex - 2] = BigDecimal("190")
        open[currentIndex - 2] = BigDecimal("195")
        close[currentIndex - 1] = BigDecimal("200")
        open[currentIndex - 1] = BigDecimal("190")
        close[currentIndex] = BigDecimal("210")
        open[currentIndex] = BigDecimal("200")

        // Adjust high and low for the engulfing candles
        high[currentIndex - 2] = close[currentIndex - 2] + BigDecimal("2")
        low[currentIndex - 2] = close[currentIndex - 2] - BigDecimal("2")
        high[currentIndex - 1] = close[currentIndex - 1] + BigDecimal("2")
        low[currentIndex - 1] = close[currentIndex - 1] - BigDecimal("2")
        high[currentIndex] = close[currentIndex] + BigDecimal("2")
        low[currentIndex] = close[currentIndex] - BigDecimal("2")

        // Initialize DataColumns
        val closeColumn = DataColumn.create("close", close)
        val openColumn = DataColumn.create("open", open)
        val highColumn = DataColumn.create("high", high)
        val lowColumn = DataColumn.create("low", low)
        val macdSignalColumn = DataColumn.create("macdSignal", macdSignal)
        val macdColumn = DataColumn.create("macd", macd)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = FibMacd(
            close = closeColumn,
            open = openColumn,
            high = highColumn,
            low = lowColumn,
            macdSignal = macdSignalColumn,
            macd = macdColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert the expected outcome
        assertEquals(StrategyDecision.Nothing, result, "Expected no trade signal due to lack of MACD crossover.")
    }

    @Test
    fun testNoEngulfingPattern() {
        // Scenario: Uptrend with all conditions met except for the bullish engulfing pattern

        val size = 200
        val currentIndex = size - 1
        val period = 100

        // Initialize data columns
        val close = MutableList(size) { BigDecimal("150") }
        val open = MutableList(size) { BigDecimal("150") }
        val high = MutableList(size) { BigDecimal("153") }
        val low = MutableList(size) { BigDecimal("147") }
        val macdSignal = MutableList(size) { BigDecimal("0") }
        val macd = MutableList(size) { BigDecimal("0") }
        val ema200 = MutableList(size) { BigDecimal("140") }

        // Simulate an uptrend
        for (i in period until size) {
            close[i] = close[i - 1] + BigDecimal("1")
            open[i] = close[i - 1]
            high[i] = close[i] + BigDecimal("2")
            low[i] = close[i] - BigDecimal("2")
            ema200[i] = ema200[i - 1] + BigDecimal("0.5")
        }

        // Introduce a clear trough before the peak
        low[110] = BigDecimal("130")
        high[110] = close[110] + BigDecimal("2")
        close[110] = low[110] + BigDecimal("2")
        open[110] = low[110] + BigDecimal("1")

        // Adjust surrounding lows
        low[108] = BigDecimal("135")
        low[109] = BigDecimal("134")
        low[111] = BigDecimal("134")
        low[112] = BigDecimal("135")

        // Introduce a peak at index 120
        high[120] = BigDecimal("170")
        low[120] = close[120] - BigDecimal("2")
        close[120] = high[120] - BigDecimal("2")
        open[120] = high[120] - BigDecimal("3")

        // Adjust surrounding highs
        high[118] = BigDecimal("165")
        high[119] = BigDecimal("166")
        high[121] = BigDecimal("166")
        high[122] = BigDecimal("165")

        // Ensure recent low is between Fibonacci levels
        low[currentIndex - 2] = BigDecimal("175") // Between Fibonacci levels

        // Simulate MACD crossover upwards
        macdSignal[currentIndex - 2] = BigDecimal("-0.5")
        macd[currentIndex - 2] = BigDecimal("-1")
        macdSignal[currentIndex - 1] = BigDecimal("0")
        macd[currentIndex - 1] = BigDecimal("-0.5")
        macdSignal[currentIndex] = BigDecimal("0.5")
        macd[currentIndex] = BigDecimal("1") // Ensure MACD crossover up

        // Do NOT simulate a Bullish Engulfing Candle
        // Instead, create candles that do not satisfy the engulfing pattern
        close[currentIndex - 2] = BigDecimal("195")
        open[currentIndex - 2] = BigDecimal("190") // Bullish candle
        close[currentIndex - 1] = BigDecimal("190")
        open[currentIndex - 1] = BigDecimal("195") // Bearish candle
        close[currentIndex] = BigDecimal("200")
        open[currentIndex] = BigDecimal("190") // Bullish candle but no engulfing

        // Initialize DataColumns
        val closeColumn = DataColumn.create("close", close)
        val openColumn = DataColumn.create("open", open)
        val highColumn = DataColumn.create("high", high)
        val lowColumn = DataColumn.create("low", low)
        val macdSignalColumn = DataColumn.create("macdSignal", macdSignal)
        val macdColumn = DataColumn.create("macd", macd)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = FibMacd(
            close = closeColumn,
            open = openColumn,
            high = highColumn,
            low = lowColumn,
            macdSignal = macdSignalColumn,
            macd = macdColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert the expected outcome
        assertEquals(StrategyDecision.Nothing, result, "Expected no trade signal due to lack of engulfing pattern.")
    }

    @Test
    fun testFibonacciLevelsNotRespected() {
        // Scenario: Uptrend with conditions met except the recent low is not between Fibonacci levels

        val size = 200
        val currentIndex = size - 1
        val period = 100

        // Initialize data columns
        val close = MutableList(size) { BigDecimal("150") }
        val open = MutableList(size) { BigDecimal("150") }
        val high = MutableList(size) { BigDecimal("153") }
        val low = MutableList(size) { BigDecimal("147") }
        val macdSignal = MutableList(size) { BigDecimal("0") }
        val macd = MutableList(size) { BigDecimal("0") }
        val ema200 = MutableList(size) { BigDecimal("140") }

        // Simulate an uptrend
        for (i in period until size) {
            close[i] = close[i - 1] + BigDecimal("1")
            open[i] = close[i - 1]
            high[i] = close[i] + BigDecimal("2")
            low[i] = close[i] - BigDecimal("2")
            ema200[i] = ema200[i - 1] + BigDecimal("0.5")
        }

        // Introduce a clear trough before the peak
        low[110] = BigDecimal("130")
        high[110] = close[110] + BigDecimal("2")
        close[110] = low[110] + BigDecimal("2")
        open[110] = low[110] + BigDecimal("1")

        // Adjust surrounding lows
        low[108] = BigDecimal("135")
        low[109] = BigDecimal("134")
        low[111] = BigDecimal("134")
        low[112] = BigDecimal("135")

        // Introduce a peak at index 120
        high[120] = BigDecimal("170")
        low[120] = close[120] - BigDecimal("2")
        close[120] = high[120] - BigDecimal("2")
        open[120] = high[120] - BigDecimal("3")

        // Adjust surrounding highs
        high[118] = BigDecimal("165")
        high[119] = BigDecimal("166")
        high[121] = BigDecimal("166")
        high[122] = BigDecimal("165")

        // Simulate MACD crossover upwards
        macdSignal[currentIndex - 2] = BigDecimal("-0.5")
        macd[currentIndex - 2] = BigDecimal("-1")
        macdSignal[currentIndex - 1] = BigDecimal("0")
        macd[currentIndex - 1] = BigDecimal("-0.5")
        macdSignal[currentIndex] = BigDecimal("0.5")
        macd[currentIndex] = BigDecimal("1") // Ensure MACD crossover up

        // Simulate a Bullish Engulfing Candle
        close[currentIndex - 2] = BigDecimal("190")
        open[currentIndex - 2] = BigDecimal("195")
        close[currentIndex - 1] = BigDecimal("200")
        open[currentIndex - 1] = BigDecimal("190")
        close[currentIndex] = BigDecimal("210")
        open[currentIndex] = BigDecimal("200")

        // Adjust high and low for the engulfing candles
        high[currentIndex - 2] = close[currentIndex - 2] + BigDecimal("2")
        // Do NOT adjust low[currentIndex - 2] here
        high[currentIndex - 1] = close[currentIndex - 1] + BigDecimal("2")
        low[currentIndex - 1] = close[currentIndex - 1] - BigDecimal("2")
        high[currentIndex] = close[currentIndex] + BigDecimal("2")
        low[currentIndex] = close[currentIndex] - BigDecimal("2")

        // Now, set the low[currentIndex - 2] to 100, after adjustments
        low[currentIndex - 2] = BigDecimal("100") // Lower than any Fibonacci level

        // Initialize DataColumns
        val closeColumn = DataColumn.create("close", close)
        val openColumn = DataColumn.create("open", open)
        val highColumn = DataColumn.create("high", high)
        val lowColumn = DataColumn.create("low", low)
        val macdSignalColumn = DataColumn.create("macdSignal", macdSignal)
        val macdColumn = DataColumn.create("macd", macd)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = FibMacd(
            close = closeColumn,
            open = openColumn,
            high = highColumn,
            low = lowColumn,
            macdSignal = macdSignalColumn,
            macd = macdColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert the expected outcome
        assertEquals(
            StrategyDecision.Nothing,
            result,
            "Expected no trade signal due to Fibonacci levels not being respected."
        )
    }

}