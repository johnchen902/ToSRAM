package tosram.view;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.function.Function;

import javax.swing.JPanel;

import tosram.RuneMap;
import tosram.RuneStone;

/**
 * A table that shows a <code>RuneMap</code>.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class RuneMapTable extends JPanel {

	private Function<RuneStone, Component> func;
	private RuneMap map;

	// TODO document
	public RuneMapTable(Function<RuneStone, Component> func, RuneMap map) {
		this.func = func;
		this.map = new RuneMap(map);
		update();
	}

	public RuneMapTable(Function<RuneStone, Component> func) {
		this(func, getEmptyMap());
	}

	public RuneMapTable(RuneMap map) {
		this(RuneLabel::new, getEmptyMap());
	}

	public RuneMapTable() {
		this(RuneLabel::new, getEmptyMap());
	}

	private static RuneMap getEmptyMap() {
		RuneMap m = new RuneMap(6, 5);
		for (int i = 0; i < m.getWidth(); i++)
			for (int j = 0; j < m.getHeight(); j++)
				m.setRuneStone(i, j, RuneStone.UNKNOWN);
		return m;
	}

	/**
	 * Set the <code>RuneMap</code> shown in this map.
	 * 
	 * @param map
	 *            the <code>RuneMap</code> shown; can be <code>null</code>
	 * @see #getRuneMap()
	 */
	public void setRuneMap(RuneMap map) {
		this.map = new RuneMap(map);
		update();
	}

	/**
	 * Get the <code>RuneMap</code> shown in this map.
	 * 
	 * @return the <code>RuneMap</code> shown; can be <code>null</code>
	 * @see #setRuneMap(RuneMap)
	 */
	public RuneMap getRuneMap() {
		return new RuneMap(map);
	}

	private void update() {
		removeAll();
		setLayout(new GridLayout(map.getHeight(), map.getWidth(), 1, 1));
		for (int i = 0; i < map.getHeight(); i++)
			for (int j = 0; j < map.getWidth(); j++)
				add(func.apply(map.getStone(j, i)));
		validate();
	}
}
