package tosram;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of <code>RuneStoneGetter</code>.
 * 
 * @author johnchen902
 */
public class DefaultImageRuneMapConverter implements ImageRuneMapConverter {

	// The size of expected RuneMap
	private static final int WIDTH = 6;
	private static final int HEIGHT = 5;

	/*-
	 * +--/-\--|
	 * | /   \ |
	 * |/ /-\ \|
	 * | |   | |
	 * |\ \-/ /|
	 * | \   / |
	 * +--\-/--+
	 */
	/*
	 * In the graph above, the rectangle is the area of runestone. The outer
	 * circle is its (inscribed?) circle. The inner circle is the (locus?) of
	 * points being poked. The ratio of radius of two circles is the following
	 * constant.
	 */
	private static final double RADIUS_RATIO = 0.5;

	// The number of points fetched for each RuneStone.
	private static final int FETCH_POINT = 16;

	// The maximal distance between the RuneStone's color and standard color
	private static final int REJECT_THEREHOLD = 2500;

	/**
	 * Create a default RGB-&gt;ReneStone mapping.
	 */
	private static Map<Integer, RuneStone> defaultRGBMap() {
		Map<Integer, RuneStone> map0 = new HashMap<Integer, RuneStone>();
		map0.put(0xE9_28_14, RuneStone.FIRE);
		map0.put(0x1C_CA_2D, RuneStone.EARTH);
		map0.put(0x4A_A6_E8, RuneStone.WATER);
		map0.put(0xCA_9A_00, RuneStone.LIGHT);
		map0.put(0xD2_2C_F4, RuneStone.DARK);
		map0.put(0xEA_84_C0, RuneStone.HEART);
		map0.put(0xFA_67_3D, RuneStone.FIRE);
		map0.put(0x15_E6_1E, RuneStone.EARTH);
		map0.put(0x71_D7_FE, RuneStone.WATER);
		map0.put(0xF5_BE_2D, RuneStone.LIGHT);
		map0.put(0xFE_4B_FC, RuneStone.DARK);
		map0.put(0xFB_D3_DD, RuneStone.HEART);
		map0.put(0xFF_FF_FF, RuneStone.UNKNOWN);
		return Collections.unmodifiableMap(map0);
	}

	@Override
	public RuneMap convert(BufferedImage image) {
		MutableRuneMap stones = new MutableRuneMap(6, 5);
		for (int y = 0; y < HEIGHT; y++)
			for (int x = 0; x < WIDTH; x++) {
				int avgRGB = getSampleRGB(image, x, y);
				RuneStone stone = getRuneStone(avgRGB);
				stones.setRuneStone(x, y, stone);
			}
		return new RuneMap(stones);
	}

	/**
	 * Sample colors of some point in the specified block in the image.
	 * 
	 * @param image
	 *            the image to be sampled
	 * @param x
	 *            the X index of the block
	 * @param y
	 *            the Y index of the block
	 * @return an integer pixel in the default RGB color model and default sRGB
	 *         colorspace
	 */
	private static int getSampleRGB(BufferedImage image, int x, int y) {
		int width = image.getWidth(), height = image.getHeight();
		double centerX = (x + .5) * width / WIDTH;
		double centerY = (y + .5) * height / HEIGHT;
		double radius = Math.min(width / WIDTH, height / HEIGHT)
				* (RADIUS_RATIO * .5);

		int r = 0, g = 0, b = 0;
		for (int i = 0; i < FETCH_POINT; i++) {
			int rgb = image.getRGB(
					(int) (centerX + radius
							* Math.cos(2 * Math.PI * i / FETCH_POINT)),
					(int) (centerY + radius
							* Math.sin(2 * Math.PI * i / FETCH_POINT)));
			r += rgb & 0xFF0000;
			g += rgb & 0x00FF00;
			b += rgb & 0x0000FF;
		}
		r = (r >> 16) / FETCH_POINT;
		g = (g >> 8) / FETCH_POINT;
		b = (b >> 0) / FETCH_POINT;

		return (r << 16) + (g << 8) + (b << 0);
	}

	/**
	 * Choose a <code>RuneStone</code> from the given color.
	 * 
	 * @param rgb
	 *            an integer pixel in the default RGB color model and default
	 *            sRGB colorspace
	 * @param newLine
	 *            (used for debug)
	 * @return the <code>RuneStone</code> chosen by this method
	 */
	private static RuneStone getRuneStone(int rgb) {
		Map<Integer, RuneStone> rgbMap = defaultRGBMap();
		int minDistance = Integer.MAX_VALUE;
		RuneStone bestStone = RuneStone.UNKNOWN;
		for (Map.Entry<Integer, RuneStone> entry : rgbMap.entrySet()) {
			if (minDistance > distance(rgb, entry.getKey())) {
				minDistance = distance(rgb, entry.getKey());
				bestStone = entry.getValue();
			}
		}
		if (minDistance > REJECT_THEREHOLD)
			return RuneStone.UNKNOWN; // reject as no stone is appropriate
		return bestStone;
	}

	/**
	 * The distance between two colors i.e. the square of distance between
	 * point(r1, g1, b1) and point(r2, g2, b2).
	 * 
	 * @param rgb1
	 *            an integer pixel in the default RGB color model and default
	 *            sRGB colorspace
	 * @param rgb2
	 *            an integer pixel in the default RGB color model and default
	 *            sRGB colorspace
	 * @return an integer denote the distance
	 */
	private static int distance(int rgb1, int rgb2) {
		int red1 = (rgb1 >> 16) & 0xFF, green1 = (rgb1 >> 8) & 0xFF, blue1 = rgb1 & 0xFF;
		int red2 = (rgb2 >> 16) & 0xFF, green2 = (rgb2 >> 8) & 0xFF, blue2 = rgb2 & 0xFF;
		return (red1 - red2) * (red1 - red2) + (green1 - green2)
				* (green1 - green2) + (blue1 - blue2) * (blue1 - blue2);
	}
}
