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
	private String text;

	/**
	 * Create a <code>StrategyCreater</code> with the specified text.
	 * 
	 * @param text
	 *            the text shown
	 */
	public DefaultStrategyCreater(String text) {
		this.text = text;
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends StrategyCreater> list, StrategyCreater value,
			int index, boolean isSelected, boolean cellHasFocus) {
		return dlcr.getListCellRendererComponent(list, text, index, isSelected,
				cellHasFocus);
	}

	@Override
	public void settings(Component parent) {
		JOptionPane.showMessageDialog(parent, "No settings available.", text,
				JOptionPane.INFORMATION_MESSAGE);
	}

}
