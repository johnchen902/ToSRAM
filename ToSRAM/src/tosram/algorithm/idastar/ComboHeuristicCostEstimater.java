package tosram.algorithm.idastar;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;

import tosram.MutableRuneMap;
import tosram.RuneMap;
import tosram.RuneStone;
import tosram.algorithm.ComboCountingAlgorithm.Combo;

/**
 * The h-function considering numbers of combo.
 * 
 * @author johnchen902
 */
public class ComboHeuristicCostEstimater implements HeuristicCostEstimater {

	private final int factor;
	private int maxCombo;

	public ComboHeuristicCostEstimater() {
		this(3);
	}

	public ComboHeuristicCostEstimater(int factor) {
		this.factor = factor;
	}

	@Override
	public void setSourceState(RuneMap map) {
		maxCombo = getMaxCombo(map);
	}

	@Override
	public int estimateHeuristicCost(MutableRuneMap map, List<Combo> combo) {
		return Math.max(0, factor * (maxCombo - combo.size()));
	}

	@Override
	public String describe(MutableRuneMap map, List<Combo> combo) {
		return String.format("%d/%d Combo", combo.size(), maxCombo);
	}

	private static int getMaxCombo(RuneMap map) {
		return map
				.stream()
				.filter(s -> s != null && s != RuneStone.UNKNOWN)
				.collect(groupingBy(identity(), counting()))
				.values()
				.stream()
				.mapToInt(ComboHeuristicCostEstimater::getMaxComboSingleElement)
				.sum();
	}

	private static int getMaxComboSingleElement(long count) {
		return getMaxComboSingleElement((int) count);
	}

	private static int getMaxComboSingleElement(int count) {
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
}
