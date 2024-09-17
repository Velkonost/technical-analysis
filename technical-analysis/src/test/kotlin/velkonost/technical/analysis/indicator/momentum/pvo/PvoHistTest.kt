package velkonost.technical.analysis.indicator.momentum.pvo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class PvoHistTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = PvoHist(
                volume = volumeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}