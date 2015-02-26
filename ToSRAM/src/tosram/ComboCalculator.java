package tosram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A calculator of combo in a map.
 * 
 * @author johnchen902
 */
public class ComboCalculator {

	private static final int WIDTH = 6;
	private static final int HEIGHT = 5;
	private static final long MIN_HORIZONTAL = 0b00001110_00000000;
	private static final long MIN_VERTICAL = 0b00000010_00000010_00000010_00000000;

	/**
	 * A describer of combo in a map.
	 * 
	 * @author johnchen902
	 */
	public static class Describer {
		private final List<Combo> comboList;
		private/* lazy-initialization */List<Combo> fullComboList;
		private final Describer stacked;

		private Describer() {
			this(Collections.<Combo> emptyList(), null);
		}

		private Describer(List<Combo> comboList, Describer nextComboDescriber) {
			this.comboList = comboList;
			this.stacked = nextComboDescriber;
		}

		/**
		 * Get the list of combo in this map, excluding combo caused by
		 * stacking.
		 * 
		 * @return a list of combo
		 */
		public List<Combo> getPartialComboList() {
			return Collections.unmodifiableList(comboList);
		}

		/**
		 * Get the list of combo in this map, including combo caused by
		 * stacking.
		 * 
		 * @return a list of combo
		 */
		public List<Combo> getFullComboList() {
			if (fullComboList == null) {
				fullComboList = new ArrayList<Combo>();
				for (Describer cd = this; cd != null; cd = cd.getStacked())
					fullComboList.addAll(cd.comboList);
			}
			return Collections.unmodifiableList(fullComboList);
		}

		/**
		 * Get the number of combo in this map, including combo caused by
		 * stacking. Equivalent to <code>getFullComboList().size()</code>
		 * 
		 * @return the number of combo
		 */
		public int getFullComboCount() {
			if (fullComboList == null) {
				int count = 0;
				for (Describer cd = this; cd != null; cd = cd.getStacked())
					count += cd.comboList.size();
				return count;
			} else
				return fullComboList.size();
		}

		/**
		 * Get the combo caused by stacking
		 * 
		 * @return an <code>Describer</code>
		 */
		public Describer getStacked() {
			return stacked;
		}
	}

	/**
	 * A combo consists of two element: the location and shape of combo and the
	 * type of combo.
	 * 
	 * @author johnchen902
	 */
	public static class Combo {

		private/* mutable */long mask; // see getMask
		private final RuneStone type;

		public Combo(long mask, RuneStone type) {
			this.mask = mask;
			this.type = type;
		}

		/**
		 * A bit mask represent whether a stone is in this combo. Format:
		 * <table>
		 * <tr>
		 * <td>
		 * 
		 * <pre>
		 * a b c d e f
		 * g h i j k l
		 * m n o p q r
		 * s t u v w x
		 * y z A B C D
		 * </pre>
		 * 
		 * </td>
		 * <td>
		 * 
		 * <pre>
		 * 0b00000000\
		 *   0DCBAzy0\
		 *   0xwvuts0\
		 *   0rqponm0\
		 *   0lkjihg0\
		 *   0fedcba0\
		 *   00000000
		 * </pre>
		 * 
		 * </td>
		 * </tr>
		 * </table>
		 */
		public long getMask() {
			return mask;
		}

		/**
		 * Get the type of stones in this combo.
		 * 
		 * @return a <code>Type</code>
		 */
		public RuneStone getType() {
			return type;
		}

		@Override
		public String toString() {
			return "Combo[mask=0x" + Long.toHexString(mask) + ", type=" + type
					+ "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Combo))
				return false;
			Combo that = (Combo) obj;
			return mask == that.mask && type == that.type;
		}

		@Override
		public int hashCode() {
			int hash = 17;
			hash = hash * 31 + (int) (mask >>> 32);
			hash = hash * 31 + (int) (mask);
			hash = hash * 31 + type.hashCode();
			return hash;
		}
	}

	/**
	 * Set the map to compute.
	 * 
	 * @param that
	 *            the map being computed
	 */
	public static Describer getDescriber(RuneMap map) {
		List<Combo> comboList = new ArrayList<Combo>(2 * (WIDTH - 2)
				* (HEIGHT - 2));
		scanForHorizontalCombo(map, comboList);
		scanForVerticalCombo(map, comboList);
		if (comboList.size() > 0) {
			merge(comboList);
			// remove zeroes
			int j = 0;
			for (int i = 0; i < comboList.size(); i++)
				if (comboList.get(i).mask != 0)
					comboList.set(j++, comboList.get(i));
			while (comboList.size() > j)
				comboList.remove(comboList.size() - 1);
			// remove
			RuneMap nextmap = remove(map, comboList);
			fall(nextmap);
			// consider stacking
			return new Describer(comboList, getDescriber(nextmap));
		}
		return new Describer();
	}

	private static RuneStone typeOf(RuneMap map, int x, int y) {
		return map.getStone(x, y);
	}

	private static long neighborMask(long mask) {
		return mask << (WIDTH + 2) | mask << 1 | mask | mask >> 1
				| mask >> (WIDTH + 2);
	}

	// scan for three in a row horizontally
	private static void scanForHorizontalCombo(RuneMap map,
			List<Combo> comboList) {
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH - 2; x++) {
				RuneStone type = typeOf(map, x + 2, y);
				if (type == RuneStone.UNKNOWN) {
					x += 2;
				} else {
					if (typeOf(map, x + 1, y) == type) {
						if (typeOf(map, x, y) == type) {
							long mask = MIN_HORIZONTAL << (y * (WIDTH + 2) + x);
							comboList.add(new Combo(mask, type));
						}
						for (x++; x < WIDTH - 2
								&& type == typeOf(map, x + 2, y); x++) {
							long mask = MIN_HORIZONTAL << (y * (WIDTH + 2) + x);
							comboList.add(new Combo(mask, type));
						}
					}
					x++;
				}
			}
			/*-
			for (int x = 0; x < WIDTH - 2; x++)
				if (typeOf(x, y) != Type.UNKNOWN
						&& typeOf(x, y) == typeOf(x + 1, y)
						&& typeOf(x + 1, y) == typeOf(x + 2, y)) {
					long mask = MIN_HORIZONTAL << (y * (WIDTH + 2) + x);
					comboList.add(new Combo(mask, typeOf(x, y)));
				}
			 */
		}
	}

	// scan for three in a row vertically
	private static void scanForVerticalCombo(RuneMap map, List<Combo> comboList) {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT - 2; y++) {
				RuneStone type = typeOf(map, x, y + 2);
				if (type == RuneStone.UNKNOWN) {
					y += 2;
				} else {
					if (typeOf(map, x, y + 1) == type) {
						if (typeOf(map, x, y) == type) {
							long mask = MIN_VERTICAL << (y * (WIDTH + 2) + x);
							comboList.add(new Combo(mask, type));
						}
						for (y++; y < HEIGHT - 2
								&& type == typeOf(map, x, y + 2); y++) {
							long mask = MIN_VERTICAL << (y * (WIDTH + 2) + x);
							comboList.add(new Combo(mask, type));
						}
					}
					y++;
				}
			}
			/*-
			for (int y = 0; y < HEIGHT - 2; y++)
				if (typeOf(x, y) != Type.UNKNOWN
						&& typeOf(x, y) == typeOf(x, y + 1)
						&& typeOf(x, y + 1) == typeOf(x, y + 2)) {
					long mask = MIN_VERTICAL << (y * (WIDTH + 2) + x);
					comboList.add(new Combo(mask, typeOf(x, y)));
				}
			 */
		}
	}

	// combine neighboring or overlapping combos
	private static void merge(List<Combo> comboList) {
		for (int i = 0; i < comboList.size(); i++) {
			final Combo c1 = comboList.get(i);
			if (c1.mask == 0)
				continue;
			long neighbormask = neighborMask(c1.mask);
			boolean merged;
			do {
				merged = false;
				for (int j = i + 1; j < comboList.size(); j++) {
					Combo c2 = comboList.get(j);
					if (c1.type == c2.type && (neighbormask & c2.mask) != 0) {
						neighbormask = neighborMask(c1.mask |= c2.mask);
						c2.mask = 0;
						merged = true;
					}
				}
			} while (merged);
		}
	}

	// remove used stones
	private static RuneMap remove(RuneMap map, List<Combo> comboList) {
		RuneMap nextmap = new RuneMap(map);
		for (Combo c : comboList) {
			// mask &= mask - 1: remove lowest one bit
			for (long mask = c.mask; mask != 0; mask &= mask - 1) {
				int zeros = Long.numberOfTrailingZeros(mask);
				int x = zeros % (WIDTH + 2) - 1;
				int y = zeros / (WIDTH + 2) - 1;
				nextmap.setRuneStone(x, y, null);
			}
		}
		return nextmap;
	}

	// fall
	private static void fall(RuneMap nextmap) {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = HEIGHT - 1, y2 = HEIGHT; y >= 0; y--) {
				do {
					y2--;
				} while (y2 >= 0 && nextmap.getStone(x, y2) == null);
				if (y != y2) {
					if (y2 >= 0) {
						nextmap.setRuneStone(x, y, nextmap.getStone(x, y2));
						nextmap.setRuneStone(x, y2, null);
					} else {
						nextmap.setRuneStone(x, y, RuneStone.UNKNOWN);
					}
				}
			}
		}
	}
}
