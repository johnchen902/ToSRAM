package tosram.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.*;

import tosram.*;
import tosram.strategy.ImprovementStrategy;
import tosram.strategy.SearchStrategy;
import tosram.strategy.SolutionStrategy;
import tosram.strategy.StrategySearchPathRobot;
import tosram.view.strategy.StrategyPanel;

/**
 * The frame shown on the screen. Please don't try to serialize me. I don't
 * regard the serialized form.
 * 
 * @author johnchen902
 */
/*-
 * Life cycle:
 *                     next|back|auto|cancel|
 * 1. To get rectangle   2 |    |    |      |
 * 2. Getting rectangle    |    |  4 |  1   |
 * 3. To get RuneMap     4 |  1 |    |      |
 * 4. Getting RuneMap      |    |  5 |      |   
 * 5. To compute         6 |  3 |    |      |
 * 6. Computing            |    |  7 |  7   |
 * 7. To move stone      8 |  5 |    |      |
 * 8. Moving stone         |    |  3 |      |
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private enum LifeCycle {
		TO_GET_RECT, GETTING_RECT, TO_GET_MAP, GETTING_MAP, TO_COMPUTE, COMPUTING, TO_MOVE, MOVING,
	}

	private LifeCycle state;
	private Rectangle mapArea;
	private RuneMap realMap;
	private Path path;
	private ComputionWorker computionWorker; // to be interrupted

	private JPanel pnMain;
	private JPanel pnSide;

	private JProgressBar pbProgress; // progress of computing
	private JLabel lbStatus; // general states and computing milestones
	private JLabel lbTime; // time spent in computing
	private JLabel lbStartPoint; // starting point of path
	private PathPanel pnPath; // path drawn above tbStones
	private RuneMapTable tbStones;
	private DirectionList listDir;

	private JButton btnNext; // the "Next" button
	private JButton btnBack; // the "Back" button
	private JButton btnInterrupt; // the "Interrupt" button
	private JCheckBox chckbxSettings; // toggle settings

	private StrategyPanel strategyList;

	private final Robot awtRobot;

	private static final String timeFormatString = "#.### seconds";
	private static final NumberFormat timeFormat = new DecimalFormat(
			timeFormatString);

	/**
	 * Create a <code>MainFrame</code>
	 */
	public MainFrame() {
		super("Tower of Savior Runestone Auto Mover");

		initUI();

		try {
			awtRobot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		state = LifeCycle.TO_GET_RECT;
		setStatus("To get rectangle...");
		// btnNext.setEnabled(true);
		btnBack.setEnabled(false);
		btnInterrupt.setEnabled(false);
	}

	private void initUI() {
		initMainPanel();
		initSidePanel();

		hideSettings();

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

			pbProgress = new JProgressBar();
			pbProgress.setStringPainted(true);
			pbProgress.setMaximum(10000);
			pbProgress.setFont(pbProgress.getFont().deriveFont(24f));
			pnNorth.add(pbProgress);

			lbStatus = new JLabel();
			pnNorth.add(lbStatus);

			{
				JPanel pnLine3 = new JPanel();
				pnNorth.add(pnLine3);
				pnLine3.setLayout(new GridLayout(1, 2, 5, 0));

				lbTime = new JLabel();
				lbTime.setFont(lbTime.getFont().deriveFont(24f));
				pnLine3.add(lbTime);

				lbStartPoint = new JLabel();
				lbStartPoint.setFont(lbStartPoint.getFont().deriveFont(24f));
				pnLine3.add(lbStartPoint);
			}
		}
		{
			JPanel pnCenter = new JPanel();
			pnMain.add(pnCenter, BorderLayout.CENTER);
			pnCenter.setLayout(new GridLayout(1, 0, 5, 0));

			{
				JLayeredPane layeredPane = new JLayeredPane();
				pnCenter.add(layeredPane);

				pnPath = new PathPanel();
				pnPath.setBounds(0, 0, 189, 231);
				layeredPane.add(pnPath, Integer.valueOf(2));

				tbStones = new RuneMapTable();
				tbStones.setBounds(0, 0, 189, 231);
				layeredPane.add(tbStones, Integer.valueOf(1));
				tbStones.setFont(tbStones.getFont().deriveFont(24f));
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				pnCenter.add(scrollPane);
				listDir = new DirectionList();
				scrollPane.setViewportView(listDir);
				listDir.setFont(listDir.getFont().deriveFont(24f));
			}
		}
		{
			JPanel pnSouth = new JPanel();
			pnMain.add(pnSouth, BorderLayout.SOUTH);

			btnNext = new JButton("Next");
			pnSouth.add(btnNext);
			btnNext.addActionListener(new NextActionListener());

			btnBack = new JButton("Back");
			pnSouth.add(btnBack);
			btnBack.addActionListener(new BackActionListener());

			btnInterrupt = new JButton("Interrupt");
			pnSouth.add(btnInterrupt);
			btnInterrupt.addActionListener(new CancelActionListener());

			chckbxSettings = new JCheckBox("Settings");
			pnSouth.add(chckbxSettings);
			chckbxSettings.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (chckbxSettings.isSelected())
						showSettings();
					else
						hideSettings();
				}
			});
		}
	}

	private void initSidePanel() {
		pnSide = new JPanel(new GridLayout(2, 1));
		{
			strategyList = new StrategyPanel();
			pnSide.add(strategyList);
		}
		{
			RuneStone.Type[] types = RuneStone.Type.values();
			RuneMap map0 = new RuneMap(2, types.length);

			int i = 0;
			for (RuneStone.Type type : types) {
				map0.setRuneStone(0, i, new RuneStone(type, false));
				map0.setRuneStone(1, i, new RuneStone(type, true));
				i++;
			}

			RuneMapTable rmt = new RuneMapTable();
			rmt.setAcceptDrop(false);
			rmt.setRuneMap(map0);
			pnSide.add(new JScrollPane(rmt));
		}
	}

	private void showSettings() {
		int width0 = pnMain.getWidth();
		getContentPane().removeAll();
		JSplitPane sp = new JSplitPane();
		sp.setBorder(null);
		sp.setLeftComponent(pnMain);
		sp.setRightComponent(pnSide);
		sp.setDividerLocation(width0);
		sp.setResizeWeight(2.0 / 3.0);
		getContentPane().add(sp);
		setSize(600, 400);
		validate();
	}

	private void hideSettings() {
		getContentPane().removeAll();
		getContentPane().add(pnMain);
		setSize(400, 400);
		validate();
	}

	private class NextActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (state) {
			case TO_GET_RECT:
				btnNext.setEnabled(false);
				state = LifeCycle.GETTING_RECT;
				setStatus("Getting rectangle...");
				Rectangle screenArea = GraphicsEnvironment
						.getLocalGraphicsEnvironment().getDefaultScreenDevice()
						.getDefaultConfiguration().getBounds();
				mapArea = SubimageChooser.showRectangleDialog(
						awtRobot.createScreenCapture(screenArea),
						"Where are the runestones? Drag a rectangle.");
				if (mapArea == null) {
					state = LifeCycle.TO_GET_RECT;
					setStatus("To get rectangle...");
					btnNext.setEnabled(true);
				} else {
					state = LifeCycle.GETTING_MAP;
					setStatus("Getting map...");
					setPath(null);
					new ReadStoneWorker().execute();
				}
				break;
			case TO_GET_MAP:
				btnNext.setEnabled(false);
				btnBack.setEnabled(false);
				state = LifeCycle.GETTING_MAP;
				setStatus("Getting map...");
				setPath(null);
				new ReadStoneWorker().execute();
				break;
			case TO_COMPUTE:
				btnNext.setEnabled(false);
				btnBack.setEnabled(false);
				btnInterrupt.setEnabled(true);
				state = LifeCycle.COMPUTING;
				setStatus("Computing...");
				(computionWorker = new ComputionWorker()).execute();
				break;
			case TO_MOVE:
				btnNext.setEnabled(false);
				btnBack.setEnabled(false);
				state = LifeCycle.MOVING;
				setStatus("Moving...");
				new MoveWorker().execute();
				break;
			default:
				throw new IllegalStateException("Unexpected state " + state);
			}
		}
	}

	private class BackActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (state) {
			case TO_GET_MAP:
				btnBack.setEnabled(false);
				state = LifeCycle.TO_GET_RECT;
				mapArea = null;
				setStatus("To get rectangle...");
				break;
			case TO_COMPUTE:
				state = LifeCycle.TO_GET_MAP;
				realMap = null;
				setRuneMapShown(null);
				setStatus("To get map...");
				break;
			case TO_MOVE:
				state = LifeCycle.TO_COMPUTE;
				setRuneMapShown(realMap);
				setPath(null);
				setStatus("To compute...");
				break;
			default:
				throw new IllegalStateException("Unexpected state " + state);
			}
		}
	}

	private class CancelActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (state) {
			case COMPUTING:
				computionWorker.interrupt();
				break;
			default:
				throw new IllegalStateException("Unexpected state " + state);
			}
		}
	}

	private class ReadStoneWorker extends SwingWorker<RuneMap, RuneMap> {

		private static final int DELAY_BETWEEN_READ_STONE = 500;
		private final RuneStoneGetter rsg = new DefaultRuneStoneGetter();

		@Override
		protected RuneMap doInBackground() throws Exception {
			// get RuneMap until the map become fixed

			RuneMap map = rsg.getRuneStones(awtRobot
					.createScreenCapture(mapArea));

			while (true) {
				awtRobot.delay(DELAY_BETWEEN_READ_STONE);
				RuneMap prevmap = map;
				map = rsg.getRuneStones(awtRobot.createScreenCapture(mapArea));

				publish(map);

				int xCount = 0;
				for (int y = 0; y < map.getHeight(); y++)
					for (int x = 0; x < map.getWidth(); x++)
						if (map.getStone(x, y).getType() == RuneStone.Type.UNKNOWN)
							xCount++;
				if (xCount < map.getWidth() * map.getHeight() / 2
						&& map.equals(prevmap))
					break;
			}

			return map;
		}

		@Override
		protected void process(List<RuneMap> chunks) {
			setRuneMapShown(chunks.get(chunks.size() - 1));
		}

		@Override
		protected void done() {
			try {
				setRuneMapShown(realMap = this.get());
			} catch (Exception ignore) {
				ignore.printStackTrace();
			}
			state = LifeCycle.TO_COMPUTE;
			setStatus("To compute...");
			btnNext.setEnabled(true);
			btnBack.setEnabled(true);
		}

	}

	private class ComputionWorker extends SwingWorker<Path, Object> implements
			PathRobot.StatusListener {

		private final PathRobot pathRobot;
		private double computeTime;
		private String finalStatus = "no result";
		private Thread thread;

		public ComputionWorker() {
			SearchStrategy ses = new ImprovementStrategy();
			SolutionStrategy ss = strategyList.createStrategy();
			pathRobot = new StrategySearchPathRobot(ses, ss);
		}

		public void interrupt() {
			thread.interrupt();
		}

		@Override
		protected Path doInBackground() throws Exception {
			thread = Thread.currentThread();

			pathRobot.setStatusListener(this);

			long tBegin = System.nanoTime();
			Path path = pathRobot.getPath(realMap);
			long tEnd = System.nanoTime();

			computeTime = (tEnd - tBegin) * 1e-9;

			return path;
		}

		@Override
		public void updateProgress(double progress) {
			publish(progress);
		}

		@Override
		public void updateMilestone(String milestone) {
			publish(milestone);
		}

		@Override
		protected void process(List<Object> chunks) {
			double max = -1;
			for (Object o : chunks) {
				if (o instanceof Double) {
					max = Math.max(max, (double) o);
				} else {
					setStatus(finalStatus = o.toString());
				}
			}
			if (max != -1)
				MainFrame.this.setProgress(max);
		}

		@Override
		protected void done() {
			try {
				Path p = get();
				setPath(p);
				setRuneMapShown(Paths.follow(realMap, p));
			} catch (Exception ignore) {
				ignore.printStackTrace();
			}
			state = LifeCycle.TO_MOVE;
			computionWorker = null;
			setTime(computeTime);
			setStatus("Computed: " + finalStatus);
			btnNext.setEnabled(true);
			btnBack.setEnabled(true);
			btnInterrupt.setEnabled(false);
		}
	}

	private class MoveWorker extends SwingWorker<Void, Void> {

		private static final int DELAY_BEFORE_TAKEOVER = 400;
		private static final int DELAY_AFTER_PRESS = 3000;
		private static final int DELAY_AFTER_MOVE = 90;
		private static final int DELAY_BEFORE_RELEASE = 100;
		private static final int DELAY_AFTER_RELEASE = 100;

		@Override
		protected Void doInBackground() throws Exception {
			// hold the mouse and move
			Rectangle r = mapArea;
			awtRobot.delay(DELAY_BEFORE_TAKEOVER);
			int x = path.getBeginPoint().x, y = path.getBeginPoint().y;
			{
				// move to start point
				int px = r.x + r.width * (2 * x + 1) / 2 / realMap.getWidth();
				int py = r.y + r.height * (2 * y + 1) / 2 / realMap.getHeight();
				awtRobot.mouseMove(px, py);
				awtRobot.delay(DELAY_AFTER_MOVE);
			}
			// press
			awtRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			awtRobot.delay(DELAY_AFTER_PRESS);
			// follow the path
			for (Direction dir : path.getDirections()) {
				switch (dir) {
				case WEST:
				case WEST_NORTH:
				case WEST_SOUTH:
					x--;
					break;
				case EAST:
				case EAST_NORTH:
				case EAST_SOUTH:
					x++;
					break;
				case NORTH:
				case SOUTH:
					break;
				}
				switch (dir) {
				case SOUTH:
				case WEST_SOUTH:
				case EAST_SOUTH:
					y++;
					break;
				case NORTH:
				case WEST_NORTH:
				case EAST_NORTH:
					y--;
					break;
				case WEST:
				case EAST:
					break;
				}

				int px = r.x + r.width * (2 * x + 1) / 2 / realMap.getWidth();
				int py = r.y + r.height * (2 * y + 1) / 2 / realMap.getHeight();
				awtRobot.mouseMove(px, py);
				awtRobot.delay(DELAY_AFTER_MOVE);
			}
			// release
			awtRobot.delay(DELAY_BEFORE_RELEASE);
			awtRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			awtRobot.delay(DELAY_AFTER_RELEASE);
			Point p = new Point(getWidth() / 2, getHeight() / 2);
			SwingUtilities.convertPointToScreen(p, MainFrame.this);
			awtRobot.mouseMove(p.x, p.y);
			return null;
		}

		@Override
		protected void done() {
			state = LifeCycle.GETTING_MAP;
			setStatus("Getting map...");
			setPath(null);
			new ReadStoneWorker().execute();
		}

	}

	private void setStatus(String status) {
		// calculate font size
		Font font = lbStatus.getFont();
		int textW = Integer.MAX_VALUE;
		int maxWidth = lbStatus.getWidth();
		for (int k = 24; textW > maxWidth && k > 0; k--) {
			font = font.deriveFont((float) k);
			textW = lbStatus.getFontMetrics(font).stringWidth(status);
		}
		// set
		lbStatus.setText(status);
		lbStatus.setFont(font);
	}

	private void setProgress(double value) {
		int ivalue = (int) (value * pbProgress.getMaximum());
		if (pbProgress.getValue() != ivalue) {
			pbProgress.setValue(ivalue);
			pbProgress.repaint();
		}
	}

	private void setTime(double seconds) {
		if (!Double.isNaN(seconds))
			lbTime.setText(timeFormat.format(seconds));
		else
			lbTime.setText(timeFormatString);
	}

	private void setPath(Path path) {
		this.path = path;
		if (path != null) {
			Point point = path.getBeginPoint();
			lbStartPoint.setText("Start: (" + (point.x + 1) + ", "
					+ (point.y + 1) + ")");
			listDir.setDirections(path.getDirections());
			pnPath.setPath(path);
		} else {
			lbStartPoint.setText("Start:");
			listDir.setDirections(null);
			pnPath.setPath(null);
		}
	}

	private void setRuneMapShown(RuneMap map) {
		tbStones.setRuneMap(map);
		if (map != null) {
			float tableWidth = tbStones.getWidth();
			float tableHeight = 0f;
			for (int row = 0; row < tbStones.getRowCount(); row++)
				tableHeight += tbStones.getRowHeight(row);
			pnPath.setCellSize(tableWidth / tbStones.getColumnCount(),
					tableHeight / tbStones.getRowCount());
		}
	}

}
