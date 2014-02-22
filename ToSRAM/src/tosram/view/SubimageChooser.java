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
	private Point p1, p2;
	private ImagePanel panel;
	private boolean canceled;
	private int selectId;

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

	public boolean isCanceled() {
		return canceled;
	}

	public void setAnswerArea(Rectangle rect) {
		setP1(new Point(rect.x, rect.y));
		setP2(new Point(rect.x + rect.width, rect.y + rect.height));
	}

	public Rectangle getAnswerArea() {
		Rectangle choosenArea = new Rectangle();
		choosenArea.setFrameFromDiagonal(getP1(), getP2());
		return choosenArea;
	}

	private Point getPoint(int i) {
		if (i == 1)
			return getP1();
		else if (i == 2)
			return getP2();
		return null;
	}

	private void setPoint(int i, Point p) {
		if (i == 1)
			setP1(p);
		else if (i == 2)
			setP2(p);
	}

	private Point getP1() {
		return new Point(p1);
	}

	private void setP1(Point p1) {
		this.p1 = new Point(p1);
	}

	private Point getP2() {
		return new Point(p2);
	}

	private void setP2(Point p2) {
		this.p2 = new Point(p2);
	}

	private class MyMouseAdapter extends MouseAdapter {

		private void updateCursor(Point mp) {
			int cursorId;
			if (selectId != 0) {
				cursorId = Cursor.CROSSHAIR_CURSOR;
			} else if (getP1().distance(mp) < 10.0
					|| getP2().distance(mp) < 10.0) {
				cursorId = Cursor.HAND_CURSOR;
			} else {
				cursorId = Cursor.CROSSHAIR_CURSOR;
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
				if (getPoint(i).distance(e.getPoint()) < minDis) {
					selectId = i;
					minDis = getPoint(i).distance(e.getPoint());
				}
			}
			if (selectId != 0) {
				setPoint(selectId, e.getPoint());
			} else {
				setP1(e.getPoint());
				setP2(e.getPoint());
				selectId = 2;
			}
			panel.repaint();
			updateCursor(e.getPoint());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			selectId = 0;
			panel.repaint();
			updateCursor(e.getPoint());
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (selectId == 0)
				return;
			setPoint(selectId, e.getPoint());
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

			Rectangle choosenArea = new Rectangle();
			choosenArea.setFrameFromDiagonal(p1, p2);

			g.setColor(new Color(255, 0, 0, 127));
			g.fillRect(choosenArea.x, choosenArea.y, choosenArea.width,
					choosenArea.height);

			g.setColor(Color.WHITE);
			g.setXORMode(Color.BLACK);
			final int arm = 10;
			if (selectId != 1) {
				g.drawLine(p1.x - arm, p1.y, p1.x + arm, p1.y);
				g.drawLine(p1.x, p1.y - arm, p1.x, p1.y - 1);
				g.drawLine(p1.x, p1.y + 1, p1.x, p1.y + arm);
			}
			if (selectId != 2) {
				g.drawLine(p2.x - arm, p2.y, p2.x + arm, p2.y);
				g.drawLine(p2.x, p2.y - arm, p2.x, p2.y - 1);
				g.drawLine(p2.x, p2.y + 1, p2.x, p2.y + arm);
			}
		}
	}
}
