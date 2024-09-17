package velkonost.technical.analysis.indicator.trend.ichimoku

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class IchimokuATest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = IchimokuA(
                high = highColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}