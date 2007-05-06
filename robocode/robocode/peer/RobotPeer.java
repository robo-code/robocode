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
 *     - Code cleanup
 *     - Bugfix: updateMovement() checked for distanceRemaining > 1 instead of
 *       distanceRemaining > 0 if slowingDown and moveDirection == -1
 *     - Bugfix: Substituted wait(10000) with wait() in tick() method, so that
 *       robots do not hang when game is paused
 *     - Bugfix: Teleportation when turning the robot to 0 degrees while forcing
 *       the robot towards the bottom
 *     - Added setPaintEnabled() and isPaintEnabled()
 *     - Added setSGPaintEnabled() and isSGPaintEnabled()
 *     - Replaced the colorIndex with bodyColor, gunColor, and radarColor
 *     - Replaced the setColors() with setBodyColor(), setGunColor(), and
 *       setRadarColor()
 *     - Added bulletColor, scanColor, setBulletColor(), and setScanColor() and
 *       removed getColorIndex()
 *     - Optimizations
 *     - Ported to Java 5
 *     - Bugfix: HitRobotEvent.isMyFault() returned false despite the fact that
 *       the robot was moving toward the robot it collides with. This was the
 *       case when distanceRemaining == 0
 *     - Removed isDead field as the robot state is used as replacement
 *     - Added isAlive() method
 *     - Added constructor for creating a new robot with a name only
 *     - Added the set() that copies a RobotRecord into this robot in order to
 *       support the replay feature
 *     - Fixed synchronization issues with several member fields
 *     Luis Crespo
 *     - Added states
 *     Titus Chen
 *     - Bugfix: Hit wall and teleporting problems with checkWallCollision()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.peer;


import static java.lang.Math.*;
import static robocode.io.Logger.log;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalNearAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

import robocode.*;
import robocode.battle.Battle;
import robocode.battle.record.RobotRecord;
import robocode.battlefield.BattleField;
import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.RobotException;
import robocode.exception.WinException;
import robocode.manager.NameManager;
import robocode.peer.robot.*;
import robocode.util.BoundingRectangle;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Titus Chen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotPeer implements Runnable, ContestantPeer {

	// Robot States: all states last one turn, except ALIVE and DEAD
	public static final int
			STATE_ACTIVE = 0,
			STATE_HIT_WALL = 1,
			STATE_HIT_ROBOT = 2,
			STATE_DEAD = 3;

	public static final int
			WIDTH = 40,
			HEIGHT = 40;

	private static final int
			HALF_WIDTH_OFFSET = (WIDTH / 2 - 2),
			HALF_HEIGHT_OFFSET = (HEIGHT / 2 - 2);

	private static final long
			MAX_SET_CALL_COUNT = 10000,
			MAX_GET_CALL_COUNT = 10000;

	public RobotOutputStream out;

	private double energy;
	private double velocity;
	private double heading;
	private double radarHeading;
	private double gunHeading;
	private double x;
	private double y;

	private double acceleration;
	private double maxVelocity = Rules.MAX_VELOCITY; // Can be changed by robot
	private double maxTurnRate = Rules.MAX_TURN_RATE_RADIANS; // Can be changed by robot

	private double turnRemaining;
	private double radarTurnRemaining;
	private double gunTurnRemaining;

	private double distanceRemaining;

	private double turnRate;

	private Robot robot;

	private BattleField battleField;

	private BoundingRectangle boundingBox;

	private Arc2D scanArc;

	private boolean isDroid;
	private boolean isIORobot;
	private boolean isRunning;
	private boolean isStopped;
	private boolean isSleeping;
	private boolean isWinner;

	private double lastGunHeading;
	private double lastHeading;
	private double lastRadarHeading;
	private double lastX;
	private double lastY;

	private double saveAngleToTurn;
	private double saveDistanceToGo;
	private double saveGunAngleToTurn;
	private double saveRadarAngleToTurn;
	private boolean scan;

	private Battle battle;

	private EventManager eventManager;

	private Condition waitCondition;

	private boolean isAdjustGunForBodyTurn;
	private boolean isAdjustRadarForGunTurn;
	private boolean isAdjustRadarForBodyTurn;

	private boolean isAdjustRadarForBodyTurnSet;

	private boolean checkFileQuota;
	private double gunHeat;
	private boolean halt;
	private boolean inCollision;

	private String name;
	private String shortName;
	private String nonVersionedName;

	private int setCallCount;
	private int getCallCount;

	private RobotClassManager robotClassManager;
	private RobotFileSystemManager robotFileSystemManager;
	private RobotThreadManager robotThreadManager;

	private int skippedTurns;

	private RobotStatistics statistics;

	private Color bodyColor;
	private Color gunColor;
	private Color radarColor;
	private Color bulletColor;
	private Color scanColor;

	private RobotMessageManager messageManager;

	private TeamPeer teamPeer;

	private boolean isDuplicate;
	private boolean slowingDown;

	private int moveDirection;

	private boolean testingCondition;

	private BulletPeer newBullet;

	private boolean paintEnabled;
	private boolean sgPaintEnabled;

	protected int state;

	public RobotPeer(String name) {
		this.name = name;
	}

	public boolean isIORobot() {
		return isIORobot;
	}

	public void setIORobot(boolean ioRobot) {
		this.isIORobot = ioRobot;
	}

	public synchronized void setTestingCondition(boolean testingCondition) {
		this.testingCondition = testingCondition;
	}

	public synchronized boolean getTestingCondition() {
		return testingCondition;
	}

	public boolean isDroid() {
		return isDroid;
	}

	public void setDroid(boolean droid) {
		this.isDroid = droid;
	}

	public final void move(double distance) {
		setMove(distance);
		do {
			tick(); // Always tick at least once
		} while (distanceRemaining != 0);
	}

	private void checkRobotCollision() {
		inCollision = false;

		for (int i = 0; i < battle.getRobots().size(); i++) {
			RobotPeer r = battle.getRobots().get(i);

			if (!(r == null || r == this || r.isDead()) && boundingBox.intersects(r.boundingBox)) {
				// Bounce back
				double angle = atan2(r.getX() - x, r.getY() - y);

				double movedx = velocity * sin(heading);
				double movedy = velocity * cos(heading);

				boolean atFault = false;
				double bearing = normalRelativeAngle(angle - heading);

				if (velocity > 0 && bearing > -PI / 2 && bearing < PI / 2) {
					velocity = 0;
					atFault = true;
					distanceRemaining = 0;
					statistics.scoreRammingDamage(i, Rules.ROBOT_HIT_DAMAGE);
					this.setEnergy(energy - Rules.ROBOT_HIT_DAMAGE);
					r.setEnergy(r.getEnergy() - Rules.ROBOT_HIT_DAMAGE);
					inCollision = true;
					x -= movedx;
					y -= movedy;

					if (r.getEnergy() == 0) {
						if (r.isAlive()) {
							r.kill();
							statistics.scoreRammingKill(i);
						}
					}
				} else if (velocity < 0 && (bearing < -PI / 2 || bearing > PI / 2)) {
					velocity = 0;
					atFault = true;
					distanceRemaining = 0;
					statistics.scoreRammingDamage(i, Rules.ROBOT_HIT_DAMAGE);
					this.setEnergy(energy - Rules.ROBOT_HIT_DAMAGE);
					r.setEnergy(r.getEnergy() - Rules.ROBOT_HIT_DAMAGE);
					inCollision = true;
					x -= movedx;
					y -= movedy;

					if (r.getEnergy() == 0) {
						if (r.isAlive()) {
							r.kill();
							statistics.scoreRammingKill(i);
						}
					}
				}

				eventManager.add(
						new HitRobotEvent(r.getName(), normalRelativeAngle(angle - heading), r.getEnergy(), atFault));
				r.eventManager.add(
						new HitRobotEvent(getName(), normalRelativeAngle(PI + angle - r.getHeading()), energy, false));

			} // if robot active & not me & hit
		} // for robots
		if (inCollision) {
			state = STATE_HIT_ROBOT;
		}
	}

	private void checkWallCollision() {
		boolean hitWall = false;
		double fixx = 0, fixy = 0;
		double angle = 0;

		if (x > getBattleFieldWidth() - HALF_WIDTH_OFFSET) {
			hitWall = true;
			fixx = getBattleFieldWidth() - HALF_WIDTH_OFFSET - x;
			angle = normalRelativeAngle(PI / 2 - heading);
		}

		if (x < HALF_WIDTH_OFFSET) {
			hitWall = true;
			fixx = HALF_WIDTH_OFFSET - x;
			angle = normalRelativeAngle(3 * PI / 2 - heading);
		}

		if (y > getBattleFieldHeight() - HALF_HEIGHT_OFFSET) {
			hitWall = true;
			fixy = getBattleFieldHeight() - HALF_HEIGHT_OFFSET - y;
			angle = normalRelativeAngle(-heading);
		}

		if (y < HALF_HEIGHT_OFFSET) {
			hitWall = true;
			fixy = HALF_HEIGHT_OFFSET - y;
			angle = normalRelativeAngle(PI - heading);
		}

		if (hitWall) {
			eventManager.add(new HitWallEvent(angle));

			// only fix both x and y values if hitting wall at an angle
			if ((heading % (Math.PI / 2)) != 0) {
				double tanHeading = tan(heading);

				// if it hits bottom or top wall
				if (fixx == 0) {
					fixx = fixy * tanHeading;
				} // if it hits a side wall
				else if (fixy == 0) {
					fixy = fixx / tanHeading;
				} // if the robot hits 2 walls at the same time (rare, but just in case)
				else if (abs(fixx / tanHeading) > abs(fixy)) {
					fixy = fixx / tanHeading;
				} else if (abs(fixy * tanHeading) > abs(fixx)) {
					fixx = fixy * tanHeading;
				}
			}
			x += fixx;
			y += fixy;

			x = (HALF_WIDTH_OFFSET >= x)
					? HALF_WIDTH_OFFSET
					: ((getBattleFieldWidth() - HALF_WIDTH_OFFSET < x) ? getBattleFieldWidth() - HALF_WIDTH_OFFSET : x);
			y = (HALF_HEIGHT_OFFSET >= y)
					? HALF_HEIGHT_OFFSET
					: ((getBattleFieldHeight() - HALF_HEIGHT_OFFSET < y) ? getBattleFieldHeight() - HALF_HEIGHT_OFFSET : y);

			// Update energy, but do not reset inactiveTurnCount
			if (robot instanceof robocode.AdvancedRobot) {
				this.setEnergy(energy - Rules.getWallHitDamage(velocity), false);
			}

			updateBoundingBox();

			distanceRemaining = 0;
			velocity = 0;
			acceleration = 0;
		}
		if (hitWall) {
			state = STATE_HIT_WALL;
		}
	}

	public final void death() {
		throw new DeathException();
	}

	public Battle getBattle() {
		return battle;
	}

	public double getBattleFieldHeight() {
		return battleField.getHeight();
	}

	public double getBattleFieldWidth() {
		return battleField.getWidth();
	}

	public BoundingRectangle getBoundingBox() {
		return boundingBox;
	}

	public synchronized double getGunHeading() {
		return gunHeading;
	}

	public synchronized double getHeading() {
		return heading;
	}

	public String getName() {
		return (name != null) ? name : robotClassManager.getClassNameManager().getFullClassNameWithVersion();
	}

	public String getShortName() {
		return (shortName != null)
				? shortName
				: robotClassManager.getClassNameManager().getUniqueShortClassNameWithVersion();
	}

	public String getVeryShortName() {
		return (shortName != null)
				? shortName
				: robotClassManager.getClassNameManager().getUniqueVeryShortClassNameWithVersion();
	}

	public String getNonVersionedName() {
		return (nonVersionedName != null)
				? nonVersionedName
				: robotClassManager.getClassNameManager().getFullClassName();
	}

	public int getOthers() {
		return battle.getActiveRobots() - (isAlive() ? 1 : 0);
	}

	public synchronized double getRadarHeading() {
		return radarHeading;
	}

	public synchronized double getX() {
		return x;
	}

	public synchronized double getY() {
		return y;
	}

	public synchronized boolean isAdjustGunForBodyTurn() {
		return isAdjustGunForBodyTurn;
	}

	public synchronized boolean isAdjustRadarForGunTurn() {
		return isAdjustRadarForGunTurn;
	}

	public synchronized boolean isDead() {
		return state == STATE_DEAD;
	}

	public synchronized boolean isAlive() {
		return state != STATE_DEAD;
	}

	public void run() {
		setRunning(true);

		try {
			if (robot != null) {
				robot.run();
			}
			for (;;) {
				tick();
			}
		} catch (DeathException e) {
			out.println("SYSTEM: " + getName() + " has died");
		} catch (WinException e) {
			; // Do nothing
		} catch (DisabledException e) {
			setEnergy(0);
			String msg = e.getMessage();

			if (msg == null) {
				msg = "";
			} else {
				msg = ": " + msg;
			}
			out.println("SYSTEM: Robot disabled" + msg);
		} catch (Exception e) {
			out.println(getName() + ": Exception: " + e);
			out.printStackTrace(e);
		} catch (Throwable t) {
			if (!(t instanceof ThreadDeath)) {
				out.println(getName() + ": Throwable: " + t);
				out.printStackTrace(t);
			} else {
				log(getName() + " stopped successfully.");
			}
		}
		setRunning(false);

		// If battle is waiting for us, well, all done!
		synchronized (this) {
			notify();
		}
	}

	private boolean intersects(Arc2D arc, Rectangle2D rect) {
		return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
				arc.getStartPoint().getY()))
				? true
				: arc.intersects(rect);
	}

	public void scan() {
		if (isDroid) {
			return;
		}

		double startAngle = getLastRadarHeading();
		double scanRadians = getRadarHeading() - startAngle;

		// Check if we passed through 360
		if (scanRadians < -PI) {
			scanRadians = 2 * PI + scanRadians;
		} else if (scanRadians > PI) {
			scanRadians = scanRadians - 2 * PI;
		}

		// In our coords, we are scanning clockwise, with +y up
		// In java coords, we are scanning counterclockwise, with +y down
		// All we need to do is adjust our angle by -90 for this to work.
		startAngle -= PI / 2;

		startAngle = normalAbsoluteAngle(startAngle);

		scanArc.setArc(getX() - Rules.RADAR_SCAN_RADIUS, getY() - Rules.RADAR_SCAN_RADIUS, 2 * Rules.RADAR_SCAN_RADIUS,
				2 * Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

		for (RobotPeer r : battle.getRobots()) {
			if (!(r == null || r == this || r.isDead()) && intersects(scanArc, r.boundingBox)) {
				double dx = r.getX() - getX();
				double dy = r.getY() - getY();
				double angle = atan2(dx, dy);
				double dist = Math.hypot(dx, dy);

				eventManager.add(
						new ScannedRobotEvent(r.getName(), r.getEnergy(), normalRelativeAngle(angle - getHeading()), dist,
						r.getHeading(), r.getVelocity()));
			}
		}
	}

	public synchronized void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		isAdjustGunForBodyTurn = newAdjustGunForBodyTurn;
	}

	public synchronized void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		isAdjustRadarForGunTurn = newAdjustRadarForGunTurn;
		if (!isAdjustRadarForBodyTurnSet) {
			isAdjustRadarForBodyTurn = newAdjustRadarForGunTurn;
		}
	}

	public final synchronized void setMove(double distance) {
		if (energy == 0) {
			return;
		}
		distanceRemaining = distance;
		acceleration = 0;

		if (distance == 0) {
			moveDirection = 0;
		} else if (distance > 0) {
			moveDirection = 1;
		} else {
			moveDirection = -1;
		}
		slowingDown = false;
	}

	public void setBattle(Battle newBattle) {
		battle = newBattle;
		battleField = battle.getBattleField();
	}

	public synchronized void kill() {
		battle.resetInactiveTurnCount(10.0);
		if (isAlive()) {
			eventManager.add(new DeathEvent());
			if (isTeamLeader()) {
				for (RobotPeer teammate : teamPeer) {
					if (!(teammate.isDead() || teammate == this)) {
						teammate.setEnergy(teammate.getEnergy() - 30);

						BulletPeer sBullet = new BulletPeer(this, battle);

						sBullet.setState(BulletPeer.STATE_HIT_VICTIM);
						sBullet.setX(teammate.getX());
						sBullet.setY(teammate.getY());
						sBullet.setVictim(teammate);
						sBullet.hasHitVictim = true;
						sBullet.setPower(4);
						battle.addBullet(sBullet);
					}
				}
			}
			battle.generateDeathEvents(this);
			// 'fake' bullet for explosion on self
			BulletPeer sBullet = new ExplosionPeer(this, battle);

			sBullet.setX(x);
			sBullet.setY(y);
			battle.addBullet(sBullet);
		}
		setEnergy(0);

		state = STATE_DEAD;
	}

	public synchronized void preInitialize() {
		state = STATE_DEAD;
	}

	public synchronized void setGunHeading(double newGunHeading) {
		gunHeading = newGunHeading;
	}

	public synchronized void setHeading(double heading) {
		this.heading = heading;
	}

	public synchronized void setRadarHeading(double newRadarHeading) {
		radarHeading = newRadarHeading;
	}

	public synchronized void setX(double newX) {
		x = newX;
	}

	public synchronized void setY(double newY) {
		y = newY;
	}

	public final void tick() {
		if (newBullet != null) {
			battle.addBullet(newBullet);
			newBullet = null;
		}

		// Entering tick
		if (Thread.currentThread() != robotThreadManager.getRunThread()) {
			throw new RobotException("You cannot take action in this thread!");
		}
		if (testingCondition) {
			throw new RobotException(
					"You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
		}

		setSetCallCount(0);
		setGetCallCount(0);

		// This stops autoscan from scanning...
		if (waitCondition != null && waitCondition.test()) {
			waitCondition = null;
		}

		// If we are stopping, yet the robot took action (in onWin or onDeath), stop now.
		if (halt) {
			if (isDead()) {
				death();
			} else if (isWinner) {
				throw new WinException();
			}
		}

		synchronized (this) {
			// Notify the battle that we are now asleep.
			// This ends any pending wait() call in battle.runRound().
			// Should not actually take place until we release the lock in wait(), below.
			notify();
			isSleeping = true;
			// Notifying battle that we're asleep
			// Sleeping and waiting for battle to wake us up.
			try {
				wait();
			} catch (InterruptedException e) {
				log("Wait interrupted");
			}
			isSleeping = false;
			// Notify battle thread, which is waiting in
			// our wakeup() call, to return.
			// It's quite possible, by the way, that we'll be back in sleep (above)
			// before the battle thread actually wakes up
			notify();
		}

		eventManager.setFireAssistValid(false);

		if (isDead()) {
			setHalt(true);
		}

		eventManager.processEvents();

		out.resetCounter();
	}

	public synchronized final void setTurnGun(double radians) {
		this.gunTurnRemaining = radians;
	}

	public final void turnGun(double radians) {
		setTurnGun(radians);
		do {
			tick(); // Always tick at least once
		} while (gunTurnRemaining != 0);
	}

	public synchronized final void setTurnChassis(double radians) {
		if (energy > 0) {
			turnRemaining = radians;
		}
	}

	public final void turnChassis(double radians) {
		setTurnChassis(radians);
		do {
			tick(); // Always tick at least once
		} while (turnRemaining != 0);
	}

	public synchronized final void setTurnRadar(double radians) {
		this.radarTurnRemaining = radians;
	}

	public final void turnRadar(double radians) {
		setTurnRadar(radians);
		do {
			tick(); // Always tick at least once
		} while (radarTurnRemaining != 0);
	}

	public final synchronized void update() {
		// Reset robot state to active if it is not dead
		if (isAlive()) {
			state = STATE_ACTIVE;
		}

		updateGunHeat();

		lastHeading = heading;
		lastGunHeading = gunHeading;
		lastRadarHeading = radarHeading;

		if (!inCollision) {
			updateHeading();
		}

		updateGunHeading();
		updateRadarHeading();
		updateMovement();

		// At this point, robot has turned then moved.
		// We could be touching a wall or another bot...

		// First and foremost, we can never go through a wall:
		checkWallCollision();

		// Now check for robot collision
		checkRobotCollision();

		// Scan false means robot did not call scan() manually.
		// But if we're moving, scan
		if (!scan) {
			scan = (lastHeading != heading || lastGunHeading != gunHeading || lastRadarHeading != radarHeading
					|| lastX != x || lastY != y || waitCondition != null);
		}
	}

	public synchronized void updateBoundingBox() {
		boundingBox.setRect(x - WIDTH / 2 + 2, y - HEIGHT / 2 + 2, WIDTH - 4, HEIGHT - 4);
	}

	private void updateGunHeading() {
		if (gunTurnRemaining > 0) {
			if (gunTurnRemaining < Rules.GUN_TURN_RATE_RADIANS) {
				gunHeading += gunTurnRemaining;
				radarHeading += gunTurnRemaining;
				if (isAdjustRadarForGunTurn()) {
					radarTurnRemaining -= gunTurnRemaining;
				}
				gunTurnRemaining = 0;
			} else {
				gunHeading += Rules.GUN_TURN_RATE_RADIANS;
				radarHeading += Rules.GUN_TURN_RATE_RADIANS;
				gunTurnRemaining -= Rules.GUN_TURN_RATE_RADIANS;
				if (isAdjustRadarForGunTurn()) {
					radarTurnRemaining -= Rules.GUN_TURN_RATE_RADIANS;
				}
			}
		} else if (gunTurnRemaining < 0) {
			if (gunTurnRemaining > -Rules.GUN_TURN_RATE_RADIANS) {
				gunHeading += gunTurnRemaining;
				radarHeading += gunTurnRemaining;
				if (isAdjustRadarForGunTurn()) {
					radarTurnRemaining -= gunTurnRemaining;
				}
				gunTurnRemaining = 0;
			} else {
				gunHeading -= Rules.GUN_TURN_RATE_RADIANS;
				radarHeading -= Rules.GUN_TURN_RATE_RADIANS;
				gunTurnRemaining += Rules.GUN_TURN_RATE_RADIANS;
				if (isAdjustRadarForGunTurn()) {
					radarTurnRemaining += Rules.GUN_TURN_RATE_RADIANS;
				}
			}
		}
		gunHeading = normalAbsoluteAngle(gunHeading);
	}

	private void updateHeading() {
		boolean normalizeHeading = true;

		turnRate = min(maxTurnRate, (.4 + .6 * (1 - (abs(velocity) / Rules.MAX_VELOCITY))) * Rules.MAX_TURN_RATE_RADIANS);

		if (turnRemaining > 0) {
			if (turnRemaining < turnRate) {
				heading += turnRemaining;
				gunHeading += turnRemaining;
				radarHeading += turnRemaining;
				if (isAdjustGunForBodyTurn()) {
					gunTurnRemaining -= turnRemaining;
				}
				if (isAdjustRadarForBodyTurn()) {
					radarTurnRemaining -= turnRemaining;
				}
				turnRemaining = 0;
			} else {
				heading += turnRate;
				gunHeading += turnRate;
				radarHeading += turnRate;
				turnRemaining -= turnRate;
				if (isAdjustGunForBodyTurn()) {
					gunTurnRemaining -= turnRate;
				}
				if (isAdjustRadarForBodyTurn()) {
					radarTurnRemaining -= turnRate;
				}
			}
		} else if (turnRemaining < 0) {
			if (turnRemaining > -turnRate) {
				heading += turnRemaining;
				gunHeading += turnRemaining;
				radarHeading += turnRemaining;
				if (isAdjustGunForBodyTurn()) {
					gunTurnRemaining -= turnRemaining;
				}
				if (isAdjustRadarForBodyTurn()) {
					radarTurnRemaining -= turnRemaining;
				}
				turnRemaining = 0;
			} else {
				heading -= turnRate;
				gunHeading -= turnRate;
				radarHeading -= turnRate;
				turnRemaining += turnRate;
				if (isAdjustGunForBodyTurn()) {
					gunTurnRemaining += turnRate;
				}
				if (isAdjustRadarForBodyTurn()) {
					radarTurnRemaining += turnRate;
				}
			}
		} else {
			normalizeHeading = false;
		}

		if (normalizeHeading) {
			if (turnRemaining == 0) {
				heading = normalNearAbsoluteAngle(heading);
			} else {
				heading = normalAbsoluteAngle(heading);
			}
		}
		if (Double.isNaN(heading)) {
			System.out.println("HOW IS HEADING NAN HERE");
		}
	}

	private void updateMovement() {
		if (distanceRemaining == 0 && velocity == 0) {
			return;
		}

		lastX = x;
		lastY = y;

		if (!slowingDown) {
			// Set moveDir and slow down for move(0)
			if (moveDirection == 0) {
				// On move(0), we're always slowing down.
				slowingDown = true;

				// Pretend we were moving in the direction we're heading,
				if (velocity > 0) {
					moveDirection = 1;
				} else if (velocity < 0) {
					moveDirection = -1;
				} else {
					moveDirection = 0;
				}
			}
		}

		double desiredDistanceRemaining = distanceRemaining;

		if (slowingDown) {
			if (moveDirection == 1 && distanceRemaining < 0) {
				desiredDistanceRemaining = 0;
			} else if (moveDirection == -1 && distanceRemaining > 0) {
				desiredDistanceRemaining = 0;
			}
		}
		double slowDownVelocity = (int) ((Rules.DECELERATION / 2) * (sqrt(4 * abs(desiredDistanceRemaining) + 1) - 1));

		if (moveDirection == -1) {
			slowDownVelocity = -slowDownVelocity;
		}

		if (!slowingDown) {
			// Calculate acceleration
			if (moveDirection == 1) {
				// Brake or accelerate
				if (velocity < 0) {
					acceleration = Rules.DECELERATION;
				} else {
					acceleration = Rules.ACCELERATION;
				}

				if (velocity + acceleration > slowDownVelocity) {
					slowingDown = true;
				}
			} else if (moveDirection == -1) {
				if (velocity > 0) {
					acceleration = -Rules.DECELERATION;
				} else {
					acceleration = -Rules.ACCELERATION;
				}

				if (velocity + acceleration < slowDownVelocity) {
					slowingDown = true;
				}
			}
		}

		if (slowingDown) {
			// note:  if slowing down, velocity and distanceremaining have same sign
			if (distanceRemaining != 0 && abs(velocity) <= Rules.DECELERATION
					&& abs(distanceRemaining) <= Rules.DECELERATION) {
				slowDownVelocity = distanceRemaining;
			}

			double perfectAccel = slowDownVelocity - velocity;

			if (perfectAccel > Rules.DECELERATION) {
				perfectAccel = Rules.DECELERATION;
			} else if (perfectAccel < -Rules.DECELERATION) {
				perfectAccel = -Rules.DECELERATION;
			}

			acceleration = perfectAccel;
		}

		// Calculate velocity
		if (velocity > maxVelocity || velocity < -maxVelocity) {
			acceleration = 0;
		}

		velocity += acceleration;
		if (velocity > maxVelocity) {
			velocity -= min(Rules.DECELERATION, velocity - maxVelocity);
		}
		if (velocity < -maxVelocity) {
			velocity += min(Rules.DECELERATION, -velocity - maxVelocity);
		}

		double dx = velocity * sin(heading);
		double dy = velocity * cos(heading);

		x += dx;
		y += dy;

		boolean updateBounds = false;

		if (dx != 0 || dy != 0) {
			updateBounds = true;
		}

		if (slowingDown && velocity == 0) {
			distanceRemaining = 0;
			moveDirection = 0;
			slowingDown = false;
			acceleration = 0;
		}

		if (updateBounds) {
			updateBoundingBox();
		}

		distanceRemaining -= velocity;
	}

	private void updateRadarHeading() {
		if (radarTurnRemaining > 0) {
			if (radarTurnRemaining < Rules.RADAR_TURN_RATE_RADIANS) {
				radarHeading += radarTurnRemaining;
				radarTurnRemaining = 0;
			} else {
				radarHeading += Rules.RADAR_TURN_RATE_RADIANS;
				radarTurnRemaining -= Rules.RADAR_TURN_RATE_RADIANS;
			}
		} else if (radarTurnRemaining < 0) {
			if (radarTurnRemaining > -Rules.RADAR_TURN_RATE_RADIANS) {
				radarHeading += radarTurnRemaining;
				radarTurnRemaining = 0;
			} else {
				radarHeading -= Rules.RADAR_TURN_RATE_RADIANS;
				radarTurnRemaining += Rules.RADAR_TURN_RATE_RADIANS;
			}
		}

		radarHeading = normalAbsoluteAngle(radarHeading);
	}

	public synchronized void wakeup(Battle b) {
		if (isSleeping) {
			// Wake up the thread
			notify();
			try {
				wait(10000);
			} catch (InterruptedException e) {}
		}
	}

	public synchronized void setHalt(boolean halt) {
		this.halt = halt;
	}

	public int compareTo(ContestantPeer cp) {
		double score1 = statistics.getTotalScore();
		double score2 = cp.getStatistics().getTotalScore();

		if (battle.isRunning()) {
			score1 += statistics.getCurrentScore();
			score2 += cp.getStatistics().getCurrentScore();
		}
		return (int) (score2 + 0.5) - (int) (score1 + 0.5);
	}

	public Robot getRobot() {
		return robot;
	}

	public TeamPeer getTeamPeer() {
		return teamPeer;
	}

	public boolean isTeamLeader() {
		return (getTeamPeer() != null && getTeamPeer().getTeamLeader() == this);
	}

	public long getTime() {
		return battle.getCurrentTime();
	}

	public synchronized double getVelocity() {
		return velocity;
	}

	public void initialize(double x, double y, double heading) {
		state = STATE_ACTIVE;

		isWinner = false;
		this.x = lastX = x;
		this.y = lastY = y;

		setLastHeading();
		this.heading = gunHeading = radarHeading = lastGunHeading = lastRadarHeading = heading;

		acceleration = velocity = 0;

		if (isTeamLeader() && isDroid) {
			energy = 220;
		} else if (isTeamLeader()) {
			energy = 200;
		} else if (isDroid) {
			energy = 120;
		} else {
			energy = 100;
		}
		gunHeat = 3;

		distanceRemaining = turnRemaining = gunTurnRemaining = radarTurnRemaining = 0;

		setStop(true);
		setHalt(false);

		setScan(false);

		inCollision = false;

		scanArc.setAngleStart(0);
		scanArc.setAngleExtent(0);
		scanArc.setFrame(-100, -100, 1, 1);

		eventManager.reset();

		setMaxVelocity(999);
		setMaxTurnRate(999);

		statistics.initialize();

		out.resetCounter();

		setSetCallCount(0);
		setGetCallCount(0);
		skippedTurns = 0;

		getRobotThreadManager().resetCpuTime();

		setAdjustGunForBodyTurn(false);
		setAdjustRadarForBodyTurn(false);
		setAdjustRadarForGunTurn(false);
		isAdjustRadarForBodyTurnSet = false;

		newBullet = null;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public final void resume() {
		setResume();
		tick();
	}

	public synchronized void setMaxTurnRate(double newTurnRate) {
		if (Double.isNaN(newTurnRate)) {
			out.println("You cannot setMaxTurnRate to: " + newTurnRate);
			return;
		}
		maxTurnRate = min(toRadians(abs(newTurnRate)), Rules.MAX_TURN_RATE_RADIANS);
	}

	public synchronized void setMaxVelocity(double newVelocity) {
		if (Double.isNaN(newVelocity)) {
			out.println("You cannot setMaxVelocity to: " + newVelocity);
			return;
		}
		maxVelocity = min(abs(newVelocity), Rules.MAX_VELOCITY);
	}

	public synchronized final void setResume() {
		if (isStopped) {
			isStopped = false;
			distanceRemaining = saveDistanceToGo;
			turnRemaining = saveAngleToTurn;
			gunTurnRemaining = saveGunAngleToTurn;
			radarTurnRemaining = saveRadarAngleToTurn;
		}
	}

	public void setRobot(Robot newRobot) {
		robot = newRobot;
		if (robot != null) {
			if (robot instanceof robocode.TeamRobot) {
				messageManager = new RobotMessageManager(this);
			}
			eventManager.setRobot(newRobot);
		}
	}

	public final synchronized void setStop(boolean overwrite) {
		if (!isStopped || overwrite) {
			this.saveDistanceToGo = distanceRemaining;
			this.saveAngleToTurn = turnRemaining;
			this.saveGunAngleToTurn = gunTurnRemaining;
			this.saveRadarAngleToTurn = radarTurnRemaining;
		}
		isStopped = true;

		this.distanceRemaining = 0;
		this.turnRemaining = 0;
		this.gunTurnRemaining = 0;
		this.radarTurnRemaining = 0;
	}

	public synchronized void setVelocity(double newVelocity) {
		velocity = newVelocity;
	}

	public void setWinner(boolean newWinner) {
		isWinner = newWinner;
		if (isWinner) {
			out.println("SYSTEM: " + getName() + " wins the round.");
			eventManager.add(new WinEvent());
		}
	}

	public final void stop(boolean overwrite) {
		setStop(overwrite);
		tick();
	}

	public synchronized void waitFor(Condition condition) {
		waitCondition = condition;
		do {
			tick(); // Always tick at least once
		} while (!condition.test());

		waitCondition = null;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public synchronized double getLastGunHeading() {
		return lastGunHeading;
	}

	public synchronized double getLastRadarHeading() {
		return lastRadarHeading;
	}

	public synchronized void setScan(boolean scan) {
		this.scan = scan;
	}

	/**
	 * RobotPeer constructor
	 */
	public RobotPeer(RobotClassManager robotClassManager, long fileSystemQuota) {
		super();
		this.robotClassManager = robotClassManager;
		robotThreadManager = new RobotThreadManager(this);
		robotFileSystemManager = new RobotFileSystemManager(this, fileSystemQuota);
		eventManager = new EventManager(this);
		boundingBox = new BoundingRectangle();
		scanArc = new Arc2D.Double();
		teamPeer = robotClassManager.getTeamManager();

		// Create statistics after teamPeer set
		statistics = new RobotStatistics(this);
	}

	public synchronized Bullet setFire(double power) {
		if (Double.isNaN(power)) {
			out.println("SYSTEM: You cannot call fire(NaN)");
			return null;
		}
		if (gunHeat > 0 || energy == 0) {
			return null;
		}

		double firePower = min(energy, min(max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

		this.setEnergy(energy - firePower);

		gunHeat += Rules.getGunHeat(firePower);

		BulletPeer bullet = new BulletPeer(this, battle);

		bullet.setPower(firePower);
		bullet.setVelocity(Rules.getBulletSpeed(firePower));
		if (eventManager.isFireAssistValid()) {
			bullet.setHeading(eventManager.getFireAssistAngle());
		} else {
			bullet.setHeading(getGunHeading());
		}
		bullet.setX(x);
		bullet.setY(y);

		newBullet = bullet;

		return bullet.getBullet();
	}

	public synchronized double getDistanceRemaining() {
		return distanceRemaining;
	}

	public synchronized double getEnergy() {
		return energy;
	}

	public synchronized double getGunHeat() {
		return gunHeat;
	}

	public synchronized double getGunTurnRemaining() {
		return gunTurnRemaining;
	}

	public synchronized double getMaxVelocity() {
		return maxVelocity;
	}

	public int getNumRounds() {
		return getBattle().getNumRounds();
	}

	public synchronized RobotOutputStream getOut() {
		if (out == null) {
			if (battle != null) {
				out = new RobotOutputStream(battle.getBattleThread());
			}
		}
		return out;
	}

	public synchronized double getRadarTurnRemaining() {
		return radarTurnRemaining;
	}

	public RobotClassManager getRobotClassManager() {
		return robotClassManager;
	}

	public RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}

	public RobotThreadManager getRobotThreadManager() {
		return robotThreadManager;
	}

	public int getRoundNum() {
		return getBattle().getRoundNum();
	}

	public synchronized boolean getScan() {
		return scan;
	}

	public Arc2D getScanArc() {
		return scanArc;
	}

	public int getSkippedTurns() {
		return skippedTurns;
	}

	public RobotStatistics getRobotStatistics() {
		return statistics;
	}

	public ContestantStatistics getStatistics() {
		return statistics;
	}

	public synchronized double getTurnRemaining() {
		return turnRemaining;
	}

	public synchronized boolean isAdjustRadarForBodyTurn() {
		return isAdjustRadarForBodyTurn;
	}

	public boolean isCheckFileQuota() {
		return checkFileQuota;
	}

	public synchronized void setCall() {
		setCallCount++;
		if (setCallCount == MAX_SET_CALL_COUNT) {
			out.println("SYSTEM: You have made " + setCallCount + " calls to setXX methods without calling execute()");
			throw new DisabledException("Too many calls to setXX methods");
		}
	}

	public synchronized void getCall() {
		getCallCount++;
		if (getCallCount == MAX_GET_CALL_COUNT) {
			out.println("SYSTEM: You have made " + getCallCount + " calls to getXX methods without calling execute()");
			throw new DisabledException("Too many calls to getXX methods");
		}
	}

	public synchronized void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		isAdjustRadarForBodyTurn = newAdjustRadarForBodyTurn;
		isAdjustRadarForBodyTurnSet = true;
	}

	public void setCheckFileQuota(boolean newCheckFileQuota) {
		out.println("CheckFileQuota on");
		checkFileQuota = newCheckFileQuota;
	}

	public synchronized void setDistanceRemaining(double new_distanceRemaining) {
		distanceRemaining = new_distanceRemaining;
	}

	public synchronized void setDuplicate(int count) {
		isDuplicate = true;

		String countString = " (" + (count + 1) + ')';

		NameManager cnm = getRobotClassManager().getClassNameManager();

		name = cnm.getFullClassNameWithVersion() + countString;
		shortName = cnm.getUniqueShortClassNameWithVersion() + countString;
		nonVersionedName = cnm.getFullClassName() + countString;
	}

	public boolean isDuplicate() {
		return isDuplicate;
	}

	public synchronized void setEnergy(double newEnergy) {
		setEnergy(newEnergy, true);
	}

	public synchronized void setEnergy(double newEnergy, boolean resetInactiveTurnCount) {
		if (resetInactiveTurnCount && (energy != newEnergy)) {
			battle.resetInactiveTurnCount(energy - newEnergy);
		}
		energy = newEnergy;
		if (energy < .01) {
			energy = 0;
			distanceRemaining = 0;
			turnRemaining = 0;
		}
	}

	public synchronized void setGunHeat(double newGunHeat) {
		gunHeat = newGunHeat;
	}

	public synchronized void setInterruptible(boolean interruptable) {
		eventManager.setInterruptible(eventManager.getCurrentTopEventPriority(), interruptable);
	}

	public void setSkippedTurns(int newSkippedTurns) {
		skippedTurns = newSkippedTurns;
	}

	public void setStatistics(RobotStatistics newStatistics) {
		statistics = newStatistics;
	}

	private synchronized void updateGunHeat() {
		gunHeat -= battle.getGunCoolingRate();
		if (gunHeat < 0) {
			gunHeat = 0;
		}
	}

	public synchronized void zap(double zapAmount) {
		if (energy == 0) {
			kill();
			return;
		}
		energy -= abs(zapAmount);
		if (energy < .1) {
			energy = 0;
			distanceRemaining = 0;
			turnRemaining = 0;
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean running) {
		this.isRunning = running;
	}

	public synchronized boolean isSleeping() {
		return isSleeping;
	}

	public synchronized void setSetCallCount(int setCallCount) {
		this.setCallCount = setCallCount;
	}

	public synchronized void setGetCallCount(int getCallCount) {
		this.getCallCount = getCallCount;
	}

	public boolean intersectsLine(BoundingRectangle r, double x1, double y1, double x2, double y2) {
		int out1, out2;

		if ((out2 = r.outcode(x2, y2)) == 0) {
			return true;
		}
		while ((out1 = r.outcode(x1, y1)) != 0) {
			log("testing: " + x1 + "," + y1);
			if ((out1 & out2) != 0) {
				return false;
			}
			if ((out1 & (Rectangle2D.OUT_LEFT | Rectangle2D.OUT_RIGHT)) != 0) {
				double x = r.x;

				if ((out1 & Rectangle2D.OUT_RIGHT) != 0) {
					log("adding r.getWidth");
					x += r.getWidth();
					log("x is now: " + x);
				}
				y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
				x1 = x;
				log("x1 is now: " + x1);
			} else {
				double y = r.y;

				if ((out1 & Rectangle2D.OUT_BOTTOM) != 0) {
					log("adding r.getHeight");
					y += r.getHeight();
				}
				x1 = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
				y1 = y;
			}
		}
		return true;
	}

	public Color getBodyColor() {
		return bodyColor;
	}

	public void setBodyColor(Color color) {
		bodyColor = color;
	}

	public Color getRadarColor() {
		return radarColor;
	}

	public void setRadarColor(Color color) {
		radarColor = color;
	}

	public Color getGunColor() {
		return gunColor;
	}

	public void setGunColor(Color color) {
		gunColor = color;
	}

	public Color getBulletColor() {
		return bulletColor;
	}

	public void setBulletColor(Color color) {
		bulletColor = color;
	}

	public Color getScanColor() {
		return scanColor;
	}

	public void setScanColor(Color color) {
		scanColor = color;
	}

	public RobotMessageManager getMessageManager() {
		return messageManager;
	}

	public void setPaintEnabled(boolean enabled) {
		paintEnabled = enabled;
	}

	public boolean isPaintEnabled() {
		return paintEnabled;
	}

	public void setSGPaintEnabled(boolean enabled) {
		sgPaintEnabled = enabled;
	}

	public boolean isSGPaintEnabled() {
		return sgPaintEnabled;
	}

	public synchronized int getState() {
		return state;
	}

	public synchronized void setState(int newState) {
		state = newState;
	}

	public synchronized void set(RobotRecord rr) {
		x = rr.x;
		y = rr.y;
		energy = (double) rr.energy / 10;
		heading = Math.PI * rr.heading / 128;
		radarHeading = Math.PI * rr.radarHeading / 128;
		gunHeading = Math.PI * rr.gunHeading / 128;
		state = rr.state;
		bodyColor = toColor(rr.bodyColor);
		gunColor = toColor(rr.gunColor);
		radarColor = toColor(rr.radarColor);
		bulletColor = toColor(rr.bulletColor);
		scanColor = toColor(rr.scanColor);
	}

	private static Color toColor(short rgb565) {
		if (rgb565 == 0) {
			return null;
		}
		if (rgb565 == 0x20) {
			return Color.BLACK;
		}
		return new Color(255 * ((rgb565 & 0xF800) >> 11) / 31, 255 * ((rgb565 & 0x07e0) >> 5) / 63,
				255 * (rgb565 & 0x001f) / 31);
	}

	private synchronized void setLastHeading() {
		lastHeading = heading;
	}
}
