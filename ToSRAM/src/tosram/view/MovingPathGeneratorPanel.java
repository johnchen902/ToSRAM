package tosram.view;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.GridLayout;

import javax.swing.JRadioButton;
import javax.swing.BoxLayout;

import tosram.BasicMovingPathGenerator;
import tosram.HandMovingPathGenerator;
import tosram.MovingPathGenerator;

/**
 * A panel that let user choose time limit to move stone and a
 * {@link MovingPathGenerator}.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class MovingPathGeneratorPanel extends JPanel {
	private JSpinner spnTimeLimit;
	private JRadioButton btnHand;
	private JRadioButton btnSimple;

	public MovingPathGeneratorPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel label = new JLabel("Moving Time (ms)");
		label.setAlignmentX(0.5f);
		add(label);

		spnTimeLimit = new JSpinner(new SpinnerNumberModel(5000, 250, 20000, 250));
		add(spnTimeLimit);

		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		ButtonGroup group = new ButtonGroup();

		btnSimple = new JRadioButton("Simple Move");
		btnSimple.setSelected(true);
		btnSimple.setAlignmentX(0.5f);
		panel.add(btnSimple);
		group.add(btnSimple);

		btnHand = new JRadioButton("Hand-like Move");
		btnHand.setAlignmentX(0.5f);
		panel.add(btnHand);
		group.add(btnHand);
	}

	/**
	 * Get the time limit user selected.
	 * 
	 * @return the maximum time to move stone in milliseconds
	 */
	public int getTimeLimit() {
		return (int) spnTimeLimit.getValue();
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
