package velkonost.technical.analysis.indicator.trend.ichimoku

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class IchimokuConversionLineTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = IchimokuConversionLine(
                high = highColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}