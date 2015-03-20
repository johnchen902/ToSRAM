package tosram.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import tosram.MutableRuneMap;
import tosram.RuneMap;
import tosram.RuneStone;

/**
 * A button showing a <code>RuneStone</code>.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class RuneButton extends JButton {
	public RuneButton(RuneStone stone) {
		Objects.requireNonNull(stone);

		setText(stone.toString());
		setBackground(getBackgroundColor(stone));
		setForeground(getForegroundColor(stone));
		setHorizontalAlignment(CENTER);
		setMargin(new Insets(0, -5, 0, -5));
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
				RuneStone newStone = showRuneStoneDialog(btn);
				if (newStone == null || newStone == stone)
					return;
				MutableRuneMap mp = table.getRuneMap().toMutable();
				mp.setRuneStone(x, y, newStone);
				table.setRuneMap(new RuneMap(mp));
			});
			return btn;
		};
	}

	private static RuneStone showRuneStoneDialog(RuneButton btn) {
		JPanel msg = new JPanel(new GridLayout(3, 3, 1, 1));
		JOptionPane pane = new JOptionPane(msg, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, new Object[0]);
		JDialog dialog = pane.createDialog(btn, "Select a Runestone");

		RuneStone[] selection = new RuneStone[1];
		for (RuneStone s : RuneStone.values()) {
			RuneButton b = new RuneButton(s);
			b.addActionListener(e -> {
				selection[0] = s;
				dialog.setVisible(false);
			});
			char c = s.name().charAt(0);
			b.setMnemonic(c);
			InputMap im = b.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			im.put(KeyStroke.getKeyStroke("pressed " + c), "pressed");
			im.put(KeyStroke.getKeyStroke("released " + c), "released");
			msg.add(b);
		}
		msg.add(new Box.Filler(null, btn.getSize(), null), 6);

		dialog.pack();
		dialog.setLocationRelativeTo(btn);
		dialog.setVisible(true);
		dialog.dispose();
		return selection[0];
	}
}
