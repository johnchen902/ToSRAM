package tosram.view.strategy;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A <code>Transferable</code> working with {@link StrategyCreater} within local
 * virtual machine.
 * 
 * @author johnchen902
 */
public class StrategyCreaterTransferable implements Transferable {

	/**
	 * The <code>DataFlavor</code> of this <code>Transferable</code>
	 */
	public static final DataFlavor FLAVOR;

	static {
		try {
			FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=" + StrategyCreater.class.getName());
		} catch (ClassNotFoundException e) {
			throw new Error(e);
		}
	}

	private final StrategyCreater data;

	/**
	 * Create a <code>Transferable</code> of the specified
	 * <code>StrategyCreater</code>.
	 * 
	 * @param stone
	 *            a <code>RuneStone</code>
	 */
	public StrategyCreaterTransferable(StrategyCreater fsp) {
		this.data = fsp;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(FLAVOR);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(FLAVOR))
			return data;
		else
			throw new UnsupportedFlavorException(flavor);
	}
}
