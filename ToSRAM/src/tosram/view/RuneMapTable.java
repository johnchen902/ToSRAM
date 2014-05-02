package tosram.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import tosram.RuneMap;
import tosram.RuneStone;

/**
 * A table that shows a <code>RuneMap</code>.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class RuneMapTable extends JTable {
	private RuneMapModel tableModel;
	private boolean editable;

	public RuneMapTable() {
		setTableHeader(null);
		setModel(tableModel = new RuneMapModel());
		setDefaultRenderer(RuneStone.class, new RuneMapCellRenderer());
		setDragEnabled(true);
		setDropMode(DropMode.ON);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// setEditable(false);
		setTransferHandler(new ReadOnlyTransferHandler());
		setDefaultEditor(RuneStone.class, null);

		addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("font")
						|| evt.getPropertyName().equals("runeMap"))
					adjustRowHeight();
			}
		});
	}

	/**
	 * Get whether the table is editable. Not editable by default.
	 * 
	 * @return a <code>true</code> if the table is editable; <code>false</code>
	 *         otherwise
	 * @see #setEditable(boolean)
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Set whether the table is editable.
	 * 
	 * @param editable
	 *            <code>true</code> if the table is editable; <code>false</code>
	 *            otherwise
	 * @see #isEditable()
	 */
	public void setEditable(boolean editable) {
		if (this.editable != editable) {
			this.editable = editable;
			if (editable) {
				setTransferHandler(new ReadWriteTransferHandler());
				setDefaultEditor(RuneStone.class, new RuneMapCellEditor());
			} else {
				setTransferHandler(new ReadOnlyTransferHandler());
				setDefaultEditor(RuneStone.class, null);
			}
		}
	}

	/**
	 * Set the <code>RuneMap</code> shown in this map.
	 * 
	 * @param map
	 *            the <code>RuneMap</code> shown; can be <code>null</code>
	 * @see #getRuneMap()
	 */
	public void setRuneMap(RuneMap map) {
		if (map == null) {
			if (tableModel.getRunemap() != null) {
				RuneMap oldMap = getRuneMap();
				tableModel.setRuneMap(null);
				firePropertyChange("runeMap", oldMap, null);
			}
		} else {
			if (!map.equals(tableModel.getRunemap())) {
				RuneMap oldMap = getRuneMap();
				tableModel.setRuneMap(new RuneMap(map));
				firePropertyChange("runeMap", oldMap, new RuneMap(map));
			}
		}
	}

	/**
	 * Get the <code>RuneMap</code> shown in this map.
	 * 
	 * @return the <code>RuneMap</code> shown; can be <code>null</code>
	 * @see #setRuneMap(RuneMap)
	 */
	public RuneMap getRuneMap() {
		RuneMap map = tableModel.getRunemap();
		return map == null ? null : new RuneMap(map);
	}

	private void adjustRowHeight() {
		// http://stackoverflow.com/a/1784601/2040040
		for (int row = 0; row < getRowCount(); row++) {
			int rowHeight = getRowHeight();
			for (int column = 0; column < getColumnCount(); column++) {
				Component comp = prepareRenderer(getCellRenderer(row, column),
						row, column);
				rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			}
			setRowHeight(row, rowHeight);
		}
	}

	/**
	 * Create a <code>RuneMapTable</code> that contains all types and strengths
	 * of stones.
	 * 
	 * @return a <code>RuneMapTable</code>
	 */
	public static RuneMapTable createAllStonesInstance() {
		RuneStone.Type[] types = RuneStone.Type.values();
		RuneMap map0 = new RuneMap(2, types.length);
		for (int i = 0; i < types.length; i++) {
			RuneStone.Type type = types[i];
			map0.setRuneStone(0, i, new RuneStone(type, false));
			map0.setRuneStone(1, i, new RuneStone(type, true));
		}

		RuneMapTable rmt = new RuneMapTable();
		rmt.setRuneMap(map0);
		return rmt;
	}

	private static class RuneMapModel extends AbstractTableModel {

		private RuneMap runemap;

		@Override
		public int getRowCount() {
			return (runemap == null) ? 0 : runemap.getHeight();
		}

		@Override
		public int getColumnCount() {
			return (runemap == null) ? 0 : runemap.getWidth();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return runemap.getStone(columnIndex, rowIndex);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return RuneStone.class;
		}

		public void setRuneMap(RuneMap map) {
			RuneMap oldmap = this.runemap;
			this.runemap = map;
			if (oldmap == null || map == null
					|| oldmap.getWidth() != map.getWidth()
					|| oldmap.getHeight() != map.getHeight()) {
				fireTableStructureChanged();
			} else {
				fireTableDataChanged();
			}
		}

		public RuneMap getRunemap() {
			return runemap;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return rowIndex >= 0 && rowIndex < getRowCount()
					&& columnIndex >= 0 && columnIndex < getColumnCount();
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			runemap.setRuneStone(columnIndex, rowIndex, (RuneStone) aValue);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	private static class RuneMapCellRenderer extends DefaultTableCellRenderer {
		@Override
		protected void setValue(Object value) {
			super.setValue(value);
			if (value == null)
				return;
			RuneStone stone = (RuneStone) value;
			switch (stone.getType()) {
			case FIRE:
				setBackground(Color.RED);
				break;
			case WATER:
				setBackground(Color.BLUE);
				break;
			case GREEN:
				setBackground(Color.GREEN);
				break;
			case LIGHT:
				setBackground(Color.YELLOW);
				break;
			case DARK:
				setBackground(Color.MAGENTA);
				break;
			case HEART:
				setBackground(Color.PINK);
				break;
			case UNKNOWN:
			default:
				setBackground(Color.WHITE);
				break;
			}
			if (stone.isStronger() && stone.getType() != RuneStone.Type.UNKNOWN)
				setForeground(Color.WHITE);
			else
				setForeground(Color.BLACK);
		}
	}

	private static class RuneMapCellEditor extends DefaultCellEditor {
		public RuneMapCellEditor() {
			super(new JTextField(1));
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			Component c = super.getTableCellEditorComponent(table, value,
					isSelected, row, column);
			c.setFont(table.getFont());
			return c;
		}

		@Override
		public boolean stopCellEditing() {
			try {
				getCellEditorValue();
			} catch (IllegalArgumentException e) {
				editorComponent.getToolkit().beep();
				return false;
			}
			return super.stopCellEditing();
		}

		@Override
		public Object getCellEditorValue() {
			String str = ((JTextField) editorComponent).getText();
			if (str.length() > 1)
				str = String.valueOf(str.charAt(str.length() - 1));
			return RuneStone.valueOf(str);
		}
	}

	// thanks to http://stackoverflow.com/a/13856193/2040040
	private static class ReadOnlyTransferHandler extends TransferHandler {
		@Override
		public boolean canImport(TransferSupport support) {
			return false;
		}

		@Override
		public boolean importData(TransferSupport support) {
			return false;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return DnDConstants.ACTION_COPY;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			JTable table = (JTable) c;
			if (table.getSelectedRow() != -1 && table.getSelectedColumn() != -1) {
				RuneStone value = (RuneStone) table.getValueAt(
						table.getSelectedRow(), table.getSelectedColumn());
				return new RuneStoneTransferable(value);
			} else
				return null;
		}
	}

	private static class ReadWriteTransferHandler extends
			ReadOnlyTransferHandler {
		@Override
		public boolean canImport(TransferSupport support) {
			if (!support.isDrop())
				return false;
			if (!support
					.isDataFlavorSupported(RuneStoneTransferable.RUNE_STONE_FLAVOR))
				return false;
			JTable table = (JTable) support.getComponent();
			JTable.DropLocation dl = (JTable.DropLocation) support
					.getDropLocation();
			if (!table.getModel().isCellEditable(dl.getRow(), dl.getColumn()))
				return false;
			return true;
		}

		@Override
		public boolean importData(TransferSupport support) {
			boolean accept = false;
			if (canImport(support)) {
				try {
					Transferable t = support.getTransferable();
					Object value = t
							.getTransferData(RuneStoneTransferable.RUNE_STONE_FLAVOR);
					JTable table = (JTable) support.getComponent();
					JTable.DropLocation dl = (JTable.DropLocation) support
							.getDropLocation();
					table.getModel().setValueAt(value, dl.getRow(),
							dl.getColumn());
					accept = true;
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
			return accept;
		}
	}
}
