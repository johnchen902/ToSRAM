package tosram.view.strategy;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import org.javatuples.LabelValue;

import tosram.PathRobot;
import tosram.strategy.StrategySearchPathRobot;
import tosram.view.PathRobotCreater;

/**
 * A <code>PathRobotCreater</code> that creates a
 * {@link StrategySearchPathRobot}.
 * 
 * @author johnchen902
 */
public class StrategyRobotCreater implements PathRobotCreater {

	private StrategyPanel strategyPanel;
	private SearchStrategyPanel searchPanel;

	private void initialize() {
		if (strategyPanel != null)
			return;
		strategyPanel = new StrategyPanel();
		searchPanel = new SearchStrategyPanel();
	}

	@Override
	public PathRobot createPathRobot() {
		initialize();
		return new StrategySearchPathRobot(searchPanel.createSearchStrategy(),
				strategyPanel.createStrategy());
	}

	@Override
	public List<LabelValue<String, Component>> getSettingsTabs() {
		initialize();
		return Arrays.asList(
				LabelValue.with("Strategies", (Component) strategyPanel),
				LabelValue.with("Searching", (Component) searchPanel));
	}

	@Override
	public String toString() {
		return "Strategy Robot";
	}
}
