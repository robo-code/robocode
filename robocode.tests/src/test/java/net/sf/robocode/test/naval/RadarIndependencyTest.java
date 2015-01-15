package net.sf.robocode.test.naval;


import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.IShipSnapshot;
import robocode.naval.NavalRules;
import robocode.util.Utils;
import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;

/**
 * Tests whether the radar can move independently from the ship.
 * @author Thales B.V./ Thomas Hakkers
 *
 */
public class RadarIndependencyTest extends RobocodeTestBed{

	double firstRadarHeading;
	boolean firstTime = true;
	
	@Override
	public String getRobotNames() {
		return "tested.ships.IndependentComponentShip,tested.ships.SittingDuck";        
	}

	@Override
	public String getInitialPositions() {
		return "(0,0,0), (500,500,0)"; // Make sure the robots do not collide!
	}
	

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot robotSnappy = event.getTurnSnapshot().getRobots()[0];
		if(robotSnappy instanceof IShipSnapshot){
			IShipSnapshot shipSnappy = (IShipSnapshot)robotSnappy;
			double radarHeading = shipSnappy.getComponents().get(NavalRules.IDX_CENTRAL_RADAR).getAngle();
			radarHeading = Utils.normalAbsoluteAngle(radarHeading + shipSnappy.getBodyHeading());
			if(firstTime){
				firstTime = false;
				firstRadarHeading = radarHeading;
			}
			else{
				System.out.println("RadarHeading: " + radarHeading + " = " + firstRadarHeading + " State: " + shipSnappy.getState());
				Assert.assertTrue("Testing if " + radarHeading + " is the correct value", isPrettyNear(radarHeading, firstRadarHeading));
			}
			
		}
		else{
			System.err.println("NOT INSTANCE OF SHIPSNAPSHOT");
		}
	}	
	
	public boolean isPrettyNear(double heading1, double heading2){
		return Math.abs(heading1 - heading2) < 0.05;
	}
}
