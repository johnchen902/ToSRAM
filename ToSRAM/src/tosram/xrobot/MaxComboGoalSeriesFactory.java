package tosram.xrobot;

import java.util.EnumMap;
import java.util.Map.Entry;

import tosram.ComboCalculator;
import tosram.RuneMap;
import tosram.RuneStone.Type;

/**
 * A factory that produce a series of goal leading to the maximum combo.
 * 
 * @author johnchen902
 */
public class MaxComboGoalSeriesFactory implements GoalSeriesFactory {

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
		for (Entry<Type, Integer> e : map.entrySet()) {
			if (e.getKey() != Type.UNKNOWN) {
				maxCombo += getPossibleCombo(e.getValue());
			}
		}
		return new Goal[] { new MaxComboGoal(0), new MaxComboGoal(maxCombo) };
	}

	@Override
	public String describeGoal(Goal g, Goal fg) {
		MaxComboGoal goal = (MaxComboGoal) g, finalGoal = (MaxComboGoal) fg;
		if (goal.getCombo() == finalGoal.getCombo())
			return goal.getCombo() + " combo";
		else
			return goal.getCombo() + "/" + finalGoal.getCombo() + " combo";
	}

	private static class MaxComboGoal implements Goal {

		private final int goalCombo;

		public MaxComboGoal(int goalCombo) {
			this.goalCombo = goalCombo;
		}

		public int getCombo() {
			return goalCombo;
		}

		@Override
		public Result getResult(RuneMap runemap) {
			int combo = ComboCalculator.getDescriber(runemap)
					.getFullComboCount();
			if (combo < goalCombo)
				return new Result(3 * (goalCombo - combo));
			else
				return new Result(new MaxComboGoal(goalCombo + 1));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + goalCombo;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof MaxComboGoal))
				return false;
			MaxComboGoal other = (MaxComboGoal) obj;
			if (goalCombo != other.goalCombo)
				return false;
			return true;
		}
	}
}
