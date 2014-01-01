package tosram.view.strategy;

import slider.RangeSlider;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import tosram.RuneStone.Type;
import tosram.strategy.UseStoneStrategy;
import tosram.strategy.StrategySearchPathRobot.Strategy;

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
		super("Restrict Stone Usage");
		this.type = this.selectingType = type;
	}

	@Override
	public Strategy createStrategy(Strategy next) {
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
		JPanel pnUp = new JPanel();
		ButtonGroup group = new ButtonGroup();

		pnUp.add(createRadioButtonForType(group, Type.FIRE));
		pnUp.add(createRadioButtonForType(group, Type.WATER));
		pnUp.add(createRadioButtonForType(group, Type.GREEN));
		pnUp.add(createRadioButtonForType(group, Type.LIGHT));
		pnUp.add(createRadioButtonForType(group, Type.DARK));
		pnUp.add(createRadioButtonForType(group, Type.HEART));

		JPanel pnDown = new JPanel();

		pnDown.add(new JLabel("Stone Used:"));

		RangeSlider rangeSlider = new RangeSlider();
		pnDown.add(rangeSlider);
		rangeSlider.setMinimum(0);
		rangeSlider.setMaximum(10);
		rangeSlider.setValue(lowerBound);
		rangeSlider.setUpperValue(upperBound);
		rangeSlider.setMajorTickSpacing(1);
		rangeSlider.setPaintTicks(true);
		rangeSlider.setPaintLabels(true);

		JPanel pn = new JPanel(new GridLayout(2, 1));
		pn.add(pnUp);
		pn.add(pnDown);

		int result = JOptionPane.showConfirmDialog(parent, pn,
				"Select the type", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			type = selectingType;
			lowerBound = rangeSlider.getValue();
			upperBound = rangeSlider.getUpperValue();
		} else {
			selectingType = type;
		}
	}

	private JRadioButton createRadioButtonForType(ButtonGroup group, Type btType) {
		JRadioButton jrb = new JRadioButton(btType.toString(), btType == type);
		jrb.addItemListener(new SelectItemListener(btType));
		group.add(jrb);
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
