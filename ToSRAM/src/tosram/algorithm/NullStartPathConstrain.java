package tosram.algorithm;

import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;

/**
 * A <code>PathConstrain</code> that forbids exchanging two same stone as the
 * first move.
 * 
 * @author johnchen902
 */
public class NullStartPathConstrain implements PathConstrain {
	@Override
	public boolean canStart(int startingX, int startingY, MutableRuneMap map) {
		return true;
	}

	@Override
	public boolean canMove(int startingX, int startingY,
			List<Direction> directions, Direction direction, int resultingX,
			int resultingY, MutableRuneMap map) {
		return directions.size() != 0
				|| map.getRuneStone(startingX, startingY) != map.getRuneStone(
						resultingX, resultingY);
	}
}
