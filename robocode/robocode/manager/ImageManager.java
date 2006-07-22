/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten to support new rendering engine
 *******************************************************************************/
package robocode.manager;


import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import robocode.render.*;
import robocode.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class ImageManager {
	private Image[] groundImages = new Image[5];

	private RenderImage[][] explodeRenderImages;
	private RenderImage explodeDebriseRenderImage;

	private Image robotImage;
	private Image gunImage;
	private Image radarImage;

	private RenderImage robotRenderImage;
	private RenderImage gunRenderImage;
	private RenderImage radarRenderImage;

	private final int MAX_NUM_COLORS = 16;
	
	private Color robotColors[] = new Color[MAX_NUM_COLORS];
	private Color gunColors[] = new Color[MAX_NUM_COLORS];
	private Color radarColors[] = new Color[MAX_NUM_COLORS];
	
	private RenderImage coloredRobotRenderImage[] = new RenderImage[MAX_NUM_COLORS];
	private RenderImage coloredGunRenderImage[] = new RenderImage[MAX_NUM_COLORS];
	private RenderImage coloredRadarRenderImage[] = new RenderImage[MAX_NUM_COLORS];
	
	private int nextColorIndex;

	public ImageManager() {
		initialize();
	}	

	public void resetColorIndex() {
		nextColorIndex = 0;
	}

	public void initialize() {
		getRobotImage();
		getGunImage();
		getRadarImage();
		getExplosionRenderImage(0, 0);
	}

	public Image getGroundTileImage(int index) {
		if (groundImages[index] == null) {
			groundImages[index] = ImageUtil.getBufferedImage(this,
					"/resources/images/ground/blue_metal/blue_metal_" + index + ".png");
		}		
		return groundImages[index];
	}
	
	public int getNumExplosions() {
		return explodeRenderImages.length;
	}

	public int getExplosionFrames(int which) {
		return explodeRenderImages[which].length;
	}

	public RenderImage getExplosionRenderImage(int which, int frame) {
		if (explodeRenderImages == null) {

			boolean done = false;

			int numExplosion, numFrame;

			ArrayList explosions = new ArrayList();

			for (numExplosion = 1; !done; numExplosion++) {
				ArrayList frames = new ArrayList();

				for (numFrame = 1;; numFrame++) {
					StringBuffer filename = new StringBuffer("/resources/images/explosion/explosion");

					filename.append(numExplosion).append('-').append(numFrame).append(".png");

					URL url = getClass().getResource(filename.toString());

					if (url == null) {
						if (numFrame == 1) {
							done = true;
						} else {
							explosions.add(frames);
						}
						break;
					}

					try {
						frames.add(new RenderImage(ImageIO.read(url)));
					} catch (Exception e) {
						Utils.log("Could not load image: " + filename);
						break;
					}		
				}
			}
			
			numExplosion = explosions.size();
			explodeRenderImages = new RenderImage[numExplosion][];

			for (int i = numExplosion - 1; i >= 0; i--) {
				ArrayList frames = (ArrayList) explosions.get(i);

				explodeRenderImages[i] = (RenderImage[]) frames.toArray(new RenderImage[0]);
			}
		}
		return explodeRenderImages[which][frame];
	}

	public RenderImage getExplosionDebriseRenderImage() {
		if (explodeDebriseRenderImage == null) {
			explodeDebriseRenderImage = new RenderImage(
					ImageUtil.getBufferedImage(this, "/resources/images/ground/explode_debris.png"));
		}
		return explodeDebriseRenderImage;
	}
	
	/**
	 * Gets the robot image
	 * Loads from disk if necessary.
	 * @return the robot image
	 */
	private Image getRobotImage() {
		if (robotImage == null) {
			robotImage = ImageUtil.getBufferedImage(this, "/resources/images/body.png");	
		}
		return robotImage;
	}
	
	/**
	 * Gets the gun image
	 * Loads from disk if necessary.
	 * @return the gun image
	 */
	private Image getGunImage() {
		if (gunImage == null) {
			gunImage = ImageUtil.getBufferedImage(this, "/resources/images/turret.png");
		}
		return gunImage;
	}
	
	/**
	 * Gets the radar image
	 * Loads from disk if necessary.
	 * @return the radar image
	 */
	private Image getRadarImage() {
		if (radarImage == null) {
			radarImage = ImageUtil.getBufferedImage(this, "/resources/images/radar.png");
		}
		return radarImage;
	}	
	
	public RenderImage getColoredRobotRenderImage(int colorIndex) {
		if (colorIndex == -1) {
			return getRobotRenderImage();
		}
		if (coloredRobotRenderImage[colorIndex] == null) {
			Image coloredImage = ImageUtil.createColouredRobotImage(getRobotImage(), getRobotColor(colorIndex));

			coloredRobotRenderImage[colorIndex] = new RenderImage(coloredImage);
		}
		return coloredRobotRenderImage[colorIndex];
	}

	public RenderImage getColoredGunRenderImage(int colorIndex) {
		if (colorIndex == -1) {
			return getGunRenderImage();
		}
		if (coloredGunRenderImage[colorIndex] == null) {
			Image coloredImage = ImageUtil.createColouredRobotImage(getGunImage(), getGunColor(colorIndex));

			coloredGunRenderImage[colorIndex] = new RenderImage(coloredImage);
		}
		return coloredGunRenderImage[colorIndex];
	}

	public RenderImage getColoredRadarRenderImage(int colorIndex) {
		if (colorIndex == -1) {
			return getRadarRenderImage();
		}
		if (coloredRadarRenderImage[colorIndex] == null) {
			Image coloredImage = ImageUtil.createColouredRobotImage(getRadarImage(), getRadarColor(colorIndex));

			coloredRadarRenderImage[colorIndex] = new RenderImage(coloredImage);
		}
		return coloredRadarRenderImage[colorIndex];
	}

	private RenderImage getRobotRenderImage() {
		if (robotRenderImage == null) {
			robotRenderImage = new RenderImage(getRobotImage());
		}
		return robotRenderImage;
	}

	private RenderImage getGunRenderImage() {
		if (gunRenderImage == null) {
			gunRenderImage = new RenderImage(getGunImage());
		}
		return gunRenderImage;
	}

	private RenderImage getRadarRenderImage() {
		if (radarRenderImage == null) {
			radarRenderImage = new RenderImage(getRadarImage());
		}
		return radarRenderImage;
	}

	public Color getRobotColor(int index) {
		try {
			return robotColors[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Utils.log("No such robot color: " + index);
			return null;
		}
	}

	public Color getGunColor(int index) {
		try {
			return gunColors[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Utils.log("No such gun color: " + index);
			return null;
		}
	}

	public Color getRadarColor(int index) {
		try {
			return radarColors[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Utils.log("No such radar color: " + index);
			return null;
		}
	}

	public synchronized int getNewColorsIndex(Color robotColor, Color gunColor, Color radarColor) {
		if (nextColorIndex < MAX_NUM_COLORS) {
			replaceColorsIndex(nextColorIndex, robotColor, gunColor, radarColor);
			return nextColorIndex++;
		} else {
			return -1;
		}
	}

	public synchronized int replaceColorsIndex(int colorIndex, Color robotColor, Color gunColor, Color radarColor) {
		if (colorIndex == -1) {
			return -1;
		}
		if (colorIndex < MAX_NUM_COLORS) {
			robotColors[colorIndex] = robotColor;
			gunColors[colorIndex] = gunColor;
			radarColors[colorIndex] = radarColor;
		
			Image coloredRobotImage = ImageUtil.createColouredRobotImage(getRobotImage(), robotColor);
			Image coloredGunImage = ImageUtil.createColouredRobotImage(getGunImage(), gunColor);
			Image coloredRadarImage = ImageUtil.createColouredRobotImage(getRadarImage(), radarColor);

			coloredRobotRenderImage[colorIndex] = new RenderImage(coloredRobotImage);
			coloredGunRenderImage[colorIndex] = new RenderImage(coloredGunImage);
			coloredRadarRenderImage[colorIndex] = new RenderImage(coloredRadarImage);

			return colorIndex;
		} else {
			return -1;
		}
	}
}
