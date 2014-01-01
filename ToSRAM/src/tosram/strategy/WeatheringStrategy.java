package tosram.strategy;

import java.util.Arrays;
import java.util.Deque;

import tosram.Direction;
import tosram.RuneMap;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A strategy that handles weathering stones.
 * 
 * @author johnchen902
 */
public class WeatheringStrategy extends FilterStrategy {

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
	public WeatheringStrategy(Strategy next, boolean[] mask) {
		super(next);
		this.mask = Arrays.copyOf(mask, mask.length);
	}

	int position;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		super.submit(map, x, y, stack);
		position = y * map.getWidth() + x;
	}

	@Override
	public boolean isToStop() {
		return mask[position] || super.isToStop();
	}
}
