package tosram.view.strategy;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

import tosram.strategy.LinearStrategy;
import tosram.strategy.StrategySearchPathRobot.Strategy;

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

	/**
	 * Just create one.
	 */
	public StrategyPanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblStrategies = new JLabel("Strategies");
		lblStrategies.setLabelFor(this);
		lblStrategies.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblStrategies, BorderLayout.NORTH);

		JScrollPane spUsing = new JScrollPane();
		add(spUsing, BorderLayout.CENTER);

		listUsing = new JList<>(usingModel = createInitialUsingModel());
		listUsing.setCellRenderer(new StrategyCreaterRenderer());
		listUsing.setDropMode(DropMode.INSERT);
		listUsing.setDragEnabled(true);
		listUsing.setTransferHandler(new UsingListTransferHandler());
		listUsing.addMouseListener(new SettingsMouseAdapter());
		spUsing.setViewportView(listUsing);

		btnAddRemove = new JButton("Add");
		add(btnAddRemove, BorderLayout.SOUTH);
		btnAddRemove.addActionListener(new AddListener());
		btnAddRemove.setTransferHandler(new RemoveTransferHandler());

		listUnused = new JList<>(unusedModel = createInitialUnusedModel());
		listUnused.setCellRenderer(new StrategyCreaterRenderer());
		listUnused.addMouseListener(new SettingsMouseAdapter());
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
		model.addElement(new UseStoneCreater(tosram.RuneStone.Type.FIRE));
		model.addElement(new SixInComboCreater());
		model.addElement(new KComboCreater());
		model.addElement(new WeatheringChooser());
		return model;
	}

	/**
	 * Create the final strategy from the strategies user selected.
	 * 
	 * @return a <code>Strategy</code>
	 */
	public Strategy createStrategy() {
		Strategy ss = LinearStrategy.createLinearStrategy();
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
				int result = JOptionPane.showConfirmDialog(StrategyPanel.this,
						listUnused, "Choose a strategy to add",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (result != JOptionPane.OK_OPTION)
					return;
				int sid = listUnused.getSelectedIndex();
				if (sid == -1)
					return;
				usingModel.addElement(unusedModel.remove(sid));
			}
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
