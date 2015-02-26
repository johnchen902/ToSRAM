package tosram.strategy;

import java.util.Deque;
import java.util.EnumSet;

import tosram.Direction;
import tosram.MutableRuneMap;

/**
 * A strategy that limit step of search explicitly.
 * 
 * @author johnchen902
 */
public class StepLimitStrategy implements SearchStrategy {

	private final SearchStrategy strategy;
	private final int limit;

	/**
	 * A strategy that limit step of search explicitly.
	 * 
	 * @param next
	 *            the filtered strategy
	 * @param limit
	 *            the limit of number of step
	 */
	public StepLimitStrategy(SearchStrategy next, int limit) {
		strategy = next;
		this.limit = limit;
	}

	private int step;

	@Override
	public void reset() {
		strategy.reset();
	}

	@Override
	public void submit(MutableRuneMap map, int x, int y,
			Deque<Direction> stack, double quality) {
		strategy.submit(map, x, y, stack, quality);
		step = stack.size();
	}

	@Override
	public EnumSet<Direction> getDirections() {
		if (step >= limit) {
			return EnumSet.noneOf(Direction.class);
		} else {
			return strategy.getDirections();
		}
	}

	@Override
	public double adaptProgress(double progress) {
		return strategy.adaptProgress(progress);
	}

}
