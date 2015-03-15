package tosram.algorithm.path;

import java.util.ArrayList;
import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.algorithm.PathConstrain;

/**
 * A <code>PathConstrain</code> that permits only when all provided
 * <code>PathConstrain</code>s permits.
 * 
 * @author johnchen902
 */
public class CompositePathConstrain implements PathConstrain {

	private List<PathConstrain> constrains;

	/**
	 * Create a <code>CompositePathConstrain</code> with the specified list of
	 * <code>PathConstrain</code>
	 * 
	 * @param constrains
	 *            the specified list of <code>PathConstrain</code>
	 */
	public CompositePathConstrain(List<PathConstrain> constrains) {
		this.constrains = new ArrayList<>(constrains);
	}

	@Override
	public boolean canStart(int startingX, int startingY, MutableRuneMap map) {
		for (PathConstrain c : constrains)
			if (!c.canStart(startingX, startingY, map))
				return false;
		return true;
	}

	@Override
	public boolean canMove(int startingX, int startingY,
			List<Direction> directions, Direction direction, int resultingX,
			int resultingY, MutableRuneMap map) {
		for (PathConstrain c : constrains)
			if (!c.canMove(startingX, startingY, directions, direction,
					resultingX, resultingY, map))
				return false;
		return true;
	}
}
