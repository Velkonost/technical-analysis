package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal

/**
 * Accumulation/Distribution Index (ADI) indicator implementation.
 * The ADI is a volume-based indicator that measures the cumulative flow of money into and out of a security.
 * It helps identify divergences between price and volume, which can signal potential trend reversals.
 *
 * The ADI is calculated as:
 * 1. Compute the Close Location Value (CLV):
 *    CLV = [(Close - Low) - (High - Close)] / (High - Low)
 * 2. Multiply the CLV by the volume for each period
 * 3. Calculate the cumulative sum of these values
 *
 * Trading signals:
 * - Rising ADI indicates accumulation (buying pressure)
 * - Falling ADI indicates distribution (selling pressure)
 * - Divergences between ADI and price can signal potential reversals
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property volume Column of volume values
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class AccDistIndexIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val fillna: Boolean = false
) : Indicator(name = IndicatorName.Adi) {

    /**
     * Calculates the Accumulation/Distribution Index (ADI) values.
     * The calculation involves:
     * 1. Computing the Close Location Value (CLV)
     * 2. Multiplying the CLV by the volume
     * 3. Calculating the cumulative sum
     *
     * @return DataColumn<BigDecimal> containing the ADI values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val clv = close.mapIndexed { index, closeValue ->
            val highValue = high[index]
            val lowValue = low[index]
            val denominator = highValue.subtract(lowValue)

            // Проверка деления на ноль и обработка граничных случаев
            if (denominator.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal.ZERO
            } else {
                val numerator = closeValue.subtract(lowValue).subtract(highValue.subtract(closeValue))
                numerator.divide(denominator, scale, java.math.RoundingMode.HALF_UP)
            }
        }

        val adiValues = clv.mapIndexed { index, clvValue ->
            clvValue.multiply(volume[index])
        }

        return adiValues.cumSum(fillna)
    }
}


