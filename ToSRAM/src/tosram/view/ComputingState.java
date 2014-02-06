package tosram.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.Timer;

import tosram.Path;
import tosram.PathRobot;
import tosram.RuneMap;

class ComputingState implements MFState {

	private MainFrame frame;
	private ComputionWorker computionWorker;
	private Timer timer;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("Computing...");
		frame.setInterruptActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computionWorker.interrupt();
			}
		});
		final long timeStart = System.currentTimeMillis();
		timer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setTime((System.currentTimeMillis() - timeStart) / 1000.0);
			}
		});
		timer.start();
		(computionWorker = new ComputionWorker()).execute();
	}

	private class ComputionWorker extends SwingWorker<Path, Object> implements
			PathRobot.StatusListener {

		private final PathRobot pathRobot;
		private double computeTime;
		private String finalStatus = "no result";
		private Thread thread;

		public ComputionWorker() {
			pathRobot = frame.createPathRobot();
		}

		public void interrupt() {
			thread.interrupt();
		}

		@Override
		protected Path doInBackground() throws Exception {
			thread = Thread.currentThread();

			pathRobot.setStatusListener(this);

			long tBegin = System.nanoTime();
			Path path = pathRobot.getPath(new RuneMap(frame.getRealMap()));
			long tEnd = System.nanoTime();

			computeTime = (tEnd - tBegin) * 1e-9;

			return path;
		}

		@Override
		public void updateProgress(double progress) {
			publish(progress);
		}

		@Override
		public void updateMilestone(String milestone) {
			publish(milestone);
		}

		@Override
		protected void process(List<Object> chunks) {
			double max = -1;
			for (Object o : chunks) {
				if (o instanceof Double) {
					max = Math.max(max, (double) o);
				} else {
					frame.setStatus(finalStatus = o.toString());
				}
			}
			if (max != -1)
				frame.setProgress(max);
		}

		@Override
		protected void done() {
			timer.stop();
			try {
				Path p = get();
				frame.setPath(p);
				frame.setRuneMapShown(Path.follow(frame.getRealMap(), p));
			} catch (Exception ignore) {
				ignore.printStackTrace();
			}
			frame.setTime(computeTime);
			frame.setInterruptActionListener(null);
			frame.transferState(new ToMoveState(finalStatus));
		}
	}
}
