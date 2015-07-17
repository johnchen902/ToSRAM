package tosram.algorithm.path;

import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.algorithm.PathRestriction;

/**
 * Restriction which disallows U-turns i.e. consecutive opposite moves, which
 * would be useless in most cases.
 * 
 * @author johnchen902
 */
public class UTurnRestriction implements PathRestriction {
	@Override
	public boolean canStart(int startingX, int startingY, MutableRuneMap map) {
		return true;
	}

	@Override
	public boolean canMove(int startingX, int startingY,
			List<Direction> directions, Direction direction, int resultingX,
			int resultingY, MutableRuneMap map) {
		return directions.isEmpty()
				|| directions.get(directions.size() - 1).getOppsite() != direction;
	}
}
