package tosram.strategy;

import java.util.Deque;

import tosram.ComboDescriber;
import tosram.Direction;
import tosram.RuneMap;

/**
 * A strategy that looks for exact a specified number of combo;
 * 
 * @author johnchen902
 */
public class KComboStrategy extends FilterSolutionStrategy {

	private final int target;
	private int minDifference;

	/**
	 * Create a <code>KComboStrategy</code> that looks for exact
	 * <code>target</code> combo.
	 * 
	 * @param next
	 *            the filtered strategy
	 * @param target
	 *            the combo looking for
	 */
	public KComboStrategy(SolutionStrategy next, int target) {
		super(next);
		this.target = target;
	}

	@Override
	public void reset() {
		super.reset();
		minDifference = Integer.MAX_VALUE;
	}

	private int combo;
	private int difference;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboDescriber cd) {
		super.submit(map, x, y, stack, cd);
		combo = cd.getCombo();
		difference = Math.abs(combo - target);
	}

	@Override
	public int compareSolution() {
		if (difference != minDifference)
			return difference < minDifference ? 1 : -1;
		return super.compareSolution();
	}

	@Override
	public double getQuality() {
		return 0.06 * Math.max(5 - difference, 0) + 0.7 * super.getQuality();
	}

	@Override
	public String getMilestone() {
		return combo + " combo " + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		minDifference = difference;
	}

}
