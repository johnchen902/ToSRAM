package tosram.algorithm.idastar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.Path;
import tosram.RuneMap;
import tosram.algorithm.AbstractPathFinder;
import tosram.algorithm.ComboCounter;
import tosram.algorithm.LongComboCounter;
import tosram.algorithm.PathRestriction;
import tosram.algorithm.path.UTurnRestriction;

/**
 * Iterative-deepening A* algorithm.
 * 
 * @author johnchen902
 */
public class IDAStarPathFinder extends AbstractPathFinder {

	private final HeuristicCostEstimater estimater;
	private int minGUsed, minHFound;

	/**
	 * A constructor with some arguments.
	 * 
	 * @param comboer
	 *            the algorithm to count combo
	 * @param restrict
	 *            the restriction about path
	 * @param estimater
	 *            the h-function of this algorithm
	 */
	public IDAStarPathFinder(ComboCounter comboer, PathRestriction restrict,
			HeuristicCostEstimater estimater) {
		super(comboer, restrict);
		this.estimater = Objects.requireNonNull(estimater);
	}

	/**
	 * A constructor with default arguments.
	 */
	public IDAStarPathFinder() {
		this(new LongComboCounter(), new UTurnRestriction(),
				new ComboHeuristicCostEstimater());
	}

	/*
	 * Returns the estimated cost of the map.
	 */
	private int costOf(MutableRuneMap map) {
		return estimater.estimateCost(map, countCombo(map));
	}

	@Override
	protected void findPath(RuneMap initialMap) {
		minHFound = Integer.MAX_VALUE;
		estimater.setInitialMap(initialMap);
		MutableRuneMap map = initialMap.toMutable();
		for (int limit = costOf(map); minHFound != 0 && !isStopped(); limit++)
			for (int x = 0; x < map.getWidth(); x++)
				for (int y = 0; y < map.getHeight(); y++)
					if (canStart(x, y, map))
						findPath(map, limit, x, y, new ArrayList<>(), x, y, 0);
	}

	// The Recursion!
	private void findPath(MutableRuneMap map, int limit, int startX,
			int startY, List<Direction> directions, int x1, int y1, int g) {
		int h = costOf(map);
		if (g + h > limit || isStopped())
			return;
		if ((h < minHFound || (h == minHFound && g < minGUsed))
				&& !directions.isEmpty()) {
			Path path = new Path(new Point(startX, startY), directions);
			String descr = estimater.describe(map, countCombo(map));
			descr += " " + directions.size() + " Move";
			result(path, descr);

			minHFound = h;
			minGUsed = g;
		}
		for (Direction d : Direction.values()) {
			int x2 = x1 + d.getX(), y2 = y1 + d.getY();
			if (!map.isInRange(x2, y2))
				continue;
			if (!canMove(startX, startY, directions, d, x2, y2, map))
				continue;

			map.swap(x1, y1, x2, y2);
			directions.add(d);

			findPath(map, limit, startX, startY, directions, x2, y2,
					g + (d.isDiagonal() ? 2 : 1));

			map.swap(x1, y1, x2, y2);
			directions.remove(directions.size() - 1);
		}
	}
}
