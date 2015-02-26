package tosram.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import tosram.MutableRuneMap;
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
		Border border = getBorder();
		if (border instanceof CompoundBorder)
			setBorder(((CompoundBorder) border).getOutsideBorder());
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

	public static RuneMapTable.Renderer factory(RuneMapTable table) {
		return (stone, x, y) -> {
			RuneButton btn = new RuneButton(stone);
			btn.addActionListener(e -> {
				MutableRuneMap mp = table.getRuneMap().toMutable();
				switch (mp.getRuneStone(x, y)) {
				case FIRE:
					mp.setRuneStone(x, y, RuneStone.EARTH);
					break;
				case EARTH:
					mp.setRuneStone(x, y, RuneStone.WATER);
					break;
				case WATER:
					mp.setRuneStone(x, y, RuneStone.LIGHT);
					break;
				case LIGHT:
					mp.setRuneStone(x, y, RuneStone.DARK);
					break;
				case DARK:
					mp.setRuneStone(x, y, RuneStone.HEART);
					break;
				case HEART:
					mp.setRuneStone(x, y, RuneStone.UNKNOWN);
					break;
				case UNKNOWN:
					mp.setRuneStone(x, y, RuneStone.FIRE);
					break;
				}
				table.setRuneMap(new RuneMap(mp));
			});
			return btn;
		};
	}
}
