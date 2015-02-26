package tosram.xrobot;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.Path;
import tosram.PathRobot;
import tosram.RuneMap;
import tosram.RuneStone;

/**
 * An implementation of <code>PathRobot</code> based on <a
 * href="http://en.wikipedia.org/wiki/IDA*">IDA*</a>.
 * 
 * @author johnchen902
 */
public class IDAStarRobot implements PathRobot {

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

	private final GoalSeriesFactory gsf;
	private final Moving moving;
	private Goal finalGoal, nextGoal, currentGoal, madeGoal;

	private StatusListener listener;
	private Deque<Direction> stack = new ArrayDeque<Direction>();
	private int bx, by;
	private int currentDiagonalCount;
	private int currentMoveCount;

	public IDAStarRobot(GoalSeriesFactory goalSeriesfactory, Moving moving) {
		this.gsf = goalSeriesfactory;
		this.moving = moving;
	}

	private Path search(MutableRuneMap m, int x, int y, int g, int bound,
			double pa, double pb) {
		Goal.Result goalResult = currentGoal.getResult(m, x, y);
		int f = g + goalResult.heuristicCostEstimate();
		if (f > bound || Thread.currentThread().isInterrupted())
			return null;
		if (goalResult.isMade()) {
			nextGoal = goalResult.getNext();
			return new Path(new Point(bx, by), new ArrayDeque<>(stack));
		}
		if (listener != null)
			listener.updateProgress(pa);
		Direction[] directions = moving.getDirections(x, y);
		for (Direction dir : directions) {
			if (stack.peekLast() == Direction.getOppsite(dir))
				continue;
			int nx = getX(x, dir);
			int ny = getY(y, dir);
			if (nx < 0 || nx >= m.getWidth() || ny < 0 || ny >= m.getHeight())
				continue;

			RuneStone stone1 = m.getRuneStone(x, y);
			RuneStone stone2 = m.getRuneStone(nx, ny);
			m.setRuneStone(x, y, stone2);
			m.setRuneStone(nx, ny, stone1);

			int cost = moving.cost(dir, stack);

			stack.addLast(dir);
			Path result = search(m, nx, ny, g + cost, bound, pa, pb
					/ directions.length);
			pa += pb / directions.length;
			stack.removeLast();

			m.setRuneStone(x, y, stone1);
			m.setRuneStone(nx, ny, stone2);

			if (result != null)
				return result;
		}
		return null;
	}

	private Path search(RuneMap m, int bound) {
		double pa = 0;
		for (by = 0; by < m.getHeight(); by++)
			for (bx = 0; bx < m.getWidth(); bx++) {
				MutableRuneMap mm = m.toMutable();
				Path result = search(mm, bx, by, 0, bound, pa, 1.0 / 30);
				pa += 1.0 / 30;
				if (result != null)
					return result;
			}
		return null;
	}

	private Path runIDAStar(RuneMap stones) {
		int bound = currentGoal.getResult(stones.toMutable())
				.heuristicCostEstimate();
		while (!Thread.currentThread().isInterrupted()) {
			updateMilestone(bound);
			Path result = search(stones, bound);
			if (result != null)
				return result;
			else
				bound++;
		}
		return null;
	}

	private static int countDiagonal(Path path) {
		int count = 0;
		for (Direction d : path.getDirections())
			if (d.ordinal() >= 4)
				count++;
		return count;
	}

	@Override
	public Path getPath(RuneMap stones) {
		Path path = new Path(new Point(0, 0),
				Collections.<Direction> emptyList());

		Goal[] goalPair = gsf.createGoalSeries(stones);
		madeGoal = currentGoal = goalPair[0];
		finalGoal = goalPair[1];
		while (true) {
			Path result = runIDAStar(stones);
			if (result == null) {
				updateMilestone(-1);
				break;
			}
			path = result;

			currentMoveCount = path.getDirections().size();
			currentDiagonalCount = countDiagonal(path);
			madeGoal = currentGoal;

			updateMilestone(-1);
			if (currentGoal.equals(finalGoal))
				break;
			currentGoal = nextGoal;
		}
		if (listener != null)
			listener.updateProgress(1.0);
		return path;
	}

	private void updateMilestone(int bound) {
		if (listener == null)
			return;
		StringBuilder builder = new StringBuilder();

		builder.append(gsf.describeGoal(madeGoal, finalGoal));

		builder.append(' ').append(currentMoveCount).append(" step");
		if (currentMoveCount > 1)
			builder.append('s');

		if (currentDiagonalCount > 0) {
			builder.append(' ').append(currentDiagonalCount)
					.append(" diagonal");
			if (currentDiagonalCount > 1) {
				builder.append('s');
			}
		}

		if (bound > 0) {
			builder.append(" bound=").append(bound);
		}

		listener.updateMilestone(builder.toString());
	}

	@Override
	public void setStatusListener(StatusListener listener) {
		this.listener = listener;
	}
}
