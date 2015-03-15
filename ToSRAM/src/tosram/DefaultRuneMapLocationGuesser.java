package tosram;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DefaultRuneMapLocationGuesser {

	private static int distance(int rgb1, int rgb2) {
		int red1 = (rgb1 >> 16) & 0xFF, green1 = (rgb1 >> 8) & 0xFF, blue1 = rgb1 & 0xFF;
		int red2 = (rgb2 >> 16) & 0xFF, green2 = (rgb2 >> 8) & 0xFF, blue2 = rgb2 & 0xFF;
		return (red1 - red2) * (red1 - red2) + (green1 - green2)
				* (green1 - green2) + (blue1 - blue2) * (blue1 - blue2);
	}

	private static int[] maximumSubArray(int[] array) {
		int bestBegin = 0, bestEnd = -1, bestSum = 0;
		for (int begin = 0, end = 0, sum = 0; end < array.length; end++) {
			if ((sum += array[end]) < 0) {
				begin = end + 1;
				sum = 0;
			} else if (sum > bestSum) {
				bestSum = sum;
				bestBegin = begin;
				bestEnd = end;
			}
		}
		return new int[] { bestBegin, bestEnd };
	}

	/**
	 * Attempt to guess the bounds of RuneMap in the image.
	 * 
	 * @param image
	 *            the source image
	 * @return a Rectangle
	 */
	public static Rectangle guess(BufferedImage image) {
		int[] sumVert = new int[image.getWidth()];
		int[] sumHori = new int[image.getHeight()];
		Arrays.fill(sumVert, -60);
		Arrays.fill(sumHori, -60);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				if (distance(rgb, 0x442211) < 300
						|| distance(rgb, 0x27180C) < 300) {
					sumVert[x]++;
					sumHori[y]++;
				}
			}
		}
		int[] pii1 = maximumSubArray(sumVert);
		int[] pii2 = maximumSubArray(sumHori);
		Rectangle rect = new Rectangle();
		rect.setFrameFromDiagonal(pii1[0], pii2[0], pii1[1] + 1, pii2[1] + 1);
		if (rect.height > rect.width) {
			// the dimension should be near 6:5
			int newHeight = rect.width * 5 / 6;
			rect.y += rect.height - newHeight;
			rect.height = newHeight;
		}
		return rect;
	}
}
