package tosram.view;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

// Idea from https://weblogs.java.net/blog/enicholas/archive/2006/06/mnemonic_magic.html
/**
 * A utility to assign mnemonics (a <code>char</code>) automatically to
 * registered components.
 * 
 * @author johnchen902
 */
public class MnemonicsDispatcher {

	private MnemonicsDispatcher() {
	}

	/**
	 * An interface representing something that can have mnemonics dispatched.
	 * Clients do not need to implement this interface if their component is
	 * supported (e.g. <code>JLabel</code>, <code>AbstractButton</code>).
	 * 
	 * @see MnemonicsDispatcher#addMnemonic(Dispatchable)
	 * @see MnemonicsDispatcher#removeMnemonic(Dispatchable)
	 * @see MnemonicsDispatcher#registerComponent(JLabel)
	 * @see MnemonicsDispatcher#registerComponent(AbstractButton)
	 * @author johnchen902
	 */
	public static interface Dispatchable {
		/**
		 * Get the root of the dispatched object. No objects with the same root
		 * will have same mnemonic.
		 * 
		 * @return the root
		 */
		public Object getRoot();

		/**
		 * Get the text of the dispatched object. Only characters in the text
		 * will be used as mnemonic.
		 * 
		 * @return the text
		 */
		public String getText();

		/**
		 * Called when a mnemonic is dispatched to the object.
		 * 
		 * @param c
		 *            the mnemonic
		 * @param i
		 *            the index of the mnemonic appeared in the text
		 */
		public void setMnemonic(char c, int i);

		/**
		 * Called when a mnemonic is removed from the object.
		 */
		public void removeMnemonic();
	}

	private static class MyHierarchyListener implements HierarchyListener {
		private Dispatchable dispatchable;

		private MyHierarchyListener(Dispatchable dispatchable) {
			this.dispatchable = dispatchable;
		}

		@Override
		public void hierarchyChanged(HierarchyEvent e) {
			if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == 0)
				return;
			if (e.getComponent().isShowing()) {
				addMnemonic(dispatchable);
			} else {
				removeMnemonic(dispatchable);
			}
		}
	}

	private static class MyTextChangeListener implements PropertyChangeListener {
		private Dispatchable dispatchable;

		private MyTextChangeListener(Dispatchable dispatchable) {
			this.dispatchable = dispatchable;
		}

		@Override
		public void propertyChange(PropertyChangeEvent e) {
			removeMnemonic(dispatchable);
			addMnemonic(dispatchable);
		}
	}

	/**
	 * Register the <code>JLabel</code> so that its mnemonic will be
	 * automatically dispatched and updated.
	 * 
	 * @param lb
	 *            the <code>JLabel</code> to register
	 */
	public static void registerComponent(final JLabel lb) {
		Dispatchable dis = new Dispatchable() {
			@Override
			public Object getRoot() {
				return SwingUtilities.getRoot(lb);
			}

			@Override
			public String getText() {
				return lb.getText();
			}

			@Override
			public void setMnemonic(char c, int i) {
				lb.setDisplayedMnemonic(c);
				lb.setDisplayedMnemonicIndex(i);
			}

			@Override
			public void removeMnemonic() {
				lb.setDisplayedMnemonicIndex(-1);
			}
		};
		lb.addHierarchyListener(new MyHierarchyListener(dis));
		lb.addPropertyChangeListener("text", new MyTextChangeListener(dis));
	}

	/**
	 * Register the <code>AbstractButton</code> so that its mnemonic will be
	 * automatically dispatched and updated.
	 * 
	 * @param lb
	 *            the <code>AbstractButton</code> to register
	 */
	public static void registerComponent(final AbstractButton bt) {
		Dispatchable dis = new Dispatchable() {
			@Override
			public Object getRoot() {
				return SwingUtilities.getRoot(bt);
			}

			@Override
			public String getText() {
				return bt.getText();
			}

			@Override
			public void setMnemonic(char c, int i) {
				bt.setMnemonic(c);
				bt.setDisplayedMnemonicIndex(i);
			}

			@Override
			public void removeMnemonic() {
				bt.setDisplayedMnemonicIndex(-1);
			}
		};
		bt.addHierarchyListener(new MyHierarchyListener(dis));
		bt.addPropertyChangeListener("text", new MyTextChangeListener(dis));
	}

	private static final WeakHashMap<Object, Map<Character, Dispatchable>> map = new WeakHashMap<>();

	private static boolean tryChar(Dispatchable dispatchable, Object root,
			char c, int index) {
		c = Character.toLowerCase(c);
		if (map.get(root).containsKey(c)) {
			return false;
		} else {
			dispatchable.setMnemonic(c, index);
			map.get(root).put(c, dispatchable);
			return true;
		}
	}

	/**
	 * Attempt to dispatch a mnemonic to a <code>Dispatchable</code>
	 * 
	 * @param dispatchable
	 *            the object to have mnemonic dispatched
	 */
	public static void addMnemonic(Dispatchable dispatchable) {
		Object root = dispatchable.getRoot();
		if (!map.containsKey(root))
			map.put(root, new HashMap<Character, Dispatchable>());
		String text = dispatchable.getText();
		// Try the first character in the string
		if (!text.isEmpty())
			if (tryChar(dispatchable, root, text.charAt(0), 0))
				return;
		// Try capitalized letters
		for (int i = 1; i < text.length(); i++)
			if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(text.charAt(i)) != -1)
				if (tryChar(dispatchable, root, text.charAt(i), i))
					return;
		// Try lower case consonants
		for (int i = 1; i < text.length(); i++)
			if ("bcdfghjklmnpqrstvwxyz".indexOf(text.charAt(i)) != -1)
				if (tryChar(dispatchable, root, text.charAt(i), i))
					return;
		// Try lower case vowels
		for (int i = 1; i < text.length(); i++)
			if ("aeiou".indexOf(text.charAt(i)) != -1)
				if (tryChar(dispatchable, root, text.charAt(i), i))
					return;
		// Give up
	}

	/**
	 * Remove a mnemonic of <code>Dispatchable</code>. The method has no effect
	 * if the object is never dispatched a mnemonic.
	 * 
	 * @param dispatchable
	 *            the object to have mnemonic removed
	 */
	public static void removeMnemonic(Dispatchable dispatchable) {
		Object root = dispatchable.getRoot();
		if (!map.containsKey(root))
			return;
		for (Entry<Character, Dispatchable> e : map.get(root).entrySet())
			if (e.getValue() == dispatchable) {
				map.get(root).remove(e.getKey());
				dispatchable.removeMnemonic();
				return;
			}
	}
}
