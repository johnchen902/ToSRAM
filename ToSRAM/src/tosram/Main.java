package tosram;

import javax.swing.SwingUtilities;

import tosram.view.MainFrame;

/**
 * The class who contains the <code>main</code> method.
 * 
 * @author johnchen902
 */
public class Main {

	private Main() {
	}

	/**
	 * Simply <code>main</code>.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainFrame();
			}
		});
	}
}
