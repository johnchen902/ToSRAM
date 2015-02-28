package tosram.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JPanel;

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
		if (path == null) {
			points = null;
			path2d = null;
		} else {
			points = PathCalculator.calculatePoints(path, cellWidth,
					cellHeight, cellWidth / 8, cellHeight / 8);
			path2d = PathCalculator.calculatePath(points);
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
	}
}
