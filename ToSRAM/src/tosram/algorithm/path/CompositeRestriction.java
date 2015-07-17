package tosram.algorithm.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.algorithm.PathRestriction;

/**
 * This class take some restriction and allows a move if and only if all
 * provided restrictions allow it.
 * 
 * @author johnchen902
 */
public class CompositeRestriction implements PathRestriction {

	private List<PathRestriction> restrictions;

	/**
	 * Returns a restriction that allows a move if and only if all restrictions
	 * in the list allow it.
	 * 
	 * @param restrictions
	 *            the specified list
	 * @return a composite restriction
	 */
	public static PathRestriction composite(List<PathRestriction> restrictions) {
		Objects.requireNonNull(restrictions);
		if (restrictions.isEmpty())
			return new NullRestriction();
		else if (restrictions.size() == 1)
			return restrictions.get(0);
		else
			return new CompositeRestriction(restrictions);
	}

	private CompositeRestriction(List<PathRestriction> restrictions) {
		this.restrictions = new ArrayList<>(restrictions);
	}

	@Override
	public boolean canStart(int stX, int stY, MutableRuneMap map) {
		for (PathRestriction restriction : restrictions)
			if (!restriction.canStart(stX, stY, map))
				return false;
		return true;
	}

	@Override
	public boolean canMove(int stX, int stY, List<Direction> dirs,
			Direction dir, int rsX, int rsY, MutableRuneMap map) {
		for (PathRestriction restriction : restrictions)
			if (!restriction.canMove(stX, stY, dirs, dir, rsX, rsY, map))
				return false;
		return true;
	}
}
