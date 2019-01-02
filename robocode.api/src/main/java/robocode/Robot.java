/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import robocode.robotinterfaces.*;
import robocode.robotinterfaces.peer.IStandardRobotPeer;

import java.awt.*;


/**
 * The basic robot class that you will extend to create your own robots.
 * <p>
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
 * @see <a target="_top" href="https://robocode.sourceforge.io">
 *      robocode.sourceforge.net</a>
 * @see <a href="https://robocode.sourceforge.io/myfirstrobot/MyFirstRobot.html">
 *      Building your first robot<a>
 *
 * @see JuniorRobot
 * @see AdvancedRobot
 * @see TeamRobot
 * @see Droid
 * @see RateControlRobot
 * @see BorderSentry
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Stefan Westen (contributor)
 * @author Pavel Savara (contributor)
 */
public class Robot extends _Robot implements IInteractiveRobot, IPaintRobot, IBasicEvents3, IInteractiveEvents, IPaintEvents {

	private static final int
			WIDTH = 36,
			HEIGHT = 36;

	/**
	 * Constructs a new robot.
	 */
	public Robot() {}

	/**
	 * {@inheritDoc}}
	 */
	public final Runnable getRobotRunnable() {
		return this;
	}

	/**
	 * {@inheritDoc}}
	 */
	public final IBasicEvents getBasicEventListener() {
		return this;
	}

	/**
	 * {@inheritDoc}}
	 */
	public final IInteractiveEvents getInteractiveEventListener() {
		return this;
	}

	/**
	 * {@inheritDoc}}
	 */
	public final IPaintEvents getPaintEventListener() {
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
	 *                 If this value is negative, the robot will move back instead of ahead.
	 * @see #back(double)
	 * @see #onHitWall(HitWallEvent)
	 * @see #onHitRobot(HitRobotEvent)
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
	 *                 If this value is negative, the robot will move ahead instead of back.
	 * @see #ahead(double)
	 * @see #onHitWall(HitWallEvent)
	 * @see #onHitRobot(HitRobotEvent)
	 */
	public void back(double distance) {
		if (peer != null) {
			peer.move(-distance);
		} else {
			uninitializedException();
		}
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
	 * Returns the direction that the robot's body is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's body is facing, in degrees.
	 * @see #getGunHeading()
	 * @see #getRadarHeading()
	 */
	public double getHeading() {
		if (peer != null) {
			double rv = 180.0 * peer.getBodyHeading() / Math.PI;

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
	 * @see #getWidth()
	 */
	public double getHeight() {
		if (peer == null) {
			uninitializedException();
		}
		return HEIGHT;
	}

	/**
	 * Returns the width of the robot measured in pixels.
	 *
	 * @return the width of the robot measured in pixels.
	 * @see #getHeight()
	 */
	public double getWidth() {
		if (peer == null) {
			uninitializedException();
		}
		return WIDTH;
	}

	/**
	 * Returns the robot's name.
	 *
	 * @return the robot's name.
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
	 * @return the X position of the robot.
	 * @see #getY()
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
	 * @return the Y position of the robot.
	 * @see #getX()
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
	 * @param degrees the amount of degrees to turn the robot's body to the left.
	 *                If {@code degrees} > 0 the robot will turn left.
	 *                If {@code degrees} < 0 the robot will turn right.
	 *                If {@code degrees} = 0 the robot will not turn, but execute.
	 * @see #turnRight(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunRight(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarRight(double)
	 */
	public void turnLeft(double degrees) {
		if (peer != null) {
			peer.turnBody(-Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's body to the right by degrees.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the robot's turn is 0.
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
	 * @param degrees the amount of degrees to turn the robot's body to the right.
	 *                If {@code degrees} > 0 the robot will turn right.
	 *                If {@code degrees} < 0 the robot will turn left.
	 *                If {@code degrees} = 0 the robot will not turn, but execute.
	 * @see #turnLeft(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunRight(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarRight(double)
	 */
	public void turnRight(double degrees) {
		if (peer != null) {
			peer.turnBody(Math.toRadians(degrees));
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
			peer.execute();
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
	 * A event is generated when the bullet hits a robot
	 * ({@link BulletHitEvent}), wall ({@link BulletMissedEvent}), or another
	 * bullet ({@link BulletHitBulletEvent}).
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
	 *              from the robot's energy.
	 * @see #fireBullet(double)
	 * @see #getGunHeat()
	 * @see #getGunCoolingRate()
	 * @see #onBulletHit(BulletHitEvent)
	 * @see #onBulletHitBullet(BulletHitBulletEvent)
	 * @see #onBulletMissed(BulletMissedEvent)
	 */
	public void fire(double power) {
		if (peer != null) {
			peer.setFire(power);
			peer.execute();
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
	 * A event is generated when the bullet hits a robot
	 * ({@link BulletHitEvent}), wall ({@link BulletMissedEvent}), or another
	 * bullet ({@link BulletHitBulletEvent}).
	 * <p>
	 * Example:
	 * <pre>
	 *   // Fire a bullet with maximum power if the gun is ready
	 *   if (getGunHeat() == 0) {
	 *       Bullet bullet = fireBullet(Rules.MAX_BULLET_POWER);
	 *
	 *       // Get the velocity of the bullet
	 *       if (bullet != null) {
	 *           double bulletVelocity = bullet.getVelocity();
	 *       }
	 *   }
	 * </pre>
	 *
	 * @param power the amount of energy given to the bullet, and subtracted
	 *              from the robot's energy.
	 * @return a {@link Bullet} that contains information about the bullet if it
	 *         was actually fired, which can be used for tracking the bullet after it
	 *         has been fired. If the bullet was not fired, {@code null} is returned.
	 * @see #fire(double)
	 * @see Bullet
	 * @see #getGunHeat()
	 * @see #getGunCoolingRate()
	 * @see #onBulletHit(BulletHitEvent)
	 * @see #onBulletHitBullet(BulletHitBulletEvent)
	 * @see #onBulletMissed(BulletMissedEvent)
	 */
	public Bullet fireBullet(double power) {
		if (peer != null) {
			return peer.fire(power);
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
	 * @see #getGunHeat()
	 * @see #fire(double)
	 * @see #fireBullet(double)
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
	 * @see #getHeading()
	 * @see #getRadarHeading()
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
	 * @see #getGunCoolingRate()
	 * @see #fire(double)
	 * @see #fireBullet(double)
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
	 * @see #getRoundNum()
	 */
	public int getNumRounds() {
		if (peer != null) {
			return peer.getNumRounds();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the sentry border size for a {@link robocode.BorderSentry BorderSentry} that defines the how
	 * far a BorderSentry is allowed to move from the border edges measured in units.<br>
	 * Hence, the sentry border size defines the width/range of the border area surrounding the battlefield that
	 * BorderSentrys cannot leave (sentry robots robots must stay in the border area), but it also define the
	 * distance from the border edges where BorderSentrys are allowed/able to make damage to robots entering this
	 * border area.
	 * 
	 * @return the border size in units/pixels.
	 * 
	 * @since 1.9.0.0
	 */
	public int getSentryBorderSize() {
		if (peer != null) {
			return peer.getSentryBorderSize();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns how many opponents that are left in the current round.
	 *
	 * @return how many opponents that are left in the current round.
	 */
	public int getOthers() {
		if (peer != null) {
			return peer.getOthers();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns how many sentry robots that are left in the current round.
	 *
	 * @return how many sentry robots that are left in the current round.
	 * 
	 * @since 1.9.1.0
	 */
	public int getNumSentries() {
		if (peer != null) {
			return peer.getNumSentries();
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
	 * @see #getHeading()
	 * @see #getGunHeading()
	 */
	public double getRadarHeading() {
		if (peer != null) {
			return peer.getRadarHeading() * 180.0 / Math.PI;
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the current round number (0 to {@link #getNumRounds()} - 1) of
	 * the battle.
	 *
	 * @return the current round number of the battle (zero indexed).
	 * @see #getNumRounds()
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
	 * @return the game time/turn of the current round.
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
	 * @return the velocity of the robot measured in pixels/turn.
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
	 * {@inheritDoc}
	 */
	public void onBulletHit(BulletHitEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBulletHitBullet(BulletHitBulletEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBulletMissed(BulletMissedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onDeath(DeathEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onHitByBullet(HitByBulletEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onHitRobot(HitRobotEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onHitWall(HitWallEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onRobotDeath(RobotDeathEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onScannedRobot(ScannedRobotEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onWin(WinEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onRoundEnded(RoundEndedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBattleEnded(BattleEndedEvent event) {}

	/**
	 * Scans for other robots. This method is called automatically by the game,
	 * as long as the robot is moving, turning its body, turning its gun, or
	 * turning its radar.
	 * <p>
	 * Scan will cause {@link #onScannedRobot(ScannedRobotEvent)
	 * onScannedRobot(ScannedRobotEvent)} to be called if you see a robot.
	 * <p>
	 * There are 2 reasons to call {@code scan()} manually:
	 * <ol>
	 * <li>You want to scan after you stop moving.
	 * <li>You want to interrupt the {@code onScannedRobot} event. This is more
	 * likely. If you are in {@code onScannedRobot} and call {@code scan()},
	 * and you still see a robot, then the system will interrupt your
	 * {@code onScannedRobot} event immediately and start it from the top.
	 * </ol>
	 * <p>
	 * This call executes immediately.
	 *
	 * @see #onScannedRobot(ScannedRobotEvent)
	 * @see ScannedRobotEvent
	 */
	public void scan() {
		if (peer != null) {
			((IStandardRobotPeer) peer).rescan();
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
	 * compensate for this, you can call {@code setAdjustGunForRobotTurn(true)}.
	 * When this is set, the gun will turn independent from the robot's turn,
	 * i.e. the gun will compensate for the robot's body turn.
	 * <p>
	 * Note: This method is additive until you reach the maximum the gun can
	 * turn. The "adjust" is added to the amount you set for turning the robot,
	 * then capped by the physics of the game. If you turn infinite, then the
	 * adjust is ignored (and hence overridden).
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
	 *   // At this point, the robot is facing right (90 degrees), but the gun is still facing up.
	 *   turnLeft(90);
	 *   // Both are back to 0 degrees.
	 * </pre>
	 * <p>
	 * Note: The gun compensating this way does count as "turning the gun".
	 * See {@link #setAdjustRadarForGunTurn(boolean)} for details.
	 *
	 * @param independent {@code true} if the gun must turn independent from the
	 *                    robot's turn; {@code false} if the gun must turn with the robot's turn.
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	public void setAdjustGunForRobotTurn(boolean independent) {
		if (peer != null) {
			((IStandardRobotPeer) peer).setAdjustGunForBodyTurn(independent);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the radar to turn independent from the robot's turn.
	 * <p>
	 * Ok, so this needs some explanation: The radar is mounted on the gun, and
	 * the gun is mounted on the robot's body. So, normally, if the robot turns
	 * 90 degrees to the right, the gun turns, as does the radar. Hence, if the
	 * robot turns 90 degrees to the right, then the gun and radar will turn
	 * with it as the radar is mounted on top of the gun. To compensate for
	 * this, you can call {@code setAdjustRadarForRobotTurn(true)}. When this is
	 * set, the radar will turn independent from the robot's turn, i.e. the
	 * radar will compensate for the robot's turn.
	 * <p>
	 * Note: This method is additive until you reach the maximum the radar can
	 * turn. The "adjust" is added to the amount you set for turning the robot,
	 * then capped by the physics of the game. If you turn infinite, then the
	 * adjust is ignored (and hence overridden).
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
	 *                    the robots's turn; {@code false} if the radar must turn with the robot's
	 *                    turn.
	 * @see #setAdjustGunForRobotTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	public void setAdjustRadarForRobotTurn(boolean independent) {
		if (peer != null) {
			((IStandardRobotPeer) peer).setAdjustRadarForBodyTurn(independent);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the radar to turn independent from the gun's turn.
	 * <p>
	 * Ok, so this needs some explanation: The radar is mounted on the robot's
	 * gun. So, normally, if the gun turns 90 degrees to the right, then the
	 * radar will turn with it as it is mounted on top of the gun. To compensate
	 * for this, you can call {@code setAdjustRadarForGunTurn(true)}. When this
	 * is set, the radar will turn independent from the robot's turn, i.e. the
	 * radar will compensate for the gun's turn.
	 * <p>
	 * Note: This method is additive until you reach the maximum the radar can
	 * turn. The "adjust" is added to the amount you set for turning the gun,
	 * then capped by the physics of the game. If you turn infinite, then the
	 * adjust is ignored (and hence overridden).
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
	 * Note: Calling {@code setAdjustRadarForGunTurn(boolean)} will
	 * automatically call {@link #setAdjustRadarForRobotTurn(boolean)} with the
	 * same value, unless you have already called it earlier. This behavior is
	 * primarily for backward compatibility with older Robocode robots.
	 *
	 * @param independent {@code true} if the radar must turn independent from
	 *                    the gun's turn; {@code false} if the radar must turn with the gun's
	 *                    turn.
	 * @see #setAdjustRadarForRobotTurn(boolean)
	 * @see #setAdjustGunForRobotTurn(boolean)
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
	 * indicates the default (blue) color.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setColors(null, Color.RED, new Color(150, 0, 150));
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param bodyColor  the new body color
	 * @param gunColor   the new gun color
	 * @param radarColor the new radar color
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors(Color)
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
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
	 * indicates the default (blue) color for the body, gun, radar, and scan
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
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param bodyColor	the new body color
	 * @param gunColor	 the new gun color
	 * @param radarColor   the new radar color
	 * @param bulletColor  the new bullet color
	 * @param scanArcColor the new scan arc color
	 * @see #setColors(Color, Color, Color)
	 * @see #setAllColors(Color)
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
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
	 * indicates the default (blue) color for the body, gun, radar, and scan
	 * arc, but white for the bullet color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setAllColors(Color.RED);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new color for all the colors of the robot
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
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
	 * A {@code null} indicates the default (blue) color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setBodyColor(Color.BLACK);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new body color
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors(Color)
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
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
	 * A {@code null} indicates the default (blue) color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setGunColor(Color.RED);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new gun color
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors(Color)
	 * @see #setBodyColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
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
	 * A {@code null} indicates the default (blue) color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setRadarColor(Color.YELLOW);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new radar color
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors(Color)
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
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
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setBulletColor(Color.GREEN);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new bullet color
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors(Color)
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
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
	 * A {@code null} indicates the default (blue) color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 *
	 *   public void run() {
	 *       setScanColor(Color.WHITE);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new scan arc color
	 * @see #setColors(Color, Color, Color)
	 * @see #setColors(Color, Color, Color, Color, Color)
	 * @see #setAllColors(Color)
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see Color
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
	 * Immediately stops all movement, and saves it for a call to
	 * {@link #resume()}. If there is already movement saved from a previous
	 * stop, this will have no effect.
	 * <p>
	 * This method is equivalent to {@code #stop(false)}.
	 *
	 * @see #resume()
	 * @see #stop(boolean)
	 */
	public void stop() {
		stop(false);
	}

	/**
	 * Immediately stops all movement, and saves it for a call to
	 * {@link #resume()}. If there is already movement saved from a previous
	 * stop, you can overwrite it by calling {@code stop(true)}.
	 *
	 * @param overwrite If there is already movement saved from a previous stop,
	 *                  you can overwrite it by calling {@code stop(true)}.
	 * @see #resume()
	 * @see #stop()
	 */
	public void stop(boolean overwrite) {
		if (peer != null) {
			((IStandardRobotPeer) peer).stop(overwrite);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately resumes the movement you stopped by {@link #stop()}, if any.
	 * <p>
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * @see #stop()
	 * @see #stop(boolean)
	 */
	public void resume() {
		if (peer != null) {
			((IStandardRobotPeer) peer).resume();
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
	 * @param degrees the amount of degrees to turn the robot's gun to the left.
	 *                If {@code degrees} > 0 the robot's gun will turn left.
	 *                If {@code degrees} < 0 the robot's gun will turn right.
	 *                If {@code degrees} = 0 the robot's gun will not turn, but execute.
	 * @see #turnGunRight(double)
	 * @see #turnLeft(double)
	 * @see #turnRight(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarRight(double)
	 * @see #setAdjustGunForRobotTurn(boolean)
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
	 * @param degrees the amount of degrees to turn the robot's gun to the right.
	 *                If {@code degrees} > 0 the robot's gun will turn right.
	 *                If {@code degrees} < 0 the robot's gun will turn left.
	 *                If {@code degrees} = 0 the robot's gun will not turn, but execute.
	 * @see #turnGunLeft(double)
	 * @see #turnLeft(double)
	 * @see #turnRight(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarRight(double)
	 * @see #setAdjustGunForRobotTurn(boolean)
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
	 * @param degrees the amount of degrees to turn the robot's radar to the left.
	 *                If {@code degrees} > 0 the robot's radar will turn left.
	 *                If {@code degrees} < 0 the robot's radar will turn right.
	 *                If {@code degrees} = 0 the robot's radar will not turn, but execute.
	 * @see #turnRadarRight(double)
	 * @see #turnLeft(double)
	 * @see #turnRight(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunRight(double)
	 * @see #setAdjustRadarForRobotTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
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
	 * @param degrees the amount of degrees to turn the robot's radar to the right.
	 *                If {@code degrees} > 0 the robot's radar will turn right.
	 *                If {@code degrees} < 0 the robot's radar will turn left.
	 *                If {@code degrees} = 0 the robot's radar will not turn, but execute.
	 * @see #turnRadarLeft(double)
	 * @see #turnLeft(double)
	 * @see #turnRight(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunRight(double)
	 * @see #setAdjustRadarForRobotTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
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
	 * @return the robot's current energy.
	 */
	public double getEnergy() {
		if (peer != null) {
			return peer.getEnergy();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns a graphics context used for painting graphical items for the robot.
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
	 * the bottom left corner of the battlefield, where X is towards right and Y
	 * is upwards.
	 *
	 * @return a graphics context used for painting graphical items for the robot.
	 * @see #onPaint(Graphics2D)
	 * @since 1.6.1
	 */
	public Graphics2D getGraphics() {
		if (peer != null) {
			return peer.getGraphics();
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Sets the debug property with the specified key to the specified value.
	 * <p>
	 * This method is very useful when debugging or reviewing your robot as you
	 * will be able to see this property displayed in the robot console for your
	 * robots under the Debug Properties tab page.
	 *
	 * @param key the name/key of the debug property.
	 * @param value the new value of the debug property, where {@code null} or
	 *              the empty string is used for removing this debug property. 
	 * @since 1.6.2
	 */
	public void setDebugProperty(String key, String value) {
		if (peer != null) {
			peer.setDebugProperty(key, value);
			return;
		}
		uninitializedException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onPaint(Graphics2D g) {}

	/**
	 * {@inheritDoc}
	 */
	public void onKeyPressed(java.awt.event.KeyEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onKeyReleased(java.awt.event.KeyEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onKeyTyped(java.awt.event.KeyEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onMouseClicked(java.awt.event.MouseEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onMouseEntered(java.awt.event.MouseEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onMouseExited(java.awt.event.MouseEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onMousePressed(java.awt.event.MouseEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onMouseReleased(java.awt.event.MouseEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onMouseMoved(java.awt.event.MouseEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onMouseDragged(java.awt.event.MouseEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onMouseWheelMoved(java.awt.event.MouseWheelEvent e) {}

	/**
	 * {@inheritDoc}
	 */
	public void onStatus(StatusEvent e) {}
}
