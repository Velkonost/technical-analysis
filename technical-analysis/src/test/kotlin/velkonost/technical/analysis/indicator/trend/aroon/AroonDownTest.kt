package velkonost.technical.analysis.indicator.trend.aroon

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class AroonDownTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = AroonDown(
                high = highColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}