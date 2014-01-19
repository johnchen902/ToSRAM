package tosram.strategy;

import java.util.Arrays;
import java.util.Deque;

import tosram.Direction;
import tosram.RuneMap;

/**
 * A strategy that handles weathering stones.
 * 
 * @author johnchen902
 */
public class WeatheringStrategy implements SearchStrategy {

	private final SearchStrategy strategy;
	private final boolean[] mask; // true if weathering

	/**
	 * A strategy that handles weathering stones.
	 * 
	 * @param next
	 *            the filtered strategy
	 * @param mask
	 *            <code>mask[y * w + x]</code> is <code>true</code> if the stone
	 *            at position <code>(x, y)</code> is a weathering stone;
	 *            <code>false</code> otherwise
	 */
	public WeatheringStrategy(SearchStrategy next, boolean[] mask) {
		strategy = next;
		this.mask = Arrays.copyOf(mask, mask.length);
	}

	private int position;

	@Override
	public void reset() {
		strategy.reset();
	}

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			double quality) {
		strategy.submit(map, x, y, stack, quality);
		position = y * map.getWidth() + x;
	}

	@Override
	public boolean isToStop() {
		return mask[position] || strategy.isToStop();
	}

	@Override
	public boolean isToDiagonal() {
		return strategy.isToDiagonal();
	}

	@Override
	public double adaptProgress(double progress) {
		return strategy.adaptProgress(progress);
	}

}
