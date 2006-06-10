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
import java.awt.image.*;
import robocode.render.*;
import robocode.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class ImageManager {
	private Image[] groundImages = new Image[5];
	private Image bulletImage;

	private Image[][][] explodeImages;
	private RenderImage explodeDebriseRenderImage;

	private final int EXPLODE_SIZE_MULTIPLIER = 15;
	private final int EXPLODE_SIZES = 6;
	private int numExplosions;
	private Dimension[] explodeDimension;
	
	private Image robotImage;
	private Image gunImage;
	private Image radarImage;

	private RenderImage robotRenderImage;
	private RenderImage gunRenderImage;
	private RenderImage radarRenderImage;
	
	private int nextColorIndex;
	private final int MAX_NUM_COLORS = 16;
	
	private Color robotColors[] = new Color[MAX_NUM_COLORS];
	private Color gunColors[] = new Color[MAX_NUM_COLORS];
	private Color radarColors[] = new Color[MAX_NUM_COLORS];
	
	private RenderImage coloredRobotRenderImage[] = new RenderImage[MAX_NUM_COLORS];
	private RenderImage coloredGunRenderImage[] = new RenderImage[MAX_NUM_COLORS];
	private RenderImage coloredRadarRenderImage[] = new RenderImage[MAX_NUM_COLORS];
	
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
		getBulletImage();
		getExplosionImage(0, 0, 0);
	}

	/**
	 * Gets the bullet image.
	 * Loads from disk if necessary.
	 * @return the bullet image
	 */
	public Image getBulletImage() {
		if (bulletImage == null) {
			bulletImage = ImageUtil.getBufferedImage(this, "/resources/images/g1bullet.gif");			
		}
		return bulletImage;
	}

	public Image getGroundTileImage(int index) {
		if (groundImages[index] == null) {
			groundImages[index] = ImageUtil.getBufferedImage(this,
					"/resources/images/ground/blue_metal/blue_metal_" + index + ".png");
		}		
		return groundImages[index];
	}
	
	public Dimension getExplodeDimension(int which) {
		if (explodeDimension == null) {
			getExplosionImage(which, 0, 0);
		}
		return explodeDimension[which];
	}

	public int getNumExplosions() {
		return explodeImages.length;
	}

	public int getExplosionFrames(int whichExplosion) {
		return explodeImages[whichExplosion].length;
	}

	/**
	 * Gets an explosion image
	 * Loads from disk if necessary.
	 * @return an explosion image
	 */
	public Image getExplosionImage(int which, int frame, int size) {
		if (explodeImages == null) {
			boolean done = false;

			numExplosions = 0;
			while (!done) {
				if (getClass().getResource(getExplodeFilename(numExplosions, 0)) != null) {
					numExplosions++;
				} else {
					done = true;
				}
			}
		
			explodeImages = new Image[numExplosions][][];
			explodeDimension = new Dimension[numExplosions];
		
			for (int i = 0; i < numExplosions; i++) {
				int f = 1;

				while (getClass().getResource(getExplodeFilename(i, f)) != null) {
					f++;
				}
				explodeImages[i] = new Image[f][EXPLODE_SIZES];
			
				for (int j = 0; j < f; j++) {
					explodeImages[i][j][0] = ImageUtil.getImage(this, getExplodeFilename(i, j));
					if (j == 0) {
						explodeDimension[i] = new Dimension(explodeImages[i][j][0].getWidth(null),
								explodeImages[i][j][0].getHeight(null));
					}
					for (int k = 1; k < EXPLODE_SIZES; k++) {
						int psize = EXPLODE_SIZE_MULTIPLIER * k;

						explodeImages[i][j][k] = new BufferedImage(psize, psize, BufferedImage.TYPE_INT_ARGB);
						Graphics eg = explodeImages[i][j][k].getGraphics();

						eg.drawImage(explodeImages[i][j][0], 0, 0, psize, psize, null);
					}
				}

				// TODO:  Get more efficient about resizing (rather than a cheesy if)
				if (i == 0) {
					for (int j = 0; j < f; j++) {
						for (int k = 1; k < EXPLODE_SIZES; k++) {
							int psize = EXPLODE_SIZE_MULTIPLIER * k;

							explodeImages[i][j][k] = new BufferedImage(psize, psize, BufferedImage.TYPE_INT_ARGB);
							Graphics eg = explodeImages[i][j][k].getGraphics();

							eg.drawImage(explodeImages[i][j][0], 0, 0, psize, psize, null);
						}
					}
				}
			}
		}
		return explodeImages[which][frame][size];
	}

	private String getExplodeFilename(int which, int frame) {
		return "/resources/images/explosion/explosion" + (which + 1) + "-" + (frame + 1) + ".png";
	}

	public RenderImage getExplosionDebrise() {
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
			Image coloredImage = ImageUtil.createColorModifiedImage(getRobotImage(), getRobotColor(colorIndex));

			coloredRobotRenderImage[colorIndex] = new RenderImage(coloredImage);
		}
		return coloredRobotRenderImage[colorIndex];
	}

	public RenderImage getColoredGunRenderImage(int colorIndex) {
		if (colorIndex == -1) {
			return getGunRenderImage();
		}
		if (coloredGunRenderImage[colorIndex] == null) {
			Image coloredImage = ImageUtil.createColorModifiedImage(getGunImage(), getGunColor(colorIndex));

			coloredGunRenderImage[colorIndex] = new RenderImage(coloredImage);
		}
		return coloredGunRenderImage[colorIndex];
	}

	public RenderImage getColoredRadarRenderImage(int colorIndex) {
		if (colorIndex == -1) {
			return getRadarRenderImage();
		}
		if (coloredRadarRenderImage[colorIndex] == null) {
			Image coloredImage = ImageUtil.createColorModifiedImage(getRadarImage(), getRadarColor(colorIndex));

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

	/**
	 * Gets the explodeSizeMultiplier.
	 * @return Returns a int
	 */
	public int getExplodeSizeMultiplier() {
		return EXPLODE_SIZE_MULTIPLIER;
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
		
			Image coloredRobotImage = ImageUtil.createColorModifiedImage(getRobotImage(), robotColor);
			Image coloredGunImage = ImageUtil.createColorModifiedImage(getGunImage(), gunColor);
			Image coloredRadarImage = ImageUtil.createColorModifiedImage(getRadarImage(), radarColor);

			coloredRobotRenderImage[colorIndex] = new RenderImage(coloredRobotImage);
			coloredGunRenderImage[colorIndex] = new RenderImage(coloredGunImage);
			coloredRadarRenderImage[colorIndex] = new RenderImage(coloredRadarImage);

			return colorIndex;
		} else {
			return -1;
		}
	}
}
