package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class BollingerBandsPbandTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = BollingerBandsPband(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}