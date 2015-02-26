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
		StringBuilder sb = new StringBuilder(height * (width + 1) - 1);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				RuneStone stone = getRuneStone(x, y);
				sb.append(stone == null ? "?" : stone.toString());
			}
			if (y != height - 1)
				sb.append('/');
		}
		return sb.toString();
	}
}
