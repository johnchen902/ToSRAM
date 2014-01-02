package tosram.strategy;

import java.util.Deque;

import tosram.ComboDescriber;
import tosram.Direction;
import tosram.RuneMap;

/**
 * A <code>FilterStrategy</code> contains some other strategy, which it uses as
 * its the secondary strategy in case of tie. The class
 * <code>FilterStrategy</code> itself simply overrides all methods of
 * <code>Strategy</code> with versions that pass all requests to the contained
 * strategy. Subclasses of <code>FilterStrategy</code> may further override some
 * of these methods.
 * 
 * @author johnchen902
 */
public class FilterSolutionStrategy implements SolutionStrategy {

	/**
	 * The strategy to be filtered.
	 */
	protected final SolutionStrategy strategy;

	/**
	 * Creates a <code>FilterStrategy</code> by assigning the argument
	 * <code>strategy</code> to the field <code>this.strategy</code> so as to
	 * remember it for later use.
	 * 
	 * @param strategy
	 *            the underlying strategy
	 */
	protected FilterSolutionStrategy(SolutionStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void reset() {
		strategy.reset();
	}

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboDescriber cd) {
		strategy.submit(map, x, y, stack, cd);
	}

	@Override
	public int compareSolution() {
		return strategy.compareSolution();
	}

	@Override
	public double getQuality() {
		return strategy.getQuality();
	}

	@Override
	public String getMilestone() {
		return strategy.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		strategy.solutionAccepted();
	}

}
