package tosram.algorithm;

import tosram.Direction;
import tosram.MutableRuneMap;

/**
 * A <code>PathConstrain</code> that do not constrain the path in any way.
 * 
 * @author johnchen902
 */
public class EmptyPathConstrain implements PathConstrain {

	@Override
	public boolean canStart(int startingX, int startingY, MutableRuneMap map) {
		return true;
	}

	@Override
	public boolean canMove(int startingX, int startingY, Direction direction,
			int resultingX, int resultingY, MutableRuneMap map) {
		return true;
	}
}
