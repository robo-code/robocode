package navalsample;

import java.awt.Color;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedShipEvent;
import robocode.Ship;

public class SampleShip extends Ship{
	int direction = 1;
	// This is the code your Ship will run
	public void run(){
		// The following components can all be colored seperately
		// Setup
		setBodyColor(Color.red);
		setFrontCannonColor(Color.orange);
		setBackCannonColor(Color.yellow);
		setRadarColor(Color.green);
		setMineComponentColor(Color.cyan);
		setBulletColor(Color.blue);
		setScanColor(Color.magenta);
		
		//Easy way to make your radar go in circles forever
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		
		// Loop
		while(true){
			//Makes sure we go 4000 units toward a certain direction
			setAhead(4000 * direction);
			//Execute makes sure your commands actually get executed. You could see it
			//as if you're ending your turn.
			//Try removing the execute command. You'll see that that your Ship
			//won't even move! This is because you'll end up saying "Go ahead!" an infinite 
			//amount of times, without ever ending your turn.
			execute();
		}
	}
	// When a wall is hit, reverse direction and make a slight turn.
	public void onHitWall(HitWallEvent event){
		direction *= -1;
		setTurnRightDegrees(45);
	}

	public void onHitRobot(HitRobotEvent event){
		//Fill in something you'd like your Ship to do when it hits another Ship
	}
	
	public void onScannedShip(ScannedShipEvent event){
		// The easiest way to target the scanned ship is to use these functions
		// Turn your front cannon towards the ship
		setTurnFrontCannonRightRadians(event.getBearingFrontRadians());
		//At this point you've already told your ship to move 4000 ahead,
		//AND you've told it to turn its front cannon
		//All you gotta do now is wait for your cannon to reach its destination
		while(getFrontCannonTurnRemainingRadians() != 0){
			execute();
			if(getFrontCannonAtBlindSpot())
				break;
		}
		//If your front cannon is not at its blindspot
		if(!getFrontCannonAtBlindSpot()){
			//Then shoot!
			fireFrontCannon(3);
		}
		//We waited all this time for our cannon to turn and it didn't even reach its target?!
		else{
			//Bomb the place.
			placeMine(5);
		}
	}
}
