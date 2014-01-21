package tosram;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 * An interface to transform the path computed to the path able to move, denoted
 * by some point-delay pair.
 * 
 * @author johnchen902
 */
public interface MovingPathGenerator {

	/**
	 * A point-delay pair.
	 * 
	 * @author johnchen902
	 */
	public static class Move {
		private Point point;
		private int delay;

		/**
		 * Construct such a point-delay pair.
		 * 
		 * @param point
		 *            the point this move should go to
		 * @param delay
		 *            the delay after this move
		 */
		public Move(Point point, int delay) {
			this.point = point;
			this.delay = delay;
		}

		/**
		 * Get the point this move should go to.
		 */
		public Point getPoint() {
			return point;
		}

		/**
		 * Set the point this move should go to.
		 */
		public void setPoint(Point point) {
			this.point = point;
		}

		/**
		 * Get the delay after this move.
		 */
		public int getDelay() {
			return delay;
		}

		/**
		 * Set the delay after this move.
		 */
		public void setDelay(int delay) {
			this.delay = delay;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Move))
				return false;
			Move that = (Move) obj;
			return delay == that.delay && point.equals(that.point);
		}

		@Override
		public int hashCode() {
			int code = 17;
			code = code * 31 + delay;
			code = code * 31 + point.hashCode();
			return code;
		}

		@Override
		public String toString() {
			return "[" + point + "," + delay + "]";
		}
	}

	/**
	 * Get a path to move from the following arguments:
	 * 
	 * @param path
	 *            the path to build from
	 * @param screen
	 *            the bounds of runestones
	 * @param mapSize
	 *            the size of the runestones
	 * @param time
	 *            the maximum time used to move stones
	 * @param startImmediately
	 *            <code>true</code> if the timing starts when right before first
	 *            move; <code>false</code> if the timing starts when the first
	 *            stone is moved.
	 * @return a list of {@link Move}
	 */
	public List<Move> getMovePath(Path path, Rectangle screen,
			Dimension mapSize, int time, boolean startImmediately);
}
