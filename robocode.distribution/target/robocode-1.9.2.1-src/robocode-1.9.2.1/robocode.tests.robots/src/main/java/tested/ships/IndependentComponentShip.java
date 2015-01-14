package tested.ships;


import robocode.HitWallEvent;
import robocode.Ship;
import robocode.naval.NavalRules;

public class IndependentComponentShip extends Ship{
	int direction = 1;
	int i = 0;
	@Override
	public void run() {
		//Make sure every component moves independently
		setAdjustComponentForShipTurn(NavalRules.IDX_CENTRAL_RADAR, true);
		setAdjustComponentForShipTurn(NavalRules.IDX_WEAPON_FRONT, true);	
		setAdjustComponentForShipTurn(NavalRules.IDX_WEAPON_BACK, true);
		setTurnRightRadians(Double.POSITIVE_INFINITY);
				
		while (true) {
			System.out.print(i + ": " + getRadarHeadingDegrees());
			setTurnRightDegrees(90);
			setAhead(50*direction);
			execute();
		}
	}
	
	@Override
	public void onHitWall(HitWallEvent e){
		direction *= -1;
	}
}
