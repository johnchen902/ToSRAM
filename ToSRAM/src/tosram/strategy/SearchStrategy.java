package tosram.strategy;

import java.util.Deque;
import java.util.EnumSet;

import tosram.Direction;
import tosram.RuneMap;

public interface SearchStrategy {

	/**
	 * Clear the solutions found.
	 */
	public void reset();

	/**
	 * Submit current solution and prepare to invoke other methods.
	 * 
	 * @param map
	 *            the resulting <code>RuneMap</code>
	 * @param x
	 *            the X coordinate of the current location
	 * @param y
	 *            the Y coordinate of the current location
	 * @param stack
	 *            the directions of current path
	 * @param quality
	 *            the quality of the solution
	 */
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			double quality);

	/**
	 * Adapt the progress calculated by treating each branch equally to the
	 * progress calculated by estimating remaining calculation time.
	 * <ul>
	 * <li>If the input is <code>0.0</code>, the output is <code>0.0</code>.</li>
	 * <li>If the input is <code>1.0</code>, the output is <code>1.0</code>.</li>
	 * </ul>
	 * 
	 * @param progress
	 *            a <code>double</code> between <code>0.0</code> and
	 *            <code>1.0</code>, non-strictly increasing until
	 *            {@link #reset()}
	 * @return a <code>double</code> between <code>0.0</code> and
	 *         <code>1.0</code>
	 */
	public double adaptProgress(double progress);

	/**
	 * The directions to go?
	 * 
	 * @return the set of directions to go
	 */
	public EnumSet<Direction> getDirections();
}
