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
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer;


import java.awt.*;
import java.awt.geom.*;
import java.awt.Color;

import robocode.Robot;
import robocode.Condition;
import robocode.HitRobotEvent;
import robocode.DeathEvent;
import robocode.WinEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.exception.*;
import robocode.battle.Battle;
import robocode.battlefield.BattleField;
import robocode.util.*;
import robocode.peer.BulletPeer;
import robocode.peer.robot.*;
import robocode.manager.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobotPeer implements Runnable, ContestantPeer {
	public boolean printit;
	public int printcount;
	
	private double velocity;
	private double heading;
	private double radarHeading;
	private double gunHeading;
	private double x;
	private double y;

	private double acceleration;
	private double maxAcceleration = 1; // .4
	private double maxVelocity = 8;
	private double angleToTurn;
	private double radarAngleToTurn;
	private double gunAngleToTurn;
	private double turnRate;
	private double gunTurnRate = Math.toRadians(20.0);
	private double radarTurnRate = Math.toRadians(45.0);

	private double maxBraking = 2; // 0.7;

	private boolean sleeping;

	private Robot robot;
	private int width;
	private int height;

	private BattleField battleField;

	private BoundingRectangle boundingBox;
	private Arc2D.Double scanArc;
	private double scanRadius;

	private boolean adjustGunForBodyTurn;
	private boolean adjustRadarForGunTurn;
	private boolean dead;

	public RobotOutputStream out;

	private boolean running;

	public Rectangle arcRect;

	private boolean isStopped;

	private double lastGunHeading;
	private double lastHeading;
	private double lastRadarHeading;
	private double lastX;
	private double lastY;
	private double maxScanRadius = 1200;
	private double maxTurnRate = Math.toRadians(10.0);

	private double saveAngleToTurn;
	private double saveDistanceToGo;
	private double saveGunAngleToTurn;
	private double saveRadarAngleToTurn;
	private boolean scan;

	private double systemMaxTurnRate = Math.toRadians(10.0);
	private double systemMaxVelocity = 8;

	private boolean winner;

	private Battle battle;
	private double distanceRemaining;
	private EventManager eventManager;

	private Condition waitCondition;

	private boolean adjustRadarForBodyTurn;
	private boolean adjustRadarForBodyTurnSet;
	public boolean checkFileQuota;
	private double energy;
	private double gunHeat;
	private boolean halt;
	private boolean inCollision;
	private String name;
	private String nonVersionedName;
	private int setCallCount;
	private int getCallCount;
	private RobotClassManager robotClassManager;
	private RobotFileSystemManager robotFileSystemManager;
	private RobotThreadManager robotThreadManager;
	private String shortName;
	private int skippedTurns;
	private RobotStatistics statistics;
	
	private int colorIndex = -1;
	
	private int setColorRoundNum = -1;
	
	private RobotMessageManager messageManager;
	private ImageManager imageManager;
	private TeamPeer teamPeer;
	private boolean droid;
	private TextPeer sayTextPeer;
	
	private boolean duplicate;
	private int moveDirection;
	private boolean slowingDown;

	private boolean testingCondition;
	private BulletPeer newBullet;
	
	private boolean ioRobot;
	
	private final long maxSetCallCount = 10000;
	private final long maxGetCallCount = 10000;
		
	private boolean paintEnabled;

	public TextPeer getSayTextPeer() {
		return sayTextPeer;
	}

	public synchronized boolean isIORobot() {
		return ioRobot;
	}

	public synchronized void setIORobot(boolean ioRobot) {
		this.ioRobot = ioRobot;
	}	

	public synchronized void setTestingCondition(boolean testingCondition) {
		this.testingCondition = testingCondition;
	}

	public synchronized boolean getTestingCondition() {
		return testingCondition;
	}

	public boolean isDroid() {
		return droid;
	}

	public void setDroid(boolean droid) {
		this.droid = droid;
	}

	public final void move(double distance) {
		setMove(distance);
		tick();
		while (getDistanceRemaining() != 0) {
			tick();
		}
	}

	public void checkRobotCollision() {

		double ramDamage = .6;

		this.inCollision = false;
		for (int i = 0; i < battle.getRobots().size(); i++) {
			RobotPeer r = (RobotPeer) battle.getRobots().elementAt(i);

			if (r != null && r != this && !r.isDead()) {
				// If we hit
				if (getBoundingBox().intersects(r.getBoundingBox())) {
					// Bounce back
					double angle, dx, dy;

					dx = r.getX() - x;
					dy = r.getY() - y;
					angle = Math.atan2(dx, dy);
					double movedx, movedy;

					movedx = velocity * Math.sin(heading);
					movedy = velocity * Math.cos(heading);

					boolean atFault = false;
					double bearing = Utils.normalRelativeAngle(angle - heading);

					if (velocity > 0 && bearing > -Math.PI / 2 && bearing < Math.PI / 2) {
						velocity = 0;
						if (distanceRemaining > 0) {
							atFault = true;
							distanceRemaining = 0;
							getRobotStatistics().scoreRammingDamage(i, ramDamage);
						} else {
							getRobotStatistics().damagedByRamming(ramDamage);
						}
						this.setEnergy(energy - ramDamage);
						r.setEnergy(r.energy - ramDamage);
						r.getRobotStatistics().damagedByRamming(ramDamage);
						this.inCollision = true;
						x -= movedx;
						y -= movedy;

						if (r.getEnergy() == 0) {
							if (!r.isDead()) {
								r.setDead(true);
								getRobotStatistics().scoreKilledEnemyRamming(i);
							}
						}
					} else if (velocity < 0 && (bearing < -Math.PI / 2 || bearing > Math.PI / 2)) {
						velocity = 0;
						if (distanceRemaining < 0) {
							atFault = true;
							distanceRemaining = 0;
							getRobotStatistics().scoreRammingDamage(i, ramDamage);
						} else {
							getRobotStatistics().damagedByRamming(ramDamage);
						}
						this.setEnergy(energy - ramDamage);
						r.setEnergy(r.energy - ramDamage);
						r.getRobotStatistics().damagedByRamming(ramDamage);
						this.inCollision = true;
						x -= movedx;
						y -= movedy;

						if (r.getEnergy() == 0) {
							if (!r.isDead()) {
								r.setDead(true);
								getRobotStatistics().scoreKilledEnemyRamming(i);
							}
						}
					} else {// out.println("I'm moving: " + distanceRemaining + " with bearing: " + Math.toDegrees(bearing));
					}

					eventManager.add(
							new HitRobotEvent(r.getName(), Utils.normalRelativeAngle(angle - heading), r.getEnergy(), atFault));
					r.getEventManager().add(
							new HitRobotEvent(getName(), Utils.normalRelativeAngle(Math.PI + angle - r.heading), getEnergy(),
							false));

				} // if hit
			} // if robot active & not me
		} // for robots
	}

	public void checkWallCollision() {
		boolean hitWall = false;
		double fixx = 0.0, fixy = 0.0;
		double angle = 0.0;

		if (boundingBox.x + boundingBox.width > battleField.getBoundingBox().x + battleField.getBoundingBox().width) {
			hitWall = true;
			fixx = battleField.getBoundingBox().x + battleField.getBoundingBox().width - boundingBox.width
					- boundingBox.x - .001;
			angle = Utils.normalRelativeAngle(Math.PI / 2 - heading);
			eventManager.add(new HitWallEvent(angle));
		}
	
		if (boundingBox.x < battleField.getBoundingBox().x) {
			hitWall = true;
			fixx = battleField.getBoundingBox().x - boundingBox.x + .001;
			angle = Utils.normalRelativeAngle(3 * Math.PI / 2 - heading);
			eventManager.add(new HitWallEvent(angle));
		}
		if (boundingBox.y + boundingBox.height > battleField.getBoundingBox().y + battleField.getBoundingBox().height) {
			hitWall = true;
			fixy = battleField.getBoundingBox().y + battleField.getBoundingBox().height - getBoundingBox().height
					- getBoundingBox().y - .001;
			angle = Utils.normalRelativeAngle(-heading);
			eventManager.add(new HitWallEvent(angle));
		}
		if (boundingBox.y < battleField.getBoundingBox().y) {
			hitWall = true;
			fixy = battleField.getBoundingBox().y - boundingBox.y + .001;
			angle = Utils.normalRelativeAngle(Math.PI - heading);
			eventManager.add(new HitWallEvent(angle));
		}

		if (hitWall) {
			double velocity1 = 0, velocity2 = 0;

			if (Math.abs(Math.sin(heading)) > .00001 && fixx != 0) {
				velocity1 = fixx / Math.sin(heading);
			} else {
				velocity1 = 0;
			}

			if (Math.abs(Math.cos(heading)) > .00001 && fixy != 0) {
				velocity2 = fixy / Math.cos(heading);
			} else {
				velocity2 = 0;
			}

			double fixv = 0;

			if (Math.max(Math.abs(velocity1), Math.abs(velocity2)) == Math.abs(velocity1)) {
				fixv = velocity1;
			} else {
				fixv = velocity2;
			}

			double dx = fixv * Math.sin(heading);
			double dy = fixv * Math.cos(heading);
 		
			// Sanity
			if (Math.abs(dx) < Math.abs(fixx)) {
				dx = fixx;
			}
			if (Math.abs(dy) < Math.abs(fixy)) {
				dy = fixy;
			}
 	  
			// IF THIS IS NOT HERE
			// STRANGE THINGS HAPPEN UNDER IBM JAVA 1.4
			// ?????
			if (Double.isNaN(velocity1)) {
				;
			}
			if (Double.isNaN(velocity2)) {
				;
			}
		
			x += dx;
			y += dy;

			// Update energy, but do not reset inactiveTurnCount
			if (robot instanceof robocode.AdvancedRobot) {
				this.setEnergy(energy - (Math.max(Math.abs(velocity) * .5 - 1, 0)), false);
			}

			updateBoundingBox();
	  
			distanceRemaining = 0; // += Math.sqrt(dx * dx + dy * dy);
			velocity = 0;
			acceleration = 0;
		}
	}

	public final void death() {
		throw new DeathException();
	}

	public Battle getBattle() {
		return battle;
	}

	public BattleField getBattleField() {
		return battleField;
	}

	public synchronized double getBattleFieldHeight() {
		return battle.getBattleField().getHeight();
	}

	public synchronized double getBattleFieldWidth() {
		return battle.getBattleField().getWidth();
	}

	public synchronized BoundingRectangle getBoundingBox() {
		return boundingBox;
	}

	public synchronized double getGunHeading() {
		return gunHeading;
	}

	public synchronized double getHeading() {
		return heading;
	}

	public synchronized int getHeight() {
		return height;
	}

	public synchronized String getName() {
		if (name == null) {
			return robotClassManager.getClassNameManager().getFullClassNameWithVersion();
		} else {
			return name;
		}
	}

	public synchronized String getNonVersionedName() {
		if (nonVersionedName == null) {
			return robotClassManager.getClassNameManager().getFullClassName();
		} else {
			return nonVersionedName;
		}
	}

	public synchronized int getOthers() {
		if (!isDead()) {
			return battle.getActiveRobots() - 1;
		} else {
			return battle.getActiveRobots();
		}
	}

	public synchronized double getRadarHeading() {
		return radarHeading;
	}

	public synchronized int getWidth() {
		return width;
	}

	public synchronized double getX() {
		return x;
	}

	public synchronized double getY() {
		return y;
	}

	public synchronized boolean isAdjustGunForBodyTurn() {
		return adjustGunForBodyTurn;
	}

	public synchronized boolean isAdjustRadarForGunTurn() {
		return adjustRadarForGunTurn;
	}

	public synchronized boolean isDead() {
		return dead;
	}

	public void run() {
	
		setRunning(true);

		try {
			if (robot != null) {
				robot.run();
			}
			while (true) {
				tick();
			}
		} catch (DeathException e) {
			out.println("SYSTEM: " + getName() + " has died");
		} catch (WinException e) {} catch (DisabledException e) {
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
				Utils.log(getName() + " stopped successfully.");
			}
		}
		setRunning(false);
		// If battle is waiting for us, well, all done!
		synchronized (this) {
			this.notify();
		}
	}

	public boolean intersects(Arc2D arc, Rectangle2D rect) {
		if (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
				arc.getStartPoint().getY())) {
			return true;
		}		
		return arc.intersects(rect);	
	}

	public void scan() {
		if (isDroid()) {
			return;
		}
		
		double startAngle = lastRadarHeading;
		double scanRadians = radarHeading - lastRadarHeading;

		// Check if we passed through 360
		if (scanRadians < -Math.PI) {
			scanRadians = 2 * Math.PI + scanRadians;
		} else if (scanRadians > Math.PI) {
			scanRadians = scanRadians - 2 * Math.PI;
		}

		// In our coords, we are scanning clockwise, with +y up
		// In java coords, we are scanning counterclockwise, with +y down
		// All we need to do is adjust our angle by -90 for this to work.
		startAngle -= Math.PI / 2;

		scanRadius = maxScanRadius; // 1000.0; // - 2000.0 * Math.abs(scanRadians) / Math.PI; // approx

		if (scanRadians < 0) {
			startAngle += scanRadians;
			scanRadians *= -1;
		}

		startAngle = Utils.normalAbsoluteAngle(startAngle);

		scanArc.setArc(x - scanRadius, y - scanRadius, 2 * scanRadius, 2 * scanRadius, 180.0 * startAngle / Math.PI,
				180.0 * scanRadians / Math.PI, Arc2D.PIE);

		for (int i = 0; i < battle.getRobots().size(); i++) {
			RobotPeer r = (RobotPeer) battle.getRobots().elementAt(i);

			if (r != null && r != this && !r.isDead()) {
				if (intersects(scanArc, r.getBoundingBox())) {
					double dx, dy, angle, dist;

					dx = r.getX() - x;
					dy = r.getY() - y;
					angle = Math.atan2(dx, dy);
					dist = Math.sqrt(dx * dx + dy * dy);
					eventManager.add(
							new ScannedRobotEvent(r.getName(), r.energy, Utils.normalRelativeAngle(angle - heading), dist,
							r.getHeading(), r.getVelocity()));
				}
			}
		}
	}

	public synchronized void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		adjustGunForBodyTurn = newAdjustGunForBodyTurn;
	}

	public synchronized void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		adjustRadarForGunTurn = newAdjustRadarForGunTurn;
		if (!adjustRadarForBodyTurnSet) {
			adjustRadarForBodyTurn = newAdjustRadarForGunTurn;
		}
	}

	public final synchronized void setMove(double distance) {
		if (getEnergy() == 0) {
			return;
		}
		this.distanceRemaining = distance;
		this.acceleration = 0;
		if (distance == 0) {
			this.moveDirection = 0;
		} else if (distance > 0) {
			this.moveDirection = 1;
		} else {
			this.moveDirection = -1;
		}
		this.slowingDown = false;
	}

	public void setBattle(Battle newBattle) {
		battle = newBattle;
	}

	public void setBattleField(BattleField newBattleField) {
		battleField = newBattleField;
	}

	public synchronized void setDead(boolean dead) {
		if (dead) {
			battle.resetInactiveTurnCount(10.0);
			if (!this.dead) {
				eventManager.add(new DeathEvent());
				if (this.isTeamLeader()) {
					for (int i = 0; i < teamPeer.size(); i++) {
						RobotPeer teammate = teamPeer.elementAt(i);

						if (!teammate.isDead() && teammate != this) {
							teammate.setEnergy(teammate.getEnergy() - 30);
							BulletPeer sBullet = new BulletPeer(this, battle);

							sBullet.setX(teammate.getX());
							sBullet.setY(teammate.getY());
							sBullet.setVictim(teammate);
							sBullet.hitVictim = true;
							sBullet.setPower(4);
							sBullet.setActive(false);
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
			this.setEnergy(0.0);
		}
		this.dead = dead;
	}

	public synchronized void preInitialize() {
		this.dead = true;
	}

	public synchronized void setGunHeading(double newGunHeading) {
		gunHeading = newGunHeading;
	}

	public synchronized void setHeading(double heading) {
		this.heading = heading;
	}

	public synchronized void setHeight(int newHeight) {
		height = newHeight;
	}

	public synchronized void setRadarHeading(double newRadarHeading) {
		radarHeading = newRadarHeading;
	}

	public synchronized void setWidth(int newWidth) {
		width = newWidth;
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
		if (getTestingCondition()) {
			throw new RobotException(
					"You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
		}

		setSetCallCount(0);
		setGetCallCount(0);
	
		// This stops autoscan from scanning...
		if (waitCondition != null && waitCondition.test() == true) {
			waitCondition = null;
		}
		
		// If we are stopping, yet the robot took action (in onWin or onDeath), stop now.
		if (halt) {
			if (isDead()) {
				death();
			} else if (isWinner()) {
				throw new WinException();
			}
		}

		synchronized (this) {
			// Notify the battle that we are now asleep.
			// This ends any pending wait() call in battle.runRound().
			// Should not actually take place until we release the lock in wait(), below.
			this.notify();
			sleeping = true;
			// Notifying battle that we're asleep
			// Sleeping and waiting for battle to wake us up.
			try {
				this.wait(10000); // attempt to catch bug.
			} catch (InterruptedException e) {
				Utils.log("Wait interrupted");
			}
			sleeping = false;
			// Notify battle thread, which is waiting in
			// our wakeup() call, to return.
			// It's quite possible, by the way, that we'll be back in sleep (above)
			// before the battle thread actually wakes up
			this.notify();
		}

		eventManager.setFireAssistValid(false);

		if (isDead()) {
			halt();
		}
	
		eventManager.processEvents();

		out.resetCounter();
	}

	public synchronized final void setTurnGun(double radians) {
		this.gunAngleToTurn = radians;
	}

	public final void turnGun(double radians) {
		setTurnGun(radians);
		tick();
		while (getGunTurnRemaining() != 0) {
			tick();
		}
	}

	public synchronized final void setTurnChassis(double radians) {
		if (getEnergy() == 0) {
			return;
		}
		this.angleToTurn = 1.0 * radians;
	}

	public final void turnChassis(double radians) {
		setTurnChassis(radians);
		tick(); // Always tick at least once
		while (getTurnRemaining() != 0) {
			tick();
		}
	}

	public synchronized final void setTurnRadar(double radians) {
		this.radarAngleToTurn = radians;
	}

	public final void turnRadar(double radians) {
		setTurnRadar(radians);
		tick(); // Always tick at least once
		while (getRadarTurnRemaining() != 0) {
			tick();
		}
	}

	public final synchronized void update() {
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
		if (scan == false) {
			if (lastHeading != heading || lastGunHeading != gunHeading || lastRadarHeading != radarHeading || lastX != x
					|| lastY != y || waitCondition != null) {
				// if (noScan == false)
				scan = true;
			}
		}
	
	}

	public synchronized void updateBoundingBox() {
		boundingBox.setRect(x - width / 2 + 2, y - height / 2 + 2, width - 4, height - 4);
	}

	public void updateGunHeading() {
		if (gunAngleToTurn > 0) {
			if (gunAngleToTurn < gunTurnRate) {
				gunHeading += gunAngleToTurn;
				radarHeading += gunAngleToTurn;
				if (adjustRadarForGunTurn) {
					radarAngleToTurn -= gunAngleToTurn;
				}
				gunAngleToTurn = 0;
			} else {
				gunHeading += gunTurnRate;
				radarHeading += gunTurnRate;
				gunAngleToTurn -= gunTurnRate;
				if (adjustRadarForGunTurn) {
					radarAngleToTurn -= gunTurnRate;
				}
			}
		} else if (gunAngleToTurn < 0) {
			if (gunAngleToTurn > -gunTurnRate) {
				gunHeading += gunAngleToTurn;
				radarHeading += gunAngleToTurn;
				if (adjustRadarForGunTurn) {
					radarAngleToTurn -= gunAngleToTurn;
				}
				gunAngleToTurn = 0;
			} else {
				gunHeading -= gunTurnRate;
				radarHeading -= gunTurnRate;
				gunAngleToTurn += gunTurnRate;
				if (adjustRadarForGunTurn) {
					radarAngleToTurn += gunTurnRate;
				}
			}
		}
		gunHeading = Utils.normalAbsoluteAngle(gunHeading);
	}

	public void updateHeading() {
		boolean normalizeHeading = true;

		turnRate = Math.min(maxTurnRate, (.4 + .6 * (1 - (Math.abs(velocity) / systemMaxVelocity))) * systemMaxTurnRate);
	
		if (angleToTurn > 0) {
			if (angleToTurn < turnRate) {
				heading += angleToTurn;
				gunHeading += angleToTurn;
				radarHeading += angleToTurn;
				if (adjustGunForBodyTurn) {
					gunAngleToTurn -= angleToTurn;
				}
				if (adjustRadarForBodyTurn) {
					radarAngleToTurn -= angleToTurn;
				}
				angleToTurn = 0;
			} else {
				heading += turnRate;
				gunHeading += turnRate;
				radarHeading += turnRate;
				angleToTurn -= turnRate;
				if (adjustGunForBodyTurn) {
					gunAngleToTurn -= turnRate;
				}
				if (adjustRadarForBodyTurn) {
					radarAngleToTurn -= turnRate;
				}
			}
		} else if (angleToTurn < 0) {
			if (angleToTurn > -turnRate) {
				heading += angleToTurn;
				gunHeading += angleToTurn;
				radarHeading += angleToTurn;
				if (adjustGunForBodyTurn) {
					gunAngleToTurn -= angleToTurn;
				}
				if (adjustRadarForBodyTurn) {
					radarAngleToTurn -= angleToTurn;
				}
				angleToTurn = 0;
			} else {
				heading -= turnRate;
				gunHeading -= turnRate;
				radarHeading -= turnRate;
				angleToTurn += turnRate;
				if (adjustGunForBodyTurn) {
					gunAngleToTurn += turnRate;
				}
				if (adjustRadarForBodyTurn) {
					radarAngleToTurn += turnRate;
				}
			}
		} else {
			normalizeHeading = false;
		}

		if (normalizeHeading) {
			if (angleToTurn == 0) {
				heading = Utils.normalNearAbsoluteAngle(heading);
			} else {
				heading = Utils.normalAbsoluteAngle(heading);
			}
		}
		if (Double.isNaN(heading)) {
			System.out.println("HOW IS HEADING NAN HERE");
		}
	}

	public void updateMovement() {
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
			} else if (moveDirection == -1 && distanceRemaining > 1) {
				desiredDistanceRemaining = 0;
			}
		}
		double slowDownVelocity = (int) ((maxBraking / 2) * (Math.sqrt(4 * Math.abs(desiredDistanceRemaining) + 1) - 1));

		if (moveDirection == -1) {
			slowDownVelocity = -slowDownVelocity;
		}
		
		if (!slowingDown) {
			// Calculate acceleration
			if (moveDirection == 1) {
				// Brake or accelerate
				if (velocity < 0) {
					acceleration = maxBraking;
				} else {
					acceleration = maxAcceleration;
				}
				
				if (velocity + acceleration > slowDownVelocity) {
					slowingDown = true;
				}
			} else if (moveDirection == -1) {
				if (velocity > 0) {
					acceleration = -maxBraking;
				} else {
					acceleration = -maxAcceleration;
				}
			
				if (velocity + acceleration < slowDownVelocity) {
					slowingDown = true;
				}
			}
		}
	
		if (slowingDown) {
			// note:  if slowing down, velocity and distanceremaining have same sign
			if (distanceRemaining != 0 && Math.abs(velocity) <= maxBraking && Math.abs(distanceRemaining) <= maxBraking) {
				slowDownVelocity = distanceRemaining;
			}
		
			double perfectAccel = slowDownVelocity - velocity;

			if (perfectAccel > maxBraking) {
				perfectAccel = maxBraking;
			} else if (perfectAccel < -maxBraking) {
				perfectAccel = -maxBraking;
			}
		
			// log("perfect accel: " + perfectAccel);
			acceleration = perfectAccel;
		
		}

		// Calculate velocity
		if (velocity > maxVelocity || velocity < -maxVelocity) {
			acceleration = 0;
		}
		
		velocity += acceleration;
		if (velocity > maxVelocity) {
			velocity -= Math.min(maxBraking, velocity - maxVelocity);
		}
		if (velocity < -maxVelocity) {
			velocity += Math.min(maxBraking, -velocity - maxVelocity);
		}

		double dx = velocity * Math.sin(heading);
		double dy = velocity * Math.cos(heading);

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

	public void updateRadarHeading() {
		if (radarAngleToTurn > 0) {
			if (radarAngleToTurn < radarTurnRate) {
				radarHeading += radarAngleToTurn;
				radarAngleToTurn = 0;
			} else {
				radarHeading += radarTurnRate;
				radarAngleToTurn -= radarTurnRate;
			}
		} else if (radarAngleToTurn < 0) {
			if (radarAngleToTurn > -radarTurnRate) {
				radarHeading += radarAngleToTurn;
				radarAngleToTurn = 0;
			} else {
				radarHeading -= radarTurnRate;
				radarAngleToTurn += radarTurnRate;
			}
		}

		radarHeading = Utils.normalAbsoluteAngle(radarHeading);
	}

	public synchronized void wakeup(Battle b) {
		if (sleeping) {
			// Wake up the thread
			this.notify();
			try {
				this.wait(10000);
			} catch (InterruptedException e) {}
		}
	}

	public void halt() {
		halt = true;
	}

	public int compareTo(Object o) {
		if (!(o instanceof ContestantPeer)) {
			return 0;
		}
		ContestantPeer r = (ContestantPeer) o;

		if (r.getStatistics().getTotalScore() > statistics.getTotalScore()) {
			return 1;
		} else if (r.getStatistics().getTotalScore() < statistics.getTotalScore()) {
			return -1;
		} else {
			return 0;
		}
	}

	public Robot getRobot() {
		return robot;
	}

	public TeamPeer getTeamPeer() {
		return teamPeer;
	}

	public boolean isTeamLeader() {
		if (getTeamPeer() != null && getTeamPeer().getTeamLeader() == this) {
			return true;
		}
		return false;
	}

	public synchronized long getTime() {
		return battle.getCurrentTime();
	}

	public synchronized double getVelocity() {
		return velocity;
	}

	public void initialize(double x, double y, double heading) {
		setDead(false);
		setWinner(false);
		setX(x);
		setY(y);
		lastX = x;
		lastY = y;
		setHeading(heading);
		setGunHeading(heading);
		setRadarHeading(heading);
		lastHeading = heading;
		lastGunHeading = heading;
		lastRadarHeading = heading;
		acceleration = 0;
		velocity = 0;
		if (isTeamLeader() && isDroid()) {
			setEnergy(220.0);
		} else if (isTeamLeader()) {
			setEnergy(200.0);
		} else if (isDroid()) {
			setEnergy(120.0);
		} else {
			setEnergy(100.0);
		}
		gunHeat = 3;
		distanceRemaining = 0;
		angleToTurn = 0;
		gunAngleToTurn = 0;
		radarAngleToTurn = 0;
	
		isStopped = false;
		scan = false;
		scanArc.setAngleStart(0);
		scanArc.setAngleExtent(0);
		scanRadius = 0;
		scanArc.setFrame(-100, -100, 1, 1);
		eventManager.reset();
		setMaxVelocity(999);
		setMaxTurnRate(999);
		statistics.initializeRound();
		halt = false;
		out.resetCounter();
		inCollision = false;
		setCallCount = 0;
		getCallCount = 0;
		skippedTurns = 0;
		getRobotThreadManager().resetCpuTime();
	
		adjustGunForBodyTurn = false;
		adjustRadarForGunTurn = false;
		adjustRadarForBodyTurn = false;
		adjustRadarForBodyTurnSet = false;
	
		newBullet = null;
	}

	public boolean isWinner() {
		return winner;
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
		maxTurnRate = Math.min(Math.toRadians(Math.abs(newTurnRate)), systemMaxTurnRate);
	}

	public synchronized void setMaxVelocity(double newVelocity) {
		if (Double.isNaN(newVelocity)) {
			out.println("You cannot setMaxVelocity to: " + newVelocity);
			return;
		}
		maxVelocity = Math.min(Math.abs(newVelocity), systemMaxVelocity);
	}

	public synchronized final void setResume() {
		if (!isStopped) {
			return;
		}

		isStopped = false;
		distanceRemaining = saveDistanceToGo;
		angleToTurn = saveAngleToTurn;
		gunAngleToTurn = saveGunAngleToTurn;
		radarAngleToTurn = saveRadarAngleToTurn;
	}

	public void setRobot(Robot newRobot) {
		robot = newRobot;
		if (robot instanceof robocode.TeamRobot) {
			messageManager = new RobotMessageManager(this);
		}
		eventManager.setRobot(newRobot);
	}

	public final synchronized void setStop(boolean overwrite) {
		if (!isStopped || overwrite == true) {
			this.saveDistanceToGo = distanceRemaining;
			this.saveAngleToTurn = angleToTurn;
			this.saveGunAngleToTurn = gunAngleToTurn;
			this.saveRadarAngleToTurn = radarAngleToTurn;
		}
		isStopped = true;
	
		this.distanceRemaining = 0;
		this.angleToTurn = 0;
		this.gunAngleToTurn = 0;
		this.radarAngleToTurn = 0;
	}

	public synchronized void setVelocity(double newVelocity) {
		velocity = newVelocity;
	}

	public void setWinner(boolean newWinner) {
		winner = newWinner;
		if (winner) {
			out.println("SYSTEM: " + getName() + " wins the round.");
			eventManager.add(new WinEvent());
		}
	}

	public final void stop(boolean overwrite) {
		setStop(overwrite);
		tick();
	}

	public void waitFor(Condition condition) {
		waitCondition = condition;
		tick();
		while (condition.test() == false) {
			tick();
		}
		waitCondition = null;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public double getLastGunHeading() {
		return lastGunHeading;
	}

	public double getLastRadarHeading() {
		return lastRadarHeading;
	}

	public synchronized void setScan(boolean scan) {
		this.scan = scan;
	}

	/**
	 * RobotPeer constructor
	 */
	public RobotPeer(RobotClassManager robotClassManager, RobotRepositoryManager robotManager, long fileSystemQuota) {
		super();
		this.robotClassManager = robotClassManager;
		robotThreadManager = new RobotThreadManager(this);
		robotFileSystemManager = new RobotFileSystemManager(this, fileSystemQuota);
		eventManager = new EventManager(this);
		boundingBox = new BoundingRectangle();
		scanArc = new Arc2D.Double();
		imageManager = robotManager.getImageManager();
		this.teamPeer = robotClassManager.getTeamManager();

		// Create statistics after teamPeer set
		statistics = new RobotStatistics(this);
	}

	public robocode.Bullet setFire(double power) {
		if (Double.isNaN(power)) {
			out.println("SYSTEM: You cannot call fire(NaN)");
			return null;
		}
		double firePower = power;

		if (firePower < .1) {
			firePower = .1;
		}
		if (firePower > 3) {
			firePower = 3;
		}
	  
		if (gunHeat > 0 || getEnergy() == 0) {
			return null;
		}

		if (firePower > energy) {
			firePower = energy;
		}
		this.setEnergy(energy - firePower);

		gunHeat += 1 + (firePower / 5);
		BulletPeer bullet = new BulletPeer(this, battle);

		bullet.setPower(firePower);
		bullet.setVelocity(20 - 3 * firePower);
		if (eventManager.isFireAssistValid()) {
			bullet.setHeading(eventManager.getFireAssistAngle());
		} else {
			bullet.setHeading(getGunHeading());
		}
		bullet.setOwner(this);
		bullet.setX(x);
		bullet.setY(y);

		if (bullet != null) {
			newBullet = bullet;
		}	

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
		if (printit) {
			System.err.println("returning getGunTurnRemaining to");
			System.err.flush();
		}
		return gunAngleToTurn;
	}

	public synchronized double getMaxVelocity() {
		return maxVelocity;
	}

	public synchronized int getNumRounds() {
		return getBattle().getNumRounds();
	}

	public synchronized RobotOutputStream getOut() {
		if (out == null) {
			if (battle == null) {
				return null;
			}
			out = new RobotOutputStream(battle.getBattleThread());
		}
		return out;
	}

	public synchronized double getRadarTurnRemaining() {
		return radarAngleToTurn;
	}

	public RobotClassManager getRobotClassManager() {
		return robotClassManager;
	}

	public robocode.peer.robot.RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}

	public robocode.peer.robot.RobotThreadManager getRobotThreadManager() {
		return robotThreadManager;
	}

	public synchronized int getRoundNum() {
		return getBattle().getRoundNum();
	}

	public synchronized boolean getScan() {
		return this.scan;
	}

	public synchronized Arc2D.Double getScanArc() {
		return scanArc;
	}

	public double getScanRadius() {
		return scanRadius;
	}

	public String getShortName() {
		if (shortName == null) {
			return robotClassManager.getClassNameManager().getUniqueShortClassNameWithVersion();
		} else {
			return shortName;
		}
	}

	public int getSkippedTurns() {
		return skippedTurns;
	}

	public robocode.peer.robot.RobotStatistics getRobotStatistics() {
		return statistics;
	}

	public ContestantStatistics getStatistics() {
		return statistics;
	}

	public synchronized double getTurnRemaining() {
		return angleToTurn;
	}

	public String getVeryShortName() {
		if (shortName == null) {
			return robotClassManager.getClassNameManager().getUniqueVeryShortClassNameWithVersion();
		} else {
			return shortName;
		}
	}

	public synchronized boolean isAdjustRadarForBodyTurn() {
		return adjustRadarForBodyTurn;
	}

	public boolean isCheckFileQuota() {
		return checkFileQuota;
	}

	public synchronized void setCall() {
		setCallCount++;
		if (setCallCount == maxSetCallCount) {
			out.println("SYSTEM: You have made " + setCallCount + " calls to setXX methods without calling execute()");
			throw new DisabledException("Too many calls to setXX methods");
		}
	}

	public synchronized void getCall() {
		getCallCount++;
		if (getCallCount == maxGetCallCount) {
			out.println("SYSTEM: You have made " + getCallCount + " calls to getXX methods without calling execute()");
			throw new DisabledException("Too many calls to getXX methods");
		}
	}

	public synchronized void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		adjustRadarForBodyTurn = newAdjustRadarForBodyTurn;
		adjustRadarForBodyTurnSet = true;
	}

	public void setCheckFileQuota(boolean newCheckFileQuota) {
		out.println("CheckFileQuota on");
		checkFileQuota = newCheckFileQuota;
	}

	public synchronized void setDistanceRemaining(double new_distanceRemaining) {
		distanceRemaining = new_distanceRemaining;
	}

	public void setDuplicate(int count) {
		this.duplicate = true;
		this.name = getRobotClassManager().getClassNameManager().getFullClassNameWithVersion() + " (" + (count + 1)
				+ ")";
		this.shortName = getRobotClassManager().getClassNameManager().getUniqueShortClassNameWithVersion() + " ("
				+ (count + 1) + ")";
		this.nonVersionedName = getRobotClassManager().getClassNameManager().getFullClassName() + " (" + (count + 1)
				+ ")";
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setEnergy(double newEnergy) {
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
			angleToTurn = 0;
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

	public void setStatistics(robocode.peer.robot.RobotStatistics newStatistics) {
		statistics = newStatistics;
	}

	public synchronized void updateGunHeat() {
		gunHeat -= battle.getGunCoolingRate();
		if (gunHeat < 0) {
			gunHeat = 0;
		}
	}

	public synchronized void zap(double zapAmount) {
		if (energy == 0) {
			setDead(true);
			return;
		}
		energy -= Math.abs(zapAmount);
		if (energy < .1) {
			energy = 0;
			distanceRemaining = 0;
			angleToTurn = 0;
		}
	}

	public synchronized boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	public synchronized boolean isSleeping() {
		return sleeping;
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
			Utils.log("testing: " + x1 + "," + y1);
			if ((out1 & out2) != 0) {
				return false;
			}
			if ((out1 & (Rectangle2D.OUT_LEFT | Rectangle2D.OUT_RIGHT)) != 0) {
				double x = r.getX();

				if ((out1 & Rectangle2D.OUT_RIGHT) != 0) {
					Utils.log("adding r.getWidth");
					x += r.getWidth();
					Utils.log("x is now: " + x);
				}
				y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
				x1 = x;
				Utils.log("x1 is now: " + x1);
			} else {
				double y = r.getY();

				if ((out1 & Rectangle2D.OUT_BOTTOM) != 0) {
					Utils.log("adding r.getHeight");
					y += r.getHeight();
				}
				x1 = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
				y1 = y;
			}
		}
		return true;
	}

	public synchronized int getColorIndex() {
		return colorIndex;
	}

	public synchronized void setColors(Color robotColor, Color gunColor, Color radarColor) {
		if (getBattle().getManager().getProperties().getOptionsBattleAllowColorChanges() == false) {
			if (getRoundNum() == setColorRoundNum) {
				return;
			}
			if (getRoundNum() != 0) {
				return;
			}
		}

		if (colorIndex == -1) {
			colorIndex = imageManager.getNewColorsIndex(robotColor, gunColor, radarColor);
			setColorRoundNum = getRoundNum();
		} else {
			imageManager.replaceColorsIndex(colorIndex, robotColor, gunColor, radarColor);
		}
	}

	public RobotMessageManager getMessageManager() {
		return messageManager;
	}

	public boolean say(String text) {
		if (sayTextPeer == null) {
			sayTextPeer = new TextPeer();
		}
		
		if (sayTextPeer.isReady()) {
			if (text.length() > 100) {
				return false;
			}
			sayTextPeer.setText(getVeryShortName() + ": " + text);
			sayTextPeer.setX((int) getX());
			sayTextPeer.setY((int) getY());
			sayTextPeer.setDuration(20 + text.length());	
			return true;
		} else {
			return false;
		}	
	}

	public void updateSayText() {
		if (sayTextPeer != null) {
			sayTextPeer.tick();
		}
	}

	public void setPaintEnabled(boolean enabled) {
		paintEnabled = enabled;
	}

	public boolean isPaintEnabled() {
		return paintEnabled;
	}
}
