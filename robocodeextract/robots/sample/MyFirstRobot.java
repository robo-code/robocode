package sample;
import robocode.*;
/**
 * MyFirstRobot - a sample robot by Mathew Nelson
 * 
 * Moves in a seesaw motion, and spins the gun around at each end
 */
public class MyFirstRobot extends Robot
{
	/**
	 * MyFirstRobot's run method - Seesaw
	 */
	public void run() {
		
		while (true) {
			ahead(100);			// Move ahead 100
			turnGunRight(360);	// Spin gun around	
			back(100);			// Move back 100
			turnGunRight(360);	// Spin gun around
		}
	}
	
	/**
	 * Fire when we see a robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}
	
	/**
	 * We were hit!  Turn perpendicular to the bullet,
	 * so our seesaw might avoid a future shot.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}
}												