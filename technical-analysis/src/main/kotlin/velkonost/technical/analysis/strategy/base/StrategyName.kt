package velkonost.technical.analysis.strategy.base

/**
 * Enum class representing all available trading strategies in the library.
 * Each strategy combines one or more technical indicators to generate trading signals.
 * Strategies are designed to identify specific market conditions and trading opportunities.
 *
 * Available Strategies:
 * - Breakout: Identifies price breakouts from established ranges or patterns
 * - EmaCross: Uses EMA crossovers to identify trend changes
 * - EmaCrossover: Advanced EMA crossover strategy with additional confirmation signals
 * - CandleWick: Analyzes candlestick wicks for potential reversal signals
 * - GoldenCross: Uses the intersection of fast and slow moving averages
 * - StochRsiMacd: Combines Stochastic RSI and MACD for momentum and trend confirmation
 * - RsiStochEma: Integrates RSI, Stochastic, and EMA for comprehensive market analysis
 * - StochasticBb: Uses Stochastic oscillator with Bollinger Bands for volatility-based signals
 * - TripleEma: Implements a triple EMA crossover system for trend following
 * - TripleEmaStochRsiAtr: Advanced strategy combining Triple EMA, Stochastic RSI, and ATR
 * - HeikinAshiEma: Uses Heikin-Ashi candles with EMA for trend identification
 * - HeikinAshiEma2: Enhanced version of Heikin-Ashi EMA strategy
 * - FibMacd: Combines Fibonacci levels with MACD for trend and reversal signals
 *
 * Each strategy has a unique title used for identification in the output data.
 * Strategies can be used individually or combined for more complex trading systems.
 */
enum class StrategyName(val title: String) {
    Breakout("breakout"),
    EmaCross("EMA_cross"),
    EmaCrossover("EmaCrossover"),
    CandleWick("candle_wick"),
    GoldenCross("goldenCross"),
    StochRsiMacd("StochRSIMACD"),
    RsiStochEma("rsi_stoch_ema"),
    StochasticBb("stochBB"),
    TripleEma("tripleEMA"),
    TripleEmaStochRsiAtr("tripleEMAStochasticRSIATR"),
    HeikinAshiEma("heikin_ashi_ema"),
    HeikinAshiEma2("heikin_ashi_ema2"),
    FibMacd("fibMACD")
}