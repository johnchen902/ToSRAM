package tosram.view;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import tosram.DefaultRuneMapLocationGuesser;

class GettingRectangleState implements MFState {

	@Override
	public void checkIn(MainFrame frame) {
		frame.setStatus("Getting where're the runestones...");
		Rectangle screenArea = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().getBounds();
		Rectangle mapArea;
		try {
			BufferedImage image = new Robot().createScreenCapture(screenArea);
			Rectangle initial = DefaultRuneMapLocationGuesser.guess(image);

			SubimageChooser sc = new SubimageChooser(image);
			if (initial.width * initial.height >= 6400) {
				sc.setAnswerArea(initial);
			}
			sc.setTitle("Where Are the Runestones?");
			sc.setVisible(true);
			if (sc.isCanceled())
				mapArea = null;
			else
				mapArea = sc.getAnswerArea();
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
