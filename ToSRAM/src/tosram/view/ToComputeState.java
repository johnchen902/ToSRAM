package tosram.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ToComputeState implements MFState {

	private MainFrame frame;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("To compute...");
		frame.setNextActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkOut();
				frame.transferState(new ComputingState());
			}
		});
		frame.setBackActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkOut();
				frame.transferState(new ToGetStoneState());
			}
		});
	}

	private void checkOut() {
		frame.setNextActionListener(null);
		frame.setBackActionListener(null);
	}

}
