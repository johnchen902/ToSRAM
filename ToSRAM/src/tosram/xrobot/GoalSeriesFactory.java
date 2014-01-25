package tosram.xrobot;

import tosram.RuneMap;

/**
 * A factory that defines a series of goal to make.
 * 
 * @author johnchen902
 */
public interface GoalSeriesFactory {
	/**
	 * Produce the series of goal to make.
	 * 
	 * @param initialMap
	 *            the initial map
	 * @return an array of goal with two element:
	 *         <code>{ <i>initial goal</i>, <i>final goal</i> }</code>
	 */
	public Goal[] createGoalSeries(RuneMap initialMap);

	/**
	 * Describe the goal in same series that it had produced with a user
	 * readable form.
	 * 
	 * @param goal
	 *            the goal currently made
	 * @param finalGoal
	 *            the final goal
	 * @return a user readable string
	 */
	public String describeGoal(Goal goal, Goal finalGoal);
}
