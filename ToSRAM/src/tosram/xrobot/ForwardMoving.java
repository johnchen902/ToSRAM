package tosram.xrobot;

import java.util.Arrays;
import java.util.Deque;

import tosram.Direction;

/**
 * A moving that prefers keeping the same direction, that is, penalizes changing
 * direction.
 * 
 * @author johnchen902
 */
public class ForwardMoving implements Moving {

	private final int turnCost;

	/**
	 * Construct a moving that penalizes direction-changing moves.
	 * 
	 * @param turnCost
	 *            the cost of a direction-changing move
	 */
	public ForwardMoving(int turnCost) {
		this.turnCost = turnCost;
	}

	@Override
	public int cost(Direction d, Deque<Direction> stack) {
		return d == stack.peekLast() ? 1 : turnCost;
	}

	@Override
	public Direction[] getDirections(int x, int y) {
		return Arrays.copyOf(Direction.values(), 4);
	}
}
