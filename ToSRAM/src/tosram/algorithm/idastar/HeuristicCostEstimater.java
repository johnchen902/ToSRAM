package tosram.algorithm.idastar;

import java.util.List;

import tosram.MutableRuneMap;
import tosram.RuneMap;
import tosram.algorithm.ComboCountingAlgorithm.Combo;

/**
 * The h-function used by IDA* algorithm.
 * 
 * @author johnchen902
 */
public interface HeuristicCostEstimater {

	/**
	 * Set the source state.
	 * 
	 * @param map
	 *            the source state
	 */
	public void setSourceState(RuneMap map);

	/**
	 * Estimate heuristic cost to the destination state.
	 * 
	 * @param map
	 *            the current state; should not be modified
	 * @param combo
	 *            the combo already evaluated
	 * @return Estimated heuristic cost
	 */
	public int estimateHeuristicCost(MutableRuneMap map, List<Combo> combo);

	/**
	 * Get the textual judgment of the destination state.
	 * 
	 * @param map
	 *            the current state; should not be modified
	 * @param combo
	 *            the combo already evaluated
	 * @return the textual judgment
	 */
	public String describe(MutableRuneMap map, List<Combo> combo);
}
