package tosram.view.strategy;

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import tosram.strategy.KComboStrategy;
import tosram.strategy.SolutionStrategy;
import tosram.view.MnemonicsDispatcher;

/**
 * A <code>StrategyCreater</code> who creates {@link KComboStrategy}
 * 
 * @author johnchen902
 */
public class KComboCreater extends DefaultStrategyCreater {

	private int combo = 4;

	@Override
	protected String getName() {
		return MessageFormat.format("{0, number, integer} Combo",
				new Object[] { combo });
	}

	@Override
	public SolutionStrategy createStrategy(SolutionStrategy next) {
		return new KComboStrategy(next, combo);
	}

	@Override
	public void settings(Component parent) {
		JPanel pn = new JPanel();

		JLabel label = new JLabel("Combo:");
		pn.add(label);
		MnemonicsDispatcher.registerComponent(label);

		JSlider slider = new JSlider(0, 10);
		pn.add(slider);
		label.setLabelFor(slider);
		slider.setValue(combo);
		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		int result = JOptionPane.showConfirmDialog(parent, pn,
				"A Number of Combo", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION)
			combo = slider.getValue();
	}
}
