package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.HeikinAshiEma2
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class HeikinAshiEma2Test {
    @Test
    fun testLongSignalGenerated() {
        val size = 200
        val currentIndex = size - 1
        val currentPos = -99  // No current position

        // Initialize data columns
        val openStreamH = MutableList(size) { BigDecimal("100") }
        val highH = MutableList(size) { BigDecimal("105") }
        val lowH = MutableList(size) { BigDecimal("95") }
        val closeH = MutableList(size) { BigDecimal("100") }
        val fastd = MutableList(size) { BigDecimal("0.25") }  // Below longThreshold
        val fastk = MutableList(size) { BigDecimal("0.25") }  // Below longThreshold
        val ema200 = MutableList(size) { BigDecimal("90") }  // Default EMA200

        // Simulate stochastic K crossing above D
        fastk[currentIndex - 1] = BigDecimal("0.25")
        fastd[currentIndex - 1] = BigDecimal("0.28")
        fastk[currentIndex] = BigDecimal("0.29")
        fastd[currentIndex] = BigDecimal("0.28")

        // Ensure price is above EMA200
        closeH[currentIndex] = BigDecimal("105")
        ema200[currentIndex] = BigDecimal("100")

        // Simulate a Bullish Meta Candle at idxI
        val idxI = currentIndex - 5  // idxI = 199 - 5 = 194
        closeH[idxI] = BigDecimal("101")
        openStreamH[idxI] = BigDecimal("100")
        lowH[idxI] = openStreamH[idxI]

        // Adjust EMA200 to satisfy condition ema200[idxJ] > closeH[idxJ]
        val idxJ = idxI
        ema200[idxJ] = BigDecimal("102")
        closeH[idxJ + 1] = BigDecimal("103")
        ema200[idxJ + 1] = BigDecimal("101")

        // Ensure fastd and fastk are below longThreshold
        for (r in idxJ downTo idxJ - 5) {
            if (r >= 0) {
                fastd[r] = BigDecimal("0.25")
                fastk[r] = BigDecimal("0.25")
            }
        }

        // Create DataColumns
        val openStreamHColumn = DataColumn.create("openStreamH", openStreamH)
        val highHColumn = DataColumn.create("highH", highH)
        val lowHColumn = DataColumn.create("lowH", lowH)
        val closeHColumn = DataColumn.create("closeH", closeH)
        val fastdColumn = DataColumn.create("fastd", fastd)
        val fastkColumn = DataColumn.create("fastk", fastk)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = HeikinAshiEma2(
            openStreamH = openStreamHColumn,
            highH = highHColumn,
            lowH = lowHColumn,
            closeH = closeHColumn,
            currentPos = currentPos,
            fastd = fastdColumn,
            fastk = fastkColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert that a LONG signal is generated
        assertEquals(StrategyDecision.Long, result, "Expected a LONG trade direction.")
    }

    @Test
    fun testShortSignalGenerated() {
        val size = 200
        val currentIndex = size - 1
        val currentPos = -99  // No current position

        // Initialize data columns
        val openStreamH = MutableList(size) { BigDecimal("100") }
        val highH = MutableList(size) { BigDecimal("105") }
        val lowH = MutableList(size) { BigDecimal("95") }
        val closeH = MutableList(size) { BigDecimal("100") }
        val fastd = MutableList(size) { BigDecimal("0.8") }  // Above shortThreshold
        val fastk = MutableList(size) { BigDecimal("0.8") }  // Above shortThreshold
        val ema200 = MutableList(size) { BigDecimal("110") } // Default EMA200

        // Simulate stochastic K crossing below D
        fastk[currentIndex - 1] = BigDecimal("0.85")
        fastd[currentIndex - 1] = BigDecimal("0.8")
        fastk[currentIndex] = BigDecimal("0.75")
        fastd[currentIndex] = BigDecimal("0.8")

        // Ensure price is below EMA200
        closeH[currentIndex] = BigDecimal("105")
        ema200[currentIndex] = BigDecimal("110")

        // Simulate a Bearish Meta Candle
        val idxI = currentIndex - 5  // idxI = 199 - 5 = 194
        closeH[idxI] = BigDecimal("109")
        openStreamH[idxI] = BigDecimal("110")
        highH[idxI] = openStreamH[idxI]

        // Adjust EMA200 to satisfy condition ema200[idxJ] < closeH[idxJ]
        val idxJ = idxI
        ema200[idxJ] = BigDecimal("108")  // Adjusted ema200[194]
        closeH[idxJ + 1] = BigDecimal("108")
        ema200[idxJ + 1] = BigDecimal("110")

        // Ensure fastd and fastk are above shortThreshold
        for (r in idxJ downTo idxJ - 5) {
            if (r >= 0) {
                fastd[r] = BigDecimal("0.75")
                fastk[r] = BigDecimal("0.75")
            }
        }

        // Create DataColumns
        val openStreamHColumn = DataColumn.create("openStreamH", openStreamH)
        val highHColumn = DataColumn.create("highH", highH)
        val lowHColumn = DataColumn.create("lowH", lowH)
        val closeHColumn = DataColumn.create("closeH", closeH)
        val fastdColumn = DataColumn.create("fastd", fastd)
        val fastkColumn = DataColumn.create("fastk", fastk)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = HeikinAshiEma2(
            openStreamH = openStreamHColumn,
            highH = highHColumn,
            lowH = lowHColumn,
            closeH = closeHColumn,
            currentPos = currentPos,
            fastd = fastdColumn,
            fastk = fastkColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert that a SHORT signal is generated
        assertEquals(StrategyDecision.Short, result, "Expected a SHORT trade direction.")
    }

    @Test
    fun testNoSignalGenerated() {
        val size = 200
        val currentIndex = size - 1
        val currentPos = -99  // No current position

        // Initialize data columns
        val openStreamH = MutableList(size) { BigDecimal("100") }
        val highH = MutableList(size) { BigDecimal("105") }
        val lowH = MutableList(size) { BigDecimal("95") }
        val closeH = MutableList(size) { BigDecimal("100") }
        val fastd = MutableList(size) { BigDecimal("0.5") }
        val fastk = MutableList(size) { BigDecimal("0.5") }
        val ema200 = MutableList(size) { BigDecimal("100") }

        // No stochastic crossover
        fastk[currentIndex - 1] = BigDecimal("0.5")
        fastd[currentIndex - 1] = BigDecimal("0.5")
        fastk[currentIndex] = BigDecimal("0.5")
        fastd[currentIndex] = BigDecimal("0.5")

        // Price equal to EMA200
        closeH[currentIndex] = BigDecimal("100")
        ema200[currentIndex] = BigDecimal("100")

        // Create DataColumns
        val openStreamHColumn = DataColumn.create("openStreamH", openStreamH)
        val highHColumn = DataColumn.create("highH", highH)
        val lowHColumn = DataColumn.create("lowH", lowH)
        val closeHColumn = DataColumn.create("closeH", closeH)
        val fastdColumn = DataColumn.create("fastd", fastd)
        val fastkColumn = DataColumn.create("fastk", fastk)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = HeikinAshiEma2(
            openStreamH = openStreamHColumn,
            highH = highHColumn,
            lowH = lowHColumn,
            closeH = closeHColumn,
            currentPos = currentPos,
            fastd = fastdColumn,
            fastk = fastkColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert that no trade signal is generated
        assertEquals(StrategyDecision.Nothing, result, "Expected no trade direction.")
    }

    @Test
    fun testCloseLongPosition() {
        val size = 200
        val currentIndex = size - 1
        val currentPos = 1  // Currently in a LONG position

        // Initialize data columns
        val openStreamH = MutableList(size) { BigDecimal("100") }
        val closeH = MutableList(size) { BigDecimal("105") }

        // Simulate price dropping (closeH < openStreamH)
        openStreamH[currentIndex] = BigDecimal("105")
        closeH[currentIndex] = BigDecimal("100")

        // Create DataColumns
        val openStreamHColumn = DataColumn.create("openStreamH", openStreamH)
        val closeHColumn = DataColumn.create("closeH", closeH)

        // Initialize the strategy
        val strategy = HeikinAshiEma2(
            openStreamH = openStreamHColumn,
            highH = DataColumn.create("highH", MutableList(size) { BigDecimal.ZERO }),
            lowH = DataColumn.create("lowH", MutableList(size) { BigDecimal.ZERO }),
            closeH = closeHColumn,
            currentPos = currentPos,
            fastd = DataColumn.create("fastd", MutableList(size) { BigDecimal.ZERO }),
            fastk = DataColumn.create("fastk", MutableList(size) { BigDecimal.ZERO }),
            ema200 = DataColumn.create("ema200", MutableList(size) { BigDecimal.ZERO }),
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert that position should be closed
        assertEquals(StrategyDecision.Nothing, result, "Expected no new trade direction.")
    }

    @Test
    fun testCloseShortPosition() {
        val size = 200
        val currentIndex = size - 1
        val currentPos = 0  // Currently in a SHORT position

        // Initialize data columns
        val openStreamH = MutableList(size) { BigDecimal("105") }
        val closeH = MutableList(size) { BigDecimal("100") }

        // Simulate price rising (closeH > openStreamH)
        openStreamH[currentIndex] = BigDecimal("100")
        closeH[currentIndex] = BigDecimal("105")

        // Create DataColumns
        val openStreamHColumn = DataColumn.create("openStreamH", openStreamH)
        val closeHColumn = DataColumn.create("closeH", closeH)

        // Initialize the strategy
        val strategy = HeikinAshiEma2(
            openStreamH = openStreamHColumn,
            highH = DataColumn.create("highH", MutableList(size) { BigDecimal.ZERO }),
            lowH = DataColumn.create("lowH", MutableList(size) { BigDecimal.ZERO }),
            closeH = closeHColumn,
            currentPos = currentPos,
            fastd = DataColumn.create("fastd", MutableList(size) { BigDecimal.ZERO }),
            fastk = DataColumn.create("fastk", MutableList(size) { BigDecimal.ZERO }),
            ema200 = DataColumn.create("ema200", MutableList(size) { BigDecimal.ZERO }),
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert that position should be closed
        assertEquals(StrategyDecision.Nothing, result, "Expected no new trade direction.")
    }

    @Test
    fun testInsufficientData() {
        val size = 5  // Not enough data for the strategy to function properly
        val currentIndex = size - 1
        val currentPos = -99  // No current position

        // Initialize data columns with minimal data
        val openStreamH = MutableList(size) { BigDecimal("100") }
        val highH = MutableList(size) { BigDecimal("105") }
        val lowH = MutableList(size) { BigDecimal("95") }
        val closeH = MutableList(size) { BigDecimal("100") }
        val fastd = MutableList(size) { BigDecimal("0.5") }
        val fastk = MutableList(size) { BigDecimal("0.5") }
        val ema200 = MutableList(size) { BigDecimal("100") }

        // Create DataColumns
        val openStreamHColumn = DataColumn.create("openStreamH", openStreamH)
        val highHColumn = DataColumn.create("highH", highH)
        val lowHColumn = DataColumn.create("lowH", lowH)
        val closeHColumn = DataColumn.create("closeH", closeH)
        val fastdColumn = DataColumn.create("fastd", fastd)
        val fastkColumn = DataColumn.create("fastk", fastk)
        val ema200Column = DataColumn.create("ema200", ema200)

        // Initialize the strategy
        val strategy = HeikinAshiEma2(
            openStreamH = openStreamHColumn,
            highH = highHColumn,
            lowH = lowHColumn,
            closeH = closeHColumn,
            currentPos = currentPos,
            fastd = fastdColumn,
            fastk = fastkColumn,
            ema200 = ema200Column,
            currentIndex = currentIndex
        )

        // Calculate the result
        val result = strategy.calculate()

        // Assert that no trade signal is generated due to insufficient data
        assertEquals(StrategyDecision.Nothing, result, "Expected no trade direction due to insufficient data.")
    }


}