package tosram.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.EventListener;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.javatuples.Pair;

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
	private Shape activeSegement;
	private PathAnimation pa;
	private double cellWidth = 50;
	private double cellHeight = 50;
	private int smallDelay = 300;
	private int bigDelay = 3000;

	/**
	 * Create an empty <code>PathPanel</code>
	 */
	public PathPanel() {
		setOpaque(false);
	}

	/**
	 * Adds the specified animation listener to receive callbacks from this
	 * panel. If listener <code>l</code> is <code>null</code>, no exception is
	 * thrown and no action is performed.
	 * 
	 * @param l
	 *            the animation listener
	 * @see AnimationListener
	 * @see #removeAnimationListener
	 * @see #getAnimationListeners
	 */
	public void addAnimationListener(AnimationListener l) {
		listenerList.add(AnimationListener.class, l);
	}

	/**
	 * Removes the specified animation listener so that it no longer receives
	 * callbacks from this panel. This method performs no function, nor does it
	 * throw an exception, if the listener specified by the argument was not
	 * previously added to this component. If listener <code>l</code> is
	 * <code>null</code>, no exception is thrown and no action is performed.
	 * 
	 * @param l
	 *            the animation listener
	 * @see AnimationListener
	 * @see #addAnimationListener
	 * @see #getAnimationListeners
	 */
	public void removeAnimationListener(AnimationListener l) {
		listenerList.remove(AnimationListener.class, l);
	}

	/**
	 * Returns an array of all the animation listeners registered on this panel.
	 * 
	 * @return all of this panel's <code>AnimationListener</code>s or an empty
	 *         array if no animation listeners are currently registered
	 * 
	 * @see #addAnimationListener
	 * @see #removeAnimationListener
	 */
	public AnimationListener[] getAnimationListeners() {
		return listenerList.getListeners(AnimationListener.class);
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
			Pair<Path2D, List<Shape>> rpath = calculatePath();
			gpath = rpath.getValue0();
			pa = new PathAnimation(rpath.getValue1());
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
	public void setCellSize(double cellW, double cellH) {
		this.cellWidth = cellW;
		this.cellHeight = cellH;
		if (gpath != null && pa != null) {
			Pair<Path2D, List<Shape>> rpath = calculatePath();
			gpath = rpath.getValue0();
			pa.setSegements(rpath.getValue1());
		}
	}

	/**
	 * @return the delay of animation between moves
	 */
	public int getSmallDelay() {
		return smallDelay;
	}

	/**
	 * @param smallDelay
	 *            the delay of animation between moves
	 */
	public void setSmallDelay(int smallDelay) {
		this.smallDelay = smallDelay;
	}

	/**
	 * @return the delay of animation before start and after end
	 */
	public int getBigDelay() {
		return bigDelay;
	}

	/**
	 * @param bigDelay
	 *            the delay of animation before start and after end
	 */
	public void setBigDelay(int bigDelay) {
		this.bigDelay = bigDelay;
	}

	private Pair<Path2D, List<Shape>> calculatePath() {
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

		if (activeSegement != null) {
			Shape s = new Area(new BasicStroke(5f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND).createStrokedShape(activeSegement));

			g.setColor(Color.BLACK);
			g.fill(s);

			g.setStroke(new BasicStroke(1f));
			g.setColor(Color.WHITE);
			g.draw(s);
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldval);
	}

	/**
	 * A callback when the panel start animating, animates and stop animating.
	 * 
	 * @author johnchen902
	 */
	public interface AnimationListener extends EventListener {

		/**
		 * Called when the panel start animating.
		 */
		public void animationStart();

		/**
		 * Called when the panel animates a move.
		 * 
		 * @param directionIndex
		 *            the index of the animated direction in the
		 *            <code>Path</code> passed in
		 */
		public void animate(int directionIndex);

		/**
		 * Called when the panel stop animating.
		 */
		public void animationStop();
	}

	private class PathAnimation extends Timer implements ActionListener {

		private List<Shape> segements;
		private ListIterator<Shape> iterator;

		public PathAnimation(List<Shape> segements) {
			super(bigDelay, null);
			setInitialDelay(0);
			this.segements = segements;
			this.iterator = segements.listIterator();
			super.addActionListener(this);
		}

		public void setSegements(List<Shape> segements) {
			if (this.segements.size() != segements.size())
				throw new IllegalArgumentException("Different segement size!");
			this.segements = segements;
			if (iterator != null)
				this.iterator = segements.listIterator(iterator.nextIndex());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (iterator.hasNext()) {
				int i = iterator.nextIndex();
				if (i == 0) {
					for (AnimationListener o : getAnimationListeners())
						o.animationStart();
				} else {
					for (AnimationListener o : getAnimationListeners())
						o.animate(i - 1);
				}
				activeSegement = iterator.next();
				setDelay(iterator.hasNext() ? smallDelay : bigDelay);
			} else {
				iterator = segements.listIterator();
				activeSegement = null;
				for (AnimationListener o : getAnimationListeners())
					o.animationStop();
				setDelay(bigDelay);
			}
			repaint();
		}
	}
}
