package tosram.view.strategy;

import tosram.strategy.MaxComboStrategy;
import tosram.strategy.SolutionStrategy;

/**
 * A <code>StrategyCreater</code> who creates {@link MaxComboStrategy}
 * 
 * @author johnchen902
 */
public class MaxComboCreater extends DefaultStrategyCreater {

	/**
	 * Constructor.
	 */
	public MaxComboCreater() {
		super("Max Combo");
	}

	@Override
	public SolutionStrategy createStrategy(SolutionStrategy next) {
		return new MaxComboStrategy(next);
	}

}
