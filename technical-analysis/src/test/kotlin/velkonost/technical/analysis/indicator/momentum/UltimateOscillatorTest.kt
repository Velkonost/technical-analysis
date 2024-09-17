package velkonost.technical.analysis.indicator.momentum

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class UltimateOscillatorTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = UltimateOscillator(
                high = highColumn,
                close = closeColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}