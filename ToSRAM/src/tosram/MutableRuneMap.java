package tosram;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A mutable matrix of RuneStone.
 * 
 * @author johnchen902
 */
public class MutableRuneMap {
	private final int width, height;
	private final RuneStone[] stones;

	/**
	 * Create an empty MutableRuneMap with specified size.
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
		Arrays.fill(stones, RuneStone.UNKNOWN);
	}

	/**
	 * Create a MutableRuneMap with copied content from <code>that</code>.
	 * 
	 * @param that
	 *            the MutableRuneMap to copy from
	 */
	public MutableRuneMap(MutableRuneMap that) {
		this.width = that.width;
		this.height = that.height;
		this.stones = Arrays.copyOf(that.stones, that.stones.length);
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
		if (!isInRange(x, y))
			throw new IndexOutOfBoundsException();
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
		if (!isInRange(x, y))
			throw new IndexOutOfBoundsException();
		stones[y * width + x] = Objects.requireNonNull(r);
	}

	/**
	 * Checks if the specified location is in valid range of this
	 * <code>MutableRuneMap</code>.
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
	 * Swap two stone at the specific locations.
	 * 
	 * @param x1
	 *            the X coordinate of the first stone
	 * @param y1
	 *            the Y coordinate of the first stone
	 * @param x2
	 *            the X coordinate of the second stone
	 * @param y2
	 *            the Y coordinate of the second stone
	 */
	public void swap(int x1, int y1, int x2, int y2) {
		RuneStone stone1 = getRuneStone(x1, y1);
		RuneStone stone2 = getRuneStone(x2, y2);
		setRuneStone(x1, y1, stone2);
		setRuneStone(x2, y2, stone1);
	}

	/**
	 * Returns a sequential Stream with this <code>MutableRuneMap</code> as its
	 * source.
	 * 
	 * @return a <code>Stream</code> for this <code>MutableRuneMap</code>
	 */
	public Stream<RuneStone> stream() {
		return Arrays.stream(stones);
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
