package tosram.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ToMoveState implements MFState {

	private MainFrame frame;
	private final String finalStatus;

	public ToMoveState(String finalStatus) {
		this.finalStatus = finalStatus;
	}

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("Ready to move: " + finalStatus);
		frame.setNextText("Move");
		frame.setNextActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkOut();
				frame.transferState(new MovingState());
			}
		});
		frame.setBackActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkOut();
				frame.setPath(null);
				frame.setRuneMapShown(frame.getRealMap());
				frame.transferState(new ToComputeState());
			}
		});
	}

	private void checkOut() {
		frame.setNextText(null);
		frame.setNextActionListener(null);
		frame.setBackActionListener(null);
	}
}
