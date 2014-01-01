package tosram.view.strategy;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import tosram.strategy.StrategySearchPathRobot.Strategy;
import tosram.strategy.WeatheringStrategy;

/**
 * A chooser that creates <code>WeatheringStrategy</code>.
 * 
 * @author johnchen902
 */
public class WeatheringChooser extends DefaultStrategyCreater {

	private static final int WIDTH = 6;
	private static final int HEIGHT = 5;
	private final boolean[] mask = new boolean[WIDTH * HEIGHT];

	/**
	 * Constructor.
	 */
	public WeatheringChooser() {
		super("Weathering");
	}

	@Override
	public Strategy createStrategy(Strategy next) {
		return new WeatheringStrategy(next, mask);
	}

	@Override
	public void settings(Component parent) {
		JCheckBox[] cbxes = new JCheckBox[WIDTH * HEIGHT];
		JPanel pn = new JPanel(new GridLayout(HEIGHT, WIDTH));
		for (int y = 0; y < HEIGHT; y++)
			for (int x = 0; x < WIDTH; x++)
				pn.add(cbxes[y * WIDTH + x] = new JCheckBox("", mask[y * WIDTH
						+ x]));

		int result = JOptionPane.showConfirmDialog(parent, pn,
				"Select weathering stones", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION)
			for (int y = 0; y < HEIGHT; y++)
				for (int x = 0; x < WIDTH; x++)
					mask[y * WIDTH + x] = cbxes[y * WIDTH + x].isSelected();
	}

}
