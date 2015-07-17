package tosram.algorithm;

import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;

/**
 * Restrictions about what moves can be made.
 * 
 * @author johnchen902
 */
public interface PathRestriction {

	/**
	 * Determines whether it's allowed to start from the specified location.
	 * 
	 * @param startingX
	 *            the X coordinate of starting location
	 * @param startingY
	 *            the Y coordinate of starting location
	 * @param map
	 *            the initial map
	 * @return <code>true</code> if it's allowed to start from the specified
	 *         location; <code>false</code> otherwise
	 */
	public boolean canStart(int startingX, int startingY, MutableRuneMap map);

	/**
	 * Determines whether it's allowed to move toward the specified direction.
	 * 
	 * @param startingX
	 *            the X coordinate of starting location
	 * @param startingY
	 *            the Y coordinate of starting location
	 * @param directions
	 *            the directions already moved
	 * @param direction
	 *            the direction in question
	 * @param resultingX
	 *            the X coordinate of resulting location
	 * @param resultingY
	 *            the Y coordinate of resulting location
	 * @param map
	 *            the map resulted from <code>directions</code> but before
	 *            <code>direction</code>
	 * @return <code>true</code> if it's allowed to move toward the specified
	 *         direction; <code>false</code> otherwise
	 */
	public boolean canMove(int startingX, int startingY,
			List<Direction> directions, Direction direction, int resultingX,
			int resultingY, MutableRuneMap map);
}
