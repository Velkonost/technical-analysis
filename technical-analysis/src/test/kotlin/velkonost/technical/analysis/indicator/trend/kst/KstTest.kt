package velkonost.technical.analysis.indicator.trend.kst

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class KstTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = Kst(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}