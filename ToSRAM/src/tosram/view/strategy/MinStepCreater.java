package tosram.view.strategy;

import tosram.strategy.MinStepStrategy;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A <code>StrategyCreater</code> who creates {@link MinStepStrategy}
 * 
 * @author johnchen902
 */
public class MinStepCreater extends DefaultStrategyCreater {

	/**
	 * Constructor.
	 */
	public MinStepCreater() {
		super("Min Step");
	}

	@Override
	public Strategy createStrategy(Strategy next) {
		return new MinStepStrategy(next);
	}

}
