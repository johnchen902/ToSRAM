package tosram.strategy;

import static tosram.Direction.EAST;
import static tosram.Direction.EAST_NORTH;
import static tosram.Direction.EAST_SOUTH;
import static tosram.Direction.NORTH;
import static tosram.Direction.SOUTH;
import static tosram.Direction.WEST;
import static tosram.Direction.WEST_NORTH;
import static tosram.Direction.WEST_SOUTH;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;

import tosram.ComboCalculator;
import tosram.DefaultPath;
import tosram.Direction;
import tosram.Path;
import tosram.PathRobot;
import tosram.RuneMap;
import tosram.RuneStone;

/**
 * An implementation of <code>PathRobot</code> that searches all the place its
 * strategy specifies for the best path, where <i>the best</i> is also defined
 * by the strategy. Terminate calculation early if interrupted.
 * 
 * @author johnchen902
 */
public class StrategySearchPathRobot implements PathRobot {

	/**
	 * A strategy determines the range searched and the quality of the solution.
	 * 
	 * @author johnchen902
	 */
	public static interface Strategy {
		/**
		 * Clear the solutions found and set the initial map.
		 * 
		 * @param initial
		 *            the initial map
		 */
		public void reset(RuneMap initial);

		/**
		 * Submit a solution and prepare to invoke other methods.
		 * 
		 * @param map
		 *            the resulting <code>RuneMap</code>
		 * @param x
		 *            the X coordinate of the current location
		 * @param y
		 *            the Y coordinate of the current location
		 * @param stack
		 *            the directions of current path
		 */
		public void submit(RuneMap map, int x, int y, Deque<Direction> stack);

		/**
		 * Return a <code>ComboCalculator</code> of the submitted solution.
		 * 
		 * @return a <code>ComboCalculator</code>
		 */
		public ComboCalculator getComboCalculator();

		/**
		 * Do you want to move stones diagonally?
		 * 
		 * @return <code>true</code> if to move stones diagonally;
		 *         <code>false</code> otherwise
		 */
		public boolean isToDiagonal();

		/**
		 * Do you want to stop this branch?
		 * 
		 * @return <code>true</code> if to stop this branch; <code>false</code>
		 *         otherwise
		 */
		public boolean isToStop();

		/**
		 * Is the currently submitted solution better or worse than the
		 * currently accepted solution?
		 * 
		 * @return a positive number if the currently submitted solution is
		 *         better; a negative number if the currently submitted solution
		 *         is worse; <code>0</code> if two solution are equally good
		 */
		public int compareSolution();

		/**
		 * Get a text description of this currently submitted solution.
		 * 
		 * @return a <code>String</code>
		 */
		public String getMilestone();

		/**
		 * Set the currently submitted solution to the currently accepted
		 * solution, even if the solution become worse.
		 */
		public void solutionAccepted();
	}

	private final Strategy strategy;
	private StatusListener listener;
	private Path bestSolution;

	/**
	 * Create a <code>StrategySearchPathRobot</code> with specified strategy.
	 * 
	 * @param strategy
	 *            the strategy specified
	 */
	public StrategySearchPathRobot(Strategy strategy) {
		if (strategy == null)
			throw new NullPointerException("strategy");
		this.strategy = strategy;
	}

	@Override
	public Path getPath(RuneMap stones) {
		strategy.reset(stones);
		bestSolution = null;

		try {
			double subprogress = 1.0 / (stones.getWidth() * stones.getHeight());
			double curprogress = 0;
			for (int y = 0; y < stones.getHeight(); y++)
				for (int x = 0; x < stones.getWidth(); x++) {
					compute(new RuneMap(stones), x, y, curprogress, subprogress);
					curprogress += subprogress;
				}
		} catch (Exception exception) {
			if (listener != null)
				listener.updateMilestone(exception.toString());
			exception.printStackTrace();
		}

		setProgress(1.0);

		return bestSolution;
	}

	private long lastPublishTime = System.currentTimeMillis();

	private void setProgress(double progress) {
		long t = System.currentTimeMillis();
		// Reduce send rate
		if (listener != null && (t > lastPublishTime || progress == 1.0)) {
			lastPublishTime = t;
			listener.updateProgress(progress);
		}
	}

	@Override
	public void setStatusListener(StatusListener listener) {
		this.listener = listener;
	}

	private Deque<Direction> stack = new ArrayDeque<Direction>();
	private int bx;
	private int by;

	private void compute(RuneMap init, int bx, int by, double pB, double pI) {
		this.bx = bx;
		this.by = by;
		turn(init, bx, by, pB, pI);
	}

	// pB = the progress when this method starts
	// pB + pI = the progress when this method ends
	private void turn(RuneMap cc, int x, int y, double pB, double pI) {
		strategy.submit(cc, x, y, stack);

		if (strategy.compareSolution() > 0) {
			strategy.solutionAccepted();
			bestSolution = new DefaultPath(new Point(bx, by), stack);
			if (listener != null)
				listener.updateMilestone(strategy.getMilestone());
		}

		if (strategy.isToStop())
			return;

		if (Thread.currentThread().isInterrupted())
			return;

		boolean diag = strategy.isToDiagonal();

		setProgress(pB);

		double pSI;
		if (!diag) {
			int nSI = 3;
			if (stack.isEmpty())
				nSI++;
			if (x == 0 || x == cc.getWidth() - 1)
				nSI--;
			if (y == 0 || y == cc.getHeight() - 1)
				nSI--;
			pSI = pI / nSI;
		} else {
			boolean xBorder = x == 0 || x == cc.getWidth() - 1;
			boolean yBorder = y == 0 || y == cc.getHeight() - 1;
			int nSI;
			if (xBorder && yBorder)
				nSI = 3 - 1;
			else if (!xBorder && !yBorder)
				nSI = 8 - 1;
			else
				nSI = 5 - 1;
			if (stack.isEmpty())
				nSI++;
			pSI = pI / nSI;
		}

		if (move(cc, x, y, pB, pSI, x - 1, y, WEST))
			pB += pSI;
		if (move(cc, x, y, pB, pSI, x + 1, y, EAST))
			pB += pSI;
		if (move(cc, x, y, pB, pSI, x, y - 1, NORTH))
			pB += pSI;
		if (move(cc, x, y, pB, pSI, x, y + 1, SOUTH))
			pB += pSI;
		if (diag) {
			if (move(cc, x, y, pB, pSI, x - 1, y - 1, WEST_NORTH))
				pB += pSI;
			if (move(cc, x, y, pB, pSI, x - 1, y + 1, WEST_SOUTH))
				pB += pSI;
			if (move(cc, x, y, pB, pSI, x + 1, y - 1, EAST_NORTH))
				pB += pSI;
			if (move(cc, x, y, pB, pSI, x + 1, y + 1, EAST_SOUTH))
				pB += pSI;
		}
	}

	private boolean move(RuneMap cc, int x, int y, double pB, double pSI,
			int nx, int ny, Direction dir) {
		if (nx >= 0 && nx < cc.getWidth() && ny >= 0 && ny < cc.getHeight()
				&& stack.peekLast() != Direction.getOppsite(dir)) {
			RuneStone stone1 = cc.getStone(x, y);
			RuneStone stone2 = cc.getStone(nx, ny);
			cc.setRuneStone(x, y, stone2);
			cc.setRuneStone(nx, ny, stone1);

			stack.addLast(dir);
			turn(cc, nx, ny, pB, pSI);
			stack.removeLast();

			cc.setRuneStone(x, y, stone1);
			cc.setRuneStone(nx, ny, stone2);

			return true;
		}
		return false;
	}

}
