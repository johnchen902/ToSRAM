package tosram.view.strategy;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_CONTEXT_MENU;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_UP;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.beans.EventHandler;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import tosram.strategy.NullSolutionStrategy;
import tosram.strategy.SolutionStrategy;

/**
 * A panel that let user choose and order some strategies and create final one
 * from them.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class StrategyPanel extends JPanel {

	private DefaultListModel<StrategyCreater> usingModel;
	private DefaultListModel<StrategyCreater> unusedModel;
	private JList<StrategyCreater> listUsing;
	private JList<StrategyCreater> listUnused;
	private JButton btnAddRemove;

	public StrategyPanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane spUsing = new JScrollPane();
		add(spUsing, BorderLayout.CENTER);

		listUsing = new JList<>(usingModel = createInitialUsingModel());
		listUsing.setCellRenderer(new StrategyCreaterRenderer());
		listUsing.setDropMode(DropMode.INSERT);
		listUsing.setDragEnabled(true);
		listUsing.setTransferHandler(new UsingListTransferHandler());
		listUsing.addMouseListener(new SettingsMouseAdapter());
		listUsing.setSelectedIndex(0);
		listUsing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		spUsing.setViewportView(listUsing);

		btnAddRemove = new JButton("Add...");
		add(btnAddRemove, BorderLayout.SOUTH);
		btnAddRemove.addActionListener(new AddListener());
		btnAddRemove.setTransferHandler(new RemoveTransferHandler());

		listUnused = new JList<>(unusedModel = createInitialUnusedModel());
		listUnused.setCellRenderer(new StrategyCreaterRenderer());
		listUnused.addMouseListener(new SettingsMouseAdapter());
		listUnused.setSelectedIndex(0);
		listUnused.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		initActions();
	}

	private static class PopupListener extends MouseAdapter {
		private JList<?> list;
		private JPopupMenu popup;

		PopupListener(JList<?> list, JPopupMenu popup) {
			this.list = list;
			this.popup = popup;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				list.setSelectedIndex(list.locationToIndex(e.getPoint()));
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private static class PopupAction extends AbstractAction {
		private JList<?> list;
		private JPopupMenu popup;

		PopupAction(JList<?> list, JPopupMenu popup) {
			this.list = list;
			this.popup = popup;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (list.getSelectedIndex() == -1)
				return;
			Rectangle rect = list.getCellBounds(list.getSelectedIndex(),
					list.getSelectedIndex());
			popup.show(list, rect.x, rect.y);
		}
	}

	private void initActions() {
		InputMap im = listUsing.getInputMap();
		im.put(getKeyStroke('\n'), "settings");
		im.put(getKeyStroke('\b'), "remove");
		im.put(getKeyStroke('\u007f'), "remove");
		im.put(getKeyStroke(VK_UP, ALT_DOWN_MASK), "moveUp");
		im.put(getKeyStroke(VK_DOWN, ALT_DOWN_MASK), "moveDown");
		im.put(getKeyStroke(VK_PAGE_UP, ALT_DOWN_MASK), "moveTop");
		im.put(getKeyStroke(VK_PAGE_DOWN, ALT_DOWN_MASK), "moveBottom");
		im.put(getKeyStroke(VK_CONTEXT_MENU, 0), "showPopup");

		ActionMap ac = listUsing.getActionMap();
		ac.put("settings", new SettingsAction(listUsing));
		ac.put("remove", new RemoveAction());
		ac.put("moveUp", new MoveAction(MoveAction.MOVE_UP));
		ac.put("moveDown", new MoveAction(MoveAction.MOVE_DOWN));
		ac.put("moveTop", new MoveAction(MoveAction.MOVE_TOP));
		ac.put("moveBottom", new MoveAction(MoveAction.MOVE_BOTTOM));

		JPopupMenu usingPopup = new JPopupMenu();
		listUsing.addMouseListener(new PopupListener(listUsing, usingPopup));
		ac.put("showPopup", new PopupAction(listUsing, usingPopup));
		usingPopup.add(new JMenuItem(new SettingsAction(listUsing)));
		usingPopup.addSeparator();
		usingPopup.add(new JMenuItem(new MoveAction(MoveAction.MOVE_UP)));
		usingPopup.add(new JMenuItem(new MoveAction(MoveAction.MOVE_DOWN)));
		usingPopup.add(new JMenuItem(new MoveAction(MoveAction.MOVE_TOP)));
		usingPopup.add(new JMenuItem(new MoveAction(MoveAction.MOVE_BOTTOM)));
		usingPopup.addSeparator();
		usingPopup.add(new JMenuItem(new RemoveAction()));

		InputMap im2 = listUnused.getInputMap();
		im2.put(getKeyStroke('\n'), "settings");
		im2.put(getKeyStroke(VK_CONTEXT_MENU, 0), "showPopup");

		ActionMap ac2 = listUnused.getActionMap();
		ac2.put("settings", new SettingsAction(listUnused));

		JPopupMenu unusedPopup = new JPopupMenu();
		listUnused.addMouseListener(new PopupListener(listUnused, unusedPopup));
		ac2.put("showPopup", new PopupAction(listUnused, unusedPopup));
		unusedPopup.add(new JMenuItem(new SettingsAction(listUnused)));
	}

	private DefaultListModel<StrategyCreater> createInitialUsingModel() {
		DefaultListModel<StrategyCreater> model = new DefaultListModel<>();
		model.addElement(new MaxComboCreater());
		model.addElement(new MinStepCreater());
		return model;
	}

	private DefaultListModel<StrategyCreater> createInitialUnusedModel() {
		DefaultListModel<StrategyCreater> model = new DefaultListModel<>();
		model.addElement(new GroupAttackCreater());
		model.addElement(new UseStoneCreater());
		model.addElement(new UseStoneCreater(tosram.RuneStone.Type.GREEN));
		model.addElement(new SixInComboCreater());
		model.addElement(new KComboCreater());
		model.addElement(new NoStackingCreater());
		return model;
	}

	/**
	 * Create the final strategy from the strategies user selected.
	 * 
	 * @return a <code>SolutionStrategy</code>
	 */
	public SolutionStrategy createStrategy() {
		SolutionStrategy ss = new NullSolutionStrategy();
		for (int i = listUsing.getModel().getSize() - 1; i >= 0; i--) {
			StrategyCreater fsp = listUsing.getModel().getElementAt(i);
			ss = fsp.createStrategy(ss);
		}
		return ss;
	}

	private class AddListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (listUnused.getModel().getSize() == 0) {
				JOptionPane.showMessageDialog(StrategyPanel.this,
						"No more strategy available", "Add a strategy",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane op = new JOptionPane(listUnused,
						JOptionPane.QUESTION_MESSAGE,
						JOptionPane.OK_CANCEL_OPTION, null, null, listUnused);
				op.setComponentOrientation(getComponentOrientation());

				Dialog dialog = op.createDialog(StrategyPanel.this,
						"Choose a strategy to add");
				dialog.addWindowListener(EventHandler.create(
						WindowListener.class, listUnused,
						"requestFocusInWindow", null, "windowOpened"));
				dialog.setVisible(true);
				dialog.dispose();

				Object value = op.getValue();
				if (!Integer.valueOf(JOptionPane.OK_OPTION).equals(value))
					return;
				int sid = listUnused.getSelectedIndex();
				if (sid == -1)
					return;
				usingModel.add(0, unusedModel.remove(sid));
				listUsing.setSelectedIndex(0);
				listUsing.requestFocusInWindow();
			}
		}
	}

	private class RemoveAction extends AbstractAction {
		public RemoveAction() {
			super("Remove");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int i = listUsing.getSelectedIndex();
			if (i != -1)
				unusedModel.addElement(usingModel.remove(i));
		}
	}

	private class RemoveTransferHandler extends TransferHandler {
		@Override
		public boolean canImport(TransferSupport support) {
			return support.isDrop()
					&& support
							.isDataFlavorSupported(StrategyCreaterTransferable.FLAVOR);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if (!canImport(support))
				return false;
			try {
				Transferable t = support.getTransferable();
				StrategyCreater value = (StrategyCreater) t
						.getTransferData(StrategyCreaterTransferable.FLAVOR);
				unusedModel.addElement(value);
				return true;
			} catch (Exception exp) {
				exp.printStackTrace();
				return false;
			}
		}
	}

	private class SettingsAction extends AbstractAction {
		private JList<StrategyCreater> srcList;

		public SettingsAction(JList<StrategyCreater> srcList) {
			super("Settings");
			this.srcList = srcList;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			StrategyCreater val = srcList.getSelectedValue();
			if (val != null)
				val.settings(StrategyPanel.this);
		}
	}

	private class SettingsMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				@SuppressWarnings("unchecked")
				JList<StrategyCreater> src = (JList<StrategyCreater>) e
						.getSource();
				StrategyCreater val = src.getSelectedValue();
				if (val != null)
					val.settings(StrategyPanel.this);
			}
		}
	}

	private class MoveAction extends AbstractAction {
		public static final int MOVE_UP = 0;
		public static final int MOVE_DOWN = 1;
		public static final int MOVE_TOP = 2;
		public static final int MOVE_BOTTOM = 3;

		private final int move;

		public MoveAction(int move) {
			this.move = move;
			putValue(Action.NAME, getName());
		}

		private String getName() {
			switch (move) {
			case MOVE_UP:
				return "Move Up";
			case MOVE_DOWN:
				return "Move Down";
			case MOVE_TOP:
				return "Move To Top";
			case MOVE_BOTTOM:
				return "Move To Bottom";
			default:
				return "Unknown Move " + move;
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int i = listUsing.getSelectedIndex();
			if (i != -1) {
				int dest = getDestination(i, usingModel.size());
				usingModel.add(dest, usingModel.remove(i));
				listUsing.setSelectedIndex(dest);
			}
		}

		private int getDestination(int from, int size) {
			switch (move) {
			case MOVE_UP:
				return from == 0 ? 0 : from - 1;
			case MOVE_DOWN:
				return from == size - 1 ? size - 1 : from + 1;
			case MOVE_TOP:
				return 0;
			case MOVE_BOTTOM:
				return size - 1;
			default:
				return from;
			}
		}
	}

	private class UsingListTransferHandler extends TransferHandler {

		private int exportIndex = -1;

		@Override
		public boolean canImport(TransferSupport support) {
			return support.isDrop()
					&& support
							.isDataFlavorSupported(StrategyCreaterTransferable.FLAVOR);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if (!canImport(support))
				return false;
			try {
				Transferable t = support.getTransferable();
				StrategyCreater value = (StrategyCreater) t
						.getTransferData(StrategyCreaterTransferable.FLAVOR);
				JList.DropLocation dl = (JList.DropLocation) support
						.getDropLocation();
				int location = dl.getIndex();
				if (exportIndex >= location)
					exportIndex++;
				usingModel.add(location, value);
				return true;
			} catch (Exception exp) {
				exp.printStackTrace();
				return false;
			}
		}

		@Override
		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			btnAddRemove.setText("Remove");
			exportIndex = listUsing.getSelectedIndex();
			return new StrategyCreaterTransferable(listUsing.getSelectedValue());
		}

		@Override
		protected void exportDone(JComponent c, Transferable data, int action) {
			if (action == MOVE)
				usingModel.remove(exportIndex);
			btnAddRemove.setText("Add");
		}
	}

	private class StrategyCreaterRenderer implements
			ListCellRenderer<StrategyCreater> {
		@Override
		public Component getListCellRendererComponent(
				JList<? extends StrategyCreater> list, StrategyCreater value,
				int index, boolean isSelected, boolean cellHasFocus) {
			return value.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
		}
	}
}
