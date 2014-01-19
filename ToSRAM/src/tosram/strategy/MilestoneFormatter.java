package tosram.strategy;

import static java.text.MessageFormat.format;
import tosram.RuneStone;
import tosram.RuneStoneFormatter;

class MilestoneFormatter {

	static String formatGroupAttacks(int groupAttacks) {
		return format("{0} G.A. ", new Object[] { groupAttacks });
	}

	static String formatCombo(int combo) {
		return format("{0} combo{0,choice,1#|1<s} ", new Object[] { combo });
	}

	static String formatSteps(int steps) {
		return format("{0} step{0,choice,1#|1<s} ", new Object[] { steps });
	}

	static String formatStacking() {
		return "STACKING ";
	}

	static String formatNull() {
		return "!";
	}

	static String formatSixInCombo(boolean hasSix) {
		if (hasSix)
			return "with six ";
		else
			return "without six ";
	}

	static String formatStoneUsage(RuneStone.Type type, int amount) {
		return format("{1} {0}{1,choice,1#|1<s} ", new Object[] {
				RuneStoneFormatter.format(type), amount });
	}
}
