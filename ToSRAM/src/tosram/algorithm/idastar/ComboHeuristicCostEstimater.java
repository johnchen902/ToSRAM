package tosram.algorithm.idastar;

import java.util.List;

import tosram.MutableRuneMap;
import tosram.RuneMap;
import tosram.algorithm.ComboCountingAlgorithm.Combo;
import tosram.algorithm.MaximumComboCalculator;

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
		maxCombo = MaximumComboCalculator.getMaxCombo(map);
	}

	@Override
	public int estimateHeuristicCost(MutableRuneMap map, List<Combo> combo) {
		return Math.max(0, factor * (maxCombo - combo.size()));
	}

	@Override
	public String describe(MutableRuneMap map, List<Combo> combo) {
		return String.format("%d/%d Combo", combo.size(), maxCombo);
	}
}
