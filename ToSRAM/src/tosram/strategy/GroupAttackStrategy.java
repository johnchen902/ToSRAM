package tosram.strategy;

import java.util.Deque;
import java.util.EnumSet;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.RuneMap;
import tosram.RuneStone.Type;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A strategy that tries to find most group attacks.
 * 
 * @author johnchen902
 */
public class GroupAttackStrategy extends FilterStrategy {

	private final EnumSet<Type> requirements;
	private int bestGroupAttacks;

	/**
	 * Create a <code>GroupAttackStrategy</code> find solution with most group
	 * attack of any type except heart.
	 * 
	 * @param next
	 *            the filtered strategy
	 */
	public GroupAttackStrategy(Strategy next) {
		this(next, EnumSet.complementOf(EnumSet.of(Type.HEART)));
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
	public GroupAttackStrategy(Strategy next, Type type) {
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
	public GroupAttackStrategy(Strategy next, EnumSet<Type> typeSet) {
		super(next);
		this.requirements = EnumSet.copyOf(typeSet);
	}

	@Override
	public void reset(RuneMap initial) {
		super.reset(initial);
		bestGroupAttacks = 0;
	}

	private int groupAttacks;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		super.submit(map, x, y, stack);
		EnumSet<Type> types = EnumSet.noneOf(Type.class);
		for (ComboCalculator.Combo ccc : getComboCalculator().getComboList()) {
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
	public String getMilestone() {
		return groupAttacks + "G.A. " + super.getMilestone();
	}

	@Override
	public void solutionAccepted() {
		super.solutionAccepted();
		bestGroupAttacks = groupAttacks;
	}
}
