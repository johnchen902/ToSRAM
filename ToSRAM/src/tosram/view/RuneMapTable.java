package tosram.view;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Objects;

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

	public static interface Renderer {
		public Component render(RuneStone stone, int x, int y);
	}

	private RuneMap map;
	private Renderer renderer;

	// TODO document
	public RuneMapTable(RuneMap map, Renderer renderer) {
		this.map = new RuneMap(map);
		this.renderer = Objects.requireNonNull(renderer);
		update();
	}

	public RuneMapTable(Renderer renderer) {
		this(createEmptyMap(), renderer);
	}

	public RuneMapTable(RuneMap map) {
		this(map, RuneLabel.factory());
	}

	public RuneMapTable() {
		this(createEmptyMap(), RuneLabel.factory());
	}

	private static RuneMap createEmptyMap() {
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

	/**
	 * Set the function used to render <code>RuneStone</code>.
	 * 
	 * @param renderer
	 *            the function used to render <code>RuneStone</code>.
	 */
	public void setRenderer(Renderer renderer) {
		this.renderer = Objects.requireNonNull(renderer);
		update();
	}

	/**
	 * Get the function used to render <code>RuneStone</code>.
	 * 
	 * @return the function used to render <code>RuneStone</code>.
	 */
	public Renderer getRenderer() {
		return renderer;
	}

	private void update() {
		removeAll();
		setLayout(new GridLayout(map.getHeight(), map.getWidth(), 1, 1));
		for (int i = 0; i < map.getHeight(); i++)
			for (int j = 0; j < map.getWidth(); j++)
				add(renderer.render(map.getStone(j, i), j, i));
		validate();
	}
}
