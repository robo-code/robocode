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
package robocode.peer;


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
// import robocode.battleview.RobotGraphics;

import robocode.util.*;

import robocode.peer.BulletPeer;
import robocode.peer.robot.*;

import robocode.manager.*;


/**
 * Insert the type's description here.
 * Creation date: (12/15/2000 12:04:09 PM)
 * @author: Mathew A. Nelson
 */
public class RobotPeer implements Runnable, ContestantPeer {
	public boolean printit = false;
	public int printcount = 0;
	
	private double velocity;
	private double heading;
	private double radarHeading;
	private double gunHeading;
	private double x = 0;
	private double y = 0;

	private double acceleration = 0.0;
	private double maxAcceleration = 1; // .4
	private double maxVelocity = 8;
	private double angleToTurn;
	private double radarAngleToTurn;
	private double gunAngleToTurn;
	private double turnRate;
	private double gunTurnRate = Math.toRadians(20.0);
	private double radarTurnRate = Math.toRadians(45.0);

	private double maxBraking = 2; // 0.7;

	private boolean sleeping = false;

	private Robot robot;
	private int width;
	private int height;

	private BattleField battleField;
	// public RobotGraphics robotGraphics;



	private BoundingRectangle boundingBox;
	private java.awt.geom.Arc2D.Double scanArc;
	private double scanRadius;

	private boolean adjustGunForBodyTurn = false;
	private boolean adjustRadarForGunTurn = false;
	private boolean dead = false;

	public RobotOutputStream out = null;

	public java.awt.Rectangle dirtyRect;
	private boolean running = false;

	public java.awt.Rectangle arcRect;
	private robocode.dialog.RobotButton button = null;

	private double desiredVelocity = 0.0;

	private boolean isStopped = false;
	private double lastGunHeading;
	private double lastHeading;
	private double lastRadarHeading;
	private double lastX = 0;
	private double lastY = 0;
	private double maxScanRadius = 1200;
	private double maxTurnRate = Math.toRadians(10.0);

	private double saveAngleToTurn = 0.0;
	private double saveDistanceToGo = 0.0;
	private double saveGunAngleToTurn = 0.0;
	private double saveRadarAngleToTurn = 0.0;
	private boolean scan = false;
	// private java.awt.geom.Line2D.Double scanLine;

	private double systemMaxTurnRate = Math.toRadians(10.0);
	private double systemMaxVelocity = 8;

	private boolean winner = false;

	private Battle battle;
	private double distanceRemaining = 0.0;
	private EventManager eventManager = null;

	private Condition waitCondition = null;

	private boolean adjustRadarForBodyTurn = false;
	private boolean adjustRadarForBodyTurnSet = false;
	public boolean checkFileQuota = false;
	private double energy;
	private double gunHeat = 0;
	private boolean halt = false;
	private boolean inCollision = false;
	private String name = null;
	private String nonVersionedName = null;
	private int setCallCount = 0;
	private int getCallCount = 0;
	private RobotClassManager robotClassManager = null;
	private RobotFileSystemManager robotFileSystemManager = null;
	private RobotThreadManager robotThreadManager = null;
	private String shortName = null;
	private int skippedTurns = 0;
	private RobotStatistics statistics;
	
	private int colorIndex = -1;
	
	private int setColorRoundNum = -1;
	// private Object monitor = new Object();
	
	private RobotMessageManager messageManager = null;
	private RobotRepositoryManager robotManager = null;
	private ImageManager imageManager = null;
	private TeamPeer teamPeer = null;
	private boolean droid = false;
	private TextPeer sayTextPeer = null;
	
	private boolean duplicate = false;
	private int moveDirection = 0;
	private boolean slowingDown = false;

	private boolean testingCondition = false;
	private BulletPeer newBullet = null;
	
	private boolean ioRobot = false;
	
	private final long maxSetCallCount = 10000;
	private final long maxGetCallCount = 10000;
		
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:58:32 PM)
	 */
	public final void move(double distance) {
		setMove(distance);
		tick();
		while (getDistanceRemaining() != 0) {
			tick();
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 6:52:26 PM)
	 */
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

					// Calculate the velocity that the robots are heading together
					// out.println("Hit!  angle with " + dx + "," + dy + " is: " + angle * 180.0 / Math.PI);
					/* double angleToHim =
					 Mathlatan2( 
					 double headingTowardHim = angleTowardHim - getHeading();
					 */		
					// My velocity towards him is subtracted from his velocity toward me.
					// His velocity towards me is subtracted from my velocity toward him.

				


				
					// if (distanceRemaining != 0) // will result in small miscalculation if we hit a
					// robot at the exact end of a move... oh well.
					// {
					// distanceRemaining += velocity;
					// }

					boolean atFault = false;
					double bearing = Utils.normalRelativeAngle(angle - heading);

					if (velocity > 0 && bearing > -Math.PI / 2 && bearing < Math.PI / 2) {
						// out.println("I'm moving toward him!  Hit at " + bearing * 180 / Math.PI);
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
						// out.println("I'm moving backwards toward him!  Hit at " + bearing * 180 / Math.PI);
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

					/* if (getEnergy() == 0)
					 {
					 if (!isDead())
					 {
					 setDead(true);
					 r.getStatistics().scoreKilledEnemyRamming(battle.getRobots().indexOf(this));
					 }
					 }
					 */

				} // if hit
			} // if robot active & not me
		} // for robots
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 6:52:26 PM)
	 */
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

			/* double fixv = velocity1;
			 if (Math.abs(velocity2) > Math.abs(fixv))
			 fixv = velocity2;
			 */
			// System.out.println(heading + " - sin: " + Math.sin(heading));
			// System.out.println(heading + " - cos: " + Math.cos(heading));


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
		
			// double amt;
			// amt = fixv;
			// System.out.println(heading + "," + fixx + "," + fixy + "," + fixv + "(" + velocity1 + "," + velocity2 + ")");
			// }
			x += dx;
			y += dy;
			// System.out.println(" to " + x + "," + y);

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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 4:31:19 PM)
	 */
	public final void death() {
		throw new DeathException();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:33:11 PM)
	 * @return robocode.Battle
	 */
	public Battle getBattle() {
		return battle;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 7:00:24 PM)
	 * @return robocode.BattleField
	 */
	public BattleField getBattleField() {
		return battleField;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:34:38 PM)
	 * @return double
	 */
	public synchronized double getBattleFieldHeight() {
		return battle.getBattleField().getHeight();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:34:38 PM)
	 * @return double
	 */
	public synchronized double getBattleFieldWidth() {
		return battle.getBattleField().getWidth();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/23/2000 4:14:33 PM)
	 * @return BoundingRectangle
	 */
	public synchronized BoundingRectangle getBoundingBox() {
		return boundingBox;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/22/2000 4:03:55 PM)
	 * @return double
	 */
	public synchronized double getGunHeading() {
		return gunHeading;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:20:38 PM)
	 * @return double
	 */
	public synchronized double getHeading() {
		return heading;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:18:05 PM)
	 * @return int
	 */
	public synchronized int getHeight() {
		return height;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 4:00:00 PM)
	 * @return java.lang.String
	 */
	public synchronized java.lang.String getName() {
		if (name == null) {
			return robotClassManager.getClassNameManager().getFullClassNameWithVersion();
		} else {
			return name;
		}
	}

	public synchronized java.lang.String getNonVersionedName() {
		if (nonVersionedName == null) {
			return robotClassManager.getClassNameManager().getFullClassName();
		} else {
			return nonVersionedName;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/18/2000 9:51:42 PM)
	 * @return int
	 */
	public synchronized int getOthers() {
		if (!isDead()) {
			return battle.getActiveRobots() - 1;
		} else {
			return battle.getActiveRobots();
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/22/2000 3:54:36 PM)
	 * @return double
	 */
	public synchronized double getRadarHeading() {
		return radarHeading;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:17:56 PM)
	 * @return int
	 */
	public synchronized int getWidth() {
		return width;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 4:04:02 PM)
	 * @return double
	 */
	public synchronized double getX() {
		return x;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 4:04:11 PM)
	 * @return double
	 */
	public synchronized double getY() {
		return y;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/24/2000 12:34:06 PM)
	 * @return boolean
	 */
	public synchronized boolean isAdjustGunForBodyTurn() {
		return adjustGunForBodyTurn;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/24/2000 12:34:24 PM)
	 * @return boolean
	 */
	public synchronized boolean isAdjustRadarForGunTurn() {
		return adjustRadarForGunTurn;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/29/2000 9:47:51 AM)
	 * @return boolean
	 */
	public synchronized boolean isDead() {
		return dead;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 12:34:06 PM)
	 */
	public void run() {
	
		setRunning(true);

		/*
		 synchronized(monitor)
		 {
		 sleeping = true;
		 try {
		 monitor.wait();
		 }
		 catch (InterruptedException e) {
		 log("Wait interrupted");
		 }
		 sleeping = false;
		 }
		 */


		try {
			if (robot != null) {
				// synchronized(getRobotThreadManager().getRunThread())
				// {
				robot.run();
			}
			// }
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
				log(getName() + " stopped successfully.");
			}
		}
		setRunning(false);
		// If battle is waiting for us, well, all done!
		synchronized (this) {
			this.notify();
		}

		// System.out.println("Cpu: " + getRobotThreadManager().getCpuTime() + " time: " + getTime());

		
	}

	public boolean intersects(Arc2D arc, Rectangle2D rect) {
		if (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
				arc.getStartPoint().getY())) {
			return true;
		}
		
		return arc.intersects(rect);
	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/23/2000 5:59:25 PM)
	 */
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
				180.0 * scanRadians / Math.PI, java.awt.geom.Arc2D.PIE);

		/*
		 double startangle;
		 startangle = (180 * radarHeading / Math.PI) - 90;
		 startangle -= scanDegrees / 2;
		 if (startangle < 0) startangle += 360;
		 if (startangle >= 360) startangle -= 360;
		 scanArc.setArc(x - scanRadius,y-scanRadius,2*scanRadius,2*scanRadius,startangle, scanDegrees, java.awt.geom.Arc2D.PIE);
		 */
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

				/*

				 // approx. 0.6 degrees: use line intersect then.
				 if (Math.abs(scanRadians) < .01)
				 {
				 out.println("*** Using line intersect");
				 if (r.getBoundingBox().intersectsLine(x,y,scanArc.getStartPoint().getX(),scanArc.getStartPoint().getY()))
				 {
				 //scanArc.getStartPoint().getX(),scanArc.getStartPoint().getY()))
				 //				scanLine.intersects(r.boundingBox.x,r.boundingBox.y,r.boundingBox.width,r.boundingBox.height))
				 //					if (getName().equals("sample.Walls"))
				 //						log("My intersect is: " + (int)x + "," + (int)y + "," + (int)rx + "," + (int)ry + ", to the rectangangle " + (int)r.getBoundingBox().x + "," + (int)r.getBoundingBox().y + "," + (int)r.getBoundingBox().width + "," + (int)r.getBoundingBox().height);
				 double dx, dy, angle, dist;
				 dx = r.getX() - x;
				 dy = r.getY() - y;
				 angle = Math.atan2(dx,dy);
				 dist = Math.sqrt(dx * dx + dy *dy);
				 eventManager.add(new ScannedRobotEvent(r.getName(),r.energy, Utils.normalRelativeAngle(angle - heading), dist, r.getHeading(), r.getVelocity()));
				 out.println("*** Generating scan event (line)");
				 }
				 else
				 out.println("*** " + r.getBoundingBox() + " does not intersec " + x + "," + y + "," + scanArc.getStartPoint().getX() + "," + scanArc.getStartPoint().getY());
				 }
				 //			else if (scanArc.intersects(r.boundingBox.x,r.boundingBox.y,r.boundingBox.width,r.boundingBox.height))
				 else if (intersects(scanArc,r.boundingBox.x,r.boundingBox.y,r.boundingBox.width,r.boundingBox.height))
				 //			if ((new Area(scanArc)).intersects(r.boundingBox))
				 {
				 double dx, dy, angle, dist;
				 dx = r.getX() - x;
				 dy = r.getY() - y;
				 angle = Math.atan2(dx,dy);
				 dist = Math.sqrt(dx * dx + dy *dy);
				 eventManager.add(new ScannedRobotEvent(r.getName(),r.energy, Utils.normalRelativeAngle(angle - heading), dist, r.getHeading(), r.getVelocity()));
				 out.println("*** Generating scan event (arc)");
				 }
				 else
				 if ((new Area(scanArc)).intersects(r.boundingBox))
				 out.println("***** It SHOULD HAVE HIT *****");
				 else
				 out.println("*** " + r.getBoundingBox() + " does not intersect " + scanArc.getAngleStart() + "," + scanArc.getAngleExtent() + " from " + getX() + "," + getY());
				 */			
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/24/2000 12:34:06 PM)
	 * @param newAdjustGunForBodyTurn boolean
	 */
	public synchronized void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		adjustGunForBodyTurn = newAdjustGunForBodyTurn;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/24/2000 12:34:24 PM)
	 * @param newAdjustRadarForGunTurn boolean
	 */
	public synchronized void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		adjustRadarForGunTurn = newAdjustRadarForGunTurn;
		if (!adjustRadarForBodyTurnSet) {
			adjustRadarForBodyTurn = newAdjustRadarForGunTurn;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:58:32 PM)
	 */
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
		// this.isAccelerating = true;
		// this.isBraking = false;
		// acceleration = maxAcceleration;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:33:11 PM)
	 * @param newBattle robocode.Battle
	 */
	public void setBattle(Battle newBattle) {
		battle = newBattle;

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 7:00:24 PM)
	 * @param newBattleField robocode.BattleField
	 */
	public void setBattleField(BattleField newBattleField) {
		battleField = newBattleField;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/29/2000 9:47:51 AM)
	 * @param newDead boolean
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/22/2000 4:03:55 PM)
	 * @param newGunHeading double
	 */
	public synchronized void setGunHeading(double newGunHeading) {
		gunHeading = newGunHeading;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:20:38 PM)
	 * @param newHeading double
	 */
	public synchronized void setHeading(double heading) {
		this.heading = heading;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:18:05 PM)
	 * @param newHeight int
	 */
	public synchronized void setHeight(int newHeight) {
		height = newHeight;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/22/2000 3:54:36 PM)
	 * @param newRadarHeading double
	 */
	public synchronized void setRadarHeading(double newRadarHeading) {
		radarHeading = newRadarHeading;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 3:17:56 PM)
	 * @param newWidth int
	 */
	public synchronized void setWidth(int newWidth) {
		width = newWidth;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 4:04:11 PM)
	 * @param newY double
	 */
	public synchronized void setX(double newX) {
		x = newX;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 4:04:11 PM)
	 * @param newY double
	 */
	public synchronized void setY(double newY) {
		y = newY;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:39:32 PM)
	 */
	public final void tick() {
		if (newBullet != null) {
			battle.addBullet(newBullet);
			newBullet = null;
		}

		// log("Entering tick.");
		if (Thread.currentThread() != robotThreadManager.getRunThread()) {
			throw new RobotException("You cannot take action in this thread!");
		}
		if (getTestingCondition()) {
			throw new RobotException(
					"You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
		}

		// if (getCallCount > 0)
		// System.out.println("Before tick, " + getCallCount + " calls with " + skippedTurns + " skipped turns.");
		setSetCallCount(0);
		setGetCallCount(0);
	
		// This stops autoscan from scanning...
		if (waitCondition != null && waitCondition.test() == true) {
			waitCondition = null;
		}
		
		boolean skipEventProcessing = false;

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
			// log("Notifying battle that we're asleep");
			// log("Sleeping and waiting for battle to wake us up.");
			try {
				this.wait(10000); // attempt to catch bug.
			} catch (InterruptedException e) {
				log("Wait interrupted");
			}
			sleeping = false;
			// Notify battle thread, which is waiting in
			// our wakeup() call, to return.
			// It's quite possible, by the way, that we'll be back in sleep (above)
			// before the battle thread actually wakes up
			this.notify();
		}

		/*
		 synchronized (this) {
		 this.notify();
		 }	
		 // wait for battle to wake us up...
		 synchronized(monitor)
		 {
		 sleeping = true;
		 //log("Notifying battle that we're asleep");
		 //log("Sleeping and waiting for battle to wake us up.");
		 try {
		 monitor.wait(15000);
		 } catch (InterruptedException e) {
		 log("Wait interrupted");
		 }
		 sleeping = false;
		 }
		 //log("Battle woke us up, letting wakeUp return.");
		 synchronized(monitor) {
		 //try {Thread.sleep(500);} catch (InterruptedException e) {}
		 monitor.notify();
		 }

		 */
		// log("Tick is awake.");
	
		eventManager.setFireAssistValid(false);

		if (isDead()) {
			halt();
		}
	
		eventManager.processEvents();

		out.resetCounter();

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:58:32 PM)
	 */
	public synchronized final void setTurnGun(double radians) {
		this.gunAngleToTurn = radians;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:58:32 PM)
	 */
	public final void turnGun(double radians) {
		setTurnGun(radians);
		tick();
		while (getGunTurnRemaining() != 0) {
			tick();
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:58:32 PM)
	 */
	public synchronized final void setTurnChassis(double radians) {
		if (getEnergy() == 0) {
			return;
		}
		this.angleToTurn = 1.0 * radians;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:58:32 PM)
	 */
	public final void turnChassis(double radians) {
		setTurnChassis(radians);
		tick(); // Always tick at least once
		while (getTurnRemaining() != 0) {
			tick();
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:58:32 PM)
	 */
	public synchronized final void setTurnRadar(double radians) {
		this.radarAngleToTurn = radians;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 3:58:32 PM)
	 */
	public final void turnRadar(double radians) {
		setTurnRadar(radians);
		tick(); // Always tick at least once
		while (getRadarTurnRemaining() != 0) {
			tick();
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/18/2000 5:07:28 PM)
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 6:34:48 PM)
	 */
	public void updateGunHeading() {

		// gunAngleToTurn = normalRelativeAngle(gunAngleToTurn);
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 6:34:48 PM)
	 */
	public void updateHeading() {

		boolean normalizeHeading = true;

		turnRate = Math.min(maxTurnRate, (.4 + .6 * (1 - (Math.abs(velocity) / systemMaxVelocity))) * systemMaxTurnRate);
	
		// angleToTurn = normalRelativeAngle(angleToTurn);
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 6:42:26 PM)
	 */
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
					// log("Slow down! (v+a=" + (velocity + acceleration) + " > " + slowDownVelocity);
					slowingDown = true;
				}
			} else if (moveDirection == -1) {
				if (velocity > 0) {
					acceleration = -maxBraking;
				} else {
					acceleration = -maxAcceleration;
				}
			
				if (velocity + acceleration < slowDownVelocity) {
					// log("Slow down! (v+a=" + (velocity + acceleration) + " < " + slowDownVelocity);
					slowingDown = true;
				}
			}
		}
	
		if (slowingDown) {
			// note:  if slowing down, velocity and distanceremaining have same sign
			if (distanceRemaining != 0 && Math.abs(velocity) <= maxBraking && Math.abs(distanceRemaining) <= maxBraking) {
				// log("Adjusting slowdown velocity to " + distanceRemaining);
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
		// log("vel: " + velocity);
		if (velocity > maxVelocity) {
			velocity -= Math.min(maxBraking, velocity - maxVelocity);
		}
		if (velocity < -maxVelocity) {
			velocity += Math.min(maxBraking, -velocity - maxVelocity);
		}

		// if (velocity == 0 && Math.abs(distanceRemaining) < maxBraking)
		// velocity = distanceRemaining;
		
		// log("Moving with velocity: " + velocity + " distrmn " + distanceRemaining);
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

		/*
		 if (slowingDown)
		 {
		 if (moveDirection == 1 && distanceRemaining < 0)
		 distanceRemaining = 0;
		 else if (moveDirection == -1 && distanceRemaining > 0)
		 distanceRemaining = 0;
		 }
		 */
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 6:34:48 PM)
	 */
	public void updateRadarHeading() {

		// radarAngleToTurn = normalRelativeAngle(radarAngleToTurn);
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 5:20:01 PM)
	 * @param b robocode.Battle
	 */
	public synchronized void wakeup(Battle b) {
		if (sleeping) {
			// log("Waking up " + getName());
			// Wake up the thread
			this.notify();
			try {
				this.wait(10000);
				// log("Wakeup returning.");
			} catch (InterruptedException e) {}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/18/2000 9:40:21 PM)
	 */
	public void halt() {
		halt = true;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 4:40:01 PM)
	 * @return int
	 * @param o java.lang.Object
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2001 11:10:05 AM)
	 * @param newButton robocode.dialog.RobotButton
	 */

	/* public void setButton(robocode.dialog.RobotButton newButton) {
	 button = newButton;
	 }
	 */

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2001 11:10:05 AM)
	 * @return robocode.dialog.RobotButton
	 */

	/* public robocode.dialog.RobotButton getButton() {
	 return button;
	 }
	 */

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 12:16:13 PM)
	 * @return robocode.Robot
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/22/2000 1:36:25 PM)
	 * @return robocode.battleview.RobotGraphics
	 */

	/*
	 public RobotGraphics getRobotGraphics() {
	 return robotGraphics;
	 }
	 */

	public TeamPeer getTeamPeer() {
		return teamPeer;
	}

	public boolean isTeamLeader() {
		if (getTeamPeer() != null && getTeamPeer().getTeamLeader() == this) {
			return true;
		}
		return false;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 2:24:41 PM)
	 * @return long
	 */
	public synchronized long getTime() {
		return battle.getCurrentTime();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/26/2001 12:32:13 PM)
	 * @return double
	 */
	public synchronized double getVelocity() {
		return velocity;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/29/2000 9:47:25 AM)
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 6:17:34 PM)
	 * @return boolean
	 */
	public boolean isWinner() {
		return winner;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/18/2000 5:36:46 PM)
	 */
	public final void resume() {
		setResume();

		tick();

		/* while (distanceRemaining != 0 || angleToTurn != 0 || gunAngleToTurn != 0 || radarAngleToTurn != 0)
		 {
		 tick();
		 }
		 */
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/26/2001 12:32:13 PM)
	 * @param newVelocity double
	 */
	public synchronized void setMaxTurnRate(double newTurnRate) {
		if (Double.isNaN(newTurnRate)) {
			out.println("You cannot setMaxTurnRate to: " + newTurnRate);
			return;
		}
		maxTurnRate = Math.min(Math.toRadians(Math.abs(newTurnRate)), systemMaxTurnRate);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/26/2001 12:32:13 PM)
	 * @param newVelocity double
	 */
	public synchronized void setMaxVelocity(double newVelocity) {
		if (Double.isNaN(newVelocity)) {
			out.println("You cannot setMaxVelocity to: " + newVelocity);
			return;
		}
		maxVelocity = Math.min(Math.abs(newVelocity), systemMaxVelocity);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/18/2000 5:36:46 PM)
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 12:16:13 PM)
	 * @param newRobot robocode.Robot
	 */
	public void setRobot(Robot newRobot) {
		robot = newRobot;
		if (robot instanceof robocode.TeamRobot) {
			// log(getName() + " IS a team robot.");

			messageManager = new RobotMessageManager(this);
		} else {// log(newRobot + " (" + getName() + ") is not a team robot.");
		}
		eventManager.setRobot(newRobot);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/18/2000 5:36:46 PM)
	 */
	public final synchronized void setStop(boolean overwrite) {

		if (!isStopped || overwrite == true) {
			this.saveDistanceToGo = distanceRemaining;
			this.saveAngleToTurn = angleToTurn;
			this.saveGunAngleToTurn = gunAngleToTurn;
			this.saveRadarAngleToTurn = radarAngleToTurn;
		}
		isStopped = true;
	
		this.distanceRemaining = 0;
		// this.velocity = 0;
		// this.acceleration = 0;
		this.angleToTurn = 0;
		this.gunAngleToTurn = 0;
		this.radarAngleToTurn = 0;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/26/2001 12:32:13 PM)
	 * @param newVelocity double
	 */
	public synchronized void setVelocity(double newVelocity) {
		velocity = newVelocity;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 6:17:34 PM)
	 * @param newWinner boolean
	 */
	public void setWinner(boolean newWinner) {
		winner = newWinner;
		if (winner) {
			out.println("SYSTEM: " + getName() + " wins the round.");
			eventManager.add(new WinEvent());
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/18/2000 5:36:46 PM)
	 */
	public final void stop(boolean overwrite) {
		setStop(overwrite);
		tick();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 10:24:16 AM)
	 * @param condition robocode.JCondition
	 */
	public void waitFor(Condition condition) {
		waitCondition = condition;
		tick();
		while (condition.test() == false) {
			tick();
		}
		waitCondition = null;
	}

	/**
	 * Insert the method's description here.
	 * @return EventManager
	 */
	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * Insert the method's description here.
	 * @return double
	 */
	public double getLastGunHeading() {
		return lastGunHeading;
	}

	/**
	 * Insert the method's description here.
	 * @return double
	 */
	public double getLastRadarHeading() {
		return lastRadarHeading;
	}

	/**
	 * Insert the method's description here.
	 * @param scan boolean
	 */
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
		scanArc = new java.awt.geom.Arc2D.Double();
		// scanLine = new java.awt.geom.Line2D.Double();
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

	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 7:08:49 PM)
	 * @return double
	 */
	public synchronized double getDistanceRemaining() {
		return distanceRemaining;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 3:14:44 AM)
	 * @return double
	 */
	public synchronized double getEnergy() {
		return energy;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 8:49:46 PM)
	 * @return double
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (10/9/2001 2:23:14 PM)
	 * @return double
	 */
	public synchronized double getMaxVelocity() {
		return maxVelocity;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 4:48:33 PM)
	 * @return int
	 */
	public synchronized int getNumRounds() {
		return getBattle().getNumRounds();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/12/2001 10:07:37 PM)
	 * @return RobocodeOutputStream
	 */
	public synchronized RobotOutputStream getOut() {
		if (out == null) {
			if (battle == null) {
				return null;
			}
			// throw new RuntimeException("Cannot create robot's output stream with a null battle.");
			out = new RobotOutputStream(battle.getBattleThread());
		
		}
		return out;
	}

	public synchronized double getRadarTurnRemaining() {
		return radarAngleToTurn;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 2:32:17 PM)
	 * @return java.lang.String
	 */
	public RobotClassManager getRobotClassManager() {
		return robotClassManager;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/27/2001 7:08:26 PM)
	 * @return robocode.peer.robot.RobotFileSystemManager
	 */
	public robocode.peer.robot.RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/18/2001 6:00:15 PM)
	 * @return robocode.peer.robot.RobotThreadHandler
	 */
	public robocode.peer.robot.RobotThreadManager getRobotThreadManager() {
		return robotThreadManager;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 2:24:24 PM)
	 * @return int
	 */
	public synchronized int getRoundNum() {
		return getBattle().getRoundNum();
	}

	/**
	 * Insert the method's description here.
	 * @param scan boolean
	 */
	public synchronized boolean getScan() {
		return this.scan;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 7:26:33 PM)
	 * @return java.awt.geom.Arc2D.Double
	 */
	public synchronized java.awt.geom.Arc2D.Double getScanArc() {
		return scanArc;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 7:27:09 PM)
	 * @return double
	 */
	public double getScanRadius() {
		return scanRadius;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 4:00:00 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getShortName() {
		if (shortName == null) {
			return robotClassManager.getClassNameManager().getUniqueShortClassNameWithVersion();
		} else {
			return shortName;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/3/2001 11:02:21 PM)
	 * @return int
	 */
	public int getSkippedTurns() {
		return skippedTurns;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/18/2001 3:49:45 PM)
	 * @return robocode.peer.robot.RobotStatistics
	 */
	public robocode.peer.robot.RobotStatistics getRobotStatistics() {
		return statistics;
	}

	public ContestantStatistics getStatistics() {
		return statistics;
	}

	public synchronized double getTurnRemaining() {
		return angleToTurn;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 4:00:00 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getVeryShortName() {
		if (shortName == null) {
			return robotClassManager.getClassNameManager().getUniqueVeryShortClassNameWithVersion();
		} else {
			return shortName;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/1/2001 11:03:09 PM)
	 * @return boolean
	 */
	public synchronized boolean isAdjustRadarForBodyTurn() {
		return adjustRadarForBodyTurn;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/27/2001 2:50:41 PM)
	 * @return boolean
	 */
	public boolean isCheckFileQuota() {
		return checkFileQuota;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 5:29:16 PM)
	 * @param s java.lang.String
	 */
	public void log(String s) {
		Utils.log(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 6:22:54 PM)
	 */
	public synchronized void setCall() {
		setCallCount++;
		if (setCallCount == maxSetCallCount) {
			out.println("SYSTEM: You have made " + setCallCount + " calls to setXX methods without calling execute()");
			throw new DisabledException("Too many calls to setXX methods");

			/* out.println("SYSTEM: Stopping robot due to excessive calls to setXX methods.");
			 halt = true;
			 setDead(true);
			 tick(); */
		}
	}

	public synchronized void getCall() {
		getCallCount++;
		if (getCallCount == maxGetCallCount) {
			out.println("SYSTEM: You have made " + getCallCount + " calls to getXX methods without calling execute()");
			throw new DisabledException("Too many calls to getXX methods");
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/1/2001 11:03:09 PM)
	 * @param newAdjustRadarForBodyTurn boolean
	 */
	public synchronized void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		adjustRadarForBodyTurn = newAdjustRadarForBodyTurn;
		adjustRadarForBodyTurnSet = true;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/27/2001 2:50:41 PM)
	 * @param newCheckFileQuota boolean
	 */
	public void setCheckFileQuota(boolean newCheckFileQuota) {
		out.println("CheckFileQuota on");
		checkFileQuota = newCheckFileQuota;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/5/2001 3:40:19 PM)
	 * @param new_distanceRemaining double
	 */
	public synchronized void setDistanceRemaining(double new_distanceRemaining) {
		distanceRemaining = new_distanceRemaining;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 3:14:44 AM)
	 * @param count int
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 3:14:44 AM)
	 * @param newEnergy double
	 */
	public void setEnergy(double newEnergy) {
		setEnergy(newEnergy, true);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 3:14:44 AM)
	 * @param newEnergy double
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 8:49:46 PM)
	 * @param newGunHeat double
	 */
	public synchronized void setGunHeat(double newGunHeat) {
		gunHeat = newGunHeat;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/2001 12:57:34 PM)
	 * @param interruptable boolean
	 */
	public synchronized void setInterruptible(boolean interruptable) {
		eventManager.setInterruptible(eventManager.getCurrentTopEventPriority(), interruptable);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/3/2001 11:02:21 PM)
	 * @param newSkippedTurns int
	 */
	public void setSkippedTurns(int newSkippedTurns) {
		skippedTurns = newSkippedTurns;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/18/2001 3:49:45 PM)
	 * @param newStatistics robocode.peer.robot.RobotStatistics
	 */
	public void setStatistics(robocode.peer.robot.RobotStatistics newStatistics) {
		statistics = newStatistics;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 6:34:48 PM)
	 */
	public synchronized void updateGunHeat() {

		gunHeat -= battle.getGunCoolingRate();

		if (gunHeat < 0) {
			gunHeat = 0;
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 3:20:21 AM)
	 */
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

		/* if (getEnergy() < 0)
		 setDead(true);
		 */
	}

	/**
	 * Gets the running.
	 * @return Returns a boolean
	 */
	public synchronized boolean isRunning() {
		return running;
	}

	/**
	 * Sets the running.
	 * @param running The running to set
	 */
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Gets the sleeping.
	 * @return Returns a boolean
	 */
	public synchronized boolean isSleeping() {
		return sleeping;
	}

	/**
	 * Sets the nonBlockingCallCount.
	 * @param nonBlockingCallCount The nonBlockingCallCount to set
	 */
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
				double x = r.getX();

				if ((out1 & Rectangle2D.OUT_RIGHT) != 0) {
					log("adding r.getWidth");
					x += r.getWidth();
					log("x is now: " + x);
				}
				y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
				x1 = x;
				log("x1 is now: " + x1);
			} else {
				double y = r.getY();

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

	/**
	 * Gets the colorIndex.
	 * @return Returns a int
	 */
	public synchronized int getColorIndex() {
		return colorIndex;
	}

	public synchronized void setColors(Color robotColor, Color gunColor, Color radarColor) {
		if (getBattle().getManager().getProperties().getOptionsBattleAllowColorChanges() == false) {
			if (getRoundNum() == setColorRoundNum) {
				return;
			}
			// this.robotColor != -1 || this.gunColor != -1 || this.radarColor != -1)
			// throw new RobotException("You may only call setColors once.");
			if (getRoundNum() != 0) {
				return;
			}
		}

		if (this.colorIndex == -1) {
			this.colorIndex = imageManager.getNewColorsIndex(robotColor, gunColor, radarColor);
			setColorRoundNum = getRoundNum();
		} else {
			imageManager.replaceColorsIndex(colorIndex, robotColor, gunColor, radarColor);
		}
	}

	/**
	 * Gets the messageManager.
	 * @return Returns a RobotMessageManager
	 */
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

}
