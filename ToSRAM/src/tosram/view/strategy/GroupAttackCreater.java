package tosram.view.strategy;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EnumSet;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import tosram.RuneStone.Type;
import tosram.strategy.GroupAttackStrategy;
import tosram.strategy.SolutionStrategy;

/**
 * A <code>StrategyCreater</code> who creates {@link GroupAttackStrategy}
 * 
 * @author johnchen902
 */
public class GroupAttackCreater extends DefaultStrategyCreater {

	private EnumSet<Type> set = EnumSet.complementOf(EnumSet.of(Type.HEART,
			Type.UNKNOWN));

	/**
	 * Constructor.
	 */
	public GroupAttackCreater() {
		super("Group Attack");
	}

	@Override
	public SolutionStrategy createStrategy(SolutionStrategy next) {
		return new GroupAttackStrategy(next, set);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends StrategyCreater> list, StrategyCreater value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component comp = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
		if (set.size() == 1) {
			switch (set.iterator().next()) {
			case FIRE:
				comp.setBackground(Color.RED);
				break;
			case WATER:
				comp.setBackground(Color.BLUE);
				break;
			case GREEN:
				comp.setBackground(Color.GREEN);
				break;
			case LIGHT:
				comp.setBackground(Color.YELLOW);
				break;
			case DARK:
				comp.setBackground(Color.MAGENTA);
				break;
			case HEART:
				comp.setBackground(Color.PINK);
				break;
			case UNKNOWN:
			default:
				// unexpected
				break;
			}
		}
		return comp;
	}

	@Override
	public void settings(Component parent) {
		JPanel pn = new JPanel();
		EnumSet<Type> set = EnumSet.copyOf(this.set);
		pn.add(createCheckBoxForType(set, Type.FIRE));
		pn.add(createCheckBoxForType(set, Type.WATER));
		pn.add(createCheckBoxForType(set, Type.GREEN));
		pn.add(createCheckBoxForType(set, Type.LIGHT));
		pn.add(createCheckBoxForType(set, Type.DARK));
		pn.add(createCheckBoxForType(set, Type.HEART));
		int result = JOptionPane.showConfirmDialog(parent, pn, "Select types",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			if (set.size() == 0) {
				JOptionPane.showMessageDialog(parent,
						"At least one type must be selected", "Group Attack",
						JOptionPane.ERROR_MESSAGE);
			} else {
				this.set = EnumSet.copyOf(set);
			}
		}
	}

	private static JCheckBox createCheckBoxForType(EnumSet<Type> set, Type type) {
		JCheckBox cbx = new JCheckBox(type.toString(), set.contains(type));
		cbx.addItemListener(new ToggleItemListener(set, type));
		return cbx;
	}

	private static class ToggleItemListener implements ItemListener {

		private final EnumSet<Type> set;
		private final Type type;

		public ToggleItemListener(EnumSet<Type> set, Type type) {
			this.set = set;
			this.type = type;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				set.add(type);
			else
				set.remove(type);
		}
	}
}
