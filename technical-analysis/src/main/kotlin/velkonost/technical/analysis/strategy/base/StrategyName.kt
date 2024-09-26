package velkonost.technical.analysis.strategy.base

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
    TripleEmaStochRsiAtr("tripleEMAStochasticRSIATR")
}