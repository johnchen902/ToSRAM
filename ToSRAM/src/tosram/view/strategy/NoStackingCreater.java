package tosram.view.strategy;

import tosram.strategy.NoStackingStrategy;
import tosram.strategy.SolutionStrategy;

/**
 * A <code>StrategyCreater</code> that creates {@link NoStackingStrategy}.
 * 
 * @author johnchen902
 */
public class NoStackingCreater extends DefaultStrategyCreater {

	public NoStackingCreater() {
		super("No Stacking");
	}

	@Override
	public SolutionStrategy createStrategy(SolutionStrategy next) {
		return new NoStackingStrategy(next);
	}
}
