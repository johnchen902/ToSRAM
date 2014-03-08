package tosram.xrobot;

import tosram.RuneMap;

/**
 * A goal for an IDA* robot to make.
 * 
 * @author johnchen902
 */
public interface Goal {
	/**
	 * Get a result about the specified map.
	 * 
	 * @param runemap
	 *            the specified map.
	 * @return a Goal.Result
	 */
	public Goal.Result getResult(RuneMap runemap);

	/**
	 * Get a result about the specified map and the position of the stone held.
	 * 
	 * @param runemap
	 *            the specified map.
	 * @param x
	 *            the X coordinate of the position of the stone held
	 * @param y
	 *            the Y coordinate of the position of the stone held
	 * @return a Goal.Result
	 */
	public Goal.Result getResult(RuneMap runemap, int x, int y);

	/**
	 * A result includes a estimated heuristic cost, a "whether goal is make"
	 * and the next goal to work on.
	 * 
	 * @author johnchen902
	 */
	public static class Result {
		private final int heuristicCostEstimate;
		private final boolean isMade;
		private final Goal next;

		/**
		 * Create a result with the specified estimated heuristic cost, goal is
		 * not made, and no next goal to work on.
		 * 
		 * @param heuristicCostEstimate
		 *            the estimated heuristic cost
		 */
		public Result(int heuristicCostEstimate) {
			this.heuristicCostEstimate = heuristicCostEstimate;
			this.isMade = false;
			this.next = null;
		}

		/**
		 * Create a result with estimated heuristic cost is zero, goal is made,
		 * and the specified next goal to work on.
		 * 
		 * @param next
		 *            the next goal to work on
		 */
		public Result(Goal next) {
			this.heuristicCostEstimate = 0;
			this.isMade = true;
			this.next = next;
		}

		/**
		 * Get the estimated heuristic cost.
		 */
		public int heuristicCostEstimate() {
			return heuristicCostEstimate;
		}

		/**
		 * Get whether goal is made.
		 * 
		 * @return <code>true</code> if goal is made; <code>false</code>
		 *         otherwise.
		 */
		public boolean isMade() {
			return isMade;
		}

		/**
		 * Get the next goal to work on..
		 */
		public Goal getNext() {
			return next;
		}
	}
}
