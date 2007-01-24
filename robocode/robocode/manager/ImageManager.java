/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten to support new rendering engine and to use dynamic coloring
 *       instead of static color index
 *     Titus Chen
 *     - Added and integrated RenderCache which removes the eldest image from the
 *       cache of rendered robot images when max. capacity (MAX_NUM_COLORS) of
 *       images has been reached
 *******************************************************************************/
package robocode.manager;


import java.awt.Image;
import java.awt.Color;
import java.util.*;

import robocode.render.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Titus Chen (contributor)
 */
public class ImageManager {
	private Image[] groundImages = new Image[5];

	private RenderImage[][] explosionRenderImages;
	private RenderImage debriseRenderImage;

	private Image bodyImage;
	private Image gunImage;
	private Image radarImage;

	private static final int MAX_NUM_COLORS = 256;

	private HashMap<Color, RenderImage> robotBodyImageCache = new RenderCache<Color, RenderImage>();
	private HashMap<Color, RenderImage> robotGunImageCache = new RenderCache<Color, RenderImage>();
	private HashMap<Color, RenderImage> robotRadarImageCache = new RenderCache<Color, RenderImage>();

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
			groundImages[index] = ImageUtil.getImage("/resources/images/ground/blue_metal/blue_metal_" + index + ".png");
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
			int numExplosion, numFrame;
			String filename;
			
			List<List<RenderImage>> explosions = new ArrayList<List<RenderImage>>();

			boolean done = false;

			for (numExplosion = 1; !done; numExplosion++) {
				List<RenderImage> frames = new ArrayList<RenderImage>();

				for (numFrame = 1;; numFrame++) {
					filename = "/resources/images/explosion/explosion" + numExplosion + '-' + numFrame + ".png";

					if (getClass().getResource(filename) == null) {
						if (numFrame == 1) {
							done = true;
						} else {
							explosions.add(frames);
						}
						break;
					}

					frames.add(new RenderImage(ImageUtil.getImage(filename)));
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
			debriseRenderImage = new RenderImage(ImageUtil.getImage("/resources/images/ground/explode_debris.png"));
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
			bodyImage = ImageUtil.getImage("/resources/images/body.png");	
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
			gunImage = ImageUtil.getImage("/resources/images/turret.png");
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
			radarImage = ImageUtil.getImage("/resources/images/radar.png");
		}
		return radarImage;
	}	
	
	public RenderImage getColoredBodyRenderImage(Color color) {
		RenderImage img = robotBodyImageCache.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getBodyImage(), color));
			robotBodyImageCache.put(color, img);
		}
		return img;
	}

	public RenderImage getColoredGunRenderImage(Color color) {
		RenderImage img = robotGunImageCache.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getGunImage(), color));
			robotGunImageCache.put(color, img);
		}
		return img;
	}

	public RenderImage getColoredRadarRenderImage(Color color) {
		RenderImage img = robotRadarImageCache.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getRadarImage(), color));
			robotRadarImageCache.put(color, img);
		}
		return img;
	}

	/**
	 * Class used for caching rendered robot parts in various colors.
	 * 
	 * @author Titus Chen
	 */
	@SuppressWarnings("serial")
	private class RenderCache<K, V> extends LinkedHashMap<K, V> {
		
		/* Note about initial capacity:
		 * To avoid rehashing (inefficient though probably unavoidable), initial
		 * capacity must be at least 1 greater than the maximum capacity.
		 * However, initial capacities are set to the smallest power of 2 greater
		 * than or equal to the passed argument, resulting in 512 with this code.
		 * I was not aware of this before, but notice: the current implementation
		 * behaves similarly.  The simple solution would be to set maximum capacity
		 * to 255, but the problem with doing so is that in a battle of 256 robots
		 * of different colors, the net result would end up being real-time
		 * rendering due to the nature of access ordering.  However, 256 robot
		 * battles are rarely fought.
		 */
		private static final int INITIAL_CAPACITY = MAX_NUM_COLORS + 1;

		private static final float LOAD_FACTOR = 1;

		public RenderCache() {

			/* The "true" parameter needed for access-order:
			 * when cache fills, the least recently accessed entry is removed
			 */
			super(INITIAL_CAPACITY, LOAD_FACTOR, true);
		}
		
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > MAX_NUM_COLORS;
		}
	}
}
