package velkonost.technical.analysis.indicator.indicator.volatility.keltnerChannel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class KeltnerChannelWbandTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = velkonost.technical.analysis.indicator.volatility.keltnerChannel.KeltnerChannelWband(
                high = highColumn,
                close = closeColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}