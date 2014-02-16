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
		frame.addButton("Compute", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setRealMap(frame.getRuneMapShown());
				checkOut();
				frame.transferState(new ComputingState());
			}
		});
		frame.addButton("Read Again", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setRuneMapShown(null);
				frame.setRealMap(null);
				checkOut();
				frame.transferState(new GettingStoneState());
			}
		});
		frame.addButton("Back", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setRuneMapShown(null);
				frame.setRealMap(null);
				checkOut();
				frame.transferState(new ToGetRectangleState());
			}
		});
	}

	private void checkOut() {
		frame.setRuneMapEditable(false);
		frame.removeButtons();
	}
}
