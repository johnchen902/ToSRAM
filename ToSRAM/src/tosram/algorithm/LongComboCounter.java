package tosram.algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tosram.MutableRuneMap;
import tosram.RuneStone;

/**
 * An combo counter using <code>long</code> as bitmask under Tower of Savior
 * rules. As <code>long</code> only has <code>64</code> bits, it can only handle
 * maps satisfying <code>(width + 1) * height - 1 <= 64</code>.
 * 
 * @author johnchen902
 */
public class LongComboCounter implements ComboCounter {

	private static class ComboImpl extends Combo {

		private/* assigned after construction */int batch;
		private/* mutable */long mask;
		private final RuneStone type;
		private final int width;

		private ComboImpl(long mask, RuneStone type, int width) {
			this.mask = mask;
			this.type = type;
			this.width = width;
		}

		@Override
		public int getBatch() {
			return batch;
		}

		@Override
		public RuneStone getType() {
			return type;
		}

		@Override
		public int getCount() {
			return Long.bitCount(mask);
		}

		@Override
		public List<Point> getPoints() {
			List<Point> p = new ArrayList<Point>(getCount());
			long mask = this.mask;
			while (mask != 0L) {
				int pos = Long.numberOfTrailingZeros(mask);
				p.add(new Point(pos % (width + 1), pos / (width + 1)));
				mask &= ~Long.lowestOneBit(mask);
			}
			return Collections.unmodifiableList(p);
		}

		private long toWidth(int newWidth) {
			if (width == newWidth)
				return this.mask;
			long mask = this.mask, newMask = 0L;
			while (mask != 0) {
				int pos = Long.numberOfTrailingZeros(mask);
				int x = pos % (width + 1), y = pos / (width + 1);
				newMask |= getBit(x, y, newWidth);
				mask &= ~Long.lowestOneBit(mask);
			}
			return newMask;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof ComboImpl))
				return super.equals(obj);
			ComboImpl other = (ComboImpl) obj;
			if (batch != other.batch)
				return false;
			if (type != other.type)
				return false;
			if (toWidth(other.width) != width)
				return false;
			return true;
		}

		private int getPointHashCode(int x, int y) {
			long bits = java.lang.Double.doubleToLongBits(x);
			bits ^= java.lang.Double.doubleToLongBits(y) * 31;
			int result = (((int) bits) ^ ((int) (bits >> 32)));
			assert result == new Point(x, y).hashCode();
			return result;
		}

		private int getPointsHashCode() {
			int result = 1;
			long mask = this.mask;
			while (mask != 0) {
				int pos = Long.numberOfTrailingZeros(mask);
				int x = pos % (width + 1), y = pos / (width + 1);
				result = 31 * result + getPointHashCode(x, y);
				mask &= ~Long.lowestOneBit(mask);
			}
			assert result == getPoints().hashCode();
			return result;
		}

		/*
		 * It is overridden to compute the same hash code without creating all
		 * the objects. Not sure if it's really better but I'm not bothered to
		 * revert it.
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getBatch();
			result = prime * result + getType().hashCode();
			result = prime * result + getPointsHashCode();
			assert result == super.hashCode();
			return result;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>((width + 1) * height) - 1 > 64</code>
	 */
	@Override
	public List<Combo> countCombo(MutableRuneMap map) {
		if (((map.getWidth() + 1) * map.getHeight()) - 1 > Long.SIZE)
			throw new IllegalArgumentException("Cannot handle map with size "
					+ map.getWidth() + " x " + map.getHeight());

		List<Combo> comboList = new ArrayList<>();
		for (int batch = 0;; batch++) {
			int lastSize = comboList.size();
			scanForHorizontalCombo(map, comboList);
			scanForVerticalCombo(map, comboList);
			if (lastSize == comboList.size())
				break;
			mergeCombo(comboList, lastSize);
			removeEmptyCombo(comboList, lastSize);
			setBatch(comboList, lastSize, batch);
			map = dissolve(map, comboList, lastSize);
		}
		return Collections.unmodifiableList(comboList);
	}

	/*
	 * Get the bit of a stone at the specified location.
	 */
	private static long getBit(int x, int y, int width) {
		return 1L << (y * (width + 1) + x);
	}

	/*
	 * Get the bit mask of a simple horizontal combo at the specified location.
	 */
	private static long horizontalMask(int x, int y, int width) {
		return 0b111L << (y * (width + 1) + x);
	}

	/*
	 * Find all horizontal combo and add them to comboList.
	 */
	private static void scanForHorizontalCombo(MutableRuneMap map,
			List<Combo> comboList) {
		int width = map.getWidth(), height = map.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 2; x < width;) { // . . x
				RuneStone type = map.getRuneStone(x, y);
				if (type == RuneStone.UNKNOWN) {
					x += 3; // . . U . . x
				} else {
					if (map.getRuneStone(x - 1, y) == type) {
						long mask = 0;
						if (map.getRuneStone(x - 2, y) == type)
							mask = horizontalMask(x - 2, y, width);
						for (x++; x < width && type == map.getRuneStone(x, y); x++)
							mask |= horizontalMask(x - 2, y, width);
						if (mask != 0)
							comboList.add(new ComboImpl(mask, type, width));
					}
					x += 2; // . a b+ . x
				}
			}
		}
	}

	/*
	 * Get the bit mask of a simple vertical combo at (0, 0).
	 */
	private static long baseVerticalMask(int width) {
		return 1L | 1L << (width + 1) | 1L << (width + 1) << (width + 1);
	}

	/*
	 * Get the bit mask of a simple vertical combo at the specified location.
	 */
	private static long verticalMask(int x, int y, int width) {
		return baseVerticalMask(width) << (y * (width + 1) + x);
	}

	/*
	 * Find all vertical combo and add them to comboList.
	 */
	private static void scanForVerticalCombo(MutableRuneMap map,
			List<Combo> comboList) {
		int width = map.getWidth(), height = map.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 2; y < height;) { // . . y
				RuneStone type = map.getRuneStone(x, y);
				if (type == RuneStone.UNKNOWN) {
					y += 3; // . . U . . y
				} else {
					if (map.getRuneStone(x, y - 1) == type) {
						long mask = 0;
						if (map.getRuneStone(x, y - 2) == type)
							mask = verticalMask(x, y - 2, width);
						for (y++; y < height && type == map.getRuneStone(x, y); y++)
							mask |= verticalMask(x, y - 2, width);
						if (mask != 0)
							comboList.add(new ComboImpl(mask, type, width));
					}
					y += 2; // . a b+ . y
				}
			}
		}
	}

	/*
	 * Get the mask including itself and its neighborhood.
	 */
	private static long neighborMask(long mask, int width) {
		return mask << (width + 1) | mask << 1 | mask | mask >>> 1
				| mask >>> (width + 1);
	}

	/*
	 * Check if two combo should be merged to one.
	 */
	private static boolean shouldMerge(ComboImpl c1, ComboImpl c2) {
		return c1.type == c2.type
				&& (neighborMask(c1.mask, c1.width) & c2.mask) != 0;
	}

	/*
	 * Merge all combo that should be merged.
	 */
	private static void mergeCombo(List<Combo> comboList, int from) {
		for (int i = from; i < comboList.size(); i++) {
			final ComboImpl c1 = (ComboImpl) comboList.get(i);
			if (c1.mask == 0)
				continue;
			boolean merged;
			do {
				merged = false;
				for (int j = i + 1; j < comboList.size(); j++) {
					ComboImpl c2 = (ComboImpl) comboList.get(j);
					if (shouldMerge(c1, c2)) {
						c1.mask |= c2.mask;
						c2.mask = 0;
						merged = true;
					}
				}
			} while (merged);
		}
	}

	private static void removeEmptyCombo(List<Combo> comboList, int from) {
		comboList.removeIf(c -> ((ComboImpl) c).mask == 0);
	}

	private static void setBatch(List<Combo> comboList, int from, int batch) {
		for (int i = from; i < comboList.size(); i++)
			((ComboImpl) comboList.get(i)).batch = batch;
	}

	/*
	 * Remove stones in combo from the map, make remaining stones fall and fill
	 * empty spots with unknown stones.
	 */
	private static MutableRuneMap dissolve(MutableRuneMap map,
			List<Combo> comboList, int from) {
		map = new MutableRuneMap(map);
		long usedMask = 0;
		for (int i = from; i < comboList.size(); i++)
			usedMask |= ((ComboImpl) comboList.get(i)).mask;
		int width = map.getWidth(), height = map.getHeight();
		for (int x = 0; x < width; x++) {
			int yTo = height - 1;
			int yFrom = height - 2;
			while (yTo >= 0 && yFrom >= 0) {
				if ((usedMask & getBit(x, yFrom, width)) != 0 || yFrom >= yTo) {
					yFrom--;
				} else {
					if ((usedMask & getBit(x, yTo, width)) != 0) {
						map.setRuneStone(x, yTo, map.getRuneStone(x, yFrom));
						usedMask ^= getBit(x, yTo, width);
						usedMask ^= getBit(x, yFrom, width);
						yFrom--;
					}
					yTo--;
				}
			}
			while (yTo >= 0) {
				if ((usedMask & getBit(x, yTo, width)) != 0) {
					map.setRuneStone(x, yTo, RuneStone.UNKNOWN);
					usedMask ^= getBit(x, yTo, width);
				}
				yTo--;
			}
		}
		return map;
	}
}
