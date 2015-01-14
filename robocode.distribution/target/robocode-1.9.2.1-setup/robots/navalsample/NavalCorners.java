package navalsample;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.HitRobotEvent;
import robocode.ScannedShipEvent;
import robocode.Ship;

/**
 * Goes to a wall at a 45 + n*90 degree angle, and
 * scans the area for ships, which it will then 
 * proceed to shoot.
 * @author Thomas
 *
 */
public class NavalCorners extends Ship{
	boolean backwards;

	Point2D.Double[] corners;

	/**
	 * run:  Corners' main run function.
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.red);
		setFrontCannonColor(Color.black);
		setBackCannonColor(Color.black);
		setRadarColor(Color.yellow);
		setBulletColor(Color.green);
		setScanColor(Color.green);
		
		corners = new Point2D.Double[4];
		
		corners[0] = new Point2D.Double(getBattleFieldWidth(), getBattleFieldHeight());
		corners[1] = new Point2D.Double(getBattleFieldWidth(), 0);
		corners[2] = new Point2D.Double(0,0);
		corners[3] = new Point2D.Double(0, getBattleFieldHeight());

		// Move to a corner
		goCorner();

//		// Initialize gun turn speed to 4
		int gunIncrement = 4;
		if(backwards){
			setTurnRadarRightDegrees(getBodyHeadingDegrees() - getRadarHeadingDegrees());
		}
		else{
			setTurnRadarRightDegrees(getBodyHeadingDegrees() - getRadarHeadingDegrees() + 180);
		}
		while(getRadarTurnRemainingDegrees() != 0){
			execute();
		}
		if(backwards){
			for (int i = 0; i < 25; i++) {
				setTurnFrontCannonRightDegrees(gunIncrement);
				setTurnRadarRightDegrees(gunIncrement*0.8);
				execute();
			}
		}
		else{
			for (int i = 0; i < 25; i++) {
				setTurnBackCannonRightDegrees(gunIncrement);
				setTurnRadarRightDegrees(gunIncrement*0.8);
				execute();
			}
		}
		gunIncrement *= -1;
		
		// Spin gun back and forth
		while (true) {
			if(backwards){
				for (int i = 0; i < 50; i++) {
					setTurnFrontCannonRightDegrees(gunIncrement);
					setTurnRadarRightDegrees(gunIncrement*0.8);
					execute();
				}
			}
			else{
				for (int i = 0; i < 50; i++) {
					setTurnBackCannonRightDegrees(gunIncrement);
					setTurnRadarRightDegrees(gunIncrement*0.8);
					execute();
				}
			}
			gunIncrement *= -1;
		}
	}

	/**
	 * goCorner:  A very inefficient way to get to a corner.  Can you do better?
	 */
	public void goCorner() {
		//Get the easiest corner to go to
		int corner = (int)(getBodyHeadingRadians() / (Math.PI/2));
		
		
		setTurnRightDegrees(normalRelativeAngleDegrees((corner*90 + 45)- getBodyHeadingDegrees()));
		
		backwards = getBackwards(corner);
		if(backwards){
			setBack(5000);
		}
		else{
			setAhead(5000);
		}	
	}

	//true if backwards; false if not
	public boolean getBackwards(int corner){
		int cornerBackward = (corner + 2)%4;
		
		return getDistance(corner) > getDistance(cornerBackward);
	}
	
	public double getDistance(int corner){
		double a = getX() - corners[corner].getX();
		double b = getY() - corners[corner].getY();
		return Math.sqrt(a*a + b*b);
	}
	
	/**
	 * onScannedShip:  Stop and fire!
	 */
	public void onScannedShip(ScannedShipEvent e) {
		if(backwards){
			setTurnFrontCannonRightDegrees(e.getBearingFront());
		}
		else{
			setTurnBackCannonRightDegrees(e.getBearingBack());
		}
		smartFire(e.getDistance());
		
	}
	
	public void onHitRobot(HitRobotEvent e){
		if(backwards)
			fireBackCannon(3);
		else
			fireFrontCannon(3);
	}

	/**
	 * smartFire:  Custom fire method that determines firepower based on distance.
	 *
	 * @param robotDistance the distance to the robot to fire at
	 */
	public void smartFire(double robotDistance) {
		if (robotDistance > 600 || getEnergy() < 15) {
			if(!backwards)
				fireBackCannon(1);
			else
				fireFrontCannon(1);
		} else if (robotDistance > 300) {
			if(!backwards)
				fireBackCannon(2);
			else
				fireFrontCannon(2);
		} else {
			if(!backwards)
				fireBackCannon(3);
			else
				fireFrontCannon(3);
		}
	}
}
