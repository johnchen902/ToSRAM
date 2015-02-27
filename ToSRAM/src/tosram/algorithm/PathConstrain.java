package tosram.algorithm;

import tosram.Direction;
import tosram.MutableRuneMap;

/**
 * The constrain about what paths can be made.
 * 
 * @author johnchen902
 */
public interface PathConstrain {

	/**
	 * Determines whether the path can start from the specified location.
	 * 
	 * @param startingX
	 *            the X coordinate of starting location
	 * @param startingY
	 *            the Y coordinate of starting location
	 * @param map
	 *            the current <code>MutableRuneMap</code>
	 * @return <code>true</code> if the path can start from the specified point;
	 *         <code>false</code> otherwise
	 */
	public boolean canStart(int startingX, int startingY, MutableRuneMap map);

	/**
	 * Determines whether it can move with the specified direction.
	 * 
	 * @param startingX
	 *            the X coordinate of starting location
	 * @param startingY
	 *            the Y coordinate of starting location
	 * @param direction
	 *            the direction in question
	 * @param resultingX
	 *            the X coordinate of resulting location
	 * @param resultingY
	 *            the Y coordinate of resulting location
	 * @param map
	 *            the current <code>MutableRuneMap</code>
	 * @return <code>true</code> if it can move with the specified direction;
	 *         <code>false</code> otherwise
	 */
	public boolean canMove(int startingX, int startingY, Direction direction,
			int resultingX, int resultingY, MutableRuneMap map);
}
