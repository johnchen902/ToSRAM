package tosram.algorithm.montecarlo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.Path;
import tosram.RuneMap;
import tosram.algorithm.AbstractPathFinder;
import tosram.algorithm.ComboCounter;
import tosram.algorithm.MaxComboCalculator;
import tosram.algorithm.PathRestriction;

/**
 * MonteCarlo Tree Search with Upper Confidence Bound.
 * 
 * @author johnchen902
 */
/*
 * I've already forgotten what the magic here is. Maybe you can find some
 * information here: https://en.wikipedia.org/wiki/Monte_Carlo_tree_search
 */
public class MonteCarloPathFinder extends AbstractPathFinder {

	private static final int VALUE_PER_COMBO = 10;
	private int maximumPossibleCombo;
	private int highestValue = 0;
	private int highestPossibleValue = 0;
	private final int iterations;

	public MonteCarloPathFinder(ComboCounter comboer, PathRestriction restrict) {
		this(comboer, restrict, Integer.MAX_VALUE);
	}

	public MonteCarloPathFinder(ComboCounter comboer, PathRestriction restrict,
			int iterations) {
		super(comboer, restrict);
		this.iterations = iterations;
	}

	@Override
	protected void findPath(RuneMap initialMap) {
		maximumPossibleCombo = MaxComboCalculator.getMaxCombo(initialMap);
		highestValue = 0;
		highestPossibleValue = maximumPossibleCombo * VALUE_PER_COMBO;
		try {
			MutableRuneMap m = initialMap.toMutable();
			Root root = new Root(m);
			for (int i = 0; i < iterations && !isStopped(); i++)
				root.select(m);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		System.gc();
	}

	private int evaluate(MutableRuneMap map, List<Direction> directions) {
		int combo = countCombo(map).size();
		int v = combo * VALUE_PER_COMBO - directions.size();
		return Math.max(v, 0);
	}

	private String message(MutableRuneMap map, List<Direction> directions) {
		int combo = countCombo(map).size();
		return String.format("%d/%d Combo %d Move", combo,
				maximumPossibleCombo, directions.size());
	}

	private class Root {
		private int size;
		private Map<Point, Node> children;

		public Root(MutableRuneMap map) {
			children = new HashMap<>();
			for (int x = 0; x < map.getWidth(); x++)
				for (int y = 0; y < map.getHeight(); y++)
					if (canStart(x, y, map))
						children.put(new Point(x, y), new Node());
		}

		public void select(MutableRuneMap map) {
			if (children.size() == 0)
				return;
			Entry<Point, Node> e = selectChild();
			Point p = e.getKey();
			e.getValue().select(map, p.x, p.y, new ArrayList<>(), p.x, p.y);
			size++;
		}

		private Entry<Point, Node> selectChild() {
			double highest = 0;
			Entry<Point, Node> highestE = null;
			for (Entry<Point, Node> e : children.entrySet()) {
				double w = e.getValue().weight(size);
				if (w > highest || highestE == null) {
					highest = w;
					highestE = e;
				}
			}
			return highestE;
		}
	}

	private class Node {
		private int size;
		private int value = Integer.MIN_VALUE;
		private EnumMap<Direction, Node> children;

		public void select(MutableRuneMap map, int startX, int startY,
				List<Direction> directions, int x1, int y1) {
			if (children == null) {
				evaluate(map, startX, startY, directions);
				expand(map, startX, startY, directions, x1, y1);
				return;
			}
			size++;
			if (children.isEmpty())
				return;
			Entry<Direction, Node> e = selectChild();
			Direction d = e.getKey();
			Node n = e.getValue();

			int x2 = x1 + d.getX(), y2 = y1 + d.getY();
			map.swap(x1, y1, x2, y2);
			directions.add(d);

			n.select(map, startX, startY, directions, x2, y2);
			value = Math.max(value, n.value);

			map.swap(x1, y1, x2, y2);
			directions.remove(directions.size() - 1);
		}

		private void evaluate(MutableRuneMap map, int startX, int startY,
				List<Direction> directions) {
			value = MonteCarloPathFinder.this.evaluate(map, directions);
			if (!directions.isEmpty() && value > highestValue) {
				highestValue = value;
				result(new Path(new Point(startX, startY), directions),
						message(map, directions));
			}
		}

		private void expand(MutableRuneMap map, int startX, int startY,
				List<Direction> directions, int x1, int y1) {
			size = 1;
			children = new EnumMap<>(Direction.class);
			for (Direction d : Direction.values()) {
				int x2 = x1 + d.getX(), y2 = y1 + d.getY();
				if (!map.isInRange(x2, y2))
					continue;
				if (!canMove(startX, startY, directions, d, x2, y2, map))
					continue;
				children.put(d, new Node());
			}
		}

		private Entry<Direction, Node> selectChild() {
			double highest = 0;
			Entry<Direction, Node> highestE = null;
			for (Entry<Direction, Node> e : children.entrySet()) {
				double w = e.getValue().weight(size);
				if (w > highest || highestE == null) {
					highest = w;
					highestE = e;
				}
			}
			return highestE;
		}

		public double weight(int t) {
			if (children == null)
				return Double.POSITIVE_INFINITY;
			return (double) value / highestPossibleValue
					+ Math.sqrt(2 * Math.log(t) / size);
		}
	}
}
