package tested.ships;

import robocode.Ship;

public class ShipDistanceRemaining extends Ship{
	public void run(){
		setAhead(150);
		while(getDistanceRemaining() != 0)
			execute();
		
		setBack(200);
	}
}
