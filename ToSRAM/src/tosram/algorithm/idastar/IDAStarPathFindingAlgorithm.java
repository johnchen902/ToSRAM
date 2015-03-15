package tosram.algorithm.idastar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.Path;
import tosram.RuneMap;
import tosram.algorithm.AbstractPathFindingAlgorithm;
import tosram.algorithm.ComboCountingAlgorithm;
import tosram.algorithm.LongComboCountingAlgorithm;
import tosram.algorithm.PathConstrain;
import tosram.algorithm.path.UTurnPathConstrain;

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
		while (minHFound != 0 && !isStopped())
			findPathLimited(map, limit++);
	}

	private void findPathLimited(MutableRuneMap map, int limit) {
		for (int x = 0; x < map.getWidth(); x++)
			for (int y = 0; y < map.getHeight(); y++)
				findPathStartAt(map, limit, x, y);
	}

	private void findPathStartAt(MutableRuneMap map, int limit, int x, int y) {
		if (constrain.canStart(x, y, map))
			findPathFrom(map, limit, x, y, new ArrayList<>(), x, y, 0);
	}

	private void findPathFrom(MutableRuneMap map, int limit, int startX,
			int startY, List<Direction> directions, int x1, int y1, int g) {
		int h = h(map);
		if (g + h > limit || isStopped())
			return;
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
			if (!map.isInRange(x2, y2))
				continue;
			if (!constrain.canMove(startX, startY, directions, d, x2, y2, map))
				continue;

			map.swap(x1, y1, x2, y2);
			directions.add(d);

			findPathFrom(map, limit, startX, startY, directions, x2, y2,
					g + (d.ordinal() < 4 ? 1 : 2));

			map.swap(x1, y1, x2, y2);
			directions.remove(directions.size() - 1);
		}
	}
}
