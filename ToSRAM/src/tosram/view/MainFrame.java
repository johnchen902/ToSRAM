package tosram.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import tosram.Path;
import tosram.RuneMap;
import tosram.algorithm.PathFinder;

/**
 * The frame containing most of the user interface of this program.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final String STATUS_IDLE = "Idle";
	private static final String STATUS_EDITTING = "Editting";
	private static final String STATUS_COMPUTING = "Computing";
	private static final String STATUS_ERROR = "Error";

	private static final String BUTTON_EDIT = "Edit";
	private static final String BUTTON_FINISH = "Finish";
	private static final String BUTTON_COMPUTE = "Compute";
	private static final String BUTTON_STOP = "Stop";
	private static final String BUTTON_SETTINGS = "Settings";

	private JLabel lbStatus;
	private RuneMapTable tbStones;
	private PathPanel pnPath;
	private Settings settings;
	private JButton btEdit;
	private JButton btCompute;
	private JButton btStop;
	private JButton btSettings;

	private RuneMap runeMap;
	private PathFinder pathFinder;

	public MainFrame() {
		super("Tower of Savior Runestone Auto Mover");

		JPanel pnMain = createMainPanel();

		getContentPane().add(pnMain);
		setSize(400, 400);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		setVisible(true);

		settings = new Settings();
	}

	private JPanel createMainPanel() {
		JPanel pnMain = new JPanel(new BorderLayout());

		{
			lbStatus = new JLabel();
			pnMain.add(lbStatus, BorderLayout.NORTH);
			lbStatus.setText(STATUS_IDLE);
			lbStatus.setFont(lbStatus.getFont().deriveFont(24f));
			lbStatus.setHorizontalAlignment(JLabel.CENTER);
		}
		{
			JLayeredPane layeredPane = new JLayeredPane();
			pnMain.add(layeredPane, BorderLayout.CENTER);
			layeredPane.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					Component c = e.getComponent();
					pnPath.setBounds(new Rectangle(c.getSize()));
					pnPath.setCellSize(c.getWidth() / 6, c.getHeight() / 5);
					tbStones.setBounds(new Rectangle(c.getSize()));
					c.validate();
				}
			});

			pnPath = new PathPanel();
			layeredPane.add(pnPath, Integer.valueOf(2));

			tbStones = new RuneMapTable();
			layeredPane.add(tbStones, Integer.valueOf(1));
			tbStones.setFont(tbStones.getFont().deriveFont(24f));
			runeMap = tbStones.getRuneMap();
		}
		{
			JPanel buttonsPanel = new JPanel();
			pnMain.add(buttonsPanel, BorderLayout.SOUTH);

			btEdit = new JButton(BUTTON_EDIT);
			buttonsPanel.add(btEdit);
			btEdit.addActionListener(e -> toggleEditing());

			btCompute = new JButton(BUTTON_COMPUTE);
			buttonsPanel.add(btCompute);
			btCompute.addActionListener(e -> startComputing());

			btStop = new JButton(BUTTON_STOP);
			buttonsPanel.add(btStop);
			btStop.addActionListener(e -> stopComputing());
			btStop.setEnabled(false);

			btSettings = new JButton(BUTTON_SETTINGS);
			buttonsPanel.add(btSettings);
			btSettings.addActionListener(e -> showSettings());
		}
		return pnMain;
	}

	private void toggleEnabled(JButton... buttons) {
		for (JButton button : buttons)
			button.setEnabled(!button.isEnabled());
	}

	private void toggleEditing() {
		if (btEdit.getText().equals(BUTTON_EDIT))
			startEditing();
		else
			finishEditing();
	}

	private void startEditing() {
		toggleEnabled(btCompute, btSettings);

		pnPath.setPath(null);
		tbStones.setRuneMap(runeMap);
		btEdit.setText(BUTTON_FINISH);
		tbStones.setRenderer(RuneButton.factory(tbStones));
		lbStatus.setText(STATUS_EDITTING);
	}

	private void finishEditing() {
		toggleEnabled(btCompute, btSettings);

		btEdit.setText(BUTTON_EDIT);
		tbStones.setRenderer(RuneLabel.factory());
		lbStatus.setText(STATUS_IDLE);
		runeMap = tbStones.getRuneMap();
	}

	private void startComputing() {
		toggleEnabled(btEdit, btCompute, btStop, btSettings);

		pnPath.setPath(null);
		lbStatus.setText(STATUS_COMPUTING);
		pathFinder = settings.getAlgorithm();
		new ComputationWorker().execute();
	}

	private void stopComputing() {
		if (btStop.isEnabled()) {
			toggleEnabled(btEdit, btCompute, btStop, btSettings);

			pathFinder.stop();
		}
	}

	private void showSettings() {
		Object editor = settings.getEditorPanel();
		int result = JOptionPane.showConfirmDialog(this, editor, "Settings",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION)
			settings.commit();
		else
			settings.cancel();
	}

	private class ComputationWorker extends SwingWorker<Void, Object[]> {
		@Override
		protected Void doInBackground() {
			pathFinder.findPath(runeMap,
					(path, message) -> publish(new Object[] { path, message }));
			return null;
		}

		@Override
		protected void process(List<Object[]> chunks) {
			Object[] pair = chunks.get(chunks.size() - 1);
			Path path = (Path) pair[0];
			String message = (String) pair[1];
			pnPath.setPath(path);
			lbStatus.setText(message);
			tbStones.setRuneMap(Path.follow(runeMap, path));
			repaint();
		}

		@Override
		protected void done() {
			stopComputing();
			try {
				get();
			} catch (ExecutionException e) {
				e.printStackTrace();
				lbStatus.setText(STATUS_ERROR + ": " + e.getCause());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
