/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package samplesentry;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import robocode.*;
import robocode.util.Utils;


/**
 * Guard - a sample sentry robot that guards the borders of the battlefield.
 *
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.9.0.0
 */
public class Guard extends AdvancedRobot implements BorderSentry {

	// Map used retrieve the scanned data for a robot based on the robot name
	Map<String/* Name of robot */, ScannedRobotData> scannedRobotData = new HashMap<String, ScannedRobotData>();

	// Current target robot, which we our robot should fire at
	ScannedRobotData target;

	// Last time when the robot shiftet its direction
	long lastDirectionShift;

	// Current direction, where 1 means ahead/forward, and -1 means back
	int direction = 1;

	// The main method of the robot, which is called when the robot started up.
	public void run() {

		// Do initialization stuff..

		// Let the radar turn be independent on the gun turn
		setAdjustRadarForGunTurn(true);
		// Let the gun turn be independent on the robot (body) turn
		setAdjustGunForRobotTurn(true);

		// Set the robot body and turret/gun to black, and the radar, bullets and scan arc to red
		setColors(Color.BLACK, Color.BLACK, Color.RED, Color.RED, Color.RED);

		// Set the radar to turn left as fast as possible forever.
		// Note, that it will take 8 turns to turn the radar 360 degrees in total (360 / 45 degrees)
		setTurnRadarLeft(Double.POSITIVE_INFINITY);

		// Loop forever (if the robot stops doing anything, the game will disable it)
		while (true) {
			// Prepare next turn...

			// Move the robot
			move();
			// Execute all pending robot commands, meaning that the robot will now take action!
			// This means that commands like, setFire(), setTurnLeft(), setTurnRadarRight() etc. will be executed.
			execute();
		}
	}

	/**
	 * This method is called by the game before each turn with the current status of our robot.
	 *
	 * @param statusEvent is the status event containing status data for our robot.
	 */
	public void onStatus(StatusEvent statusEvent) {

		// 'newTarget' is used for selecting our new target to fire at.
		// Set this to null here, which means that we have no target... yet!
		ScannedRobotData newTarget = null;

		// Make a copy of our list containing all scanned robot data.
		List<ScannedRobotData> currentScanData = new ArrayList<ScannedRobotData>(scannedRobotData.values());

		// Remove scanned robot data that is too old. When data is more than 360 / 45 degrees = 8 turns old,
		// our radar should retrieve new information about a particular robot, unless it has died or is
		// out of the scan radius (1200 units). Anyways, the robot will probably have moved far ways since then.
		for (Iterator<ScannedRobotData> it = currentScanData.iterator(); it.hasNext();) {
			ScannedRobotData robotData = it.next();
			// Remove entry, when the the delta time (scanned event time - status event time) is more than 8 turns
			if (statusEvent.getTime() - robotData.time > 8) {
				it.remove();
			}
		}

		// Now, make a copy of current scan data that should contain target candidates
		List<ScannedRobotData> targetCandidates = new ArrayList<ScannedRobotData>(currentScanData);

		// Remove all target candidates that are outside our sentry robot's attack range as we
		// will not be able to harm those robots with our gun fire (is game rules).
		for (Iterator<ScannedRobotData> it = targetCandidates.iterator(); it.hasNext();) {
			ScannedRobotData robotData = it.next();
			// Remove robot data if its location is outside the sentry attack range
			if (isOutsideSentryAttackRange(robotData.x, robotData.y)) {
				it.remove();
			}
		}

		// If we don't have any target candidates, we have no candidates to fire at.
		// However, we will then use the current scan data for moving out robot as close to the nearest robot
		// outside the sentry robot's attack range, so we have a better chance to hit it when it moves too
		// close to the border.
		if (targetCandidates.size() == 0) {
			targetCandidates = new ArrayList<ScannedRobotData>(currentScanData);
		}

		// Now, pick the new target robot among the target candidates based on how far those are to our robot.
		// Pick the robot closest to our robot.

		double shortestDistance = Double.MAX_VALUE; // Initialize shortest distance seen, to maximum value

		for (Iterator<ScannedRobotData> it = targetCandidates.iterator(); it.hasNext();) {
			ScannedRobotData robotData = it.next();
			// Calculate the distance to the robot based on the hypotenuse (Pythagoras)
			double distance = distanceTo(robotData.x, robotData.y);
			// Check if the distance is shorter than shortest distance calculated so far
			if (distance < shortestDistance) {
				// If the distance is shorter then save the calculated distance as the new shortest distance
				shortestDistance = distance;
				// Save the robot data as our new target robot
				newTarget = robotData;
			}
		}

		// Check if we got a new target robot
		if (newTarget != null) {
			// We got a new target robot..

			// Calculate the target angle of the robot
			double targetAngle = Math.atan2(newTarget.x - getX(), newTarget.y - getY());
			// Normalize the target angle so that the angle is kept within the range [0, PI[
			targetAngle = Utils.normalAbsoluteAngle(targetAngle);

			// Calculate the delta gun angle, i.e. how much of robot should be moved to the right.
			double deltaGunAngle = Utils.normalRelativeAngle(targetAngle - getGunHeadingRadians());

			// Set the gun to turn right the number of radians stored in the calculated delta gun angle.
			// Note: The setTurnGunRightRadians() will first be executed, when the execute() method is called.
			// If the delta gun angle is negative, it will automatically be moved in the opposite direction.
			setTurnGunRightRadians(deltaGunAngle);

			// Check that the radar turn has almost completed (has less than 1 degrees left to turn) and
			// that the target robot is not outside the entry attack range.
			if (getRadarTurnRemaining() < 1 && !isOutsideSentryAttackRange(newTarget.x, newTarget.y)) {

				// Calcuate the firepower we should use when firing at our target..
				
				// We want to use the maximum bullet power 3, when the target is within 100 units and
				// the bullet power should become lesser the longer the distance is to our target.
				// Hence, we calculate the firepower as 3 * 100 / distance to target.
				double firePower = 300 / distanceTo(newTarget.x, newTarget.y);

				// Set our gun to fire with the calculated amount of fire power.
				// Note: If the fire power is less than the minimum fire power 0.1 the gun will not fire.
				// If the fire power is greater than the maximum fire power 3, then gun will use 3 as fire power.
				setFire(firePower);
			}

			// Paint a circle on our current target (debugging graphics)
			paintCircle(newTarget.x, newTarget.y);
		}

		// Set our new target as our new "global" target, so that other methods can use it
		this.target = newTarget;
	}

	/**
	 * This method is called by the game whenever our robot has scanned another robot by turning the radar.
	 * Note: Our radar is turning around forever at maximum speed, so this event is called as soon as the
	 * radar "hits" another robot.
	 * 
	 * @param scannedRobotEvent is the scanned robot event containing data about the scanned robot.
	 */
	public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
		// Exit this method, if the scanned robot is another sentry robot
		if (scannedRobotEvent.isSentryRobot()) {
			return;
		}

		// Get the distance from our robot to the scanned robot
		double distance = scannedRobotEvent.getDistance();
		// Calculate the angle in radians to the scanned robot.
		// Angle = our robot's heading (angle) + the bearing (delta angle) to the scanned robot.
		double angle = Math.toRadians(getHeading() + scannedRobotEvent.getBearing());

		// Prepare an entry with scanned robot data that we can store in our scanned data map
		ScannedRobotData robotData = new ScannedRobotData();
		// Store the name of the scanned robot
		robotData.name = scannedRobotEvent.getName();
		// Store the time when the robot was scanned
		robotData.time = scannedRobotEvent.getTime();
		// Calculate and store the x coordinate of the scanned robot (using the Robocode coordinate system)
		robotData.x = getX() + Math.sin(angle) * distance;
		// Calculate and store the y coordinate of the scanned robot (using the Robocode coordinate system)
		robotData.y = getY() + Math.cos(angle) * distance;

		// Store the entry of scanned robot entry in our map over scanned robot data.
		// We use the name of the robot as the key for accessing the scanned data for that particular robot later
		scannedRobotData.put(robotData.name, robotData);
	}

	/**
	 * This method is called by the game whenever another robot dies.
	 * 
	 * @param robotDeathEvent is the robot death event containing data about the robot that died.
	 */
	public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
		// Remove the scanned robot data for the robot that died.
		// This data is useless now, and will only confuse our robot if it stays in our scanned robot data.
		scannedRobotData.remove(robotDeathEvent.getName());

		// Remove our global target, if our target was the robot that died, but only if we have a current target
		if (target != null && target.name.equals(robotDeathEvent.getName())) {
			target = null;
		}
	}

	/**
	 * Method for moving our robot around on the battlefield.
	 */
	private void move() {

		// The movement strategy is to move as close to our target robot as possible.
		// Our robot should move along the borders all the time vertically or horizontally.
		// When we get close to our target or have no where to go, our robot should shift its direction
		// from side to side, so it does not stand still at any time.

		// Get close to our target if we have a target robot
		if (target != null) {
			// Calculate the range from the walls/borders, our robot should keep within
			int borderRange = getSentryBorderSize() - 20;

			// The horizontal and vertical flags are used for determining, if our robot should
			// move horizontal or vertical.
			boolean horizontal = false;
			boolean vertical = false;

			// Set the about of radians we should turn towards the left
			double turnLeft = getHeadingRadians();
	
			// Check if our robot is at the upper or lower border and hence should move horizontally
			if (getY() < borderRange || getY() > getBattleFieldHeight() - borderRange) {
				horizontal = true;
			}
			// Check if our robot is at the left or right border and hence should move vertically
			if (getX() < borderRange || getX() > getBattleFieldWidth() - borderRange) {
				vertical = true;
			}
	
			// If we are in one of the corners of the battlefield, we could move both horizontally or vertically.
			// In this situation, we need to choose one of the two directions.
			if (horizontal && vertical) {
				// If the horizontal distance to our target is lesser than the vertical distance,
				// then we choose to move vertically, and hence we clear the horizontal flag.
				if (Math.abs(target.x - getX()) <= Math.abs(target.y - getY())) {
					horizontal = false; // Do not move horizontally => move vertically
				}
			}
			// Adjust the heading of our robot with 90 degrees, if it must move horizontally.
			// Otherwise the calculated heading is towards moving vertically.
			if (horizontal) {
				turnLeft -= Math.PI / 2;			
			}
			// Set the robot to turn left the amount of radians we have just calculated
			setTurnLeftRadians(Utils.normalRelativeAngle(turnLeft));

			// Check if our robot has finished turning, i.e. has less than 1 degrees left to turn
			if (getTurnRemaining() < 1) {
				// If we should move horizontally, the set the robot to move ahead with
				// the horizontal distance to the target robot. Otherwise, use the vertical distance.
				if (horizontal) {
					setAhead(target.x - getX());
				} else {
					setAhead(target.y - getY());
				}
			}
		}

		// Check if the absolute distance remaining of our current robot move is less than 40 and
		// that at least 25 turns have passed since the last time our robot shifted its direction.
		if (Math.abs(getDistanceRemaining()) < 40 && (getTime() - lastDirectionShift > 25)) {
			// Record the current time as the new 'last direction shift time'
			lastDirectionShift = getTime();
			// Change the direction, which should shift between 1 and -1.
			// Hence we simply multiply the direction variable with -1.
			direction *= -1;
			// Force the robot to move either 100 units forward or back depending on the current direction.
			ahead(100 * direction);
		}		
	}

	/**
	 * This method checks if a point (x, y) is outside of the attack range for a SentryRobot.
	 * @param x is the x coordinate.
	 * @param y is the y coordinate.
	 * @return true if the point is outside the attack range of sentry robots; false otherwise.
	 */
	private boolean isOutsideSentryAttackRange(double x, double y) {
		return (x > getSentryBorderSize() && // minimum border x
				y > getSentryBorderSize() && // minimum border y 
				x < ((int) getBattleFieldWidth() - getSentryBorderSize()) && // maximum border x
				y < ((int) getBattleFieldHeight() - getSentryBorderSize())); // maximum border y
	}

	/**
	 * Returns the distance to a point (x, y) from our robot.
	 * @param x is the x coordinate of the point.
	 * @param y is the y coordinate of the point.
	 * @return the distance to the point from our robot.
	 */
	private double distanceTo(double x, double y) {
		// Calculate the distance to the robot based on the hypotenuse (Pythagoras)
		return Math.hypot(x - getX(), y - getY());
	}
	
	/**
	 * Paints a yellow circle around a point (x, y).
	 * @param x is the x coordinate for the center of the circle.
	 * @param y is the y coordinate for the center of the circle.
	 */
	private void paintCircle(double x, double y) {
		// Get the robot graphics context
		Graphics2D gfx = getGraphics();
		// Set the pen color to the yelow color
		gfx.setColor(Color.YELLOW);
		// Set the stroke of the pen to be a basic solid stroke with a with of 2 pixels
		gfx.setStroke(new BasicStroke(2));
		// Draw a circle (oval) that has a radius of 20 pixels with the center in the input coordinates.
		gfx.drawOval((int) x - 20, (int) y - 20, 40, 40);
	}

	/**
	 * This class is used for storing data about a robot that has been scanned.
	 * The data is a snapshot containing the name of the robot that was scanned at a specific time,
	 * and the location (x, y) of the robot at that time.
	 */
	class ScannedRobotData {
		String name; // name of the scanned robot
		long time; // time when the robot was scanned
		double x; // x coordinate of the robot position 
		double y; // y coordinate of the robot position
	}
}
