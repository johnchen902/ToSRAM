package tosram.xrobot;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map.Entry;

import tosram.ComboCalculator;
import tosram.DefaultPath;
import tosram.Path;
import tosram.PathRobot;
import tosram.RuneMap;
import tosram.Direction;
import tosram.RuneStone;
import tosram.RuneStone.Type;

/**
 * An implementation of <code>PathRobot</code> based on <a
 * href="http://en.wikipedia.org/wiki/IDA*">IDA*</a>.
 * 
 * @author johnchen902
 */
public class IDAStarRobot implements PathRobot {

	private static int getPossibleCombo(int count) {
		if (count <= 20)
			return count / 3;
		switch (count) {
		case 21:
		case 22:
			return 5;
		case 23:
			return 4;
		case 24:
		case 25:
			return 3;
		case 26:
			return 2;
		case 27:
		case 28:
		case 29:
		case 30:
			return 1;
		default:
			return 0;
		}
	}

	private static int getMaxCombo(RuneMap runemap) {
		EnumMap<Type, Integer> map = new EnumMap<>(Type.class);
		for (Type type : Type.values())
			map.put(type, 0);
		for (int y = 0; y < runemap.getHeight(); y++)
			for (int x = 0; x < runemap.getWidth(); x++) {
				Type t = runemap.getStone(x, y).getType();
				map.put(t, map.get(t) + 1);
			}
		int combo = 0;
		boolean pureColor = false;
		for (Entry<Type, Integer> e : map.entrySet()) {
			if (e.getKey() != Type.UNKNOWN) {
				combo += getPossibleCombo(e.getValue());
				if (e.getValue() >= 18)
					pureColor = true;
			}
		}
		return pureColor ? -combo : combo;
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

	private StatusListener listener;
	private Deque<Direction> stack = new ArrayDeque<Direction>();
	private int bx, by;
	private int maxCombo;
	private int targetCombo;
	private int currentDiagonalCount;
	private int currentMoveCount;
	private boolean pureColor;

	private int heuristicCostEstimate(ComboCalculator.Describer combo) {
		return 3 * (targetCombo - combo.getFullComboCount());
	}

	private int cost(Direction d) {
		return d.ordinal() < 4 ? 1 : 2;
	}

	private boolean isGoal(ComboCalculator.Describer combo) {
		return targetCombo <= combo.getFullComboCount();
	}

	private DefaultPath search(RuneMap m, int x, int y, int g, int bound,
			double pa, double pb) {
		ComboCalculator.Describer combo = ComboCalculator.getDescriber(m);
		int f = g + heuristicCostEstimate(combo);
		if (f > bound || Thread.currentThread().isInterrupted())
			return null;
		if (isGoal(combo)) {
			return new DefaultPath(new Point(bx, by), new ArrayDeque<>(stack));
		}
		if (listener != null)
			listener.updateProgress(pa);
		for (Direction dir : Direction.values()) {
			if (stack.peekLast() == Direction.getOppsite(dir))
				continue;
			int nx = getX(x, dir);
			int ny = getY(y, dir);
			if (nx < 0 || nx >= m.getWidth() || ny < 0 || ny >= m.getHeight())
				continue;

			RuneStone stone1 = m.getStone(x, y);
			RuneStone stone2 = m.getStone(nx, ny);
			m.setRuneStone(x, y, stone2);
			m.setRuneStone(nx, ny, stone1);

			stack.addLast(dir);
			DefaultPath result = search(m, nx, ny, g + cost(dir), bound, pa,
					pb / 8);
			pa += pb / 8;
			stack.removeLast();

			m.setRuneStone(x, y, stone1);
			m.setRuneStone(nx, ny, stone2);

			if (result != null)
				return result;
		}
		return null;
	}

	private DefaultPath search(RuneMap m, int bound) {
		double pa = 0;
		for (by = 0; by < m.getHeight(); by++)
			for (bx = 0; bx < m.getWidth(); bx++) {
				DefaultPath result = search(m, bx, by, 0, bound, pa, 1.0 / 30);
				pa += 1.0 / 30;
				if (result != null)
					return result;
			}
		return null;
	}

	private Path runIDAStar(RuneMap stones) {
		int bound = heuristicCostEstimate(ComboCalculator.getDescriber(stones));
		while (!Thread.currentThread().isInterrupted()) {
			updateMilestone(targetCombo - 1, bound);
			DefaultPath result = search(stones, bound);
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
		stones = new RuneMap(stones);
		maxCombo = getMaxCombo(stones);
		pureColor = false;
		if (maxCombo < 0) {
			pureColor = true;
			maxCombo = -maxCombo;
		}
		Path path = new DefaultPath(new Point(0, 0),
				Collections.<Direction> emptyList());
		for (targetCombo = 1; targetCombo <= maxCombo; targetCombo++) {
			Path result = runIDAStar(stones);
			if (result == null) {
				updateMilestone(targetCombo - 1, -1);
				break;
			}
			path = result;

			currentMoveCount = path.getDirections().size();
			currentDiagonalCount = countDiagonal(path);

			updateMilestone(targetCombo, -1);
		}
		if (listener != null)
			listener.updateProgress(1.0);
		return path;
	}

	private void updateMilestone(int currentCombo, int bound) {
		if (listener == null)
			return;
		StringBuilder builder = new StringBuilder();

		builder.append(currentCombo);
		if (currentCombo != maxCombo)
			builder.append(" / ").append(maxCombo);
		builder.append(" combo");

		if (pureColor)
			builder.append(" (W:PURE!!)");

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
