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
}
