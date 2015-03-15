package tosram;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Build a round path from a linear path.
 * 
 * @author johnchen902
 */
public class RoundPath {
	private Path2D.Double path = new Path2D.Double();
	private List<Shape> segements = new ArrayList<Shape>();
	private Point2D.Double p1, p2;
	private final double coef;

	public RoundPath() {
		this(2.0 / 3.0);
	}

	public RoundPath(double coef) {
		this.coef = coef;
	}

	private void addPoint(Point2D.Double p3) {
		if (p2 == null) {
			path.moveTo(p3.x, p3.y);
		} else if (p1 == null) {
			segements.add(new Line2D.Double(p2.x, p2.y, (p2.x + p3.x) / 2,
					(p2.y + p3.y) / 2));
			path.lineTo((p2.x + p3.x) / 2, (p2.y + p3.y) / 2);
		} else {
			double startX = (p1.x + p2.x) / 2;
			double startY = (p1.y + p2.y) / 2;
			double endX = (p2.x + p3.x) / 2;
			double endY = (p2.y + p3.y) / 2;
			double ctrlX1 = startX + (coef * (p2.x - startX));
			double ctrlY1 = startY + (coef * (p2.y - startY));
			double ctrlX2 = endX + (coef * (p2.x - endX));
			double ctrlY2 = endY + (coef * (p2.y - endY));
			segements.add(new CubicCurve2D.Double(startX, startY, ctrlX1,
					ctrlY1, ctrlX2, ctrlY2, endX, endY));
			path.curveTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, endX, endY);
		}
		p1 = p2;
		p2 = p3;
	}

	private void end() {
		if (p1 != null) {
			segements.add(new Line2D.Double((p1.x + p2.x) / 2,
					(p1.y + p2.y) / 2, p2.x, p2.y));
			path.lineTo(p2.x, p2.y);
		}
		p1 = p2 = null;
	}

	/**
	 * Adds a point to the path by moving to the specified coordinates specified
	 * in double precision.
	 * 
	 * @param x
	 *            the specified X coordinate
	 * @param y
	 *            the specified Y coordinate
	 */
	public void moveTo(double x, double y) {
		end();
		addPoint(new Point2D.Double(x, y));
	}

	/**
	 * Adds a rounded segment, defined by a new points, to the path by drawing a
	 * curve that specified point (x, y) as a control point. All coordinates are
	 * specified in double precision.
	 * 
	 * @param x
	 *            the X coordinate of the control point
	 * @param y
	 *            the X coordinate of the control point
	 */
	public void roundTo(double x, double y) {
		addPoint(new Point2D.Double(x, y));
	}

	/**
	 * Get the round path built from previously specified points.
	 * 
	 * @return a <code>Path2D.Double</code>
	 */
	public Path2D.Double getPath() {
		Path2D.Double path = new Path2D.Double(this.path);
		if (p1 != null)
			path.lineTo(p2.x, p2.y);
		return path;
	}

	/**
	 * Get the segments used to build the round path.
	 * 
	 * @return a list of <code>Shape</code>, each of which represents a segment.
	 */
	public List<Shape> getSegements() {
		List<Shape> segements = new ArrayList<Shape>(this.segements);
		if (p1 != null)
			segements.add(new Line2D.Double((p1.x + p2.x) / 2,
					(p1.y + p2.y) / 2, p2.x, p2.y));
		return segements;
	}
}
