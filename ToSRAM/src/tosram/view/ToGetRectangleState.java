package tosram.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ToGetRectangleState implements MFState {

	private MainFrame frame;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("Where are the runestones?");
		frame.setNextText("Find");
		frame.setNextActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setNextText(null);
				frame.setNextActionListener(null);
				frame.transferState(new GettingRectangleState());
			}
		});
	}
}
