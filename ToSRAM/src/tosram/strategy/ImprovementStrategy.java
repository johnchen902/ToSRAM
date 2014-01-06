package tosram.strategy;

import java.util.Arrays;
import java.util.Deque;

import tosram.Direction;
import tosram.RuneMap;

/**
 * A search strategy that permits a branch when it determines that branch is
 * improving.
 * 
 * @author johnchen902
 */
public class ImprovementStrategy implements SearchStrategy {

	private static final int MAX_STEP = 50;
	private static final int HOPEFUL_STEP = 9;
	private final double[] bestQualities;

	public ImprovementStrategy() {
		bestQualities = new double[MAX_STEP];
	}

	@Override
	public void reset() {
		Arrays.fill(bestQualities, 0.0);
	}

	private int steps;
	private double quality;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			double q) {
		steps = stack.size();
		quality = q;
		for (int i = steps; i < MAX_STEP && quality > bestQualities[i]; i++)
			bestQualities[i] = quality;
	}

	@Override
	public boolean isToStop() {
		if (steps >= MAX_STEP)
			return true;
		if (steps >= HOPEFUL_STEP
				&& bestQualities[steps - HOPEFUL_STEP] > quality)
			return true;
		return false;
	}

	@Override
	public boolean isToDiagonal() {
		return false;
	}

}
