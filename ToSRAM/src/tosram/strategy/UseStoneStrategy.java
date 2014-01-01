package tosram.strategy;

import java.util.Deque;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.RuneMap;
import tosram.RuneStone.Type;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A strategy that restrict the usage of a specified type of stone.
 * 
 * @author johnchen902
 */
public class UseStoneStrategy extends FilterStrategy {

	private final Type type;
	private final int lower, upper;
	private int minDifference;

	/**
	 * Create a strategy such that the number of the used stone of the specified
	 * type is in the specified range.
	 * 
	 * @param next
	 *            the filtered strategy
	 * @param type
	 *            the specified type
	 * @param lower
	 *            the lower limit of the specified range (including)
	 * @param upper
	 *            the upper limit of the specified range (including)
	 */
	public UseStoneStrategy(Strategy next, Type type, int lower, int upper) {
		super(next);
		if (lower > upper)
			throw new IllegalArgumentException("lower > upper");
		this.type = type;
		this.lower = lower;
		this.upper = upper;
	}

	@Override
	public void reset(RuneMap initial) {
		super.reset(initial);
		minDifference = Integer.MAX_VALUE;
	}

	private int count;
	private int difference;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		super.submit(map, x, y, stack);
		count = 0;
		for (ComboCalculator.Combo ccc : getComboCalculator().getComboList())
			if (ccc.getType() == type)
				count += Long.bitCount(ccc.getMask());
		difference = count > upper ? count - upper : lower > count ? lower
				- count : 0;
	}

	@Override
	public int compareSolution() {
		if (minDifference != difference)
			return minDifference - difference;
		return super.compareSolution();
	}

	@Override
	public String getMilestone() {
		return count + " " + type + "s " + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		minDifference = difference;
	}
}
