package tosram.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.javatuples.LabelValue;

import tosram.MovingPathGenerator;
import tosram.Path;
import tosram.PathRobot;
import tosram.RuneMap;
import tosram.view.strategy.StrategyRobotCreater;
import tosram.view.xrobot.IDAStarRobotCreater;

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
	private JTabbedPane pnSide;

	private JProgressBar pbProgress; // progress of computing
	private JLabel lbStatus; // general states and computing milestones
	private JLabel lbTime; // time spent in computing
	private JLabel lbStartPoint; // starting point of path
	private PathPanel pnPath; // path drawn above tbStones
	private RuneMapTable tbStones;
	private DirectionList listDir;

	private JPanel buttonsPanel;

	private PathRobotCreater robotCreater;
	private MovingPane movingPane;

	public MainFrame() {
		super("Tower of Savior Runestone Auto Mover");

		initUI();

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
				tbStones.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						tbStones.setFocusable(true);
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
			buttonsPanel = new JPanel();
			pnMain.add(buttonsPanel, BorderLayout.SOUTH);

			JLabel lblMap = new JLabel("Map\u2191");
			MnemonicsDispatcher.registerComponent(lblMap);
			PropertyChangeListener mnemonicChanged = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					JLabel lb = (JLabel) e.getSource();
					lb.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
							KeyStroke.getKeyStroke((int) e.getOldValue(),
									InputEvent.ALT_DOWN_MASK, false), null);
					lb.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
							.put(KeyStroke.getKeyStroke((int) e.getNewValue(),
									InputEvent.ALT_DOWN_MASK, false), "editMap");
				}
			};
			lblMap.addPropertyChangeListener("displayedMnemonic",
					mnemonicChanged);
			lblMap.getActionMap().put("editMap", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					tbStones.setFocusable(true);
					tbStones.requestFocusInWindow();
				}
			});

			buttonsPanel.add(lblMap);
		}
	}

	private void initSidePanel() {
		pnSide = new JTabbedPane();
		{
			JPanel pnWrapper = new JPanel();
			pnWrapper.add(createMiscellaneousPanel());

			pnSide.addTab("Miscellaneous", pnWrapper);
		}
		{
			JPanel pnWrapper = new JPanel();
			pnSide.addTab("Edit Map", pnWrapper);
			pnWrapper.setLayout(new BorderLayout(0, 0));

			RuneMapTable rmt = RuneMapTable.createAllStonesInstance();
			pnWrapper.add(rmt);
			rmt.setFocusable(false);
			rmt.setFont(rmt.getFont().deriveFont(24f));

			JButton btnHelp = new JButton("How to edit map?");
			pnWrapper.add(btnHelp, BorderLayout.SOUTH);
			btnHelp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String msg = "You can edit the map in the following ways:\n"
							+ "    * Drag from a cell in the \"Edit Map\" tab, and drop to one in the map.\n"
							+ "    * Drag from a cell in the map, and drop to another one.\n"
							+ "    * Double-click a cell in the map, and enter the corresponding code.\n";

					JOptionPane.showMessageDialog(MainFrame.this, msg,
							"How to edit map", JOptionPane.INFORMATION_MESSAGE);
				}
			});
			MnemonicsDispatcher.registerComponent(btnHelp);
		}
		{
			selectRobot(robotCreater);
			if (pnSide.getTabCount() >= 3)
				pnSide.setSelectedIndex(2);
		}
	}

	private void selectRobot(PathRobotCreater prc) {
		while (pnSide.getTabCount() > 2) {
			pnSide.removeTabAt(pnSide.getTabCount() - 1);
		}
		robotCreater = prc;
		for (LabelValue<String, Component> e : robotCreater.getSettingsTabs()) {
			pnSide.addTab(e.getLabel(), e.getValue());
		}
	}

	private JComponent createMiscellaneousPanel() {
		Box pn = Box.createVerticalBox();

		JPanel pnRobot = new JPanel();
		pn.add(pnRobot);
		pnRobot.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnRobot.setBorder(BorderFactory.createTitledBorder("Robot"));
		pnRobot.setLayout(new GridLayout(0, 1));

		final String ROBOT = "robot";
		ButtonGroup groupRobot = new ButtonGroup();
		JRadioButton btnFast = new JRadioButton("Fast", true);
		pnRobot.add(btnFast);
		groupRobot.add(btnFast);
		btnFast.putClientProperty(ROBOT, new IDAStarRobotCreater());
		MnemonicsDispatcher.registerComponent(btnFast);

		JRadioButton btnFlexible = new JRadioButton("Flexible");
		pnRobot.add(btnFlexible);
		groupRobot.add(btnFlexible);
		btnFlexible.putClientProperty(ROBOT, new StrategyRobotCreater());
		MnemonicsDispatcher.registerComponent(btnFlexible);

		ActionListener alRobot = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComponent bt = (JComponent) e.getSource();
				selectRobot((PathRobotCreater) bt.getClientProperty(ROBOT));
			}
		};
		btnFast.addActionListener(alRobot);
		btnFlexible.addActionListener(alRobot);
		robotCreater = (PathRobotCreater) btnFast.getClientProperty(ROBOT);

		final PathPanel.AnimationListener al = new PathPanel.AnimationListener() {
			@Override
			public void animationCycleStop() {
				setRuneMapShown(Path.follow(realMap, path));
			}

			@Override
			public void animationCycleStart() {
				setRuneMapShown(realMap);
			}

			@Override
			public void animate(int index) {
				setRuneMapShown(Path.follow(realMap, path, index + 1));
			}
		};

		Box pnAnima = Box.createVerticalBox();
		pnAnima.setBorder(BorderFactory.createTitledBorder("Animation"));

		JPanel pnSpeed = new JPanel();
		pnAnima.add(pnSpeed);
		pnSpeed.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel lbSpeed = new JLabel("Speed:");
		pnSpeed.add(lbSpeed);
		lbSpeed.setAlignmentX(Component.CENTER_ALIGNMENT);
		MnemonicsDispatcher.registerComponent(lbSpeed);

		JSpinner spnSpeed = new JSpinner(new SpinnerNumberModel(1, 0.5, 8, 0.5));
		pnSpeed.add(spnSpeed);
		lbSpeed.setLabelFor(spnSpeed);
		spnSpeed.setEditor(new JSpinner.NumberEditor(spnSpeed, "#.#x"));
		spnSpeed.addChangeListener(EventHandler.create(ChangeListener.class,
				pnPath, "animationSpeed", "source.value"));

		JCheckBox cbx = new JCheckBox("Animate Stones");
		pnAnima.add(cbx);
		cbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					pnPath.addAnimationListener(al);
				} else {
					pnPath.removeAnimationListener(al);
					if (realMap != null && path != null)
						setRuneMapShown(Path.follow(realMap, path));
				}
			}
		});
		cbx.setSelected(true);
		MnemonicsDispatcher.registerComponent(cbx);

		pn.add(pnAnima);

		movingPane = new MovingPane();
		movingPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		pn.add(movingPane);
		movingPane.setBorder(BorderFactory.createTitledBorder("Moving"));
		return pn;
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

	private List<JButton> buttons = new ArrayList<>();

	void addButton(String text, ActionListener listener) {
		JButton bt = new JButton(text);
		bt.addActionListener(listener);
		MnemonicsDispatcher.registerComponent(bt);
		buttons.add(bt);
		buttonsPanel.add(bt);
		validate();
	}

	void removeButtons() {
		for (JButton c : buttons)
			buttonsPanel.remove(c);
		buttons.clear();
		validate();
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
			lbTime.setText(MessageFormat.format(
					"{0,number,0.000} second{0,choice,1<|2#s}",
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

	RuneMap getRuneMapShown() {
		return tbStones.getRuneMap();
	}

	void setRuneMapShown(RuneMap map) {
		tbStones.setRuneMap(map);
		if (map != null) {
			adjustRuneMapShown();
		}
	}

	private void adjustRuneMapShown() {
		float tableWidth = tbStones.getWidth();
		float tableHeight = tbStones.getHeight();
		for (int row = 0; row < tbStones.getRowCount(); row++)
			tbStones.setRowHeight(row,
					(int) (tableHeight / tbStones.getRowCount()));
		pnPath.setCellSize(tableWidth / tbStones.getColumnCount(),
				(int) (tableHeight / tbStones.getRowCount()));
	}

	void setRuneMapEditable(boolean editable) {
		tbStones.setEditable(editable);
	}

	PathRobot createPathRobot() {
		return robotCreater.createPathRobot();
	}

	int getMovingTimeLimit() {
		return movingPane.getTimeLimit();
	}

	boolean isMovingFastStart() {
		return movingPane.isFastStart();
	}

	MovingPathGenerator getMovingPathGenerator() {
		return movingPane.createMovingPathGenerator();
	}
}
