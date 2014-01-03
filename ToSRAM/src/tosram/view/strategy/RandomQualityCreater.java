package tosram.view.strategy;

import tosram.strategy.RandomQualityStrategy;
import tosram.strategy.SolutionStrategy;

/**
 * A <code>StrategyCreater</code> who creates {@link RandomQualityStrategy}
 * 
 * @author johnchen902
 */
public class RandomQualityCreater extends DefaultStrategyCreater {

	/**
	 * Constructor.
	 */
	public RandomQualityCreater() {
		super("Random Quality");
	}

	@Override
	public SolutionStrategy createStrategy(SolutionStrategy next) {
		return new RandomQualityStrategy(next);
	}

}
