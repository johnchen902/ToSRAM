package tosram.xrobot;

import tosram.Direction;

/**
 * An interface determining possible moves and the cost of moves.
 * 
 * @author johnchen902
 */
public interface Moving {

	/**
	 * Get possible moves.
	 * 
	 * @param x
	 *            the current X coordination
	 * @param y
	 *            the current Y coordination
	 * @return an array of possible moves
	 */
	public Direction[] getDirections(int x, int y);

	/**
	 * Get the cost of a move.
	 * 
	 * @param d
	 *            the move
	 * @return the cost
	 */
	public int cost(Direction d);
}