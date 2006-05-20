package sample;


import robocode.*;


/**
 * Fire - a sample robot by Mathew Nelson
 * 
 * Sits still.  Spins gun around.  Moves when hit.
 */
public class Fire extends Robot {
	int dist = 50; // distance to move when we're hit
	
	/**
	 * run: Fire's main run function
	 */	
	public void run() {
		// Spin the gun around slowly... forever
		while (true) {
			turnGunRight(5);
		}
	}
	
	/**
	 * onScannedRobot: Fire!
	 */	
	public void onScannedRobot(ScannedRobotEvent e) {
		// If the other robot is close by, and we have plenty of life,
		// fire hard!
		if (e.getDistance() < 50 && getEnergy() > 50) {
			fire(3);
		} // otherwise, fire 1.
		else {
			fire(1);
		}
		// Call scan again, before we turn the gun
		scan();
	}
	
	/**
	 * onHitByBullet:  Turn perpendicular to the bullet, and move a bit.
	 */	
	public void onHitByBullet(HitByBulletEvent e) {
		turnRight(normalRelativeAngle(90 - (getHeading() - e.getHeading())));
		
		ahead(dist);
		dist *= -1;
		scan();
	}
	
	/**
	 * onHitRobot:  Aim at it.  Fire Hard!
	 */	
	public void onHitRobot(HitRobotEvent e) {
		double turnGunAmt = normalRelativeAngle(e.getBearing() + getHeading() - getGunHeading());

		turnGunRight(turnGunAmt);
		fire(3);
	}
	
	/**
	 * normalRelativeAngle:  returns angle such that -180<angle<=180
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

