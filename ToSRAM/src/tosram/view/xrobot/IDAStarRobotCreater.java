package tosram.view.xrobot;

import java.awt.Component;
import java.awt.FlowLayout;
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
import javax.swing.border.EmptyBorder;

import org.javatuples.LabelValue;

import tosram.PathRobot;
import tosram.view.PathRobotCreater;
import tosram.view.WeatheringPane;
import tosram.xrobot.DiagonalMoving;
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
	private JSpinner spnDiagonalCost;
	private JCheckBox chckbxWeathering;
	private WeatheringPane weatheringPane;

	private void initialize() {
		if (wrapper != null)
			return;

		wrapper = new JPanel();

		Box box = Box.createVerticalBox();
		wrapper.add(box);

		cbxDiagonal = new JCheckBox("Diagonal", true);
		box.add(cbxDiagonal);

		JPanel pnCost = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnCost.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnCost.setBorder(new EmptyBorder(0, 16, 0, 0));
		box.add(pnCost);

		JLabel lblCost = new JLabel("Cost:");
		pnCost.add(lblCost);
		cbxDiagonal.addItemListener(EventHandler.create(ItemListener.class,
				lblCost, "enabled", "source.selected"));

		spnDiagonalCost = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
		pnCost.add(spnDiagonalCost);
		lblCost.setLabelFor(spnDiagonalCost);
		spnDiagonalCost.setAlignmentX(JSpinner.LEFT_ALIGNMENT);
		String toolTipText = "The cost of a diagonal move relative to a normal one.";
		spnDiagonalCost.setToolTipText(toolTipText);
		cbxDiagonal.addItemListener(EventHandler.create(ItemListener.class,
				spnDiagonalCost, "enabled", "source.selected"));

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

		JPanel panel = new JPanel();
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setBorder(new EmptyBorder(0, 12, 0, 0));
		box.add(panel);

		weatheringPane = new WeatheringPane();
		panel.add(weatheringPane);
		weatheringPane.setEnabled(false);
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
			moving = new WeatheringMoving(moving, weatheringPane.getWeathered());
		}
		return new IDAStarRobot(new MaxComboGoalSeriesFactory(), moving);
	}

	@Override
	public List<LabelValue<String, Component>> getSettingsTabs() {
		initialize();
		return Arrays.asList(LabelValue.with("Diagonal and Weathering",
				(Component) wrapper));
	}
}
