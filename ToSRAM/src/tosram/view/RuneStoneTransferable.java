package tosram.view;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import tosram.RuneStone;

/**
 * A <code>Transferable</code> working with <code>RuneStone</code> within local
 * virtual machine.
 * 
 * @author johnchen902
 */
public class RuneStoneTransferable implements Transferable {

	/**
	 * The <code>DataFlavor</code> of this <code>Transferable</code>
	 */
	public static final DataFlavor RUNE_STONE_FLAVOR;

	static {
		try {
			RUNE_STONE_FLAVOR = new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType + ";class="
							+ RuneStone.class.getName());
		} catch (ClassNotFoundException e) {
			throw new Error(e);
		}
	}

	private final RuneStone stone;

	/**
	 * Create a <code>RuneStoneTransferable</code> with specified
	 * <code>RuneStone</code>.
	 * 
	 * @param stone
	 *            a <code>RuneStone</code>
	 */
	public RuneStoneTransferable(RuneStone stone) {
		this.stone = stone;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { RUNE_STONE_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(RUNE_STONE_FLAVOR);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(RUNE_STONE_FLAVOR))
			return stone;
		else
			throw new UnsupportedFlavorException(flavor);
	}
}
