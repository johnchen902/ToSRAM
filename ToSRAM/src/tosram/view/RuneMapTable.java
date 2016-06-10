package tosram.view;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Objects;
import java.util.function.IntSupplier;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import tosram.MutableRuneMap;
import tosram.RuneMap;
import tosram.RuneStone;

/**
 * A table that shows a <code>RuneMap</code>.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class RuneMapTable extends JPanel {

	/**
	 * A supplier of rendering component.
	 * 
	 * @author johnchen902
	 */
	public static interface Renderer {

		/**
		 * Supply the rendering component of a <code>RuneStone</code> at
		 * specific location.
		 * 
		 * @param stone
		 *            the <code>RuneStone</code> to be rendered
		 * @param x
		 *            the X index of the <code>RuneStone</code>
		 * @param y
		 *            the Y index of the <code>RuneStone</code>
		 * @return a component that renders the <code>RuneStone</code>; will be
		 *         added to <code>RuneMapTable</code>
		 */
		public Component render(RuneStone stone, int x, int y);
	}

	private RuneMap map;
	private Renderer renderer;
	private int focusId = -1;

	/**
	 * Create a <code>RuneMapTable</code> with the specified
	 * <code>RuneMap</code> and <code>Renderer</code>.
	 * 
	 * @param map
	 *            the <code>RuneMap</code> shown
	 * @param renderer
	 *            the function used to render <code>RuneStone</code>
	 * @see #setRuneMap(RuneMap)
	 * @see #setRenderer(Renderer)
	 */
	public RuneMapTable(RuneMap map, Renderer renderer) {
		this.map = map;
		this.renderer = Objects.requireNonNull(renderer);
		update();
		putTraverseAction("UP", () -> -map.getWidth());
		putTraverseAction("DOWN", () -> +map.getWidth());
		putTraverseAction("LEFT", () -> -1);
		putTraverseAction("RIGHT", () -> +1);
	}

	private void putTraverseAction(String key, IntSupplier amount) {
		InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke("pressed " + key), key);
		ActionMap am = getActionMap();
		am.put(key, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newFocus = focusId + amount.getAsInt();
				if (newFocus < 0) {
					getComponent(0).transferFocusBackward();
				} else if (newFocus >= getComponentCount()) {
					getComponent(getComponentCount() - 1).transferFocus();
				} else {
					getComponent(newFocus).requestFocusInWindow();
				}
			}
		});
	}

	/**
	 * Create a <code>RuneMapTable</code> with a default <code>RuneMap</code>
	 * and the specified <code>Renderer</code>.
	 * 
	 * @param renderer
	 *            the function used to render <code>RuneStone</code>
	 * @see #setRenderer(Renderer)
	 */
	public RuneMapTable(Renderer renderer) {
		this(createSampleMap(), renderer);
	}

	/**
	 * Create a <code>RuneMapTable</code> with the specified
	 * <code>RuneMap</code> and a default <code>Renderer</code>.
	 * 
	 * @param map
	 *            the <code>RuneMap</code> shown
	 * @see #setRuneMap(RuneMap)
	 */
	public RuneMapTable(RuneMap map) {
		this(map, RuneLabel.factory());
	}

	/**
	 * Create a <code>RuneMapTable</code> with default <code>RuneMap</code> and
	 * <code>Renderer</code>.
	 */
	public RuneMapTable() {
		this(createSampleMap(), RuneLabel.factory());
	}

	private static RuneMap createSampleMap() {
		MutableRuneMap m = new MutableRuneMap(6, 5);
		m.setRuneStone(0, 0, RuneStone.FIRE);
		m.setRuneStone(1, 0, RuneStone.LIGHT);
		m.setRuneStone(2, 0, RuneStone.LIGHT);
		m.setRuneStone(3, 0, RuneStone.EARTH);
		m.setRuneStone(4, 0, RuneStone.LIGHT);
		m.setRuneStone(5, 0, RuneStone.DARK);
		m.setRuneStone(0, 1, RuneStone.FIRE);
		m.setRuneStone(1, 1, RuneStone.HEART);
		m.setRuneStone(2, 1, RuneStone.EARTH);
		m.setRuneStone(3, 1, RuneStone.FIRE);
		m.setRuneStone(4, 1, RuneStone.WATER);
		m.setRuneStone(5, 1, RuneStone.DARK);
		m.setRuneStone(0, 2, RuneStone.DARK);
		m.setRuneStone(1, 2, RuneStone.DARK);
		m.setRuneStone(2, 2, RuneStone.LIGHT);
		m.setRuneStone(3, 2, RuneStone.EARTH);
		m.setRuneStone(4, 2, RuneStone.EARTH);
		m.setRuneStone(5, 2, RuneStone.WATER);
		m.setRuneStone(0, 3, RuneStone.LIGHT);
		m.setRuneStone(1, 3, RuneStone.HEART);
		m.setRuneStone(2, 3, RuneStone.HEART);
		m.setRuneStone(3, 3, RuneStone.LIGHT);
		m.setRuneStone(4, 3, RuneStone.HEART);
		m.setRuneStone(5, 3, RuneStone.DARK);
		m.setRuneStone(0, 4, RuneStone.HEART);
		m.setRuneStone(1, 4, RuneStone.HEART);
		m.setRuneStone(2, 4, RuneStone.DARK);
		m.setRuneStone(3, 4, RuneStone.EARTH);
		m.setRuneStone(4, 4, RuneStone.FIRE);
		m.setRuneStone(5, 4, RuneStone.LIGHT);
		return new RuneMap(m);
	}

	/**
	 * Set the <code>RuneMap</code> shown in this map.
	 * 
	 * @param map
	 *            the <code>RuneMap</code> shown
	 * @see #getRuneMap()
	 */
	public void setRuneMap(RuneMap map) {
		this.map = Objects.requireNonNull(map);
		update();
	}

	/**
	 * Get the <code>RuneMap</code> shown in this map.
	 * 
	 * @return the <code>RuneMap</code> shown; can be <code>null</code>
	 * @see #setRuneMap(RuneMap)
	 */
	public RuneMap getRuneMap() {
		return map;
	}

	/**
	 * Set the function used to render <code>RuneStone</code>.
	 * 
	 * @param renderer
	 *            the function used to render <code>RuneStone</code>
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
			for (int j = 0; j < map.getWidth(); j++) {
				Component c = renderer.render(map.getRuneStone(j, i), j, i);
				add(c);
				c.addFocusListener(createFocusListener());
			}
		validate();
		if (focusId >= 0 && focusId < getComponentCount())
			getComponent(focusId).requestFocusInWindow();
	}

	private FocusListener createFocusListener() {
		return new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				Component c1 = e.getComponent();
				Component c2 = e.getOppositeComponent();
				if (c2 == null)
					return;
				Window w1 = SwingUtilities.getWindowAncestor(c1);
				Window w2 = SwingUtilities.getWindowAncestor(c2);
				if (w1 == w2)
					focusId = -1;
			}

			@Override
			public void focusGained(FocusEvent e) {
				for (int i = 0; i < getComponentCount(); i++)
					if (e.getComponent() == getComponent(i))
						focusId = i;
			}
		};
	}
}
