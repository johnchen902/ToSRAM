package tosram.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * The frame shown on the screen. Please don't try to serialize me. I don't
 * regard the serialized form.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel pnMain;
	private JLabel lbStatus; // general states and computing milestones
	private PathPanel pnPath; // path drawn above tbStones
	private RuneMapTable tbStones;

	private JPanel buttonsPanel;

	public MainFrame() {
		super("Tower of Savior Runestone Auto Mover");

		initUI();
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
			lbStatus.setText("Idle");
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
						tbStones.setBounds(c.getBounds());
						c.validate();
					}
				});

				pnPath = new PathPanel();
				layeredPane.add(pnPath, Integer.valueOf(2));

				tbStones = new RuneMapTable();
				layeredPane.add(tbStones, Integer.valueOf(1));
				tbStones.setFont(tbStones.getFont().deriveFont(24f));
			}
		}
		{
			buttonsPanel = new JPanel();
			pnMain.add(buttonsPanel, BorderLayout.SOUTH);
			buttonsPanel.add(new JButton("Edit"));
		}
	}
}
