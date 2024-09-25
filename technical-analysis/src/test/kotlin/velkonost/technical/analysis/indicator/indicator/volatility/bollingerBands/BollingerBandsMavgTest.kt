package velkonost.technical.analysis.indicator.indicator.volatility.bollingerBands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class BollingerBandsMavgTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = velkonost.technical.analysis.indicator.volatility.bollingerBands.BollingerBandsMavg(
                close = closeColumn,
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}