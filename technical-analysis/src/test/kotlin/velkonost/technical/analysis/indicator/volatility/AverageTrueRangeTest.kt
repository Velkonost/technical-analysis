package velkonost.technical.analysis.indicator.volatility

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class AverageTrueRangeTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = AverageTrueRange(
                high = highColumn,
                close = closeColumn,
                low = lowColumn,
                window = 10,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}