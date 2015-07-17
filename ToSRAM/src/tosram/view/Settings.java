package tosram.view;

import java.awt.GridLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import tosram.algorithm.LongComboCounter;
import tosram.algorithm.PathRestriction;
import tosram.algorithm.PathFinder;
import tosram.algorithm.idastar.ComboHeuristicCostEstimater;
import tosram.algorithm.idastar.IDAStarPathFinder;
import tosram.algorithm.montecarlo.MonteCarloPathFinder;
import tosram.algorithm.path.CompositeRestriction;
import tosram.algorithm.path.DiagonalMoveRestriction;
import tosram.algorithm.path.IdenticalStartRestriction;
import tosram.algorithm.path.UTurnRestriction;

/**
 * The Settings handler.
 * 
 * @author johnchen902
 */
public class Settings {

	private JPanel panel;
	private JRadioButton btnIDAStar;
	private JRadioButton btnMonteCarlo;
	private JSpinner spnIteration;
	private JCheckBox cbxDiagonalMove;
	private boolean iDAStar = true;
	private boolean monteCarlo = false;
	private int iteration = 1000000;
	private boolean diagonalMove = true;

	private void initEditorPanel() {
		panel = new JPanel(new GridLayout(0, 1));
		ButtonGroup group = new ButtonGroup();
		panel.add(btnIDAStar = new JRadioButton("ID A*"));
		group.add(btnIDAStar);
		panel.add(btnMonteCarlo = new JRadioButton("Monte Carlo"));
		group.add(btnMonteCarlo);

		panel.add(spnIteration = new JSpinner(new SpinnerNumberModel(1000000,
				10000, 2000000000, 10000)));
		btnMonteCarlo.addItemListener(e -> spnIteration
				.setEnabled(btnMonteCarlo.isSelected()));
		spnIteration.setEnabled(false);

		panel.add(cbxDiagonalMove = new JCheckBox("No Diagonal Move"));

		panel.add(new JLabel("Look and Feel"));
		JComboBox<String> comboBox = new JComboBox<>(Arrays
				.stream(UIManager.getInstalledLookAndFeels())
				.map(LookAndFeelInfo::getName).toArray(String[]::new));
		comboBox.addActionListener(e -> {
			String s = comboBox.getSelectedItem().toString();
			for (LookAndFeelInfo x : UIManager.getInstalledLookAndFeels())
				if (x.getName().equals(s))
					try {
						UIManager.setLookAndFeel(x.getClassName());
						break;
					} catch (Exception ex) {
						ex.printStackTrace();
					}

			for (Window w : Window.getWindows())
				SwingUtilities.updateComponentTreeUI(w);
			SwingUtilities.getWindowAncestor(panel).pack();
		});
		panel.add(comboBox);
		cancel();
	}

	/**
	 * Get the panel to edit settings
	 * 
	 * @return a <code>JPanel</code>
	 */
	public JPanel getEditorPanel() {
		if (panel == null)
			initEditorPanel();
		return panel;
	}

	/**
	 * Commit the editor's change.
	 */
	public void commit() {
		iDAStar = btnIDAStar.isSelected();
		monteCarlo = btnMonteCarlo.isSelected();
		iteration = (int) spnIteration.getValue();
		diagonalMove = cbxDiagonalMove.isSelected();
	}

	/**
	 * Cancel the editor's change.
	 */
	public void cancel() {
		btnIDAStar.setSelected(iDAStar);
		btnMonteCarlo.setSelected(monteCarlo);
		spnIteration.setValue(iteration);
		cbxDiagonalMove.setSelected(diagonalMove);
	}

	/**
	 * Create a <code>PathFindingAlgorithm</code> from the settings.
	 * 
	 * @return a <code>PathFindingAlgorithm</code>
	 */
	public PathFinder getAlgorithm() {
		List<PathRestriction> list = new ArrayList<>();
		list.add(new UTurnRestriction());
		list.add(new IdenticalStartRestriction());
		if (diagonalMove)
			list.add(new DiagonalMoveRestriction());
		if (iDAStar)
			return new IDAStarPathFinder(new LongComboCounter(),
					CompositeRestriction.composite(list),
					new ComboHeuristicCostEstimater());
		if (monteCarlo)
			return new MonteCarloPathFinder(new LongComboCounter(),
					CompositeRestriction.composite(list), iteration);
		throw new AssertionError("iDAStar || monteCarlo");
	}
}
