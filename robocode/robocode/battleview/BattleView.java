/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
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
import javax.swing.*;
import java.awt.image.*;

import robocode.dialog.RobocodeFrame;
import robocode.peer.*;
import robocode.battlefield.*;
import robocode.battle.*;
import robocode.util.*;
import robocode.manager.*;


/**
 * Insert the type's description here.
 * Creation date: (12/13/2000 8:41:14 PM)
 * @author: Mathew A. Nelson
 */
public class BattleView extends JPanel {

	public static final int PAINTROBOCODELOGO = 0;
	public static final int PAINTBATTLE = 1;

	private RobocodeFrame robocodeFrame;

	// The battle and battlefield, 
	private Battle battle;
	private BattleField battleField;

	private boolean initialized = false;
	private double scale = 1.0;
	private java.awt.Image osi;
	private java.awt.Graphics2D osg;
	private int paintMode = PAINTROBOCODELOGO;
	
	private boolean clear = false;
	
	// Floor related fields -- not currently used.
	private boolean useFloor = false; // true;
	private boolean[][] dirtyArr = null;
	private int floorHeight = 64;
	private int floorWidth = 64;

	// Draw option related things	
	private boolean drawRobotName = false;
	private Rectangle nameStringRect = new Rectangle();
	private boolean drawRobotEnergy = false;
	private Rectangle energyStringRect = new Rectangle();
	private boolean drawScanArcs = false;
	private Rectangle arcRect;

	// Debugging and fps
	private boolean displayFps = false;
	// private boolean drawBoundingRects = false;

	// Fonts and the like
	private Font bigFont;
	private FontMetrics bigFontMetrics;
	private Font regularFont;
	private FontMetrics regularFontMetrics;
	private Font smallFont;
	private FontMetrics smallFontMetrics;
	
	private ImageManager imageManager = null;
	private Repainter repainter = new Repainter();
	
	private RobocodeManager manager = null;

	int adjust = 0;

	/**
	 * Simple Runnable class to allow draw() to run paintImmediately()
	 * in the Swing thread
	 */
	public class Repainter implements Runnable {
		public void run() {
			paintImmediately(0, 0, getWidth(), getHeight());
		}
	}

	/**
	 * The main draw method.  The game calls this every frame.
	 */
	public void draw(boolean clear) {
		try {
			if (robocodeFrame.isIconified() && clear == false) {
				return;
			}
			setClear(clear);
			SwingUtilities.invokeAndWait(repainter);
			setClear(true);
		} catch (Exception e) {
			log("Could not draw: " + e);
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 1:18:35 AM)
	 * @return int
	 */
	public int getPaintMode() {
		return paintMode;
	}

	public void setDisplayOptions() {
		setDisplayFps(manager.getProperties().getOptionsViewFps());
		setDrawRobotName(manager.getProperties().getOptionsViewRobotNames());
		setDrawRobotEnergy(manager.getProperties().getOptionsViewRobotEnergy());
		setDrawScanArcs(manager.getProperties().getOptionsViewScanArcs());
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 1:35:37 PM)
	 */
	public void initialize(Graphics g) {

		scale = 1;

		// If we're re-initializing, clear osi.
		if (osi != null) {
			osi.flush();
			osi = null;
			osg = null;
		}
		// System.gc();

		// If we are scaled...
		if (getWidth() < battleField.getWidth() || getHeight() < battleField.getHeight()) {
			// Use the smaller scale.
			// Actually we don't need this, since
			// the RobocodeFrame keeps our aspect ratio intact.
			double scalex, scaley;

			scalex = (double) getWidth() / (double) battleField.getWidth();
			scaley = (double) getHeight() / (double) battleField.getHeight();
			if (scalex < scaley) {
				scale = scalex;
			} else {
				scale = scaley;
			}
			
			if (System.getProperty("SINGLEBUFFER", "false").equals("false")) {
				osi = (BufferedImage) createImage(getWidth(), getHeight()); // new BufferedImage(currentDimension.width, currentDimension.height, BufferedImage.TYPE_INT_RGB);
				osg = ((BufferedImage) osi).createGraphics();
				((Graphics2D) osg).scale(scale, scale);
			} else {
				osg = (Graphics2D) g;
			}
			// Setting clip eliminates fastclip problem with drawString:
			osg.setClip(0, 0, battleField.getWidth(), battleField.getHeight());
		} // else not scaled.
		else {
			if (System.getProperty("SINGLEBUFFER", "false").equals("false")) {
				// log("BattleView is an unscaled Graphics.");
				osi = createImage(getWidth(), getHeight());
				osg = (Graphics2D) osi.getGraphics();
			} else {
				osg = (Graphics2D) g;
			}
			// Setting clip eliminates fastclip problem with drawString:
			// We also need it for the ImageArray (to fix the same problem)
			osg.setClip(0, 0, battleField.getWidth(), battleField.getHeight());
		}

		smallFont = new Font("Dialog", Font.PLAIN, (int) (10 / scale));
		regularFont = new Font("Dialog", Font.PLAIN, (int) (12 / scale));
		bigFont = new Font("Dialog", Font.BOLD, (int) (72 / scale)); // 144);

		smallFontMetrics = osg.getFontMetrics(smallFont);
		regularFontMetrics = osg.getFontMetrics(regularFont);
		bigFontMetrics = osg.getFontMetrics(bigFont);

		initialized = true;
		setClear(true);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 1:31:43 PM)
	 */
	public void paintComponent(Graphics g) {

		switch (paintMode) {
		case PAINTROBOCODELOGO:
			paintRobocodeLogo(g);
			return;

		case PAINTBATTLE: {
			if (!initialized) {
				initialize(g);
			}
			if (osi == null) {
				osg = (Graphics2D) g;
				if (scale != 1) {
					osg.scale(scale, scale);
				}
			}
			paintBattle(osg);
	
		}
		}

		// If osi is null, we're drawing singlebuffered anyway
		if (osi != null) {
			g.drawImage(osi, 0, 0, null);
		}
	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/31/2000 1:30:58 AM)
	 */
	public void paintBattle(Graphics2D osg) {

		// set a fudge factor for scaled battles
		setAdjust();

		// Clear last frame in osg
		clearFrame(osg);
	
		// Draw ground
		drawGround(osg);
	
		// Draw battlefield objects
		drawObjects(osg);
	    
		// Draw scan arcs
		drawScanArcs(osg);

		// Draw robots
		drawRobots(osg);

		// Draw all bullets
		drawBullets(osg);
	
		// Draw all text
		drawText(osg);
	
	}

	private void setAdjust() {
		if (scale < 1) {
			adjust = (int) ((1 / scale) + 1.0);
		} else {
			adjust = 0;
		}
	}

	private void clearFrame(Graphics2D osgS) {
		// Set background to black
		osg.setColor(Color.black);
		
		// Clear last frame
		if (clear || drawScanArcs) {
			osg.fillRect(0, 0, battleField.getWidth() + 10, battleField.getHeight() + 10);
		} else {
			fillDirtyRects(osg);
		}
	}

	private void fillDirtyRects(Graphics2D osg) {
		RobotPeer c;
		BulletPeer b;

		for (int i = 0; i < battle.getRobots().size(); i++) {
			c = (RobotPeer) battle.getRobots().elementAt(i);
			if (c.dirtyRect != null) {
				if (useFloor) {
					int al = (int) c.dirtyRect.x / 64;
					int at = (int) c.dirtyRect.y / 64;
					int fl = floorWidth * al;
					int ft = floorHeight * at; 
					int fw = (c.dirtyRect.x - fl) + c.dirtyRect.width;
					int fh = (c.dirtyRect.y - ft) + c.dirtyRect.height;

					fw = fw / 64 + 1;
					fh = fh / 64 + 1;
					for (int fy = at; fy < at + fh; fy++) {
						for (int fx = al; fx < al + fw; fx++) {
							dirtyArr[fy][fx] = true;
						}
					}
				} else {
					osg.fillRect(c.dirtyRect.x, c.dirtyRect.y, c.dirtyRect.width, c.dirtyRect.height);
				}
				if (c.isDead()) {
					c.dirtyRect = null;
				}
			}
			if (c.getSayTextPeer() != null) {
				Rectangle r = c.getSayTextPeer().getDirtyRect();

				if (r != null) {
					osg.fillRect(r.x, r.y, r.width, r.height);
				}
			}
		}
		for (int i = 0; i < battle.getBullets().size(); i++) {
			b = (BulletPeer) battle.getBullets().elementAt(i);
			if (b.dirtyRect != null) {
				if (useFloor) {
					int al = (int) b.dirtyRect.x / 64;
					int at = (int) b.dirtyRect.y / 64;
					int fl = floorWidth * al;
					int ft = floorHeight * at; 
					int fw = (b.dirtyRect.x - fl) + b.dirtyRect.width;
					int fh = (b.dirtyRect.y - ft) + b.dirtyRect.height;

					fw = fw / 64 + 1;
					fh = fh / 64 + 1;
					for (int fy = at; fy < at + fh; fy++) {
						for (int fx = al; fx < al + fw; fx++) {
							dirtyArr[fy][fx] = true;
						}
					}
				} else {
					osg.fillRect(b.dirtyRect.x, b.dirtyRect.y, b.dirtyRect.width, b.dirtyRect.height);
				}
				if (!b.isActive() && b.hitVictim == false && b.hitBullet == false) {
					b.dirtyRect = null;
				}
			}
		}
	}

	public void drawGround(Graphics2D osg) {
		if (!useFloor) {
			return;
		}
		
		for (int y = 0; y < battleField.getHeight() / floorHeight + 1; y++) {
			for (int x = 0; x < battleField.getWidth() / floorWidth + 1; x++) {
				if (dirtyArr[y][x]) {
					osg.drawImage(imageManager.getFloorImage(), x * floorWidth, y * floorHeight, null);
					dirtyArr[y][x] = false;
				}
			}
		}
	}

	protected void drawObjects(Graphics2D osg) {// ((ShapesBattleField)battleField).drawShapes((Graphics2D)osg);
	}

	private void drawScanArcs(Graphics2D osg) {
		if (drawScanArcs) {
			RobotPeer c;

			for (int i = 0; i < battle.getRobots().size(); i++) {
				c = (RobotPeer) battle.getRobots().elementAt(i);
				if (c.isDead()) {
					continue;
				}
				c.arcRect = drawScanArc((Graphics2D) osg, c);
			}
		}
	}

	private void drawRobots(Graphics2D osg) {
		RobotPeer c;
		int numRotations = imageManager.getNumRotations();

		for (int i = 0; i < battle.getRobots().size(); i++) {
			c = (RobotPeer) battle.getRobots().elementAt(i);
			if (c.isDead()) {
				continue;
			}

			int x, y;

			x = (int) c.getX();
			y = battle.getBattleField().getHeight() - (int) c.getY();

			int inum = (int) (((c.getHeading() * 180.0 / Math.PI) + 5) / (360.0 / numRotations));

			while (inum < 0) {
				inum += numRotations;
			}
			while (inum >= numRotations) {
				inum -= numRotations;
			}

			int rnum = (int) (((c.getRadarHeading() * 180.0 / Math.PI) + 5) / (360.0 / numRotations));

			while (rnum < 0) {
				rnum += numRotations;
			}
			while (rnum >= numRotations) {
				rnum -= numRotations;
			}

			int gnum = (int) (((c.getGunHeading() * 180.0 / Math.PI) + 5) / (360.0 / numRotations));

			while (gnum < 0) {
				gnum += numRotations;
			}
			while (gnum >= numRotations) {
				gnum -= numRotations;
			}

			/*
			 if (drawBoundingRects)
			 {
			 BoundingRectangle re = (BoundingRectangle)c.getBoundingBox();
			 osg.setColor(Color.blue);
			 osg.fillRect((int)re.x,(int)(battle.getBattleField().getHeight() - re.y - re.width),(int)re.width,(int)re.height);
			 }
			 */
		
			imageManager.getColoredRobotImageArray(c.getColorIndex()).drawCentered(osg, inum, x, y);
			imageManager.getColoredGunImageArray(c.getColorIndex()).drawCentered(osg, gnum, x, y);
			if (!c.isDroid()) {
				imageManager.getColoredRadarImageArray(c.getColorIndex()).drawCentered(osg, rnum, x, y);
			}
		}
	}

	private void drawText(Graphics2D osg) {
		int imgWidth = imageManager.getColoredRobotImageArray(-1).getWidth();
		int imgHeight = imageManager.getColoredRobotImageArray(-1).getHeight();
		RobotPeer c;

		for (int i = 0; i < battle.getRobots().size(); i++) {
			c = (RobotPeer) battle.getRobots().elementAt(i);
			if (c.isDead()) {
				continue;
			}

			int x, y;

			x = (int) c.getX();
			y = battle.getBattleField().getHeight() - (int) c.getY();

			int l, t;		

			l = x - imgWidth / 2;
			t = y - imgHeight / 2;
			if (drawRobotEnergy && c.getRobot() != null) {
				osg.setColor(Color.white);
				int ll = (int) c.getEnergy();
				int rl = (int) ((c.getEnergy() - ll + .001) * 10.0);

				if (rl == 10) {
					rl = 9;
				}
				String energyString = ll + "." + rl;

				if (c.getEnergy() == 0 && !c.isDead()) {
					energyString = "Disabled";
				}

				centerString(energyString, x, y - imgHeight / 2 - smallFontMetrics.getHeight() / 2, smallFont,
						smallFontMetrics, energyStringRect);
			}
			if (drawRobotName) {
				if (!drawRobotEnergy) {
					osg.setColor(Color.white);
				}
				centerString(c.getVeryShortName(), x, y + imgHeight / 2 + smallFontMetrics.getHeight() / 2, smallFont,
						smallFontMetrics, nameStringRect);
			}

			if (c.dirtyRect == null) {
				c.dirtyRect = new Rectangle();
			}

			c.dirtyRect.x = l;
			c.dirtyRect.y = t;
			int right = l + imgWidth;
			int bottom = t + imgHeight;

			/* // nevermind, we just clear the whole battle
			 if (drawScanArcs)
			 {
			 c.dirtyRect.x = Math.min(c.dirtyRect.x,c.arcRect.x);
			 c.dirtyRect.y = Math.min(c.dirtyRect.y,c.arcRect.y);
			 right = Math.max(right,c.arcRect.x + c.arcRect.width);
			 bottom = Math.max(bottom,c.arcRect.y+c.arcRect.height);
			 
			 }
			 */
			if (drawRobotEnergy) {
				c.dirtyRect.x = Math.min(c.dirtyRect.x, energyStringRect.x);
				c.dirtyRect.y = Math.min(c.dirtyRect.y, energyStringRect.y);
				right = Math.max(right, energyStringRect.x + energyStringRect.width);
				bottom = Math.max(bottom, energyStringRect.y + energyStringRect.height);
			}
			if (drawRobotName) {
				c.dirtyRect.x = Math.min(c.dirtyRect.x, nameStringRect.x);
				c.dirtyRect.y = Math.min(c.dirtyRect.y, nameStringRect.y);
				right = Math.max(right, nameStringRect.x + nameStringRect.width);
				bottom = Math.max(bottom, nameStringRect.y + nameStringRect.height);
			}
			c.dirtyRect.width = right - c.dirtyRect.x;
			c.dirtyRect.height = bottom - c.dirtyRect.y;
 		
			if (l < 0) {
				c.dirtyRect.width -= l;
			}
			if (t < 0) {
				c.dirtyRect.height -= t;
			}

			if (scale < 1) {
				c.dirtyRect.x -= adjust;
				c.dirtyRect.y -= adjust;
				c.dirtyRect.width += adjust << 1;
				c.dirtyRect.height += adjust << 1;
			}

			if (c.getSayTextPeer() != null) {
				if (c.getSayTextPeer().getText() != null) {
					if (c.getSayTextPeer().getDirtyRect() == null) {
						c.getSayTextPeer().setDirtyRect(new Rectangle());
					}
					osg.setColor(c.getSayTextPeer().getColor());
					centerString(c.getSayTextPeer().getText(), c.getSayTextPeer().getX(),
							battle.getBattleField().getHeight() - c.getSayTextPeer().getY(), smallFont, smallFontMetrics,
							c.getSayTextPeer().getDirtyRect());
				} else {
					c.getSayTextPeer().setDirtyRect(null);
				}
			}
		}
	}

	private void drawBullets(Graphics2D osg) {
		BulletPeer b;
		int l, t;

		for (int i = 0; i < battle.getBullets().size(); i++) {
			b = (BulletPeer) battle.getBullets().elementAt(i);
			if (!b.isActive() && !b.hitVictim && !b.hitBullet) {
				continue;
			}

			if (!b.hitVictim && !b.hitBullet) {
				l = (int) b.getX() - 2;
				t = (int) (battle.getBattleField().getHeight() - b.getY() - 3);

				/*
				 if (drawBoundingRects)
				 {
				 osg.setColor(Color.yellow);
				 Line2D.Double ld = new Line2D.Double();
				 ld.setLine(
				 b.boundingLine.getX1(),
				 battle.getBattleField().getHeight() - b.boundingLine.getY1(),
				 b.boundingLine.getX2(),
				 battle.getBattleField().getHeight() - b.boundingLine.getY2());
				 ((Graphics2D)osg).draw(ld);
				 }
				 */
				if (scale > .5) {
					drawBulletImage(osg, imageManager.getBulletImage(), l, t);
					// osg.drawImage(bulletImage,l,t, null);
				} else {
					osg.drawImage(imageManager.getBulletImage(), l, t, (int) (1 / scale + 1.0), (int) (1 / scale + 1.0),
							null);
				}
				if (b.dirtyRect != null && b.isActive()) {
					b.dirtyRect.x = l;
					b.dirtyRect.y = t;
					if (scale == 1) {
						b.dirtyRect.width = 5;
						b.dirtyRect.height = 7;
					} else {
						b.dirtyRect.width = (int) (5 / scale);
						b.dirtyRect.height = (int) (7 / scale);
					}
				} else {
					if (scale == 1) {
						b.dirtyRect = new Rectangle(l, t, 5, 7);
					} else {
						b.dirtyRect = new Rectangle(l, t, (int) (5 / scale), (int) (7 / scale));
					}
				}

				/*
				 if (drawBoundingRects)
				 {
				 Line2D.Double ld = new Line2D.Double();
				 ld.setLine(
				 b.boundingLine.getX1(),
				 battle.getBattleField().getHeight() - b.boundingLine.getY1(),
				 b.boundingLine.getX2(),
				 battle.getBattleField().getHeight() - b.boundingLine.getY2());
				 b.dirtyRect.x = (int)Math.min(ld.getX1(),ld.getX2()) - 5;
				 b.dirtyRect.y = (int)Math.min(ld.getY1(),ld.getY2()) - 5;
				 b.dirtyRect.width = (int)Math.abs(ld.getX1() - ld.getX2()) + 10;
				 b.dirtyRect.height = (int)Math.abs(ld.getY1() - ld.getY2()) + 10;
				 }
				 */
			} else {
				int sizeIndex, halfSize;

				if (b instanceof ExplosionPeer) {
					sizeIndex = 0;
					halfSize = 40;
					l = (int) b.getX() - b.getWidth() / 2;
					t = (int) (battle.getBattleField().getHeight() - b.getY() - b.getHeight() / 2);
				} else {
					if (b.getPower() >= 4) {
						sizeIndex = 5;
					} else if (b.getPower() >= 3) {
						sizeIndex = 4;
					} else if (b.getPower() >= 2) {
						sizeIndex = 3;
					} else if (b.getPower() >= 1) {
						sizeIndex = 2;
					} else {
						sizeIndex = 1;
					}

					if (b.hitBullet) {
						sizeIndex = 2;
					}

					halfSize = sizeIndex * imageManager.getExplodeSizeMultiplier() / 2;
					l = (int) b.getX() - halfSize;
					t = (int) (battle.getBattleField().getHeight() - b.getY() - halfSize);
				}

				try {
					drawExplodeImage(osg, imageManager.getExplosionImage(b.getWhichExplosion(), b.getFrame(), sizeIndex),
							l, t);
				} catch (ArrayIndexOutOfBoundsException e) {// log("Out of bounds: " + b.getFrame() + " of size " + sizeIndex);
				}

				if (b.dirtyRect != null) {
					b.dirtyRect.x = l;
					b.dirtyRect.y = t;
					b.dirtyRect.width = 2 * halfSize + 1;
					b.dirtyRect.height = 2 * halfSize + 1;
				} else {
					b.dirtyRect = new Rectangle(l, t, 2 * halfSize + 1, 2 * halfSize + 1);
				}
				if (b instanceof ExplosionPeer) {
					b.dirtyRect.width = imageManager.getExplodeDimension(b.getWhichExplosion()).width;
					b.dirtyRect.height = imageManager.getExplodeDimension(b.getWhichExplosion()).height;
				}

			}
			if (scale < 1) {
				b.dirtyRect.x -= adjust;
				b.dirtyRect.y -= adjust;
				b.dirtyRect.width += adjust << 1;
				b.dirtyRect.height += adjust << 1;
			}

		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 1:30:20 PM)
	 */
	public void setBattle(Battle battle) {
		this.battle = battle;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 1:18:35 AM)
	 * @param newPaintMode int
	 */
	public void setPaintMode(int newPaintMode) {
		paintMode = newPaintMode;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/3/2001 9:13:10 PM)
	 */
	public void update(Graphics g) {
		// I don't think this is ever called in swing...
		paint(g);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/9/2001 2:17:30 PM)
	 * @param s java.lang.String
	 * @param x int
	 * @param y int
	 * @param dirtyRect BoundingRectangle
	 */
	public void centerString(String s, int x, int y, Font font, FontMetrics fm, Rectangle dirtyRect) {

		osg.setFont(font);
	
		int left, top, descent;
		int width, height;

		width = fm.stringWidth(s);
		height = fm.getHeight();
		descent = fm.getDescent();

		left = x - width / 2;
		top = y - height / 2;

		if (left + width > battleField.getWidth()) {
			left = battleField.getWidth() - width;
		}
		if (top + height >= battleField.getHeight()) {
			top = battleField.getHeight() - height - 1;
		}
		if (left < 0) {
			left = 0;
		}
		if (top < 0) {
			top = 0;
		}

		if (dirtyRect != null) {
			dirtyRect.setFrame(left, top, width, height);
			if (scale < 1) {
				// Strange... stringWidth is too small in many cases when scaling is in effect.
				dirtyRect.width *= 1.2;
			}
		}
	
		// osg.drawGlyphVector(font.createGlyphVector(fontRenderContext,s),left,top + height - descent);

		// osg.setColor(Color.blue);
		// osg.fillRect(left,top,dirtyRect.width,dirtyRect.height);
		// osg.setColor(Color.white);
		osg.drawString(s, left, top + height - descent);
	
		// osg.fillRect(left,top,width,height);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/24/2001 8:26:06 PM)
	 * @param g java.awt.Graphics
	 * @param i java.awt.Image
	 * @param x int
	 * @param y int
	 */
	public void drawBulletImage(Graphics g, Image i, int x, int y) {
		g.drawImage(i, x, y, null);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/24/2001 8:26:06 PM)
	 * @param g java.awt.Graphics
	 * @param i java.awt.Image
	 * @param x int
	 * @param y int
	 */
	public void drawExplodeImage(Graphics g, Image i, int x, int y) {
		g.drawImage(i, x, y, null);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/24/2001 8:26:06 PM)
	 * @param g java.awt.Graphics
	 * @param i java.awt.Image
	 * @param x int
	 * @param y int
	 */
	public void drawExplodeImage(Graphics g, Image i, int x, int y, int sx, int sy) {
		g.drawImage(i, x, y, sx, sy, null);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/11/2001 2:57:16 PM)
	 * @param fileName java.lang.String
	 */
	public void saveFrame(String fileName) {
		try {
			java.io.FileOutputStream o = new java.io.FileOutputStream(fileName);
			com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(o);

			encoder.encode((BufferedImage) osi);
			o.close();
		} catch (Exception e) {
			log("Could not save frame: " + e);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/17/2001 12:44:11 AM)
	 * @param s java.lang.String
	 */
	public void setTitle(String s) {
		if (robocodeFrame != null) {
			robocodeFrame.setTitle(s);
		}
	}

	/**
	 * BattleView constructor comment.
	 */
	public BattleView(RobocodeManager manager, RobocodeFrame robocodeFrame, ImageManager imageManager) {
		super(false);
		this.manager = manager;
		this.robocodeFrame = robocodeFrame;
		this.imageManager = imageManager;
		this.manager = manager;
		setBackground(Color.blue);
	}

	/**
	 * Draw the 
	 * Insert the method's description here.
	 * Creation date: (12/20/2000 6:05:25 PM)
	 * @return java.awt.Rectangle
	 * @param img java.awt.Image
	 * @param theta double
	 */
	public Rectangle drawScanArc(Graphics2D osg, RobotPeer c) {

		osg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) .2));
		osg.setColor(Color.blue);

		java.awt.geom.Arc2D.Double scanArc = (java.awt.geom.Arc2D.Double) c.getScanArc().clone();

		double aStart = scanArc.getAngleStart();
		double aExtent = scanArc.getAngleExtent();

		aStart = 360 - aStart;
		aStart -= aExtent;
		if (aStart < 0) {
			aStart += 360;
		}
		scanArc.setAngleStart(aStart);
		scanArc.y = battle.getBattleField().getHeight() - c.getY() - c.getScanRadius();

		if (aExtent >= .5) {
			osg.fill(scanArc);
		} else {
			osg.draw(scanArc);
		}
		osg.setComposite(AlphaComposite.SrcOver);
		return scanArc.getBounds();
	}

	private void log(String s) {
		Utils.log(s);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 1:21:11 PM)
	 */
	public void setBattleField(BattleField battleField) {
		this.battleField = battleField;

		if (battleField != null) {
			// Used in RobocodeFrame, battleViewPanel
			// We are not actually in a layout manager.
			setPreferredSize(new Dimension(battleField.getWidth(), battleField.getHeight()));
			setMaximumSize(new Dimension(battleField.getWidth(), battleField.getHeight()));
			((JPanel) getParent()).setPreferredSize(new Dimension(battleField.getWidth(), battleField.getHeight()));
			// log("My preferred size is now: " + getPreferredSize());
			invalidate();
			validate();
			// log("My preferred size is now: " + getPreferredSize());
			if (useFloor) {
				dirtyArr = new boolean[battleField.getHeight() / 64 + 1][battleField.getWidth() / 64 + 1];
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/4/2001 6:59:13 PM)
	 * @param db boolean
	 */
	public void setBattlePaused(boolean db) {
		setDoubleBuffered(db);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/4/2001 6:59:13 PM)
	 * @param db boolean
	 */
	public void setDoubleBuffered(boolean doubleBuffered) {
	
		if (System.getProperty("GLOBALSINGLEBUFFER", "false").equals("true")) {
			// log("Setting doubleBuffered to: " + doubleBuffered);
			javax.swing.RepaintManager.currentManager(this).setDoubleBufferingEnabled(doubleBuffered);
		}
		super.setDoubleBuffered(doubleBuffered);
		Component parent = getParent();

		while (parent != null) {
			if (parent instanceof JComponent) {
				((JComponent) parent).setDoubleBuffered(doubleBuffered);
			}
			parent = parent.getParent();
		}
	}

	/**
	 * Draws "Robocode" in giant letters
	 */

	private void paintRobocodeLogo(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(Color.darkGray);
		g.setColor(new Color(15, 15, 15));
	
		Font bf = new Font("Dialog", Font.BOLD, 72);
		FontMetrics fm = g.getFontMetrics(bf);

		int left, top, descent;
		int width, height;

		width = fm.stringWidth("Robocode");
		height = fm.getHeight();
		descent = fm.getDescent();
	
		left = getWidth() / 2 - width / 2;
		top = getHeight() / 2 - height / 2;

		g.setFont(bf);
		g.drawString("Robocode", left, top + height - descent);
	
		/*
		 String version = "Version " + manager.getVersionManager().getVersion();
		 Font sf = new Font("Dialog",Font.BOLD,24);
		 fm = g.getFontMetrics(sf);
		 width = fm.stringWidth(version);
		 height = fm.getHeight();
		 descent = fm.getDescent();
		 left = getWidth() - width;
		 top = getHeight() - descent;
		 g.setFont(sf);
		 g.drawString(version,left,top);
		 */
	
	
	
		return;
	}

	/**
	 * Gets the displayFps.
	 * @return Returns a boolean
	 */
	public boolean isDisplayFps() {
		return displayFps;
	}

	/**
	 * Sets the displayFps.
	 * @param displayFps The displayFps to set
	 */
	public void setDisplayFps(boolean displayFps) {
		this.displayFps = displayFps;
	}

	/**
	 * Gets the initialized.
	 * @return Returns a boolean
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Sets initialized.
	 * @param initialized
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	/**
	 * Sets drawRobotEnergy
	 * @param newDrawRobotEnergy boolean
	 */
	public void setDrawRobotEnergy(boolean drawRobotEnergy) {
		this.drawRobotEnergy = drawRobotEnergy;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/18/2001 3:10:17 PM)
	 * @param newDrawRobotName boolean
	 */
	public void setDrawRobotName(boolean newDrawRobotName) {
		drawRobotName = newDrawRobotName;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/18/2001 3:11:12 PM)
	 * @param newDrawScanArcs boolean
	 */
	public void setDrawScanArcs(boolean newDrawScanArcs) {
		drawScanArcs = newDrawScanArcs;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/18/2001 3:10:12 PM)
	 * @return boolean
	 */
	public boolean isDrawRobotEnergy() {
		return drawRobotEnergy;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/18/2001 3:10:17 PM)
	 * @return boolean
	 */
	public boolean isDrawRobotName() {
		return drawRobotName;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/18/2001 3:11:12 PM)
	 * @return boolean
	 */
	public boolean isDrawScanArcs() {
		return drawScanArcs;
	}

	public void setClear(boolean clear) {
		this.clear = clear;
	}

}
