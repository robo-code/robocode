package navalsample;

import java.awt.Color;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedShipEvent;
import robocode.Ship;
import robocode.util.Utils;

/**
 * Spin around in backward circles until a target is
 * found, after which it attempt to turn towards it,
 * ram it and place mines on the other ship.
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public class RamMiner extends Ship{
	boolean hitWall= false;

	/**
	 * run: Spin around looking for a target
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.lightGray);
		setFrontCannonColor(Color.gray);
		setBackCannonColor(Color.gray);
		setRadarColor(Color.darkGray);
		setMineComponentColor(Color.yellow);
		
		setTurnRadarRightDegrees(getBodyHeadingDegrees() - getRadarHeadingDegrees() + 180);
		
		while (true) {
			setTurnRightDegrees(10);
			setBack(50);
			execute();
		}
	}

	/**
	 * onScannedShip:  We have a target.  Go get it.
	 */
	public void onScannedShip(ScannedShipEvent e) {
		setTurnRightRadians(Utils.normalRelativeAngle(e.getBearingRadians() - Math.PI));
		System.out.println("Test! " + Math.toDegrees(e.getBearingRadians()));
		setBack(e.getDistance() + 5);
		scan(); // Might want to move ahead again!
	}

	/**
	 * onHitRobot:  Turn to face ship, fire hard, and ram him again!
	 */
	public void onHitRobot(HitRobotEvent e) {
		if(hitWall){
			if (e.getEnergy() > 16) {
				fireFrontCannon(3);
			} else if (e.getEnergy() > 10) {
				fireFrontCannon(2);
			} else if (e.getEnergy() > 4) {
				fireFrontCannon(1);
			} else if (e.getEnergy() > 2) {
				fireFrontCannon(.5);
			} else if (e.getEnergy() > .4) {
				fireFrontCannon(.1);
			}
		}
		else{
			setTurnRightDegrees(e.getBearing());
	
			// Determine a shot that won't kill the robot...
			// We want to ram him instead for bonus points
			if (e.getEnergy() > 70) {
				placeMine(15);
			} else if (e.getEnergy() > 50) {
				placeMine(12);
			} else if (e.getEnergy() > 35) {
				placeMine(10);
			} else if (e.getEnergy() > 20) {
				fireBackCannon(3);
			} else if (e.getEnergy() > 10) {
				fireBackCannon(1);
			}else{
				fireBackCannon(0.1);
			}
			setBack(40); // Ram him again!
		}
	}
	
	public void onHitWall(HitWallEvent e){
		if(hitWall){
			hitWall = true;
			setAhead(-50);
			while(getDistanceRemaining() != 0){
				execute();
			}
			hitWall = false;
		}
		else{
			hitWall = true;
			setAhead(100);
			while(getDistanceRemaining() != 0){
				execute();
			}
			hitWall = false;
		}
		
	}
}
