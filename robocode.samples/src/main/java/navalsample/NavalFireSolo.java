package navalsample;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedShipEvent;
import robocode.Ship;

/**
 * 				Sample Ship
 * Works better in a 1v1 scenario
 * Turn its radar around forever. When it senses a target, it'll
 * try to turn its weapons towards it and shoot. 
 * When the NavalFireSolo is hit, it'll move back or forward to
 * try and dodge the next bullet.
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public class NavalFireSolo extends Ship{
	int dist = 200; // distance to move when we're hit
	int imprecission = 5;	//How imprecise the guns are. 

	/**
	 * run:  NavalFireSolo's main run function
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.red);
		setFrontCannonColor(Color.red);
		setBackCannonColor(Color.red);
		setRadarColor(Color.orange);
		setMineComponentColor(Color.orange);
		setScanColor(Color.orange);
		setBulletColor(Color.orange);

		// Spin the radar around ... forever
		
		setTurnRadarRightDegrees(Double.POSITIVE_INFINITY);
		execute();
		
	}

	/**
	 * onScannedShip: Turn weapons towards it and fire!
	 */
	public void onScannedShip(ScannedShipEvent e) {
		setTurnFrontCannonRightDegrees(e.getBearingFront());
		System.out.println("Test! Front: " + e.getBearingFront());
		//Test whether the enemy Ship is in gun range.
		//FRONT
		if (Math.abs(getFrontCannonTurnRemainingDegrees()) < imprecission) {
			if(e.getDistance() < 200 && getEnergy() > 50)
				fireFrontCannon(3);
			else
				fireFrontCannon(2);
		} 
		setTurnBackCannonRightDegrees(e.getBearingBack());
		System.out.println("Test! Back: " + e.getBearingBack());
		//BACK
		if (Math.abs(getBackCannonTurnRemainingDegrees()) < imprecission) {
			if(e.getDistance() < 200 && getEnergy() > 50 || getOthers() == 1)
				fireBackCannon(3);
			else
				fireBackCannon(2);
		}
	}

	/**
	 * onHitByBullet:  Move almost a full length of a Ship ahead or backward
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		setTurnRightDegrees(normalRelativeAngleDegrees(90 - (getBodyHeadingDegrees() - e.getHeading())));

		setAhead(dist);
		dist *= -1;
	}

	/**
	 * onHitRobot:  Aim at it.  Fire Hard!
	 */
	public void onHitRobot(HitRobotEvent e) {
		double turnGunAmtFront = normalRelativeAngleDegrees(e.getBearing() + getBodyHeadingDegrees() - getFrontCannonHeadingDegrees());
		setTurnFrontCannonRightDegrees(turnGunAmtFront);
		//fire immediately
		fireFrontCannon(3);
		
		double turnGunAmtBack = normalRelativeAngleDegrees(e.getBearing() + getBodyHeadingDegrees() - getBackCannonHeadingDegrees());
		setTurnBackCannonRightDegrees(turnGunAmtBack);
		//fire immediately
		fireBackCannon(3);
	}
	
}
