package tosram.view;

import javax.swing.JPanel;

import tosram.algorithm.PathFindingAlgorithm;
import tosram.algorithm.idastar.IDAStarPathFindingAlgorithm;

// TODO document
public class Settings {
	public JPanel getEditorPanel() {
		return new JPanel();
	}

	public void commit() {
	}

	public void cancel() {
	}

	public PathFindingAlgorithm getAlgorithm() {
		return new IDAStarPathFindingAlgorithm();
	}
}
