package tosram.view;

import java.awt.Component;
import java.util.List;
import org.javatuples.LabelValue;

import tosram.PathRobot;

/**
 * An interface that creates {@link PathRobot}.
 * 
 * @author johnchen902
 */
public interface PathRobotCreater {

	/**
	 * Get the settings components.
	 * 
	 * @return some title-component pair
	 */
	public List<LabelValue<String, Component>> getSettingsTabs();

	/**
	 * Create the <code>PathRobot</code>
	 * 
	 * @return a <code>PathRobot</code>
	 */
	public PathRobot createPathRobot();
}
