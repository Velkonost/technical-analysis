package velkonost.technical.analysis.indicator.indicator.volatility.bollingerBands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class BollingerBandsHbandIndicatorTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator =
                velkonost.technical.analysis.indicator.volatility.bollingerBands.BollingerBandsHbandIndicator(
                    close = closeColumn,
                    fillna = true
                )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}