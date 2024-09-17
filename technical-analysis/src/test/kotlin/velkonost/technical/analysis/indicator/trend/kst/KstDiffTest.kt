package velkonost.technical.analysis.indicator.trend.kst

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class KstDiffTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = KstDiff(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}