package tosram.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ToComputeState implements MFState {

	private MainFrame frame;
	private ActionListener next, back;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("To compute...");
		frame.getNextButton().setEnabled(true);
		frame.getNextButton().addActionListener(next = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkOut();
				frame.transferState(new ComputingState());
			}
		});
		frame.getBackButton().setEnabled(true);
		frame.getBackButton().addActionListener(back = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkOut();
				frame.transferState(new ToGetStoneState());
			}
		});
	}

	private void checkOut() {
		frame.getNextButton().setEnabled(false);
		frame.getNextButton().removeActionListener(next);
		frame.getBackButton().setEnabled(false);
		frame.getBackButton().removeActionListener(back);
	}

}
