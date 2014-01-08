package tosram.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import tosram.Direction;

class MovingState implements MFState {

	private MainFrame frame;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("Moving...");
		new MovingWorker().execute();
	}

	private class MovingWorker extends SwingWorker<Void, Void> {

		private static final int DELAY_BEFORE_TAKEOVER = 400;
		private static final int DELAY_AFTER_PRESS = 3000;
		private static final int DELAY_AFTER_MOVE = 90;
		private static final int DELAY_BEFORE_RELEASE = 100;
		private static final int DELAY_AFTER_RELEASE = 100;

		@Override
		protected Void doInBackground() throws Exception {
			// hold the mouse and move
			Rectangle r = frame.getMapArea();
			Robot robot = frame.getRobot();
			robot.delay(DELAY_BEFORE_TAKEOVER);
			int x = frame.getPath().getBeginPoint().x;
			int y = frame.getPath().getBeginPoint().y;
			int w = frame.getRealMap().getWidth();
			int h = frame.getRealMap().getHeight();
			{
				// move to start point
				int px = r.x + r.width * (2 * x + 1) / 2 / w;
				int py = r.y + r.height * (2 * y + 1) / 2 / h;
				robot.mouseMove(px, py);
				robot.delay(DELAY_AFTER_MOVE);
			}
			// press
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(DELAY_AFTER_PRESS);
			// follow the path
			for (Direction dir : frame.getPath().getDirections()) {
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

				int px = r.x + r.width * (2 * x + 1) / 2 / w;
				int py = r.y + r.height * (2 * y + 1) / 2 / h;
				robot.mouseMove(px, py);
				robot.delay(DELAY_AFTER_MOVE);
			}
			// release
			robot.delay(DELAY_BEFORE_RELEASE);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(DELAY_AFTER_RELEASE);
			Point p = new Point(frame.getWidth() / 2, frame.getHeight() / 2);
			SwingUtilities.convertPointToScreen(p, frame); // XXX access UI
			robot.mouseMove(p.x, p.y);
			return null;
		}

		@Override
		protected void done() {
			frame.requestFocus();
			frame.transferState(new GettingStoneState());
		}
	}
}
