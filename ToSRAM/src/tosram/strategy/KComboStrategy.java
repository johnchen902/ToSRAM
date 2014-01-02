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
	public KComboStrategy(SolutionStrategy next, int target) {
		super(next);
		this.target = target;
	}

	@Override
	public void reset() {
		super.reset();
		bestHasK = false;
	}

	private int combo;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboDescriber cd) {
		super.submit(map, x, y, stack, cd);
		combo = cd.getCombo();
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
