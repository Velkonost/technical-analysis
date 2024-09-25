package velkonost.technical.analysis.indicator.indicator.momentum

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.example.ExampleRunner
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class AwesomeOscillatorIndicatorTest {

    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = velkonost.technical.analysis.indicator.momentum.AwesomeOscillatorIndicator(
                high = highColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}