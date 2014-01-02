package tosram.strategy;

import java.util.Deque;

import tosram.ComboDescriber;
import tosram.Direction;
import tosram.RuneMap;

public class NullSolutionStrategy implements SolutionStrategy {

	@Override
	public void reset() {
	}

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboDescriber combos) {
	}

	@Override
	public int compareSolution() {
		return 0;
	}

	@Override
	public String getMilestone() {
		return "!";
	}

	@Override
	public void solutionAccepted() {
	}

}
