package tosram.strategy;

import java.util.Deque;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.RuneMap;
import tosram.strategy.StrategySearchPathRobot.Strategy;

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
public class FilterStrategy implements StrategySearchPathRobot.Strategy {

	/**
	 * The strategy to be filtered.
	 */
	protected final StrategySearchPathRobot.Strategy strategy;

	/**
	 * Creates a <code>FilterStrategy</code> by assigning the argument
	 * <code>strategy</code> to the field <code>this.strategy</code> so as to
	 * remember it for later use.
	 * 
	 * @param strategy
	 *            the underlying strategy
	 */
	protected FilterStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void reset(RuneMap initial) {
		strategy.reset(initial);
	}

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		strategy.submit(map, x, y, stack);
	}

	@Override
	public ComboCalculator getComboCalculator() {
		return strategy.getComboCalculator();
	}

	@Override
	public boolean isToDiagonal() {
		return strategy.isToDiagonal();
	}

	@Override
	public boolean isToStop() {
		return strategy.isToStop();
	}

	@Override
	public int compareSolution() {
		return strategy.compareSolution();
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
