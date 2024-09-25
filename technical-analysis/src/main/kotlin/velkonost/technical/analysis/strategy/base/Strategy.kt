package velkonost.technical.analysis.strategy.base

abstract class Strategy(
    val name: StrategyName,
    protected val scale: Int = 10
) {

    abstract fun calculate(): StrategyDecision

}