package net.sf.robocode.test.naval;


import static org.junit.Assert.*;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.IShipSnapshot;
import robocode.naval.NavalRules;
import robocode.util.Utils;
import net.sf.robocode.test.helpers.RobocodeTestBed;

public class ScannedShipEventTest extends RobocodeTestBed{
	
	private static final double radarShipX = 300;
	private static final double radarShipY = 300;
	private static final double radarShipHeading = 0;
	
	private static final double duckyX = 450;
	private static final double duckyY = 300;
	private static final double duckyHeading = 0;
	
	private static final double predictedBearingBack = -131.5623350076348;
	private static final double predictedBearingFront = 96.08852815419517;
	private static final double predictedBearing = 88.8542371618249;
	
	public String getRobotNames() {
		return "tested.ships.RadarEventShip,tested.ships.SittingDuck";
	}
	
	@Override
	public String getInitialPositions() {
		return "("+radarShipX+","+radarShipY+","+radarShipHeading+"), ("+ duckyX+","+duckyY+","+duckyHeading+")"; 
	}

	//Bearing = amount you need to turn to reach the target
	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot robotSnappy = event.getTurnSnapshot().getRobots()[0];
		if(robotSnappy instanceof IShipSnapshot){
			IShipSnapshot shipSnappy = (IShipSnapshot)robotSnappy;
			String output = shipSnappy.getOutputStreamSnapshot();
			String[] strings = output.split("~");
			if(strings.length > 2){
				System.out.println("bB: " + strings[0] + "  fB: " + strings[1] + "  rB: " + strings[2]);
				
				assertTrue("Test", Utils.isNear(Double.parseDouble(strings[0]), predictedBearingBack));
				assertTrue("Test", Utils.isNear(Double.parseDouble(strings[1]), predictedBearingFront));
				assertTrue("Test", Utils.isNear(Double.parseDouble(strings[2]), predictedBearing));

			}
		}
		else{
			System.err.println("NOT INSTANCE OF SHIPSNAPSHOT");
		}
		
	}
	
	/**
	 * Returns the X value the middle of the Ship has.
	 * The Ship uses a pivot which is not equal to the middle.
	 * @return The X value of the middle of the Ship.
	 */
	protected double getXMiddle(double x, double heading){
		return x + (NavalRules.PROW_OFFSET * Math.cos(heading + Math.PI/2));
	}
	/**
	 * Returns the Y value the middle of the Ship has.
	 * The Ship uses a pivot which is not equal to the middle.
	 * @return The Y value of the middle of the Ship.
	 */
	protected double getYMiddle(double y, double heading){
		return y + (NavalRules.PROW_OFFSET * Math.sin(heading + Math.PI/2));
	}
}
