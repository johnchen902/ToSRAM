package tosram;

/**
 * Direction when moving a stone.
 * 
 * @author johnchen902
 */
public enum Direction {

	/**
	 * To move stone left
	 */
	WEST,

	/**
	 * To move stone right
	 */
	EAST,

	/**
	 * To move stone up
	 */
	NORTH,

	/**
	 * To move stone down
	 */
	SOUTH,

	/**
	 * To move stone left-down
	 */
	WEST_SOUTH,

	/**
	 * To move stone left-up
	 */
	WEST_NORTH,

	/**
	 * To move stone right-down
	 */
	EAST_SOUTH,

	/**
	 * To move stone right-up
	 */
	EAST_NORTH;

	/**
	 * Returns the opposite direction.
	 * <ul>
	 * <li>{@link #WEST} &lt;--&gt; {@link #EAST}</li>
	 * <li>{@link #NORTH} &lt;--&gt; {@link #SOUTH}</li>
	 * <li>{@link #WEST_SOUTH} &lt;--&gt; {@link #EAST_NORTH}</li>
	 * <li>{@link #WEST_NORTH} &lt;--&gt; {@link #EAST_SOUTH}</li>
	 * </ul>
	 * 
	 * @return the opposite direction
	 */
	public Direction getOppsite() {
		switch (this) {
		case WEST:
			return EAST;
		case EAST:
			return WEST;
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case WEST_SOUTH:
			return EAST_NORTH;
		case WEST_NORTH:
			return EAST_SOUTH;
		case EAST_SOUTH:
			return WEST_NORTH;
		case EAST_NORTH:
			return WEST_SOUTH;
		}
		throw new AssertionError(this);
	}

	/**
	 * Get the difference of X coordinate implied by this direction.
	 * <ul>
	 * <li><code>+1</code>: {@link #EAST}, {@link #EAST_NORTH} and
	 * {@link #EAST_SOUTH}</li>
	 * <li><code>0</code>: {@link #NORTH} and {@link #SOUTH}</li>
	 * <li><code>-1</code>: {@link #WEST}, {@link #WEST_NORTH} and
	 * {@link #WEST_SOUTH}</li>
	 * </ul>
	 * 
	 * @return the difference of X coordinate implied by this direction
	 */
	public int getX() {
		switch (this) {
		case WEST:
		case WEST_NORTH:
		case WEST_SOUTH:
			return -1;
		case EAST:
		case EAST_NORTH:
		case EAST_SOUTH:
			return +1;
		case NORTH:
		case SOUTH:
			return 0;
		}
		throw new AssertionError(this);
	}

	/**
	 * Get the difference of Y coordinate implied by this direction.
	 * <ul>
	 * <li><code>+1</code>: {@link #SOUTH}, {@link #WEST_SOUTH} and
	 * {@link #EAST_SOUTH}</li>
	 * <li><code>0</code>: {@link #WEST} and {@link #EAST}</li>
	 * <li><code>-1</code>: {@link #NORTH}, {@link #WEST_NORTH} and
	 * {@link #EAST_NORTH}</li>
	 * </ul>
	 * 
	 * @return the difference of Y coordinate implied by this direction
	 */
	public int getY() {
		switch (this) {
		case SOUTH:
		case WEST_SOUTH:
		case EAST_SOUTH:
			return +1;
		case NORTH:
		case WEST_NORTH:
		case EAST_NORTH:
			return -1;
		case WEST:
		case EAST:
			return 0;
		}
		throw new AssertionError(this);
	}

	/**
	 * Determines if this direction is diagonal.
	 * <ul>
	 * <li><code>true</code>: {@link #WEST_SOUTH}, {@link #WEST_NORTH},
	 * {@link #EAST_SOUTH} and {@link #EAST_NORTH}</li>
	 * <li><code>false</code>: {@link #WEST}, {@link #EAST}, {@link #NORTH} and
	 * {@link #SOUTH}</li>
	 * </ul>
	 * 
	 * @return <code>true</code> if and only if this direction is diagonal
	 */
	public boolean isDiagonal() {
		return this.ordinal() >= 4;
	}
}
