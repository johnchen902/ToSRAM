package tosram.view;

import java.awt.Color;

import javax.swing.JButton;

import tosram.RuneMap;
import tosram.RuneStone;

// TODO document
@SuppressWarnings("serial")
public class RuneButton extends JButton {
	public RuneButton(RuneStone stone) {
		if (stone == null)
			return;
		setText(stone.toString());
		setOpaque(true);
		setBackground(getBackgroundColor(stone));
		setForeground(getForegroundColor(stone));
		setHorizontalAlignment(CENTER);
	}

	private Color getBackgroundColor(RuneStone stone) {
		switch (stone.getType()) {
		case FIRE:
			return Color.RED;
		case WATER:
			return Color.BLUE;
		case GREEN:
			return Color.GREEN;
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
		if (stone.isStronger() && stone.getType() != RuneStone.Type.UNKNOWN)
			return Color.WHITE;
		else
			return Color.BLACK;
	}

	public static RuneMapTable.Renderer factory(RuneMapTable table) {
		return (stone, x, y) -> {
			RuneButton btn = new RuneButton(stone);
			btn.addActionListener(e -> {
				RuneMap mp = table.getRuneMap();
				switch (mp.getStone(x, y).getType()) {
				case FIRE:
					mp.setRuneStone(x, y, new RuneStone(RuneStone.Type.GREEN));
					break;
				case GREEN:
					mp.setRuneStone(x, y, new RuneStone(RuneStone.Type.WATER));
					break;
				case WATER:
					mp.setRuneStone(x, y, new RuneStone(RuneStone.Type.LIGHT));
					break;
				case LIGHT:
					mp.setRuneStone(x, y, new RuneStone(RuneStone.Type.DARK));
					break;
				case DARK:
					mp.setRuneStone(x, y, new RuneStone(RuneStone.Type.HEART));
					break;
				case HEART:
				case UNKNOWN:
				default:
					mp.setRuneStone(x, y, new RuneStone(RuneStone.Type.FIRE));
					break;
				}
				table.setRuneMap(mp);
			});
			return btn;
		};
	}
}
