package velkonost.technical.analysis.indicator.trend.sma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class SmaFastTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = SmaFast(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}