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
 *     - Added setColors(Color, Color, Color, Color, Color), setAllColors(),
 *       setBodyColor(), setGunColor(), setRadarColor(), setBulletColor(), and
 *       setScanColor()
 *     - Updated Javadoc
 *     - The finalize() is now protected instead of public
 *     - Added onKeyPressed(), onKeyReleased(), onKeyTyped() events
 *     - Added onMouseMoved(), onMouseClicked(), onMouseReleased(),
 *       onMouseEntered(), onMouseExited(), onMouseDragged(), onMouseWheelMoved()
 *       events
 *     - The uninitializedException() method does not need a method name as input
 *       parameter anymore
 *     - The PrintStream 'out' has been moved to the new _RobotBase class
 *     Matthew Reeder
 *     - Fix for HyperThreading hang issue
 *     Stefan Westen (RobocodeGL) & Flemming N. Larsen
 *     - Added onPaint() method for painting the robot
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode;


import robocode.robotinterfaces.*;
import robocode.robotinterfaces.peer.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


/**
 * The basic robot class that you will extend to create your own robots.
 *
 * <p>Please note the following standards will be used:
 * <br> heading - absolute angle in degrees with 0 facing up the screen,
 * positive clockwise. 0 <= heading < 360.
 * <br> bearing - relative angle to some object from your robot's heading,
 * positive clockwise. -180 < bearing <= 180
 * <br> All coordinates are expressed as (x,y).
 * <br> All coordinates are positive.
 * <br> The origin (0,0) is at the bottom left of the screen.
 * <br> Positive x is right.
 * <br> Positive y is up.
 *
 * @see <a target="_top" href="http://robocode.sourceforge.net">
 * robocode.sourceforge.net</a>
 * @see <a href="http://robocode.sourceforge.net/myfirstrobot/MyFirstRobot.html">
 * Building your first robot<a>
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Stefan Westen (contributor)
 * @author Pavel Savara (contributor)
 */
public class Robot extends _Robot implements IInteractiveRobot, IBasicEvents, IInteractiveEvents {

	/**
	 * Constructs a new robot.
	 */
	public Robot() {}

	/**
	 * Robot implements <code>Runnable</code> internally.
	 * This method is called by the game and should not be used by robots.
	 *
	 * @return a <code>Runnable</code> implementation.
	 *
	 * @since 1.6
	 */
	public final Runnable getRobotRunnable() {
		return this;
	}

	/**
	 * Robot is listening to basic events internally.
	 * This method is called by the game and should not be used by robots.
	 *
	 * @return listener to basic robot events.
	 *
	 * @since 1.6
	 */
	public final IBasicEvents getBasicEventListener() {
		return this;
	}

	/**
	 * Robot is listening to interactive events internally.
	 * This method is called by the game and should not be used by robots.
	 *
	 * @return listener to interactive events.
	 *
	 * @since 1.6
	 */
	public final IInteractiveEvents getInteractiveEventListener() {
		return this;
	}

	/**
	 * Immediately moves your robot ahead (forward) by distance measured in
	 * pixels.
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the remaining distance to move is 0.
	 * <p>
	 * If the robot collides with a wall, the move is complete, meaning that the
	 * robot will not move any further. If the robot collides with another
	 * robot, the move is complete if you are heading toward the other robot.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot is set to move backward
	 * instead of forward.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Move the robot 100 pixels forward
	 *   ahead(100);
	 *
	 *   // Afterwards, move the robot 50 pixels backward
	 *   ahead(-50);
	 * </pre>
	 *
	 * @param distance the distance to move ahead measured in pixels.
	 *    If this value is negative, the robot will move back instead of ahead.
	 *
	 * @see #back
	 * @see #onHitWall
	 * @see #onHitRobot
	 */
	public void ahead(double distance) {
		if (peer != null) {
			peer.move(distance);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately moves your robot backward by distance measured in pixels.
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the remaining distance to move is 0.
	 * <p>
	 * If the robot collides with a wall, the move is complete, meaning that the
	 * robot will not move any further. If the robot collides with another
	 * robot, the move is complete if you are heading toward the other robot.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot is set to move forward instead
	 * of backward.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Move the robot 100 pixels backward
	 *   back(100);
	 *
	 *   // Afterwards, move the robot 50 pixels forward
	 *   back(-50);
	 * </pre>
	 *
	 * @param distance the distance to move back measured in pixels.
	 *    If this value is negative, the robot will move ahead instead of back.
	 *
	 * @see #ahead
	 * @see #onHitWall
	 * @see #onHitRobot
	 */
	public void back(double distance) {
		if (peer != null) {
			peer.move(-distance);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Returns the height of the current battlefield measured in pixels.
	 *
	 * @return the height of the current battlefield measured in pixels.
	 */
	public double getBattleFieldHeight() {
		if (peer != null) {
			return peer.getBattleFieldHeight();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the width of the current battlefield measured in pixels.
	 *
	 * @return the width of the current battlefield measured in pixels.
	 */
	public double getBattleFieldWidth() {
		if (peer != null) {
			return peer.getBattleFieldWidth();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the direction that the robot's body is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's body is facing, in degrees.
	 */
	public double getHeading() {
		if (peer != null) {
			double rv = 180.0 * peer.getHeading() / Math.PI;

			while (rv < 0) {
				rv += 360;
			}
			while (rv >= 360) {
				rv -= 360;
			}
			return rv;
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the height of the robot measured in pixels.
	 *
	 * @return the height of the robot measured in pixels.
	 *
	 * @see #getWidth
	 */
	public double getHeight() {
		if (peer == null) {
			uninitializedException();
		}
		return robocode.peer.RobotPeer.HEIGHT;
	}

	/**
	 * Returns the width of the robot measured in pixels.
	 *
	 * @return the width of the robot measured in pixels.
	 *
	 * @see #getHeight
	 */
	public double getWidth() {
		if (peer == null) {
			uninitializedException();
		}
		return robocode.peer.RobotPeer.WIDTH;
	}

	/**
	 * Returns the robot's name
	 *
	 * @return the robot's name
	 */
	public String getName() {
		if (peer != null) {
			return peer.getName();
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns the X position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the X position of the robot
	 *
	 * @see #getY
	 */
	public double getX() {
		if (peer != null) {
			return peer.getX();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the Y position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the Y position of the robot
	 *
	 * @see #getX
	 */
	public double getY() {
		if (peer != null) {
			return peer.getY();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * The main method in every robot. You must override this to set up your
	 * robot's basic behavior.
	 * <p>
	 * Example:
	 * <pre>
	 *   // A basic robot that moves around in a square
	 *   public void run() {
	 *       while (true) {
	 *           ahead(100);
	 *           turnRight(90);
	 *       }
	 *   }
	 * </pre>
	 */
	public void run() {}

	/**
	 * Immediately turns the robot's body to the left by degrees.
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the robot's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot 180 degrees to the left
	 *   turnLeft(180);
	 *
	 *   // Afterwards, turn the robot 90 degrees to the right
	 *   turnLeft(-90);
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's body to the left
	 *    If this value is negative, the robot's body is set to turn to the right
	 */
	public void turnLeft(double degrees) {
		if (peer != null) {
			peer.turnChassis(-Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's body to the right by degrees.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot 180 degrees to the right
	 *   turnRight(180);
	 *
	 *   // Afterwards, turn the robot 90 degrees to the left
	 *   turnRight(-90);
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's body to the right
	 *    If this value is negative, the robot's body is set to turn to the left
	 */
	public void turnRight(double degrees) {
		if (peer != null) {
			peer.turnChassis(Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Do nothing this turn, meaning that the robot will skip it's turn.
	 * <p>
	 * This call executes immediately, and does not return until the turn is
	 * over.
	 */
	public void doNothing() {
		if (peer != null) {
			peer.tick();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Called by the system to 'clean up' after your robot.
	 * You may not override this method.
	 */
	@Override
	protected final void finalize() {}

	/**
	 * Immediately fires a bullet. The bullet will travel in the direction the
	 * gun is pointing.
	 * <p>
	 * The specified bullet power is an amount of energy that will be taken from
	 * the robot's energy. Hence, the more power you want to spend on the
	 * bullet, the more energy is taken from your robot.
	 * <p>
	 * The bullet will do (4 * power) damage if it hits another robot. If power
	 * is greater than 1, it will do an additional 2 * (power - 1) damage.
	 * You will get (3 * power) back if you hit the other robot. You can call
	 * {@link Rules#getBulletDamage(double)} for getting the damage that a
	 * bullet with a specific bullet power will do.
	 * <p>
	 * The specified bullet power should be between
	 * {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
	 * <p>
	 * Note that the gun cannot fire if the gun is overheated, meaning that
	 * {@link #getGunHeat()} returns a value > 0.
	 * <p>
	 * An event is generated when the bullet hits a robot, wall, or another
	 * bullet.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Fire a bullet with maximum power if the gun is ready
	 *   if (getGunHeat() == 0) {
	 *       fire(Rules.MAX_BULLET_POWER);
	 *   }
	 * </pre>
	 *
	 * @param power the amount of energy given to the bullet, and subtracted
	 *     from the robot's energy.
	 *
	 * @see #fireBullet
	 * @see #getGunHeat
	 * @see #getGunCoolingRate
	 * @see #onBulletHit
	 * @see #onBulletHitBullet
	 * @see #onBulletMissed
	 */
	public void fire(double power) {
		if (peer != null) {
			peer.setFire(power);
			peer.tick();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately fires a bullet. The bullet will travel in the direction the
	 * gun is pointing.
	 * <p>
	 * The specified bullet power is an amount of energy that will be taken from
	 * the robot's energy. Hence, the more power you want to spend on the
	 * bullet, the more energy is taken from your robot.
	 * <p>
	 * The bullet will do (4 * power) damage if it hits another robot. If power
	 * is greater than 1, it will do an additional 2 * (power - 1) damage.
	 * You will get (3 * power) back if you hit the other robot. You can call
	 * {@link Rules#getBulletDamage(double)} for getting the damage that a
	 * bullet with a specific bullet power will do.
	 * <p>
	 * The specified bullet power should be between
	 * {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
	 * <p>
	 * Note that the gun cannot fire if the gun is overheated, meaning that
	 * {@link #getGunHeat()} returns a value > 0.
	 * <p>
	 * An event is generated when the bullet hits a robot, wall, or another
	 * bullet.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Fire a bullet with maximum power if the gun is ready
	 *   if (getGunHeat() == 0) {
	 *       Bullet bullet = fireBullet(Rules.MAX_BULLET_POWER);
	 *
	 *       // Get the velocity of the bullet
	 *       double bulletVelocity = bullet.getVelocity();
	 *   }
	 * </pre>
	 *
	 * @param power the amount of energy given to the bullet, and subtracted
	 *     from the robot's energy.
	 * @return a {@link Bullet} that contains information about the bullet if it
	 *    was actually fired, which can be used for tracking the bullet after it
	 *    has been fired. If the bullet was not fired, {@code null} is returned.
	 *
	 * @see #fire
	 * @see Bullet
	 * @see #getGunHeat
	 * @see #getGunCoolingRate
	 * @see #onBulletHit
	 * @see #onBulletHitBullet
	 * @see #onBulletMissed
	 */
	public Bullet fireBullet(double power) {
		if (peer != null) {
			Bullet b = peer.setFire(power);

			peer.tick();
			return b;
		}
		uninitializedException();
		return null;
	}

	/**
	 * Returns the rate at which the gun will cool down, i.e. the amount of heat
	 * the gun heat will drop per turn.
	 * <p>
	 * The gun cooling rate is default 0.1 / turn, but can be changed by the
	 * battle setup. So don't count on the cooling rate being 0.1!
	 *
	 * @return the gun cooling rate
	 *
	 * @see #getGunHeat
	 * @see #fire
	 * @see #fireBullet
	 */
	public double getGunCoolingRate() {
		if (peer != null) {
			return peer.getGunCoolingRate();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the direction that the robot's gun is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's gun is facing, in degrees.
	 */
	public double getGunHeading() {
		if (peer != null) {
			return peer.getGunHeading() * 180.0 / Math.PI;
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the current heat of the gun. The gun cannot fire unless this is
	 * 0. (Calls to fire will succeed, but will not actually fire unless
	 * getGunHeat() == 0).
	 * <p>
	 * The amount of gun heat generated when the gun is fired is
	 * 1 + (firePower / 5). Each turn the gun heat drops by the amount returned
	 * by {@link #getGunCoolingRate()}, which is a battle setup.
	 * <p>
	 * Note that all guns are "hot" at the start of each round, where the gun
	 * heat is 3.
	 *
	 * @return the current gun heat
	 *
	 * @see #getGunCoolingRate
	 * @see #fire
	 * @see #fireBullet
	 */
	public double getGunHeat() {
		if (peer != null) {
			return peer.getGunHeat();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the number of rounds in the current battle.
	 *
	 * @return the number of rounds in the current battle
	 */
	public int getNumRounds() {
		if (peer != null) {
			return peer.getNumRounds();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns how many opponents are left in the current round.
	 *
	 * @return how many opponents are left in the current round.
	 */
	public int getOthers() {
		if (peer != null) {
			return peer.getOthers();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the direction that the robot's radar is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's radar is facing, in degrees.
	 */
	public double getRadarHeading() {
		if (peer != null) {
			return peer.getRadarHeading() * 180.0 / Math.PI;
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the number of the current round (0 to {@link #getNumRounds()} - 1)
	 * in the battle.
	 *
	 * @return the number of the current round in the battle
	 *
	 * @see #getNumRounds
	 */
	public int getRoundNum() {
		if (peer != null) {
			return peer.getRoundNum();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the game time of the current round, where the time is equal to
	 * the current turn in the round.
	 * <p>
	 * A battle consists of multiple rounds.
	 * <p>
	 * Time is reset to 0 at the beginning of every round.
	 *
	 * @return the game time/turn of the current round
	 */
	public long getTime() {
		if (peer != null) {
			return peer.getTime();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the velocity of the robot measured in pixels/turn.
	 * <p>
	 * The maximum velocity of a robot is defined by {@link Rules#MAX_VELOCITY}
	 * (8 pixels / turn).
	 *
	 * @return the velocity of the robot measured in pixels/turn
	 *
	 * @see Rules#MAX_VELOCITY
	 */
	public double getVelocity() {
		if (peer != null) {
			return peer.getVelocity();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * This method is called when one of your bullets hits another robot.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onBulletHit(BulletHitEvent event) {
	 *       out.println("I hit " + event.getName() + "!");
	 *   }
	 * </pre>
	 *
	 * @param event the bullet-hit event set by the game
	 * @see BulletHitEvent
	 * @see Event
	 */
	public void onBulletHit(BulletHitEvent event) {}

	/**
	 * This method is called when one of your bullets hits another bullet.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onBulletHitBullet(BulletHitBulletEvent event) {
	 *       out.println("I hit a bullet fired by " + event.getBullet().getName() + "!");
	 *   }
	 * </pre>
	 *
	 * @param event the bullet-hit-bullet event set by the game
	 * @see BulletHitBulletEvent
	 * @see Event
	 */
	public void onBulletHitBullet(BulletHitBulletEvent event) {}

	/**
	 * This method is called when one of your bullets misses, i.e. hits a wall.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onBulletHit(BulletMissedEvent event) {
	 *       out.println("Drat, I missed.");
	 *   }
	 * </pre>
	 *
	 * @param event the bullet-missed event set by the game
	 * @see BulletMissedEvent
	 * @see Event
	 */
	public void onBulletMissed(BulletMissedEvent event) {}

	/**
	 * This method is called if your robot dies.
	 * <p>
	 * You should override it in your robot if you want to be informed of this
	 * event. Actions will have no effect if called from this section. The
	 * intent is to allow you to perform calculations or print something out
	 * when the robot is killed.
	 *
	 * @param event the death event set by the game
	 *
	 * @see DeathEvent
	 * @see Event
	 */
	public void onDeath(DeathEvent event) {}

	/**
	 * This method is called when your robot is hit by a bullet.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onHitByBullet(HitByBulletEvent event) {
	 *       out.println(event.getRobotName() + " hit me!");
	 *   }
	 * </pre>
	 *
	 * @param event the hit-by-bullet event set by the game
	 * @see HitByBulletEvent
	 * @see Event
	 */
	public void onHitByBullet(HitByBulletEvent event) {}

	/**
	 * This method is called when your robot collides with another robot.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onHitRobot(HitRobotEvent event) {
	 *       if (event.getBearing() > -90 && event.getBearing() <= 90) {
	 *           back(100);
	 *       } else {
	 *           ahead(100);
	 *       }
	 *   }
	 *
	 *   -- or perhaps, for a more advanced robot --
	 *
	 *   public void onHitRobot(HitRobotEvent event) {
	 *       if (event.getBearing() > -90 && event.getBearing() <= 90) {
	 *           setBack(100);
	 *       } else {
	 *           setAhead(100);
	 *       }
	 *   }
	 * </pre>
	 *
	 * The angle is relative to your robot's facing. So 0 is straight ahead of
	 * you.
	 * <p>
	 * This event can be generated if another robot hits you, in which case
	 * {@link HitRobotEvent#isMyFault() event.isMyFault()} will return
	 * {@code false}. In this case, you will not be automatically stopped by the
	 * game -- but if you continue moving toward the robot you will hit it (and
	 * generate another event). If you are moving away, then you won't hit it.
	 *
	 * @param event the hit-robot event set by the game
	 * @see HitRobotEvent
	 * @see Event
	 */
	public void onHitRobot(HitRobotEvent event) {}

	/**
	 * This method is called when your robot collides with a wall.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * The wall at the top of the screen is 0 degrees, right is 90 degrees,
	 * bottom is 180 degrees, left is 270 degrees. But this event is relative to
	 * your heading, so: The bearing is such that turnRight(e.getBearing()) will
	 * point you perpendicular to the wall.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onHitWall(HitWallEvent event) {
	 *       out.println("Ouch, I hit a wall bearing " + event.getBearing() + " degrees.");
	 *   }
	 * </pre>
	 *
	 * @param event the hit-wall event set by the game
	 * @see HitWallEvent
	 * @see Event
	 */
	public void onHitWall(HitWallEvent event) {}

	/**
	 * This method is called when another robot dies.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 *
	 * @param event The robot-death event set by the game
	 * @see RobotDeathEvent
	 * @see Event
	 */
	public void onRobotDeath(RobotDeathEvent event) {}

	/**
	 * This method is called when your robot sees another robot, i.e. when the
	 * robot's radar scan "hits" another robot.
	 * You should override it in your robot if you want to be informed of this
	 * event. (Almost all robots should override this!)
	 * <p>
	 * This event is automatically called if there is a robot in range of your
	 * radar.
	 * <p>
	 * Note that the robot's radar can only see robot within the range defined
	 * by {@link Rules#equals(Object)} (1200 pixels).
	 * <p>
	 * Also not that the bearing of the scanned robot is relative to your
	 * robot's heading.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onScannedRobot(ScannedRobotEvent event) {
	 *       // Assuming radar and gun are aligned...
	 *       if (event.getDistance() < 100) {
	 *           fire(3);
	 *       } else {
	 *           fire(1);
	 *       }
	 *   }
	 * </pre>
	 *
	 * Note:
	 *  The game assists Robots in firing, as follows:
	 *  If the gun and radar are aligned (and were aligned last turn),
	 *    and the event is current,
	 *    and you call fire() before taking any other actions,
	 *    fire() will fire directly at the robot.
	 *  In essence, this means that if you can see a robot, and it doesn't move,
	 *  then fire will hit it.
	 *  <br>
	 *  AdvancedRobots will NOT be assisted in this manner, and are expected to
	 *  examine the event to determine if fire() would hit. (i.e. you are
	 *  spinning your gun around, but by the time you get the event, your gun is
	 *  5 degrees past the robot).
	 *
	 * @param event the scanned-robot event set by the game
	 *
	 * @see #scan
	 * @see ScannedRobotEvent
	 * @see Event
	 * @see #turnRadarLeft
	 * @see #turnRadarRight
	 */
	public void onScannedRobot(ScannedRobotEvent event) {}

	/**
	 * This method is called if your robot wins a battle.
	 * <p>
	 * Your robot could perform a victory dance here! :-)
	 *
	 * @param event the win event set by the game
	 * @see WinEvent
	 * @see Event
	 */
	public void onWin(WinEvent event) {}

	/**
	 * Immediately resumes the movement you stopped by stop(), if any.
	 * <p>
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * @see #stop
	 */
	public void resume() {
		if (peer != null) {
			((IStandardRobotPeer) peer).resume();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Scans for other robots. This method is called automatically by the game,
	 * as long as you are moving, turning, turning your gun, or turning your
	 * radar.
	 * <p>
	 * Scan will cause {@link #onScannedRobot(ScannedRobotEvent) onScannedRobot}
	 * to be called if you see a robot.
	 * <p>
	 * There are 2 reasons to call scan() manually:
	 * 1 - You want to scan after you stop moving.
	 * 2 - You want to interrupt the onScannedRobot event.
	 *     This is more likely. If you are in onScannedRobot, and call scan(),
	 *     and you still see a robot, then the system will interrupt your
	 *     onScannedRobot event immediately and start it from the top.
	 * <p>
	 * This call executes immediately.
	 *
	 * @see #onScannedRobot
	 * @see ScannedRobotEvent
	 */
	public void scan() {
		if (peer != null) {
			((IStandardRobotPeer) peer).scanReset();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the gun to turn independent from the robot's turn.
	 * <p>
	 * Ok, so this needs some explanation: The gun is mounted on the robot's
	 * body. So, normally, if the robot turns 90 degrees to the right, then the
	 * gun will turn with it as it is mounted on top of the robot's body. To
	 * compensate for this, you can call setAdjustGunForRobotTurn(true). When
	 * this is set, the gun will turn independent from the robot's turn, i.e.
	 * the gun will compensate for the robot's body turn.
	 * <p>
	 * Example, assuming both the robot and gun start out facing up (0 degrees):
	 * <pre>
	 *   // Set gun to turn with the robot's turn
	 *   setAdjustGunForRobotTurn(false); // This is the default
	 *   turnRight(90);
	 *   // At this point, both the robot and gun are facing right (90 degrees)
	 *   turnLeft(90);
	 *   // Both are back to 0 degrees
	 *
	 *   -- or --
	 *
	 *   // Set gun to turn independent from the robot's turn
	 *   setAdjustGunForRobotTurn(true);
	 *   turnRight(90);
	 *   // At this point, the robot is facting right (90 degrees), but the gun is still facing up.
	 *   turnLeft(90);
	 *   // Both are back to 0 degrees.
	 * </pre>
	 *
	 * Note: The gun compensating this way does count as "turning the gun".
	 * See {@link #setAdjustRadarForGunTurn(boolean)} for details.
	 *
	 * @param independent {@code true} if the gun must turn independent from the
	 *    robot's turn; {@code false} if the gun must turn with the robot's turn.
	 *
	 * @see #setAdjustRadarForGunTurn
	 */
	public void setAdjustGunForRobotTurn(boolean independent) {
		if (peer != null) {
			((IStandardRobotPeer) peer).setAdjustGunForBodyTurn(independent);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the radar to turn independent from the gun's turn.
	 * <p>
	 * Ok, so this needs some explanation: The radar is mounted on the robot's
	 * gun. So, normally, if the gun turns 90 degrees to the right, then the radar
	 * will turn with it as it is mounted on top of the gun. To compensate for
	 * this, you can call setAdjustRadarForGunTurn(true). When this is set, the
	 * radar will turn independent from the robot's turn, i.e. the radar will
	 * compensate for the gun's turn.
	 * <p>
	 * Example, assuming both the gun and radar start out facing up (0 degrees):
	 * <pre>
	 *   // Set radar to turn with the gun's turn
	 *   setAdjustRadarForGunTurn(false); // This is the default
	 *   turnGunRight(90);
	 *   // At this point, both the radar and gun are facing right (90 degrees);
	 *
	 *   -- or --
	 *
	 *   // Set radar to turn independent from the gun's turn
	 *   setAdjustRadarForGunTurn(true);
	 *   turnGunRight(90);
	 *   // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
	 * </pre>
	 * Note: Calling setAdjustRadarForGunTurn will automatically call
	 * {@link #setAdjustRadarForRobotTurn(boolean)} with the same value, unless
	 * you have already called it earlier. This behavior is primarily for
	 * backward compatibility with older Robocode robots.
	 *
	 * @param independent {@code true} if the radar must turn independent from
	 *    the gun's turn; {@code false} if the radar must turn with the gun's
	 *    turn.
	 *
	 * @see #setAdjustRadarForRobotTurn
	 * @see #setAdjustGunForRobotTurn
	 */
	public void setAdjustRadarForGunTurn(boolean independent) {
		if (peer != null) {
			((IStandardRobotPeer) peer).setAdjustRadarForGunTurn(independent);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the color of the robot's body, gun, and radar in the same time.
	 * <p>
	 * You may only call this method one time per battle. A {@code null}
	 * indicates the default (blue-ish) color.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setColors(null, Color.RED, new Color(150, 0, 150));
	 *   }
	 * </pre>
	 *
	 * @param bodyColor the new body color
	 * @param gunColor the new gun color
	 * @param radarColor the new radar color
	 *
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors
	 * @see #setBodyColor
	 * @see #setGunColor
	 * @see #setRadarColor
	 * @see #setBulletColor
	 * @see #setScanColor
	 * @see java.awt.Color
	 */
	public void setColors(Color bodyColor, Color gunColor, Color radarColor) {
		if (peer != null) {
			peer.setBodyColor(bodyColor);
			peer.setGunColor(gunColor);
			peer.setRadarColor(radarColor);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the color of the robot's body, gun, radar, bullet, and scan arc in
	 * the same time.
	 * <p>
	 * You may only call this method one time per battle. A {@code null}
	 * indicates the default (blue-ish) color for the body, gun, radar, and scan
	 * arc, but white for the bullet color.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setColors(null, Color.RED, Color.GREEN, null, new Color(150, 0, 150));
	 *   }
	 * </pre>
	 *
	 * @param bodyColor the new body color
	 * @param gunColor the new gun color
	 * @param radarColor the new radar color
	 * @param bulletColor the new bullet color
	 * @param scanArcColor the new scan arc color
	 *
	 * @see #setColors(Color, Color, Color)
	 * @see #setAllColors
	 * @see #setBodyColor
	 * @see #setGunColor
	 * @see #setRadarColor
	 * @see #setBulletColor
	 * @see #setScanColor
	 * @see java.awt.Color
	 *
	 * @since 1.1.3
	 */
	public void setColors(Color bodyColor, Color gunColor, Color radarColor, Color bulletColor, Color scanArcColor) {
		if (peer != null) {
			peer.setBodyColor(bodyColor);
			peer.setGunColor(gunColor);
			peer.setRadarColor(radarColor);
			peer.setBulletColor(bulletColor);
			peer.setScanColor(scanArcColor);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets all the robot's color to the same color in the same time, i.e. the
	 * color of the body, gun, radar, bullet, and scan arc.
	 * <p>
	 * You may only call this method one time per battle. A {@code null}
	 * indicates the default (blue-ish) color for the body, gun, radar, and scan
	 * arc, but white for the bullet color.
	 *
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setAllColors(Color.RED);
	 *   }
	 * </pre>
	 *
	 * @param color the new color for all the colors of the robot
	 *
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setBodyColor
	 * @see #setGunColor
	 * @see #setRadarColor
	 * @see #setBulletColor
	 * @see #setScanColor
	 * @see java.awt.Color
	 *
	 * @since 1.1.3
	 */
	public void setAllColors(Color color) {
		if (peer != null) {
			peer.setBodyColor(color);
			peer.setGunColor(color);
			peer.setRadarColor(color);
			peer.setBulletColor(color);
			peer.setScanColor(color);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the color of the robot's body.
	 * <p>
	 * A {@code null} indicates the default (blue-ish) color.
	 *
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setBodyColor(Color.BLACK);
	 *   }
	 * </pre>
	 *
	 * @param color the new body color
	 *
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors
	 * @see #setGunColor
	 * @see #setRadarColor
	 * @see #setBulletColor
	 * @see #setScanColor
	 * @see java.awt.Color
	 *
	 * @since 1.1.2
	 */
	public void setBodyColor(Color color) {
		if (peer != null) {
			peer.setBodyColor(color);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the color of the robot's gun.
	 * <p>
	 * A {@code null} indicates the default (blue-ish) color.
	 *
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setGunColor(Color.RED);
	 *   }
	 * </pre>
	 *
	 * @param color the new gun color
	 *
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors
	 * @see #setBodyColor
	 * @see #setRadarColor
	 * @see #setBulletColor
	 * @see #setScanColor
	 * @see java.awt.Color
	 *
	 * @since 1.1.2
	 */
	public void setGunColor(Color color) {
		if (peer != null) {
			peer.setGunColor(color);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the color of the robot's radar.
	 * <p>
	 * A {@code null} indicates the default (blue-ish) color.
	 *
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setRadarColor(Color.YELLOW);
	 *   }
	 * </pre>
	 *
	 * @param color the new radar color
	 *
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors
	 * @see #setBodyColor
	 * @see #setGunColor
	 * @see #setBulletColor
	 * @see #setScanColor
	 * @see java.awt.Color
	 *
	 * @since 1.1.2
	 */
	public void setRadarColor(Color color) {
		if (peer != null) {
			peer.setRadarColor(color);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the color of the robot's bullets.
	 * <p>
	 * A {@code null} indicates the default white color.
	 *
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setBulletColor(Color.GREEN);
	 *   }
	 * </pre>
	 *
	 * @param color the new bullet color
	 *
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors
	 * @see #setBodyColor
	 * @see #setGunColor
	 * @see #setRadarColor
	 * @see #setScanColor
	 * @see java.awt.Color
	 *
	 * @since 1.1.2
	 */
	public void setBulletColor(Color color) {
		if (peer != null) {
			peer.setBulletColor(color);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the color of the robot's scan arc.
	 * <p>
	 * A {@code null} indicates the default (blue-ish) color.
	 *
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setScanColor(Color.WHITE);
	 *   }
	 * </pre>
	 *
	 * @param color the new scan arc color
	 *
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors
	 * @see #setBodyColor
	 * @see #setGunColor
	 * @see #setRadarColor
	 * @see #setBulletColor
	 * @see java.awt.Color
	 *
	 * @since 1.1.2
	 */
	public void setScanColor(Color color) {
		if (peer != null) {
			peer.setScanColor(color);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately stops all movement, and saves it for a call to resume(). If
	 * there is already movement saved from a previous stop, this will have no
	 * effect.
	 * <p>
	 * This method is equivalent to stop(false).
	 *
	 * @see #resume
	 * @see #stop(boolean)
	 */
	public void stop() {
		stop(false);
	}

	/**
	 * Immediately stops all movement, and saves it for a call to resume(). If
	 * there is already movement saved from a previous stop, you can overwrite
	 * it by calling stop(true).
	 *
	 * @param overwrite If there is already movement saved from a previous stop, you can overwrite it by calling stop(true).
	 * @see #resume
	 * @see #stop
	 */
	public void stop(boolean overwrite) {
		if (peer != null) {
			((IStandardRobotPeer) peer).stop(overwrite);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's gun to the left by degrees.
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the gun's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's gun 180 degrees to the left
	 *   turnGunLeft(180);
	 *
	 *   // Afterwards, turn the robot's gun 90 degrees to the right
	 *   turnGunLeft(-90);
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's gun to the left
	 *    If this value is negative, the robot's gun is set to turn to the right
	 *
	 * @see #setAdjustGunForRobotTurn
	 */
	public void turnGunLeft(double degrees) {
		if (peer != null) {
			peer.turnGun(-Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's gun to the right by degrees.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the gun's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's gun 180 degrees to the right
	 *   turnGunRight(180);
	 *
	 *   // Afterwards, turn the robot's gun 90 degrees to the left
	 *   turnGunRight(-90);
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's gun to the right
	 *    If this value is negative, the robot's gun is set to turn to the left
	 *
	 * @see #setAdjustGunForRobotTurn
	 */
	public void turnGunRight(double degrees) {
		if (peer != null) {
			peer.turnGun(Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's radar to the left by degrees.
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's radar 180 degrees to the left
	 *   turnRadarLeft(180);
	 *
	 *   // Afterwards, turn the robot's radar 90 degrees to the right
	 *   turnRadarLeft(-90);
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's radar to the left
	 *    If this value is negative, the robot's radar is set to turn to the right
	 *
	 * @see #setAdjustRadarForRobotTurn
	 * @see #setAdjustRadarForGunTurn
	 */
	public void turnRadarLeft(double degrees) {
		if (peer != null) {
			((IStandardRobotPeer) peer).turnRadar(-Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's radar to the right by degrees.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's radar 180 degrees to the right
	 *   turnRadarRight(180);
	 *
	 *   // Afterwards, turn the robot's radar 90 degrees to the left
	 *   turnRadarRight(-90);
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's radar to the right
	 *    If this value is negative, the robot's radar is set to turn to the left
	 *
	 * @see #setAdjustRadarForRobotTurn
	 * @see #setAdjustRadarForGunTurn
	 */
	public void turnRadarRight(double degrees) {
		if (peer != null) {
			((IStandardRobotPeer) peer).turnRadar(Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Returns the robot's current energy.
	 *
	 * @return the robot's current energy
	 */
	public double getEnergy() {
		if (peer != null) {
			return peer.getEnergy();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Sets the radar to turn independent from the robot's turn.
	 * <p>
	 * Ok, so this needs some explanation: The radar is mounted on the gun, and
	 * the gun is mounted on the robot's body. So, normally, if the robot turns
	 * 90 degrees to the right, the gun turns, as does the radar. Hence, if the
	 * robot turns 90 degrees to the right, then the gun and radar will turn with
	 * it as the radar is mounted on top of the gun. To compensate for this, you
	 * can call setAdjustRadarForRobotTurn(true). When this is set, the radar will
	 * turn independent from the robot's turn, i.e. the radar will compensate for
	 * the robot's turn.
	 * <p>
	 * Example, assuming the robot, gun, and radar all start out facing up (0
	 * degrees):
	 * <pre>
	 *   // Set radar to turn with the robots's turn
	 *   setAdjustRadarForRobotTurn(false); // This is the default
	 *   turnRight(90);
	 *   // At this point, the body, gun, and radar are all facing right (90 degrees);
	 *
	 *   -- or --
	 *
	 *   // Set radar to turn independent from the robot's turn
	 *   setAdjustRadarForRobotTurn(true);
	 *   turnRight(90);
	 *   // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
	 * </pre>
	 *
	 * @param independent {@code true} if the radar must turn independent from
	 *    the robots's turn; {@code false} if the radar must turn with the robot's
	 *    turn.
	 *
	 * @see #setAdjustGunForRobotTurn
	 * @see #setAdjustRadarForGunTurn
	 */
	public void setAdjustRadarForRobotTurn(boolean independent) {
		if (peer != null) {
			((IStandardRobotPeer) peer).setAdjustRadarForBodyTurn(independent);
		} else {
			uninitializedException();
		}
	}

	/**
	 * This method is called every time the robot is painted. You should
	 * override this method if you want to draw items for your robot on the
	 * battle field, e.g. targets, virtual bullets etc.
	 * <p>
	 * This method is very useful for debugging your robot.
	 * <p>
	 * Note that the robot will only be painted if the "Paint" is enabled on the
	 * robot's console window; otherwise the robot will never get painted (the
	 * reason being that all robots might have graphical items that must be
	 * painted, and then you might not be able to tell what graphical items that
	 * have been painted for your robot).
	 * <p>
	 * Also note that the coordinate system for the graphical context where you
	 * paint items fits for the Robocode coordinate system where (0, 0) is at
	 * the buttom left corner of the battlefield, where X is towards right and Y
	 * is upwards.
	 *
	 * @param g the graphics context to use for painting graphical items for the
	 *    robot
	 *
	 * @since 1.1
	 */
	public void onPaint(Graphics2D g) {}

	/**
	 * This method is called when a key has been pressed.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use key events.
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(KeyEvent)
	 * @see #onKeyReleased(KeyEvent)
	 * @see #onKeyTyped(KeyEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onKeyPressed(KeyEvent e) {}

	/**
	 * This method is called when a key has been released.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use key events.
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
	 * @see #onKeyPressed(KeyEvent)
	 * @see #onKeyTyped(KeyEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onKeyReleased(KeyEvent e) {}

	/**
	 * This method is called when a key has been typed (pressed and released).
	 * <p>
	 * See the sample.Interactive robot for an example of how to use key events.
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
	 * @see #onKeyPressed(KeyEvent)
	 * @see #onKeyReleased(KeyEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onKeyTyped(KeyEvent e) {}

	/**
	 * This method is called when a mouse button has been clicked (pressed and released).
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onMouseClicked(MouseEvent e) {}

	/**
	 * This method is called when the mouse has entered the battle view.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onMouseEntered(MouseEvent e) {}

	/**
	 * This method is called when the mouse has exited the battle view.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onMouseExited(MouseEvent e) {}

	/**
	 * This method is called when a mouse button has been pressed. 
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onMousePressed(MouseEvent e) {}

	/**
	 * This method is called when a mouse button has been released. 
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onMouseReleased(MouseEvent e) {}

	/**
	 * This method is called when the mouse has been moved. 
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onMouseMoved(MouseEvent e) {}

	/**
	 * This method is called when a mouse button has been pressed and then dragged. 
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onMouseDragged(MouseEvent e) {}

	/**
	 * This method is called when the mouse wheel has been rotated. 
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 * 
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(MouseWheelEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * 
	 * @since 1.3.4
	 */
	public void onMouseWheelMoved(MouseWheelEvent e) {} 

	/**
	 * This method is called every turn to provide the current robot status as
	 * a complete snapshot of the robot's state at that specific time.
	 * <p>
	 * The main benefit of this method is that you'll automatically receive all
	 * the current data values of the robot like e.g. the x and y coordinate,
	 * heading, gun heat etc., which are grouped into the exact same time/turn.
	 * <p>
	 * This is the only way to map the robots data values to a specific time.
	 * For example, it is not possible to determine the exact time of the
	 * robot's heading by calling first calling getTime() and then getHeading()
	 * afterwards, as the time MIGHT change after between the getTime() and
	 * getHeading() call.  
	 *
	 * @param e the event containing the robot status and the time it was
	 *    provided.
	 *    
	 * @since 1.5
	 */
	public void onStatus(StatusEvent e) {}
}
