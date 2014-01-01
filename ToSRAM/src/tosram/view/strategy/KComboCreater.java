package tosram.view.strategy;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JSlider;

import tosram.strategy.KComboStrategy;
import tosram.strategy.StrategySearchPathRobot.Strategy;

/**
 * A <code>StrategyCreater</code> who creates {@link KComboStrategy}
 * 
 * @author johnchen902
 */
public class KComboCreater extends DefaultStrategyCreater {

	private int combo = 4;

	/**
	 * Constructor
	 */
	public KComboCreater() {
		super("K Combo");
	}

	public Strategy createStrategy(Strategy next) {
		return new KComboStrategy(next, combo);
	}

	@Override
	public void settings(Component parent) {
		JSlider slider = new JSlider(0, 10);
		slider.setValue(combo);
		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		int result = JOptionPane.showConfirmDialog(parent, slider,
				"How many combo?", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION)
			combo = slider.getValue();
	}
}
