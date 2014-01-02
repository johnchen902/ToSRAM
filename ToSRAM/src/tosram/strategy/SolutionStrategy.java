package tosram.strategy;

import java.util.Deque;

import tosram.ComboDescriber;
import tosram.Direction;
import tosram.RuneMap;

/**
 * A strategy determines the quality of the solution.
 * 
 * @author johnchen902
 */
public interface SolutionStrategy {
	/**
	 * Clear the solutions found.
	 */
	public void reset();

	/**
	 * Submit a solution and prepare to invoke other methods.
	 * 
	 * @param map
	 *            the resulting <code>RuneMap</code>
	 * @param x
	 *            the X coordinate of the current location
	 * @param y
	 *            the Y coordinate of the current location
	 * @param stack
	 *            the directions of current path
	 * @param combos
	 *            an description of combo of the map
	 */
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboDescriber combos);

	/**
	 * Compare the currently submitted solution and the currently accepted
	 * solution.
	 * 
	 * @return a positive number if the currently submitted solution is better;
	 *         a negative number if the currently submitted solution is worse;
	 *         <code>0</code> if two solution are equally good
	 */
	public int compareSolution();

	/**
	 * Get a rough measurement of quality of the currently submitted solution.
	 * However, the quality does not guarantee the result of
	 * {@link #compareSolution()}.
	 * 
	 * @return a <code>double</code> between <code>0.0</code> (worst) and
	 *         <code>1.0</code> (best)
	 */
	public double getQuality();

	/**
	 * Get a text description of the currently submitted solution.
	 * 
	 * @return a <code>String</code>
	 */
	public String getMilestone();

	/**
	 * Set the currently submitted solution to the currently accepted solution,
	 * even if the solution become worse.
	 */
	public void solutionAccepted();
}