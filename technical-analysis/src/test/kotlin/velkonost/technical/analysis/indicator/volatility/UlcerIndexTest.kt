package velkonost.technical.analysis.indicator.volatility

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis
import velkonost.technical.analysis.indicator.volume.AccDistIndexIndicator

class UlcerIndexTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = UlcerIndex(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}