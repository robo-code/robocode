/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.battleview;


import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import robocode.util.*;


public class ImageArray {

	private BufferedImage image = null;
	private int width = -1;
	private int height = -1; // of individual images.  Actual height is height * count
	private int count = 1;
	Rectangle clipRect = new Rectangle();

	protected ImageArray(BufferedImage image, int width, int height, int count) {
		this.image = image;
		this.width = width;
		this.height = height;
		this.count = count;
	}
   
	public static BufferedImage createBlankImage(ImageArray source) {
		return new BufferedImage(source.getWidth(), source.getHeight() * source.getCount(), BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Given a source image and a number of rotations,
	 * builds a large vertical image containing the images rotated.
	 * @return RotatedImageArray
	 * @param image java.awt.Image
	 * @param numRotations int
	 */
	public static ImageArray createColoredImageArray(ImageArray source, ImageArray mask, Color c, int cutoff, BufferedImage dest) { {
			if (c == null) {
				return source;
			}
		
			double red = c.getRed() / 256.0;
			double green = c.getGreen() / 256.0;
			double blue = c.getBlue() / 256.0;
	
			BufferedImage newImage, bufferedColorMaskImage;
			int width, height;
	
			if (source.getWidth() == -1 || source.getHeight() == -1) {
				log("Cannot color an ImageArray that is not fully loaded!");
				return null;
			}
	
			width = source.getWidth();
			height = source.getHeight() * source.getCount();
			if (dest == null) {
				throw new NullPointerException("Destination image cannot be null for createColoredImageArray");
			}
			newImage = dest;
		
			Graphics2D gr = newImage.createGraphics();

			gr.drawImage(source.getImage(), new AffineTransform(), null);

			if (mask == null) {
				return new ImageArray(newImage, source.getWidth(), source.getHeight(), source.getCount());
			}

			bufferedColorMaskImage = mask.getImage();

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int maskVal = bufferedColorMaskImage.getRGB(x, y);
					int maskA = (maskVal & 0xff000000) >> 24;
					int maskR = (maskVal & 0x00ff0000) >> 16;
					int maskG = (maskVal & 0x0000ff00) >> 8;
					int maskB = (maskVal & 0x000000ff);

					if (maskR > cutoff && maskG > cutoff && maskB > cutoff) {
						int val = newImage.getRGB(x, y);
						int a = (val & 0xff000000) >> 24;
						int r = (val & 0x00ff0000) >> 16;
						int g = (val & 0x0000ff00) >> 8;
						int b = (val & 0x000000ff);

						double brightest = r;

						if (g > brightest) {
							brightest = g;
						}
						if (b > brightest) {
							brightest = b;
						}

						r = (int) (red * brightest);
						g = (int) (green * brightest);
						b = (int) (blue * brightest);
						int newColor = (a << 24) + (r << 16) + (g << 8) + b;

						newImage.setRGB(x, y, newColor);
					}
				}
			}
			gr.dispose();
			return new ImageArray(newImage, source.getWidth(), source.getHeight(), source.getCount());
		}

	}
	
	/**
	 * Given a source image and a number of rotations,
	 * builds a large vertical image containing the images rotated.
	 * @return RotatedImageArray
	 * @param image java.awt.Image
	 * @param numRotations int
	 */
	public static ImageArray createRotatedImageArray(Image image, int numRotations) {
	
		BufferedImage newImage;
		int width, height;

		width = image.getWidth(null);
		height = image.getHeight(null);
		if (width == -1 || height == -1) {
			log("Cannot rotate an image that is not fully loaded!");
			return null;
		}
		int newWidth, newHeight, newFullHeight;
		double theta45 = Math.PI / 4;

		newWidth = 1 + (int) (width * Math.abs(Math.cos(theta45)) + height * Math.abs(Math.sin(theta45)));
		newHeight = 1 + (int) (width * Math.abs(Math.sin(theta45)) + height * Math.abs(Math.cos(theta45)));
		newFullHeight = newHeight * numRotations;
	
		// log("Creating an image - " + newWidth + "," + newFullHeight);
	
		newImage = new BufferedImage(newWidth, newFullHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = newImage.createGraphics();

		g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		g.addRenderingHints(
				new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
		g.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g.addRenderingHints(
				new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
		g.addRenderingHints(
				new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));

		AffineTransform transform = new AffineTransform();
	
		for (int i = 0; i < numRotations; i++) {
			double theta = 2 * Math.PI * i / numRotations;

			// g.rotate(theta,newWidth/2,newHeight/2);
			// g.rotate(theta,width/2,height/2);
			g.translate((newWidth - width) / 2, (newHeight - height) / 2 + i * newHeight);
			g.rotate(theta, width / 2, height / 2);
			g.drawImage(image, transform, null);
			g.setTransform(transform);
		}
   
		g.dispose();
		return new ImageArray(newImage, newWidth, newHeight, numRotations);
	}

	public void drawCentered(Graphics g, int frame, int x, int y) {
		int offset = frame * height;
		int l = x - width / 2;
		int t = y - height / 2;

		// Note:  The clipRect better not be null,
		// or we'll set it to something invalid here.
		// However, I prefer this issue to constantly allocating rectangles
		// using the other g.getClipBounds() call.
		g.getClipBounds(clipRect);
		g.setClip(l, t, width, height);
		g.drawImage(image, l, t - offset, null);
		g.setClip(clipRect);
	}

	private static void log(String s) {
		Utils.log(s);
	}

	private static void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Gets the count.
	 * @return Returns a int
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Gets the image.
	 * @return Returns a Image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Gets the width.
	 * @return Returns a int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height.
	 * @return Returns a int
	 */
	public int getHeight() {
		return height;
	}

}

