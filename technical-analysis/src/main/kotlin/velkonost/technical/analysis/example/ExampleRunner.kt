package velkonost.technical.analysis.example

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.readCSV
import velkonost.technical.analysis.example.items.CsvColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.momentum.*
import velkonost.technical.analysis.indicator.momentum.ppo.Ppo
import velkonost.technical.analysis.indicator.momentum.ppo.PpoHist
import velkonost.technical.analysis.indicator.momentum.ppo.PpoSignal
import velkonost.technical.analysis.indicator.momentum.pvo.Pvo
import velkonost.technical.analysis.indicator.momentum.pvo.PvoHist
import velkonost.technical.analysis.indicator.momentum.pvo.PvoSignal
import velkonost.technical.analysis.indicator.momentum.stoch.Stoch
import velkonost.technical.analysis.indicator.momentum.stoch.StochSignal
import velkonost.technical.analysis.indicator.momentum.stochrsi.StochRsi
import velkonost.technical.analysis.indicator.momentum.stochrsi.StochRsiD
import velkonost.technical.analysis.indicator.momentum.stochrsi.StochRsiK
import velkonost.technical.analysis.indicator.other.CumulativeReturnIndicator
import velkonost.technical.analysis.indicator.other.DailyLogReturnIndicator
import velkonost.technical.analysis.indicator.other.DailyReturnIndicator
import velkonost.technical.analysis.indicator.trend.DpoIndicator
import velkonost.technical.analysis.indicator.trend.MassIndex
import velkonost.technical.analysis.indicator.trend.STCIndicator
import velkonost.technical.analysis.indicator.trend.TrixIndicator
import velkonost.technical.analysis.indicator.trend.aroon.AroonDown
import velkonost.technical.analysis.indicator.trend.aroon.AroonIndicator
import velkonost.technical.analysis.indicator.trend.aroon.AroonUp
import velkonost.technical.analysis.indicator.trend.ema.EmaFast
import velkonost.technical.analysis.indicator.trend.ema.EmaSlow
import velkonost.technical.analysis.indicator.trend.ichimoku.IchimokuA
import velkonost.technical.analysis.indicator.trend.ichimoku.IchimokuB
import velkonost.technical.analysis.indicator.trend.ichimoku.IchimokuBaseLine
import velkonost.technical.analysis.indicator.trend.ichimoku.IchimokuConversionLine
import velkonost.technical.analysis.indicator.trend.kst.Kst
import velkonost.technical.analysis.indicator.trend.kst.KstDiff
import velkonost.technical.analysis.indicator.trend.kst.KstSignal
import velkonost.technical.analysis.indicator.trend.macd.Macd
import velkonost.technical.analysis.indicator.trend.macd.MacdDiff
import velkonost.technical.analysis.indicator.trend.macd.MacdSignal
import velkonost.technical.analysis.indicator.trend.sma.SmaFast
import velkonost.technical.analysis.indicator.trend.sma.SmaSlow
import velkonost.technical.analysis.indicator.trend.vortex.VortexDiff
import velkonost.technical.analysis.indicator.trend.vortex.VortexNegative
import velkonost.technical.analysis.indicator.trend.vortex.VortexPositive
import velkonost.technical.analysis.indicator.volatility.AverageTrueRange
import velkonost.technical.analysis.indicator.volatility.UlcerIndex
import velkonost.technical.analysis.indicator.volatility.bollingerBands.*
import velkonost.technical.analysis.indicator.volatility.donchianChannel.*
import velkonost.technical.analysis.indicator.volatility.keltnerChannel.*
import velkonost.technical.analysis.indicator.volume.*
import java.io.File
import java.io.FileNotFoundException
import java.math.BigDecimal
import java.nio.file.Paths

/**
 * ExampleRunner is a utility object that demonstrates and tests the functionality of all technical indicators
 * in the library. It provides methods to:
 * 1. Load market data from CSV files
 * 2. Calculate various technical indicators
 * 3. Test indicator calculations against expected results
 * 4. Group indicators by category (volume, volatility, trend, momentum)
 *
 * The runner supports testing of indicators across different categories:
 * - Volume indicators (e.g., ADI, OBV, CMF)
 * - Volatility indicators (e.g., Bollinger Bands, Keltner Channels)
 * - Trend indicators (e.g., MACD, SMA, EMA)
 * - Momentum indicators (e.g., RSI, Stochastic)
 * - Other indicators (e.g., Daily Returns)
 *
 * Usage:
 * 1. Prepare market data in CSV format with required columns
 * 2. Use the run() method to execute all indicator calculations
 * 3. Check the results against expected values
 * 4. Analyze indicator performance and accuracy
 *
 * Note: This class is primarily used for testing and demonstration purposes.
 * For production use, individual indicators should be instantiated directly.
 */
object ExampleRunner {

    fun start(
        resultFilePath: String = "/results.csv",
    ) {
        val inputStream = this::class.java.getResourceAsStream(resultFilePath)
            ?: throw FileNotFoundException("Ресурс '$resultFilePath' не найден.")

        val dataframe = DataFrame.readCSV(
            stream = inputStream,
            colTypes = CsvColumn.entries.associate { it.name to it.type }
                    + IndicatorName.entries.associate { it.title to ColType.BigDecimal }
        )

        val highColumn: DataColumn<BigDecimal> = dataframe.getColumn(CsvColumn.High.name).cast()
        val closeColumn: DataColumn<BigDecimal> = dataframe.getColumn(CsvColumn.Close.name).cast()
        val lowColumn: DataColumn<BigDecimal> = dataframe.getColumn(CsvColumn.Low.name).cast()
        val volumeColumn: DataColumn<BigDecimal> = dataframe.getColumn(CsvColumn.Volume_BTC.name).cast()

        val indicators = mutableListOf<Indicator>()
        indicators.testVolume(highColumn, closeColumn, lowColumn, volumeColumn)
        indicators.testVolatility(highColumn, closeColumn, lowColumn)
        indicators.testTrend(highColumn, closeColumn, lowColumn)
        indicators.testMomentum(highColumn, closeColumn, lowColumn, volumeColumn)
        indicators.testOther(highColumn, closeColumn, lowColumn, volumeColumn)

        indicators.forEach { it.isEqual(dataframe) }
    }

    private fun MutableList<Indicator>.testVolume(
        highColumn: DataColumn<BigDecimal>,
        closeColumn: DataColumn<BigDecimal>,
        lowColumn: DataColumn<BigDecimal>,
        volumeColumn: DataColumn<BigDecimal>
    ) {
        AccDistIndexIndicator(
            high = highColumn,
            close = closeColumn,
            low = lowColumn,
            volume = volumeColumn,
            fillna = true
        ).also { add(it) }

        OnBalanceVolumeIndicator(
            close = closeColumn,
            volume = volumeColumn
        ).also { add(it) }

        ChaikinMoneyFlowIndicator(
            high = highColumn,
            low = lowColumn,
            close = closeColumn,
            volume = volumeColumn
        ).also { add(it) }

        ForceIndexIndicator(
            close = closeColumn,
            volume = volumeColumn,
        ).also { add(it) }

        EaseOfMovementIndicator(
            high = highColumn,
            low = lowColumn,
            volume = volumeColumn
        ).also { add(it) }

        SmaEaseOfMovementIndicator(
            high = highColumn,
            low = lowColumn,
            volume = volumeColumn
        ).also { add(it) }

        VolumePriceTrendIndicator(
            close = closeColumn,
            volume = volumeColumn,
            fillna = true
        ).also { add(it) }

        VolumeWeightedAveragePrice(
            high = highColumn,
            low = lowColumn,
            close = closeColumn,
            volume = volumeColumn
        ).also { add(it) }

        MFIIndicator(
            high = highColumn,
            low = lowColumn,
            close = closeColumn,
            volume = volumeColumn,
        ).also { add(it) }

        NegativeVolumeIndexIndicator(
            close = closeColumn,
            volume = volumeColumn
        ).also { add(it) }
    }

    /**
     * Tests volatility-based indicators using the provided market data.
     * Volatility indicators measure price volatility and market conditions.
     *
     * @param highColumn Column of high prices
     * @param closeColumn Column of closing prices
     * @param lowColumn Column of low prices
     * @param volumeColumn Column of volume values
     */
    private fun MutableList<Indicator>.testVolatility(
        highColumn: DataColumn<BigDecimal>,
        closeColumn: DataColumn<BigDecimal>,
        lowColumn: DataColumn<BigDecimal>,
    ) {
        BollingerBandsMavg(close = closeColumn).also { add(it) }
        BollingerBandsHband(close = closeColumn).also { add(it) }
        BollingerBandsLband(close = closeColumn).also { add(it) }
        BollingerBandsWband(close = closeColumn).also { add(it) }
        BollingerBandsPband(close = closeColumn).also { add(it) }
        BollingerBandsHbandIndicator(close = closeColumn).also { add(it) }
        BollingerBandsLbandIndicator(close = closeColumn).also { add(it) }

        KeltnerChannelMband(highColumn, lowColumn, closeColumn).also { add(it) }
        KeltnerChannelHband(highColumn, lowColumn, closeColumn).also { add(it) }
        KeltnerChannelLband(highColumn, lowColumn, closeColumn).also { add(it) }
        KeltnerChannelWband(highColumn, lowColumn, closeColumn).also { add(it) }
        KeltnerChannelPband(highColumn, lowColumn, closeColumn).also { add(it) }
        KeltnerChannelHbandIndicator(highColumn, lowColumn, closeColumn).also { add(it) }
        KeltnerChannelLbandIndicator(highColumn, lowColumn, closeColumn).also { add(it) }

        DonchianChannelLband(highColumn, lowColumn, closeColumn).also { add(it) }
        DonchianChannelHband(highColumn, lowColumn, closeColumn).also { add(it) }
        DonchianChannelMband(highColumn, lowColumn, closeColumn).also { add(it) }
        DonchianChannelWband(highColumn, lowColumn, closeColumn).also { add(it) }
        DonchianChannelPband(highColumn, lowColumn, closeColumn).also { add(it) }

        AverageTrueRange(highColumn, lowColumn, closeColumn, window = 10).also { add(it) }
        UlcerIndex(closeColumn).also { add(it) }
    }

    /**
     * Tests trend-based indicators using the provided market data.
     * Trend indicators identify and measure market trends.
     *
     * @param highColumn Column of high prices
     * @param closeColumn Column of closing prices
     * @param lowColumn Column of low prices
     */
    private fun MutableList<Indicator>.testTrend(
        highColumn: DataColumn<BigDecimal>,
        closeColumn: DataColumn<BigDecimal>,
        lowColumn: DataColumn<BigDecimal>,
    ) {
        Macd(closeColumn).also { add(it) }
        MacdSignal(closeColumn).also { add(it) }
        MacdDiff(closeColumn).also { add(it) }

        SmaFast(closeColumn).also { add(it) }
        SmaSlow(closeColumn).also { add(it) }

        EmaFast(closeColumn).also { add(it) }
        EmaSlow(closeColumn).also { add(it) }

        VortexPositive(highColumn, lowColumn, closeColumn).also { add(it) }
        VortexNegative(highColumn, lowColumn, closeColumn).also { add(it) }
        VortexDiff(highColumn, lowColumn, closeColumn).also { add(it) }

        TrixIndicator(closeColumn).also { add(it) }
        MassIndex(highColumn, lowColumn).also { add(it) }
        DpoIndicator(closeColumn).also { add(it) }

        Kst(closeColumn).also { add(it) }
        KstSignal(closeColumn).also { add(it) }
        KstDiff(closeColumn).also { add(it) }

        IchimokuConversionLine(highColumn, lowColumn).also { add(it) }
        IchimokuBaseLine(highColumn, lowColumn).also { add(it) }
        IchimokuA(highColumn, lowColumn).also { add(it) }
        IchimokuB(highColumn, lowColumn).also { add(it) }

        STCIndicator(closeColumn).also { add(it) }
        AroonUp(highColumn, lowColumn).also { add(it) }
        AroonDown(highColumn, lowColumn).also { add(it) }
        AroonIndicator(highColumn, lowColumn).also { add(it) }
    }

    /**
     * Tests momentum-based indicators using the provided market data.
     * Momentum indicators measure the speed and strength of price movements.
     *
     * @param highColumn Column of high prices
     * @param closeColumn Column of closing prices
     * @param lowColumn Column of low prices
     * @param volumeColumn Column of volume values
     */
    private fun MutableList<Indicator>.testMomentum(
        highColumn: DataColumn<BigDecimal>,
        closeColumn: DataColumn<BigDecimal>,
        lowColumn: DataColumn<BigDecimal>,
        volumeColumn: DataColumn<BigDecimal>
    ) {
        RsiIndicator(closeColumn).also { add(it) }
        StochRsi(closeColumn).also { add(it) }
        StochRsiK(closeColumn).also { add(it) }
        StochRsiD(closeColumn).also { add(it) }
        TsiIndicator(closeColumn).also { add(it) }
        UltimateOscillator(highColumn, lowColumn, closeColumn).also { add(it) }
        Stoch(highColumn, lowColumn, closeColumn).also { add(it) }
        StochSignal(highColumn, lowColumn, closeColumn).also { add(it) }
        WilliamsRIndicator(highColumn, lowColumn, closeColumn).also { add(it) }
        AwesomeOscillatorIndicator(highColumn, lowColumn).also { add(it) }
        ROCIndicator(closeColumn).also { add(it) }
        Ppo(closeColumn).also { add(it) }
        PpoSignal(closeColumn).also { add(it) }
        PpoHist(closeColumn).also { add(it) }
        Pvo(volumeColumn).also { add(it) }
        PvoSignal(volumeColumn).also { add(it) }
        PvoHist(volumeColumn).also { add(it) }
    }

    /**
     * Tests other miscellaneous indicators using the provided market data.
     * These include indicators for returns and other calculations.
     *
     * @param highColumn Column of high prices
     * @param closeColumn Column of closing prices
     * @param lowColumn Column of low prices
     * @param volumeColumn Column of volume values
     */
    private fun MutableList<Indicator>.testOther(
        highColumn: DataColumn<BigDecimal>,
        closeColumn: DataColumn<BigDecimal>,
        lowColumn: DataColumn<BigDecimal>,
        volumeColumn: DataColumn<BigDecimal>
    ) {
        DailyReturnIndicator(closeColumn).also { add(it) }
        DailyLogReturnIndicator(closeColumn).also { add(it) }
        CumulativeReturnIndicator(closeColumn).also { add(it) }
    }
}