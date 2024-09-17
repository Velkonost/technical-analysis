package velkonost.technical.analysis.indicator.volume

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class ChaikinMoneyFlowIndicatorTest {

    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = ChaikinMoneyFlowIndicator(
                high = highColumn,
                close = closeColumn,
                low = lowColumn,
                volume = volumeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}