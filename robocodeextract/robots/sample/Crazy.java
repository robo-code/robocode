package sample;


import robocode.*;


/**
 * Crazy - a sample robot by Mathew Nelson
 * 
 * This robot moves around in a crazy pattern
 */
public class Crazy extends AdvancedRobot {
	boolean movingForward;

	/**
	 * run: Crazy's main run function
	 */	
	public void run() {
		while (true) {
			// Tell the game we will want to move ahead 40000 -- some large number
			setAhead(40000);
			movingForward = true;
			// Tell the game we will want to turn right 90
			setTurnRight(90);
			// At this point, we have indicated to the game that *when we do something*, 
			// we will want to move ahead and turn right.  That's what "set" means.
			// It is important to realize we have not done anything yet!
			// In order to actually move, we'll want to call a method that
			// takes real time, such as waitFor.
			// waitFor actually starts the action -- we start moving and turning.
			// It will not return until we have finished turning.
			waitFor(new TurnCompleteCondition(this));
			// Note:  We are still moving ahead now, but the turn is complete.
			// Now we'll turn the other way...
			setTurnLeft(180);
			// ... and wait for the turn to finish ...
			waitFor(new TurnCompleteCondition(this));
			// ... then the other way ...
			setTurnRight(180);
			// .. and wait for that turn to finish.
			waitFor(new TurnCompleteCondition(this));
			// then back to the top to do it all again
		}
	}
	
	/**
	 * onHitWall:  Handle collision with wall.
	 */	
	public void onHitWall(HitWallEvent e) {
		// Bounce off!
		reverseDirection();
	}
	
	/**
	 * reverseDirection: switch from ahead to back & vice versa
	 */	
	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
	}
	
	/**
	 * onScannedRobot:  Fire!
	 */	
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}

	/**
	 * onHitRobot:  Back up!
	 */	
	public void onHitRobot(HitRobotEvent e) {
		// If we're moving the other robot, reverse!
		if (e.isMyFault()) {
			reverseDirection();
		}
	}
}		

