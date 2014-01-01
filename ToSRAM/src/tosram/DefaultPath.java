package tosram;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A default implement of {@link Path}.
 * 
 * @author johnchen902
 */
public class DefaultPath implements Path {

	private final Point point;
	private final List<Direction> path;

	/**
	 * Constructs a path beginning from the specified point, with the directions
	 * specified by the list.
	 * 
	 * @param point
	 *            the specified point
	 * @param list
	 *            the specified list
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public DefaultPath(Point point, Collection<Direction> list) {
		this.point = new Point(point);
		this.path = Collections
				.unmodifiableList(new ArrayList<Direction>(list));
	}

	@Override
	public Point getBeginPoint() {
		return new Point(point);
	}

	@Override
	public List<Direction> getDirections() {
		return path;
	}

	/**
	 * Return if the points and the contents of underling list are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DefaultPath))
			return false;
		DefaultPath that = (DefaultPath) obj;
		return point.equals(that.point) && path.equals(that.path);
	}

	/**
	 * Computes a hash code for this path.
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + path.hashCode();
		hash = hash * 31 + point.hashCode();
		return hash;
	}

	/**
	 * Return the point and the content of underling list.
	 */
	@Override
	public String toString() {
		return "DefaultPath[point=" + point + ", path="
				+ Arrays.toString(path.toArray()) + "]";
	}

}
