package tosram.strategy;

import java.util.Deque;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.MutableRuneMap;

/**
 * A strategy that tries to minimize steps.
 * 
 * @author johnchen902
 */
public class MinStepStrategy extends FilterSolutionStrategy {
	private int minSteps;

	/**
	 * Create a <code>MinStepStrategy</code> with the specified filtered
	 * strategy.
	 * 
	 * @param next
	 *            the filtered strategy
	 */
	public MinStepStrategy(SolutionStrategy next) {
		super(next);
	}

	@Override
	public void reset() {
		super.reset();
		minSteps = Integer.MAX_VALUE;
	}

	private int steps;

	@Override
	public void submit(MutableRuneMap map, int x, int y,
			Deque<Direction> stack, ComboCalculator.Describer cd) {
		super.submit(map, x, y, stack, cd);
		steps = stack.size();
	}

	@Override
	public int compareSolution() {
		return steps != minSteps ? minSteps - steps : super.compareSolution();
	}

	@Override
	public String getMilestone() {
		return MilestoneFormatter.formatSteps(steps) + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		minSteps = steps;
	}
}
