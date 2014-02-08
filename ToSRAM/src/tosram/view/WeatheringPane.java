package tosram.view;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * A component that let user choose the position of weathering stones. It is a
 * {@link JComponent}, but not necessary a {@link JPanel} or a {@link JTable}.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class WeatheringPane extends JPanel {
	private final int columns;
	private final int rows;
	private final JCheckBox[][] cbxWeatherStones;

	/**
	 * Generate a WeatheringPane with 5 rows and 6 columns
	 */
	public WeatheringPane() {
		this(5, 6);
	}

	/**
	 * Generate a WeatheringPane with specified columns and row.
	 */
	public WeatheringPane(int rows, int columns) {
		super(new GridLayout(rows, columns));
		this.columns = columns;
		this.rows = rows;
		cbxWeatherStones = new JCheckBox[rows][columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++) {
				cbxWeatherStones[r][c] = new JCheckBox("     ");
				add(cbxWeatherStones[r][c]);
				cbxWeatherStones[r][c]
						.setHorizontalTextPosition(JCheckBox.CENTER);
			}
		addPropertyChangeListener("enabled", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				enablilityChanged();
			}
		});
	}

	private void enablilityChanged() {
		boolean enabled = isEnabled();
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				cbxWeatherStones[r][c].setEnabled(enabled);
	}

	/**
	 * Set whether the runestone at the specified location is a weathering
	 * stone.
	 * 
	 * @param r
	 *            the row
	 * @param c
	 *            the column
	 * @param bool
	 *            <code>true</code> if is a weathering stone; <code>false</code>
	 *            otherwise
	 */
	public void setWeathered(int r, int c, boolean bool) {
		cbxWeatherStones[r][c].setSelected(bool);
	}

	/**
	 * Get whether the runestone at the specified location is a weathering
	 * stone.
	 * 
	 * @param r
	 *            the row
	 * @param c
	 *            the column
	 * @return <code>true</code> if is a weathering stone; <code>false</code>
	 *         otherwise
	 */
	public boolean getWeathered(int r, int c) {
		return cbxWeatherStones[r][c].isSelected();
	}

	/**
	 * Set whether each of runestones is a weathering stone.
	 * 
	 * @param bools
	 *            <code>bools[r * columns + c]</code> is <code>true</code> if
	 *            the runestone at row <code>r</code> column <code>c</code> is a
	 *            weathering stone; <code>false</code> otherwise
	 */
	public void setWeathered(boolean[] bools) {
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				cbxWeatherStones[r][c].setSelected(bools[r * columns + c]);
	}

	/**
	 * Get whether each of runestones is a weathering stone.
	 * 
	 * @return <code>bools[r * columns + c]</code> is <code>true</code> if the
	 *         runestone at row <code>r</code> column <code>c</code> is a
	 *         weathering stone; <code>false</code> otherwise
	 */
	public boolean[] getWeathered() {
		boolean[] bools = new boolean[rows * columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				bools[r * columns + c] = cbxWeatherStones[r][c].isSelected();
		return bools;
	}

	/**
	 * Set whether each of runestones is a weathering stone.
	 * 
	 * @param bools
	 *            <code>bools[r][c]</code> is <code>true</code> if the runestone
	 *            at row <code>r</code> column <code>c</code> is a weathering
	 *            stone; <code>false</code> otherwise
	 */
	public void setTwoDimensionalWeathered(boolean[][] bools) {
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				cbxWeatherStones[r][c].setSelected(bools[r][c]);
	}

	/**
	 * Get whether each of runestones is a weathering stone.
	 * 
	 * @return <code>bools[r][c]</code> is <code>true</code> if the runestone at
	 *         row <code>r</code> column <code>c</code> is a weathering stone;
	 *         <code>false</code> otherwise
	 */
	public boolean[][] getTwoDimensionalWeathered() {
		boolean[][] bools = new boolean[rows][columns];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				bools[r][c] = cbxWeatherStones[r][c].isSelected();
		return bools;
	}
}
