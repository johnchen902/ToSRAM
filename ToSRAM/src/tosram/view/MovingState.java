package tosram.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import tosram.*;
import tosram.MovingPathGenerator.Move;

class MovingState implements MFState {

	private MainFrame frame;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("Moving...");
		new MovingWorker().execute();
	}

	private class MovingWorker extends SwingWorker<Void, Void> {

		private MovingPathGenerator mpg = frame.getMovingPathGenerator();
		private int timeLimit = frame.getMovingTimeLimit();
		private boolean isFastStart = frame.isMovingFastStart();

		private int getDelayBeforeTakeover() {
			return isFastStart ? 40 : 400;
		}

		private int getDelayAfterRelease() {
			return isFastStart ? 10 : 100;
		}

		private void delayRobotUntil(Robot robot, long until) {
			robot.delay(Math.max((int) (until - System.currentTimeMillis()), 0));
		}

		@Override
		protected Void doInBackground() throws Exception {
			List<Move> moves = mpg.getMovePath(frame.getPath(), frame
					.getMapArea(), new Dimension(frame.getRealMap().getWidth(),
					frame.getRealMap().getHeight()), timeLimit, isFastStart);

			Robot robot = new Robot();
			robot.delay(getDelayBeforeTakeover());
			boolean begin = true;
			long delayUntil = System.currentTimeMillis();
			for (Move move : moves) {
				if (begin) {
					robot.mouseMove(move.getPoint().x, move.getPoint().y);
					delayRobotUntil(robot, delayUntil += move.getDelay() / 2);
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					delayRobotUntil(robot, delayUntil += move.getDelay() / 2);
					begin = false;
				} else {
					robot.mouseMove(move.getPoint().x, move.getPoint().y);
					delayRobotUntil(robot, delayUntil += move.getDelay());
				}
			}

			// release
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(getDelayAfterRelease());
			Point p = new Point(frame.getWidth() / 2, frame.getHeight() / 2);
			SwingUtilities.convertPointToScreen(p, frame); // XXX access UI
			robot.mouseMove(p.x, p.y);
			return null;
		}

		@Override
		protected void done() {
			try {
				get();
			} catch (Exception ignore) {
				ignore.printStackTrace();
			}
			frame.requestFocus();
			frame.transferState(new GettingStoneState());
		}
	}
}
