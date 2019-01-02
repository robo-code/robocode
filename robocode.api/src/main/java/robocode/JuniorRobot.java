/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IJuniorRobot;
import robocode.robotinterfaces.peer.IJuniorRobotPeer;
import robocode.util.Utils;

import java.awt.*;
import static java.lang.Math.toRadians;


/**
 * This is the simplest robot type, which is simpler than the {@link Robot} and
 * {@link AdvancedRobot} classes. The JuniorRobot has a simplified model, in
 * purpose of teaching programming skills to inexperienced in programming
 * students. The simplified robot model will keep player from overwhelming of
 * Robocode's rules, programming syntax and programming concept.
 * <p>
 * Instead of using getters and setters, public fields are provided for
 * receiving information like the last scanned robot, the coordinate of the
 * robot etc.
 * <p>
 * All methods on this class are blocking calls, i.e. they do not return before
 * their action has been completed and will at least take one turn to execute.
 * However, setting colors is executed immediately and does not cost a turn to
 * perform.
 *
 * @see Robot
 * @see AdvancedRobot
 * @see TeamRobot
 * @see RateControlRobot
 * @see Droid
 * @see BorderSentry
 *
 * @author Nutch Poovarawan from Cubic Creative (designer)
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (contributor)
 *
 * @since 1.4
 */
public class JuniorRobot extends _RobotBase implements IJuniorRobot {

	/**
	 * The color black (0x000000)
	 */
	public final static int black = 0x000000;

	/**
	 * The color white (0xFFFFFF)
	 */
	public final static int white = 0xFFFFFF;

	/**
	 * The color red  (0xFF0000)
	 */
	public final static int red = 0xFF0000;

	/**
	 * The color orange (0xFFA500)
	 */
	public final static int orange = 0xFFA500;

	/**
	 * The color yellow (0xFFFF00)
	 */
	public final static int yellow = 0xFFFF00;

	/**
	 * The color green (0x008000)
	 */
	public final static int green = 0x008000;

	/**
	 * The color blue (0x0000FF)
	 */
	public final static int blue = 0x0000FF;

	/**
	 * The color purple (0x800080)
	 */
	public final static int purple = 0x800080;

	/**
	 * The color brown (0x8B4513)
	 */
	public final static int brown = 0x8B4513;

	/**
	 * The color gray (0x808080)
	 */
	public final static int gray = 0x808080;

	/**
	 * Contains the width of the battlefield.
	 *
	 * @see #fieldWidth
	 */
	public int fieldWidth;

	/**
	 * Contains the height of the battlefield.
	 *
	 * @see #fieldWidth
	 */
	public int fieldHeight;

	/**
	 * Current number of other robots on the battle field.
	 */
	public int others;

	/**
	 * Current energy of this robot, where 100 means full energy and 0 means no energy (dead).
	 */
	public int energy;

	/**
	 * Current horizontal location of this robot (in pixels).
	 *
	 * @see #robotY
	 */
	public int robotX;

	/**
	 * Current vertical location of this robot (in pixels).
	 *
	 * @see #robotX
	 */
	public int robotY;

	/**
	 * Current heading angle of this robot (in degrees).
	 *
	 * @see #turnLeft(int)
	 * @see #turnRight(int)
	 * @see #turnTo(int)
	 * @see #turnAheadLeft(int, int)
	 * @see #turnAheadRight(int, int)
	 * @see #turnBackLeft(int, int)
	 * @see #turnBackRight(int, int)
	 */
	public int heading;

	/**
	 * Current gun heading angle of this robot (in degrees).
	 *
	 * @see #gunBearing
	 * @see #turnGunLeft(int)
	 * @see #turnGunRight(int)
	 * @see #turnGunTo(int)
	 * @see #bearGunTo(int)
	 */
	public int gunHeading;

	/**
	 * Current gun heading angle of this robot compared to its body (in degrees).
	 *
	 * @see #gunHeading
	 * @see #turnGunLeft(int)
	 * @see #turnGunRight(int)
	 * @see #turnGunTo(int)
	 * @see #bearGunTo(int)
	 */
	public int gunBearing;

	/**
	 * Flag specifying if the gun is ready to fire, i.e. gun heat <= 0.
	 * {@code true} means that the gun is able to fire; {@code false}
	 * means that the gun cannot fire yet as it still needs to cool down.
	 *
	 * @see #fire()
	 * @see #fire(double)
	 */
	public boolean gunReady;

	/**
	 * Current distance to the scanned nearest other robot (in pixels).
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link #onScannedRobot()} event is active.
	 *
	 * @see #onScannedRobot()
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 * @see #scannedVelocity
	 * @see #scannedHeading
	 */
	public int scannedDistance = -1;

	/**
	 * Current angle to the scanned nearest other robot (in degrees).
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link #onScannedRobot()} event is active.
	 *
	 * @see #onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 * @see #scannedVelocity
	 * @see #scannedHeading
	 */
	public int scannedAngle = -1;

	/**
	 * Current angle to the scanned nearest other robot (in degrees) compared to
	 * the body of this robot.
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link #onScannedRobot()} event is active.
	 *
	 * @see #onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedEnergy
	 * @see #scannedVelocity
	 * @see #scannedHeading
	 */
	public int scannedBearing = -1;

	/**
	 * Current velocity of the scanned nearest other robot.
	 * If there is no robot in the radar's sight, this field will be -99.
	 * Note that a positive value means that the robot moves forward, a negative
	 * value means that the robot moved backward, and 0 means that the robot is
	 * not moving at all.
	 * This field will not be updated while {@link #onScannedRobot()} event is active.
	 *
	 * @see #onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 * @see #scannedHeading
	 */
	public int scannedVelocity = -99;

	/**
	 * Current heading of the scanned nearest other robot (in degrees).
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link #onScannedRobot()} event is active.
	 *
	 * @see #onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 * @see #scannedVelocity
	 */
	public int scannedHeading = -1;

	/**
	 * Current energy of scanned nearest other robot.
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link #onScannedRobot()} event is active.
	 *
	 * @see #onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedVelocity
	 */
	public int scannedEnergy = -1;

	/**
	 * Latest angle from where this robot was hit by a bullet (in degrees).
	 * If the robot has never been hit, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link #onHitByBullet()} event is active.
	 *
	 * @see #onHitByBullet()
	 * @see #hitByBulletBearing
	 */
	public int hitByBulletAngle = -1;

	/**
	 * Latest angle from where this robot was hit by a bullet (in degrees)
	 * compared to the body of this robot.
	 * If the robot has never been hit, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link #onHitByBullet()} event is active.
	 *
	 * @see #onHitByBullet()
	 * @see #hitByBulletAngle
	 */
	public int hitByBulletBearing = -1;

	/**
	 * Latest angle where this robot has hit another robot (in degrees).
	 * If this robot has never hit another robot, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link #onHitRobot()} event is active.
	 *
	 * @see #onHitRobot()
	 * @see #hitRobotBearing
	 */
	public int hitRobotAngle = -1;

	/**
	 * Latest angle where this robot has hit another robot (in degrees)
	 * compared to the body of this robot.
	 * If this robot has never hit another robot, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link #onHitRobot()} event is active.
	 *
	 * @see #onHitRobot()
	 * @see #hitRobotAngle
	 */
	public int hitRobotBearing = -1;

	/**
	 * Latest angle where this robot has hit a wall (in degrees).
	 * If this robot has never hit a wall, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link #onHitWall()} event is active.
	 *
	 * @see #onHitWall()
	 * @see #hitWallBearing
	 */
	public int hitWallAngle = -1;

	/**
	 * Latest angle where this robot has hit a wall (in degrees)
	 * compared to the body of this robot.
	 * If this robot has never hit a wall, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link #onHitWall()} event is active.
	 *
	 * @see #onHitWall()
	 * @see #hitWallAngle
	 */
	public int hitWallBearing = -1;

	/**
	 * The robot event handler for this robot.
	 */
	private InnerEventHandler innerEventHandler;

	/**
	 * Moves this robot forward by pixels.
	 *
	 * @param distance the amount of pixels to move forward
	 * @see #back(int)
	 * @see #robotX
	 * @see #robotY
	 */
	public void ahead(int distance) {
		if (peer != null) {
			peer.move(distance);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Moves this robot backward by pixels.
	 *
	 * @param distance the amount of pixels to move backward
	 * @see #ahead(int)
	 * @see #robotX
	 * @see #robotY
	 */
	public void back(int distance) {
		ahead(-distance);
	}

	/**
	 * Turns the gun to the specified angle (in degrees) relative to body of this robot.
	 * The gun will turn to the side with the shortest delta angle to the specified angle.
	 *
	 * @param angle the angle to turn the gun to relative to the body of this robot
	 * @see #gunHeading
	 * @see #gunBearing
	 * @see #turnGunLeft(int)
	 * @see #turnGunRight(int)
	 * @see #turnGunTo(int)
	 */
	public void bearGunTo(int angle) {
		if (peer != null) {
			peer.turnGun(Utils.normalRelativeAngle(peer.getBodyHeading() + toRadians(angle) - peer.getGunHeading()));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Skips a turn.
	 *
	 * @see #doNothing(int)
	 */
	public void doNothing() {
		if (peer != null) {
			peer.execute();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Skips the specified number of turns.
	 *
	 * @param turns the number of turns to skip
	 * @see #doNothing()
	 */
	public void doNothing(int turns) {
		if (turns <= 0) {
			return;
		}
		if (peer != null) {
			for (int i = 0; i < turns; i++) {
				peer.execute();
			}
		} else {
			uninitializedException();
		}
	}

	/**
	 * Fires a bullet with the default power of 1.
	 * If the gun heat is more than 0 and hence cannot fire, this method will
	 * suspend until the gun is ready to fire, and then fire a bullet.
	 *
	 * @see #gunReady
	 */
	public void fire() {
		fire(1);
	}

	/**
	 * Fires a bullet with the specified bullet power, which is between 0.1 and 3
	 * where 3 is the maximum bullet power.
	 * If the gun heat is more than 0 and hence cannot fire, this method will
	 * suspend until the gun is ready to fire, and then fire a bullet.
	 *
	 * @param power between 0.1 and 3
	 * @see #gunReady
	 */
	public void fire(double power) {
		if (peer != null) {
			getEventHandler().juniorFirePower = power;
			peer.execute();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Do not call this method!
	 * <p>
	 * {@inheritDoc}
	 */
	public final IBasicEvents getBasicEventListener() {
		return getEventHandler();
	}

	/**
	 * Do not call this method!
	 * <p>
	 * {@inheritDoc}
	 */
	public final Runnable getRobotRunnable() {
		return getEventHandler();
	}

	/**
	 * This event methods is called from the game when this robot has been hit
	 * by another robot's bullet. When this event occurs the
	 * {@link #hitByBulletAngle} and {@link #hitByBulletBearing} fields values
	 * are automatically updated.
	 *
	 * @see #hitByBulletAngle
	 * @see #hitByBulletBearing
	 */
	public void onHitByBullet() {}

	/**
	 * This event methods is called from the game when a bullet from this robot
	 * has hit another robot. When this event occurs the {@link #hitRobotAngle}
	 * and {@link #hitRobotBearing} fields values are automatically updated.
	 *
	 * @see #hitRobotAngle
	 * @see #hitRobotBearing
	 */
	public void onHitRobot() {}

	/**
	 * This event methods is called from the game when this robot has hit a wall.
	 * When this event occurs the {@link #hitWallAngle} and {@link #hitWallBearing}
	 * fields values are automatically updated.
	 *
	 * @see #hitWallAngle
	 * @see #hitWallBearing
	 */
	public void onHitWall() {}

	/**
	 * This event method is called from the game when the radar detects another
	 * robot. When this event occurs the {@link #scannedDistance},
	 * {@link #scannedAngle}, {@link #scannedBearing}, and {@link #scannedEnergy}
	 * field values are automatically updated.
	 *
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 */
	public void onScannedRobot() {}

	/**
	 * The main method in every robot. You must override this to set up your
	 * robot's basic behavior.
	 * <p>
	 * Example:
	 * <pre>
	 *   // A basic robot that moves around in a square
	 *   public void run() {
	 *       ahead(100);
	 *       turnRight(90);
	 *   }
	 * </pre>
	 * This method is automatically re-called when it has returned.
	 */
	public void run() {}

	/**
	 * Sets the colors of the robot. The color values are RGB values.
	 * You can use the colors that are already defined for this class.
	 *
	 * @param bodyColor  the RGB color value for the body
	 * @param gunColor   the RGB color value for the gun
	 * @param radarColor the RGB color value for the radar
	 * @see #setColors(int, int, int, int, int)
	 */
	public void setColors(int bodyColor, int gunColor, int radarColor) {
		if (peer != null) {
			peer.setBodyColor(new Color(bodyColor));
			peer.setGunColor(new Color(gunColor));
			peer.setRadarColor(new Color(radarColor));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the colors of the robot. The color values are RGB values.
	 * You can use the colors that are already defined for this class.
	 *
	 * @param bodyColor	the RGB color value for the body
	 * @param gunColor	 the RGB color value for the gun
	 * @param radarColor   the RGB color value for the radar
	 * @param bulletColor  the RGB color value for the bullets
	 * @param scanArcColor the RGB color value for the scan arc
	 * @see #setColors(int, int, int)
	 */
	public void setColors(int bodyColor, int gunColor, int radarColor, int bulletColor, int scanArcColor) {
		if (peer != null) {
			peer.setBodyColor(new Color(bodyColor));
			peer.setGunColor(new Color(gunColor));
			peer.setRadarColor(new Color(radarColor));
			peer.setBulletColor(new Color(bulletColor));
			peer.setScanColor(new Color(scanArcColor));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Moves this robot forward by pixels and turns this robot left by degrees
	 * at the same time. The robot will move in a curve that follows a perfect
	 * circle, and the moving and turning will end at the same time.
	 * <p>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance.
	 *
	 * @param distance the amount of pixels to move forward
	 * @param degrees  the amount of degrees to turn to the left
	 * @see #heading
	 * @see #robotX
	 * @see #robotY
	 * @see #turnLeft(int)
	 * @see #turnRight(int)
	 * @see #turnTo(int)
	 * @see #turnAheadRight(int, int)
	 * @see #turnBackLeft(int, int)
	 * @see #turnBackRight(int, int)
	 */
	public void turnAheadLeft(int distance, int degrees) {
		turnAheadRight(distance, -degrees);
	}

	/**
	 * Moves this robot forward by pixels and turns this robot right by degrees
	 * at the same time. The robot will move in a curve that follows a perfect
	 * circle, and the moving and turning will end at the same time.
	 * <p>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance.
	 *
	 * @param distance the amount of pixels to move forward
	 * @param degrees  the amount of degrees to turn to the right
	 * @see #heading
	 * @see #robotX
	 * @see #robotY
	 * @see #turnLeft(int)
	 * @see #turnRight(int)
	 * @see #turnTo(int)
	 * @see #turnAheadLeft(int, int)
	 * @see #turnBackLeft(int, int)
	 * @see #turnBackRight(int, int)
	 */
	public void turnAheadRight(int distance, int degrees) {
		if (peer != null) {
			((IJuniorRobotPeer) peer).turnAndMove(distance, toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Moves this robot backward by pixels and turns this robot left by degrees
	 * at the same time. The robot will move in a curve that follows a perfect
	 * circle, and the moving and turning will end at the same time.
	 * <p>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance.
	 *
	 * @param distance the amount of pixels to move backward
	 * @param degrees  the amount of degrees to turn to the left
	 * @see #heading
	 * @see #robotX
	 * @see #robotY
	 * @see #turnLeft(int)
	 * @see #turnRight(int)
	 * @see #turnTo(int)
	 * @see #turnAheadLeft(int, int)
	 * @see #turnAheadRight(int, int)
	 * @see #turnBackRight(int, int)
	 */
	public void turnBackLeft(int distance, int degrees) {
		turnAheadRight(-distance, degrees);
	}

	/**
	 * Moves this robot backward by pixels and turns this robot right by degrees
	 * at the same time. The robot will move in a curve that follows a perfect
	 * circle, and the moving and turning will end at the same time.
	 * <p>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance.
	 *
	 * @param distance the amount of pixels to move backward
	 * @param degrees  the amount of degrees to turn to the right
	 * @see #heading
	 * @see #robotX
	 * @see #robotY
	 * @see #turnLeft(int)
	 * @see #turnRight(int)
	 * @see #turnTo(int)
	 * @see #turnAheadLeft(int, int)
	 * @see #turnAheadRight(int, int)
	 * @see #turnBackLeft(int, int)
	 */
	public void turnBackRight(int distance, int degrees) {
		turnAheadRight(-distance, -degrees);
	}

	/**
	 * Turns the gun left by degrees.
	 *
	 * @param degrees the amount of degrees to turn the gun to the left
	 * @see #gunHeading
	 * @see #gunBearing
	 * @see #turnGunRight(int)
	 * @see #turnGunTo(int)
	 * @see #bearGunTo(int)
	 */
	public void turnGunLeft(int degrees) {
		turnGunRight(-degrees);
	}

	/**
	 * Turns the gun right by degrees.
	 *
	 * @param degrees the amount of degrees to turn the gun to the right
	 * @see #gunHeading
	 * @see #gunBearing
	 * @see #turnGunLeft(int)
	 * @see #turnGunTo(int)
	 * @see #bearGunTo(int)
	 */
	public void turnGunRight(int degrees) {
		if (peer != null) {
			peer.turnGun(toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Turns the gun to the specified angle (in degrees).
	 * The gun will turn to the side with the shortest delta angle to the
	 * specified angle.
	 *
	 * @param angle the angle to turn the gun to
	 * @see #gunHeading
	 * @see #gunBearing
	 * @see #turnGunLeft(int)
	 * @see #turnGunRight(int)
	 * @see #bearGunTo(int)
	 */
	public void turnGunTo(int angle) {
		if (peer != null) {
			peer.turnGun(Utils.normalRelativeAngle(toRadians(angle) - peer.getGunHeading()));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Turns this robot left by degrees.
	 *
	 * @param degrees the amount of degrees to turn to the left
	 * @see #heading
	 * @see #turnRight(int)
	 * @see #turnTo(int)
	 * @see #turnAheadLeft(int, int)
	 * @see #turnAheadRight(int, int)
	 * @see #turnBackLeft(int, int)
	 * @see #turnBackRight(int, int)
	 */
	public void turnLeft(int degrees) {
		turnRight(-degrees);
	}

	/**
	 * Turns this robot right by degrees.
	 *
	 * @param degrees the amount of degrees to turn to the right
	 * @see #heading
	 * @see #turnLeft(int)
	 * @see #turnTo(int)
	 * @see #turnAheadLeft(int, int)
	 * @see #turnAheadRight(int, int)
	 * @see #turnBackLeft(int, int)
	 * @see #turnBackRight(int, int)
	 */
	public void turnRight(int degrees) {
		if (peer != null) {
			peer.turnBody(toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * Turns this robot to the specified angle (in degrees).
	 * The robot will turn to the side with the shortest delta angle to the
	 * specified angle.
	 *
	 * @param angle the angle to turn this robot to
	 * @see #heading
	 * @see #turnLeft(int)
	 * @see #turnRight(int)
	 * @see #turnAheadLeft(int, int)
	 * @see #turnAheadRight(int, int)
	 * @see #turnBackLeft(int, int)
	 * @see #turnBackRight(int, int)
	 */
	public void turnTo(int angle) {
		if (peer != null) {
			peer.turnBody(Utils.normalRelativeAngle(toRadians(angle) - peer.getBodyHeading()));
		} else {
			uninitializedException();
		}
	}

	/*
	 * Returns the event handler of this robot.
	 */
	private InnerEventHandler getEventHandler() {
		if (innerEventHandler == null) {
			innerEventHandler = new InnerEventHandler();
		}
		return innerEventHandler;
	}

	/*
	 * The JuniorRobot event handler, which implements the basic robot events,
	 * JuniorRobot event, and Runnable.
	 */
	private final class InnerEventHandler implements IBasicEvents, Runnable {

		private double juniorFirePower;
		private long currentTurn;

		public void onBulletHit(BulletHitEvent event) {}

		public void onBulletHitBullet(BulletHitBulletEvent event) {}

		public void onBulletMissed(BulletMissedEvent event) {}

		public void onDeath(DeathEvent event) {}

		public void onHitByBullet(HitByBulletEvent event) {
			double angle = peer.getBodyHeading() + event.getBearingRadians();

			hitByBulletAngle = (int) (Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
			hitByBulletBearing = (int) (event.getBearing() + 0.5);
			JuniorRobot.this.onHitByBullet();
		}

		public void onHitRobot(HitRobotEvent event) {
			double angle = peer.getBodyHeading() + event.getBearingRadians();

			hitRobotAngle = (int) (Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
			hitRobotBearing = (int) (event.getBearing() + 0.5);
			JuniorRobot.this.onHitRobot();
		}

		public void onHitWall(HitWallEvent event) {
			double angle = peer.getBodyHeading() + event.getBearingRadians();

			hitWallAngle = (int) (Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
			hitWallBearing = (int) (event.getBearing() + 0.5);
			JuniorRobot.this.onHitWall();
		}

		public void onRobotDeath(RobotDeathEvent event) {
			others = peer.getOthers();
		}

		public void onScannedRobot(ScannedRobotEvent event) {
			scannedDistance = (int) (event.getDistance() + 0.5);
			scannedEnergy = Math.max(1, (int) (event.getEnergy() + 0.5));
			scannedAngle = (int) (Math.toDegrees(
					Utils.normalAbsoluteAngle(peer.getBodyHeading() + event.getBearingRadians()))
							+ 0.5);
			scannedBearing = (int) (event.getBearing() + 0.5);
			scannedHeading = (int) (event.getHeading() + 0.5);
			scannedVelocity = (int) (event.getVelocity() + 0.5);

			JuniorRobot.this.onScannedRobot();
		}

		public void onStatus(StatusEvent e) {
			final RobotStatus s = e.getStatus();

			others = peer.getOthers();
			energy = Math.max(1, (int) (s.getEnergy() + 0.5));
			robotX = (int) (s.getX() + 0.5);
			robotY = (int) (s.getY() + 0.5);
			heading = (int) (s.getHeading() + 0.5);
			gunHeading = (int) (s.getGunHeading() + 0.5);
			gunBearing = (int) (Utils.normalRelativeAngle(s.getGunHeading() - s.getHeading()) + 0.5);
			gunReady = (s.getGunHeat() <= 0);

			currentTurn = e.getTime();

			// Auto fire
			if (juniorFirePower > 0 && gunReady && (peer.getGunTurnRemaining() == 0)) {
				if (peer.setFire(juniorFirePower) != null) {
					gunReady = false;
					juniorFirePower = 0;
				}
			}

			// Reset event data
			scannedDistance = -1;
			scannedAngle = -1;
			scannedBearing = -1;
			scannedVelocity = -99;
			scannedHeading = -1;
			scannedEnergy = -1;
			hitByBulletAngle = -1;
			hitByBulletBearing = -1;
			hitRobotAngle = -1;
			hitRobotBearing = -1;
			hitWallAngle = -1;
			hitWallBearing = -1;		
		}

		public void onWin(WinEvent event) {}

		public void run() {
			fieldWidth = (int) (peer.getBattleFieldWidth() + 0.5);
			fieldHeight = (int) (peer.getBattleFieldHeight() + 0.5);

			// noinspection InfiniteLoopStatement
			while (true) {
				long lastTurn = currentTurn; // Used for the rescan check

				JuniorRobot.this.run(); // Run the code in the JuniorRobot

				// Make sure that we rescan if the robot did not execute anything this turn.
				// When the robot executes the currentTurn will automatically be increased by 1,
				// So when the turn stays the same, the robot did not take any action this turn.
				if (lastTurn == currentTurn) {
					peer.rescan(); // Spend a turn on rescanning
				}
			}
		}
	}
}
