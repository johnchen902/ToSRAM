package tosram.view;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingWorker;

import tosram.DefaultRuneStoneGetter;
import tosram.RuneMap;
import tosram.RuneStone;
import tosram.RuneStoneGetter;

class GettingStoneState implements MFState {

	private MainFrame frame;
	private volatile boolean interrupted;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("Reading runestones...");
		interrupted = false;
		frame.addButton("Interrupt", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				interrupted = true;
			}
		});
		new GettingStoneWorker().execute();
	}

	private class GettingStoneWorker extends SwingWorker<RuneMap, RuneMap> {

		private static final int DELAY_BETWEEN_READ_STONE = 500;
		private final RuneStoneGetter rsg = new DefaultRuneStoneGetter();

		@Override
		protected RuneMap doInBackground() throws Exception {
			// get RuneMap until the map become fixed
			Robot robot = new Robot();
			Rectangle rect = frame.getMapArea();

			RuneMap map = rsg.getRuneStones(robot.createScreenCapture(rect));

			while (!interrupted) {
				robot.delay(DELAY_BETWEEN_READ_STONE);
				RuneMap prevmap = map;
				map = rsg.getRuneStones(robot.createScreenCapture(rect));

				publish(map);

				int xCount = 0;
				for (int y = 0; y < map.getHeight(); y++)
					for (int x = 0; x < map.getWidth(); x++)
						if (map.getStone(x, y).getType() == RuneStone.Type.UNKNOWN)
							xCount++;
				if (xCount < map.getWidth() * map.getHeight() / 2
						&& map.equals(prevmap))
					break;
			}

			return map;
		}

		@Override
		protected void process(List<RuneMap> chunks) {
			frame.setRuneMapShown(chunks.get(chunks.size() - 1));
		}

		@Override
		protected void done() {
			try {
				RuneMap map = this.get();
				frame.setRealMap(map);
				frame.setRuneMapShown(map);
			} catch (Exception ignore) {
				ignore.printStackTrace();
			}

			frame.removeButtons();
			frame.transferState(new ToComputeState());
		}
	}
}
