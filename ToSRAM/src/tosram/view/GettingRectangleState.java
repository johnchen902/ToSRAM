package tosram.view;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;

class GettingRectangleState implements MFState {

	@Override
	public void checkIn(MainFrame frame) {
		frame.setStatus("Getting where're the runestones...");
		Rectangle screenArea = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().getBounds();
		Rectangle mapArea;
		try {
			String msg = "Where are the runestones? Click and drag to draw a rectanglular selection.";
			mapArea = SubimageChooser.showRectangleDialog(
					new Robot().createScreenCapture(screenArea), msg);
		} catch (AWTException e) {
			e.printStackTrace();
			mapArea = null;
		}
		if (mapArea == null) {
			frame.transferState(new ToGetRectangleState());
		} else {
			frame.setMapArea(mapArea);
			frame.transferState(new GettingStoneState());
		}
	}
}
