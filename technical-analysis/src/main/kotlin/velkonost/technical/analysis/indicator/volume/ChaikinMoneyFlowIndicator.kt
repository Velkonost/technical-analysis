package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.extensions.safeDivide
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal

class ChaikinMoneyFlowIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val fillna: Boolean = false
) : Indicator(IndicatorName.Cmf) {

    override fun calculate(): DataColumn<BigDecimal> {
        val moneyFlowMultiplier = calculateMoneyFlowMultiplier()
        val moneyFlowVolume = calculateMoneyFlowVolume(moneyFlowMultiplier)

        val rollingSumMoneyFlowVolume = moneyFlowVolume.rollingSum(window)
        val rollingSumVolume = volume.rollingSum(window)

        val cmfValues = rollingSumMoneyFlowVolume.indices.map { index ->
            val moneyFlowSum = rollingSumMoneyFlowVolume[index]
            val volumeSum = rollingSumVolume[index]
            moneyFlowSum.safeDivide(volumeSum)
        }

        return DataColumn.create(name.title, cmfValues)
    }

    private fun calculateMoneyFlowMultiplier(): DataColumn<BigDecimal> {
        return DataColumn.create(
            "Multiplier",
            close.indices.map { index ->
                val closeValue = close[index]
                val highValue = high[index]
                val lowValue = low[index]

                val numerator = closeValue.subtract(lowValue).subtract(highValue.subtract(closeValue))
                val denominator = highValue.subtract(lowValue)

                numerator.safeDivide(denominator)
            }
        )
    }

    private fun calculateMoneyFlowVolume(multiplier: DataColumn<BigDecimal>): DataColumn<BigDecimal> {
        return DataColumn.create(
            "Volume",
            multiplier.indices.map { index ->
                multiplier[index].multiply(volume[index])
            }
        )
    }

}