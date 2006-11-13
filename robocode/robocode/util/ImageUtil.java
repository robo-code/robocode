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
	 * Returns an image resource.
	 *
	 * @param filename the filename of the image to load
	 * @return the loaded image
	 */
	public static Image getImage(String filename) {

		URL url = ClassLoader.class.getResource(filename);

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
	 * Create a copy of an robot image into a coloured robot image. The colors of the
	 * input image are changed into the input color, but with the same lumination.
	 *
	 * @param img the source image
	 * @param color the new color that substitutes the old color(s) in the source image
	 * @return a new image
	 */
	public static Image createColouredRobotImage(Image img, Color color) {
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
		private float[] hsl;

		public ColorFilter(Color color) {
			hsl = RGBtoHSL(color.getRed(), color.getGreen(), color.getBlue());
		}

		public int filterRGB(int x, int y, int argb) {
			int r = (argb >> 16) & 0xff;
			int g = (argb >> 8) & 0xff;
			int b = argb & 0xff;

			float[] HSL = RGBtoHSL(r, g, b);

			if (HSL[1] > 0) {
				float L = Math.min(1, (hsl[2] + HSL[2]) / 2 + hsl[2] / 7);				

				return argb & 0xff000000 | HSLtoRGB(hsl[0], hsl[1], L);
			}
			return argb;
		}
	}

	private static float[] RGBtoHSL(int r, int g, int b) {
		float R = (float) r / 255;
		float G = (float) g / 255;
		float B = (float) b / 255;
		
		float min = Math.min(Math.min(R, G), B); // Min. value of RGB
		float max = Math.max(Math.max(R, G), B); // Max. value of RGB
		float delta = max - min; // Delta RGB value

		float L = (max + min) / 2;

		float H, S;
		
		if (delta == 0) { // This is a gray, no chroma...
			H = 0;
			S = 0;
		} else { // Chromatic data...
			if (L < 0.5f) {
				S = delta / (max + min);
			} else {
				S = delta / (2 - max - min);
			}

			float deltaR = (((max - R) / 6) + (delta / 2)) / delta;
			float deltaG = (((max - G) / 6) + (delta / 2)) / delta;
			float deltaB = (((max - B) / 6) + (delta / 2)) / delta;

			if (R == max) {
				H = deltaB - deltaG;
			} else if (G == max) {
				H = (1f / 3) + deltaR - deltaB;
			} else {
				H = (2f / 3) + deltaG - deltaR;
			}

			if (H < 0) {
				H++;
			}
			if (H > 1) {
				H--;
			}
		}
		return new float[] { H, S, L };
	}
	
	private static int HSLtoRGB(float h, float s, float l) {
		float m2 = (l <= 0.5f) ? (l * (s + 1)) : (l + s - l * s);
		float m1 = 2 * l - m2;

		int r = (int) (255 * HUEtoRGB(m1, m2, h + (1f / 3)));
		int g = (int) (255 * HUEtoRGB(m1, m2, h));
		int b = (int) (255 * HUEtoRGB(m1, m2, h - (1f / 3)));

		return (((r << 8) | g) << 8) | b;
	}

	private static float HUEtoRGB(float m1, float m2, float h) {
		if (h < 0) {
			h++;
		}
		if (h > 1) {
			h--;
		}
		if ((h * 6) < 1) {
			return m1 + (m2 - m1) * h * 6;
		}
		if ((h * 2) < 1) {
			return m2;
		}
		if ((h * 3) < 2) {
			return m1 + (m2 - m1) * ((2f / 3) - h) * 6;
		}
		return m1;
	}
}
