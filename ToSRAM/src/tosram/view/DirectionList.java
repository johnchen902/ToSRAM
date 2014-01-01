package tosram.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import tosram.Direction;

/**
 * A list of <code>Direction</code>s.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class DirectionList extends JList<Direction> {

	private DirectionModel listModel;

	/**
	 * Create <code>DirectionList</code> with an empty list.
	 */
	public DirectionList() {
		setModel(listModel = new DirectionModel());
		setCellRenderer(new DirectionListRenderer());
	}

	/**
	 * Set the <code>Direction</code>s shown.
	 * 
	 * @param dir
	 *            a list of <code>Direction</code>
	 */
	public void setDirections(List<Direction> dir) {
		listModel.setDirections(dir);
	}

	private static class DirectionModel extends AbstractListModel<Direction> {

		private List<Direction> directions;

		public void setDirections(List<Direction> dir) {
			dir = dir == null ? null : new ArrayList<Direction>(dir);

			int oldSize = (directions == null) ? 0 : directions.size();
			directions = dir;
			int newSize = (directions == null) ? 0 : directions.size();

			fireContentsChanged(this, 0, Math.min(oldSize, newSize) - 1);
			if (oldSize > newSize) {
				fireIntervalRemoved(this, newSize, oldSize - 1);
			} else if (oldSize < newSize) {
				fireIntervalAdded(this, oldSize, newSize - 1);
			}
		}

		@Override
		public int getSize() {
			return (directions == null) ? 0 : directions.size();
		}

		@Override
		public Direction getElementAt(int index) {
			return directions.get(index);
		}
	}

	private static class DirectionListRenderer extends
			javax.swing.DefaultListCellRenderer {
		private static final ImageIcon ICON_WEST = new ImageIcon(
				DirectionListRenderer.class.getResource("west.png"));
		private static final ImageIcon ICON_NORTH = new ImageIcon(
				DirectionListRenderer.class.getResource("north.png"));
		private static final ImageIcon ICON_SOUTH = new ImageIcon(
				DirectionListRenderer.class.getResource("south.png"));
		private static final ImageIcon ICON_EAST = new ImageIcon(
				DirectionListRenderer.class.getResource("east.png"));

		@Override
		public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			switch ((Direction) value) {
			case WEST:
				label.setIcon(ICON_WEST);
				break;
			case EAST:
				label.setIcon(ICON_EAST);
				break;
			case NORTH:
				label.setIcon(ICON_NORTH);
				break;
			case SOUTH:
				label.setIcon(ICON_SOUTH);
				break;
			case EAST_NORTH:
			case EAST_SOUTH:
			case WEST_NORTH:
			case WEST_SOUTH:
				label.setIcon(null);
				break;
			}
			return label;
		}
	}
}
