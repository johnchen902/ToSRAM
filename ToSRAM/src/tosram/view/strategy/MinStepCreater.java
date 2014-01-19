package tosram.view.strategy;

import tosram.strategy.MinStepStrategy;
import tosram.strategy.SolutionStrategy;

/**
 * A <code>StrategyCreater</code> who creates {@link MinStepStrategy}
 * 
 * @author johnchen902
 */
public class MinStepCreater extends DefaultStrategyCreater {

	@Override
	protected String getName() {
		return "Min Step";
	}

	@Override
	public SolutionStrategy createStrategy(SolutionStrategy next) {
		return new MinStepStrategy(next);
	}

}
