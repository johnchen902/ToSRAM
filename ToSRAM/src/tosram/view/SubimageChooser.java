package tosram.view;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Ask user to choose an rectangle of an image. Please don't try to serialize
 * me. I don't regard the serialized form.
 * 
 * @author johnchen902
 */
@SuppressWarnings("serial")
public class SubimageChooser extends JDialog {

	private ImagePanel panel;
	private boolean done;

	/**
	 * Ask user to choose an rectangle of the image.
	 * 
	 * @param image
	 *            the image shown
	 * @param title
	 *            the title shown
	 * @return the chosen rectangle or <code>null</code> if user close the
	 *         dialog.
	 */
	public static Rectangle showRectangleDialog(BufferedImage image,
			String title) {
		SubimageChooser sc = new SubimageChooser(image);
		sc.setTitle(title);
		sc.setVisible(true);
		if (!sc.isDone())
			return null;
		return sc.getAnswerArea();
	}

	/**
	 * Ask user to choose an rectangle of the image.
	 * 
	 * @param image
	 *            the image shown
	 * @param title
	 *            the title shown
	 * @param init
	 *            the initial selection
	 * @return the chosen rectangle or <code>null</code> if user close the
	 *         dialog.
	 */
	public static Rectangle showRectangleDialog(BufferedImage image,
			String title, Rectangle init) {
		SubimageChooser sc = new SubimageChooser(image);
		sc.panel.setP1(new Point(init.x, init.y));
		sc.panel.setP2(new Point(init.x + init.width, init.y + init.height));
		sc.setTitle(title);
		sc.setVisible(true);
		if (!sc.isDone())
			return null;
		return sc.getAnswerArea();
	}

	private SubimageChooser(BufferedImage img) {
		JPanel pnLeft = new JPanel();

		JButton btn = new JButton("Done");
		pnLeft.add(btn);
		getContentPane().add(pnLeft, BorderLayout.WEST);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				done = true;
				dispose();
			}
		});

		final JScrollPane scroll = new JScrollPane();
		getContentPane().add(scroll, BorderLayout.CENTER);
		scroll.setAutoscrolls(true);

		panel = new ImagePanel();
		scroll.setViewportView(panel);
		panel.setImage(img);
		panel.setP1(new Point(img.getWidth() / 3, img.getHeight() / 3));
		panel.setP2(new Point(img.getWidth() * 2 / 3, img.getHeight() * 2 / 3));
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
				done = false;
				dispose();
			}
		});

		setModal(true);
	}

	boolean isDone() {
		return done;
	}

	Rectangle getAnswerArea() {
		Rectangle choosenArea = new Rectangle();
		choosenArea.setFrameFromDiagonal(panel.getP1(), panel.getP2());
		return choosenArea;
	}

	private class MyMouseAdapter extends MouseAdapter {

		private int selectId;
		private Point offset = new Point();

		private void updateCursor(Point mp) {
			int cursorId;
			if (selectId != 0) {
				cursorId = Cursor.MOVE_CURSOR;
			} else if (panel.getP1().distance(mp) < 10.0
					|| panel.getP2().distance(mp) < 10.0) {
				cursorId = Cursor.HAND_CURSOR;
			} else {
				cursorId = Cursor.DEFAULT_CURSOR;
			}
			panel.setCursor(Cursor.getPredefinedCursor(cursorId));
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			updateCursor(e.getPoint());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			double minDis = 10.0;
			selectId = 0;
			for (int i = 1; i <= 2; i++) {
				if (panel.getPoint(i).distance(e.getPoint()) < minDis) {
					selectId = i;
					minDis = panel.getPoint(i).distance(e.getPoint());
				}
			}
			if (selectId != 0) {
				offset.x = panel.getPoint(selectId).x - e.getX();
				offset.y = panel.getPoint(selectId).y - e.getY();
				updateCursor(e.getPoint());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			selectId = 0;
			updateCursor(e.getPoint());
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (selectId == 0)
				return;
			Point p = new Point(e.getX() + offset.x, e.getY() + offset.y);
			panel.setPoint(selectId, p);
			panel.repaint();
		}
	}

	static class ImagePanel extends JPanel {
		private BufferedImage image;
		private Point p1, p2;

		public BufferedImage getImage() {
			return image;
		}

		public void setImage(BufferedImage image) {
			this.image = image;
		}

		public Point getPoint(int i) {
			if (i == 1)
				return getP1();
			else if (i == 2)
				return getP2();
			return null;
		}

		public void setPoint(int i, Point p) {
			if (i == 1)
				setP1(p);
			else if (i == 2)
				setP2(p);
		}

		public Point getP1() {
			return new Point(p1);
		}

		public void setP1(Point p1) {
			this.p1 = new Point(p1);
		}

		public Point getP2() {
			return new Point(p2);
		}

		public void setP2(Point p2) {
			this.p2 = new Point(p2);
		}

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

			Rectangle choosenArea = new Rectangle();
			choosenArea.setFrameFromDiagonal(p1, p2);

			g.setColor(new Color(255, 0, 0, 127));
			g.fillRect(choosenArea.x, choosenArea.y, choosenArea.width,
					choosenArea.height);

			g.setColor(Color.WHITE);
			g.setXORMode(Color.BLACK);
			final int arm = 10;
			g.drawLine(p1.x - arm, p1.y, p1.x + arm, p1.y);
			g.drawLine(p1.x, p1.y - arm, p1.x, p1.y - 1);
			g.drawLine(p1.x, p1.y + 1, p1.x, p1.y + arm);
			g.drawLine(p2.x - arm, p2.y, p2.x + arm, p2.y);
			g.drawLine(p2.x, p2.y - arm, p2.x, p2.y - 1);
			g.drawLine(p2.x, p2.y + 1, p2.x, p2.y + arm);
		}
	}
}
