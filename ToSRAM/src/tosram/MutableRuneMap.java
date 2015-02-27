package tosram;

import java.util.Arrays;

/**
 * A mutable matrix of RuneStone.
 * 
 * @author johnchen902
 */
public class MutableRuneMap {
	private final int width, height;
	private final RuneStone[] stones;

	/**
	 * Create a new MutableRuneMap with specified size
	 * 
	 * @param w
	 *            its width
	 * @param h
	 *            its height
	 */
	public MutableRuneMap(int w, int h) {
		if (w <= 0)
			throw new IllegalArgumentException("width <= 0");
		if (h <= 0)
			throw new IllegalArgumentException("height <= 0");
		width = w;
		height = h;
		stones = new RuneStone[w * h];
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            the MutableRuneMap to copy from
	 */
	public MutableRuneMap(MutableRuneMap that) {
		this.width = that.width;
		this.height = that.height;
		this.stones = Arrays.copyOf(that.getRuneStones(), width * height);
	}

	/**
	 * Returns the width of this MutableRuneMap.
	 * 
	 * @return the width of this MutableRuneMap
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of this MutableRuneMap.
	 * 
	 * @return the height of this MutableRuneMap
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the RuneStone at location {@code (x, y)}.
	 * 
	 * @param x
	 *            the X coordinate of the specified location
	 * @param y
	 *            the Y coordinate of the specified location
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
	 * Set the RuneStone at location {@code (x, y)} to {@code r}.
	 * 
	 * @param x
	 *            the X coordinate of the specified location
	 * @param y
	 *            the Y coordinate of the specified location
	 * @param r
	 *            a {@code RuneStone}
	 */
	public void setRuneStone(int x, int y, RuneStone r) {
		if (x < 0 || x >= width)
			throw new IndexOutOfBoundsException("x");
		if (y < 0 || y >= height)
			throw new IndexOutOfBoundsException("y");
		stones[y * width + x] = r;
	}

	/**
	 * Get the RuneStone array that backed up this MutableRuneMap.
	 * 
	 * @return the RuneStone array
	 */
	public RuneStone[] getRuneStones() {
		return stones;
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
