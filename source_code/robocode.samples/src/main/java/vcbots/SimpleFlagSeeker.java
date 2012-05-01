package vcbots;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import robocode.HitByBulletEvent;
import robocode.HitObjectEvent;
import robocode.HitObstacleEvent;
import robocode.HitWallEvent;
import robocode.ScannedObjectEvent;
import robocode.ScannedRobotEvent;
import CTFApi.CaptureTheFlagApi;

public class SimpleFlagSeeker extends CaptureTheFlagApi{
	
	/**
	 * Note that CaptureTheFlagApi inherits TeamRobot, so users can directly use functions of TeamRobot.
	 */
	
	String[] myteam;

	Point2D enemyFlag;
	Rectangle2D homeBase;
	
	boolean flagCaptured = false;

	Point2D destination;
	int skipGoTo = 20;
	
	public void run() {
		
		/*
		 * registerMe() needs to be called for every robot. Used for initialization. 
		 **/
		registerMe(); 
		
		//write your logic here
		
		while (true) {
			UpdateBattlefieldState(getBattlefieldState()); 
			myteam = getTeammates();
			homeBase = getOwnBase();
			enemyFlag = getEnemyFlag();
			System.out.println("UpdateBattlefieldState done");
			System.out.println("enemyFlag::"+enemyFlag.toString());
			System.out.println("UpdateBattlefieldState done");
			
			if (!flagCaptured)
				destination = enemyFlag;
			else 
				destination = new Point2D.Double(homeBase.getCenterX(), homeBase.getCenterY());
			
			goTo(destination);
			execute();
		}
	}
	
	public void goTo(Point2D point)	{
		// Logic to go to a particular point.
	}
	
	public void onHitObject(HitObjectEvent event) {
		if (event.getType().equals("flag") && enemyFlag.distance(getX(), getY()) < 50)
		{
			flagCaptured = true;
			skipGoTo = 0;
		}
		else if (event.getType().equals("base") && homeBase.contains(new Point2D.Double(getX(), getY())))
		{
			flagCaptured = false;
			skipGoTo = 0;
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}
	
	public void onHitByBullet(HitByBulletEvent e) {
		// Turn to confuse the other robot
		setTurnRight(5);
	}
	
	public void onHitObstacle(HitObstacleEvent e) {
		
		back(20);
		turnRight(30);
		ahead(30);
	}
	
	public void onHitWall(HitWallEvent e) {
		// Move away from the wal
		back(20);
		turnRight(30);
		ahead(30);
	}

	public void onScannedObject(ScannedObjectEvent e) {
		if (e.getObjectType().equals("flag")) {
			e.getBearing();
		}
	}
	

}