package tosram.view;

import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tosram.*;
import tosram.strategy.SearchStrategy;
import tosram.strategy.SolutionStrategy;
import tosram.view.strategy.SearchStrategyPanel;
import tosram.view.strategy.StrategyPanel;

/**
 * The frame shown on the screen. Please don't try to serialize me. I don't
 * regard the serialized form.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private MFState mfstate;

	private Rectangle mapArea;
	private RuneMap realMap;
	private Path path;

	private JSplitPane splitPane;
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

	private StrategyPanel strategyPane;
	private SearchStrategyPanel searchStrategyPane;
	private int movingTimeLimit;
	private MovingPathGenerator movingPathGenerator;

	private final Robot awtRobot;

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

		btnNext.setEnabled(false);
		btnBack.setEnabled(false);
		btnInterrupt.setEnabled(false);
		transferState(new ToGetRectangleState());
	}

	private void initUI() {
		initMainPanel();
		initSidePanel();

		splitPane = new JSplitPane();
		splitPane.setLeftComponent(pnMain);
		splitPane.setRightComponent(pnSide);
		splitPane.setBorder(null);
		splitPane.setDividerLocation(384);
		splitPane.setResizeWeight(2.0 / 3.0);

		getContentPane().add(splitPane);
		setSize(600, 400);

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
				layeredPane.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						Component c = e.getComponent();
						pnPath.setBounds(c.getBounds());
						tbStones.setBounds(c.getBounds());
						c.validate();
						adjustRuneMapShown();
					}
				});

				pnPath = new PathPanel();
				layeredPane.add(pnPath, Integer.valueOf(2));

				tbStones = new RuneMapTable();
				tbStones.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						tbStones.setFocusable(false);
					}
				});
				tbStones.setFocusable(false);
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

			JLabel lblMap = new JLabel("Map\u2191");
			lblMap.setDisplayedMnemonic('m');
			lblMap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke("alt pressed M"), "editMap");
			lblMap.getActionMap().put("editMap", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					tbStones.setFocusable(true);
					tbStones.requestFocusInWindow();
				}
			});

			pnSouth.add(lblMap);

			btnNext = new JButton("Next");
			btnNext.setMnemonic('n');
			pnSouth.add(btnNext);

			btnBack = new JButton("Back");
			btnBack.setMnemonic('b');
			pnSouth.add(btnBack);

			btnInterrupt = new JButton("Interrupt");
			btnInterrupt.setMnemonic('i');
			pnSouth.add(btnInterrupt);
		}
	}

	private void initSidePanel() {
		pnSide = new JPanel(new GridLayout(1, 1));

		JTabbedPane tabbedPane = new JTabbedPane();
		pnSide.add(tabbedPane);
		{
			strategyPane = new StrategyPanel();
			tabbedPane.addTab("Strategies", strategyPane);
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_S);
		}
		{
			searchStrategyPane = new SearchStrategyPanel();
			tabbedPane.addTab("Searching", searchStrategyPane);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_E);
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
			rmt.setFocusable(false);
			rmt.setEditable(false);
			rmt.setFont(rmt.getFont().deriveFont(24f));
			rmt.setRuneMap(map0);
			tabbedPane.addTab("Edit Map", rmt);
		}
		{
			Box pn = Box.createVerticalBox();

			final PathPanel.AnimationListener al = new PathPanel.AnimationListener() {
				@Override
				public void animationStop() {
					setRuneMapShown(Paths.follow(realMap, path));
				}

				@Override
				public void animationStart() {
					setRuneMapShown(realMap);
				}

				@Override
				public void animate(int index) {
					setRuneMapShown(Paths.follow(realMap, path, index + 1));
				}
			};

			JCheckBox cbx = new JCheckBox("Animate stones");
			cbx.setAlignmentX(Component.CENTER_ALIGNMENT);
			pn.add(cbx);
			cbx.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						pnPath.addAnimationListener(al);
					} else {
						pnPath.removeAnimationListener(al);
						if (realMap != null && path != null)
							setRuneMapShown(Paths.follow(realMap, path));
					}
				}
			});

			JLabel lb1 = new JLabel("Small Delay (ms)");
			lb1.setAlignmentX(Component.CENTER_ALIGNMENT);
			pn.add(lb1);

			JSpinner sp1 = new JSpinner(new SpinnerNumberModel(300, 10, 1000,
					10));
			sp1.addChangeListener(EventHandler.create(ChangeListener.class,
					pnPath, "smallDelay", "source.value"));
			pn.add(sp1);

			JLabel lb2 = new JLabel("Big Delay (ms)");
			lb2.setAlignmentX(Component.CENTER_ALIGNMENT);
			pn.add(lb2);

			JSpinner sp2 = new JSpinner(new SpinnerNumberModel(
					pnPath.getBigDelay(), 500, 15000, 500));
			sp2.addChangeListener(EventHandler.create(ChangeListener.class,
					pnPath, "bigDelay", "source.value"));
			pn.add(sp2);

			JLabel lb3 = new JLabel("Moving Time (ms)");
			lb3.setAlignmentX(Component.CENTER_ALIGNMENT);
			pn.add(lb3);

			movingTimeLimit = 5000;
			JSpinner sp3 = new JSpinner(new SpinnerNumberModel(movingTimeLimit,
					250, 20000, 250));
			sp3.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					JSpinner spn = (JSpinner) e.getSource();
					movingTimeLimit = (int) spn.getValue();
				}
			});
			pn.add(sp3);

			JPanel panel = new JPanel();
			pn.add(panel);
			panel.setLayout(new GridLayout(0, 1, 0, 0));

			ButtonGroup group = new ButtonGroup();

			JRadioButton rdbtnBasicMove = new JRadioButton("Simple Move");
			group.add(rdbtnBasicMove);
			panel.add(rdbtnBasicMove);
			rdbtnBasicMove.setAlignmentX(Component.CENTER_ALIGNMENT);
			rdbtnBasicMove.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED)
						movingPathGenerator = new BasicMovingPathGenerator();
				}
			});
			rdbtnBasicMove.setSelected(true);

			JRadioButton rdbtnHandlikeMove = new JRadioButton("Hand-like Move");
			group.add(rdbtnHandlikeMove);
			panel.add(rdbtnHandlikeMove);
			rdbtnHandlikeMove.setAlignmentX(0.5f);
			rdbtnHandlikeMove.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED)
						movingPathGenerator = new HandMovingPathGenerator();
				}
			});

			JPanel pnWrapper = new JPanel();
			pnWrapper.add(pn);

			tabbedPane.addTab("Miscellaneous", pnWrapper);
			tabbedPane.setMnemonicAt(3, KeyEvent.VK_C);
		}
	}

	Rectangle getMapArea() {
		return mapArea;
	}

	void setMapArea(Rectangle mapArea) {
		this.mapArea = mapArea;
	}

	RuneMap getRealMap() {
		return realMap;
	}

	void setRealMap(RuneMap realMap) {
		this.realMap = realMap;
	}

	Path getPath() {
		return path;
	}

	JButton getNextButton() {
		return btnNext;
	}

	JButton getBackButton() {
		return btnBack;
	}

	JButton getInterruptButton() {
		return btnInterrupt;
	}

	Robot getRobot() {
		return awtRobot;
	}

	int getMovingTimeLimit() {
		return movingTimeLimit;
	}

	void transferState(MFState state) {
		this.mfstate = state;
		this.mfstate.checkIn(this);
	}

	void setStatus(String status) {
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

	void setProgress(double value) {
		int ivalue = (int) (value * pbProgress.getMaximum());
		if (pbProgress.getValue() != ivalue) {
			pbProgress.setValue(ivalue);
			pbProgress.repaint();
		}
	}

	void setTime(double seconds) {
		if (!Double.isNaN(seconds))
			lbTime.setText(MessageFormat.format("{0,number,#.###} seconds",
					new Object[] { seconds }));
		else
			lbTime.setText("");
	}

	void setPath(Path path) {
		this.path = path;
		if (path != null) {
			Point point = path.getBeginPoint();
			lbStartPoint.setText(MessageFormat.format(
					"Start: ({0,number,integer}, {1,number,integer})",
					new Object[] { point.x + 1, point.y + 1 }));
			listDir.setDirections(path.getDirections());
			pnPath.setPath(path);
		} else {
			lbStartPoint.setText("");
			listDir.setDirections(null);
			pnPath.setPath(null);
		}
	}

	void setRuneMapShown(RuneMap map) {
		tbStones.setRuneMap(map);
		if (map != null) {
			adjustRuneMapShown();
		}
	}

	private void adjustRuneMapShown() {
		float tableWidth = tbStones.getWidth();
		float tableHeight = 0f;
		for (int row = 0; row < tbStones.getRowCount(); row++)
			tableHeight += tbStones.getRowHeight(row);
		pnPath.setCellSize(tableWidth / tbStones.getColumnCount(), tableHeight
				/ tbStones.getRowCount());
	}

	SearchStrategy getSearchStrategy() {
		return searchStrategyPane.createSearchStrategy();
	}

	SolutionStrategy getSolutionStrategy() {
		return strategyPane.createStrategy();
	}

	MovingPathGenerator getMovingPathGenerator() {
		return movingPathGenerator;
	}
}
