package tosram.strategy;

import java.util.Deque;
import tosram.ComboCalculator;
import tosram.Direction;
import tosram.RuneMap;
import tosram.RuneStone.Type;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A strategy that requires six stones to be removed in the same combo.
 * 
 * @author johnchen902
 */
public class SixInComboStrategy extends FilterStrategy {

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
	public SixInComboStrategy(Strategy next, boolean heartAllowed) {
		super(next);
		this.heartAllowed = heartAllowed;
	}

	@Override
	public void reset(RuneMap initial) {
		super.reset(initial);
		bestHasSix = false;
	}

	private boolean hasSix;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		super.submit(map, x, y, stack);
		hasSix = false;
		for (ComboCalculator.Combo ccc : getComboCalculator().getComboList()) {
			if (Long.bitCount(ccc.getMask()) >= 6
					&& (ccc.getType() != Type.HEART || heartAllowed)) {
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
	public String getMilestone() {
		if (hasSix)
			return "with six " + super.getMilestone();
		else
			return "without six " + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		bestHasSix = hasSix;
	}
}
