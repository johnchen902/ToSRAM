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

import tosram.ComboDescriber;
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

	private final SearchStrategy searchStrategy;
	private final SolutionStrategy solutionStrategy;
	private final ComboDescriber comboDescriber;
	private StatusListener listener;
	private Path bestSolution;

	/**
	 * Create a <code>StrategySearchPathRobot</code> with specified strategies.
	 * 
	 * @param searchStrategy
	 *            the specified strategy about search
	 * @param solutionStrategy
	 *            the specified strategy about solution
	 */
	public StrategySearchPathRobot(SearchStrategy searchStrategy,
			SolutionStrategy solutionStrategy) {
		if (searchStrategy == null)
			throw new NullPointerException("SearchStrategy");
		if (solutionStrategy == null)
			throw new NullPointerException("SolutionStrategy");
		this.searchStrategy = searchStrategy;
		this.solutionStrategy = solutionStrategy;
		this.comboDescriber = new ComboDescriber();
	}

	@Override
	public Path getPath(RuneMap stones) {
		solutionStrategy.reset();
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
		comboDescriber.setMap(cc);
		solutionStrategy.submit(cc, x, y, stack, comboDescriber);

		if (solutionStrategy.compareSolution() > 0) {
			solutionStrategy.solutionAccepted();
			bestSolution = new DefaultPath(new Point(bx, by), stack);
			if (listener != null)
				listener.updateMilestone(solutionStrategy.getMilestone());
		}

		searchStrategy.submit(cc, x, y, stack, comboDescriber);
		if (searchStrategy.isToStop())
			return;

		if (Thread.currentThread().isInterrupted())
			return;

		boolean diag = searchStrategy.isToDiagonal();

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
