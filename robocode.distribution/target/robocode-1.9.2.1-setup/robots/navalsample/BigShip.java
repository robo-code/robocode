package navalsample;

import java.awt.Color;

import robocode.ScannedShipEvent;
import robocode.Ship;
import robocode.util.Utils;

/**
 * Stands still and kills stuff with big bullets.
 * @author Thomas
 *
 */
public class BigShip extends Ship{
	int directionFrontCannon = 1;
	int directionBackCannon = 1;
	double speed = 4;
	double radarHeading;
	
	public void run(){
		setBodyColor(Color.black);
		setFrontCannonColor(Color.yellow);
		setBackCannonColor(Color.yellow);
		setRadarColor(Color.yellow);
		setMineComponentColor(Color.yellow);
		setBulletColor(Color.yellow);
		setScanColor(Color.red);
		
		setTurnFrontCannonRightDegrees(90);
		setTurnBackCannonLeftDegrees(90);
		setTurnRadarRightDegrees(90);
		while(getBackCannonTurnRemainingRadians() != 0 
				&& getFrontCannonTurnRemainingRadians() != 0 
				&& getRadarTurnRemainingRadians() != 0){
			
			execute();
		}
		
		
		
		while(true){
			radarHeading = Utils.normalAbsoluteAngleDegrees(getBodyHeadingDegrees() -getRadarHeadingDegrees());
			if(radarHeading > 90 && radarHeading < 270){
				setTurnFrontCannonLeftDegrees(speed);
				setTurnBackCannonRightDegrees(speed);
			}
			else{
				setTurnFrontCannonRightDegrees(speed);
				setTurnBackCannonLeftDegrees(speed);
			}
			setTurnRadarRightDegrees(speed);
			execute();
		}
		
	}
	@Override
	public void onScannedShip(ScannedShipEvent e){
		setTurnFrontCannonRightRadians(e.getBearingFrontRadians());
		setTurnBackCannonRightRadians(e.getBearingBackRadians());
		while(getFrontCannonTurnRemainingRadians() != 0 && getBackCannonTurnRemainingRadians() !=0){
			execute();
			if(getFrontCannonAtBlindSpot() || getBackCannonAtBlindSpot())
				break;
		}
		if(!getFrontCannonAtBlindSpot())
			fireFrontCannon(3);
		if(!getBackCannonAtBlindSpot())
			fireBackCannon(3);
	}
}
