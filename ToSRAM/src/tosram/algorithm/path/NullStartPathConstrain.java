package tosram.algorithm.path;

import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.RuneStone;
import tosram.algorithm.PathConstrain;

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
		if (directions.size() != 1)
			return true;
		RuneStone stone1 = map.getRuneStone(startingX, startingY);
		startingX += directions.get(0).getX();
		startingY += directions.get(0).getY();
		RuneStone stone2 = map.getRuneStone(startingX, startingY);
		return stone1 != stone2;
	}
}
