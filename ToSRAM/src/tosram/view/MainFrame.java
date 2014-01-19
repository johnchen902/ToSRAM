package tosram.view;

import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.text.MessageFormat;

import javax.swing.*;
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

	private boolean settingsVisible;
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
	private JCheckBox chckbxSettings; // toggle settings

	private StrategyPanel strategyPane;
	private SearchStrategyPanel searchStrategyPane;

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

		settingsVisible = true;

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
			lblMap.setLabelFor(tbStones);
			lblMap.setDisplayedMnemonic('m');
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

			chckbxSettings = new JCheckBox("Settings", true);
			chckbxSettings.setMnemonic('s');
			pnSouth.add(chckbxSettings);
			chckbxSettings.addActionListener(EventHandler.create(
					ActionListener.class, this, "settingsVisible",
					"source.selected"));
		}
	}

	private void initSidePanel() {
		pnSide = new JPanel(new GridLayout(1, 1));

		JTabbedPane tabbedPane = new JTabbedPane();
		pnSide.add(tabbedPane);
		{
			strategyPane = new StrategyPanel();
			tabbedPane.addTab("Strategies", strategyPane);
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_T);
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
			rmt.setEditable(false);
			rmt.setFont(rmt.getFont().deriveFont(24f));
			rmt.setRuneMap(map0);
			tabbedPane.addTab("Edit Map", rmt);
		}
		{
			JPanel pn = new JPanel();
			pn.setLayout(new BoxLayout(pn, BoxLayout.Y_AXIS));

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
			cbx.setMnemonic('o');
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

			JLabel lb1 = new JLabel(MessageFormat.format(
					"Small Delay ({0,number,integer} ~ {1,number,integer})",
					new Object[] { 90, 1000 }));
			lb1.setDisplayedMnemonic('d');
			lb1.setAlignmentX(Component.CENTER_ALIGNMENT);
			pn.add(lb1);

			JSpinner sp1 = new JSpinner(new SpinnerNumberModel(
					pnPath.getSmallDelay(), 90, 1000, 10));
			lb1.setLabelFor(sp1);
			sp1.addChangeListener(EventHandler.create(ChangeListener.class,
					pnPath, "smallDelay", "source.value"));
			pn.add(sp1);

			JLabel lb2 = new JLabel(MessageFormat.format(
					"Big Delay ({0,number,integer} ~ {1,number,integer})",
					new Object[] { 500, 15000 }));
			lb2.setDisplayedMnemonic('l');
			lb2.setAlignmentX(Component.CENTER_ALIGNMENT);
			pn.add(lb2);

			JSpinner sp2 = new JSpinner(new SpinnerNumberModel(
					pnPath.getBigDelay(), 500, 15000, 500));
			lb2.setLabelFor(sp2);
			sp2.addChangeListener(EventHandler.create(ChangeListener.class,
					pnPath, "bigDelay", "source.value"));
			pn.add(sp2);

			pn.add(Box.createVerticalGlue());

			JPanel pnWrapper = new JPanel();
			pnWrapper.add(pn);
			tabbedPane.addTab("Miscellaneous", pnWrapper);
			tabbedPane.setMnemonicAt(3, KeyEvent.VK_A);
		}
	}

	/**
	 * Set whether the settings is visible.
	 * 
	 * @param visible
	 *            <code>true</code> if the settings are visible;
	 *            <code>false</code> otherwise
	 */
	public void setSettingsVisible(boolean visible) {
		if (visible != settingsVisible) {
			if (visible) {
				showSettings();
			} else {
				hideSettings();
			}
			settingsVisible = visible;
		}
	}

	/**
	 * Get whether the settings is visible
	 * 
	 * @return <code>true</code> if the settings are visible; <code>false</code>
	 *         otherwise
	 */
	public boolean isSettingsVisible() {
		return settingsVisible;
	}

	private void showSettings() {
		int mainWidth = pnMain.getWidth();
		int sideWidth = mainWidth * 200 / 384;
		int insetsBoth = getInsets().left + getInsets().right;
		int height = getHeight();
		getContentPane().remove(pnMain);
		splitPane.setLeftComponent(pnMain);
		splitPane.setRightComponent(pnSide);
		getContentPane().add(splitPane);
		setSize(insetsBoth + mainWidth + sideWidth, height);
		validate();
		splitPane.setDividerLocation(mainWidth);
	}

	private void hideSettings() {
		int mainWidth = pnMain.getWidth();
		int insetsBoth = getInsets().left + getInsets().right;
		int height = getHeight();
		getContentPane().remove(splitPane);
		getContentPane().add(pnMain);
		setSize(insetsBoth + mainWidth, height);
		validate();
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

}
