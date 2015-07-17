package tosram.algorithm.path;

import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.algorithm.PathRestriction;

/**
 * Restriction which disallows diagonal moves.
 * 
 * @author johnchen902
 */
public class DiagonalMoveRestriction implements PathRestriction {

	@Override
	public boolean canStart(int startingX, int startingY, MutableRuneMap map) {
		return true;
	}

	@Override
	public boolean canMove(int startingX, int startingY,
			List<Direction> directions, Direction direction, int resultingX,
			int resultingY, MutableRuneMap map) {
		return !direction.isDiagonal();
	}
}
