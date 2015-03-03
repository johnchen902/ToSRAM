package tosram.view;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tosram.Direction;
import tosram.Path;

/**
 * Utility to display a {@link Path}.
 * 
 * @author johnchen902
 */
public class PathCalculator {

	private PathCalculator() {
	}

	private static class Data {
		private int x1, y1, x2, y2;
		private Direction d1, d2;

		private Data(int x1, int y1, int x2, int y2, Direction d1, Direction d2) {
			if (d1 == null && d2 == null)
				throw new IllegalArgumentException("cannot be both null");
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.d1 = d1;
			this.d2 = d2;
		}

		private boolean isStraight() {
			return d1 != null && d2 != null && d1.getOppsite() == d2;
		}
	}

	private static List<Data> calculateData(Path path) {
		List<Data> data = new ArrayList<>();
		int x1 = path.getBeginPoint().x, y1 = path.getBeginPoint().y;
		List<Direction> directions = path.getDirections();
		for (int i = 0; i <= directions.size(); i++) {
			Direction d1 = i == 0 ? null : directions.get(i - 1).getOppsite();
			Direction d2 = i == directions.size() ? null : directions.get(i);

			loop: for (Point p = new Point();; nextPoint(p)) {
				Data dt = new Data(x1, y1, p.x, p.y, d1, d2);
				for (Data d : data)
					if (isBad(d, dt))
						continue loop;
				data.add(dt);
				break;
			}

			if (d2 != null) {
				x1 += d2.getX();
				y1 += d2.getY();
			}
		}
		return data;
	}

	/**
	 * Get points to display from the specified <code>Path</code>.
	 * 
	 * @param path
	 *            the <code>Path</code>
	 * @param cw
	 *            the width of a cell
	 * @param ch
	 *            the height of a cell
	 * @param dw
	 *            the base adjustment of X value
	 * @param dh
	 *            the base adjustment of Y value
	 * @return a list of <code>Point2D</code>
	 */
	public static List<Point2D> calculatePoints(Path path, double cw,
			double ch, double dw, double dh) {
		if (path.getDirections().size() == 0) {
			double x = cw * (path.getBeginPoint().x + .5);
			double y = ch * (path.getBeginPoint().y + .5);
			return Arrays.asList(new Point2D.Double(x, y));
		}

		List<Data> data = calculateData(path);
		List<Point2D> points = new ArrayList<>(data.size());
		for (Data d : data) {
			double x = cw * (d.x1 + .5) + dw * d.x2;
			double y = ch * (d.y1 + .5) + dh * d.y2;
			points.add(new Point2D.Double(x, y));
		}

		return points;
	}

	/**
	 * Get a <code>Path2D</code> from the specified list of points
	 * 
	 * @param points
	 *            a list of <code>Point2D</code>
	 * @return the corresponding <code>Path2D</code>
	 */
	public static Path2D calculatePath(List<Point2D> points) {
		Path2D p2d = new Path2D.Double();
		p2d.moveTo(points.get(0).getX(), points.get(0).getY());
		for (int i = 1; i < points.size(); i++)
			p2d.lineTo(points.get(i).getX(), points.get(i).getY());
		return p2d;
	}

	/**
	 * Get a <code>Path2D</code> from the specified <code>Path</code>.
	 * 
	 * @param path
	 *            the <code>Path</code>
	 * @param cw
	 *            the width of a cell
	 * @param ch
	 *            the height of a cell
	 * @param dw
	 *            the base adjustment of X value
	 * @param dh
	 *            the base adjustment of Y value
	 * @return the corresponding <code>Path2D</code>
	 */
	public static Path2D calculatePath(Path path, double cw, double ch,
			double dw, double dh) {
		return calculatePath(calculatePoints(path, cw, ch, dw, dh));
	}

	private static void nextPoint(Point p) {
		if (p.x >= p.y && p.x + p.y <= 0)
			p.x++;
		else if (p.x > p.y)
			p.y++;
		else if (p.x + p.y > 0)
			p.x--;
		else
			p.y--;
	}

	private static boolean isBad(Data a, Data b) {
		if (a.x1 != b.x1 || a.y1 != b.y1)
			return false;
		if (a.isStraight() && b.isStraight())
			return isOnSameLine(a, b);
		if (!a.isStraight() && isOnPoint(b, a.x2, a.y2))
			return true;
		if (!b.isStraight() && isOnPoint(a, b.x2, b.y2))
			return true;
		return false;
	}

	private static boolean isOnSameLine(Data a, Data b) {
		Direction da = getRepresentDirection(a);
		Direction db = getRepresentDirection(b);
		if (da != db)
			return false;
		int line1 = a.x2 * da.getY() - a.y2 * da.getX();
		int line2 = b.x2 * db.getY() - b.y2 * db.getX();
		return line1 == line2;
	}

	private static Direction getRepresentDirection(Data a) {
		if (a.d1.ordinal() < a.d2.ordinal())
			return a.d1;
		else
			return a.d2;
	}

	private static boolean isOnPoint(Data a, int x, int y) {
		if (a.d1 != null && isOnPoint(a.x2, a.y2, a.d1, x, y))
			return true;
		if (a.d2 != null && isOnPoint(a.x2, a.y2, a.d2, x, y))
			return true;
		return false;
	}

	private static boolean isOnPoint(int x1, int y1, Direction d, int x2, int y2) {
		// opposite direction
		if ((x2 - x1) * d.getX() < 0)
			return false;
		if ((y2 - y1) * d.getY() < 0)
			return false;
		// horizontal or vertical
		if (d.getX() == 0)
			return x1 == x2;
		if (d.getY() == 0)
			return y1 == y2;
		// Should be division, but it's +1 or -1 anyway
		return (x2 - x1) * d.getX() == (y2 - y1) * d.getY();
	}
}
