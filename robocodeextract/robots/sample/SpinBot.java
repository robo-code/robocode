package sample;
import robocode.*;
/**
 * SpinBot - a sample robot by Mathew Nelson
 * 
 * Moves in a circle, firing hard when an enemy is detected
 */
public class SpinBot extends AdvancedRobot {

	/**
	 * SpinBot's run method - Circle
	 */
	public void run() {
	  	while (true) {
			// Tell the game that when we take move,
			//  we'll also want to turn right... a lot.
			setTurnRight(10000);
			// Limit our speed to 5
			setMaxVelocity(5);
			// Start moving (and turning)
			ahead(10000);
			// Repeat.
	  	} 
	}
	
	/**
	 * onScannedRobot: Fire hard!
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
	  fire(3);
	}
	
	/**
	 * onHitRobot:  If it's our fault, we'll stop turning and moving,
	 *              so we need to turn again to keep spinning.
	 */
	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() > -10 && e.getBearing() < 10)
			fire(3);
		if (e.isMyFault())
			turnRight(10);
	}
}
						