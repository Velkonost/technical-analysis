package velkonost.technical.analysis.indicator.indicator.trend.ichimoku

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class IchimokuConversionLineTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = velkonost.technical.analysis.indicator.trend.ichimoku.IchimokuConversionLine(
                high = highColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}