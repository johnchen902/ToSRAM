package tosram.algorithm.path;

import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.RuneStone;
import tosram.algorithm.PathRestriction;

/**
 * Restriction which prevents moves starting with exchanging two identical
 * stones from continuing, which would be pointless in most cases.<br>
 * Exchanging two identical stones without continuing is allowed because it may
 * be the best or even the only move possible. (Consider case where all stones
 * are the same.)
 * 
 * @author johnchen902
 */
public class IdenticalStartRestriction implements PathRestriction {

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
		int middleX = startingX + directions.get(0).getX();
		int middleY = startingY + directions.get(0).getY();
		RuneStone stone1 = map.getRuneStone(startingX, startingY);
		RuneStone stone2 = map.getRuneStone(middleX, middleY);
		return stone1 != stone2;
	}
}
