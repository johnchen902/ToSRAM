package tosram.view.strategy;

import java.awt.Color;
import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JList;
import javax.swing.JOptionPane;

import tosram.strategy.SixInComboStrategy;
import tosram.strategy.SolutionStrategy;

/**
 * A <code>StrategyCreater</code> who creates {@link SixInComboStrategy}
 * 
 * @author johnchen902
 */
public class SixInComboCreater extends DefaultStrategyCreater {

	private boolean heartAllowed;

	@Override
	protected String getName() {
		return "Six In a Combo";
	}

	@Override
	public SolutionStrategy createStrategy(SolutionStrategy next) {
		return new SixInComboStrategy(next, heartAllowed);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends StrategyCreater> list, StrategyCreater value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component comp = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
		if (heartAllowed)
			comp.setBackground(Color.PINK);
		return comp;
	}

	@Override
	public void settings(Component parent) {
		final String pattern = "Is heart stone counted in? Currently {0, choice, 0#No|1#Yes}.";

		int result = JOptionPane.showConfirmDialog(parent, MessageFormat
				.format(pattern, new Object[] { heartAllowed ? 1 : 0 }),
				getName(), JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == JOptionPane.YES_OPTION)
			heartAllowed = true;
		else if (result == JOptionPane.NO_OPTION)
			heartAllowed = false;
	}

}
