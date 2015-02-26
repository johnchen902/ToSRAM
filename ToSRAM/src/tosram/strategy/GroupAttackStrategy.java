package tosram.strategy;

import java.util.Deque;
import java.util.EnumSet;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.RuneMap;
import tosram.RuneStone;

/**
 * A strategy that tries to find most group attacks.
 * 
 * @author johnchen902
 */
public class GroupAttackStrategy extends FilterSolutionStrategy {

	private final EnumSet<RuneStone> requirements;
	private int bestGroupAttacks;

	/**
	 * Create a <code>GroupAttackStrategy</code> find solution with most group
	 * attack of any type except heart.
	 * 
	 * @param next
	 *            the filtered strategy
	 */
	public GroupAttackStrategy(SolutionStrategy next) {
		this(next, EnumSet.complementOf(EnumSet.of(RuneStone.HEART)));
	}

	/**
	 * Create a <code>GroupAttackStrategy</code> find solution with group attack
	 * of the specified type.
	 * 
	 * @param next
	 *            the filtered strategy
	 * @param type
	 *            the type of group attack required
	 */
	public GroupAttackStrategy(SolutionStrategy next, RuneStone type) {
		this(next, EnumSet.of(type));
	}

	/**
	 * Create a <code>GroupAttackStrategy</code> find solution with most group
	 * attack of the specified types.
	 * 
	 * @param next
	 *            the filtered strategy
	 * @param typeSet
	 *            the set of type of group attack required
	 */
	public GroupAttackStrategy(SolutionStrategy next, EnumSet<RuneStone> typeSet) {
		super(next);
		this.requirements = EnumSet.copyOf(typeSet);
	}

	@Override
	public void reset() {
		super.reset();
		bestGroupAttacks = 0;
	}

	private int groupAttacks;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack,
			ComboCalculator.Describer cd) {
		super.submit(map, x, y, stack, cd);
		EnumSet<RuneStone> types = EnumSet.noneOf(RuneStone.class);
		for (ComboCalculator.Combo ccc : cd.getFullComboList()) {
			if (Long.bitCount(ccc.getMask()) >= 5) {
				types.add(ccc.getType());
			}
		}
		types.retainAll(requirements);
		groupAttacks = types.size();
	}

	@Override
	public int compareSolution() {
		if (groupAttacks != bestGroupAttacks)
			return groupAttacks - bestGroupAttacks;
		return super.compareSolution();
	}

	@Override
	public double getQuality() {
		final double weight = 0.2 + 0.1 * requirements.size();
		final double weight1 = weight / requirements.size();
		final double weight2 = 1 - weight;
		return weight1 * groupAttacks + weight2 * super.getQuality();
	}

	@Override
	public String getMilestone() {
		return MilestoneFormatter.formatGroupAttacks(groupAttacks)
				+ super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		bestGroupAttacks = groupAttacks;
	}
}
