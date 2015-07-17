package tosram.algorithm;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.Path;
import tosram.RuneMap;
import tosram.algorithm.ComboCounter.Combo;

/**
 * This class provides a skeletal implementation of the {@link PathFinder}
 * interface to minimize the effort required to implement this interface.
 * 
 * @author johnchen902
 */
public abstract class AbstractPathFinder implements PathFinder {

	private ComboCounter comboCounter;
	private PathRestriction pathRestriction;
	private BiConsumer<Path, String> callBack;
	private volatile boolean isRunning, shouldRun;

	/**
	 * Constructor specifying the {@link ComboCounter} and
	 * {@link PathRestriction} to use.
	 * 
	 * @param comboCounter
	 *            the {@link ComboCounter} to use.
	 * @param pathRestriction
	 *            the {@link PathRestriction} to use.
	 */
	public AbstractPathFinder(ComboCounter comboCounter,
			PathRestriction pathRestriction) {
		this.comboCounter = Objects.requireNonNull(comboCounter);
		this.pathRestriction = Objects.requireNonNull(pathRestriction);
	}

	@Override
	public void findPath(RuneMap initialMap, BiConsumer<Path, String> callBack) {
		Objects.requireNonNull(initialMap);
		Objects.requireNonNull(callBack);
		if (isRunning)
			throw new IllegalStateException("Already running");
		this.callBack = callBack;
		try {
			isRunning = shouldRun = true;
			findPath(initialMap);
		} finally {
			isRunning = false;
		}
	}

	@Override
	public void stop() {
		if (isRunning)
			shouldRun = false;
	}

	/**
	 * Find a good path out of a <code>RuneMap</code>.
	 * 
	 * @param initialMap
	 *            the starting <code>RuneMap</code>.
	 */
	protected abstract void findPath(RuneMap initialMap);

	/**
	 * Call this when finding a good result.
	 * 
	 * @param p
	 *            the result found
	 * @param s
	 *            the description of the result
	 */
	protected void result(Path p, String s) {
		callBack.accept(Objects.requireNonNull(p), Objects.requireNonNull(s));
	}

	/**
	 * Determines whether this algorithm has been stopped by user.
	 * 
	 * @return <code>true</code> if has been stopped by user; <code>false</code>
	 *         otherwise
	 */
	protected boolean isStopped() {
		return !shouldRun;
	}

	/**
	 * Delegate {@link ComboCounter}. Please see the "See Also" for usage.
	 * 
	 * @see tosram.algorithm.ComboCounter#countCombo(tosram.MutableRuneMap)
	 */
	protected final List<Combo> countCombo(MutableRuneMap map) {
		return comboCounter.countCombo(map);
	}

	/**
	 * Delegate {@link PathRestriction}. Please see the "See Also" for usage.
	 * 
	 * @see tosram.algorithm.PathRestriction#canStart(int, int,
	 *      tosram.MutableRuneMap)
	 */
	protected final boolean canStart(int stX, int stY, MutableRuneMap map) {
		return pathRestriction.canStart(stX, stY, map);
	}

	/**
	 * Delegate {@link PathRestriction}. Please see the "See Also" for usage.
	 * 
	 * @see tosram.algorithm.PathRestriction#canMove(int, int, java.util.List,
	 *      tosram.Direction, int, int, tosram.MutableRuneMap)
	 */
	protected final boolean canMove(int stX, int stY, List<Direction> dirs,
			Direction dir, int rsX, int rsY, MutableRuneMap map) {
		return pathRestriction.canMove(stX, stY, dirs, dir, rsX, rsY, map);
	}
}
