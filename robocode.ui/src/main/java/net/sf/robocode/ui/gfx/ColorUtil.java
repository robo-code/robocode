/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.gfx;


import static robocode.util.Utils.isNear;

import java.awt.*;


/**
 * Class used for utilizing colors.
 *
 * @author Flemming N. Larsen (original)
 */
class ColorUtil {

	/**
	 * Return a Color based on a color in RGB565 format.
	 *
	 * @param rgb565 the color in RGB565 format.
	 * @return a Color based on the specifed color in RGB565 format.
	 */
	public static Color toColor(short rgb565) {
		if (rgb565 == 0) {
			return null;
		}
		if (rgb565 == 0x20) {
			return Color.BLACK;
		}
		return new Color(255 * ((rgb565 & 0xF800) >> 11) / 31, 255 * ((rgb565 & 0x07e0) >> 5) / 63,
				255 * (rgb565 & 0x001f) / 31);
	}

	/**
	 * Returns a color in the RGB565 format based on a Color instance.
	 *
	 * @param c the color to convert into a RGB565 color.
	 * @return a color in the RGB565 format based on the specified Color.
	 */
	public static short toRGB565(Color c) {
		if (c == null) {
			return 0;
		}
		short rgb = (short) (((c.getRed() & 0xf8) << 8) | ((c.getGreen() & 0xfc) << 3) | (c.getBlue() >> 3));

		// 0 is reserved for null -> set green (has highest resolution) to 1
		if (rgb == 0) {
			return 0x20;
		}
		// If the color actually was 0x20 then set it to 0x40 (to the nearest green)
		if (rgb == 0x20) {
			return 0x40;
		}
		return rgb;
	}

	/**
	 * Converts a RGB color into a HSL color.
	 *
	 * @param r the red color component.
	 * @param g the green color component.
	 * @param b the blue color component.
	 * @return a {@code float[] { H, S, L }} representing the HSL color.
	 */
	public static float[] fromRGBtoHSL(int r, int g, int b) {
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

			if (isNear(R, max)) {
				H = deltaB - deltaG;
			} else if (isNear(G, max)) {
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
		return new float[] { H, S, L};
	}

	/**
	 * Converts a HSL color into a RGB color integer value.
	 *
	 * @param h the color hue.
	 * @param s the color saturation.
	 * @param l the color lumination.
	 * @return an RGB integer value.
	 */
	public static int fromHSLtoRGB(float h, float s, float l) {
		float m2 = (l <= 0.5f) ? (l * (s + 1)) : (l + s - l * s);
		float m1 = 2 * l - m2;

		int r = (int) (255 * fromHUEtoRGB(m1, m2, h + (1f / 3)));
		int g = (int) (255 * fromHUEtoRGB(m1, m2, h));
		int b = (int) (255 * fromHUEtoRGB(m1, m2, h - (1f / 3)));

		return (((r << 8) | g) << 8) | b;
	}

	private static float fromHUEtoRGB(float m1, float m2, float h) {
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
