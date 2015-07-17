package tosram.algorithm.idastar;

import java.util.List;

import tosram.MutableRuneMap;
import tosram.RuneMap;
import tosram.algorithm.ComboCounter.Combo;
import tosram.algorithm.MaxComboCalculator;

/**
 * The h-function considering numbers of combo.
 * 
 * @author johnchen902
 */
public class ComboHeuristicCostEstimater implements HeuristicCostEstimater {

	private final int factor;
	private int maxCombo;

	/**
	 * A h-function assuming each combo worth 3 moves.
	 */
	public ComboHeuristicCostEstimater() {
		this(3);
	}

	/**
	 * A h-function assuming each combo worth the specified numbers of moves.
	 * 
	 * @param costPerCombo
	 *            the number of moves each combo worths.
	 */
	public ComboHeuristicCostEstimater(int costPerCombo) {
		this.factor = costPerCombo;
	}

	@Override
	public void setInitialMap(RuneMap map) {
		maxCombo = MaxComboCalculator.getMaxCombo(map);
	}

	@Override
	public int estimateCost(MutableRuneMap map, List<Combo> combo) {
		return Math.max(0, factor * (maxCombo - combo.size()));
	}

	@Override
	public String describe(MutableRuneMap map, List<Combo> combo) {
		return String.format("%d/%d Combo", combo.size(), maxCombo);
	}
}
