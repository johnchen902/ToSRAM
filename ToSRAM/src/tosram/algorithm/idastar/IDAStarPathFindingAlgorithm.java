package tosram.algorithm.idastar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.Path;
import tosram.RuneMap;
import tosram.RuneStone;
import tosram.algorithm.AbstractPathFindingAlgorithm;
import tosram.algorithm.ComboCountingAlgorithm;
import tosram.algorithm.LongComboCountingAlgorithm;
import tosram.algorithm.UTurnPathConstrain;
import tosram.algorithm.PathConstrain;

/**
 * Iterative-deepening A* algorithm.
 * 
 * @author johnchen902
 */
public class IDAStarPathFindingAlgorithm extends AbstractPathFindingAlgorithm {

	private final HeuristicCostEstimater estimater;
	private int minGUsed, minHFound;

	/**
	 * Just a constructor.
	 * 
	 * @param comboCounter
	 *            the algorithm to count combo
	 * @param constrain
	 *            the constrain about path
	 * @param estimater
	 *            the h-function of this algorithm
	 */
	public IDAStarPathFindingAlgorithm(ComboCountingAlgorithm comboCounter,
			PathConstrain constrain, HeuristicCostEstimater estimater) {
		super(comboCounter, constrain);
		this.estimater = Objects.requireNonNull(estimater);
	}

	/**
	 * Just simpler constructor.
	 */
	public IDAStarPathFindingAlgorithm() {
		this(new LongComboCountingAlgorithm(), new UTurnPathConstrain(),
				new ComboHeuristicCostEstimater());
	}

	private int h(MutableRuneMap map) {
		return estimater.estimateHeuristicCost(map,
				comboCounter.countCombo(map));
	}

	@Override
	protected void findPath(RuneMap initialMap) {
		minHFound = Integer.MAX_VALUE;
		estimater.setSourceState(initialMap);
		MutableRuneMap map = initialMap.toMutable();
		int limit = h(map);
		while (!findPathLimited(map, limit))
			limit++;
	}

	private boolean findPathLimited(MutableRuneMap map, int limit) {
		for (int x = 0; x < map.getWidth(); x++)
			for (int y = 0; y < map.getHeight(); y++)
				if (findPathStartAt(map, limit, x, y))
					return true;
		return minHFound == 0;
	}

	private boolean findPathStartAt(MutableRuneMap map, int limit, int x, int y) {
		if (!constrain.canStart(x, y, map))
			return false;
		return findPathFrom(map, limit, x, y, new ArrayList<>(), x, y, 0);
	}

	private boolean findPathFrom(MutableRuneMap map, int limit, int startX,
			int startY, List<Direction> directions, int x1, int y1, int g) {
		int h = h(map);
		if (g + h > limit)
			return false;
		if (isStopped())
			return true;
		if ((h < minHFound || (h == minHFound && g < minGUsed))
				&& directions.size() >= 1) {
			result(new Path(new Point(startX, startY), directions),
					estimater.describe(map, comboCounter.countCombo(map)) + " "
							+ directions.size() + " Move");
			minHFound = h;
			minGUsed = g;
		}
		for (Direction d : Direction.values()) {
			int x2 = x1 + d.getX(), y2 = y1 + d.getY();
			if (x2 < 0 || x2 >= map.getWidth())
				continue;
			if (y2 < 0 || y2 >= map.getHeight())
				continue;
			if (!constrain.canMove(startX, startY, directions, d, x2, y2, map))
				continue;

			RuneStone stone1 = map.getRuneStone(x1, y1);
			RuneStone stone2 = map.getRuneStone(x2, y2);
			map.setRuneStone(x1, y1, stone2);
			map.setRuneStone(x2, y2, stone1);
			directions.add(d);

			boolean b = findPathFrom(map, limit, startX, startY, directions,
					x2, y2, g + (d.ordinal() < 4 ? 1 : 2));

			map.setRuneStone(x1, y1, stone1);
			map.setRuneStone(x2, y2, stone2);
			directions.remove(directions.size() - 1);

			if (b)
				return true;
		}
		return false;
	}
}
