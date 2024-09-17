package velkonost.technical.analysis.indicator.trend.ema

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class EmaSlowTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = EmaSlow(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}