package tosram;

import java.awt.image.BufferedImage;

/**
 * Extract runestones from image.
 * 
 * @author johnchen902
 */
public interface ImageRuneMapConverter {
	/**
	 * Extract runestones from the given image.
	 * 
	 * @param image
	 *            the image that contains runestones
	 * @return the extracted runestones
	 */
	public RuneMap convert(BufferedImage image);
}
