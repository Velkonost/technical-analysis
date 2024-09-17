package velkonost.technical.analysis.indicator.momentum.stoch

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class StochSignalTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = StochSignal(
                high = highColumn,
                close = closeColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}