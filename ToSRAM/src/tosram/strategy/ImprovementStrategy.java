package tosram.strategy;

import java.util.Arrays;
import java.util.Deque;
import java.util.EnumSet;

import tosram.Direction;
import tosram.MutableRuneMap;

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
	private double minProgress;

	public ImprovementStrategy() {
		bestQualities = new double[MAX_STEP];
	}

	@Override
	public void reset() {
		Arrays.fill(bestQualities, 0.0);
		minProgress = 0.0;
	}

	private int steps;
	private double quality;

	@Override
	public void submit(MutableRuneMap map, int x, int y,
			Deque<Direction> stack, double q) {
		steps = stack.size();
		quality = q;
		for (int i = steps; i < MAX_STEP && quality > bestQualities[i]; i++)
			bestQualities[i] = quality;
	}

	@Override
	public EnumSet<Direction> getDirections() {
		if (isToStop()) {
			return EnumSet.noneOf(Direction.class);
		} else {
			return EnumSet.range(Direction.WEST, Direction.SOUTH);
		}
	}

	private boolean isToStop() {
		if (steps >= MAX_STEP)
			return true;
		if (steps >= HOPEFUL_STEP
				&& bestQualities[steps - HOPEFUL_STEP] > quality)
			return true;
		return false;
	}

	@Override
	public double adaptProgress(double progress) {
		if (progress == 0.0)
			return 0.0;
		if (progress == 1.0)
			return 1.0;
		if (minProgress == 0.0)
			minProgress = progress;
		return 1 - Math.log(progress) / Math.log(minProgress);
	}

}
