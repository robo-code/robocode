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
 *     - Modified to use BufferStrategy instead of SwingUtilities, hence this
 *       class is now extending Canvas instead of JPanel.
 *     - Integration of robocode.render.
 *     - Removed all code for handling dirty rectangles, which is not necessary
 *       anymore due to the new robocode.render.
 *     - Code cleanup
 *     - Support for Robot.onPaint() method using MirrorGraphics for mirroring
 *       the painting.
 *     - Added the ability to enable and disable drawing the ground.
 *     - Added support for Robocode SG painting
 *******************************************************************************/
package robocode.battleview;


import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import robocode.dialog.RobocodeFrame;
import robocode.peer.*;
import robocode.battlefield.*;
import robocode.battle.*;
import robocode.util.*;
import robocode.manager.*;
import robocode.render.RenderImage;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class BattleView extends Canvas {
	public static final int PAINTROBOCODELOGO = 0;
	public static final int PAINTBATTLE = 1;

	private final static Color CANVAS_BG_COLOR = SystemColor.controlDkShadow;
	
	private RobocodeFrame robocodeFrame;

	// The battle and battlefield, 
	private Battle battle;
	private BattleField battleField;

	private boolean initialized;
	private double scale = 1.0;
	private int paintMode = PAINTROBOCODELOGO;
	
	// Ground
	private int[][] groundTiles;

	private int groundTileWidth = 64;
	private int groundTileHeight = 64;
	
	private Image groundImage;

	// Draw option related things	
	private boolean drawRobotName;
	private boolean drawRobotEnergy;
	private boolean drawScanArcs;
	private boolean drawGround;

	// Debugging and fps
	private boolean displayFps;

	// Fonts and the like
	private Font smallFont;
	private FontMetrics smallFontMetrics;
	
	private ImageManager imageManager;

	private RobocodeManager manager;

	private BufferStrategy bufferStrategy;

	private Image offscreenImage;
	private Graphics2D offscreenGfx;

	private static MirroredGraphics mirroredGraphics = new MirroredGraphics();

	/**
	 * BattleView constructor.
	 */
	public BattleView(RobocodeManager manager, RobocodeFrame robocodeFrame, ImageManager imageManager) {
		super();

		this.manager = manager;
		this.robocodeFrame = robocodeFrame;
		this.imageManager = imageManager;
		this.manager = manager;
	}

	/**
	 * Shows the next frame. The game calls this every frame.
	 */
	public void showNextFrame() {
		try {
			if (robocodeFrame.isIconified() || (getWidth() <= 0) || (getHeight() <= 0)) {
				return;
			}

			if (!initialized) {
				initialize();
			}
			
			Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

			paint(g);
			g.dispose();

			// Flush the buffer to the main graphics
			if (getGraphics() != null) {
				// FNL: The above check to prevents internal NullPointerException in
				// Component.BltBufferStrategy.show()	
				bufferStrategy.show();
			}

		} catch (Exception e) {
			Utils.log("Could not draw: " + e);
			e.printStackTrace(System.err);
		}
	}

	public int getPaintMode() {
		return paintMode;
	}

	public void setDisplayOptions() {
		displayFps = manager.getProperties().getOptionsViewFps();
		drawRobotName = manager.getProperties().getOptionsViewRobotNames();
		drawRobotEnergy = manager.getProperties().getOptionsViewRobotEnergy();
		drawScanArcs = manager.getProperties().getOptionsViewScanArcs();
		drawGround = manager.getProperties().getOptionsViewGround();
	}

	private void initialize() {
		if (offscreenImage != null) {
			offscreenImage.flush();
			offscreenImage = null;
		}

		offscreenImage = createImage(getWidth(), getHeight());
		offscreenGfx = (Graphics2D) offscreenImage.getGraphics();

		if (bufferStrategy == null) {
			createBufferStrategy(2);
			bufferStrategy = getBufferStrategy();
		}

		// If we are scaled...
		if (getWidth() < battleField.getWidth() || getHeight() < battleField.getHeight()) {
			// Use the smaller scale.
			// Actually we don't need this, since
			// the RobocodeFrame keeps our aspect ratio intact.
			
			scale = Math.min((double) getWidth() / battleField.getWidth(),
					(double) getHeight() / battleField.getHeight());
		} else {
			scale = 1;
		}
		offscreenGfx.scale(scale, scale);

		// Scale font
		smallFont = new Font("Dialog", Font.PLAIN, (int) (10 / scale));
		smallFontMetrics = bufferStrategy.getDrawGraphics().getFontMetrics(smallFont);

		// Initialize ground image
		if (drawGround) {
			createGroundImage();
		} else {
			groundImage = null;
		}

		initialized = true;
	}

	private void createGroundImage() {
		// Reinitialize ground tiles

		final int NUM_HORZ_TILES = battleField.getWidth() / groundTileWidth + 1;
		final int NUM_VERT_TILES = battleField.getHeight() / groundTileHeight + 1;

		if ((groundTiles == null) || (groundTiles.length != NUM_VERT_TILES) || (groundTiles[0].length != NUM_HORZ_TILES)) {

			groundTiles = new int[NUM_VERT_TILES][NUM_HORZ_TILES];
			for (int y = NUM_VERT_TILES - 1; y >= 0; y--) {
				for (int x = NUM_HORZ_TILES - 1; x >= 0; x--) {
					groundTiles[y][x] = (int) Math.round(Math.random() * 4);
				}
			}
		}

		// Create new buffered image with the ground pre-rendered

		int groundWidth = (int) (battleField.getWidth() * scale);
		int groundHeight = (int) (battleField.getHeight() * scale);
	
		groundImage = new BufferedImage(groundWidth, groundHeight, BufferedImage.TYPE_INT_RGB);
	
		Graphics2D groundGfx = (Graphics2D) groundImage.getGraphics();

		groundGfx.setTransform(AffineTransform.getScaleInstance(scale, scale));
	
		for (int y = NUM_VERT_TILES - 1; y >= 0; y--) {
			for (int x = NUM_HORZ_TILES - 1; x >= 0; x--) {
				Image img = imageManager.getGroundTileImage(groundTiles[y][x]);

				if (img != null) {
					groundGfx.drawImage(img, x * groundTileWidth, y * groundTileHeight, null);
				}
			}
		}
	}

	public void paint(Graphics g) {
		switch (paintMode) {
		case PAINTROBOCODELOGO:
			paintRobocodeLogo(g);
			return;

		case PAINTBATTLE: {
			if (!initialized) {
				initialize();
			}
			paintBattle(offscreenGfx);
			break;
		}
		}
		// If offscreenImage is null, we're drawing singlebuffered anyway
		if (offscreenImage != null) {
			g.drawImage(offscreenImage, 0, 0, null);
		}
	}

	public void paintBattle(Graphics2D g) {
		// Reset transform
		g.setTransform(new AffineTransform());

		// Clear canvas
		g.setColor(CANVAS_BG_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Calculate border space
		double dx = (getWidth() - scale * battleField.getWidth()) / 2;
		double dy = (getHeight() - scale * battleField.getHeight()) / 2;

		// Scale and translate the graphics
		AffineTransform at = AffineTransform.getTranslateInstance(dx, dy);

		at.concatenate(AffineTransform.getScaleInstance(scale, scale));
		g.setTransform(at);

		// Set the clip rectangle
		g.setClip(0, 0, battleField.getWidth(), battleField.getHeight());

		// Draw ground
		drawGround(g);

		// Draw battlefield objects
		drawObjects(g);

		// Draw scan arcs
		drawScanArcs(g);

		// Draw robots
		drawRobots(g);

		// Draw all bullets
		drawBullets(g);

		// Draw all text
		drawText(g);
	}

	private void drawGround(Graphics2D g) {
		if (!drawGround) {
			// Ground should not be drawn
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, battleField.getWidth(), battleField.getHeight());
		} else {
			// Create pre-rendered ground image if it is not available
			if (groundImage == null) {
				createGroundImage();
			}

			// Draw the pre-rendered ground if it is available
			if (groundImage != null) {
				int groundWidth = (int) (battleField.getWidth() * scale);
				int groundHeight = (int) (battleField.getHeight() * scale);

				int dx = (getWidth() - groundWidth) / 2;
				int dy = (getHeight() - groundHeight) / 2;

				AffineTransform savedTx = g.getTransform();
				g.setTransform(new AffineTransform());

				g.drawImage(groundImage, dx, dy, groundWidth, groundHeight, null);

				g.setTransform(savedTx);
			}
		}
	}

	private void drawObjects(Graphics2D g) {
		((ShapesBattleField) battleField).drawShapes(g);
	}

	private void drawScanArcs(Graphics2D g) {
		if (drawScanArcs) {
			RobotPeer c;

			for (int i = 0; i < battle.getRobots().size(); i++) {
				c = (RobotPeer) battle.getRobots().elementAt(i);
				if (c.isDead()) {
					continue;
				}
				c.arcRect = drawScanArc((Graphics2D) g, c); 
			}
		}
	}

	private void drawRobots(Graphics2D g) {
		RobotPeer c;

		if (drawGround) {
			RenderImage explodeDebrise = imageManager.getExplosionDebrise();

			for (int i = 0; i < battle.getRobots().size(); i++) {
				c = (RobotPeer) battle.getRobots().elementAt(i);
	
				if (c.isDead()) {
					int x = (int) c.getX();
					int y = battle.getBattleField().getHeight() - (int) c.getY();

					AffineTransform at = AffineTransform.getTranslateInstance(x, y);

					explodeDebrise.setTransform(at);
					explodeDebrise.paint(g);
				}
			}
		}

		for (int i = 0; i < battle.getRobots().size(); i++) {
			c = (RobotPeer) battle.getRobots().elementAt(i);

			if (!c.isDead()) {
				int x = (int) c.getX();
				int y = battle.getBattleField().getHeight() - (int) c.getY();

				AffineTransform at = AffineTransform.getTranslateInstance(x, y);

				at.rotate(c.getHeading());

				int colorIndex = c.getColorIndex();

				RenderImage robotRenderImage = imageManager.getColoredRobotRenderImage(colorIndex);
	
				robotRenderImage.setTransform(at);
				robotRenderImage.paint(g);
	
				at = AffineTransform.getTranslateInstance(x, y);
				at.rotate(c.getGunHeading());
	
				RenderImage gunRenderImage = imageManager.getColoredGunRenderImage(colorIndex);
	
				gunRenderImage.setTransform(at);
				gunRenderImage.paint(g);
	
				if (!c.isDroid()) {
					at = AffineTransform.getTranslateInstance(x, y);
					at.rotate(c.getRadarHeading());
	
					RenderImage radarRenderImage = imageManager.getColoredRadarRenderImage(colorIndex);
	
					radarRenderImage.setTransform(at);
					radarRenderImage.paint(g);
				}
			}
		}
	}

	private void drawText(Graphics2D g) {
		// TODO: Should do it more cleanly
		int imgHeight = 48;

		RobotPeer c;

		for (int i = 0; i < battle.getRobots().size(); i++) {
			c = (RobotPeer) battle.getRobots().elementAt(i);
			if (c.isDead()) {
				continue;
			}
			int x = (int) c.getX();
			int y = battle.getBattleField().getHeight() - (int) c.getY();

			if (drawRobotEnergy && c.getRobot() != null) {
				g.setColor(Color.white);
				int ll = (int) c.getEnergy();
				int rl = (int) ((c.getEnergy() - ll + .001) * 10.0);

				if (rl == 10) {
					rl = 9;
				}
				String energyString = ll + "." + rl;

				if (c.getEnergy() == 0 && !c.isDead()) {
					energyString = "Disabled";
				}
				centerString(g, energyString, x, y - imgHeight / 2 - smallFontMetrics.getHeight() / 2, smallFont,
						smallFontMetrics);
			}
			if (drawRobotName) {
				if (!drawRobotEnergy) {
					g.setColor(Color.white);
				}
				centerString(g, c.getVeryShortName(), x, y + imgHeight / 2 + smallFontMetrics.getHeight() / 2, smallFont,
						smallFontMetrics);
			}
			if (c.isPaintEnabled() && c.getRobot() != null) {
				// TODO: Store and restore transformation, fills etc. (safe-guard)

				if (c.isSGPaintEnabled()) {
					c.getRobot().onPaint(g);
				} else {
					synchronized (mirroredGraphics) {
						mirroredGraphics.bind(g, battleField.getHeight());
						c.getRobot().onPaint(mirroredGraphics);
						mirroredGraphics.release();
					}
				}
			}
			if (c.getSayTextPeer() != null) {
				if (c.getSayTextPeer().getText() != null) {
					g.setColor(c.getSayTextPeer().getColor());
					centerString(g, c.getSayTextPeer().getText(), c.getSayTextPeer().getX(),
							battle.getBattleField().getHeight() - c.getSayTextPeer().getY(), smallFont, smallFontMetrics);
				}
			}
		}
	}

	private void drawBullets(Graphics2D g) {
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

				if (scale > .5) {
					g.drawImage(imageManager.getBulletImage(), l, t, null);
				} else {
					g.drawImage(imageManager.getBulletImage(), l, t, (int) (1 / scale + 1.0), (int) (1 / scale + 1.0),
							null);
				}
			} else {
				int sizeIndex, halfSize;

				if (b instanceof ExplosionPeer) {
					sizeIndex = 0;
					halfSize = 40;
					l = (int) b.getX() - b.getWidth() / 2;
					t = (int) (battle.getBattleField().getHeight() - b.getY() - b.getHeight() / 2);
				} else {
					double power = b.getPower();

					if (power >= 4) {
						sizeIndex = 5;
					} else if (power >= 3) {
						sizeIndex = 4;
					} else if (power >= 2) {
						sizeIndex = 3;
					} else if (power >= 1) {
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
					drawExplodeImage(g, imageManager.getExplosionImage(b.getWhichExplosion(), b.getFrame(), sizeIndex),
							l, t);
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}
	}

	public void setBattle(Battle battle) {
		this.battle = battle;
	}

	public void setPaintMode(int newPaintMode) {
		paintMode = newPaintMode;
	}

	public void centerString(Graphics2D g, String s, int x, int y, Font font, FontMetrics fm) {
		g.setFont(font);
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
		g.drawString(s, left, (top + height - descent));
	}

	private void drawExplodeImage(Graphics g, Image i, int x, int y) {
		g.drawImage(i, x, y, null);
	}

	public void saveFrame(String fileName) {
		try {
			java.io.FileOutputStream o = new java.io.FileOutputStream(fileName);
			com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(o);

			encoder.encode((BufferedImage) offscreenImage);
			o.close();
		} catch (Exception e) {
			Utils.log("Could not save frame: " + e);
		}
	}

	public void setTitle(String s) {
		if (robocodeFrame != null) {
			robocodeFrame.setTitle(s);
		}
	}

	private Rectangle drawScanArc(Graphics2D g, RobotPeer c) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) .2));
		g.setColor(Color.blue);
		Arc2D.Double scanArc = (Arc2D.Double) c.getScanArc().clone();
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
			g.fill(scanArc);
		} else {
			g.draw(scanArc);
		}
		g.setComposite(AlphaComposite.SrcOver);
		return scanArc.getBounds();
	}

	public void setBattleField(BattleField battleField) {
		this.battleField = battleField;
	}

	// TODO: FNL: Remove?
	public void setBattlePaused(boolean isPaused) {}

	/**
	 * Draws the Robocode title in giant letters
	 */
	private void paintRobocodeLogo(Graphics g) {
		setBackground(Color.BLACK);
		g.clearRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.DARK_GRAY);
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
	}

	public boolean isDisplayFps() {
		return displayFps;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
