package tosram.view;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import tosram.Direction;
import tosram.Path;

class PathPanelCalculator {

	private PathPanelCalculator() {
	}

	private static Pair<Integer, Integer> minmax(int x1, int x2) {
		return x1 <= x2 ? Pair.with(x1, x2) : Pair.with(x2, x1);
	}

	private static int findLessThan(List<Integer> list, int cmp) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) < cmp)
				return i;
		return -1;
	}

	private static Point2D.Double solveEquation(
			Triplet<Double, Double, Double> eq1,
			Triplet<Double, Double, Double> eq2) {
		double delta = eq1.getValue0() * eq2.getValue1() - eq1.getValue1()
				* eq2.getValue0();
		double deltaX = eq1.getValue2() * eq2.getValue1() - eq1.getValue1()
				* eq2.getValue2();
		double deltaY = eq1.getValue0() * eq2.getValue2() - eq1.getValue2()
				* eq2.getValue0();
		return new Point2D.Double(deltaX / delta, deltaY / delta);
	}

	private static Direction getRepresentDirection(Direction d) {
		switch (d) {
		case WEST:
		case EAST:
			return Direction.WEST;
		case NORTH:
		case SOUTH:
			return Direction.NORTH;
		case WEST_SOUTH:
		case EAST_NORTH:
			return Direction.WEST_SOUTH;
		case WEST_NORTH:
		case EAST_SOUTH:
			return Direction.WEST_NORTH;
		default:
			return null; // impossible
		}
	}

	private static void follow(Point p, Direction d) {
		p.x += d.getX();
		p.y += d.getY();
	}

	private static Point2D.Double seperatePoint(Point2D.Double p1,
			Point2D.Double p2, double a, double c) {
		return new Point2D.Double((p1.x * (c - a) + p2.x * a) / c, //
				(p1.y * (c - a) + p2.y * a) / c);
	}

	private static void addPoint(
			Map<Pair<Direction, Integer>, List<Triplet<Integer, Integer, Integer>>> data,
			Point p1, Point p2, Direction dir, int pos) {
		Direction repre = getRepresentDirection(dir);
		int key;
		Pair<Integer, Integer> value;
		switch (repre) {
		case WEST:
			key = p1.y;
			value = minmax(p1.x, p2.x);
			break;
		case NORTH:
			key = p1.x;
			value = minmax(p1.y, p2.y);
			break;
		case WEST_SOUTH:
			key = p1.x + p1.y;
			value = minmax(p1.y, p2.y);
			break;
		case WEST_NORTH:
			key = p1.x - p1.y;
			value = minmax(p1.y, p2.y);
			break;
		default: // impossible
			throw new AssertionError("unexpected direction " + repre);
		}
		if (!data.containsKey(Pair.with(repre, key)))
			data.put(Pair.with(repre, key),
					new ArrayList<Triplet<Integer, Integer, Integer>>());
		data.get(Pair.with(repre, key)).add(value.add(pos));
	}

	private static Triplet<Double, Double, Double> getEquation(
			Map<Pair<Direction, Integer>, Integer> trackSize, Point p,
			Direction dir, int track_id, double cw, double ch, double dis) {
		Direction repre = getRepresentDirection(dir);
		int key;
		switch (repre) {
		case WEST:
			key = p.y;
			break;
		case NORTH:
			key = p.x;
			break;
		case WEST_SOUTH:
			key = p.x + p.y;
			break;
		case WEST_NORTH:
			key = p.x - p.y;
			break;
		default: // impossible
			throw new AssertionError("unexpected direction " + repre);
		}
		int tracks = trackSize.get(Pair.with(repre, key));
		double offest = (track_id - (tracks - 1) / 2.0) * dis;
		switch (repre) {
		case WEST:
			return Triplet.with(0.0, 1.0, ((p.y + 0.5) * ch) + offest);
		case NORTH:
			return Triplet.with(1.0, 0.0, ((p.x + 0.5) * cw) + offest);
		case WEST_SOUTH:
			return Triplet.with(ch, cw, cw * ch * (p.x + p.y + 1) + (cw + ch)
					* offest);
		case WEST_NORTH:
			return Triplet.with(ch, -cw, cw * ch * (p.x - p.y) + (ch - cw)
					* offest);
		default: // impossible
			throw new AssertionError("unexpected direction " + repre);
		}
	}

	private static Point2D.Double solveOneEquation(
			Triplet<Double, Double, Double> eq, Point p, double cw, double ch) {
		if (eq.getValue0() == 0) { // by = c
			return new Point2D.Double((p.x + 0.5) * cw, eq.getValue2()
					/ eq.getValue1());
		} else {
			return solveEquation(Triplet.with(0.0, 1.0, (p.y + 0.5) * ch), eq);
		}
	}

	private static void moveTo(Path2D.Double path, Point2D.Double p) {
		path.moveTo(p.x, p.y);
	}

	private static void lineTo(Path2D.Double path, Point2D.Double p) {
		path.lineTo(p.x, p.y);
	}

	private static void addSegements(List<Shape> segs, Path2D.Double cornerBuf,
			Point2D.Double p1, Point2D.Double p2, int nseg) {
		lineTo(cornerBuf, seperatePoint(p1, p2, 0.5, nseg));
		segs.add(new Path2D.Double(cornerBuf));
		for (double d = 0.5; d + 1 < nseg; d++) {
			segs.add(new Line2D.Double(seperatePoint(p1, p2, d, nseg),
					seperatePoint(p1, p2, d + 1, nseg)));
		}
		cornerBuf.reset();
		moveTo(cornerBuf, seperatePoint(p1, p2, nseg - 0.5, nseg));
		lineTo(cornerBuf, p2);
	}

	static Pair<Path2D, List<Shape>> calculatePath(Path path, double cw,
			double ch, double dis) {
		if (path.getDirections().isEmpty())
			return Pair.with((Path2D) new Path2D.Double(),
					Collections.<Shape> emptyList());

		// Triplet<begin, end, id>
		Map<Pair<Direction, Integer>, List<Triplet<Integer, Integer, Integer>>> data;
		{
			data = new HashMap<>();
			Point p1 = new Point(path.getBeginPoint()), p2 = new Point(p1);
			Direction lastDirection = null;
			int pos = 0;
			for (Direction dir : path.getDirections()) {
				if (lastDirection != null && lastDirection != dir) {
					addPoint(data, p1, p2, lastDirection, pos++);
					p1.setLocation(p2);
					lastDirection = dir;
				}
				if (lastDirection == null)
					lastDirection = dir;
				follow(p2, dir);
			}
			addPoint(data, p1, p2, lastDirection, pos++);
		}

		Map<Pair<Direction, Integer>, Integer> trackSize = new HashMap<>();
		Map<Integer, Integer> trackId = new HashMap<>();
		for (Entry<Pair<Direction, Integer>, List<Triplet<Integer, Integer, Integer>>> subdata : data
				.entrySet()) {
			Collections.sort(subdata.getValue());
			List<Integer> trackEnd = new ArrayList<>();
			for (Triplet<Integer, Integer, Integer> request : subdata
					.getValue()) {
				int in = findLessThan(trackEnd, request.getValue0());
				if (in != -1) {
					trackId.put(request.getValue2(), in);
					trackEnd.set(in, request.getValue1());
				} else {
					trackId.put(request.getValue2(), trackEnd.size());
					trackEnd.add(request.getValue1());
				}
			}
			trackSize.put(subdata.getKey(), trackEnd.size());
		}

		Path2D.Double ppath = new Path2D.Double();
		Path2D.Double cornerBuf = new Path2D.Double();
		List<Shape> segements = new ArrayList<>();

		int segCount = 1;
		Point2D.Double pLast = null;
		Point p = new Point(path.getBeginPoint());
		// Triplet<a, b, c> ax+by=c
		Triplet<Double, Double, Double> e1 = null;
		Direction lastDirection = null;
		int pos = 0;
		for (Direction dir : path.getDirections()) {
			if (lastDirection != dir) {
				Triplet<Double, Double, Double> e2 = getEquation(trackSize, p,
						dir, trackId.get(pos), cw, ch, dis);
				if (e1 == null) {
					Point2D.Double pCur = solveOneEquation(e2, p, cw, ch);
					cornerBuf.moveTo(pCur.x, pCur.y);
					moveTo(ppath, pLast = pCur);
				} else {
					Point2D.Double pCur = solveEquation(e1, e2);
					addSegements(segements, cornerBuf, pLast, pCur, segCount);
					lineTo(ppath, pLast = pCur);
				}
				e1 = e2;
				pos++;
				lastDirection = dir;
				segCount = 0;
			}
			follow(p, dir);
			segCount++;
		}
		{
			Point2D.Double pCur = solveOneEquation(e1, p, cw, ch);
			addSegements(segements, cornerBuf, pLast, pCur, segCount);
			lineTo(ppath, pLast = pCur);
		}
		segements.add(cornerBuf);
		return Pair.with((Path2D) ppath, segements);
	}
}
