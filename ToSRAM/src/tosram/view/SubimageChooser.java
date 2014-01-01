package tosram.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
	private BufferedImage image;
	private ImagePanel panel;
	private Rectangle choosenArea;
	private Point startPoint;

	private Rectangle answerArea;

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
		return new SubimageChooser(image, title).getAnswerArea();
	}

	private SubimageChooser(BufferedImage image, String title) {
		this.image = image;
		setTitle(title);

		final JScrollPane scroll = new JScrollPane();
		getContentPane().add(scroll, BorderLayout.CENTER);
		scroll.setAutoscrolls(true);

		panel = new ImagePanel();
		scroll.setViewportView(panel);

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
				submit(null);
			}
		});

		setModal(true);
		setVisible(true);
	}

	private void submit(Rectangle rect) {
		answerArea = rect;
		dispose();
	}

	private Rectangle getAnswerArea() {
		return answerArea;
	}

	private class ImagePanel extends JPanel {

		private ImagePanel() {
			MouseAdapter ma = new MyMouseAdapter();
			addMouseListener(ma);
			addMouseMotionListener(ma);
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
			if (choosenArea != null) {
				g.setColor(new Color(255, 0, 0, 127));
				g.fillRect(choosenArea.x, choosenArea.y, choosenArea.width,
						choosenArea.height);
			}
		}
	}

	private class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			startPoint = e.getPoint();
			choosenArea = new Rectangle(startPoint);
			panel.repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			choosenArea.setFrameFromDiagonal(startPoint, e.getPoint());
			panel.repaint();
			int result = JOptionPane.showConfirmDialog(SubimageChooser.this,
					choosenArea, "confirm", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				submit(choosenArea);
			} else {
				choosenArea = null;
				panel.repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			choosenArea.setFrameFromDiagonal(startPoint, e.getPoint());
			panel.scrollRectToVisible(new Rectangle(e.getX(), e.getY(), 1, 1));
			panel.repaint();
		}
	}
}
