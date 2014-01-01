package tosram.view.strategy;

import tosram.strategy.MaxComboStrategy;
import tosram.strategy.StrategySearchPathRobot.Strategy;

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
	public Strategy createStrategy(Strategy next) {
		return new MaxComboStrategy(next);
	}

}
