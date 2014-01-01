package tosram.strategy;

import java.util.Deque;

import tosram.Direction;
import tosram.RuneMap;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A strategy that tries to minimize steps.
 * 
 * @author johnchen902
 */
public class MinStepStrategy extends FilterStrategy {
	private int minSteps;

	/**
	 * Create a <code>MinStepStrategy</code> with the specified filtered
	 * strategy.
	 * 
	 * @param next
	 *            the filtered strategy
	 */
	public MinStepStrategy(Strategy next) {
		super(next);
	}

	@Override
	public void reset(RuneMap initial) {
		super.reset(initial);
		minSteps = Integer.MAX_VALUE;
	}

	private int steps;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		super.submit(map, x, y, stack);
		steps = stack.size();
	}

	@Override
	public int compareSolution() {
		return steps != minSteps ? minSteps - steps : super.compareSolution();
	}

	@Override
	public String getMilestone() {
		return steps + " steps " + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		minSteps = steps;
	}
}
