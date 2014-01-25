package tosram.view.xrobot;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.beans.EventHandler;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

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

	private void initialize() {
		if (wrapper != null)
			return;

		wrapper = new JPanel();

		Box box = Box.createVerticalBox();
		wrapper.add(box);

		cbxDiagonal = new JCheckBox("Diagonal", true);
		cbxDiagonal.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(cbxDiagonal);

		JLabel label = new JLabel("Cost of Diagonal:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(label);

		spnDiagonalCost = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
		box.add(spnDiagonalCost);

		cbxDiagonal.addItemListener(EventHandler.create(ItemListener.class,
				spnDiagonalCost, "enabled", "source.selected"));
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
		return new IDAStarRobot(new MaxComboGoalSeriesFactory(), moving);
	}

	@Override
	public Map<String, Component> getSettingsTabs() {
		initialize();
		Map<String, Component> map = new HashMap<String, Component>();
		map.put("IDA* Robot Settings", wrapper);
		return map;
	}

	@Override
	public String toString() {
		return "IDA* Robot";
	}
}
