package velkonost.technical.analysis.indicator.indicator.volume

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class OnBalanceVolumeIndicatorTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = velkonost.technical.analysis.indicator.volume.OnBalanceVolumeIndicator(
                close = closeColumn,
                volume = volumeColumn,
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}