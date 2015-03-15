package tosram.view;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import tosram.algorithm.LongComboCountingAlgorithm;
import tosram.algorithm.PathConstrain;
import tosram.algorithm.PathFindingAlgorithm;
import tosram.algorithm.idastar.ComboHeuristicCostEstimater;
import tosram.algorithm.idastar.IDAStarPathFindingAlgorithm;
import tosram.algorithm.montecarlo.MonteCarloPathFindingAlgorithm;
import tosram.algorithm.path.CompositePathConstrain;
import tosram.algorithm.path.DiagonalMovePathConstrain;
import tosram.algorithm.path.NullStartPathConstrain;
import tosram.algorithm.path.UTurnPathConstrain;

/**
 * The Settings handler.
 * 
 * @author johnchen902
 */
public class Settings {

	private JPanel panel;
	private JRadioButton btnIDAStar;
	private JRadioButton btnMonteCarlo;
	private JCheckBox cbxUTurn;
	private JCheckBox cbxNullStart;
	private JCheckBox cbxDiagonalMove;
	private boolean iDAStar = true;
	private boolean monteCarlo = false;
	private boolean uTurn = true;
	private boolean nullStart = true;
	private boolean diagonalMove = true;

	private void initEditorPanel() {
		panel = new JPanel(new GridLayout(0, 1));
		ButtonGroup group = new ButtonGroup();
		panel.add(btnIDAStar = new JRadioButton("ID A*"));
		group.add(btnIDAStar);
		panel.add(btnMonteCarlo = new JRadioButton("Monte Carlo"));
		group.add(btnMonteCarlo);
		panel.add(cbxUTurn = new JCheckBox("Forbid U Turn"));
		panel.add(cbxNullStart = new JCheckBox("Forbid Null Start"));
		panel.add(cbxDiagonalMove = new JCheckBox("Forbid Diagonal Move"));
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
		uTurn = cbxUTurn.isSelected();
		nullStart = cbxNullStart.isSelected();
		diagonalMove = cbxDiagonalMove.isSelected();
	}

	/**
	 * Cancel the editor's change.
	 */
	public void cancel() {
		btnIDAStar.setSelected(iDAStar);
		btnMonteCarlo.setSelected(monteCarlo);
		cbxUTurn.setSelected(uTurn);
		cbxNullStart.setSelected(nullStart);
		cbxDiagonalMove.setSelected(diagonalMove);
	}

	/**
	 * Create a <code>PathFindingAlgorithm</code> from the settings.
	 * 
	 * @return a <code>PathFindingAlgorithm</code>
	 */
	public PathFindingAlgorithm getAlgorithm() {
		List<PathConstrain> list = new ArrayList<>();
		if (uTurn)
			list.add(new UTurnPathConstrain());
		if (nullStart)
			list.add(new NullStartPathConstrain());
		if (diagonalMove)
			list.add(new DiagonalMovePathConstrain());
		if (iDAStar)
			return new IDAStarPathFindingAlgorithm(
					new LongComboCountingAlgorithm(),
					new CompositePathConstrain(list),
					new ComboHeuristicCostEstimater());
		if (monteCarlo)
			return new MonteCarloPathFindingAlgorithm(
					new LongComboCountingAlgorithm(),
					new CompositePathConstrain(list));
		throw new AssertionError("iDAStar || monteCarlo");
	}
}
