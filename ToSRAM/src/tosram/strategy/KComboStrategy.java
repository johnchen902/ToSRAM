package tosram.strategy;

import java.util.Deque;

import tosram.Direction;
import tosram.RuneMap;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A strategy that looks for exact a specified number of combo;
 * 
 * @author johnchen902
 */
public class KComboStrategy extends FilterStrategy {

	private final int target;
	private boolean bestHasK;

	/**
	 * Create a <code>KComboStrategy</code> that looks for exact
	 * <code>target</code> combo.
	 * 
	 * @param next
	 *            the filtered strategy
	 * @param target
	 *            the combo looking for
	 */
	public KComboStrategy(Strategy next, int target) {
		super(next);
		this.target = target;
	}

	@Override
	public void reset(RuneMap initial) {
		super.reset(initial);
		bestHasK = false;
	}

	private int combo;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		super.submit(map, x, y, stack);
		combo = getComboCalculator().getCombo();
	}

	@Override
	public int compareSolution() {
		if ((combo == target) != bestHasK)
			return combo == target ? 1 : -1;
		return super.compareSolution();
	}

	@Override
	public String getMilestone() {
		return combo + " combo " + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		bestHasK = combo == target;
	}

}
