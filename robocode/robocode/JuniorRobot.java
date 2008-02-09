/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Nutch Poovarawan from Cubic Creative
 *     - The design and ideas for the JuniorRobot class
 *     Flemming N. Larsen
 *     - Implementor of the JuniorRobot
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode;


import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import java.awt.Color;

import static robocode.util.Utils.normalRelativeAngle;
import robocode.util.Utils;
import robocode.robotinterfaces.*;


/**
 * This is the simplest robot type, which is simpler than the Robot and
 * AdvancedRobot classes. The JuniorRobot has a simplified model, in purpose of
 * teaching programming skills to inexperienced in programming students.
 * The simplified robot model will keep player from overwhelming of Robocode's
 * rules, programming syntax and programming concept.
 * <p>
 * All methods on this class are blocking calls, i.e. they do not return before
 * their action has been completed and will at least take one turn to execute.
 * However, setting colors is executed immediately and does not cost a turn to
 * perform. 
 *
 * @author Nutch Poovarawan from Cubic Creative (designer)
 * @author Flemming N. Larsen (implementor)
 * 
 * @since 1.4
 */
public class JuniorRobot extends _RobotBase implements IJuniorRobot, Runnable {

	/** The color black (0x000000) */
	public final static int	black = 0x000000;

	/** The color white (0xFFFFFF) */
	public final static int white = 0xFFFFFF;

	/** The color red  (0xFF0000) */
	public final static int red = 0xFF0000;

	/** The color orange (0xFFA500) */
	public final static int orange = 0xFFA500;

	/** The color yellow (0xFFFF00) */
	public final static int yellow = 0xFFFF00;

	/** The color green (0x008000) */
	public final static int green = 0x008000;

	/** The color blue (0x0000FF) */
	public final static int blue = 0x0000FF;

	/** The color purple (0x800080) */
	public final static int purple = 0x800080;

	/** The color brown (0x8B4513) */
	public final static int brown = 0x8B4513;

	/** The color gray (0x808080) */
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
	 * <code>true</code> means that the gun is able to fire; <code>false</code>
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
	 * This field will not be updated while {@link JuniorRobot#onHitByBullet()} event is active.
	 *
	 * @see JuniorRobot#onHitByBullet()
	 * @see #hitByBulletAngle
	 */
	public int hitByBulletBearing = -1;

	/**
	 * Latest angle where this robot has hit another robot (in degrees).
	 * If this robot has never hit another robot, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitRobot()} event is active.
	 *
	 * @see JuniorRobot#onHitRobot()
	 * @see #hitRobotBearing
	 */
	public int hitRobotAngle = -1;

	/**
	 * Latest angle where this robot has hit another robot (in degrees)
	 * compared to the body of this robot.
	 * If this robot has never hit another robot, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitRobot()} event is active.
	 *
	 * @see JuniorRobot#onHitRobot()
	 * @see #hitRobotAngle
	 */
	public int hitRobotBearing = -1;

	/**
	 * Latest angle where this robot has hit a wall (in degrees).
	 * If this robot has never hit a wall, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitWall()} event is active.
	 *
	 * @see JuniorRobot#onHitWall()
	 * @see #hitWallBearing
	 */
	public int hitWallAngle = -1;

	/**
	 * Latest angle where this robot has hit a wall (in degrees)
	 * compared to the body of this robot.
	 * If this robot has never hit a wall, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitWall()} event is active.
	 *
	 * @see JuniorRobot#onHitWall()
	 * @see #hitWallAngle
	 */
	public int hitWallBearing = -1;

	/**
	 * JuniorRobot implements runnable internaly.
	 * This method is called by environment, you don't need it.
	 * @return runnable implementation
	 */
	public Runnable getRobotRunnable() {
		return listener;
	}

	/**
	 * JuniorRobot is listening to basic events internaly.
	 * This method is called by environment, you don't need it.
	 * @return listener to robot events
	 */
	public IBasicEvents getBasicEventListener() {
		return listener;
	}

	/**
	 * Contains the program that controls the behaviour of this robot.
	 * This method is automatically re-called when it has returned.
	 */
	public void run() {}

	/**
	 * Moves this robot forward by pixels.
	 *
	 * @param distance the amount of pixels to move forward
	 * 
	 * @see #back(int)
	 * @see #robotX
	 * @see #robotY
	 */
	public void ahead(int distance) {
		if (peer != null) {
			peer.move(distance);

			updateJuniorRobotFields();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Moves this robot backward by pixels.
	 *
	 * @param distance the amount of pixels to move backward
	 *
	 * @see #ahead(int)
	 * @see #robotX
	 * @see #robotY
	 */
	public void back(int distance) {
		ahead(-distance);
	}

	/**
	 * Turns this robot left by degrees.
	 *
	 * @param degrees the amount of degrees to turn to the left
	 *
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
	 *
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
			peer.turnChassis(toRadians(degrees));

			updateJuniorRobotFields();
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
	 *
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
			peer.turnChassis(normalRelativeAngle(toRadians(angle) - peer.getHeading()));

			updateJuniorRobotFields();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Moves this robot forward by pixels and turns this robot left by degrees
	 * at the same time. The robot will move in a curve that follows a perfect
	 * circle, and the moving and turning will end at the same time.<br>
	 * <br>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance. 
	 *
	 * @param distance the amount of pixels to move forward
	 * @param degrees the amount of degrees to turn to the left
	 *
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
	 * circle, and the moving and turning will end at the same time.<br>
	 * <br>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance. 
	 *
	 * @param distance the amount of pixels to move forward
	 * @param degrees the amount of degrees to turn to the right
	 *
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
			peer.turnAndMoveChassis(distance, toRadians(degrees));
			updateJuniorRobotFields();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Moves this robot backward by pixels and turns this robot left by degrees
	 * at the same time. The robot will move in a curve that follows a perfect
	 * circle, and the moving and turning will end at the same time.<br>
	 * <br>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance. 
	 *
	 * @param distance the amount of pixels to move backward
	 * @param degrees the amount of degrees to turn to the left
	 *
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
	 * circle, and the moving and turning will end at the same time.<br>
	 * <br>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance. 
	 *
	 * @param distance the amount of pixels to move backward
	 * @param degrees the amount of degrees to turn to the right
	 *
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
	 *
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
	 *
	 * @see #gunHeading
	 * @see #gunBearing
	 * @see #turnGunLeft(int)
	 * @see #turnGunTo(int)
	 * @see #bearGunTo(int)
	 */
	public void turnGunRight(int degrees) {
		if (peer != null) {
			peer.turnGun(toRadians(degrees));

			updateJuniorRobotFields();
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
	 *
	 * @see #gunHeading
	 * @see #gunBearing
	 * @see #turnGunLeft(int)
	 * @see #turnGunRight(int)
	 * @see #bearGunTo(int)
	 */
	public void turnGunTo(int angle) {
		if (peer != null) {
			peer.turnGun(normalRelativeAngle(toRadians(angle) - peer.getGunHeading()));
	
			updateJuniorRobotFields();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Turns the gun to the specified angle (in degrees) relative to body of this robot.
	 * The gun will turn to the side with the shortest delta angle to the specified angle.
	 *
	 * @param angle the angle to turn the gun to relative to the body of this robot
	 *
	 * @see #gunHeading
	 * @see #gunBearing
	 * @see #turnGunLeft(int)
	 * @see #turnGunRight(int)
	 * @see #turnGunTo(int)
	 */
	public void bearGunTo(int angle) {
		if (peer != null) {
			peer.turnGun(normalRelativeAngle(peer.getHeading() + toRadians(angle) - peer.getGunHeading()));
	
			updateJuniorRobotFields();
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
			peer.setJuniorFire(power);
			peer.tick();
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
			peer.tick();
		} else {
			uninitializedException();
		}
	}

	/**
	 * Skips the specified number of turns.
	 *
	 * @param turns the number of turns to skip
	 * 
	 * @see #doNothing()
	 */
	public void doNothing(int turns) {
		if (turns <= 0) {
			return;
		}
		if (peer != null) {
			for (int i = 0; i < turns; i++) {
				peer.tick();
			}
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the colors of the robot. The color values are RGB values.
	 * You can use the colors that are already defined for this class.
	 *
	 * @param bodyColor the RGB color value for the body
	 * @param gunColor the RGB color value for the gun
	 * @param radarColor the RGB color value for the radar
	 *
	 * @see #setColors(int, int, int, int, int)
	 */
	public void setColors(int bodyColor, int gunColor, int radarColor) {
		if (peer != null) {
			peer.setCall();
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
	 * @param bodyColor the RGB color value for the body
	 * @param gunColor the RGB color value for the gun
	 * @param radarColor the RGB color value for the radar
	 * @param bulletColor the RGB color value for the bullets
	 * @param scanArcColor the RGB color value for the scan arc
	 *
	 * @see #setColors(int, int, int)
	 */
	public void setColors(int bodyColor, int gunColor, int radarColor, int bulletColor, int scanArcColor) {
		if (peer != null) {
			peer.setCall();
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

	private RobotEventsListener listener;


	private JuniorRobot() {
		listener = new RobotEventsListener(this);
	}

	private void updateJuniorRobotFields() {
		others = getPeer().getOthers();
		energy = Math.max(1, (int) (getPeer().getEnergy() + 0.5));
		robotX = (int) (getPeer().getX() + 0.5);
		robotY = (int) (getPeer().getY() + 0.5);
		heading = (int) (toDegrees(getPeer().getHeading()) + 0.5);
		gunHeading = (int) (toDegrees(getPeer().getGunHeading()) + 0.5);
		gunBearing = (int) (toDegrees(normalRelativeAngle(getPeer().getGunHeading() - getPeer().getHeading())) + 0.5);
		gunReady = (getPeer().getGunHeat() <= 0);
	}

	private void addCustomEvent(Condition condition) {
		if (condition == null) {
			throw new NullPointerException("the condition cannot be null");
		}
		if (peer != null) {
			peer.setCall();
			peer.getEventManager().addCustomEvent(condition);
		} else {
			uninitializedException();
		}
	}

	private class GunReadyCondition extends Condition {
		@Override
		public boolean test() {
			return (getPeer().getGunHeat() <= 0);
		}
	}


	private class GunFireCondition extends Condition {
		@Override
		public boolean test() {
			return (getPeer().getGunHeat() <= 0 && getPeer().getGunTurnRemaining() == 0);
		}
	}


	private class RobotEventsListener implements IBasicEvents, Runnable {

		public RobotEventsListener(JuniorRobot junior) {
			this.junior = junior;
		}
		public JuniorRobot junior;

		public void run() {
			junior.fieldWidth = (int) (junior.getPeer().getBattleFieldWidth() + 0.5);
			junior.fieldHeight = (int) (junior.getPeer().getBattleFieldHeight() + 0.5);
			junior.updateJuniorRobotFields();

			addCustomEvent(new GunReadyCondition());
			addCustomEvent(new GunFireCondition());

			for (;;) {
				junior.run();
			}
		}

		public void onBulletHit(BulletHitEvent event) {// too complicated for junior ?
		}

		public void onBulletHitBullet(BulletHitBulletEvent event) {// too complicated for junior ?
		}

		public void onBulletMissed(BulletMissedEvent event) {// too complicated for junior ?
		}

		public void onRobotDeath(RobotDeathEvent event) {// too complicated for junior ?
		}

		public void onWin(WinEvent event) {// too complicated for junior ?
		}

		public void onDeath(DeathEvent event) {
			others = getPeer().getOthers();
		}

		public void onHitByBullet(HitByBulletEvent event) {
			junior.updateJuniorRobotFields();
			double angle = junior.getPeer().getHeading() + event.getBearingRadians();

			junior.hitByBulletAngle = (int) (Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
			junior.hitByBulletBearing = (int) (event.getBearing() + 0.5);
			junior.onHitByBullet();
		}

		public void onHitRobot(HitRobotEvent event) {
			junior.updateJuniorRobotFields();
			double angle = junior.getPeer().getHeading() + event.getBearingRadians();

			junior.hitRobotAngle = (int) (Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
			junior.hitRobotBearing = (int) (event.getBearing() + 0.5);
			junior.onHitRobot();
		}

		public void onHitWall(HitWallEvent event) {
			junior.updateJuniorRobotFields();
			double angle = junior.getPeer().getHeading() + event.getBearingRadians();

			junior.hitWallAngle = (int) (Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
			junior.hitWallBearing = (int) (event.getBearing() + 0.5);
			junior.onHitWall();
		}

		public void onScannedRobot(ScannedRobotEvent event) {
			junior.scannedDistance = (int) (event.getDistance() + 0.5);
			junior.scannedEnergy = Math.max(1, (int) (event.getEnergy() + 0.5));
			junior.scannedAngle = (int) (Math.toDegrees(
					Utils.normalAbsoluteAngle(junior.getPeer().getHeading() + event.getBearingRadians()))
							+ 0.5);
			junior.scannedBearing = (int) (event.getBearing() + 0.5);
			junior.scannedHeading = (int) (event.getHeading() + 0.5);
			junior.scannedVelocity = (int) (event.getVelocity() + 0.5);
            
			junior.onScannedRobot();
		}

		public void onCustomEvent(CustomEvent event) {
			Condition c = event.getCondition();

			if (c instanceof GunReadyCondition) {
				junior.gunReady = true;
			} else if (c instanceof GunFireCondition) {
				double firePower = junior.getPeer().getJuniorFirePower(); 

				if (firePower > 0) {
					if (junior.getPeer().setFire(firePower) != null) {
						junior.gunReady = false;
					}
					junior.getPeer().setJuniorFire(0);
				}
			}
		}
	}
}
