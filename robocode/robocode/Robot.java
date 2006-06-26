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
 *     Matthew Reeder
 *     - Fix for HyperThreading hang issue
 *     Stefan Westen & Flemming N. Larsen
 *     - Added onPaint() method for painting the robot
 *     Flemming N. Larsen
 *     - Fixed Javadoc documentation
 *******************************************************************************/
package robocode;


import java.awt.*;


/**
 * The basic robot class that you will extend to create your own
 * robots.
 *
 * <P>Please note the following standards will be used:
 * <BR> heading - absolute angle in degrees with 0 facing up the screen, positive clockwise.  0 <= heading < 360.
 * <BR> bearing - relative angle to some object from your robot's heading, positive clockwise.  -180 < bearing <= 180
 * <BR> All coordinates are expressed as (x,y).
 * <BR> All coordinates are positive.
 * <BR> The origin (0,0) is at the bottom left of the screen.
 * <BR> Positive x is right.
 * <BR> Positive y is up.
 *
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 * @see <a href="http://robocode.sourceforge.net/myfirstrobot/MyFirstRobot.html">Building your first robot<a>
 *
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Stefan Westen, Flemming N. Larsen (current)
 */
public class Robot extends _Robot implements Runnable {

	/**
	 * Moves your robot forward.
	 * This call executes immediately, and does not return until it is complete.
	 * If the robot collides with a wall, the move is complete.
	 * If the robot collides with another robot, the move is complete if you are heading toward the other robt.
	 *
	 * <P>Example
	 * <PRE>
	 *   ahead(50);
	 * </PRE>
	 * @param distance The distance to move forward
	 * @see robocode.Robot#onHitWall
	 * @see robocode.Robot#onHitRobot
	 */
	public void ahead(double distance) {
		if (peer != null) {
			peer.move(distance);
		} else {
			uninitializedException("ahead");
		}
	}

	/**
	 * Moves your robot backward.
	 * This call executes immediately, and does not return until it is complete.
	 * If the robot collides with a wall, the move is complete.
	 * If the robot collides with another robot, the move is complete if you are heading toward the other robt.
	 *
	 * <P>Example
	 * <PRE>
	 *   back(150);
	 * </PRE>
	 * @param distance The distance to backward
	 * @see robocode.Robot#onHitWall
	 * @see robocode.Robot#onHitRobot
	 */
	public void back(double distance) {
		if (peer != null) {
			peer.move(-distance);
		} else {
			uninitializedException("back");
		}
	}

	/**
	 * Get height of the current battlefield.
	 * 
	 * @return The height of the battlefield.
	 */
	public double getBattleFieldHeight() {
		if (peer != null) {
			peer.getCall();
			return peer.getBattleFieldHeight();
		} else {
			uninitializedException("getBattleFieldHeight");
			return 0; // never called
		}
	}

	/**
	 * Get width of the current battlefield.
	 * 
	 * @return The width of the battlefield.
	 */
	public double getBattleFieldWidth() {
		if (peer != null) {
			peer.getCall();
			return peer.getBattleFieldWidth();
		} else {
			uninitializedException("getBattleFieldWidth");
			return 0; // never called
		}
	}

	/**
	 * Returns the direction the robot is facing, in degrees.  
	 *  The value returned will be between 0 and 360.
	 * 
	 * @return the direction the robot is facing, in degrees.
	 */
	public double getHeading() {
		if (peer != null) {
			peer.getCall();
			double rv = 180.0 * peer.getHeading() / Math.PI;

			while (rv < 0) {
				rv += 360;
			}
			while (rv >= 360) {
				rv -= 360;
			}
			return rv;
		} else {
			uninitializedException("getHeading");
			return 0; // never called
		}
	}

	/**
	 * Returns the height of the robot
	 * 
	 * @return the height of the robot
	 */
	public double getHeight() {
		if (peer != null) {
			peer.getCall();
			return peer.getHeight();
		} else {
			uninitializedException("getHeight");
			return 0; // never called
		}
	}

	/**
	 * Returns the robot's name
	 * 
	 * @return the robot's name
	 */
	public String getName() {
		if (peer != null) {
			peer.getCall();
			return peer.getName();
		} else {
			uninitializedException("getName");
			return null; // never called
		}
	}

	/**
	 * Returns the width of the robot
	 * 
	 * @return the width of the robot
	 */
	public double getWidth() {
		if (peer != null) {
			peer.getCall();
			return peer.getWidth();
		} else {
			uninitializedException("getWidth");
			return 0; // never called
		}
	}

	/**
	 * Returns the X position of the robot.  (0,0) is at the bottom left of the battlefield.
	 * 
	 * @return the X position of the robot
	 */
	public double getX() {
		if (peer != null) {
			peer.getCall();
			return peer.getX();
		} else {
			uninitializedException("getX");
			return 0; // never called
		}
	}

	/**
	 * Returns the Y position of the robot.  (0,0) is at the bottom left of the battlefield.
	 * 
	 * @return the Y position of the robot
	 */
	public double getY() {
		if (peer != null) {
			peer.getCall();
			return peer.getY();
		} else {
			uninitializedException("getY");
			return 0; // never called
		}
	}

	/**
	 * The main method in every robot.  Override this to set up your robot's basic behavior.
	 *
	 * <P>Example
	 * <PRE>
	 * // A basic robot that moves around in a square
	 * public void run() {
	 *   while (true) {
	 *      ahead(100);
	 *      turnRight(90);
	 *   }
	 * </PRE>
	 */
	public void run() {}

	/**
	 * Rotates your robot.
	 * This call executes immediately, and does not return until it is complete.
	 * Note that the gun and radar will rotate the same amount, as they are attached to the robot.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnLeft(90);
	 * </PRE>
	 * @param degrees How many degrees to rotate left.
	 */
	public void turnLeft(double degrees) {
		if (peer != null) {
			peer.turnChassis(-Math.toRadians(degrees));
		} else {
			uninitializedException("turnLeft");
		}
	}

	/**
	 * Rotates your robot.
	 * This call executes immediately, and does not return until it is complete.
	 * Note that the gun and radar will rotate the same amount, as they are attached to the robot.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnRight(90);
	 * </PRE>
	 * @param degrees How many degrees to rotate right.
	 */
	public void turnRight(double degrees) {
		if (peer != null) {
			peer.turnChassis(Math.toRadians(degrees));
		} else {
			uninitializedException("turnRight");
		}
	}

	/**
	 * The output stream your robot should use to print.  You can view it by clicking the buttons on the right side of the battle.
	 *
	 * <P>Example
	 * public void onHitRobot(HitRobotEvent e) {
	 *    out.println("I hit a robot!  My energy: " + getEnergy() + " his energy: " + e.getEnergy());
	 * }
	 * 
	 * System.out will also print to this.
	 */
	public java.io.PrintStream out = null;

	public Robot() {}            

	/**
	 * Do nothing this turn.
	 * This call executes immediately.
	 */
	public void doNothing() {
		if (peer != null) {
			peer.tick();
		} else {
			uninitializedException("doNothing");
		}
	}

	/**
	 * Called by the system to 'clean up' after your robot.
	 * You may not override this method.
	 */
	public final void finalize() {}

	/**
	 * Fires a bullet.  The valid range for power is .1 to 3.
	 * The bullet will travel in the direction the gun is pointing.
	 * The bullet will do (4 * power) damage if it hits another robot.
	 * If power is greater than 1, it will do an additional 2 * (power - 1) damage.
	 * You will get (3 * power) back if you hit the other robot.
	 *
	 * An event will be generated when the bullet hits a robot, wall, or other bullet.
	 *
	 * This call executes immediately.
	 *
	 * @param power The energy given to the bullet, and subtracted from your energy.
	 * @see robocode.Robot#fireBullet
	 * @see robocode.Robot#onBulletHit
	 * @see robocode.Robot#onBulletHitBullet
	 * @see robocode.Robot#onBulletMissed
	 */
	public void fire(double power) {
		if (peer != null) {
			peer.setFire(power);
			peer.tick();
		} else {
			uninitializedException("fire");
		}
	}

	/**
	 * Fires a bullet.  This call is exactly like fire(double),
	 * but returns the Bullet object you fired.
	 * This call executes immediately.
	 *
	 * @see #fire
	 */
	public Bullet fireBullet(double power) {
		if (peer != null) {
			Bullet b = peer.setFire(power);

			peer.tick();
			return b;
		} else {
			uninitializedException("fireBullet");
			return null;
		}
	}

	/**
	 * Returns the rate at which the gun will cool down.
	 * 
	 * @see #getGunHeat
	 * @return the gun cooling rate
	 */
	public double getGunCoolingRate() {
		if (peer != null) {
			peer.getCall();
			return peer.getBattle().getGunCoolingRate();
		} else {
			uninitializedException("getGunCoolingRate");
			return 0; // never called
		}
	}

	/**
	 * Returns gun heading in degrees.  This is a value from 0 to 360, where 0 points to the top of the screen.
	 * 
	 * @return gun heading
	 */
	public double getGunHeading() {
		if (peer != null) {
			peer.getCall();
			return peer.getGunHeading() * 180.0 / Math.PI;
		} else {
			uninitializedException("getGunHeading");
			return 0; // never called
		}
	}

	/**
	 * Returns the current heat of the gun.  You cannot fire unless this is 0.
	 * (Calls to fire will succeed, but will not actually fire unless getGunHeat() == 0
	 * 
	 * @return the current gun heat
	 */
	public double getGunHeat() {
		if (peer != null) {
			peer.getCall();
			return peer.getGunHeat();
		} else {
			uninitializedException("getGunHeat");
			return 0; // never called
		}
	}

	/**
	 * Returns the number of rounds in the current battle
	 * 
	 * @return the number of rounds in the current battle
	 */
	public int getNumRounds() {
		if (peer != null) {
			peer.getCall();
			return peer.getNumRounds();
		} else {
			uninitializedException("getNumRounds");
			return 0; // never called
		}
	}

	/**
	 * Returns how many opponents are left 
	 * 
	 * @return how many opponents are left
	 */
	public int getOthers() {
		if (peer != null) {
			peer.getCall();
			// peer.getOthers() is synchronized, and it calls a synchronized method in Battle, so in
			// order to return successfully, we need to be able to get a lock on both peer and the
			// Battle.  Since the battle has synchronized methods which call synchronized methods in
			// RobotPeer, the Battle thread locks these two objects in the opposite order often, so
			// in order to avoid deadlock, we need to make sure the battle is free before calling this
			// method.
			synchronized (peer.getBattle()) {
				return peer.getOthers();
			}
		} else {
			uninitializedException("getOthers");
			return 0; // never called
		}
	}

	/**
	 * Returns radar heading in degrees.  This is a value from 0 to 360, where 0 points to the top of the screen.
	 * 
	 * @return radar heading
	 */
	public double getRadarHeading() {
		if (peer != null) {
			peer.getCall();
			return peer.getRadarHeading() * 180.0 / Math.PI;
		} else {
			uninitializedException("getRadarHeading");
			return 0; // never called
		}
	}

	/**
	 * Returns the number of the current round (0 to getNumRounds()-1) in the battle
	 * 
	 * @return the number of the current round in the battle
	 */
	public int getRoundNum() {
		if (peer != null) {
			peer.getCall();
			return peer.getRoundNum();
		} else {
			uninitializedException("getRoundNum");
			return 0; // never called
		}
	}

	/**
	 * Returns the current game time
	 * Note:  1 battle consists of multiple rounds
	 * Time is reset to 0 at the beginning of every round.
	 * getTime() is equivalent to the number of frames displayed this round.
	 * 
	 * @return the current game time
	 */
	public long getTime() {
		if (peer != null) {
			peer.getCall();
			return peer.getTime();
		} else {
			uninitializedException("getTime");
			return 0; // never called
		}
	}

	/**
	 * Returns the velocity of the robot.
	 * 
	 * @return the velocity of the robot
	 */
	public double getVelocity() {
		if (peer != null) {
			peer.getCall();
			return peer.getVelocity();
		} else {
			uninitializedException("getVelocity");
			return 0; // never called
		}
	}

	/**
	 * This method will be called when one of your bullets hits another robot.
	 * You should override it in your robot if you want to be informed of this event.
	 *
	 * <P>Example
	 * <PRE>
	 *   public void onBulletHit(BulletHitEvent event) {
	 *     out.println("I hit " + event.getName() + "!");
	 *   }
	 * </PRE>
	 *   
	 * @param event The event set by the game
	 * @see robocode.BulletHitEvent
	 * @see robocode.Event
	 */
	public void onBulletHit(BulletHitEvent event) {}

	/**
	 * This method will be called when one of your bullets hits another bullet.
	 * You should override it in your robot if you want to be informed of this event.
	 *
	 * <P>Example
	 * <PRE>
	 *   public void onBulletHitBullet(BulletHitBulletEvent event) {
	 *     out.println("I hit a bullet fired by " + event.getBullet().getName() + "!");
	 *   }
	 * </PRE>
	 *   
	 * @param event The event set by the game
	 * @see robocode.BulletHitBulletEvent
	 * @see robocode.Event
	 */
	public void onBulletHitBullet(BulletHitBulletEvent event) {}

	/**
	 * This method will be called when one of your bullets misses (hits a wall).
	 * You should override it in your robot if you want to be informed of this event.
	 *
	 * <P>Example
	 * <PRE>
	 *   public void onBulletHit(BulletMissedEvent event) {
	 *     out.println("Drat, I missed.");
	 *   }
	 * </PRE>
	 *   
	 * @param event The event set by the game
	 * @see robocode.BulletMissedEvent
	 * @see robocode.Event
	 */
	public void onBulletMissed(BulletMissedEvent event) {}

	/**
	 * This method will be called if your robot dies
	 * You should override it in your robot if you want to be informed of this event.
	 * Actions will have no effect if called from this section.
	 * The intent is to allow you to perform calculations or print something out when you lose.
	 *
	 * @param event The event set by the game
	 * @see robocode.DeathEvent
	 * @see robocode.Event
	 */
	public void onDeath(DeathEvent event) {}

	/**
	 * This method will be called when your robot is hit by a bullet.
	 * You should override it in your robot if you want to be informed of this event.
	 *
	 * <P>Example
	 * <PRE>
	 *   public void onHitByBullet(HitByBulletEvent event) {
	 *     out.println(event.getRobotName() + " hit me!");
	 *   }
	 * </PRE>
	 *   
	 * @param event The event set by the game
	 * @see robocode.HitByBulletEvent
	 * @see robocode.Event
	 */
	public void onHitByBullet(HitByBulletEvent event) {}

	/**
	 * This method will be called when your robot collides with another robot.
	 * You should override it in your robot if you want to be informed of this event.
	 *
	 * <P>Example
	 * <PRE>
	 *   public void onHitRobot(HitRobotEvent event) {
	 *     if (event.getBearing() > -90 && event.getBearing() <= 90)
	 *       back(100);
	 *     else
	 *       ahead(100);
	 *   }
	 *
	 *   -- or perhaps, for a more advanced robot --
	 *
	 *   public void onHitRobot(HitRobotEvent event) {
	 *     if (event.getBearing() > -90 && event.getBearing() <= 90)
	 *       setBack(100);
	 *     else
	 *       setAhead(100);
	 *   }
	 * </PRE>
	 *
	 * <P>
	 * The angle is relative to your robot's facing... so 0 is straight ahead of you.
	 * <P>
	 * This event can be generated if another robot hits you, in which case event.isMyFault() will return false.
	 * In this case, you will not be automatically stopped by the game -- but if you continue moving toward the robot you
	 * will hit it (and generate another event).  If you are moving away, then you won't hit it.
	 *
	 * @param event The event set by the game
	 * @see robocode.HitRobotEvent
	 * @see robocode.Event
	 */
	public void onHitRobot(HitRobotEvent event) {}

	/**
	 * This method will be called when your robot collides with a wall.
	 * You should override it in your robot if you want to be informed of this event.
	 * Note:  The wall at the top of the screen is 0 degrees, right is 90 degrees, bottom is 180 degrees, left is 270 degrees.
	 *  -- but this event is relative to your heading, so:
	 * The bearing is such that turnRight(e.getBearing()) will point you perpendicular to the wall.
	 *
	 * <P>Example
	 * <PRE>
	 *   public void onHitWall(HitWallEvent event) {
	 *     out.println("Ouch, I hit a wall bearing " + event.getBearing() + " degrees.");
	 *   }
	 * </PRE>
	 *
	 * @param event The event set by the game
	 * @see robocode.HitWallEvent
	 * @see robocode.Event
	 */
	public void onHitWall(HitWallEvent event) {}

	/**
	 * This method will be called if another robot dies
	 * You should override it in your robot if you want to be informed of this event.
	 *
	 * @param event The event set by the game
	 * @see robocode.RobotDeathEvent
	 * @see robocode.Event
	 */
	public void onRobotDeath(RobotDeathEvent event) {}

	/**
	 * This method will be called when your robot sees another robot.
	 * You should override it in your robot if you want to be informed of this event.
	 *  (Almost all robots should override this!)
	 * This event will be called automatically if there is a robot in range of your radar.
	 * 
	 * <P>The bearing is relative to your robot's heading.
	 *
	 * <P>Example
	 * <PRE>
	 *   public void onScannedRobot(ScannedRobotEvent event) {
	 *   	// Assuming radar and gun are aligned...
	 *   	if (event.getDistance() < 100)
	 *   		fire(3);
	 *   	else
	 *   		fire(1);
	 *   }
	 * </PRE>
	 *
	 * Note:
	 *  The game assists Robots in firing, as follows:
	 *  If the gun and radar are aligned (and were aligned last turn),
	 *    and the event is current,
	 *    and you call fire() before taking any other actions,
	 *    fire() will fire directly at the robot.
	 *  In essence, this means that if you can see a robot, and it doesn't move, then fire will hit it.
	 *
	 *  AdvancedRobots will NOT be assisted in this manner, and are expected to examine the event
	 *  to determine if fire() would hit.  (i.e. you are spinning your gun around, but by the time
	 *  you get the event, your gun is 5 degrees past the robot)
	 *
	 * @param event The event set by the game
	 * @see robocode.ScannedRobotEvent
	 * @see robocode.Event
	 */
	public void onScannedRobot(ScannedRobotEvent event) {}

	/**
	 * This method will be called if your robot wins a battle.
	 * You can do a victory dance here.
	 *
	 * @param event The event set by the game
	 * @see robocode.WinEvent
	 * @see robocode.Event
	 */
	public void onWin(WinEvent event) {}

	/**
	 * Resume the movement you stopped in stop(), if any.
	 * This call executes immediately, and does not return until it is complete.
	 * @see #stop
	 */
	public void resume() {
		if (peer != null) {
			peer.resume();
		} else {
			uninitializedException("resume");
		}
	}

	/**
	 * Look for other robots.
	 * This method is called automatically by the game,
	 * as long as you are moving, turning, turning your gun, or turning your radar.
	 *
	 * There are 2 reasons to call scan() manually:
	 * 1 - You want to scan after you stop moving
	 * 2 - You want to interrupt the onScannedRobot event.
	 *     This is more likely.  If you are in onScannedRobot, and call scan(), and you still see a robot,
	 *     then the system will interrupt your onScannedRobot event immediately and start it from the top.
	 * This call executes immediately.
	 * 
	 * Scan will cause {@link #onScannedRobot} to be called if you see a robot.
	 * @see #onScannedRobot
	 * @see robocode.ScannedRobotEvent
	 */
	public void scan() {
		if (peer != null) {
			boolean reset = false;
			boolean resetValue = false;

			if (peer.getEventManager().getCurrentTopEventPriority()
					== peer.getEventManager().getScannedRobotEventPriority()) {
				reset = true;
				resetValue = peer.getEventManager().getInterruptible(
						peer.getEventManager().getScannedRobotEventPriority());
				peer.getEventManager().setInterruptible(peer.getEventManager().getScannedRobotEventPriority(), true);
			}

			peer.setScan(true);
			peer.tick();
			if (reset) {
				peer.getEventManager().setInterruptible(peer.getEventManager().getScannedRobotEventPriority(),
						resetValue);
			}
		} else {
			uninitializedException("scan");
		}
	}

	/**
	 * Sets the gun to automatically turn the opposite way when the robot turns.  
	 *
	 * Ok, so this needs some explanation:
	 *  The gun is mounted on the robot.  So, normally, if the robot turns 90 degrees to the right,
	 *  then the gun will turn with it.
	 *
	 *  <P>To compensate for this, you can call setAdjustGunForRobotTurn(true).  When this is set,
	 *  the gun will automatically turn in the opposite direction, so that it "stays still" when
	 *  the robot turns.
	 *
	 * <P>Example, assuming both the robot and gun start out facing up (0 degrees):
	 * <PRE>
	 *   setAdjustGunForRobotTurn(false); // This is the default
	 *   turnRight(90);
	 *   // At this point, both the robot and gun are facing right (90 degrees);
	 *   turnLeft(90);
	 *   // Both are back to 0 degrees
	 *
	 *   -- or --
	 *
	 *   setAdjustGunForRobotTurn(true);
	 *   turnRight(90);
	 *   // At this point, the robot is facting right (90 degrees), but the gun is still facing up.
	 *   turnLeft(90);
	 *   // Both are back to 0 degrees.
	 *  </PRE>
	 *
	 * <P>Note:  The gun compensating this way does count as "turning the gun".  See {@link #setAdjustRadarForGunTurn} for details.
	 *
	 * @param newAdjustGunForRobotTurn <CODE>true</CODE> if the gun must move independent of the robot's turn;
	 *        <CODE>false</code> if the gun must move dependent/relative to of the robot's turn
	 * @see #setAdjustRadarForGunTurn
	 */
	public void setAdjustGunForRobotTurn(boolean newAdjustGunForRobotTurn) {
		if (peer != null) {
			peer.setCall();
			peer.setAdjustGunForBodyTurn(newAdjustGunForRobotTurn);
		} else {
			uninitializedException("setAdjustGunForRobotTurn");
		}
	}

	/**
	 * Sets the radar to automatically turn the opposite way when the gun turns.  
	 *
	 * Make sure you understand how {@link #setAdjustGunForRobotTurn} works before reading on...
	 *
	 * <P>Ok, so now you understand {@link #setAdjustGunForRobotTurn} right?
	 * 
	 *  <P>Just like the gun is mounted on the robot, the radar is mounted on the gun.
	 *  So, normally, if the gun turns 90 degrees to the right,
	 *  then the radar will turn with it.
	 *
	 *  <P>To compensate for this (if you like), you can call setAdjustRadarForGunTurn(true).  When this is set,
	 *  the radar will automatically turn in the opposite direction, so that it "stays still" when
	 *  the gun turns (in relation to the body, as of 0.97).
	 *
	 * <P>Example, assuming both the radar and gun start out facing up (0 degrees):
	 * <PRE>
	 *   setAdjustRadarForGunTurn(false); // This is the default
	 *   turnGunRight(90);
	 *   // At this point, both the radar and gun are facing right (90 degrees);
	 *
	 *   -- or --
	 *
	 *   setAdjustRadarForGunTurn(true);
	 *   turnGunRight(90);
	 *   // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
	 *
	 * </PRE>
	 * <P>Note: Calling setAdjustRadarForGunTurn will automatically call setAdjustRadarForRobotTurn
	 * with the same value, unless you have already called it yourself.  This
	 * behavior is primarily for backward compatibility with older Robocode robots.
	 *
	 * @param newAdjustRadarForGunTurn <CODE>true</CODE> if the radar must move independent of the gun's turn;
	 *        <CODE>false</code> if the radar must move dependent/relative to of the gun's turn
	 * @see #setAdjustRadarForRobotTurn
	 * @see #setAdjustGunForRobotTurn
	 */
	public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		if (peer != null) {
			peer.setCall();
			peer.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
		} else {
			uninitializedException("setAdjustRadarForGunTurn");
		}
	}

	/**
	 * Call this method to set your robot's colors.
	 * You may only call this method one time per battle.
	 * A null indicates the default (blue-ish) color.
	 * 
	 * <PRE>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 * 
	 *   public void run() {
	 *     setColors(Color.black,Color.red,new Color(150,0,150));
	 *   }
	 * </PRE>
	 * 
	 * @param robotColor Your robot's color
	 * @param gunColor Your robot's gun color
	 * @param radarColor Your robot's radar color
	 * @see java.awt.Color
	 */
	public void setColors(Color robotColor, Color gunColor, Color radarColor) {
		if (peer != null) {
			peer.setCall();
			peer.setColors(robotColor, gunColor, radarColor);
		} else {
			uninitializedException("setColors");
		}
	}

	/**
	 * Stops all movement, and saves it for a call to resume().
	 * If there is already movement saved from a previous stop, this will have no effect.
	 * This method is equivalent to stop(false);
	 * This call executes immediately.
	 * @see #stop(boolean)
	 * @see #resume
	 */
	public void stop() {
		stop(false);
	}

	/**
	 * Stops all movement, and saves it for a call to resume().
	 * If there is already movement saved from a previous stop, you can overwrite it
	 * by calling stop(true).  
	 * This call executes immediately.
	 * @see #resume
	 * @see #stop
	 */
	public void stop(boolean overwrite) {
		if (peer != null) {
			peer.stop(overwrite);
		} else {
			uninitializedException("stop");
		}
	}

	/**
	 * Rotates your robot's gun.
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnGunLeft(90);
	 * </PRE>
	 * @param degrees How many degrees to rotate the gun left.
	 */
	public void turnGunLeft(double degrees) {
		if (peer != null) {
			peer.turnGun(-Math.toRadians(degrees));
		} else {
			uninitializedException("turnGunLeft");
		}
	}

	/**
	 * Rotates your robot's gun.
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnGunRight(90);
	 * </PRE>
	 * @param degrees How many degrees to rotate the gun right.
	 */
	public void turnGunRight(double degrees) {
		if (peer != null) {
			peer.turnGun(Math.toRadians(degrees));
		} else {
			uninitializedException("turnGunRight");
		}
	}

	/**
	 * Rotates your robot's radar.
	 * This call executes immediately, and does not return until it is complete.
	 * Many robots will use the turnGun functions instead... the radar will rotate when the gun rotates, and
	 * you will probably want your gun facing in the same direction anyway.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnRadarLeft(90);
	 * </PRE>
	 * @param degrees How many degrees to rotate the radar left.
	 */
	public void turnRadarLeft(double degrees) {
		if (peer != null) {
			peer.turnRadar(-Math.toRadians(degrees));
		} else {
			uninitializedException("turnRadarLeft");
		}
	}

	/**
	 * Rotates your robot's radar.
	 * This call executes immediately, and does not return until it is complete.
	 * Many robots will use the turnGun functions instead... the radar will rotate when the gun rotates, and
	 * you will probably want your gun facing in the same direction anyway.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnRadarRight(90);
	 * </PRE>
	 * @param degrees How many degrees to rotate the radar right.
	 */
	public void turnRadarRight(double degrees) {
		if (peer != null) {
			peer.turnRadar(Math.toRadians(degrees));
		} else {
			uninitializedException("turnRadarRight");
		}
	}

	/**
	 * Returns the robot's current energy
	 * 
	 * @return the robot's energy
	 */
	public double getEnergy() {
		if (peer != null) {
			peer.getCall();
			return peer.getEnergy();
		} else {
			uninitializedException("getEnergy");
			return 0; // never called
		}
	}

	/**
	 * Sets the radar to automatically turn the opposite way when the robot turns.  
	 *
	 *  <P>The radar is mounted on the gun, which is mounted on the robot.
	 *  So, normally, if the robot turns 90 degrees to the right, the gun turns, as does the radar.0
	 *
	 *  <P>To compensate for this (if you like), you can call setAdjustRadarForRobotTurn(true).  When this is set,
	 *  the radar will automatically turn in the opposite direction, so that it "stays still" when
	 *  the body turns.
	 *
	 * <P>Example, assuming the robot, gun, and radar all start out facing up (0 degrees):
	 * <PRE>
	 *   setAdjustRadarForRobotTurn(false); // This is the default
	 *   turnRight(90);
	 *   // At this point, all three are facing right (90 degrees);
	 *
	 *   -- or --
	 *
	 *   setAdjustRadarForRobotTurn(true);
	 *   turnRight(90);
	 *   // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
	 *
	 * </PRE>
	 *
	 * @param newAdjustRadarForRobotTurn <CODE>true</CODE> if the radar must move independent of the robots's turn;
	 *        <CODE>false</code> if the radar must move dependent/relative to of the robots's turn
	 * @see #setAdjustGunForRobotTurn
	 * @see #setAdjustRadarForGunTurn
	 */
	public void setAdjustRadarForRobotTurn(boolean newAdjustRadarForRobotTurn) {
		if (peer != null) {
			peer.setCall();
			peer.setAdjustRadarForBodyTurn(newAdjustRadarForRobotTurn);
		} else {
			uninitializedException("setAdjustRadarForRobotTurn");
		}
	}

	/**
	 * This method is called every time the robot is painted if the robot
	 * painting feature is enabled on your robot. You should override this
	 * method if you want to draw items on the battle field. This method is
	 * very useful when debugging your robot.
	 * 
	 * @since Robocode v1.1
	 * 
	 * @param g The graphics context to use for painting
	 */
	public void onPaint(Graphics2D g) {}
}
