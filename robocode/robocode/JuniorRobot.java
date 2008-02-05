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


import static java.lang.Math.toRadians;

import java.awt.Color;

import static robocode.util.Utils.normalRelativeAngle;
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
public class JuniorRobot extends JuniorStructure implements IJuniorEvents, IJuniorRobot {

    /**
     * @return structure which should be updated with new info
     */
	public JuniorStructure getJuniorStructure() {
		return this;
	}

    /**
     * @return listener to junior robot events
     */
    public IJuniorEvents getJuniorEventListener() {
		return this;
	}

    /**
     * @return listener to robot events
     */
    public IRobotEvents getRobotEventListener() {
        return null;  //this is not full robot
    }

    /**
     * @return listener to system events
     */
    public ISystemEvents getSystemEventListener() {
        return null;  //this is not full robot
    }
    
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

			peer.updateJuniorRobotFields();
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

			peer.updateJuniorRobotFields();
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

			peer.updateJuniorRobotFields();
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
			peer.updateJuniorRobotFields();
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

			peer.updateJuniorRobotFields();
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
	
			peer.updateJuniorRobotFields();
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
	
			peer.updateJuniorRobotFields();
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
}
