/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.ui;


import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.gfx.ImageUtil;
import net.sf.robocode.ui.gfx.RenderImage;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Titus Chen (contributor)
 */
public class ImageManager implements IImageManager {
	
	private final ISettingsManager properties;

	private Image[] groundImages;

	private RenderImage[][] explosionRenderImages;
	private RenderImage debriseRenderImage;

	private Image bodyImage;
	private Image gunImage;
	private Image radarImage;
	
	// Naval
	private Image navalImageBody;
	private Image navalImageGun;
	private Image navalImageRadar;
	private Image navalImageMine;
	private Image navalImageMineComponent;

	private static final int MAX_NUM_COLORS = 256;

	private HashMap<Integer, RenderImage> robotBodyImageCache;
	private HashMap<Integer, RenderImage> robotGunImageCache;
	private HashMap<Integer, RenderImage> robotRadarImageCache;
	
	private HashMap<Integer, RenderImage> navalImageCacheBody;
	private HashMap<Integer, RenderImage> navalImageCacheGun;
	private HashMap<Integer, RenderImage> navalImageCacheRadar;
	private HashMap<Integer, RenderImage> navalImageCacheMine;
	private HashMap<Integer, RenderImage> navalImageCacheMineComponent;

	public ImageManager(ISettingsManager properties) {
		this.properties = properties;
	}

	public void initialize() {
		// Note that initialize could be called in order to reset all images (image buffering)

		// Reset image cache
		groundImages = new Image[5];
		explosionRenderImages = null;
		debriseRenderImage = null;
		bodyImage = null;
		gunImage = null;
		radarImage = null;
		robotBodyImageCache = new RenderCache<Integer, RenderImage>();
		robotGunImageCache = new RenderCache<Integer, RenderImage>();
		robotRadarImageCache = new RenderCache<Integer, RenderImage>();

		// Read images into the cache
		getBodyImage();
		getGunImage();
		getRadarImage();
		getExplosionRenderImage(0, 0);
		
		// Naval, reset
		{
			navalImageBody = null;
			navalImageGun = null;
			navalImageRadar = null;
			navalImageMine = null;
			navalImageMineComponent = null;
			
			navalImageCacheBody = new RenderCache<Integer, RenderImage>();
			navalImageCacheGun = new RenderCache<Integer, RenderImage>();
			navalImageCacheRadar = new RenderCache<Integer, RenderImage>();
			navalImageCacheMine = new RenderCache<Integer, RenderImage>();
			navalImageCacheMineComponent = new RenderCache<Integer, RenderImage>();

			getNavalImageBody();
			getNavalImageGun();
			getNavalImageRadar();
			getNavalImageMine();
			getNavalImageMineComponent();
		}
	}

	public Image getGroundTileImage(int index) {
		if (HiddenAccess.getNaval()) {
			groundImages[index] = getImage("/net/sf/robocode/ui/images/naval/ground/blue_metal_" + index + ".png");
		} else {
			groundImages[index] = getImage("/net/sf/robocode/ui/images/ground/blue_metal/blue_metal_" + index + ".png");
		}
		
		return groundImages[index];
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
					filename = "/net/sf/robocode/ui/images/explosion/explosion" + numExplosion + '-' + numFrame + ".png";

					if (ImageManager.class.getResource(filename) == null) {
						if (numFrame == 1) {
							done = true;
						} else {
							explosions.add(frames);
						}
						break;
					}

					frames.add(new RenderImage(getImage(filename)));
				}
			}

			numExplosion = explosions.size();
			explosionRenderImages = new RenderImage[numExplosion][];

			for (int i = numExplosion - 1; i >= 0; i--) {
				explosionRenderImages[i] = explosions.get(i).toArray(new RenderImage[explosions.size()]);
			}
		}
		return explosionRenderImages[which][frame];
	}

	public RenderImage getExplosionDebriseRenderImage() {
		if (debriseRenderImage == null) {
			debriseRenderImage = new RenderImage(getImage("/net/sf/robocode/ui/images/ground/explode_debris.png"));
		}
		return debriseRenderImage;
	}

	private Image getImage(String filename) {
		Image image = ImageUtil.getImage(filename);

		if (properties.getOptionsRenderingBufferImages()) {
			image = ImageUtil.getBufferedImage(image);
		}
		return image;
	}

	/**
	 * Gets the body image
	 * Loads from disk if necessary.
	 *
	 * @return the body image
	 */
	private Image getBodyImage() {
		if (bodyImage == null) {
			bodyImage = getImage("/net/sf/robocode/ui/images/body.png");
		}
		return bodyImage;
	}
	private Image getNavalImageBody() {
		if (navalImageBody == null) {
			navalImageBody = getImage("/net/sf/robocode/ui/images/naval/body.png");
		}
		return navalImageBody;
	}

	/**
	 * Gets the gun image
	 * Loads from disk if necessary.
	 *
	 * @return the gun image
	 */
	private Image getGunImage() {
		if (gunImage == null) {
			gunImage = getImage("/net/sf/robocode/ui/images/turret.png");
		}
		return gunImage;
	}
	private Image getNavalImageGun() {
		if (navalImageGun == null) {
			navalImageGun = getImage("/net/sf/robocode/ui/images/naval/turret.png");
		}
		return navalImageGun;
	}
	private Image getNavalImageMine(){
		if(navalImageMine == null){
			navalImageMine = getImage("/net/sf/robocode/ui/images/naval/Mine.png");
		}
		return navalImageMine;
	}
	private Image getNavalImageMineComponent(){
		if(navalImageMineComponent == null){
			navalImageMineComponent = getImage("/net/sf/robocode/ui/images/naval/MComponent.png");
		}
		return navalImageMineComponent;
	}

	/**
	 * Gets the radar image
	 * Loads from disk if necessary.
	 *
	 * @return the radar image
	 */
	private Image getRadarImage() {
		if (radarImage == null) {
			radarImage = getImage("/net/sf/robocode/ui/images/radar.png");
		}
		return radarImage;
	}
	private Image getNavalImageRadar() {
		if (navalImageRadar == null) {
			navalImageRadar = getImage("/net/sf/robocode/ui/images/naval/radar.png");
		}
		return navalImageRadar;
	}

	public RenderImage getColoredBodyRenderImage(Integer color) {
		RenderImage img = robotBodyImageCache.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getBodyImage(), new Color(color, true)));
			robotBodyImageCache.put(color, img);
		}

		return img;
	}
	public RenderImage getColoredBodyRenderNavalImage(Integer color) {
		RenderImage img = navalImageCacheBody.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getNavalImageBody(), new Color(color, true)));
			navalImageCacheBody.put(color, img);
		}

		return img;
	}

	public RenderImage getColoredGunRenderImage(Integer color) {
		RenderImage img = robotGunImageCache.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getGunImage(), new Color(color, true)));
			robotGunImageCache.put(color, img);
		}
		return img;
	}
	public RenderImage getColoredGunRenderNavalImage(Integer color) {
		RenderImage img = navalImageCacheGun.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getNavalImageGun(), new Color(color, true)));
			navalImageCacheGun.put(color, img);
		}
		return img;
	}
	public RenderImage getColoredMineRenderNavalImage(Integer color){
		RenderImage img = navalImageCacheMine.get(color);
		
		if(img == null){
			img = new RenderImage(ImageUtil.createColouredRobotImage(getNavalImageMine(), new Color(color,true)));
			navalImageCacheMine.put(color, img);
		}
		return img;
	}
	public RenderImage getColoredMineComponentRenderNavalImage(Integer color){
		RenderImage img = navalImageCacheMineComponent.get(color);
		
		if(img == null){
			img = new RenderImage(ImageUtil.createColouredRobotImage(getNavalImageMineComponent(), new Color(color,true)));
			navalImageCacheMineComponent.put(color, img);
		}
		return img;
	}

	public RenderImage getColoredRadarRenderImage(Integer color) {
		RenderImage img = robotRadarImageCache.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getRadarImage(), new Color(color, true)));
			robotRadarImageCache.put(color, img);
		}
		return img;
	}
	public RenderImage getColoredRadarRenderNavalImage(Integer color) {
		RenderImage img = navalImageCacheRadar.get(color);

		if (img == null) {
			img = new RenderImage(ImageUtil.createColouredRobotImage(getNavalImageRadar(), new Color(color, true)));
			navalImageCacheRadar.put(color, img);
		}
		return img;
	}

	/**
	 * Class used for caching rendered robot parts in various colors.
	 *
	 * @author Titus Chen
	 */
	@SuppressWarnings("serial")
	private static class RenderCache<K, V> extends LinkedHashMap<K, V> {

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

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > MAX_NUM_COLORS;
		}
	}
}
