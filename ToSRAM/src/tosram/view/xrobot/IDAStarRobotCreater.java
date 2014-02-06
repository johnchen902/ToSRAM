package tosram.view.xrobot;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.EventHandler;
import java.util.Arrays;
import java.util.List;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.javatuples.LabelValue;

import tosram.PathRobot;
import tosram.view.PathRobotCreater;
import tosram.xrobot.*;

/**
 * A <code>PathRobotCreater</code> that creates a {@link IDAStarRobot}.
 * 
 * @author johnchen902
 */
public class IDAStarRobotCreater implements PathRobotCreater {

	private JPanel wrapper;
	private JCheckBox cbxDiagonal;
	private JSpinner spnDiagonalCost;
	private JCheckBox chckbxWeathering;
	private JCheckBox[] chckbxWeatherStones;

	private void initialize() {
		if (wrapper != null)
			return;

		wrapper = new JPanel();

		Box box = Box.createVerticalBox();
		wrapper.add(box);

		cbxDiagonal = new JCheckBox("Diagonal", true);
		cbxDiagonal.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(cbxDiagonal);

		JLabel label = new JLabel("Cost of diagonal:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(label);

		spnDiagonalCost = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
		box.add(spnDiagonalCost);

		cbxDiagonal.addItemListener(EventHandler.create(ItemListener.class,
				spnDiagonalCost, "enabled", "source.selected"));

		chckbxWeathering = new JCheckBox("Weathering");
		chckbxWeathering.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(chckbxWeathering);
		chckbxWeathering.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				for (JCheckBox cbx : chckbxWeatherStones)
					cbx.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		JPanel panel = new JPanel();
		panel.setFocusCycleRoot(true);
		box.add(panel);
		panel.setLayout(new GridLayout(5, 6, 0, 0));
		chckbxWeatherStones = new JCheckBox[30];
		for (int i = 0; i < 30; i++) {
			panel.add(chckbxWeatherStones[i] = new JCheckBox("     "));
			chckbxWeatherStones[i].setEnabled(false);
			chckbxWeatherStones[i].setHorizontalTextPosition(JCheckBox.CENTER);
		}
	}

	@Override
	public PathRobot createPathRobot() {
		initialize();
		Moving moving;
		if (cbxDiagonal.isSelected()) {
			moving = new DiagonalMoving((int) spnDiagonalCost.getValue());
		} else {
			moving = new NoDiagonalMoving();
		}
		if (chckbxWeathering.isSelected()) {
			boolean[] bools = new boolean[30];
			for (int i = 0; i < 30; i++)
				bools[i] = chckbxWeatherStones[i].isSelected();
			moving = new WeatheringMoving(moving, bools);
		}
		return new IDAStarRobot(new MaxComboGoalSeriesFactory(), moving);
	}

	@Override
	public List<LabelValue<String, Component>> getSettingsTabs() {
		initialize();
		return Arrays.asList(LabelValue.with("IDA* Robot Settings",
				(Component) wrapper));
	}

	@Override
	public String toString() {
		return "IDA* Robot";
	}
}
