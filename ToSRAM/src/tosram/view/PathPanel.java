package tosram.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JPanel;
import javax.swing.Timer;

import tosram.Direction;
import tosram.Path;

/**
 * A panel showing a path.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class PathPanel extends JPanel {

	private Path path;
	private GeneralPath gpath;
	private Shape activeSegement;
	private PathAnimation pa;
	private float cellWidth = 50f;
	private float cellHeight = 50f;

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
		this.path = path;
		if (pa != null) {
			pa.stop();
			pa = null;
		}
		if (path == null) {
			gpath = null;
			activeSegement = null;
		} else {
			RoundPath rpath = calculatePath();
			gpath = rpath.getPath();
			pa = new PathAnimation(rpath.getSegements());
			pa.start();
		}
		repaint();
	}

	/**
	 * Set the dimension of each cell.
	 * 
	 * @param cellW
	 *            the width of the cell
	 * @param cellH
	 *            the height of the cell
	 */
	public void setCellSize(float cellW, float cellH) {
		this.cellWidth = cellW;
		this.cellHeight = cellH;
	}

	private RoundPath calculatePath() {
		RoundPath rpath = new RoundPath();
		int x = path.getBeginPoint().x, y = path.getBeginPoint().y;
		rpath.addPoint((x + .5f) * cellWidth, (y + .5f) * cellHeight);
		for (Direction dir : path.getDirections()) {
			switch (dir) {
			case WEST:
			case WEST_NORTH:
			case WEST_SOUTH:
				x--;
				break;
			case EAST:
			case EAST_NORTH:
			case EAST_SOUTH:
				x++;
				break;
			case NORTH:
			case SOUTH:
				break;
			}
			switch (dir) {
			case SOUTH:
			case WEST_SOUTH:
			case EAST_SOUTH:
				y++;
				break;
			case NORTH:
			case WEST_NORTH:
			case EAST_NORTH:
				y--;
				break;
			case WEST:
			case EAST:
				break;
			}
			rpath.addPoint((x + .5f) * cellWidth, (y + .5f) * cellHeight);
		}
		rpath.end();
		return rpath;
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

		if (activeSegement != null) {
			g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			g.setColor(Color.BLACK);
			g.draw(activeSegement);
			Shape s = g.getStroke().createStrokedShape(activeSegement);
			g.setStroke(new BasicStroke(1f));
			g.setColor(Color.WHITE);
			g.draw(s);
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldval);
	}

	private class PathAnimation extends Timer implements ActionListener {

		private final List<Shape> segements;
		private ListIterator<Shape> iterator;

		public PathAnimation(List<Shape> segements) {
			super(300, null);
			this.segements = segements;
			this.iterator = segements.listIterator();
			super.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (iterator.hasNext()) {
				activeSegement = iterator.next();
			} else {
				iterator = segements.listIterator();
				activeSegement = null;
			}
			repaint();
		}

	}
}