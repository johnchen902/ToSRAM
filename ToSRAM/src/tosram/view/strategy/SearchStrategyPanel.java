package tosram.view.strategy;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.EventHandler;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import tosram.strategy.ImprovementStrategy;
import tosram.strategy.LinearStrategy;
import tosram.strategy.SearchStrategy;
import tosram.strategy.StepLimitStrategy;
import tosram.strategy.WeatheringStrategy;
import tosram.view.WeatheringPane;

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
	private WeatheringPane weatheringPane;

	public SearchStrategyPanel() {
		Box box = Box.createVerticalBox();
		add(box);

		JPanel pnCombobox = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnCombobox.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.add(pnCombobox);

		JLabel lblSearchStrategies = new JLabel("Base:");
		pnCombobox.add(lblSearchStrategies);

		comboBox = new JComboBox<String>();
		pnCombobox.add(comboBox);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.addItem("Improvement");
		comboBox.addItem("Linear");

		chckbxStepLimit = new JCheckBox("Step Limit");
		box.add(chckbxStepLimit);

		JPanel indentStepLimit = new JPanel();
		indentStepLimit.setAlignmentX(Component.LEFT_ALIGNMENT);
		indentStepLimit.setBorder(new EmptyBorder(0, 22, 0, 0));
		box.add(indentStepLimit);
		indentStepLimit.setLayout(new BorderLayout(0, 0));

		spnStepLimit = new JSpinner();
		indentStepLimit.add(spnStepLimit);
		spnStepLimit.setEnabled(false);
		spnStepLimit.setModel(new SpinnerNumberModel(20, 1, 50, 1));
		chckbxStepLimit.addItemListener(EventHandler.create(ItemListener.class,
				spnStepLimit, "enabled", "source.selected"));

		chckbxWeathering = new JCheckBox("Weathering");
		box.add(chckbxWeathering);
		chckbxWeathering.addItemListener(new ItemListener() {
			boolean[] stored;

			@Override
			public void itemStateChanged(ItemEvent e) {
				weatheringPane.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (stored != null)
						weatheringPane.setWeathered(stored);
				} else {
					stored = weatheringPane.getWeathered();
					weatheringPane.setWeathered(new boolean[30]); // all false
				}
			}
		});

		JPanel indentWeathering = new JPanel();
		indentWeathering.setAlignmentX(Component.LEFT_ALIGNMENT);
		indentWeathering.setBorder(new EmptyBorder(0, 16, 0, 0));
		box.add(indentWeathering);
		indentWeathering.setLayout(new BorderLayout(0, 0));

		weatheringPane = new WeatheringPane();
		indentWeathering.add(weatheringPane);
		weatheringPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		weatheringPane.setEnabled(false);
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
			ss = new WeatheringStrategy(ss, weatheringPane.getWeathered());
		}
		return ss;
	}
}
