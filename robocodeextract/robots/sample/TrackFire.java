package sample;
import robocode.*;
/**
 * TrackFire - a sample robot by Mathew Nelson
 * 
 * Sits still.  Tracks and fires at the nearest robot it sees
 */
public class TrackFire extends Robot
{
	/**
	 * TrackFire's run method
	 */
	public void run() {
		while (true)
		{
			turnGunRight(10); // Scans automatically
		}
	}
	/**
	 * onScannedRobot:  We have a target.  Go get it.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Calculate exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngle(absoluteBearing - getGunHeading());
		// If it's close enough, fire!
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);
			// We check gun heat here, because calling fire() 
			// uses a turn, which could cause us to lose track
			// of the other robot.
			if (getGunHeat() == 0)
				fire (Math.min(3 - Math.abs(bearingFromGun),getEnergy() - .1));
		}
		// otherwise just set the gun to turn.
		// Note:  This will have no effect until we call scan()
		else {
			turnGunRight(bearingFromGun);
		}
		// Generates another scan event if we see a robot.
		// We only need to call this if the gun (and therefore radar)
		// are not turning.  Otherwise, scan is called automatically.
		if (bearingFromGun == 0)
			scan();
	}
	
	public void onWin(WinEvent e)
	{
		//Victory dance	
		turnRight(36000);
	}
	
	// Helper function
	public double normalRelativeAngle(double angle) {
		if (angle > -180 && angle <= 180)
			return angle;
		double fixedAngle = angle;
		while (fixedAngle <= -180)
			fixedAngle += 360;
		while (fixedAngle > 180)
			fixedAngle -= 360;
		return fixedAngle;
	}

}				