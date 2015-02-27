package tosram.algorithm;

import java.util.Objects;
import java.util.function.BiConsumer;

import tosram.Path;
import tosram.RuneMap;

/**
 * This class provides a skeletal implementation of the
 * {@link PathFindingAlgorithm} interface to minimize the effort required to
 * implement this interface.
 * 
 * @author johnchen902
 */
public abstract class AbstractPathFindingAlgorithm implements
		PathFindingAlgorithm {

	/**
	 * The {@link ComboCountingAlgorithm} to use.
	 */
	protected final ComboCountingAlgorithm comboCounter;

	/**
	 * The {@link PathConstrain} to use.
	 */
	protected final PathConstrain constrain;
	private BiConsumer<Path, String> callBack;
	private volatile boolean isRunning, shouldRun;

	/**
	 * Constructor. Set the {@link ComboCountingAlgorithm} and
	 * {@link PathConstrain} to use.
	 * 
	 * @param comboCounter
	 *            the {@link ComboCountingAlgorithm} to use.
	 * @param constrain
	 *            the {@link PathConstrain} to use.
	 */
	public AbstractPathFindingAlgorithm(ComboCountingAlgorithm comboCounter,
			PathConstrain constrain) {
		this.comboCounter = Objects.requireNonNull(comboCounter);
		this.constrain = Objects.requireNonNull(constrain);
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
}
