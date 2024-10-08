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
    TripleEmaStochRsiAtr("tripleEMAStochasticRSIATR"),
    HeikinAshiEma("heikin_ashi_ema"),
    HeikinAshiEma2("heikin_ashi_ema2"),
    FibMacd("fibMACD")
}