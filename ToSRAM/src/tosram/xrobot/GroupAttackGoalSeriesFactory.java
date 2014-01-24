package tosram.xrobot;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;

import tosram.ComboCalculator;
import tosram.RuneMap;
import tosram.RuneStone.Type;

/**
 * A factory that produce a series of goal leading to the maximum combo and the
 * maximum group attack.
 * 
 * @author johnchen902
 */
class GroupAttackGoalSeriesFactory implements GoalSeriesFactory {

	private static int getPossibleCombo(int count) {
		if (count <= 20)
			return count / 3;
		switch (count) {
		case 21:
		case 22:
			return 5;
		case 23:
			return 4;
		case 24:
		case 25:
			return 3;
		case 26:
			return 2;
		case 27:
		case 28:
		case 29:
		case 30:
			return 1;
		default:
			return 0;
		}
	}

	@Override
	public Goal[] createGoalSeries(RuneMap initialMap) {
		EnumMap<Type, Integer> map = new EnumMap<>(Type.class);
		for (Type type : Type.values())
			map.put(type, 0);
		for (int y = 0; y < initialMap.getHeight(); y++)
			for (int x = 0; x < initialMap.getWidth(); x++) {
				Type t = initialMap.getStone(x, y).getType();
				map.put(t, map.get(t) + 1);
			}
		int maxCombo = 0;
		EnumSet<Type> requirement = EnumSet.noneOf(Type.class);
		for (Entry<Type, Integer> e : map.entrySet()) {
			if (e.getKey() != Type.UNKNOWN) {
				int pc = getPossibleCombo(e.getValue());
				maxCombo += pc;
				if (e.getKey() != Type.HEART && pc >= 1
						&& e.getValue() >= pc * 3 + 2)
					requirement.add(e.getKey());
			}
		}
		int maxGroup = requirement.size();
		return new Goal[] {
				new GroupAttackGoal(0, 0, maxCombo, maxGroup, requirement),
				new GroupAttackGoal(maxCombo, maxGroup, requirement) };
	}

	@Override
	public String describeGoal(Goal g, Goal fg) {
		GroupAttackGoal goal = (GroupAttackGoal) g, finalGoal = (GroupAttackGoal) fg;
		String str;
		if (goal.getCombo() == finalGoal.getCombo())
			str = goal.getCombo() + " combo ";
		else
			str = goal.getCombo() + "/" + finalGoal.getCombo() + " combo ";
		if (goal.getGroup() == finalGoal.getGroup())
			str += goal.getGroup() + " G.A.";
		else
			str += goal.getGroup() + "/" + finalGoal.getGroup() + " G.A.";
		return str;
	}

	private static class GroupAttackGoal extends VectorGoal {

		private EnumSet<Type> requirement;

		public GroupAttackGoal(int goalCombo, int goalGroup, int maxGoalCombo,
				int maxGoalGroup, EnumSet<Type> requirement) {
			super(Arrays.asList(goalCombo, goalGroup), Arrays.asList(
					maxGoalCombo, maxGoalGroup));
			this.requirement = requirement;
		}

		public GroupAttackGoal(int maxGoalCombo, int maxGoalGroup,
				EnumSet<Type> requirement) {
			super(Arrays.asList(maxGoalCombo, maxGoalGroup));
			this.requirement = requirement;
		}

		public GroupAttackGoal(int makeCombo, int makeGroup,
				GroupAttackGoal previous) {
			super(Arrays.asList(makeCombo, makeGroup), previous);
			this.requirement = previous.requirement;
		}

		int getCombo() {
			List<Integer> maker = getMaker();
			return maker != null ? maker.get(0) : goalList.get(0).get(0);
		}

		int getGroup() {
			List<Integer> maker = getMaker();
			return maker != null ? maker.get(1) : goalList.get(0).get(1);
		}

		@Override
		public Result getResult(RuneMap runemap) {
			ComboCalculator.Describer cd = ComboCalculator
					.getDescriber(runemap);

			EnumSet<Type> types = EnumSet.noneOf(Type.class);
			for (ComboCalculator.Combo ccc : cd.getFullComboList()) {
				if (Long.bitCount(ccc.getMask()) >= 5) {
					types.add(ccc.getType());
				}
			}
			types.retainAll(requirement);

			int combo = cd.getFullComboCount();
			int group = types.size();
			int min = Integer.MAX_VALUE;

			for (List<Integer> goal : goalList) {
				if (combo >= goal.get(0) && group >= goal.get(1)) {
					return new Result(new GroupAttackGoal(combo, group, this));
				}
				min = Math.min(min,
						3 * (goal.get(0) - combo) + 6 * (goal.get(1) - group));
			}
			return new Result(min);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result
					+ ((requirement == null) ? 0 : requirement.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			GroupAttackGoal other = (GroupAttackGoal) obj;
			if (!requirement.equals(other.requirement))
				return false;
			return true;
		}
	}
}
