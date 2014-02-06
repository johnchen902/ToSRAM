package tosram.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;

import javax.swing.*;
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

	/**
	 * Create a not editable table.
	 */
	public RuneMapTable() {
		setTableHeader(null);
		setModel(tableModel = new RuneMapModel());
		setDefaultRenderer(RuneStone.class, new RuneMapCellRenderer());
		setDragEnabled(true);
		setDropMode(DropMode.ON);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setEditable(false);
	}

	/**
	 * Get whether the table is editable
	 * 
	 * @return a <code>true</code> if the table is editable; <code>false</code>
	 *         otherwise
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Set whether the table is editable
	 * 
	 * @param editable
	 *            <code>true</code> if the table is editable; <code>false</code>
	 *            otherwise
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
	 * Set the <code>RuneMap</code> shown in this map. Modifying the argument
	 * map modifies the shown map.
	 * 
	 * @param map
	 *            the <code>RuneMap</code> shown; can be <code>null</code>
	 */
	public void setRuneMap(RuneMap map) {
		tableModel.setRuneMap(map == null ? null : new RuneMap(map));
		if (map != null)
			adjustRowHeight();
	}

	/**
	 * Get the <code>RuneMap</code> shown in this map. Modifying the returned
	 * map modifies the shown map.
	 * 
	 * @return the <code>RuneMap</code> shown; can be <code>null</code>
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
				RuneStone.valueOf(((JTextField) editorComponent).getText());
			} catch (IllegalArgumentException e) {
				editorComponent.getToolkit().beep();
				return false;
			}
			return super.stopCellEditing();
		}

		@Override
		public Object getCellEditorValue() {
			return RuneStone.valueOf(((JTextField) editorComponent).getText());
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
			if (table.getSelectedRow() != -1 && table.getSelectedRow() != -1) {
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
