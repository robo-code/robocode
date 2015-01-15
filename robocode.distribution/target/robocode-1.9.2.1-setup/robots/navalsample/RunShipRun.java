package navalsample;

import java.awt.Color;

import robocode.HitWallEvent;
import robocode.ScannedShipEvent;
import robocode.Ship;
import robocode.util.Utils;


/**
 * Runs/sails away from every Ship out there.
 * Good against Rammers (Hopefully)
 * Shows off how a track-radar works, which is like so:
 * setTurnRadarRightRadians(Utils.normalRelativeAngle(event.getBearingRadarRadians() + getBodyHeadingRadians() - getRadarHeadingRadians()));
 * @author Thomas
 *
 */
public class RunShipRun extends Ship{
	int dangerLevel = 0;
	
	public void run(){
		Color brown = new Color(200, 200, 50);
		setBodyColor(Color.green);
		setFrontCannonColor(brown);
		setBackCannonColor(brown);
		setRadarColor(brown);
		setBulletColor(Color.green);
		setScanColor(Color.green);
		
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		
		while(true){
			if(Math.abs(getBackCannonTurnRemainingRadians()) < 5){
				fireBackCannon(3);
				placeMine(5);
			}
			execute();
		}
	}
	
	@Override
	public void onScannedShip(ScannedShipEvent event){
		double absoluteBearing = event.getBearingRadians() + getBodyHeadingRadians();
		setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
		setTurnRightRadians(Utils.normalRelativeAngle(- event.getBearingRadians()));
		setAhead(150);
		setTurnBackCannonRightRadians(event.getBearingBackRadians());
	}
	
	public void onHitWall(HitWallEvent event){
		setTurnLeftDegrees(20);
		setBack(50);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		while(getDistanceRemaining() != 0){
			execute();
		}
	}
}
