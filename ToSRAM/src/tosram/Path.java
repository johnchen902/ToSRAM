package tosram;

import java.awt.Point;
import java.util.List;

/**
 * Path to move stones.
 * 
 * @author johnchen902
 */
public interface Path {

	/**
	 * The starting point of the path.
	 * 
	 * @return the starting point
	 */
	public Point getBeginPoint();

	/**
	 * Get directions of the path.
	 * 
	 * @return the directions
	 */
	public List<Direction> getDirections();
}
