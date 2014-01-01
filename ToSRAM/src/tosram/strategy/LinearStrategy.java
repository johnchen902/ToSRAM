package tosram.strategy;

import java.util.Deque;

import tosram.ComboCalculator;
import tosram.Direction;
import tosram.RuneMap;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * Stop if <code>steps >= combo * increaseLimit + initialLimit</code>.<br>
 * Diagonal if <code>combo >= diagonalCombo</code> and (
 * <code>diagonalBeforeStop == IGNORE </code>or<code>
 * steps + diagonalBeforeStop >= combo * increaseLimit + initialLimit</code>)<br>
 * All solutions are treated as equal.
 * 
 * @author johnchen902
 */
public class LinearStrategy implements Strategy {

	/**
	 * Ignore <code>diagonalBeforeStop</code>.
	 */
	public static final int IGNORE = -1;

	private final int initialLimit;
	private final int increaseLimit;
	private final int diagonalCombo;
	private final int diagonalBeforeStop;

	private LinearStrategy(int ini, int inc, int com, int bef) {
		initialLimit = ini;
		increaseLimit = inc;
		diagonalCombo = com;
		diagonalBeforeStop = bef;
	}

	/**
	 * Create a strategy with <code>increaseLimit = 0</code> and
	 * <code>diagonalBeforeStop = IGNORE</code> and default
	 * <code>initialLimit</code> and <code>diagonalCombo</code>
	 * 
	 * @return such a strategy
	 */
	public static LinearStrategy createConstantStrategy() {
		return createConstantStrategy(15);
	}

	/**
	 * Create a strategy with <code>increaseLimit = 0</code> and
	 * <code>diagonalBeforeStop = IGNORE</code> and specified
	 * <code>initialLimit</code> and default <code>diagonalCombo</code>
	 * 
	 * @param ini
	 *            <code>initialLimit</code>
	 * @return such a strategy
	 */
	public static LinearStrategy createConstantStrategy(int ini) {
		return createConstantStrategy(ini, 5);
	}

	/**
	 * Create a strategy with <code>increaseLimit = 0</code> and
	 * <code>diagonalBeforeStop = IGNORE</code> and specified
	 * <code>initialLimit</code> and <code>diagonalCombo</code>
	 * 
	 * @param ini
	 *            <code>initialLimit</code>
	 * @param com
	 *            <code>diagonalCombo</code>
	 * @return such a strategy
	 */
	public static LinearStrategy createConstantStrategy(int ini, int com) {
		return new LinearStrategy(ini, 0, com, IGNORE);
	}

	/**
	 * Create a strategy with default <code>initialLimit</code>,
	 * <code>increaseLimit</code>, <code>diagonalCombo</code> and
	 * <code>diagonalBeforeStop</code>
	 * 
	 * @return such a strategy
	 */
	public static LinearStrategy createLinearStrategy() {
		return createLinearStrategy(3, 5);
	}

	/**
	 * Create a strategy with specified <code>initialLimit</code>,
	 * <code>increaseLimit</code> and default <code>diagonalCombo</code> and
	 * <code>diagonalBeforeStop</code>
	 * 
	 * @param ini
	 *            <code>initialLimit</code>
	 * @param inc
	 *            <code>increaseLimit</code>
	 * @return such a strategy
	 */
	public static LinearStrategy createLinearStrategy(int ini, int inc) {
		return createLinearStrategy(ini, inc, 6, 5);
	}

	/**
	 * Create a strategy with specified <code>initialLimit</code>,
	 * <code>increaseLimit</code>, <code>diagonalCombo</code> and
	 * <code>diagonalBeforeStop</code>
	 * 
	 * @param ini
	 *            <code>initialLimit</code>
	 * @param inc
	 *            <code>increaseLimit</code>
	 * @param com
	 *            <code>diagonalCombo</code>
	 * @param bef
	 *            <code>diagonalBeforeStop</code>
	 * @return such a strategy
	 */
	public static LinearStrategy createLinearStrategy(int ini, int inc,
			int com, int bef) {
		return new LinearStrategy(ini, inc, com, bef);
	}

	@Override
	public void reset(RuneMap initial) {
		// do nothing
	}

	private final ComboCalculator cc = new ComboCalculator();
	private int combo;
	private int steps;

	@Override
	public void submit(RuneMap map, int x, int y, Deque<Direction> stack) {
		cc.setMap(map);
		combo = cc.getCombo();
		steps = stack.size();
	}

	@Override
	public ComboCalculator getComboCalculator() {
		return cc;
	}

	@Override
	public boolean isToStop() {
		return steps >= combo * increaseLimit + initialLimit;
	}

	@Override
	public boolean isToDiagonal() {
		return combo >= diagonalCombo
				&& (diagonalBeforeStop == IGNORE || steps + diagonalBeforeStop >= combo
						* increaseLimit + initialLimit);
	}

	@Override
	public int compareSolution() {
		return 0;
	}

	@Override
	public String getMilestone() {
		return "!";
	}

	@Override
	public void solutionAccepted() {
		// do nothing
	}

}