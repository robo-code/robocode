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
 *     - Rewritten to support new rendering engine and to use dynamic coloring
 *       instead of static color index
 *******************************************************************************/
package robocode.manager;


import java.awt.Image;
import java.awt.Color;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;

import robocode.render.*;
import robocode.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class ImageManager {
	private Image[] groundImages = new Image[5];

	private RenderImage[][] explosionRenderImages;
	private RenderImage debriseRenderImage;

	private Image bodyImage;
	private Image gunImage;
	private Image radarImage;

	private RenderImage bodyRenderImage;
	private RenderImage gunRenderImage;
	private RenderImage radarRenderImage;

	private final int MAX_NUM_COLORS = 256;

	private HashMap<Color, RenderImage> coloredBodyRenderImageMap = new HashMap<Color, RenderImage>();
	private HashMap<Color, RenderImage> coloredGunRenderImageMap = new HashMap<Color, RenderImage>();
	private HashMap<Color, RenderImage> coloredRadarRenderImageMap = new HashMap<Color, RenderImage>();

	public ImageManager() {
		initialize();
	}	

	public void initialize() {
		getBodyImage();
		getGunImage();
		getRadarImage();
		getExplosionRenderImage(0, 0);
	}

	public Image getGroundTileImage(int index) {
		if (groundImages[index] == null) {
			groundImages[index] = ImageUtil.getImage(this,
					"/resources/images/ground/blue_metal/blue_metal_" + index + ".png");
		}		
		return groundImages[index];
	}
	
	public int getNumExplosions() {
		return explosionRenderImages.length;
	}

	public int getExplosionFrames(int which) {
		return explosionRenderImages[which].length;
	}

	public RenderImage getExplosionRenderImage(int which, int frame) {
		if (explosionRenderImages == null) {

			boolean done = false;

			int numExplosion, numFrame;

			List<List<RenderImage>> explosions = new ArrayList<List<RenderImage>>();

			for (numExplosion = 1; !done; numExplosion++) {
				List<RenderImage> frames = new ArrayList<RenderImage>();

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
			explosionRenderImages = new RenderImage[numExplosion][];

			for (int i = numExplosion - 1; i >= 0; i--) {
				explosionRenderImages[i] = explosions.get(i).toArray(new RenderImage[0]);
			}
		}
		return explosionRenderImages[which][frame];
	}

	public RenderImage getExplosionDebriseRenderImage() {
		if (debriseRenderImage == null) {
			debriseRenderImage = new RenderImage(ImageUtil.getImage(this, "/resources/images/ground/explode_debris.png"));
		}
		return debriseRenderImage;
	}
	
	/**
	 * Gets the body image
	 * Loads from disk if necessary.
	 * @return the body image
	 */
	private Image getBodyImage() {
		if (bodyImage == null) {
			bodyImage = ImageUtil.getImage(this, "/resources/images/body.png");	
		}
		return bodyImage;
	}
	
	/**
	 * Gets the gun image
	 * Loads from disk if necessary.
	 * @return the gun image
	 */
	private Image getGunImage() {
		if (gunImage == null) {
			gunImage = ImageUtil.getImage(this, "/resources/images/turret.png");
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
			radarImage = ImageUtil.getImage(this, "/resources/images/radar.png");
		}
		return radarImage;
	}	
	
	public RenderImage getColoredBodyRenderImage(Color color) {
		RenderImage img = coloredBodyRenderImageMap.get(color);

		if (img == null) {
			if (coloredBodyRenderImageMap.size() < MAX_NUM_COLORS) {
				img = new RenderImage(ImageUtil.createColouredRobotImage(getBodyImage(), color));
				coloredBodyRenderImageMap.put(color, img);
			} else {
				img = getBodyRenderImage();
			}
		}
		return img;
	}

	public RenderImage getColoredGunRenderImage(Color color) {
		RenderImage img = coloredGunRenderImageMap.get(color);

		if (img == null) {
			if (coloredGunRenderImageMap.size() < MAX_NUM_COLORS) {
				img = new RenderImage(ImageUtil.createColouredRobotImage(getGunImage(), color));
				coloredGunRenderImageMap.put(color, img);
			} else {
				img = getGunRenderImage();
			}
		}
		return img;
	}

	public RenderImage getColoredRadarRenderImage(Color color) {
		RenderImage img = coloredRadarRenderImageMap.get(color);

		if (img == null) {
			if (coloredRadarRenderImageMap.size() < MAX_NUM_COLORS) {
				img = new RenderImage(ImageUtil.createColouredRobotImage(getRadarImage(), color));
				coloredRadarRenderImageMap.put(color, img);
			} else {
				img = getRadarRenderImage();
			}
		}
		return img;
	}

	private RenderImage getBodyRenderImage() {
		if (bodyRenderImage == null) {
			bodyRenderImage = new RenderImage(getBodyImage());
		}
		return bodyRenderImage;
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
}
