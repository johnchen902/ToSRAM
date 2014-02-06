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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;

import tosram.ComboCalculator;
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
	}

	@Override
	public Path getPath(RuneMap stones) {
		solutionStrategy.reset();
		bestSolution = null;

		List<Point> points = new ArrayList<>();
		for (int y = 0; y < stones.getHeight(); y++)
			for (int x = 0; x < stones.getWidth(); x++)
				points.add(new Point(x, y));

		Collections.shuffle(points);

		try {
			double subprogress = 1.0 / points.size();
			double curprogress = 0;
			for (Point p : points) {
				compute(new RuneMap(stones), p.x, p.y, curprogress, subprogress);
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
			listener.updateProgress(searchStrategy.adaptProgress(progress));
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

	private static int getX(int x, Direction direction) {
		switch (direction) {
		case WEST:
		case WEST_NORTH:
		case WEST_SOUTH:
			return x - 1;
		case EAST:
		case EAST_NORTH:
		case EAST_SOUTH:
			return x + 1;
		case NORTH:
		case SOUTH:
		default:
			return x;
		}
	}

	private static int getY(int y, Direction direction) {
		switch (direction) {
		case SOUTH:
		case WEST_SOUTH:
		case EAST_SOUTH:
			return y + 1;
		case NORTH:
		case WEST_NORTH:
		case EAST_NORTH:
			return y - 1;
		case WEST:
		case EAST:
		default:
			return y;
		}
	}

	// pB = the progress when this method starts
	// pB + pI = the progress when this method ends
	private void turn(RuneMap cc, int x, int y, double pB, double pI) {
		solutionStrategy.submit(cc, x, y, stack,
				ComboCalculator.getDescriber(cc));

		if (solutionStrategy.compareSolution() > 0) {
			solutionStrategy.solutionAccepted();
			bestSolution = new Path(new Point(bx, by), stack);
			if (listener != null)
				listener.updateMilestone(solutionStrategy.getMilestone());
		}

		searchStrategy.submit(cc, x, y, stack, solutionStrategy.getQuality());

		if (Thread.currentThread().isInterrupted())
			return;

		setProgress(pB);

		EnumSet<Direction> directions = searchStrategy.getDirections();

		if (x == 0)
			directions.removeAll(EnumSet.of(WEST, WEST_NORTH, WEST_SOUTH));
		else if (x == cc.getWidth() - 1)
			directions.removeAll(EnumSet.of(EAST, EAST_NORTH, EAST_SOUTH));
		if (y == 0)
			directions.removeAll(EnumSet.of(NORTH, WEST_NORTH, EAST_NORTH));
		else if (y == cc.getHeight() - 1)
			directions.removeAll(EnumSet.of(SOUTH, WEST_SOUTH, EAST_SOUTH));
		if (!stack.isEmpty())
			directions.remove(Direction.getOppsite(stack.peekLast()));

		double pSI = pI / directions.size();
		ArrayList<Direction> alist = new ArrayList<>(directions);
		Collections.shuffle(alist);

		for (Direction dir : alist) {
			int nx = getX(x, dir);
			int ny = getY(y, dir);
			RuneStone stone1 = cc.getStone(x, y);
			RuneStone stone2 = cc.getStone(nx, ny);
			cc.setRuneStone(x, y, stone2);
			cc.setRuneStone(nx, ny, stone1);

			stack.addLast(dir);
			turn(cc, nx, ny, pB, pSI);
			stack.removeLast();

			cc.setRuneStone(x, y, stone1);
			cc.setRuneStone(nx, ny, stone2);
			pB += pSI;
		}
	}

}
