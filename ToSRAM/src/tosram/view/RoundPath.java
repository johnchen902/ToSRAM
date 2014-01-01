package tosram.view;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Build a round path from a jagged path.
 * 
 * @author johnchen902
 */
public class RoundPath {
	private GeneralPath path = new GeneralPath();
	private List<Shape> segements = new ArrayList<Shape>();
	private Point2D.Float p1, p2;

	private void addPoint(Point2D.Float p3) {
		if (p2 == null) {
			path.moveTo(p3.x, p3.y);
		} else if (p1 == null) {
			segements.add(new Line2D.Float(p2.x, p2.y, (p2.x + p3.x) / 2,
					(p2.y + p3.y) / 2));
			path.lineTo((p2.x + p3.x) / 2, (p2.y + p3.y) / 2);
		} else {
			segements.add(new QuadCurve2D.Float((p1.x + p2.x) / 2,
					(p1.y + p2.y) / 2, p2.x, p2.y, (p2.x + p3.x) / 2,
					(p2.y + p3.y) / 2));
			path.quadTo(p2.x, p2.y, (p2.x + p3.x) / 2, (p2.y + p3.y) / 2);
		}
		p1 = p2;
		p2 = p3;
	}

	/**
	 * Add a point to the jagged path.
	 * 
	 * @param x
	 *            the X coordinate of the added point
	 * @param y
	 *            the Y coordinate of the added point
	 */
	public void addPoint(float x, float y) {
		addPoint(new Point2D.Float(x, y));
	}

	/**
	 * End the jagged path.
	 */
	public void end() {
		if (p1 != null) {
			segements.add(new Line2D.Float((p1.x + p2.x) / 2,
					(p1.y + p2.y) / 2, p2.x, p2.y));
			path.lineTo(p2.x, p2.y);
		}
	}

	/**
	 * Get the round path built.
	 * 
	 * @return a <code>GeneralPath</code>
	 */
	public GeneralPath getPath() {
		return (GeneralPath) path.clone();
	}

	/**
	 * Get the segments used to build the rounded path.
	 * 
	 * @return a list of <code>Shape</code>
	 */
	public List<Shape> getSegements() {
		return Collections.unmodifiableList(segements);
	}

}
