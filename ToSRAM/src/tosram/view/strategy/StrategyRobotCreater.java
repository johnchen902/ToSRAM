package tosram.view.strategy;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;

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
	public Map<String, Component> getSettingsTabs() {
		initialize();
		Map<String, Component> map = new LinkedHashMap<String, Component>();
		map.put("Strategies", strategyPanel);
		map.put("Searching", searchPanel);
		return map;
	}

	@Override
	public String toString() {
		return "Strategy Robot";
	}
}