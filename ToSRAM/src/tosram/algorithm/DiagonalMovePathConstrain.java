package tosram.algorithm;

import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;

/**
 * A <code>PathConstrain</code> that forbids diagonal move.
 * 
 * @author johnchen902
 */
public class DiagonalMovePathConstrain implements PathConstrain {
	@Override
	public boolean canStart(int startingX, int startingY, MutableRuneMap map) {
		return true;
	}

	@Override
	public boolean canMove(int startingX, int startingY,
			List<Direction> directions, Direction direction, int resultingX,
			int resultingY, MutableRuneMap map) {
		return direction.ordinal() < 4;
	}
}
