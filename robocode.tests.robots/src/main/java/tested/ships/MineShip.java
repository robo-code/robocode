package tested.ships;

import robocode.Ship;

public class MineShip extends Ship{
	public void run(){
		System.out.println("Mine placed: " + placeMine(15));
	}
}
