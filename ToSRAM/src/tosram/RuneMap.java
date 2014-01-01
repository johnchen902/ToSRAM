package tosram;

import java.util.Arrays;

/**
 * A map (matrix) of stones.
 * 
 * @author johnchen902
 */
public class RuneMap {
	private final int width, height;
	private final RuneStone[] stones;

	/**
	 * Create a new map with specified size
	 * 
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 */
	public RuneMap(int w, int h) {
		width = w;
		height = h;
		stones = new RuneStone[w * h];
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            the map to copy from
	 */
	public RuneMap(RuneMap that) {
		this(that.width, that.height);
		System.arraycopy(that.stones, 0, this.stones, 0, this.stones.length);
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
	public RuneStone getStone(int x, int y) {
		return stones[y * width + x];
	}

	/**
	 * Set the stone at {@code (x, y)} to {@code r}.
	 * 
	 * @param x
	 *            the X coordinate of the stone
	 * @param y
	 *            the Y coordinate of the stone
	 * @param r
	 *            a {@code RuneStone}
	 */
	public void setRuneStone(int x, int y, RuneStone r) {
		stones[y * width + x] = r;
	}

	/**
	 * Set the content of this map to another map.
	 * 
	 * @param that
	 *            the map being assigned from
	 * @throws IllegalArgumentException
	 *             if the dimension of this is are different from the other
	 */
	public void assign(RuneMap that) throws IllegalArgumentException {
		if (this.width != that.width)
			throw new IllegalArgumentException("this.width != that.width");
		if (this.height != that.height)
			throw new IllegalArgumentException("this.height != that.height");
		System.arraycopy(that.stones, 0, this.stones, 0, this.stones.length);
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
	 * A string representation of this <code>RuneMap</code>. May contains new
	 * line in the middle, but not at the end.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(height * (width + 1) - 1);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				RuneStone stone = getStone(x, y);
				sb.append(stone == null ? "?" : stone.toString());
			}
			if (y != height - 1)
				sb.append('\n');
		}
		return sb.toString();
	}
}
