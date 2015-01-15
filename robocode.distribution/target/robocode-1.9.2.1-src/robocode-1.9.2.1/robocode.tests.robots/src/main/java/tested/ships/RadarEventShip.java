package tested.ships;

import robocode.ScannedShipEvent;
import robocode.Ship;

/**
 * Used to test the Radar on a ship
 * Prints out the event values when it scans a ship
 * @author Thomas
 *
 */
public class RadarEventShip extends Ship{
	public void run(){
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		while(true){
			execute();
		}
	}
	
	
	public void onScannedShip(ScannedShipEvent event){
		//Use a seperator that isn't used anywhere else
		System.out.print(event.getBearingBack() + "~" + event.getBearingFront() + "~" + Math.toDegrees(event.getBearingRadians()));
	}
}
