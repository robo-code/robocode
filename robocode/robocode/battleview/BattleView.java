/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten
 *******************************************************************************/
package robocode.battleview;


import robocode.battle.Battle;
import robocode.battle.events.*;
import robocode.battle.snapshot.BulletSnapshot;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;
import robocode.dialog.RobocodeFrame;
import robocode.gfx.RenderImage;
import robocode.gfx.RobocodeLogo;
import robocode.manager.ImageManager;
import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;
import robocode.manager.RobocodeProperties.PropertyListener;
import robocode.robotpaint.Graphics2DProxy;
import robocode.peer.BulletState;
import robocode.util.GraphicsState;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import static java.lang.Math.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class BattleView extends Canvas implements IBattleObserver {
	private final static String ROBOCODE_SLOGAN = "Build the best, destroy the rest";

	private final static Color CANVAS_BG_COLOR = SystemColor.controlDkShadow;

	private final static Area BULLET_AREA = new Area(new Ellipse2D.Double(-0.5, -0.5, 1, 1));

	private final static int ROBOT_TEXT_Y_OFFSET = 24;

	private RobocodeFrame robocodeFrame;

	// The battle and battlefield,
	private Battle battle;
	private BattleField battleField;

	private boolean initialized;
	private double scale = 1.0;

	// Ground
	private int[][] groundTiles;

	private int groundTileWidth = 64;
	private int groundTileHeight = 64;

	private Image groundImage;

	// Draw option related things
	private boolean drawRobotName;
	private boolean drawRobotEnergy;
	private boolean drawScanArcs;
	private boolean drawExplosions;
	private boolean drawGround;
	private boolean drawExplosionDebris;

	private int numBuffers = 2; // defaults to double buffering

	private RenderingHints renderingHints;

	// TPS and FPS
	private boolean displayTPS;
	private boolean displayFPS;

	// Fonts and the like
	private Font smallFont;
	private FontMetrics smallFontMetrics;

	private final ImageManager imageManager;

	private final RobocodeManager manager;

	private BufferStrategy bufferStrategy;

	private Image offscreenImage;
	private Graphics2D offscreenGfx;

	private GeneralPath robocodeTextPath = new RobocodeLogo().getRobocodeText();

	private static MirroredGraphics mirroredGraphics = new MirroredGraphics();

	private GraphicsState graphicsState = new GraphicsState();

	// Property listener
	private PropertyListener propertyListener;

	/**
	 * BattleView constructor.
	 */
	public BattleView(RobocodeManager manager, RobocodeFrame robocodeFrame) {
		super();

		this.manager = manager;
		this.robocodeFrame = robocodeFrame;
		imageManager = manager.getImageManager();

		battleField = new DefaultBattleField(800, 600);
	}

	/**
	 * Shows the next frame. The game calls this every frame.
	 */
	private void update() {
		if (robocodeFrame.isIconified() || (getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		if (!initialized) {
			initialize();
		}

		if (offscreenImage != null && isDisplayable()) {
			offscreenGfx = (Graphics2D) offscreenImage.getGraphics();
			if (offscreenGfx != null) {
				offscreenGfx.setRenderingHints(renderingHints);

				drawBattle(offscreenGfx);

				if (bufferStrategy != null) {
					Graphics2D g = null;

					try {
						g = (Graphics2D) bufferStrategy.getDrawGraphics();
						g.drawImage(offscreenImage, 0, 0, null);

						bufferStrategy.show();
					} catch (NullPointerException e) {// Occurs sometimes for no reason?!
					} finally {
						if (g != null) {
							g.dispose();
						}
					}
				}
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		if (battle != null && battle.isRunning()) {
			update();
		} else {
			paintRobocodeLogo((Graphics2D) g);
		}
	}

	public void setDisplayOptions() {
		RobocodeProperties props = manager.getProperties();

		displayTPS = props.getOptionsViewTPS();
		displayFPS = props.getOptionsViewFPS();
		drawRobotName = props.getOptionsViewRobotNames();
		drawRobotEnergy = props.getOptionsViewRobotEnergy();
		drawScanArcs = props.getOptionsViewScanArcs();
		drawGround = props.getOptionsViewGround();
		drawExplosions = props.getOptionsViewExplosions();
		drawExplosionDebris = props.getOptionsViewExplosionDebris();

		renderingHints = props.getRenderingHints();
		numBuffers = props.getOptionsRenderingNoBuffers();
	}

	private void initialize() {
		setDisplayOptions();

		if (offscreenImage != null) {
			offscreenImage.flush();
			offscreenImage = null;
		}

		offscreenImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
		offscreenGfx = (Graphics2D) offscreenImage.getGraphics();

		if (bufferStrategy == null) {
			createBufferStrategy(numBuffers);
			bufferStrategy = getBufferStrategy();
		}

		// If we are scaled...
		if (getWidth() < battleField.getWidth() || getHeight() < battleField.getHeight()) {
			// Use the smaller scale.
			// Actually we don't need this, since
			// the RobocodeFrame keeps our aspect ratio intact.

			scale = min((double) getWidth() / battleField.getWidth(), (double) getHeight() / battleField.getHeight());

			offscreenGfx.scale(scale, scale);
		} else {
			scale = 1;
		}

		// Scale font
		smallFont = new Font("Dialog", Font.PLAIN, (int) (10 / scale));
		smallFontMetrics = offscreenGfx.getFontMetrics(smallFont);

		// Initialize ground image
		if (drawGround) {
			createGroundImage();
		} else {
			groundImage = null;
		}

		setDisplayOptions();
		desiredTPS = manager.getProperties().getOptionsBattleDesiredTPS();

		RobocodeProperties props = manager.getProperties();

		desiredTPS = props.getOptionsBattleDesiredTPS();

		propertyListener = props.new PropertyListener() {
			@Override
			public void desiredTpsChanged(int tps) {
				desiredTPS = tps;
			}
		};

		props.addPropertyListener(propertyListener);

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
					groundTiles[y][x] = (int) round(random() * 4);
				}
			}
		}

		// Create new buffered image with the ground pre-rendered

		int groundWidth = (int) (battleField.getWidth() * scale);
		int groundHeight = (int) (battleField.getHeight() * scale);

		groundImage = new BufferedImage(groundWidth, groundHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D groundGfx = (Graphics2D) groundImage.getGraphics();

		groundGfx.setRenderingHints(renderingHints);

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

	private void drawBattle(Graphics2D g) {
		if (battle == null || battle.isAborted()) {
			return;
		}

		// Save the graphics state
		graphicsState.save(g);

		// Reset transform
		g.setTransform(new AffineTransform());

		// Reset clip
		g.setClip(null);

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

		// Draw scan arcs
		drawScanArcs(g);

		// Draw robots
		drawRobots(g);

		// Draw the border of the battlefield
		drawBorder(g);

		// Draw all bullets
		drawBullets(g);

		// Draw all text
		drawText(g);

		// Restore the graphics state
		graphicsState.restore(g);
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
				int groundWidth = (int) (battleField.getWidth() * scale) + 1;
				int groundHeight = (int) (battleField.getHeight() * scale) + 1;

				int dx = (getWidth() - groundWidth) / 2;
				int dy = (getHeight() - groundHeight) / 2;

				AffineTransform savedTx = g.getTransform();

				g.setTransform(new AffineTransform());

				g.drawImage(groundImage, dx, dy, groundWidth, groundHeight, null);

				g.setTransform(savedTx);
			}
		}
	}

	private void drawBorder(Graphics2D g) {
		final Shape savedClip = g.getClip();

		g.setClip(null);

		g.setColor(Color.RED);
		g.drawRect(-1, -1, battleField.getWidth() + 2, battleField.getHeight() + 2);

		g.setClip(savedClip);
	}

	private void drawScanArcs(Graphics2D g) {
		if (drawScanArcs || battle != null) {
			for (RobotSnapshot robotSnapshot : battle.getBattleSnapshot().getRobots()) {
				if (robotSnapshot.getState().isAlive()) {
					drawScanArc(g, robotSnapshot);
				}
			}
		}
	}

	private void drawRobots(Graphics2D g) {
		if (battle == null) {
			return;
		}
		
		double x, y;
		AffineTransform at;
		int battleFieldHeight = battle.getBattleField().getHeight();

		if (drawGround && drawExplosionDebris && battle.isRobotsLoaded()) {
			RenderImage explodeDebrise = imageManager.getExplosionDebriseRenderImage();

			for (RobotSnapshot robotSnapshot : battle.getBattleSnapshot().getRobots()) {
				if (robotSnapshot.getState().isDead()) {
					x = robotSnapshot.getX();
					y = battleFieldHeight - robotSnapshot.getY();

					at = AffineTransform.getTranslateInstance(x, y);

					explodeDebrise.setTransform(at);
					explodeDebrise.paint(g);
				}
			}
		}

		for (RobotSnapshot robotSnapshot : battle.getBattleSnapshot().getRobots()) {
			if (robotSnapshot.getState().isAlive()) {
				x = robotSnapshot.getX();
				y = battleFieldHeight - robotSnapshot.getY();

				at = AffineTransform.getTranslateInstance(x, y);
				at.rotate(robotSnapshot.getBodyHeading());

				RenderImage robotRenderImage = imageManager.getColoredBodyRenderImage(robotSnapshot.getBodyColor());

				robotRenderImage.setTransform(at);
				robotRenderImage.paint(g);

				at = AffineTransform.getTranslateInstance(x, y);
				at.rotate(robotSnapshot.getGunHeading());

				RenderImage gunRenderImage = imageManager.getColoredGunRenderImage(robotSnapshot.getGunColor());

				gunRenderImage.setTransform(at);
				gunRenderImage.paint(g);

				if (!robotSnapshot.isDroid()) {
					at = AffineTransform.getTranslateInstance(x, y);
					at.rotate(robotSnapshot.getRadarHeading());

					RenderImage radarRenderImage = imageManager.getColoredRadarRenderImage(robotSnapshot.getRadarColor());

					radarRenderImage.setTransform(at);
					radarRenderImage.paint(g);
				}
			}
		}
	}

	private void drawText(Graphics2D g) {
		if (battle == null) {
			return;
		}

		Shape savedClip = g.getClip();

		g.setClip(null);

		for (RobotSnapshot robotSnapshot : battle.getBattleSnapshot().getRobots()) {
			if (robotSnapshot.getState().isDead()) {
				continue;
			}
			int x = (int) robotSnapshot.getX();
			int y = battle.getBattleField().getHeight() - (int) robotSnapshot.getY();

			if (drawRobotEnergy) {
				g.setColor(Color.white);
				int ll = (int) robotSnapshot.getEnergy();
				int rl = (int) ((robotSnapshot.getEnergy() - ll + .001) * 10.0);

				if (rl == 10) {
					rl = 9;
				}
				String energyString = ll + "." + rl;

				if (robotSnapshot.getEnergy() == 0 && robotSnapshot.getState().isAlive()) {
					energyString = "Disabled";
				}
				centerString(g, energyString, x, y - ROBOT_TEXT_Y_OFFSET - smallFontMetrics.getHeight() / 2, smallFont,
						smallFontMetrics);
			}
			if (drawRobotName) {
				g.setColor(Color.white);
				centerString(g, robotSnapshot.getVeryShortName(), x,
						y + ROBOT_TEXT_Y_OFFSET + smallFontMetrics.getHeight() / 2, smallFont, smallFontMetrics);
			}
			if (robotSnapshot.isPaintEnabled()) {
				drawRobotPaint(g, robotSnapshot);
			}
		}

		g.setClip(savedClip);
	}

	private void drawRobotPaint(Graphics2D g, RobotSnapshot robotSnapshot) {
		// Save the graphics state
		GraphicsState gfxState = new GraphicsState();

		gfxState.save(g);

		g.setClip(0, 0, battleField.getWidth(), battleField.getHeight());

		if (robotSnapshot.isPaintRobot()) {
			Graphics2DProxy gfxProxy = robotSnapshot.getGraphicsProxy();

			if (gfxProxy != null) {
				// Do the painting
				try {
					if (robotSnapshot.isSGPaintEnabled()) {
						gfxProxy.processTo(g);
					} else {
						mirroredGraphics.bind(g, battleField.getHeight());
						gfxProxy.processTo(g);
						mirroredGraphics.release();
					}
				} catch (Exception e) {
					// Make sure that Robocode is not halted by an exception caused by letting the robot paint
					robotSnapshot.getOut().println("SYSTEM: Exception occurred on onPaint(Graphics2D):");
					e.printStackTrace(robotSnapshot.getOut());
				}
			}
		}

		// Restore the graphics state
		gfxState.restore(g);
	}

	private void drawBullets(Graphics2D g) {
		if (battle == null) {
			return;
		}

		Shape savedClip = g.getClip();

		g.setClip(null);

		double x, y;

		for (BulletSnapshot bulletSnapshot : battle.getBattleSnapshot().getBullets()) {
			x = bulletSnapshot.getPaintX();
			y = battle.getBattleField().getHeight() - bulletSnapshot.getPaintY();

			AffineTransform at = AffineTransform.getTranslateInstance(x, y);

			if (bulletSnapshot.getState().getValue() <= BulletState.MOVING.getValue()) {

				// radius = sqrt(x^2 / 0.1 * power), where x is the width of 1 pixel for a minimum 0.1 bullet
				double scale = max(2 * sqrt(2.5 * bulletSnapshot.getPower()), 2 / this.scale);

				at.scale(scale, scale);
				Area bulletArea = BULLET_AREA.createTransformedArea(at);

				Color bulletColor;

				if (manager.getProperties().getOptionsRenderingForceBulletColor()) {
					bulletColor = Color.WHITE;
				} else {
					bulletColor = bulletSnapshot.getColor();
					if (bulletColor == null) {
						bulletColor = Color.WHITE;
					}
				}
				g.setColor(bulletColor);
				g.fill(bulletArea);

			} else if (drawExplosions) {
				if (!bulletSnapshot.isExplosion()) {
					double scale = sqrt(1000 * bulletSnapshot.getPower()) / 128;

					at.scale(scale, scale);
				}

				RenderImage explosionRenderImage = imageManager.getExplosionRenderImage(
						bulletSnapshot.getExplosionImageIndex(), bulletSnapshot.getFrame());

				explosionRenderImage.setTransform(at);
				explosionRenderImage.paint(g);
			}
		}
		g.setClip(savedClip);
	}

	private void centerString(Graphics2D g, String s, int x, int y, Font font, FontMetrics fm) {
		g.setFont(font);

		int width = fm.stringWidth(s);
		int height = fm.getHeight();
		int descent = fm.getDescent();

		double left = x - width / 2;
		double top = y - height / 2;

		double scaledViewWidth = getWidth() / scale;
		double scaledViewHeight = getHeight() / scale;

		double borderWidth = (scaledViewWidth - battleField.getWidth()) / 2;
		double borderHeight = (scaledViewHeight - battleField.getHeight()) / 2;

		if (left + width > scaledViewWidth) {
			left = scaledViewWidth - width;
		}
		if (top + height > scaledViewHeight) {
			top = scaledViewHeight - height;
		}
		if (left < -borderWidth) {
			left = -borderWidth;
		}
		if (top < -borderHeight) {
			top = -borderHeight;
		}
		g.drawString(s, (int) (left + 0.5), (int) (top + height - descent + 0.5));
	}

	private Rectangle drawScanArc(Graphics2D g, RobotSnapshot robotSnapshot) {
		Arc2D.Double scanArc = (Arc2D.Double) robotSnapshot.getScanArc();

		if (scanArc == null) {
			return null;
		}

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) .2));

		scanArc.setAngleStart((360 - scanArc.getAngleStart() - scanArc.getAngleExtent()) % 360);
		scanArc.y = battle.getBattleField().getHeight() - robotSnapshot.getY() - robocode.Rules.RADAR_SCAN_RADIUS;

		Color scanColor = robotSnapshot.getScanColor();

		if (scanColor == null) {
			scanColor = Color.BLUE;
		}
		g.setColor(scanColor);

		if (abs(scanArc.getAngleExtent()) >= .5) {
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

	/**
	 * Draws the Robocode logo
	 */
	private void paintRobocodeLogo(Graphics2D g) {
		setBackground(Color.BLACK);
		g.clearRect(0, 0, getWidth(), getHeight());

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.transform(AffineTransform.getTranslateInstance((getWidth() - 320) / 2.0, (getHeight() - 46) / 2.0));
		g.setColor(new Color(0, 0x40, 0));
		g.fill(robocodeTextPath);

		Font font = new Font("Dialog", Font.BOLD, 14);
		int width = g.getFontMetrics(font).stringWidth(ROBOCODE_SLOGAN);

		g.setTransform(new AffineTransform());
		g.setFont(font);
		g.setColor(new Color(0, 0x50, 0));
		g.drawString(ROBOCODE_SLOGAN, (float) ((getWidth() - width) / 2.0), (float) (getHeight() / 2.0 + 50));
	}

	public boolean isDisplayTPS() {
		return displayTPS;
	}

	public boolean isDisplayFPS() {
		return displayFPS;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	private IBattleListener battleEventListener = new BattleEventHandler();

	public IBattleListener getBattleListener() {
		return battleEventListener;
	}

	private class BattleEventHandler extends BattleAdaptor {

		public void onBattleStarted(BattleStartedEvent event) {
			battle = manager.getBattleManager().getBattle(); // FIXME: Should be avoided!
			repaint();
		}

		public void onBattleEnded(BattleEndedEvent event) {
			battle = null;
			repaint();
		}

		public void onRoundStarted(RoundStartedEvent event) {
			turnsThisSec = 0;
			framesThisSec = 0;
			currentRobotMillis = 0;
			totalRobotMillisThisSec = 0;
			totalFrameMillisThisSec = 0;
			estimatedFPS = 0;
			delay = 0;

			resetThisSec = true;

			if (!isMinimizedMode()) {
				update();
			}
		}

		public void onTurnStarted(TurnStartedEvent event) {
			turnStartTime = System.currentTimeMillis();

			resetSec();

			turnsThisSec++;

			// Paint current battle frame
			displayTurn();

			// Measure timing
			measureTime();
		}
	}

	private int turnsPerSecond;
	private int framesPerSecond;
	
	public int getTPS() {
		return turnsPerSecond;
	}
	
	public int getFPS() {
		return framesPerSecond;
	}

	private int desiredTPS = 30;
	private long startTimeThisSec = 0;

	private int turnsThisSec;
	private int framesThisSec;

	private long turnStartTime;

	private int currentRobotMillis;
	private int totalRobotMillisThisSec;
	private int totalFrameMillisThisSec;
	private int totalTurnMillisThisSec;

	private float estFrameTimeThisSec;
	private float estimatedFPS;
	private int estimatedTurnMillisThisSec;

	private int delay;

	private boolean resetThisSec;

	private void resetSec() {
		if (resetThisSec) {
			resetThisSec = false;

			startTimeThisSec = turnStartTime;

			turnsPerSecond = turnsThisSec;
			framesPerSecond = framesThisSec;
			
			turnsThisSec = 0;
			framesThisSec = 0;

			totalRobotMillisThisSec = 0;
			totalFrameMillisThisSec = 0;
		}
	}

	private void displayTurn() {
		if (!(isMinimizedMode() || battle == null || battle.isAborted()/* || endTimer >= TURNS_DISPLAYED_AFTER_ENDING */)) {
			// Update the battle view if the frame has not been painted yet this second
			// or if it's time to paint the next frame
			if ((estimatedFPS * turnsThisSec / desiredTPS) >= framesThisSec) {
				update();
				framesThisSec++;
			}
			// FIXME:			playSounds();
		}
	}

	private void measureTime() {
		// Calculate the total time spend on robots this second
		totalRobotMillisThisSec += currentRobotMillis;

		// Calculate the total time used for the frame update
		totalFrameMillisThisSec += (int) (System.currentTimeMillis() - turnStartTime) - currentRobotMillis;

		// Calculate the total turn time this second
		totalTurnMillisThisSec = max(1, totalRobotMillisThisSec + totalFrameMillisThisSec);

		// Estimate the time remaining this second to spend on frame updates
		estFrameTimeThisSec = max(0, 1000 - desiredTPS * totalRobotMillisThisSec / turnsThisSec);

		// Estimate the possible FPS based on the estimated frame time
		estimatedFPS = max(1, framesThisSec * estFrameTimeThisSec / totalFrameMillisThisSec);

		// Estimate the time that will be used on the total turn this second
		estimatedTurnMillisThisSec = desiredTPS * totalTurnMillisThisSec / turnsThisSec;

		// Calculate delay needed for keeping the desired TPS (Turns Per Second)
		if (/* endTimer >= TURNS_DISPLAYED_AFTER_ENDING ||*/isMinimizedMode()) {
			delay = 0;
		} else {
			delay = (estimatedTurnMillisThisSec >= 1000) ? 0 : (1000 - estimatedTurnMillisThisSec) / desiredTPS;
		}

		// Set flag for if the second has passed
		resetThisSec = (System.currentTimeMillis() - startTimeThisSec) >= 1000;

		// Check if we must limit the TPS
		if (!(resetThisSec || isMinimizedMode())) {
			resetThisSec = ((desiredTPS - turnsThisSec) == 0);
		}

		// Delay to match desired TPS
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// Set the thread status back to being interrupted
				Thread.currentThread().interrupt();
			}
		}
	}

	private boolean isMinimizedMode() {
		return manager.getWindowManager().getRobocodeFrame().isIconified();
	}
}
