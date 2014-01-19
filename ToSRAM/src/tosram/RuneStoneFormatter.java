package tosram;

import java.text.MessageFormat;

public class RuneStoneFormatter {
	public static String format(RuneStone stone) {
		return MessageFormat.format("{1,choice,0#|1#Strong }{0}", new Object[] {
				stone.getType(), stone.isStronger() ? 1 : 0 });
	}

	public static String format(RuneStone.Type type) {
		switch (type) {
		case FIRE:
			return "Fire";
		case WATER:
			return "Water";
		case GREEN:
			return "Green";
		case LIGHT:
			return "Light";
		case DARK:
			return "Dark";
		case HEART:
			return "Heart";
		case UNKNOWN:
			return "Unknown";
		default:
			throw new Error(type.toString()); // unreachable
		}
	}
}
