package tosram.xrobot;

import tosram.RuneMap;

/**
 * A factory that force the path to be ended at the top.
 * 
 * @author johnchen902
 */
public class EndAtTopGoalSeriesFactory implements GoalSeriesFactory {

	private GoalSeriesFactory delegate;

	/**
	 * Construct a EndAtTopGoalSeriesFactory with specified delegation
	 * 
	 * @param delegate
	 *            the specified delegation
	 */
	public EndAtTopGoalSeriesFactory(GoalSeriesFactory delegate) {
		this.delegate = delegate;
	}

	@Override
	public Goal[] createGoalSeries(RuneMap initialMap) {
		Goal[] goals = delegate.createGoalSeries(initialMap);
		return new Goal[] { new EndAtTopGoal(goals[0]),
				new EndAtTopGoal(goals[1]) };
	}

	@Override
	public String describeGoal(Goal goal, Goal finalGoal) {
		return delegate.describeGoal(((EndAtTopGoal) goal).delegate,
				((EndAtTopGoal) finalGoal).delegate);
	}

	private static class EndAtTopGoal implements Goal {

		private Goal delegate;

		public EndAtTopGoal(Goal delegate) {
			if (delegate == null)
				throw new IllegalArgumentException("cannot be null");
			this.delegate = delegate;
		}

		@Override
		public Result getResult(RuneMap runemap) {
			return delegate.getResult(runemap);
		}

		@Override
		public Result getResult(RuneMap runemap, int x, int y) {
			Result result = delegate.getResult(runemap, x, y);
			if (result.isMade() && y == 0) {
				return new Result(new EndAtTopGoal(result.getNext()));
			} else {
				return new Result(result.heuristicCostEstimate() + y);
			}
		}

		@Override
		public int hashCode() {
			return delegate.hashCode() + 31;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof EndAtTopGoal
					&& delegate.equals(((EndAtTopGoal) obj).delegate);
		}
	}
}
