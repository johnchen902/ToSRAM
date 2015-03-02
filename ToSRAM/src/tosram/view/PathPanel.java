package tosram.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import tosram.Path;

/**
 * A panel showing a path.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class PathPanel extends JPanel {

	private Path path;
	private List<Point2D> points;
	private Path2D path2d;
	private double cellWidth = 50;
	private double cellHeight = 50;
	private Timer timer;
	private double animation;

	/**
	 * Create an empty <code>PathPanel</code>
	 */
	public PathPanel() {
		setOpaque(false);
	}

	/**
	 * Set the path shown.
	 * 
	 * @param path
	 *            the path shown; <code>null</code> if nothing is shown
	 */
	public void setPath(Path path) {
		this.path = path == null ? path : new Path(path);
		updatePath();
	}

	public Path getPath() {
		return path == null ? null : new Path(path);
	}

	/**
	 * Set the dimension of each cell.
	 * 
	 * @param cellW
	 *            the width of the cell
	 * @param cellH
	 *            the height of the cell
	 */
	public void setCellSize(double cellW, double cellH) {
		this.cellWidth = cellW;
		this.cellHeight = cellH;
		updatePath();
	}

	private void updatePath() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
		if (path == null) {
			points = null;
			path2d = null;
		} else {
			points = PathCalculator.calculatePoints(path, cellWidth,
					cellHeight, cellWidth / 8, cellHeight / 8);
			path2d = PathCalculator.calculatePath(points);

			animation = 0.0;
			timer = new Timer(1000 / 60, e -> {
				Point2D p1 = getAnimationPoint();

				animation += 1.0 / 16;
				if (animation > points.size() - 1)
					animation = 0.0;

				Point2D p2 = getAnimationPoint();
				Rectangle r = new Rectangle();
				r.setFrameFromDiagonal(p1, p2);
				r.setFrameFromCenter(r.getCenterX(), r.getCenterY(),
						r.getMaxX() + 5, r.getMaxY() + 5);
				repaint(r);
			});
			timer.start();
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		if (points == null || path2d == null)
			return;

		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g.draw(path2d);
		g.setStroke(new BasicStroke());

		final double r = 4, d = r * 2;
		Point2D st = points.get(0);
		g.setColor(Color.GREEN);
		g.fill(new Ellipse2D.Double(st.getX() - r, st.getY() - r, d, d));
		g.setColor(Color.BLACK);
		g.draw(new Ellipse2D.Double(st.getX() - r, st.getY() - r, d, d));
		Point2D ed = points.get(points.size() - 1);
		g.setColor(Color.RED);
		g.fill(new Ellipse2D.Double(ed.getX() - r, ed.getY() - r, d, d));
		g.setColor(Color.BLACK);
		g.draw(new Ellipse2D.Double(ed.getX() - r, ed.getY() - r, d, d));
		Point2D an = getAnimationPoint();
		g.setColor(Color.YELLOW);
		g.fill(new Ellipse2D.Double(an.getX() - r, an.getY() - r, d, d));
		g.setColor(Color.BLACK);
		g.draw(new Ellipse2D.Double(an.getX() - r, an.getY() - r, d, d));
	}

	private Point2D getAnimationPoint() {
		if (animation == (int) animation)
			return points.get((int) animation);
		Point2D prev = points.get((int) animation);
		Point2D next = points.get((int) animation + 1);
		double frac = animation - (int) animation;
		double x = prev.getX() + (next.getX() - prev.getX()) * frac;
		double y = prev.getY() + (next.getY() - prev.getY()) * frac;
		return new Point2D.Double(x, y);
	}
}
