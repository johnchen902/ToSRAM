package tosram.strategy;

import java.util.Deque;
import java.util.EnumSet;

import tosram.Direction;
import tosram.RuneMap;

/**
 * A search strategy that permits a branch when it doesn't exceed the step
 * limit, which is <i>in linear to</i> the quality of the solution.
 * (<i>&#21576;&#32218;&#24615;&#38364;&#20418;</i>)
 * 
 * @author johnchen902
 */
public class LinearStrategy implements SearchStrategy {

	private static final double MULTIPLIER = 55.0;
	private static final double CONSTANT = 1.0;
	private static final int GRUANTEED_STEPS = 3;
	private static final double DIAGONAL_THEREHOLD = 0.55;
	private static final double DIAGONAL_CONSTANT = -4.0;

	private int steps;
	private double quality;

	@Override
	public void reset() {
	}

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			double quality) {
		this.steps = stack.size();
		this.quality = quality;
	}

	@Override
	public EnumSet<Direction> getDirections() {
		if (isToStop()) {
			return EnumSet.noneOf(Direction.class);
		} else if (isToDiagonal()) {
			return EnumSet.allOf(Direction.class);
		} else {
			return EnumSet.range(Direction.WEST, Direction.SOUTH);
		}
	}

	private boolean isToStop() {
		return steps >= Math.max((int) (MULTIPLIER * quality + CONSTANT),
				GRUANTEED_STEPS);
	}

	private boolean isToDiagonal() {
		return quality >= DIAGONAL_THEREHOLD
				&& steps >= MULTIPLIER * quality + DIAGONAL_CONSTANT;
	}

	@Override
	public double adaptProgress(double progress) {
		return progress;
	}

}
