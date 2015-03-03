package tosram.view;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import tosram.algorithm.CompositePathConstrain;
import tosram.algorithm.DiagonalMovePathConstrain;
import tosram.algorithm.LongComboCountingAlgorithm;
import tosram.algorithm.NullStartPathConstrain;
import tosram.algorithm.PathConstrain;
import tosram.algorithm.PathFindingAlgorithm;
import tosram.algorithm.UTurnPathConstrain;
import tosram.algorithm.idastar.ComboHeuristicCostEstimater;
import tosram.algorithm.idastar.IDAStarPathFindingAlgorithm;

/**
 * The Settings handler.
 * 
 * @author johnchen902
 */
public class Settings {

	private JPanel panel;
	private JCheckBox cbxUTurn;
	private JCheckBox cbxNullStart;
	private JCheckBox cbxDiagonalMove;
	private boolean uTurn = true;
	private boolean nullStart = true;
	private boolean diagonalMove = true;

	private void initEditorPanel() {
		panel = new JPanel(new GridLayout(0, 1));
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
		uTurn = cbxUTurn.isSelected();
		nullStart = cbxNullStart.isSelected();
		diagonalMove = cbxDiagonalMove.isSelected();
	}

	/**
	 * Cancel the editor's change.
	 */
	public void cancel() {
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
		return new IDAStarPathFindingAlgorithm(
				new LongComboCountingAlgorithm(), new CompositePathConstrain(
						list), new ComboHeuristicCostEstimater());
	}
}
