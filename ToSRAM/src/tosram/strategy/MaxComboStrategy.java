package tosram.strategy;

import java.util.Deque;

import tosram.Direction;
import tosram.RuneMap;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A strategy that tries to maximize combo.
 * 
 * @author johnchen902
 */
public class MaxComboStrategy extends FilterStrategy {
	private int maxCombo;

	/**
	 * Create a <code>MaxComboStrategy</code> with the specified filtered
	 * strategy.
	 * 
	 * @param next
	 *            the filtered strategy
	 */
	public MaxComboStrategy(Strategy next) {
		super(next);
	}

	@Override
	public void reset(RuneMap initial) {
		super.reset(initial);
		maxCombo = -1;
	}

	private int combo;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		super.submit(map, x, y, stack);
		combo = getComboCalculator().getCombo();
	}

	@Override
	public int compareSolution() {
		return combo != maxCombo ? combo - maxCombo : super.compareSolution();
	}

	@Override
	public String getMilestone() {
		return combo + " combo " + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		maxCombo = combo;
	}
}
