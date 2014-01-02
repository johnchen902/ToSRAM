package tosram.view.strategy;

import java.awt.Component;

import javax.swing.ListCellRenderer;

import tosram.strategy.SolutionStrategy;

/**
 * A <code>StrategyCreater</code> can create a <code>Strategy</code> and
 * render itself.
 * 
 * @author johnchen902
 */
public interface StrategyCreater extends ListCellRenderer<StrategyCreater> {

	/**
	 * Create a <code>Strategy</code> which tie is broken with the
	 * specified strategy.
	 * 
	 * @param next
	 *            the strategy used to break tie
	 * @return constructed strategy
	 */
	public SolutionStrategy createStrategy(SolutionStrategy next);

	/**
	 * Show settings, usually a dialog.
	 * 
	 * @param parent
	 *            the parent if a dialog is required
	 */
	public void settings(Component parent);
}
