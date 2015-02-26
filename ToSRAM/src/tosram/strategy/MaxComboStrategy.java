package tosram.strategy;

import java.util.Deque;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.MutableRuneMap;

/**
 * A strategy that tries to maximize combo.
 * 
 * @author johnchen902
 */
public class MaxComboStrategy extends FilterSolutionStrategy {
	private int maxCombo;

	/**
	 * Create a <code>MaxComboStrategy</code> with the specified filtered
	 * strategy.
	 * 
	 * @param next
	 *            the filtered strategy
	 */
	public MaxComboStrategy(SolutionStrategy next) {
		super(next);
	}

	@Override
	public void reset() {
		super.reset();
		maxCombo = -1;
	}

	private int combo;

	@Override
	public void submit(MutableRuneMap map, int x, int y,
			Deque<Direction> stack, ComboCalculator.Describer cd) {
		super.submit(map, x, y, stack, cd);
		combo = cd.getFullComboCount();
	}

	@Override
	public int compareSolution() {
		return combo != maxCombo ? combo - maxCombo : super.compareSolution();
	}

	@Override
	public double getQuality() {
		// assume 0 <= combo <= 10
		return combo * 0.09 + super.getQuality() * 0.1;
	}

	@Override
	public String getMilestone() {
		return MilestoneFormatter.formatCombo(combo) + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		maxCombo = combo;
	}
}
