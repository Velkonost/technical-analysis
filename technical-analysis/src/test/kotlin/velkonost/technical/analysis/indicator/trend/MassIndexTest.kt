package velkonost.technical.analysis.indicator.trend

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class MassIndexTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = MassIndex(
                high = highColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}