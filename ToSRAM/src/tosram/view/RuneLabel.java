package tosram.view;

import java.awt.Color;

import javax.swing.JLabel;

import tosram.RuneStone;

// TODO document
@SuppressWarnings("serial")
public class RuneLabel extends JLabel {
	public RuneLabel(RuneStone stone) {
		if (stone == null)
			return;
		setText(stone.toString());
		setOpaque(true);
		setBackground(getBackgroundColor(stone));
		setForeground(getForegroundColor(stone));
		setHorizontalAlignment(CENTER);
	}

	private Color getBackgroundColor(RuneStone stone) {
		switch (stone) {
		case FIRE:
			return Color.RED;
		case EARTH:
			return Color.GREEN;
		case WATER:
			return Color.BLUE;
		case LIGHT:
			return Color.YELLOW;
		case DARK:
			return Color.MAGENTA;
		case HEART:
			return Color.PINK;
		case UNKNOWN:
		default:
			return Color.WHITE;
		}
	}

	private Color getForegroundColor(RuneStone stone) {
		return Color.BLACK;
	}

	public static RuneMapTable.Renderer factory() {
		return (stone, x, y) -> new RuneLabel(stone);
	}
}
