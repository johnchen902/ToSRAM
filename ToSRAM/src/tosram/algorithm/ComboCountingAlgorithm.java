package tosram.algorithm;

import java.awt.Point;
import java.util.List;

import tosram.MutableRuneMap;
import tosram.RuneStone;

/**
 * An algorithm to count combo of a <code>MutableRuneMap</code>.
 * 
 * @author johnchen902
 */
public interface ComboCountingAlgorithm {

	/**
	 * The detail of a combo.
	 * 
	 * @author johnchen902
	 */
	public abstract class Combo {
		/**
		 * Get on which batch this combo is made.
		 * 
		 * @return the batch this combo is made, starting from zero
		 */
		public abstract int getBatch();

		/**
		 * Get the <code>RuneStone</code> that made this combo
		 * 
		 * @return a <code>RuneStone</code>
		 */
		public abstract RuneStone getType();

		/**
		 * Get the number of <code>RuneStone</code> dissolved in this combo.
		 * 
		 * @return the number dissolved
		 */
		public abstract int getCount();

		/**
		 * Get the locations where <code>RuneStone</code> were dissolved.
		 * 
		 * @return a list of locations
		 */
		public abstract List<Point> getPoints();

		@Override
		public String toString() {
			return String.format(
					"Combo[batch=%d, type=%s, count=%d, points=%s]",
					getBatch(), getType(), getCount(), getPoints());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Combo))
				return false;
			Combo other = (Combo) obj;
			if (getBatch() != other.getBatch())
				return false;
			if (getType() != other.getType())
				return false;
			if (getCount() != other.getCount())
				return false;
			if (!getPoints().equals(other.getPoints()))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getBatch();
			result = prime * result + getType().hashCode();
			result = prime * result + getPoints().hashCode();
			return result;
		}
	}

	/**
	 * Count the combo of the <code>MutableRuneMap</code> under Tower of Savior
	 * rules.
	 * 
	 * @param map
	 *            the <code>MutableRuneMap</code>
	 * @return the combo
	 */
	public List<Combo> countCombo(MutableRuneMap map);
}
