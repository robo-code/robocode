/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.util;


import java.awt.*;
import java.awt.image.*;
import java.net.URL;

import javax.imageio.ImageIO;


/**
 * Image utility
 *
 * @author Flemming N. Larsen (original)
 */
public class ImageUtil {

	/**
	 * Returns an image resource that reside on disk archive.
	 *
	 * @param obj an object used for loading the image thru it's classloader
	 * @param filename the filename of the image to load
	 * @return the loaded image
	 */
	public static Image getImage(Object obj, String filename) {

		URL url = obj.getClass().getResource(filename);

		if (url == null) {
			Utils.log("Could not load image because of invalid filename: " + filename);
			return null;
		}

		try {
			return ImageIO.read(url);
		} catch (Exception e) {
			Utils.log("Could not load image: " + filename);
			return null;
		}
	}

	/**
	 * Returns an buffered image resource that reside in memory.
	 *
	 * @param obj an object used for loading the image thru it's classloader
	 * @param filename the filename of the image to load
	 * @return the loaded image that is buffered
	 */
	public static BufferedImage getBufferedImage(Object obj, String filename) {
		return createBufferedImage(getImage(obj, filename));
	}

	/**
	 * Creates a buffered image that resides in memory based on another image.
	 *
	 * @param img the image to create the buffer for
	 * @return an buffered image of the specified image
	 */
	public static BufferedImage createBufferedImage(Image img) {
		if (img == null) {
			return null;
		}
		
		BufferedImage bufImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		bufImage.getGraphics().drawImage(img, 0, 0, null);

		return bufImage;
	}
	
	/**
	 * Creates a new image where the colors of the source image are changed
	 * into one single other color.
	 *
	 * @param img the source image
	 * @param color the new color that substitutes the old color(s) in the source image
	 * @return a new image
	 */
	public static Image createColorModifiedImage(Image img, Color color) {
		return (color == null)
				? img
				: (img == null)
						? null
						: Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(img.getSource(), new ColorFilter(color)));
	}

	/**
	 * A color filter used for changing colors into another color.
	 *
	 * @author Flemming N. Larsen
	 */
	private static class ColorFilter extends RGBImageFilter {
		private float[] _hsb;

		public ColorFilter(Color color) {
			_hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		}

		public int filterRGB(int x, int y, int argb) {
			int r = (argb >> 16) & 0xff;
			int g = (argb >> 8) & 0xff;
			int b = argb & 0xff;

			float[] hsb = Color.RGBtoHSB(r, g, b, null);

			if (hsb[1] > 0f) {
				if (_hsb[1] > 0f) {
					return (argb & 0xff000000) | HSLtoRGB(_hsb[0], _hsb[1], hsb[2]);
				}
				return (argb & 0xff000000) | HSLtoRGB(0f, 0f, (hsb[2] + _hsb[2]) / 2);
			}
			return argb;
		}
	}

	/**
	 * Return RGB equivalent of HSL value. In the HSL color model, 0 hue (red) and 100% luminance means White; to
	 * <tt>Color.HSBtoRGB()</tt>, 0 hue and 100% luminance means Red.
	 *
	 * @param h hue, in the range 0..1
	 * @param s saturation, in the range 0..1
	 * @param l luminance, in the range 0..1
	 * @return rgb equivalent of the hsl values
	 *
	 * @see Color#HSBtoRGB()
	 */
	private static final int HSLtoRGB(float h, float s, float l) {
		float m2 = (l <= 0.5) ? (l * (s + 1f)) : (l + s - l * s);
		float m1 = 2f * l - m2;

		int r = (int) (255 * HUEtoRGB(m1, m2, h + (1f / 3f)));
		int g = (int) (255 * HUEtoRGB(m1, m2, h));
		int b = (int) (255 * HUEtoRGB(m1, m2, h - (1f / 3f)));

		return (((r << 8) | g) << 8) | b;
	}

	/**
	 * Copied from <a href=http://www.w3.org/TR/css3-color/>http://www.w3.org/TR/css3-color/</a>.
	 */
	private static float HUEtoRGB(float m1, float m2, float h) {
		if (h < 0f) {
			h += 1f;
		}
		if (h > 1f) {
			h -= 1f;
		}
		if ((h * 6f) < 1f) {
			return m1 + (m2 - m1) * h * 6f;
		}
		if ((h * 2f) < 1f) {
			return m2;
		}
		if ((h * 3f) < 2f) {
			return m1 + (m2 - m1) * ((2f / 3f) - h) * 6f;
		}
		return m1;
	}
}
