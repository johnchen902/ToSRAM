package tosram;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Path, a starting point and a sequence of directions, representing moves made
 * to a rune map. Guaranteed to be immutable.
 * 
 * @author johnchen902
 */
public final class Path {

	private final Point beginPoint;
	private final List<Direction> directions;

	/**
	 * Constructs a path starting from the specified point and moving toward
	 * directions specified by the list.
	 * 
	 * @param point
	 *            the point the path starts with
	 * @param list
	 *            the list of directions moving toward
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 */
	public Path(Point point, Collection<Direction> list) {
		this.beginPoint = new Point(point);
		this.directions = Collections
				.unmodifiableList(new ArrayList<Direction>(list));
	}

	/**
	 * The starting point of the path.
	 * 
	 * @return the starting point
	 */
	public Point getBeginPoint() {
		return new Point(beginPoint);
	}

	/**
	 * Get directions of the path.
	 * 
	 * @return the directions
	 */
	public List<Direction> getDirections() {
		return directions;
	}

	/**
	 * Return if the starting points and the directions are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Path))
			return false;
		Path that = (Path) obj;
		return beginPoint.equals(that.beginPoint)
				&& directions.equals(that.directions);
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + directions.hashCode();
		hash = hash * 31 + beginPoint.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return "Path[point=" + beginPoint + ", path="
				+ Arrays.toString(directions.toArray()) + "]";
	}

	/**
	 * Create a map by following the specified path.
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
	 * Create a map by following the specified path for the specified number of
	 * moves.
	 * 
	 * @param map
	 *            the original map
	 * @param path
	 *            the specified path
	 * @param steps
	 *            the number of moves followed
	 * @return the map after following
	 */
	public static RuneMap follow(RuneMap map, Path path, int steps) {
		if (steps < 0 || steps > path.getDirections().size())
			throw new IndexOutOfBoundsException("steps=" + steps);
		MutableRuneMap newMap = map.toMutable();
		int x = path.getBeginPoint().x, y = path.getBeginPoint().y;
		Iterator<Direction> ite = path.getDirections().iterator();
		while (steps-- != 0) {
			Direction dir = ite.next();
			newMap.swap(x, y, x += dir.getX(), y += dir.getY());
		}
		return new RuneMap(newMap);
	}
}
