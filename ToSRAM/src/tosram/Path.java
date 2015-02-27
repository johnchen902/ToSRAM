package tosram;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Path to move stones.
 * 
 * @author johnchen902
 */
public class Path {

	private final Point beginPoint;
	private final List<Direction> directions;

	/**
	 * Constructs a path beginning from the specified point, with the directions
	 * specified by the list.
	 * 
	 * @param point
	 *            the point the path begin with
	 * @param list
	 *            the list of directions
	 * @throws NullPointerException
	 *             if the specified point or collection is null
	 */
	public Path(Point point, Collection<Direction> list) {
		this.beginPoint = new Point(point);
		this.directions = Collections
				.unmodifiableList(new ArrayList<Direction>(list));
	}

	/**
	 * Copy constructor.
	 * 
	 * @param path
	 *            the path to copy from
	 * @throws NullPointerException
	 *             if <code>path</code> is <code>null</code>
	 */
	public Path(Path path) {
		this(path.beginPoint, path.directions);
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
	 * Return if the points and the contents of underling list are equal.
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
		MutableRuneMap newMap = map.toMutable();
		int x = path.getBeginPoint().x, y = path.getBeginPoint().y;
		Iterator<Direction> ite = path.getDirections().iterator();
		while (steps-- != 0) {
			Direction dir = ite.next();
			int px = x, py = y;
			x += dir.getX();
			y += dir.getY();
			RuneStone stone1 = newMap.getRuneStone(x, y);
			RuneStone stone2 = newMap.getRuneStone(px, py);
			newMap.setRuneStone(x, y, stone2);
			newMap.setRuneStone(px, py, stone1);
		}
		return new RuneMap(newMap);
	}
}
