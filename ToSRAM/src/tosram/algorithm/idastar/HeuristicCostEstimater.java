package tosram.algorithm.idastar;

import java.util.List;

import tosram.MutableRuneMap;
import tosram.RuneMap;
import tosram.algorithm.ComboCounter.Combo;

/**
 * The h-function provided to {@link IDAStarPathFinder} to implement IDA*
 * algorithm.
 * 
 * @author johnchen902
 */
public interface HeuristicCostEstimater {

	/**
	 * The caller is going to find a path for the specified map. It is designed
	 * to do preprocessing work here.
	 * 
	 * @param map
	 *            the initial map
	 */
	public void setInitialMap(RuneMap map);

	/**
	 * Estimate the cost required to move to the best possible map from the
	 * provided map. This map is guaranteed to be achievable from the map
	 * provided with the last {@link #setInitialMap(RuneMap)} call. As combo is
	 * likely a required information, it is evaluated and provided by caller.
	 * 
	 * @param map
	 *            the map to be estimated; should not be modified or stored for
	 *            future use
	 * @param combo
	 *            the combo information of the provided map
	 * @return the estimated cost
	 */
	public int estimateCost(MutableRuneMap map, List<Combo> combo);

	/**
	 * Get a user-friendly textual description of the provided map. This map is
	 * guaranteed to be the map provided with the last
	 * {@link #estimateCost(MutableRuneMap, List)} call. As combo is likely a
	 * required information, it is evaluated and provided by caller.
	 * 
	 * @param map
	 *            the map to be described; should not be modified or stored for
	 *            future use
	 * @param combo
	 *            the combo information of the provided map
	 * @return a user-friendly textual description
	 */
	public String describe(MutableRuneMap map, List<Combo> combo);
}
