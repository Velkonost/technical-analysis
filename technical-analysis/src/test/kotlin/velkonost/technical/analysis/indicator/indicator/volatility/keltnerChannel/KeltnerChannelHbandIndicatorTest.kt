package velkonost.technical.analysis.indicator.indicator.volatility.keltnerChannel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class KeltnerChannelHbandIndicatorTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator =
                velkonost.technical.analysis.indicator.volatility.keltnerChannel.KeltnerChannelHbandIndicator(
                    high = highColumn,
                    close = closeColumn,
                    low = lowColumn,
                    fillna = true
                )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}