package tosram.view.strategy;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import slider.RangeSlider;
import tosram.RuneStone.Type;
import tosram.RuneStoneFormatter;
import tosram.strategy.SolutionStrategy;
import tosram.strategy.UseStoneStrategy;
import tosram.view.MnemonicsDispatcher;

/**
 * A <code>StrategyCreater</code> who creates {@link UseStoneStrategy}
 * 
 * @author johnchen902
 */
public class UseStoneCreater extends DefaultStrategyCreater {

	private Type type;
	private Type selectingType;
	private int lowerBound = 3, upperBound = 3;

	/**
	 * Constructor with heart as initial type.
	 */
	public UseStoneCreater() {
		this(Type.HEART);
	}

	/**
	 * Constructor with a specified initial type.
	 * 
	 * @param type
	 *            the Type
	 */
	public UseStoneCreater(Type type) {
		this.type = this.selectingType = type;
	}

	@Override
	protected String getName() {
		if (lowerBound == upperBound) {
			String pattern = "Use {0, number, integer} {1}{0, choice, 1#|1<s}";
			Object[] object = { upperBound, RuneStoneFormatter.format(type) };
			return MessageFormat.format(pattern, object);
		} else {
			String pattern = "Use {0, number, integer} ~ {1, number, integer} {2}{1, choice, 1#|1<s}";
			Object[] object = { lowerBound, upperBound,
					RuneStoneFormatter.format(type) };
			return MessageFormat.format(pattern, object);
		}
	}

	@Override
	public SolutionStrategy createStrategy(SolutionStrategy next) {
		return new UseStoneStrategy(next, type, lowerBound, upperBound);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends StrategyCreater> list, StrategyCreater value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component comp = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
		switch (type) {
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
		return comp;
	}

	@Override
	public void settings(Component parent) {
		JPanel pnSettings = new JPanel(new GridLayout(2, 1));

		JPanel pnUp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnSettings.add(pnUp);
		pnUp.add(new JLabel("Type:"));

		JPanel pnTypes = new JPanel(new GridLayout(2, 3));
		pnUp.add(pnTypes);
		ButtonGroup group = new ButtonGroup();

		pnTypes.add(createRadioButtonForType(group, Type.FIRE));
		pnTypes.add(createRadioButtonForType(group, Type.WATER));
		pnTypes.add(createRadioButtonForType(group, Type.GREEN));
		pnTypes.add(createRadioButtonForType(group, Type.LIGHT));
		pnTypes.add(createRadioButtonForType(group, Type.DARK));
		pnTypes.add(createRadioButtonForType(group, Type.HEART));

		JPanel pnDown = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnSettings.add(pnDown);

		JLabel lb = new JLabel("Number:");
		pnDown.add(lb);
		MnemonicsDispatcher.registerComponent(lb);

		RangeSlider rangeSlider = new RangeSlider();
		pnDown.add(rangeSlider);
		lb.setLabelFor(rangeSlider);
		rangeSlider.setMinimum(0);
		rangeSlider.setMaximum(10);
		rangeSlider.setValue(lowerBound);
		rangeSlider.setUpperValue(upperBound);
		rangeSlider.setMajorTickSpacing(1);
		rangeSlider.setPaintTicks(true);
		rangeSlider.setPaintLabels(true);

		int result = JOptionPane.showConfirmDialog(parent, pnSettings,
				"Use a Number of Runestones", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			type = selectingType;
			lowerBound = rangeSlider.getValue();
			upperBound = rangeSlider.getUpperValue();
		} else {
			selectingType = type;
		}
	}

	private JRadioButton createRadioButtonForType(ButtonGroup group, Type btType) {
		JRadioButton jrb = new JRadioButton(RuneStoneFormatter.format(btType),
				btType == type);
		jrb.addItemListener(new SelectItemListener(btType));
		group.add(jrb);
		MnemonicsDispatcher.registerComponent(jrb);
		return jrb;
	}

	private class SelectItemListener implements ItemListener {

		private final Type itemType;

		public SelectItemListener(Type itemType) {
			this.itemType = itemType;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				selectingType = itemType;
		}
	}
}
