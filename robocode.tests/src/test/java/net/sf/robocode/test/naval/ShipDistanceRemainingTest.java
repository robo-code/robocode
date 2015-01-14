package net.sf.robocode.test.naval;

import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.IShipSnapshot;

/**
 * Tests whether getDistanceRemaining works properly for Ships
 * @author Thales B.V. / Thomas Hakkers
 */
public class ShipDistanceRemainingTest extends RobocodeTestBed{

	@Override
	public String getRobotNames() {
		return "tested.ships.ShipDistanceRemaining,tested.ships.SittingDuck";        
	}

	@Override
	public String getInitialPositions() {
		return "(300,300,0), (50,50,0)"; // Make sure the robots do not collide!
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		if(event.getTurnSnapshot().getTurn() == 27 || event.getTurnSnapshot().getTurn() == 65){
			IRobotSnapshot robotSnappy = event.getTurnSnapshot().getRobots()[0];
			if(robotSnappy instanceof IShipSnapshot){
				IShipSnapshot shipSnappy = (IShipSnapshot)robotSnappy;
				if(event.getTurnSnapshot().getTurn() == 27)
					Assert.assertTrue("Testing if the y value is reached   Y: " + shipSnappy.getY(), shipSnappy.getY() == 150.0);
				if(event.getTurnSnapshot().getTurn() == 65)
					Assert.assertTrue("Testing if the y value is reached   Y: " + shipSnappy.getY(), shipSnappy.getY() == 350.0);
			}
			else{
				System.err.println("NOT INSTANCE OF SHIPSNAPSHOT");
			}
		}
	}
}
