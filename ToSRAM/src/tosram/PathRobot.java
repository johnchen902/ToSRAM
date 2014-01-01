package tosram;

/**
 * Construct a path from a map of stones.
 * 
 * @author johnchen902
 */
public interface PathRobot {
	/**
	 * Construct a path from the specified map of stones.
	 * 
	 * @param stones
	 *            the map of stones
	 * @return the path constructed
	 */
	public Path getPath(RuneMap stones);

	/**
	 * Set the <code>StatusListener</code> to <code>listener</code>.
	 * 
	 * @param listener
	 *            the <code>StatusListener</code> being set; can be
	 *            <code>null</code>
	 */
	public void setStatusListener(StatusListener listener);

	/**
	 * A callback when status change.
	 * 
	 * @author johnchen902
	 */
	public interface StatusListener {
		/**
		 * Called when progress is made.
		 * 
		 * @param progress
		 *            a <code>double</code>. <code>0.0</code> is nothing done.
		 *            <code>1.0</code> is all done.
		 */
		public void updateProgress(double progress);

		/**
		 * Called when a significant progress is made, such as finding a better
		 * solution.
		 * 
		 * @param milestone
		 *            a string indicate the significant progress
		 */
		public void updateMilestone(String milestone);
	}
}
