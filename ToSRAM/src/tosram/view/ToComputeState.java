package tosram.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ToComputeState implements MFState {

	private MainFrame frame;

	@Override
	public void checkIn(MainFrame f0) {
		frame = f0;
		frame.setStatus("Ready to compute");
		frame.setRuneMapEditable(true);
		frame.setNextText("Compute");
		frame.setNextActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setRealMap(frame.getRuneMapShown());
				checkOut();
				frame.transferState(new ComputingState());
			}
		});
		frame.setBackActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setRuneMapShown(null);
				frame.setRealMap(null);
				checkOut();
				frame.transferState(new ToGetStoneState());
			}
		});
	}

	private void checkOut() {
		frame.setRuneMapEditable(false);
		frame.setNextText(null);
		frame.setNextActionListener(null);
		frame.setBackActionListener(null);
	}
}
