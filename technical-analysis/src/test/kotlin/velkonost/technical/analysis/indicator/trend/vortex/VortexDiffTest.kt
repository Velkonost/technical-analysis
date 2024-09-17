package velkonost.technical.analysis.indicator.trend.vortex

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class VortexDiffTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = VortexDiff(
                high = highColumn,
                close = closeColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}