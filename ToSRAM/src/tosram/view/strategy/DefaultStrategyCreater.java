package tosram.view.strategy;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 * A default <code>StrategyCreater</code> that shows some text as rendering and
 * do no settings.
 * 
 * @author johnchen902
 */
public abstract class DefaultStrategyCreater implements StrategyCreater {

	private DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

	protected String getName() {
		String name = getClass().getSimpleName();
		return name.replace("Creater", "").replaceAll("(?<=.)([A-Z])", " $1");
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends StrategyCreater> list, StrategyCreater value,
			int index, boolean isSelected, boolean cellHasFocus) {
		return createRendererByText(list, getName(), index, isSelected,
				cellHasFocus);
	}

	protected Component createRendererByText(
			JList<? extends StrategyCreater> list, String text, int index,
			boolean isSelected, boolean cellHasFocus) {
		return dlcr.getListCellRendererComponent(list, text, index, isSelected,
				cellHasFocus);
	}

	@Override
	public void settings(Component parent) {
		JOptionPane.showMessageDialog(parent, "No settings available.",
				getName(), JOptionPane.INFORMATION_MESSAGE);
	}
}
