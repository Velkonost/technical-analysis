package velkonost.technical.analysis.indicator.trend.ichimoku

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis
import velkonost.technical.analysis.indicator.volume.AccDistIndexIndicator

class IchimokuBTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = IchimokuB(
                high = highColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}