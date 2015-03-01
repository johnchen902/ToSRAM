package tosram.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
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
import tosram.algorithm.PathFindingAlgorithm;

/**
 * The frame shown on the screen.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final String STATUS_IDLE = "Idle";
	private static final String STATUS_EDITTING = "Editting";
	private static final String STATUS_COMPUTING = "Computing";
	private static final String STATUS_NOW = "Now";
	private static final String STATUS_ERROR = "Error";

	private static final String BUTTON_EDIT = "Edit";
	private static final String BUTTON_FINISH = "Finish";
	private static final String BUTTON_COMPUTE = "Compute";
	private static final String BUTTON_STOP = "Stop";
	private static final String BUTTON_SETTINGS = "Settings";

	private JPanel pnMain;
	private JLabel lbStatus; // general states and computing milestones
	private PathPanel pnPath; // path drawn above tbStones
	private RuneMapTable tbStones;
	private Settings settings;

	private JPanel buttonsPanel;
	private JButton btEdit;
	private JButton btCompute;
	private JButton btStop;
	private JButton btSettings;

	private RuneMap runeMap;
	private PathFindingAlgorithm algorithm;

	public MainFrame() {
		super("Tower of Savior Runestone Auto Mover");

		initUI();

		settings = new Settings();
	}

	private void initUI() {
		initMainPanel();

		getContentPane().add(pnMain);
		setSize(400, 400);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		setVisible(true);
	}

	private void initMainPanel() {
		pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		{
			JPanel pnNorth = new JPanel();
			pnMain.add(pnNorth, BorderLayout.NORTH);
			pnNorth.setLayout(new GridLayout(0, 1, 0, 0));

			lbStatus = new JLabel();
			pnNorth.add(lbStatus);
			lbStatus.setText(STATUS_IDLE);
			lbStatus.setFont(lbStatus.getFont().deriveFont(24f));
			lbStatus.setHorizontalAlignment(JLabel.CENTER);
		}
		{
			JPanel pnCenter = new JPanel();
			pnMain.add(pnCenter, BorderLayout.CENTER);
			pnCenter.setLayout(new GridLayout(1, 0, 5, 0));

			{
				JLayeredPane layeredPane = new JLayeredPane();
				pnCenter.add(layeredPane);
				layeredPane.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						Component c = e.getComponent();
						pnPath.setBounds(c.getBounds());
						pnPath.setCellSize(c.getWidth() / 6, c.getHeight() / 5);
						tbStones.setBounds(c.getBounds());
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
		}
		{
			buttonsPanel = new JPanel();
			pnMain.add(buttonsPanel, BorderLayout.SOUTH);

			btEdit = new JButton(BUTTON_EDIT);
			buttonsPanel.add(btEdit);
			btEdit.addActionListener(e -> {
				if (btEdit.getText().equals(BUTTON_EDIT))
					startEditing();
				else
					finishEditing();
			});

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
	}

	private void startEditing() {
		btEdit.setText(BUTTON_FINISH);
		tbStones.setRenderer(RuneButton.factory(tbStones));
		lbStatus.setText(STATUS_EDITTING);
		btCompute.setEnabled(false);
		pnPath.setPath(null);
		tbStones.setRuneMap(runeMap);
		btSettings.setEnabled(false);
	}

	private void finishEditing() {
		btEdit.setText(BUTTON_EDIT);
		tbStones.setRenderer(RuneLabel.factory());
		lbStatus.setText(STATUS_IDLE);
		btCompute.setEnabled(true);
		runeMap = tbStones.getRuneMap();
		btSettings.setEnabled(true);
	}

	private void startComputing() {
		lbStatus.setText(STATUS_COMPUTING);
		btEdit.setEnabled(false);
		btCompute.setEnabled(false);
		btStop.setEnabled(true);
		pnPath.setPath(null);
		algorithm = settings.getAlgorithm();
		btSettings.setEnabled(false);
		new SwingWorker<Void, Object[]>() {
			@Override
			protected Void doInBackground() {
				algorithm.findPath(runeMap, (p, s) -> publish(new Object[] { p,
						s }));
				return null;
			}

			@Override
			protected void process(List<Object[]> chunks) {
				Object[] pair = chunks.get(chunks.size() - 1);
				Path path = (Path) pair[0];
				pnPath.setPath(path);
				lbStatus.setText(STATUS_NOW + ": " + pair[1]);
				tbStones.setRuneMap(Path.follow(runeMap, path));
			}

			@Override
			protected void done() {
				stopComputing();
				try {
					get();
				} catch (ExecutionException e) {
					lbStatus.setText(STATUS_ERROR + ": " + e.getCause());
				} catch (InterruptedException e) {
				}
			}
		}.execute();
	}

	private void stopComputing() {
		if (btStop.isEnabled()) {
			btEdit.setEnabled(true);
			btCompute.setEnabled(true);
			btStop.setEnabled(false);
			algorithm.stop();
			btSettings.setEnabled(true);
		}
	}

	private void showSettings() {
		int result = JOptionPane.showConfirmDialog(this,
				settings.getEditorPanel(), "Settings",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION)
			settings.commit();
		else
			settings.cancel();
	}
}
