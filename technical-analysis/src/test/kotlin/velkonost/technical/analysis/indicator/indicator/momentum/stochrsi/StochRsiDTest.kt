package velkonost.technical.analysis.indicator.indicator.momentum.stochrsi

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class StochRsiDTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = velkonost.technical.analysis.indicator.momentum.stochrsi.StochRsiD(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}