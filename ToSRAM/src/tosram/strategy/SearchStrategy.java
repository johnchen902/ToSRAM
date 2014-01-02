package tosram.strategy;

import java.util.Deque;

import tosram.ComboDescriber;
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
	 * @param combos
	 *            an description of combo of the map
	 */
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboDescriber combos);

	/**
	 * Do you want to move stones diagonally?
	 * 
	 * @return <code>true</code> if to move stones diagonally;
	 *         <code>false</code> otherwise
	 */
	public boolean isToDiagonal();

	/**
	 * Do you want to stop this branch?
	 * 
	 * @return <code>true</code> if to stop this branch; <code>false</code>
	 *         otherwise
	 */
	public boolean isToStop();
}