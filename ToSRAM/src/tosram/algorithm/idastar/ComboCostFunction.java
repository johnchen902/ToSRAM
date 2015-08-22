package tosram.algorithm.idastar;

import java.util.List;

import tosram.Direction;
import tosram.MutableRuneMap;
import tosram.algorithm.ComboCounter.Combo;

/**
 * The cost function considering numbers of combo. A diagonal move cost twice as
 * a straight move.
 * 
 * @author johnchen902
 */
public class ComboCostFunction implements CostFunction {

	private final int factor;
	private final int maxCombo;

	/**
	 * A cost function assuming each combo worth the specified numbers of moves.
	 * 
	 * @param costPerCombo
	 *            the number of moves each combo worths.
	 * @param expectedCombo
	 *            the number of combo expected
	 */
	public ComboCostFunction(int costPerCombo, int expectedCombo) {
		this.factor = costPerCombo;
		this.maxCombo = expectedCombo;
	}

	@Override
	public int costOfMove(Direction direction) {
		return direction.isDiagonal() ? 2 : 1;
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
