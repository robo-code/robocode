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
package robocode.manager;


import java.awt.*;
import java.awt.image.*;
import robocode.battleview.ImageArray;

import robocode.util.*;


public class ImageManager {
	private Image floorImage = null;
	private Image bulletImage = null;
	private Image[][][] explodeImages = null;
	private final int explodeSizeMultiplier = 15;
	private final int explodeSizes = 6;
	private int numExplosions;
	private Dimension[] explodeDimension = null;
	
	private Image robotImage = null;
	private Image robotMaskImage = null;
	private Image gunImage = null;
	private Image gunMaskImage = null;
	private Image radarImage = null;
	private Image radarMaskImage = null;
	
	private ImageArray robotImageArray = null;
	private ImageArray robotMaskImageArray = null;
	private ImageArray gunImageArray = null;
	private ImageArray gunMaskImageArray = null;
	private ImageArray radarImageArray = null;
	private ImageArray radarMaskImageArray = null;
	
	private int nextColorIndex = 0;
	private final int maxColors = 16;
	
	private Color robotColors[] = new Color[maxColors];
	private Color gunColors[] = new Color[maxColors];
	private Color radarColors[] = new Color[maxColors];
	
	private ImageArray coloredRobotImageArray[] = new ImageArray[maxColors];
	private ImageArray coloredGunImageArray[] = new ImageArray[maxColors];
	private ImageArray coloredRadarImageArray[] = new ImageArray[maxColors];
	
	private BufferedImage robotImageCache[] = new BufferedImage[maxColors];
	private BufferedImage gunImageCache[] = new BufferedImage[maxColors];
	private BufferedImage radarImageCache[] = new BufferedImage[maxColors];
	
	private final int numRotations = 36;
	
	private Component component = null;

	private int WAITTIME = 15000;
	
	private int MASKCUTOFF = 130;
	
	public ImageManager() {}	

	public void resetColorIndex() {
		nextColorIndex = 0;
	}

	public void initialize(Component component) {
		this.component = component;
		getRobotImage();
		getRobotMaskImage();
		getGunImage();
		getGunMaskImage();
		getRadarImage();
		getRadarMaskImage();
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
			MediaTracker mt = new MediaTracker(component);

			try {
				bulletImage = Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/resources/images/g1bullet.gif"));
			} catch (Exception e) {
				log("Exception loading image bullet.gif: " + e);
			}
	
			mt.addImage(bulletImage, 0);
			mt.statusID(0, true);
			try {
				mt.waitForID(0, WAITTIME);
			} catch (InterruptedException e) {}

			if (mt.statusID(0, true) != MediaTracker.COMPLETE) {
				log("Error fetching bullet image. ");
				return null;
			}
		}
		return bulletImage;
	}

	/**
	 * Gets the floor image.
	 * Loads from disk if necessary.
	 * @return the floor image
	 */
	public Image getFloorImage() {
		
		if (floorImage == null) {
			MediaTracker mt = new MediaTracker(component);

			try {
				floorImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("resources/images/floor.gif"));
			} catch (Exception e) {
				log("Exception loading image floor.gif: " + e);
			}
	
			mt.addImage(floorImage, 0);
			mt.statusID(0, true);
			try {
				mt.waitForID(0, WAITTIME);
			} catch (InterruptedException e) {}
			if (mt.statusID(0, true) != MediaTracker.COMPLETE) {
				log("Error fetching floor image. ");
				return null;
			}
		}
		return floorImage;
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
			MediaTracker mt = new MediaTracker(component);

			boolean done = false;

			numExplosions = 0;
			while (!done) {
				if (getClass().getResource(getExplodeFilename(numExplosions, 0)) != null) {
					numExplosions++;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}
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
				explodeImages[i] = new Image[f][explodeSizes];
			
				for (int j = 0; j < f; j++) {
					try {
						explodeImages[i][j][0] = Toolkit.getDefaultToolkit().getImage(
								getClass().getResource(getExplodeFilename(i, j)));
						mt.addImage(explodeImages[i][j][0], i);
						mt.statusID(i, true);
						try {
							mt.waitForID(i, WAITTIME);
						} catch (InterruptedException e) {}
						if (j == 0) {
							explodeDimension[i] = new Dimension(explodeImages[i][j][0].getWidth(null),
									explodeImages[i][j][0].getHeight(null));
						}
					} catch (Exception e) {
						log("Exception loading explosion image " + i + "," + j, e);
					}
					for (int k = 1; k < explodeSizes; k++) {
						int psize = explodeSizeMultiplier * k;

						explodeImages[i][j][k] = new BufferedImage(psize, psize, BufferedImage.TYPE_INT_ARGB);
						Graphics eg = explodeImages[i][j][k].getGraphics();

						eg.drawImage(explodeImages[i][j][0], 0, 0, psize, psize, null);
					}
				}
				if (mt.statusID(i, true) != MediaTracker.COMPLETE) {
					log("Error fetching explosion images. ");
					return null;
				}

				// TODO:  Get more efficient about resizing (rather than a cheesy if)
				if (i == 0) {
					for (int j = 0; j < f; j++) {
						for (int k = 1; k < explodeSizes; k++) {
							int psize = explodeSizeMultiplier * k;

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
		// String s = "" + (frame+1);
		// while (s.length() < 3)
		// s = "0" + s;

		return "/resources/images/explosion/explosion" + (which + 1) + "-" + (frame + 1) + ".png";
	}

	/**
	 * Gets the robot image
	 * Loads from disk if necessary.
	 * @return the robot image
	 */
	private Image getRobotImage() {
		if (robotImage == null) {
			MediaTracker mt = new MediaTracker(component);

			try {
				// robotImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/g1body.gif"));
				robotImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/body.png"));
			} catch (Exception e) {
				log("Exception loading image g1body.gif: " + e);
			}
	
			mt.addImage(robotImage, 0);
			mt.statusID(0, true);
			try {
				mt.waitForID(0, WAITTIME);
			} catch (InterruptedException e) {}

			if (mt.statusID(0, true) != MediaTracker.COMPLETE) {
				log("Error fetching robot image. ");
				return null;
			}
		}
		return robotImage;
	}
	
	/**
	 * Gets the robot colormask image
	 * Loads from disk if necessary.
	 * @return the robot colormask image
	 */
	private Image getRobotMaskImage() {
		if (robotMaskImage == null) {
			MediaTracker mt = new MediaTracker(component);

			try {
				// robotMaskImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/g1body.colormask.gif"));
				robotMaskImage = Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/resources/images/bodymask.gif"));
			} catch (Exception e) {
				log("Exception loading image g1body.colormask.gif: " + e);
			}
	
			mt.addImage(robotMaskImage, 0);
			mt.statusID(0, true);
			try {
				mt.waitForID(0, WAITTIME);
			} catch (InterruptedException e) {}

			if (mt.statusID(0, true) != MediaTracker.COMPLETE) {
				log("Error fetching robot mask image. ");
				return null;
			}
		}
		return robotMaskImage;
	}
	
	/**
	 * Gets the gun image
	 * Loads from disk if necessary.
	 * @return the gun image
	 */
	private Image getGunImage() {
		if (gunImage == null) {
			MediaTracker mt = new MediaTracker(component);

			try {
				// gunImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/g1turret.gif"));
				gunImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/turret.png"));
			} catch (Exception e) {
				log("Exception loading image g1turret.gif: " + e);
			}
	
			mt.addImage(gunImage, 0);
			mt.statusID(0, true);
			try {
				mt.waitForID(0, WAITTIME);
			} catch (InterruptedException e) {}

			if (mt.statusID(0, true) != MediaTracker.COMPLETE) {
				log("Error fetching gun image. ");
				return null;
			}
		}
		return gunImage;
	}
	
	/**
	 * Gets the gun colormask image
	 * Loads from disk if necessary.
	 * @return the gun colormask image
	 */
	private Image getGunMaskImage() {
		if (gunMaskImage == null) {
			MediaTracker mt = new MediaTracker(component);

			try {
				// gunMaskImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/g1turret.colormask.gif"));
				gunMaskImage = Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/resources/images/turretmask.gif"));
			} catch (Exception e) {
				log("Exception loading image g1turret.colormask.gif: " + e);
			}
	
			mt.addImage(gunMaskImage, 0);
			mt.statusID(0, true);
			try {
				mt.waitForID(0, WAITTIME);
			} catch (InterruptedException e) {}

			if (mt.statusID(0, true) != MediaTracker.COMPLETE) {
				log("Error fetching gun mask image. ");
				return null;
			}
		}
		return gunMaskImage;
	}
	
	/**
	 * Gets the radar image
	 * Loads from disk if necessary.
	 * @return the radar image
	 */
	private Image getRadarImage() {
		if (radarImage == null) {
			MediaTracker mt = new MediaTracker(component);

			try {
				// radarImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/g1radar.gif"));
				radarImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/radar.png"));
			} catch (Exception e) {
				log("Exception loading image g1radar.gif: " + e);
			}
	
			mt.addImage(radarImage, 0);
			mt.statusID(0, true);
			try {
				mt.waitForID(0, WAITTIME);
			} catch (InterruptedException e) {}

			if (mt.statusID(0, true) != MediaTracker.COMPLETE) {
				log("Error fetching radar image. ");
				return null;
			}
		}
		return radarImage;
	}
	
	/**
	 * Gets the radar colormask image
	 * Loads from disk if necessary.
	 * @return the radar colormask image
	 */
	private Image getRadarMaskImage() {
		if (radarMaskImage == null) {
			MediaTracker mt = new MediaTracker(component);

			try {
				// radarMaskImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/g1radar.colormask.gif"));
				radarMaskImage = Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/resources/images/radarmask.gif"));
			} catch (Exception e) {
				log("Exception loading image g1radar.colorimage.gif: " + e);
			}
	
			mt.addImage(radarMaskImage, 0);
			mt.statusID(0, true);
			try {
				mt.waitForID(0, WAITTIME);
			} catch (InterruptedException e) {}

			if (mt.statusID(0, true) != MediaTracker.COMPLETE) {
				log("Error fetching radar colormask image. ");
				return null;
			}
		}
		return radarMaskImage;
	}
	
	public ImageArray getColoredRobotImageArray(int colorIndex) {
		if (colorIndex == -1) {
			return getRobotImageArray();
		}
		if (coloredRobotImageArray[colorIndex] == null) {
			coloredRobotImageArray[colorIndex] = ImageArray.createColoredImageArray(getRobotImageArray(),
					getRobotMaskImageArray(), getRobotColor(colorIndex), MASKCUTOFF, null);
		}
		return coloredRobotImageArray[colorIndex];
	}

	public ImageArray getColoredGunImageArray(int colorIndex) {
		if (colorIndex == -1) {
			return getGunImageArray();
		}
		if (coloredGunImageArray[colorIndex] == null) {
			coloredGunImageArray[colorIndex] = ImageArray.createColoredImageArray(getGunImageArray(),
					getGunMaskImageArray(), getGunColor(colorIndex), MASKCUTOFF, null);
		}
		return coloredGunImageArray[colorIndex];
	}

	public ImageArray getColoredRadarImageArray(int colorIndex) {
		if (colorIndex == -1) {
			return getRadarImageArray();
		}
		if (coloredRadarImageArray[colorIndex] == null) {
			coloredRadarImageArray[colorIndex] = ImageArray.createColoredImageArray(getRadarImageArray(),
					getRadarMaskImageArray(), getRadarColor(colorIndex), MASKCUTOFF, null);
		}
		return coloredRadarImageArray[colorIndex];
	}

	private ImageArray getRobotImageArray() {
		if (robotImageArray == null) {
			robotImageArray = ImageArray.createRotatedImageArray(getRobotImage(), numRotations);
		}
		return robotImageArray;
	}

	private ImageArray getRobotMaskImageArray() {
		if (robotMaskImageArray == null) {
			robotMaskImageArray = ImageArray.createRotatedImageArray(getRobotMaskImage(), numRotations);
		}
		return robotMaskImageArray;
	}

	private ImageArray getGunImageArray() {
		if (gunImageArray == null) {
			gunImageArray = ImageArray.createRotatedImageArray(getGunImage(), numRotations);
		}
		return gunImageArray;
	}

	private ImageArray getGunMaskImageArray() {
		if (gunMaskImageArray == null) {
			gunMaskImageArray = ImageArray.createRotatedImageArray(getGunMaskImage(), numRotations);
		}
		return gunMaskImageArray;
	}

	private ImageArray getRadarImageArray() {
		if (radarImageArray == null) {
			radarImageArray = ImageArray.createRotatedImageArray(getRadarImage(), numRotations);
		}
		return radarImageArray;
	}

	private ImageArray getRadarMaskImageArray() {
		if (radarMaskImageArray == null) {
			radarMaskImageArray = ImageArray.createRotatedImageArray(getRadarMaskImage(), numRotations);
		}
		return radarMaskImageArray;
	}

	/* static Color black 
	 The color black. 
	 static Color blue 
	 The color blue. 
	 static Color cyan 
	 The color cyan. 
	 static Color darkGray 
	 The color dark gray. 
	 static Color gray 
	 The color gray. 
	 static Color green 
	 The color green. 
	 static Color lightGray 
	 The color light gray. 
	 static Color magenta 
	 The color magenta. 
	 static Color orange 
	 The color orange. 
	 static Color pink 
	 The color pink. 
	 static Color red 
	 The color red. 
	 static Color white 
	 The color white. 
	 static Color yellow 
	 The color yellow. 

	 */

	/*
	 MediaTracker mt = new MediaTracker(this);

	 RobotGraphics robotGraphics;
	 double mindiff = .5;
	 
	 for (int i = 0; i < robotGraphicsVector.size(); i++)
	 {
	 setStatus("Please wait, building images..." + (robotGraphicsVector.size() - i - 1));
	 robotGraphics = (RobotGraphics)robotGraphicsVector.elementAt(i);

	 try {
	 robotGraphics.robotImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/" + robotGraphics.getRobotImageName()));
	 mt.addImage(robotGraphics.robotImage,i);
	 } catch (Exception e) {
	 log("Exception loading image " + robotGraphics.getRobotImageName() + ": " + e);
	 }
	 
	 try {
	 robotGraphics.gunImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/" + robotGraphics.getGunImageName()));
	 mt.addImage(robotGraphics.gunImage,i);
	 } catch (Exception e) {
	 log("Exception loading image " + robotGraphics.getGunImageName() + ": " + e);
	 }
	 
	 try {
	 robotGraphics.radarImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/" + robotGraphics.getRadarImageName()));
	 mt.addImage(robotGraphics.radarImage,i);
	 } catch (Exception e) {
	 log("Exception loading image " + robotGraphics.getRadarImageName() + ": " + e);
	 }

	 if (robotGraphics.getRobotImageColorMaskName() != null) {
	 try {
	 robotGraphics.robotImageColorMask = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/" + robotGraphics.getRobotImageColorMaskName()));
	 mt.addImage(robotGraphics.robotImageColorMask,i);
	 } catch (Exception e) {
	 log("Exception loading image " + robotGraphics.getRobotImageColorMaskName() + ": " + e);
	 }
	 }
	 
	 if (robotGraphics.getGunImageColorMaskName() != null) {
	 try {
	 robotGraphics.gunImageColorMask = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/" + robotGraphics.getGunImageColorMaskName()));
	 mt.addImage(robotGraphics.gunImageColorMask,i);
	 } catch (Exception e) {
	 log("Exception loading image " + robotGraphics.getGunImageColorMaskName() + ": " + e);
	 }
	 }
	 
	 if (robotGraphics.getRadarImageColorMaskName() != null) {
	 try {
	 robotGraphics.radarImageColorMask = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/" + robotGraphics.getRadarImageColorMaskName()));
	 mt.addImage(robotGraphics.radarImageColorMask,i);
	 } catch (Exception e) {
	 log("Exception loading image " + robotGraphics.getRadarImageColorMaskName() + ": " + e);
	 }
	 }
	 
	 mt.statusID(i,true);
	 try {
	 mt.waitForID(i,WAITTIME);
	 } catch (InterruptedException e) {
	 }
	 if (mt.statusID(i,true) != MediaTracker.COMPLETE)
	 {
	 log("Error fetching image for robot(s): "); // + robotGraphics.getRobotNames());
	 return false;
	 }
	 
	 robotGraphics.robotImages = new Image[robotGraphics.numImages];
	 robotGraphics.gunImages = new Image[robotGraphics.numImages];
	 robotGraphics.radarImages = new Image[robotGraphics.numImages];

	 int count = 0;
	 boolean validColor = false;
	 while (!validColor)
	 {
	 validColor = true;
	 robotGraphics.redColor = Math.random();
	 robotGraphics.greenColor = Math.random();
	 robotGraphics.blueColor = Math.random();
	 double zero = Math.random();
	 if (zero < .33) robotGraphics.redColor = 0;
	 else if (zero < .66) robotGraphics.greenColor = 0;
	 else robotGraphics.blueColor = 0;

	 for (int j = 0; j < i && validColor == true; j++)
	 {
	 RobotGraphics cg = (RobotGraphics)(robotGraphicsVector.elementAt(j));
	 double rd, gd, bd;
	 rd = Math.abs(robotGraphics.redColor - cg.redColor);
	 gd = Math.abs(robotGraphics.greenColor - cg.greenColor);
	 bd = Math.abs(robotGraphics.blueColor - cg.blueColor);
	 if (rd < mindiff && gd < mindiff && bd < mindiff)
	 validColor = false;
	 }

	 count++;
	 if (count > 100)
	 {
	 if (mindiff < .1)
	 {
	 validColor = true;
	 log("Too many robots to find different colors, some may look similar.");
	 }
	 else {
	 mindiff -= .1;
	 count = 0;
	 //		 	 	  	log("Colors will be different by at least " + mindiff);
	 }
	 }
	 }
	 int cutoff = 130;
	 colorImages(robotGraphics,cutoff);

	 robotGraphics.iconRobotImage = new BufferedImage(16,16, BufferedImage.TYPE_INT_ARGB);
	 Graphics ig = robotGraphics.iconRobotImage.getGraphics();
	 ig.drawImage(robotGraphics.colorRobotImage,0,0,16,16,null);
	 ig.drawImage(robotGraphics.colorGunImage,0,0,16,16,null);
	 ig.drawImage(robotGraphics.colorRadarImage,0,0,16,16,null);
	 }

	 for (int i = 0; i < RobotGraphics.numImages; i++)
	 {
	 setStatus("Please wait, rotating images..." + (RobotGraphics.numImages - i - 1));
	 for (int j = 0; j < robotGraphicsVector.size(); j++)
	 {
	 robotGraphics = (RobotGraphics)(robotGraphicsVector.elementAt(j));
	 robotGraphics.robotImages[i] = rotateImage(robotGraphics.colorRobotImage,(double)i * Math.PI / 180.0 * (360.0 / RobotGraphics.numImages));
	 robotGraphics.gunImages[i] = rotateImage(robotGraphics.colorGunImage,(double)i * Math.PI / 180.0 * (360.0 / RobotGraphics.numImages));
	 robotGraphics.radarImages[i] = rotateImage(robotGraphics.colorRadarImage,(double)i * Math.PI / 180.0 * (360.0 / RobotGraphics.numImages));
	 robotGraphics.initImageNum = i;
	 }
	 repaint(10);
	 }
	 /*
	 mt.addImage(colorRobotImage,1);
	 mt.addImage(colorGunImage,1);
	 mt.addImage(colorRadarImage,1);
	 mt.statusID(1,true);
	 try {
	 mt.waitForID(1,WAITTIME);
	 } catch (InterruptedException e) {
	 }
	 if (mt.statusID(1,true) != MediaTracker.COMPLETE)
	 {
	 log("Error building color image for robot(s): "); // + robotGraphics.getRobotNames());
	 return false;
	 }
	 */

  
	// return true;
	// }


	/**
	 * Insert the method's description here.
	 * Creation date: (1/29/2001 4:00:53 PM)
	 */
	public void cleanup() {/*
		 for (int i = 0; i < 17; i++)
		 {
		 for (int j = 0; j < explodeSizes; j++)
		 {
		 explodeImage[i][j].flush();
		 explodeImage[i][j] = null;
		 }
		 }
		 bulletImage.flush();
		 bulletImage = null;
		 floorImage.flush();
		 floorImage = null;
		 */}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(String s) {
		Utils.log(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(Throwable e) {
		Utils.log(e);
	}

	public void log(String s, Throwable e) {
		Utils.log(s, e);
	}

	/**
	 * Gets the numRotations.
	 * @return Returns a int
	 */
	public int getNumRotations() {
		return numRotations;
	}

	/**
	 * Gets the explodeSizeMultiplier.
	 * @return Returns a int
	 */
	public int getExplodeSizeMultiplier() {
		return explodeSizeMultiplier;
	}

	public Color getRobotColor(int index) {
		try {
			return robotColors[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			log("No such robot color: " + index);
			return null;
		}
	}

	public Color getGunColor(int index) {
		try {
			return gunColors[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			log("No such gun color: " + index);
			return null;
		}
	}

	public Color getRadarColor(int index) {
		try {
			return radarColors[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			log("No such radar color: " + index);
			return null;
		}
	}

	public synchronized int getNewColorsIndex(Color robotColor, Color gunColor, Color radarColor) {
		if (nextColorIndex < maxColors) {
			robotColors[nextColorIndex] = robotColor;
			gunColors[nextColorIndex] = gunColor;
			radarColors[nextColorIndex] = radarColor;
		
			coloredRobotImageArray[nextColorIndex] = ImageArray.createColoredImageArray(getRobotImageArray(),
					getRobotMaskImageArray(), robotColor, MASKCUTOFF, getRobotImageCache(nextColorIndex));

			coloredGunImageArray[nextColorIndex] = ImageArray.createColoredImageArray(getGunImageArray(),
					getGunMaskImageArray(), gunColor, MASKCUTOFF, getGunImageCache(nextColorIndex));

			coloredRadarImageArray[nextColorIndex] = ImageArray.createColoredImageArray(getRadarImageArray(),
					getRadarMaskImageArray(), radarColor, MASKCUTOFF, getRadarImageCache(nextColorIndex));

			nextColorIndex++;
			return (nextColorIndex - 1);
		} else {
			return -1;
		}
	}

	public synchronized int replaceColorsIndex(int colorIndex, Color robotColor, Color gunColor, Color radarColor) {
		if (colorIndex == -1) {
			return -1;
		}
		if (colorIndex < maxColors) {
			robotColors[colorIndex] = robotColor;
			gunColors[colorIndex] = gunColor;
			radarColors[colorIndex] = radarColor;
		
			coloredRobotImageArray[colorIndex] = ImageArray.createColoredImageArray(getRobotImageArray(),
					getRobotMaskImageArray(), robotColor, MASKCUTOFF, getRobotImageCache(colorIndex));

			coloredGunImageArray[colorIndex] = ImageArray.createColoredImageArray(getGunImageArray(),
					getGunMaskImageArray(), gunColor, MASKCUTOFF, getGunImageCache(colorIndex));

			coloredRadarImageArray[colorIndex] = ImageArray.createColoredImageArray(getRadarImageArray(),
					getRadarMaskImageArray(), radarColor, MASKCUTOFF, getRadarImageCache(colorIndex));

			return colorIndex;
		} else {
			return -1;
		}
	}

	public BufferedImage getRobotImageCache(int index) {
		if (robotImageCache[index] == null) {
			robotImageCache[index] = ImageArray.createBlankImage(getRobotImageArray());
		}
		return robotImageCache[index];
	}

	public BufferedImage getGunImageCache(int index) {
		if (gunImageCache[index] == null) {
			gunImageCache[index] = ImageArray.createBlankImage(getGunImageArray());
		}
		return gunImageCache[index];
	}

	public BufferedImage getRadarImageCache(int index) {
		if (radarImageCache[index] == null) {
			radarImageCache[index] = ImageArray.createBlankImage(getRadarImageArray());
		}
		return radarImageCache[index];
	}

}

