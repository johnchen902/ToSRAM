package tosram.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ToGetRectangleState implements MFState {

	private MainFrame frame;
	private ActionListener next;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.getNextButton().setEnabled(true);
		frame.setStatus("To get rectangle...");
		frame.getNextButton().addActionListener(next = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getNextButton().setEnabled(false);
				frame.getNextButton().removeActionListener(next);
				frame.transferState(new GettingRectangleState());
			}
		});
	}

}
