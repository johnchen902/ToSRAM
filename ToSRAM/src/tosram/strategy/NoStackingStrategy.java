package tosram.strategy;

import java.util.Deque;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.RuneMap;

/**
 * A strategy that forbids stacking.
 * 
 * @author johnchen902
 */
public class NoStackingStrategy extends FilterSolutionStrategy {

	private boolean bestNoStacking;

	/**
	 * @see FilterSolutionStrategy#FilterSolutionStrategy(SolutionStrategy)
	 */
	public NoStackingStrategy(SolutionStrategy strategy) {
		super(strategy);
	}

	@Override
	public void reset() {
		super.reset();
		bestNoStacking = false;
	}

	private boolean noStacking;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboCalculator.Describer cd) {
		super.submit(map, x, y, stack, cd);
		noStacking = cd.getPartialComboList().size() == 0
				|| cd.getStacked().getPartialComboList().size() == 0;
	}

	@Override
	public int compareSolution() {
		if (noStacking != bestNoStacking)
			return noStacking ? 1 : -1;
		return super.compareSolution();
	}

	@Override
	public double getQuality() {
		if (noStacking)
			return 0.3 + 0.7 * super.getQuality();
		else
			return 0.7 * super.getQuality();
	}

	@Override
	public String getMilestone() {
		if (noStacking)
			return super.getMilestone();
		else
			return "STACKING " + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		bestNoStacking = noStacking;
	}
}
