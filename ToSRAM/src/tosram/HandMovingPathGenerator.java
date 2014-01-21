package tosram;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A <code>MovingPathGenerator</code> that simulates a path moves by hand.
 * 
 * @author johnchen902
 */
public class HandMovingPathGenerator implements MovingPathGenerator {

	@SuppressWarnings("incomplete-switch")
	private Path2D.Double buildPath2D(Path path, Rectangle r, Dimension d) {
		RoundPath gp = new RoundPath();
		Point p = new Point(path.getBeginPoint());
		{
			double x = r.x + r.width * (p.x + 0.5) / d.width;
			double y = r.y + r.height * (p.y + 0.5) / d.height;
			gp.moveTo(x, y);
		}
		for (Direction dir : path.getDirections()) {
			switch (dir) {
			case WEST:
			case WEST_NORTH:
			case WEST_SOUTH:
				p.x--;
				break;
			case EAST:
			case EAST_NORTH:
			case EAST_SOUTH:
				p.x++;
				break;
			}
			switch (dir) {
			case SOUTH:
			case WEST_SOUTH:
			case EAST_SOUTH:
				p.y++;
				break;
			case NORTH:
			case WEST_NORTH:
			case EAST_NORTH:
				p.y--;
				break;
			}
			{
				double x = r.x + r.width * (p.x + 0.5) / d.width;
				double y = r.y + r.height * (p.y + 0.5) / d.height;
				gp.roundTo(x, y);
			}
		}
		return gp.getPath();
	}

	private Point2D.Double getNext(PathIterator pi) {
		if (pi.isDone()) {
			return null;
		} else {
			// assume pi consists only of MOVETO or LINETO
			double[] fs = new double[2];
			pi.currentSegment(fs);
			pi.next();
			return new Point2D.Double(fs[0], fs[1]);
		}
	}

	private List<Point> samplePoints(Path2D.Double path) {
		List<Point> lps = new ArrayList<>();
		PathIterator pi = path.getPathIterator(null, 1.0);
		// assume pi = MOVETO LINETO* (no CLOSE)
		Point2D.Double last = getNext(pi), current;
		while ((current = getNext(pi)) != null) {
			int cut = (int) Math.ceil(last.distance(current) / 10);
			double subXDiff = (current.x - last.x) / cut;
			double subYDiff = (current.y - last.y) / cut;
			for (int i = 0; i < cut; i++)
				lps.add(new Point((int) Math.round(last.x + i * subXDiff),
						(int) Math.round(last.y + i * subYDiff)));
			last = current;
		}
		lps.add(new Point((int) Math.round(last.x), (int) Math.round(last.y)));
		return lps;
	}

	private void removeAmbiguousPoints(List<Point> points, Path path,
			Rectangle screen, Dimension mapSize) {
		for (Iterator<Point> iterator = points.iterator(); iterator.hasNext();) {
			Point point = iterator.next();
			double bx = (point.getX() - screen.x) / screen.width
					* mapSize.width;
			double by = (point.getY() - screen.y) / screen.height
					* mapSize.height;
			if (Math.abs(bx - Math.rint(bx)) < 0.05
					&& Math.abs(by - Math.rint(by)) < 0.05)
				iterator.remove();
		}
	}

	private List<Move> allotTime(List<Point> points, int time,
			boolean startImmediately) {
		List<Move> moves = new ArrayList<>();
		int delayD = 0, size = points.size();
		for (Point p : points) {
			if ((delayD += time % size) >= size) {
				delayD -= size;
				moves.add(new Move(p, time / size + 1));
			} else {
				moves.add(new Move(p, time / size));
			}
		}
		if (!startImmediately) {
			moves.get(0).setDelay(3000);
		}
		return moves;
	}

	@Override
	public List<Move> getMovePath(Path path, Rectangle screen,
			Dimension mapSize, int time, boolean startImmediately) {
		Path2D.Double gp = buildPath2D(path, screen, mapSize);
		List<Point> points = samplePoints(gp);
		removeAmbiguousPoints(points, path, screen, mapSize);
		List<Move> moves = allotTime(points,
				Math.min(path.getDirections().size() * 100, time),
				startImmediately);
		return moves;
	}

}
