package tosram.strategy;

import java.util.Arrays;
import java.util.Deque;

import tosram.ComboDescriber;
import tosram.Direction;
import tosram.RuneMap;

public class ImprovementStrategy implements SearchStrategy {

	private static final int MAX_STEP = 50;
	private static final int HOPEFUL_STEP = 9;
	private final double[] bestValue;

	public ImprovementStrategy() {
		bestValue = new double[MAX_STEP];
	}

	@Override
	public void reset() {
		Arrays.fill(bestValue, 0.0);
	}

	private int combo;
	private int steps;
	private double value;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboDescriber cc) {
		combo = cc.getCombo();
		steps = stack.size();
		value = combo / 10.0;
		for (int i = steps; i < MAX_STEP && value > bestValue[i]; i++)
			bestValue[i] = value;
	}

	@Override
	public boolean isToStop() {
		if (steps >= MAX_STEP)
			return true;
		if (steps >= HOPEFUL_STEP && bestValue[steps - HOPEFUL_STEP] > value)
			return true;
		return false;
	}

	@Override
	public boolean isToDiagonal() {
		return false;
	}

}
