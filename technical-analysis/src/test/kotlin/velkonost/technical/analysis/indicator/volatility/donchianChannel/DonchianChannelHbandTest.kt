package velkonost.technical.analysis.indicator.volatility.donchianChannel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class DonchianChannelHbandTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = DonchianChannelHband(
                high = highColumn,
                close = closeColumn,
                low = lowColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}