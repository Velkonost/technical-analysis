package velkonost.technical.analysis.indicator.base

/**
 * Enum class representing all available technical indicators in the library.
 * Indicators are categorized into several groups:
 * - Volume indicators: Measure trading volume and its relationship with price
 * - Volatility indicators: Measure price volatility and market conditions
 * - Trend indicators: Identify and measure market trends
 * - Momentum indicators: Measure the speed and strength of price movements
 * - Other indicators: Miscellaneous technical analysis tools
 *
 * Each indicator has a unique title used for identification in the output data.
 */
enum class IndicatorName(val title: String) {

    // Volume indicators - measure trading activity and volume-price relationships
    Adi("volume_adi"),        // Accumulation/Distribution Index
    Obv("volume_obv"),        // On-Balance Volume
    Cmf("volume_cmf"),        // Chaikin Money Flow
    Fi("volume_fi"),          // Force Index
    Em("volume_em"),          // Ease of Movement
    SmaEm("volume_sma_em"),   // Smoothed Ease of Movement
    Vpt("volume_vpt"),        // Volume Price Trend
    Vwap("volume_vwap"),      // Volume Weighted Average Price
    Mfi("volume_mfi"),        // Money Flow Index
    Nvi("volume_nvi"),        // Negative Volume Index

    // Volatility indicators - measure price volatility and market conditions
    Bbm("volatility_bbm"),    // Bollinger Bands Middle
    Bbh("volatility_bbh"),    // Bollinger Bands High
    Bbl("volatility_bbl"),    // Bollinger Bands Low
    Bbw("volatility_bbw"),    // Bollinger Bands Width
    Bbp("volatility_bbp"),    // Bollinger Bands Percentage
    Bbhi("volatility_bbhi"),  // Bollinger Bands High Indicator
    Bbli("volatility_bbli"),  // Bollinger Bands Low Indicator
    Kcc("volatility_kcc"),    // Keltner Channel Center
    Kch("volatility_kch"),    // Keltner Channel High
    Kcl("volatility_kcl"),    // Keltner Channel Low
    Kcw("volatility_kcw"),    // Keltner Channel Width
    Kcp("volatility_kcp"),    // Keltner Channel Percentage
    Kchi("volatility_kchi"),  // Keltner Channel High Indicator
    Kcli("volatility_kcli"),  // Keltner Channel Low Indicator
    Dcl("volatility_dcl"),    // Donchian Channel Low
    Dch("volatility_dch"),    // Donchian Channel High
    Dcm("volatility_dcm"),    // Donchian Channel Middle
    Dcw("volatility_dcw"),    // Donchian Channel Width
    Dcp("volatility_dcp"),    // Donchian Channel Percentage
    Atr("volatility_atr"),    // Average True Range
    Ui("volatility_ui"),      // Ulcer Index

    // Trend indicators - identify and measure market trends
    Macd("trend_macd"),           // Moving Average Convergence Divergence
    MacdSignal("trend_macd_signal"), // MACD Signal Line
    MacdDiff("trend_macd_diff"),     // MACD Histogram
    SmaFast("trend_sma_fast"),       // Fast Simple Moving Average
    SmaSlow("trend_sma_slow"),       // Slow Simple Moving Average
    EmaFast("trend_ema_fast"),       // Fast Exponential Moving Average
    EmaSlow("trend_ema_slow"),       // Slow Exponential Moving Average
    VortexIndPositive("trend_vortex_ind_pos"), // Vortex Indicator Positive
    VortexIndNegative("trend_vortex_ind_neg"), // Vortex Indicator Negative
    VortexIndDiff("trend_vortex_ind_diff"),    // Vortex Indicator Difference
    Trix("trend_trix"),             // Triple Exponential Average
    MassIndex("trend_mass_index"),  // Mass Index
    Dpo("trend_dpo"),               // Detrended Price Oscillator
    Kst("trend_kst"),               // Know Sure Thing
    KstSignal("trend_kst_sig"),     // KST Signal Line
    KstDiff("trend_kst_diff"),      // KST Difference
    IchimokuConv("trend_ichimoku_conv"),    // Ichimoku Conversion Line
    IchimokuBase("trend_ichimoku_base"),    // Ichimoku Base Line
    IchimokuA("trend_ichimoku_a"),          // Ichimoku Span A
    IchimokuB("trend_ichimoku_b"),          // Ichimoku Span B
    Stc("trend_stc"),               // Schaff Trend Cycle
    IchimokuVisualA("trend_visual_ichimoku_a"), // Ichimoku Visual Span A
    IchimokuVisualB("trend_visual_ichimoku_b"), // Ichimoku Visual Span B
    AroonUp("trend_aroon_up"),      // Aroon Up
    AroonDown("trend_aroon_down"),  // Aroon Down
    AroonIndicator("trend_aroon_ind"), // Aroon Indicator

    // Momentum indicators - measure the speed and strength of price movements
    Rsi("momentum_rsi"),            // Relative Strength Index
    StochRsi("momentum_stoch_rsi"), // Stochastic RSI
    StochRsiK("momentum_stoch_rsi_k"), // Stochastic RSI %K
    StochRsiD("momentum_stoch_rsi_d"), // Stochastic RSI %D
    Tsi("momentum_tsi"),            // True Strength Index
    Uo("momentum_uo"),              // Ultimate Oscillator
    Stoch("momentum_stoch"),        // Stochastic Oscillator
    StochSignal("momentum_stoch_signal"), // Stochastic Signal
    Wr("momentum_wr"),              // Williams %R
    Ao("momentum_ao"),              // Awesome Oscillator
    Roc("momentum_roc"),            // Rate of Change
    Ppo("momentum_ppo"),            // Percentage Price Oscillator
    PpoSignal("momentum_ppo_signal"), // PPO Signal Line
    PpoHist("momentum_ppo_hist"),   // PPO Histogram
    Pvo("momentum_pvo"),            // Percentage Volume Oscillator
    PvoSignal("momentum_pvo_signal"), // PVO Signal Line
    PvoHist("momentum_pvo_hist"),   // PVO Histogram
    Kama("momentum_kama"),          // Kaufman Adaptive Moving Average

    // Other indicators - miscellaneous technical analysis tools
    Dr("others_dr"),                // Daily Return
    Dlr("others_dlr"),              // Daily Log Return
    Cr("others_cr")                 // Cumulative Return

}