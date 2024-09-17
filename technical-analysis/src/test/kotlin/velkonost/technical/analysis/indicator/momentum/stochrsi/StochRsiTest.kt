package velkonost.technical.analysis.indicator.momentum.stochrsi

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class StochRsiTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = StochRsi(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}