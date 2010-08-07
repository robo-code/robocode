package CTFbots;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import CTFApi.CaptureTheFlagApi;
import CTFApi.ICaptureTheFlagApi;

import robocode.AdvancedRobot;
import robocode.HitObjectEvent;
import robocode.HitObstacleEvent;
import robocode.robotinterfaces.IObjectEvents;
import robocode.robotinterfaces.IObjectRobot;



public class FlagSeeker extends AdvancedRobot implements IObjectEvents, IObjectRobot{
	
	CaptureTheFlagApi capApi;
	List<String> team = null;

	Point2D enemyFlag;
	Rectangle2D homeBase;
	
	boolean getEnemyFlag;

	Point2D destination;
	int skipGoTo = 20;
	
	public void run() {
		capApi = new CaptureTheFlagApi(this.getName());
		getEnemyFlag = true;
		
		
		while (true)
		{
			capApi.UpdateBattlefieldState(getBattlefieldState()); 
			
			team = capApi.getTeammates();
			homeBase = capApi.getOwnBase();
			enemyFlag = capApi.getEnemyFlag();

			if (homeBase == null)
			{
				//ExecResults (and therefore capApi) will not contain any information until after 
				//the first turn. For this reason, the first turn is skipped.
				
				execute();
			}
			else
			{
				if (enemyFlag.distance(getX(), getY()) < 50)
				{
					getEnemyFlag = false;
				}
				if (getEnemyFlag)
				{
					destination = enemyFlag;
				}
				else 
				{
					destination = new Point2D.Double(homeBase.getCenterX(), homeBase.getCenterY());
				}
				
//				if (skipGoTo > 10)
//				{
					goTo(destination);
//				}
//				else
//				{
//					skipGoTo++;
//				}
				execute();
			}
		}
	}
	
	public void goTo(Point2D point)	{
		double dx = point.getX() - getX();
		double dy = point.getY() - getY();
		int quad = 90;
		if (dx < 0 )
		{
			quad = -90;
		}
		
		double absBearing = quad - Math.atan(dy/dx) * 180 / Math.PI;
		double bearing = absBearing - getHeading();
		while (bearing > 180)
		{
			bearing -= 360;
		}
		while (bearing < -180)
		{
			bearing += 360;
		}
		setTurnRight(bearing);
		setAhead(100);
	}
	
	public void onHitObject(HitObjectEvent event) {
		if (event.getType().equals("flag") && enemyFlag.distance(getX(), getY()) < 50)
		{
			getEnemyFlag = false;
			skipGoTo = 0;
		}
		else if (event.getType().equals("base") && homeBase.contains(new Point2D.Double(getX(), getY())))
		{
			getEnemyFlag = true;
			skipGoTo = 0;
		}
	}

	public void onHitObstacle(HitObstacleEvent event) {
	}

	public IObjectEvents getObjectEventListener() {
		return this;
	}

}
