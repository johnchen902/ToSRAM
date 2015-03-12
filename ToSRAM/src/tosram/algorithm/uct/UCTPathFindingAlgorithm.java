package tosram.algorithm.uct;

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
import tosram.RuneStone;
import tosram.algorithm.AbstractPathFindingAlgorithm;
import tosram.algorithm.ComboCountingAlgorithm;
import tosram.algorithm.MaximumComboCalculator;
import tosram.algorithm.PathConstrain;

/**
 * MonteCarlo Tree Search with Upper Confidence Bound applied in Tree.
 * 
 * @author johnchen902
 */
public class UCTPathFindingAlgorithm extends AbstractPathFindingAlgorithm {

	private static final int VALUE_PER_COMBO = 10;
	private int maximumPossibleCombo;
	private int highestValue = 0;
	private int highestPossibleValue = 0;

	public UCTPathFindingAlgorithm(ComboCountingAlgorithm comboCounter,
			PathConstrain constrain) {
		super(comboCounter, constrain);
	}

	@Override
	protected void findPath(RuneMap initialMap) {
		maximumPossibleCombo = MaximumComboCalculator.getMaxCombo(initialMap);
		highestValue = 0;
		highestPossibleValue = maximumPossibleCombo * VALUE_PER_COMBO;
		try {
			MutableRuneMap m = initialMap.toMutable();
			Root root = new Root(m);
			while (!isStopped())
				root.select(m);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		System.gc();
	}

	private int evaluate(MutableRuneMap map, List<Direction> directions) {
		int combo = comboCounter.countCombo(map).size();
		int v = combo * VALUE_PER_COMBO - directions.size();
		return Math.max(v, 0);
	}

	private String message(MutableRuneMap map, List<Direction> directions) {
		int combo = comboCounter.countCombo(map).size();
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
					if (constrain.canStart(x, y, map))
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
			RuneStone stone1 = map.getRuneStone(x1, y1);
			RuneStone stone2 = map.getRuneStone(x2, y2);
			map.setRuneStone(x1, y1, stone2);
			map.setRuneStone(x2, y2, stone1);
			directions.add(d);

			n.select(map, startX, startY, directions, x2, y2);
			value = Math.max(value, n.value);

			map.setRuneStone(x1, y1, stone1);
			map.setRuneStone(x2, y2, stone2);
			directions.remove(directions.size() - 1);
		}

		private void evaluate(MutableRuneMap map, int startX, int startY,
				List<Direction> directions) {
			value = UCTPathFindingAlgorithm.this.evaluate(map, directions);
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
				if (x2 < 0 || x2 >= map.getWidth())
					continue;
				if (y2 < 0 || y2 >= map.getHeight())
					continue;
				if (!constrain.canMove(startX, startY, directions, d, x2, y2,
						map))
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
