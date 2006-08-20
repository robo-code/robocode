package sample;


import java.awt.Color;
import robocode.*;


/**
 * Tracker - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * 
 * Locks onto a robot, moves close, fires when close.
 */
public class Tracker extends Robot {
	int count = 0; // Keeps track of how long we've
	// been searching for our target
	double gunTurnAmt; // How much to turn our gun when searching
	String trackName; // Name of the robot we're currently tracking

	/**
	 * run:  Tracker's main run function
	 */	
	public void run() {
		// Set colors
		setBodyColor(new Color(128, 128, 50));
		setGunColor(new Color(50, 50, 20));
		setRadarColor(new Color(200, 200, 70));
		setScanColor(Color.white);
		setBulletColor(Color.blue);
		
		// Prepare gun
		trackName = null; // Initialize to not tracking anyone
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		gunTurnAmt = 10; // Initialize gunTurn to 10

		// Loop forever
		while (true) {
			// turn the Gun (looks for enemy)
			turnGunRight(gunTurnAmt);
			// Keep track of how long we've been looking
			count++;
			// If we've haven't seen our target for 2 turns, look left
			if (count > 2) {
				gunTurnAmt = -10;
			}
			// If we still haven't seen our target for 5 turns, look right
			if (count > 5) {
				gunTurnAmt = 10;
			}
			// If we *still* haven't seen our target after 10 turns, find another target
			if (count > 11) {
				trackName = null;
			}
		}
	}
	
	/**
	 * onScannedRobot:  Here's the good stuff
	 */	
	public void onScannedRobot(ScannedRobotEvent e) {

		// If we have a target, and this isn't it, return immediately
		// so we can get more ScannedRobotEvents.
		if (trackName != null && !e.getName().equals(trackName)) {
			return;
		}

		// If we don't have a target, well, now we do!
		if (trackName == null) {
			trackName = e.getName();
			out.println("Tracking " + trackName);
		}
		// This is our target.  Reset count (see the run method)
		count = 0;
		// If our target is too far away, turn and move torward it.
		if (e.getDistance() > 150) {
			gunTurnAmt = normalRelativeAngle(e.getBearing() + (getHeading() - getRadarHeading()));
			
			turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
			turnRight(e.getBearing()); // and see how much Tracker improves...
			// (you'll have to make Tracker an AdvancedRobot)
			ahead(e.getDistance() - 140);
			return;
		}

		// Our target is close.
		gunTurnAmt = normalRelativeAngle(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(3);
		
		// Our target is too close!  Back up.
		if (e.getDistance() < 100) {
			if (e.getBearing() > -90 && e.getBearing() <= 90) {
				back(40);
			} else {
				ahead(40);
			}
		}
		scan();
	}
	
	/**
	 * onHitRobot:  Set him as our new target
	 */	
	public void onHitRobot(HitRobotEvent e) {
		// Only print if he's not already our target.
		if (trackName != null && !trackName.equals(e.getName())) {
			out.println("Tracking " + e.getName() + " due to collision");
		}
		// Set the target
		trackName = e.getName();
		// Back up a bit.
		// Note:  We won't get scan events while we're doing this!
		// An AdvancedRobot might use setBack(); execute();
		gunTurnAmt = normalRelativeAngle(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(3);
		back(50);
	}

	/**
	 * onWin:  Do a victory dance
	 */	
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}
	}
	
	// normalAbsoluteAngle is not used in this robot,
	// but is here for reference.
	/**
	 * normalAbsoluteAngle:  Returns angle such that 0 <= angle < 360
	 */	
	public double normalAbsoluteAngle(double angle) {
		if (angle >= 0 && angle < 360) {
			return angle;
		}
		double fixedAngle = angle;

		while (fixedAngle < 0) {
			fixedAngle += 360;
		}
		while (fixedAngle >= 360) {
			fixedAngle -= 360;
		}
		return fixedAngle;
	}

	/**
	 * normalRelativeAngle:  Returns angle such that -180 < angle <= 180
	 */	
	public double normalRelativeAngle(double angle) {
		if (angle > -180 && angle <= 180) {
			return angle;
		}
		double fixedAngle = angle;

		while (fixedAngle <= -180) {
			fixedAngle += 360;
		}
		while (fixedAngle > 180) {
			fixedAngle -= 360;
		}
		return fixedAngle;
	}
}
