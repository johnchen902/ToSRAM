package tosram.xrobot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO document
abstract class VectorGoal implements Goal {
	protected final List<Integer> maximum;
	protected final List<List<Integer>> goalList;
	private List<Integer> maker;

	public VectorGoal(List<List<Integer>> goalList, List<Integer> maximum,
			Void sorryIHaveToAddAParameterToDistinguish) {
		this.maximum = maximum;
		this.goalList = goalList;
		this.maker = null;
	}

	public VectorGoal(List<Integer> initial, List<Integer> maximum) {
		this(Arrays.asList(initial), maximum, null);
	}

	public VectorGoal(List<Integer> maximum) {
		this(maximum, maximum);
	}

	public VectorGoal(List<Integer> maker, VectorGoal from) {
		this(new ArrayList<List<Integer>>(maker.size()), from.maximum, null);
		for (int i = 0; i < maker.size(); i++) {
			if (maker.get(i) < maximum.get(i)) {
				List<Integer> p = new ArrayList<Integer>(maker);
				p.set(i, maker.get(i) + 1);
				goalList.add(p);
			}
		}
		from.maker = maker;
	}

	protected List<Integer> getMaker() {
		return maker;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maximum.hashCode();
		result = prime * result + goalList.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VectorGoal other = (VectorGoal) obj;
		if (!maximum.equals(other.maximum))
			return false;
		if (!goalList.equals(other.goalList))
			return false;
		return true;
	}
}
