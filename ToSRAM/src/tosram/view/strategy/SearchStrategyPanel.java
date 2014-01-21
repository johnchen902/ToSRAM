package tosram.view.strategy;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.EventHandler;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import tosram.strategy.ImprovementStrategy;
import tosram.strategy.LinearStrategy;
import tosram.strategy.SearchStrategy;
import tosram.strategy.StepLimitStrategy;
import tosram.strategy.WeatheringStrategy;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * A panel that let user choose a {@link SearchStrategy}.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class SearchStrategyPanel extends JPanel {

	private JComboBox<String> comboBox;
	private JCheckBox chckbxStepLimit;
	private JSpinner spnStepLimit;
	private JCheckBox chckbxWeathering;
	private JCheckBox[] chckbxWeatherStones;

	public SearchStrategyPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel lblSearchStrategies = new JLabel("Search Strategies");
		lblSearchStrategies.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblSearchStrategies);

		comboBox = new JComboBox<String>();
		comboBox.addItem("Improvement");
		comboBox.addItem("Linear");
		add(comboBox);

		add(Box.createVerticalGlue());

		chckbxStepLimit = new JCheckBox("Step Limit");
		chckbxStepLimit.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(chckbxStepLimit);

		spnStepLimit = new JSpinner();
		spnStepLimit.setEnabled(false);
		spnStepLimit.setModel(new SpinnerNumberModel(20, 1, 50, 1));
		add(spnStepLimit);
		chckbxStepLimit.addItemListener(EventHandler.create(ItemListener.class,
				spnStepLimit, "enabled", "source.selected"));

		add(Box.createVerticalGlue());

		chckbxWeathering = new JCheckBox("Weathering");
		chckbxWeathering.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(chckbxWeathering);
		chckbxWeathering.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				for (JCheckBox cbx : chckbxWeatherStones)
					cbx.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		JPanel panel = new JPanel();
		panel.setFocusCycleRoot(true);
		add(panel);
		panel.setLayout(new GridLayout(5, 6, 0, 0));
		chckbxWeatherStones = new JCheckBox[30];
		for (int i = 0; i < 30; i++) {
			panel.add(chckbxWeatherStones[i] = new JCheckBox("     "));
			chckbxWeatherStones[i].setEnabled(false);
			chckbxWeatherStones[i].setHorizontalTextPosition(JCheckBox.CENTER);
		}

		add(Box.createVerticalGlue());
	}

	/**
	 * Create the search strategy user selected.
	 * 
	 * @return a <code>SearchStrategy</code>
	 */
	public SearchStrategy createSearchStrategy() {
		SearchStrategy ss;
		if (comboBox.getSelectedIndex() == 0)
			ss = new ImprovementStrategy();
		else
			ss = new LinearStrategy();
		if (chckbxStepLimit.isSelected()) {
			ss = new StepLimitStrategy(ss, (int) spnStepLimit.getValue());
		}
		if (chckbxWeathering.isSelected()) {
			boolean[] bools = new boolean[30];
			for (int i = 0; i < 30; i++)
				bools[i] = chckbxWeatherStones[i].isSelected();
			ss = new WeatheringStrategy(ss, bools);
		}
		return ss;
	}
}
