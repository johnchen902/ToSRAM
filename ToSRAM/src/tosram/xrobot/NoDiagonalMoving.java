package tosram.xrobot;

import java.util.Arrays;

import tosram.Direction;

/**
 * A <code>Moving</code> which never go diagonally and all moves cost 1.
 * 
 * @author johnchen902
 */
public class NoDiagonalMoving implements Moving {

	public int cost(Direction d) {
		return 1;
	}

	public Direction[] getDirections(int x, int y) {
		return Arrays.copyOf(Direction.values(), 4);
	}
}
