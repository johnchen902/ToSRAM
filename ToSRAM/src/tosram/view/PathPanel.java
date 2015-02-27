package tosram.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.EventListener;

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
	private Path2D gpath;
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
		if (path == null) {
			gpath = null;
		} else {
			gpath = calculatePath();
		}
		repaint();
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
		if (path == null) {
			gpath = null;
		} else {
			gpath = calculatePath();
		}
	}

	private Path2D calculatePath() {
		double dis = Math.min(cellWidth, cellHeight) / 6.3;
		return PathPanelCalculator.calculatePath(path, cellWidth, cellHeight,
				dis);
	}

	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		if (gpath == null)
			return;

		Graphics2D g = (Graphics2D) g0;
		Object oldval = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g.draw(gpath);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldval);
	}

	/**
	 * A callback when the panel start an animation cycle, animates and stop an
	 * animation cycle.
	 * 
	 * @author johnchen902
	 */
	public interface AnimationListener extends EventListener {

		/**
		 * Called when the panel started an animation cycle.
		 */
		public void animationCycleStart();

		/**
		 * Called when the panel animated a move.
		 * 
		 * @param directionIndex
		 *            the index of the animated direction in the
		 *            <code>Path</code> passed in
		 */
		public void animate(int directionIndex);

		/**
		 * Called when the panel stopped an animation cycle.
		 */
		public void animationCycleStop();
	}
}
