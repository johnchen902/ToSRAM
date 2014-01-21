package tosram.view;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;

class GettingRectangleState implements MFState {

	@Override
	public void checkIn(MainFrame frame) {
		frame.setStatus("Getting rectangle...");
		Rectangle screenArea = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().getBounds();
		Rectangle mapArea;
		try {
			mapArea = SubimageChooser.showRectangleDialog(
					new Robot().createScreenCapture(screenArea),
					"Where are the runestones? Drag a rectangle.");
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
