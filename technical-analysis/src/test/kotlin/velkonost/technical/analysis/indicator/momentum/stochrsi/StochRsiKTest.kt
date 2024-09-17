package velkonost.technical.analysis.indicator.momentum.stochrsi

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class StochRsiKTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = StochRsiK(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}