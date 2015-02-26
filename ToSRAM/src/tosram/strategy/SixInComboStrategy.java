package tosram.strategy;

import java.util.Deque;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.RuneStone;

/**
 * A strategy that requires six stones to be removed in the same combo.
 * 
 * @author johnchen902
 */
public class SixInComboStrategy extends FilterSolutionStrategy {

	private final boolean heartAllowed;
	private boolean bestHasSix;

	/**
	 * Create a <code>SixInComboStrategy</code>.
	 * 
	 * @param next
	 *            the filtered strategy
	 * @param heartAllowed
	 *            <code>true</code> if heart stones are counted;
	 *            <code>false</code> otherwise
	 */
	public SixInComboStrategy(SolutionStrategy next, boolean heartAllowed) {
		super(next);
		this.heartAllowed = heartAllowed;
	}

	@Override
	public void reset() {
		super.reset();
		bestHasSix = false;
	}

	private boolean hasSix;

	@Override
	public void submit(MutableRuneMap map, int x, int y,
			Deque<Direction> stack, ComboCalculator.Describer cd) {
		super.submit(map, x, y, stack, cd);
		hasSix = false;
		for (ComboCalculator.Combo ccc : cd.getFullComboList()) {
			if (Long.bitCount(ccc.getMask()) >= 6
					&& (ccc.getType() != RuneStone.HEART || heartAllowed)) {
				hasSix = true;
				break;
			}
		}
	}

	@Override
	public int compareSolution() {
		if (hasSix != bestHasSix)
			return hasSix ? 1 : -1;
		return super.compareSolution();
	}

	@Override
	public double getQuality() {
		if (hasSix)
			return 0.3 + 0.7 * super.getQuality();
		else
			return 0.7 * super.getQuality();
	}

	@Override
	public String getMilestone() {
		return MilestoneFormatter.formatSixInCombo(hasSix)
				+ super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		bestHasSix = hasSix;
	}
}
