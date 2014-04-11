package tosram.view.xrobot;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.javatuples.LabelValue;

import tosram.PathRobot;
import tosram.view.MnemonicsDispatcher;
import tosram.view.PathRobotCreater;
import tosram.view.WeatheringPane;
import tosram.xrobot.DiagonalMoving;
import tosram.xrobot.EndAtTopGoalSeriesFactory;
import tosram.xrobot.ForwardMoving;
import tosram.xrobot.GoalSeriesFactory;
import tosram.xrobot.IDAStarRobot;
import tosram.xrobot.MaxComboGoalSeriesFactory;
import tosram.xrobot.Moving;
import tosram.xrobot.NoDiagonalMoving;
import tosram.xrobot.WeatheringMoving;

/**
 * A <code>PathRobotCreater</code> that creates a {@link IDAStarRobot}.
 * 
 * @author johnchen902
 */
public class IDAStarRobotCreater implements PathRobotCreater {

	private JPanel wrapper;
	private JCheckBox cbxDiagonal;
	private JLabel lblCost;
	private JSpinner spnCost;
	private JCheckBox chckbxWeathering;
	private WeatheringPane weatheringPane;
	private JCheckBox cbxEndAtTop;

	private int lastCost;

	private void initialize() {
		if (wrapper != null)
			return;

		wrapper = new JPanel();

		Box box = Box.createVerticalBox();
		wrapper.add(box);

		cbxDiagonal = new JCheckBox("Diagonal", true);
		box.add(cbxDiagonal);
		MnemonicsDispatcher.registerComponent(cbxDiagonal);

		JPanel pnCost = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnCost.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnCost.setBorder(new EmptyBorder(0, 16, 0, 0));
		box.add(pnCost);

		lblCost = new JLabel();
		pnCost.add(lblCost);
		MnemonicsDispatcher.registerComponent(lblCost);

		spnCost = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
		pnCost.add(spnCost);
		lblCost.setLabelFor(spnCost);
		spnCost.setAlignmentX(JSpinner.LEFT_ALIGNMENT);

		diagonalSelected();
		spnCost.setValue(2);
		lastCost = 1;

		cbxDiagonal.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton bt = (AbstractButton) e.getSource();
				boolean selected = bt.isSelected();
				if (selected)
					diagonalSelected();
				else
					diagonalDeselected();
				int oldCost = (int) spnCost.getValue();
				spnCost.setValue(lastCost);
				lastCost = oldCost;
			}
		});

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
		MnemonicsDispatcher.registerComponent(chckbxWeathering);

		JPanel panel = new JPanel();
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setBorder(new EmptyBorder(0, 12, 0, 0));
		box.add(panel);

		weatheringPane = new WeatheringPane();
		panel.add(weatheringPane);
		weatheringPane.setEnabled(false);

		cbxEndAtTop = new JCheckBox("End at Top");
		box.add(cbxEndAtTop);
		MnemonicsDispatcher.registerComponent(cbxEndAtTop);
	}

	private void diagonalSelected() {
		lblCost.setText("Diagonal Cost:");
		String toolTipText = "Relative cost of a diagonal move.";
		spnCost.setToolTipText(toolTipText);
	}

	private void diagonalDeselected() {
		lblCost.setText("Turn Cost:");
		String toolTipText = "Relative cost of a direction changing move."
				+ " (Take more time with higher value)";
		spnCost.setToolTipText(toolTipText);
	}

	@Override
	public PathRobot createPathRobot() {
		initialize();
		Moving moving;
		int costValue = (int) spnCost.getValue();
		if (cbxDiagonal.isSelected()) {
			moving = new DiagonalMoving(costValue);
		} else {
			if (costValue == 1)
				moving = new NoDiagonalMoving();
			else
				moving = new ForwardMoving(costValue);
		}
		if (chckbxWeathering.isSelected()) {
			moving = new WeatheringMoving(moving, weatheringPane.getWeathered());
		}
		GoalSeriesFactory series = new MaxComboGoalSeriesFactory();
		if (cbxEndAtTop.isSelected())
			series = new EndAtTopGoalSeriesFactory(series);
		return new IDAStarRobot(series, moving);
	}

	@Override
	public List<LabelValue<String, Component>> getSettingsTabs() {
		initialize();
		return Arrays.asList(LabelValue.with("Diagonal and Weathering",
				(Component) wrapper));
	}
}
