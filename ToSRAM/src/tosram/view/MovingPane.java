package tosram.view;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import tosram.BasicMovingPathGenerator;
import tosram.HandMovingPathGenerator;
import tosram.MovingPathGenerator;

/**
 * A panel that let user choose settings about moving.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class MovingPane extends JPanel {
	private JSpinner spnTime;
	private JRadioButton btnHand;
	private JRadioButton btnSimple;
	private JCheckBox chckbxFastStart;

	public MovingPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel pnTime = new JPanel();
		pnTime.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		add(pnTime);

		JLabel label = new JLabel("Time:");
		pnTime.add(label);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		MnemonicsDispatcher.registerComponent(label);

		spnTime = new JSpinner(new SpinnerNumberModel(5.0, 1.25, 20.0, 0.25));
		label.setLabelFor(spnTime);
		pnTime.add(spnTime);
		spnTime.setEditor(new JSpinner.NumberEditor(spnTime, "#.##s"));

		ButtonGroup group = new ButtonGroup();

		btnSimple = new JRadioButton("Simple Path", true);
		add(btnSimple);
		group.add(btnSimple);
		MnemonicsDispatcher.registerComponent(btnSimple);

		btnHand = new JRadioButton("Hand-like Path");
		add(btnHand);
		group.add(btnHand);
		MnemonicsDispatcher.registerComponent(btnHand);

		chckbxFastStart = new JCheckBox("Fast Start");
		add(chckbxFastStart);
		MnemonicsDispatcher.registerComponent(chckbxFastStart);
	}

	/**
	 * Get the time limit user selected.
	 * 
	 * @return the maximum time to move stone in milliseconds
	 */
	public int getTimeLimit() {
		return (int) (1000 * ((Number) spnTime.getValue()).doubleValue());
	}

	/**
	 * Whether the user want to start fast.
	 * 
	 * @return <code>true</code> if the user want to start fast;
	 *         <code>false</code> otherwise
	 */
	public boolean isFastStart() {
		return chckbxFastStart.isSelected();
	}

	/**
	 * Create the generator user selected.
	 * 
	 * @return a <code>MovingPathGenerator</code>
	 */
	public MovingPathGenerator createMovingPathGenerator() {
		if (btnHand.isSelected())
			return new HandMovingPathGenerator();
		return new BasicMovingPathGenerator();
	}
}
