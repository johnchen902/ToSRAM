package tosram;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * An immutable matrix of RuneStone.
 * 
 * @author johnchen902
 */
public class RuneMap {
	private final int width, height;
	private final RuneStone[] stones;

	/**
	 * Construct a <code>RuneMap</code> from a <code>MutableRuneMap</code>
	 * 
	 * @param mutable
	 *            a <code>MutableRuneMap</code>
	 */
	public RuneMap(MutableRuneMap mutable) {
		this.width = mutable.getWidth();
		this.height = mutable.getHeight();
		this.stones = new RuneStone[width * height];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				stones[y * width + x] = mutable.getRuneStone(x, y);
	}

	/**
	 * Returns the width of the map.
	 * 
	 * @return the width of the map
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the map.
	 * 
	 * @return the height of the map
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the stone at {@code (x, y)}.
	 * 
	 * @param x
	 *            the X coordinate of the stone
	 * @param y
	 *            the Y coordinate of the stone
	 * @return a {@code RuneStone}
	 */
	public RuneStone getRuneStone(int x, int y) {
		if (!isInRange(x, y))
			throw new IndexOutOfBoundsException();
		return stones[y * width + x];
	}

	/**
	 * Checks if the specified location is in valid range of this
	 * <code>RuneMap</code>.
	 * 
	 * @param x
	 *            the X coordinate of the specified location
	 * @param y
	 *            the Y coordinate of the specified location
	 * @return <code>true</code> if in range; <code>false</code> otherwise
	 */
	public boolean isInRange(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	/**
	 * Make a <code>MutableRuneMap</code> from this <code>RuneMap</code>
	 * 
	 * @return a <code>MutableRuneMap</code>
	 */
	public MutableRuneMap toMutable() {
		MutableRuneMap mutable = new MutableRuneMap(width, height);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				mutable.setRuneStone(x, y, getRuneStone(x, y));
		return mutable;
	}

	/**
	 * Returns a sequential Stream with this <code>RuneMap</code> as its source.
	 * 
	 * @return a <code>Stream</code> for this <code>RuneMap</code>
	 */
	public Stream<RuneStone> stream() {
		return Arrays.stream(stones);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RuneMap))
			return false;
		RuneMap that = (RuneMap) obj;
		return this.width == that.width && this.height == that.height
				&& Arrays.equals(this.stones, that.stones);
	}

	@Override
	public int hashCode() {
		int h = 17;
		h = h * 31 + width;
		h = h * 31 + height;
		h = h * 31 + Arrays.hashCode(stones);
		return h;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				sb.append(getRuneStone(x, y)).append(' ');
			sb.deleteCharAt(sb.length() - 1).append('/');
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
}
