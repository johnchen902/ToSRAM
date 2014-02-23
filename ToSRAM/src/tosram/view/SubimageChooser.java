package tosram.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ask user to choose an rectangle of an image.<br>
 * Example:
 * 
 * <pre>
 * SubimageChooser chooser = new SubimageChooser(image);
 * chooser.setTitle(&quot;Hello&quot;);
 * chooser.setAnswerArea(new Rectangle(5, 5, 10, 10)); // initial selection
 * chooser.setVisible(true); // blocking
 * if (chooser.isCanceled())
 * 	System.out.println(&quot;Canceled&quot;);
 * else
 * 	System.out.println(chooser.getAnswerArea());
 * </pre>
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class SubimageChooser extends JDialog {

	private BufferedImage image;
	private Rectangle answerArea;
	private ImagePanel panel;
	private boolean canceled;

	/**
	 * Construct a chooser with given image.
	 * 
	 * @param img
	 *            image to shown
	 */
	public SubimageChooser(BufferedImage img) {
		this.image = img;

		JPanel pnLeft = new JPanel();
		getContentPane().add(pnLeft, BorderLayout.WEST);

		JButton btnOK = new JButton(UIManager.get("OptionPane.okButtonText")
				.toString());
		btnOK.setAlignmentX(Component.CENTER_ALIGNMENT);
		getRootPane().setDefaultButton(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = false;
				dispose();
			}
		});
		JButton btnCancel = new JButton(UIManager.get(
				"OptionPane.cancelButtonText").toString());
		btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnLeft.setLayout(new BoxLayout(pnLeft, BoxLayout.Y_AXIS));

		pnLeft.add(Box.createVerticalGlue());
		pnLeft.add(btnOK);
		pnLeft.add(Box.createVerticalStrut(5));
		pnLeft.add(btnCancel);
		pnLeft.add(Box.createVerticalGlue());

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = true;
				dispose();
			}
		});

		final JScrollPane scroll = new JScrollPane();
		getContentPane().add(scroll, BorderLayout.CENTER);
		scroll.setAutoscrolls(true);

		panel = new ImagePanel();
		scroll.setViewportView(panel);
		setAnswerArea(new Rectangle(img.getWidth() / 3, img.getHeight() / 3,
				img.getWidth() / 3, img.getHeight() / 3));
		MouseAdapter ma = new MyMouseAdapter();
		panel.addMouseListener(ma);
		panel.addMouseMotionListener(ma);

		setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				Point p = new Point();
				SwingUtilities.convertPointToScreen(p, scroll.getViewport());
				scroll.getViewport().setViewPosition(p);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				canceled = true;
				dispose();
			}
		});

		setModal(true);
	}

	/**
	 * Return whether the user has canceled it.
	 * 
	 * @return <code>true</code> if the user has canceled it; <code>false</code>
	 *         if not
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * Set the area answered.
	 * 
	 * @param rect
	 *            the area answered
	 */
	public void setAnswerArea(Rectangle rect) {
		answerArea = new Rectangle(rect);
	}

	/**
	 * Get the area answered.
	 * 
	 * @return the area answered
	 */
	public Rectangle getAnswerArea() {
		return new Rectangle(answerArea);
	}

	private class MyMouseAdapter extends MouseAdapter {

		private static final int TOP_LEFT = 0;
		private static final int TOP = 1;
		private static final int TOP_RIGHT = 2;
		private static final int LEFT = 3;
		private static final int FREE_FORM = 4;
		private static final int RIGHT = 5;
		private static final int BOTTOM_LEFT = 6;
		private static final int BOTOM = 7;
		private static final int BOTTOM_RIGHT = 8;

		private int selectId = FREE_FORM;

		private int getLocationType(Point p) {
			int xType = selectId % 3;
			int yType = selectId / 3;

			if (selectId % 3 != 1 || selectId == FREE_FORM
					&& p.y >= answerArea.y - 3
					&& p.y <= answerArea.y + answerArea.height + 3) {
				int dis1 = Math.abs(p.x - answerArea.x);
				int dis2 = Math.abs(p.x - answerArea.x - answerArea.width);
				if (dis1 <= 3 && dis1 < dis2) {
					xType = 0;
				} else if (dis2 <= 3) {
					xType = 2;
				}
			}
			if (selectId / 3 != 1 || selectId == FREE_FORM
					&& p.x >= answerArea.x - 3
					&& p.x <= answerArea.x + answerArea.width + 3) {
				int dis3 = Math.abs(p.y - answerArea.y);
				int dis4 = Math.abs(p.y - answerArea.y - answerArea.height);
				if (dis3 <= 3 && dis3 < dis4) {
					yType = 0;
				} else if (dis4 <= 3) {
					yType = 2;
				}
			}
			return yType * 3 + xType;
		}

		private void updateCursor(Point mp) {
			int cursorId;
			switch (getLocationType(mp)) {
			case TOP:
				cursorId = Cursor.N_RESIZE_CURSOR;
				break;
			case BOTOM:
				cursorId = Cursor.S_RESIZE_CURSOR;
				break;
			case LEFT:
				cursorId = Cursor.W_RESIZE_CURSOR;
				break;
			case RIGHT:
				cursorId = Cursor.E_RESIZE_CURSOR;
				break;
			case TOP_LEFT:
				cursorId = Cursor.NW_RESIZE_CURSOR;
				break;
			case TOP_RIGHT:
				cursorId = Cursor.NE_RESIZE_CURSOR;
				break;
			case BOTTOM_LEFT:
				cursorId = Cursor.SW_RESIZE_CURSOR;
				break;
			case BOTTOM_RIGHT:
				cursorId = Cursor.SE_RESIZE_CURSOR;
				break;
			case FREE_FORM:
				cursorId = Cursor.CROSSHAIR_CURSOR;
				break;
			default:
				cursorId = Cursor.DEFAULT_CURSOR;
				break;
			}

			panel.setCursor(Cursor.getPredefinedCursor(cursorId));
		}

		private void moveTo(Point p) {
			double minX = answerArea.getMinX();
			double minY = answerArea.getMinY();
			double maxX = answerArea.getMaxX();
			double maxY = answerArea.getMaxY();
			if (selectId < 3)
				minY = p.getY();
			else if (selectId >= 6)
				maxY = p.getY();
			if (selectId % 3 == 0)
				minX = p.getX();
			else if (selectId % 3 == 2)
				maxX = p.getX();
			answerArea.setFrameFromDiagonal(minX, minY, maxX, maxY);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			updateCursor(e.getPoint());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			selectId = getLocationType(e.getPoint());
			if (selectId == FREE_FORM) {
				answerArea.setLocation(e.getPoint());
				answerArea.setSize(0, 0);
				selectId = BOTTOM_RIGHT;
			} else {
				moveTo(e.getPoint());
			}
			panel.repaint();
			updateCursor(e.getPoint());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			selectId = FREE_FORM;
			panel.repaint();
			updateCursor(e.getPoint());
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			moveTo(e.getPoint());
			selectId = getLocationType(e.getPoint());
			updateCursor(e.getPoint());
			panel.repaint();
		}
	}

	class ImagePanel extends JPanel {

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(image.getWidth(), image.getHeight());
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0,
					image.getWidth(), image.getHeight(), this);

			g.setColor(new Color(255, 0, 0, 127));
			g.fillRect(answerArea.x, answerArea.y, answerArea.width,
					answerArea.height);

			g.setColor(Color.DARK_GRAY);
			g.drawRect(answerArea.x, answerArea.y, answerArea.width,
					answerArea.height);

			g.setColor(Color.WHITE);
			g.setXORMode(Color.BLACK);
			for (int x = 0; x <= 2; x++) {
				for (int y = 0; y <= 2; y++) {
					if (x == 1 && y == 1)
						continue;
					int x1 = answerArea.x + answerArea.width * x / 2;
					int y1 = answerArea.y + answerArea.height * y / 2;
					final int HALF = 3;
					g.drawRect(x1 - HALF, y1 - HALF, HALF * 2, HALF * 2);
				}
			}
		}
	}
}
