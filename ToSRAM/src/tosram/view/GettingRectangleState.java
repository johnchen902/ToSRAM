package tosram.view;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

class GettingRectangleState implements MFState {

	@Override
	public void checkIn(MainFrame frame) {
		frame.setStatus("Getting rectangle...");
		Rectangle screenArea = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().getBounds();
		Rectangle mapArea = SubimageChooser.showRectangleDialog(frame
				.getRobot().createScreenCapture(screenArea),
				"Where are the runestones? Drag a rectangle.");
		if (mapArea == null) {
			frame.transferState(new ToGetRectangleState());
		} else {
			frame.setMapArea(mapArea);
			frame.transferState(new GettingStoneState());
		}
	}

}
