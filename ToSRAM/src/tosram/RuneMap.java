package tosram;

import java.util.Arrays;

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
		this.stones = Arrays.copyOf(mutable.getRuneStones(), width * height);
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
		if (x < 0 || x >= width)
			throw new IndexOutOfBoundsException("x");
		if (y < 0 || y >= height)
			throw new IndexOutOfBoundsException("y");
		return stones[y * width + x];
	}

	/**
	 * Make a <code>MutableRuneMap</code> from this <code>RuneMap</code>
	 * 
	 * @return a <code>MutableRuneMap</code>
	 */
	public MutableRuneMap toMutable() {
		MutableRuneMap mutable = new MutableRuneMap(width, height);
		System.arraycopy(stones, 0, mutable.getRuneStones(), 0, width * height);
		return mutable;
	}

	/**
	 * Check if two <code>RuneMap</code> has the same width, height and content.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RuneMap))
			return false;
		RuneMap that = (RuneMap) obj;
		return this.width == that.width && this.height == that.height
				&& Arrays.equals(this.stones, that.stones);
	}

	/**
	 * Computes a hash code for this map.
	 */
	@Override
	public int hashCode() {
		int h = 17;
		h = h * 31 + width;
		h = h * 31 + height;
		h = h * 31 + Arrays.hashCode(stones);
		return h;
	}

	/**
	 * A string representation of this <code>RuneMap</code>.
	 */
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
