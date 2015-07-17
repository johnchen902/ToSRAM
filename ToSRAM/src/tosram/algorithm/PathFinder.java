package tosram.algorithm;

import java.util.function.BiConsumer;

import tosram.Path;
import tosram.RuneMap;

/**
 * An interface that find a good path (sequence of moves) of a
 * <code>RuneMap</code>.
 * 
 * @author johnchen902
 */
public interface PathFinder {

	/**
	 * Find a good path of a <code>RuneMap</code>.
	 * 
	 * @param initialMap
	 *            the starting <code>RuneMap</code>.
	 * @param callBack
	 *            a callback that would be invoked when some result is found;
	 *            the <code>Path</code> is the result found and the
	 *            <code>String</code> is the textual description of the result
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
