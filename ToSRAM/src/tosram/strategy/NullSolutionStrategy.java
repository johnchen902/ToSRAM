package tosram.strategy;

import java.util.Deque;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.MutableRuneMap;

public class NullSolutionStrategy implements SolutionStrategy {

	@Override
	public void reset() {
	}

	@Override
	public void submit(MutableRuneMap map, int x, int y,
			Deque<Direction> stack, ComboCalculator.Describer combos) {
	}

	@Override
	public int compareSolution() {
		return 0;
	}

	@Override
	public double getQuality() {
		return 0.5;
	}

	@Override
	public String getMilestone() {
		return MilestoneFormatter.formatNull();
	}

	@Override
	public void solutionAccepted() {
	}

}
