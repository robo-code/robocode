package sample;


import robocode.*;


/**
 * MyFirstJuniorRobot - a sample robot by Flemming N. Larsen
 * 
 * Moves in a seesaw motion, and spins the gun around at each end
 * when it cannot see any enemy robot. When the robot sees and enemy
 * robot, it will immediately turn the gun and fire at it.
 */
public class MyFirstJuniorRobot extends JuniorRobot {

	/**
	 * MyFirstJuniorRobot's run method - Seesaw as default
	 */
	public void run() {
		// Set robot colors
		setColors(green, black, blue);		

		// Seesaw forever
		while (true) {
			ahead(100); // Move ahead 100
			turnGunRight(360); // Spin gun around
			back(100); // Move back 100
			turnGunRight(360); // Spin gun around
		}
	}

	/**
	 * When we see a robot, turn the gun towards it and fire
	 */
	public void onScannedRobot() {
		// Turn gun to point at the scanned robot
		turnGunTo(scannedAngle);

		// Fire!
		fire(1);
	}

	/**
	 * We were hit!  Turn and move perpendicular to the bullet,
	 * so our seesaw might avoid a future shot.
	 */
	public void onHitByBullet() {
		// Move ahead 100 and in the same time turn left papendicular to the bullet
		turnAheadLeft(100, 90 - hitByBulletBearing);
	}
}
