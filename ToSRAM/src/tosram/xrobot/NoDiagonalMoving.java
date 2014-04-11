package tosram.xrobot;

import java.util.Arrays;
import java.util.Deque;

import tosram.Direction;

/**
 * A <code>Moving</code> which never go diagonally and all moves cost 1.
 * 
 * @author johnchen902
 */
public class NoDiagonalMoving implements Moving {

	@Override
	public int cost(Direction d, Deque<Direction> stack) {
		return 1;
	}

	@Override
	public Direction[] getDirections(int x, int y) {
		return Arrays.copyOf(Direction.values(), 4);
	}
}
