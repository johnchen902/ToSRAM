package tosram.algorithm.uct;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
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

// TODO document
// Upper Confidence bound applied in Tree
public class UCTPathFindingAlgorithm extends AbstractPathFindingAlgorithm {

	public UCTPathFindingAlgorithm(ComboCountingAlgorithm comboCounter,
			PathConstrain constrain) {
		super(comboCounter, constrain);
	}

	private int highestValue = 0;
	private int highestPossibleValue = 0;

	@Override
	protected void findPath(RuneMap initialMap) {
		int maxCombo = MaximumComboCalculator.getMaxCombo(initialMap);
		highestValue = 0;
		highestPossibleValue = maxCombo * 20;
		try {
			MutableRuneMap m = initialMap.toMutable();
			Root root = new Root(m);
			while (!isStopped())
				root.select(m);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	private int evaluate(MutableRuneMap map, List<Direction> directions) {
		int v = comboCounter.countCombo(map).size() * 20 - directions.size();
		return Math.max(v, 0);
	}

	private String message(MutableRuneMap map, List<Direction> directions) {
		return comboCounter.countCombo(map).size() + " Combo "
				+ directions.size() + " Move";
	}

	private class Root {
		private int size;
		private Map<Point, Node> children;

		public Root(MutableRuneMap map) {
			size = 1;
			children = new HashMap<>();
			for (int x = 0; x < map.getWidth(); x++)
				for (int y = 0; y < map.getHeight(); y++)
					if (constrain.canStart(x, y, map)) {
						Point p = new Point(x, y);
						Node n = new Node(map, x, y, Collections.emptyList());
						children.put(p, n);
						size += n.getSize();
					}
		}

		public void select(MutableRuneMap map) {
			if (children.size() == 0)
				return;
			Entry<Point, Node> e = selectChild();
			Point p = e.getKey();
			Node n = e.getValue();
			size -= n.getSize();
			n.select(map, p.x, p.y, new ArrayList<>(), p.x, p.y);
			size += n.getSize();
		}

		private Entry<Point, Node> selectChild() {
			double rand;
			{
				double wsum = 0;
				for (Node n : children.values())
					wsum += n.weight(size - 1);
				rand = Math.random() * wsum;
			}
			double wsum = 0;
			for (Entry<Point, Node> e : children.entrySet())
				if ((wsum += e.getValue().weight(size - 1)) > rand)
					return e;
			throw new AssertionError();
		}
	}

	private class Node {
		private int size;
		private int value;
		private EnumMap<Direction, Node> children;

		public Node(MutableRuneMap map, int startX, int startY,
				List<Direction> directions) {
			size = 1;
			value = evaluate(map, directions);
			if (!directions.isEmpty() && value > highestValue) {
				highestValue = value;
				result(new Path(new Point(startX, startY), directions),
						message(map, directions));
			}
		}

		public void select(MutableRuneMap map, int startX, int startY,
				List<Direction> directions, int x1, int y1) {
			if (children == null) {
				expand(map, startX, startY, directions, x1, y1);
				return;
			}
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

			size -= n.getSize();
			n.select(map, startX, startY, directions, x2, y2);
			size += n.getSize();

			map.setRuneStone(x1, y1, stone1);
			map.setRuneStone(x2, y2, stone2);
			directions.remove(directions.size() - 1);
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

				RuneStone stone1 = map.getRuneStone(x1, y1);
				RuneStone stone2 = map.getRuneStone(x2, y2);
				map.setRuneStone(x1, y1, stone2);
				map.setRuneStone(x2, y2, stone1);
				directions.add(d);

				Node n = new Node(map, startX, startY, directions);
				children.put(d, n);
				size += n.getSize();

				map.setRuneStone(x1, y1, stone1);
				map.setRuneStone(x2, y2, stone2);
				directions.remove(directions.size() - 1);
			}
		}

		private Entry<Direction, Node> selectChild() {
			if (children.size() == 1)
				return children.entrySet().iterator().next();

			double rand;
			{
				double wsum = 0;
				for (Node n : children.values())
					wsum += n.weight(size - 1);
				rand = Math.random() * wsum;
			}
			double wsum = 0;
			for (Entry<Direction, Node> e : children.entrySet())
				if ((wsum += e.getValue().weight(size - 1)) > rand)
					return e;
			throw new AssertionError();
		}

		public int getSize() {
			return size;
		}

		public double weight(int t) {
			return (double) value / highestPossibleValue
					+ Math.sqrt(2 * Math.log(t) / size);
		}
	}
}
