package tosram;

import java.util.Iterator;

/**
 * This class consists exclusively of static methods that operate on or return
 * <code>Path</code>.
 * 
 * @author johnchen902
 */
public class Paths {
	private Paths() {
	}

	/**
	 * Return what a map will be following the specified path.
	 * 
	 * @param map
	 *            the original map
	 * @param path
	 *            the specified path
	 * @return the map after following
	 */
	public static RuneMap follow(RuneMap map, Path path) {
		return follow(map, path, path.getDirections().size());
	}

	/**
	 * Return what a map will be following the specified path for specified
	 * steps.
	 * 
	 * @param map
	 *            the original map
	 * @param path
	 *            the specified path
	 * @param steps
	 *            the number of steps followed
	 * @return the map after following
	 */
	public static RuneMap follow(RuneMap map, Path path, int steps) {
		if (steps < 0 || steps > path.getDirections().size())
			throw new IndexOutOfBoundsException("steps=" + steps);
		RuneMap newMap = new RuneMap(map);
		int x = path.getBeginPoint().x, y = path.getBeginPoint().y;
		Iterator<Direction> ite = path.getDirections().iterator();
		while (steps-- != 0) {
			Direction dir = ite.next();
			int px = x, py = y;
			switch (dir) {
			case WEST:
			case WEST_NORTH:
			case WEST_SOUTH:
				x--;
				break;
			case EAST:
			case EAST_NORTH:
			case EAST_SOUTH:
				x++;
				break;
			case NORTH:
			case SOUTH:
				break;
			}
			switch (dir) {
			case SOUTH:
			case WEST_SOUTH:
			case EAST_SOUTH:
				y++;
				break;
			case NORTH:
			case WEST_NORTH:
			case EAST_NORTH:
				y--;
				break;
			case WEST:
			case EAST:
				break;
			}
			RuneStone stone1 = newMap.getStone(x, y);
			RuneStone stone2 = newMap.getStone(px, py);
			newMap.setRuneStone(x, y, stone2);
			newMap.setRuneStone(px, py, stone1);
		}
		return newMap;
	}
}
