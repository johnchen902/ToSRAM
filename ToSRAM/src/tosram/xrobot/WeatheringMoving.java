package tosram.xrobot;

import java.util.Arrays;
import java.util.Deque;

import tosram.Direction;

/**
 * A moving that handles weathering stones.
 * 
 * @author johnchen902
 */
public class WeatheringMoving implements Moving {

	private final Moving moving;
	private final boolean[] mask; // true if weathering
	private static final int WIDTH = 6;

	/**
	 * A moving that handles weathering stones.
	 * 
	 * @param next
	 *            the filtered moving
	 * @param mask
	 *            <code>mask[y * w + x]</code> is <code>true</code> if the stone
	 *            at position <code>(x, y)</code> is a weathering stone;
	 *            <code>false</code> otherwise
	 */
	public WeatheringMoving(Moving moving, boolean[] mask) {
		this.moving = moving;
		this.mask = Arrays.copyOf(mask, mask.length);
	}

	@Override
	public Direction[] getDirections(int x, int y) {
		if (mask[y * WIDTH + x]) {
			return new Direction[0];
		} else {
			return moving.getDirections(x, y);
		}
	}

	@Override
	public int cost(Direction d, Deque<Direction> stack) {
		return moving.cost(d, stack);
	}
}
