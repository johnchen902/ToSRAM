package tosram.algorithm;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import tosram.RuneMap;
import tosram.RuneStone;

/**
 * A utility computing maximum combo of a <code>RuneMap</code>
 * 
 * @author johnchen902
 */
public class MaximumComboCalculator {
	private MaximumComboCalculator() {
	}

	/**
	 * Get the maximum possible combo for the specified <code>RuneMap</code>
	 * 
	 * @param map
	 *            the specified <code>RuneMap</code>
	 * @return the maximum possible combo
	 */
	public static int getMaxCombo(RuneMap map) {
		return map.stream().filter(s -> s != RuneStone.UNKNOWN)
				.collect(groupingBy(identity(), counting())).values().stream()
				.mapToInt(MaximumComboCalculator::getMaxComboSingleElement)
				.sum();
	}

	private static int getMaxComboSingleElement(long count) {
		return getMaxComboSingleElement((int) count);
	}

	private static int getMaxComboSingleElement(int count) {
		if (count <= 20)
			return count / 3;
		switch (count) {
		case 21:
		case 22:
			return 5;
		case 23:
			return 4;
		case 24:
		case 25:
			return 3;
		case 26:
			return 2;
		case 27:
		case 28:
		case 29:
		case 30:
			return 1;
		default:
			return 0;
		}
	}
}
