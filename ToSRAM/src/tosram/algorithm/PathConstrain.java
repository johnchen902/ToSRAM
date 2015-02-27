package tosram.algorithm;

import java.awt.Point;

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
	 * @param startingPoint
	 *            the starting location in question
	 * @param map
	 *            the current <code>MutableRuneMap</code>
	 * @return <code>true</code> if the path can start from the specified point;
	 *         <code>false</code> otherwise
	 */
	public boolean canStart(Point startingPoint, MutableRuneMap map);

	/**
	 * Determines whether it can move with the specified direction.
	 * 
	 * @param startingPoint
	 *            the starting location of the path
	 * @param direction
	 *            the direction in question
	 * @param resultingPoint
	 *            the resulting location if moved
	 * @param map
	 *            the current <code>MutableRuneMap</code>
	 * @return <code>true</code> if it can move with the specified direction;
	 *         <code>false</code> otherwise
	 */
	public boolean canMove(Point startingPoint, Direction direction,
			Point resultingPoint, MutableRuneMap map);
}
