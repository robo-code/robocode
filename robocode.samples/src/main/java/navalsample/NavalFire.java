package navalsample;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedShipEvent;
import robocode.Ship;

/**
 * 				Sample Ship
 * Same as {@link NavalFireSolo}. Only difference is that this version has some
 * kind of laziness implemented. Which means that if the target is too far a way
 * for the weapon, then the weapon won't turn.
 * @author Thales B.V. / Thomas Hakkers
 */
public class NavalFire extends Ship{
	int dist = 200; // distance to move when we're hit
	int imprecission = 10;	//How imprecise the guns are. 
	int laziness = 80;	//Distance it wants to move its guns. The Ship wants to shoot on every target out there. If we don't do this, the guns will just go back and forth trying to reach the enemy of the previous event.

	/**
	 * run:  NavalFire's main run function
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.orange);
		setFrontCannonColor(Color.orange);
		setBackCannonColor(Color.orange);
		setRadarColor(Color.red);
		setMineComponentColor(Color.red);
		setScanColor(Color.red);
		setBulletColor(Color.red);

		// Spin the radar around ... forever
		
		setTurnRadarRightDegrees(Double.POSITIVE_INFINITY);
		execute();
		
	}

	/**
	 * onScannedShip: Turn weapons towards it and fire!
	 */
	public void onScannedShip(ScannedShipEvent e) {
		if(Math.abs(e.getBearingFront()) < laziness){
			setTurnFrontCannonRightDegrees(e.getBearingFront());
			//Test whether the enemy Ship is in gun range.
			//FRONT
			if (Math.abs(getFrontCannonTurnRemainingDegrees()) < imprecission) {
				if(e.getDistance() < 200 && getEnergy() > 50)
					fireFrontCannon(3);
				else
					fireFrontCannon(2);
			} 
		}
		else if(Math.abs(e.getBearingBack()) < laziness){
			setTurnBackCannonRightDegrees(e.getBearingBack());
			//BACK
			if (Math.abs(getBackCannonTurnRemainingDegrees()) < imprecission) {
				if(e.getDistance() < 200 && getEnergy() > 50)
					fireBackCannon(3);
				else
					fireBackCannon(2);
			} 
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
