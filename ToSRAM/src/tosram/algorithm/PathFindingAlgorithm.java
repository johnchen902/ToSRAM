package tosram.algorithm;

import java.util.function.BiConsumer;

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
	 * @param callBack
	 *            a callback that would be invoked when finding a good result;
	 *            the <code>Path</code> is the result found and the
	 *            <code>String</code> is the description of the result
	 * @see #stop()
	 */
	public void findPath(RuneMap initialMap, BiConsumer<Path, String> callBack);

	/**
	 * Stop <code>findPath</code>.
	 * 
	 * @see #findPath(RuneMap, BiConsumer)
	 */
	public void stop();
}
