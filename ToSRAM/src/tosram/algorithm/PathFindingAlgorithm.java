package tosram.algorithm;

import tosram.Path;
import tosram.RuneMap;

/**
 * An algorithm to find a good path out of a <code>RuneMap</code>.
 * 
 * @author johnchen902
 */
public interface PathFindingAlgorithm {

	/**
	 * Find a good path out of a <code>RuneMap</code>.
	 * 
	 * @param initialMap
	 *            the starting <code>RuneMap</code>.
	 * @return the path the algorithm found
	 */
	public Path findPath(RuneMap initialMap);
}
