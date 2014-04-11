package tosram.xrobot;

import java.util.Deque;

import tosram.Direction;

/**
 * A <code>Moving</code> which goes all directions, normal moves cost 1 and
 * diagonal moves cost an client specified number.
 * 
 * @author johnchen902
 */
public class DiagonalMoving implements Moving {

	private final int diagonalCost;

	/**
	 * Create a <code>DiagonalMoving</code> with the default cost of diagonal
	 * moves.
	 */
	public DiagonalMoving() {
		this(2);
	}

	/**
	 * Create a <code>DiagonalMoving</code> with the specified number as the
	 * cost of diagonal moves.
	 * 
	 * @param diagonalCost
	 *            the specified cost of diagonal moves
	 */
	public DiagonalMoving(int diagonalCost) {
		this.diagonalCost = diagonalCost;
	}

	@Override
	public int cost(Direction d, Deque<Direction> stack) {
		return d.ordinal() < 4 ? 1 : diagonalCost;
	}

	@Override
	public Direction[] getDirections(int x, int y) {
		return Direction.values();
	}
}
