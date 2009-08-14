package CTFbots;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import CTFApi.ICaptureTheFlagApi;

import robocode.AdvancedRobot;
import robocode.HitObjectEvent;
import robocode.HitObstacleEvent;
import robocode.robotinterfaces.IObjectEvents;
import robocode.robotinterfaces.IObjectRobot;



public class FlagSeeker extends AdvancedRobot implements IObjectEvents, IObjectRobot{
	
	ICaptureTheFlagApi capApi;
	List<String> team = null;

	Point2D enemyFlag;
	Rectangle2D homeBase;

	Point2D destination;
	int skipGoTo = 20;
	
	public void run() {
		capApi = (ICaptureTheFlagApi)getExtensionApi();
		
		team = capApi.getTeammates(getName());
		homeBase = capApi.getOwnBase(getName());
		enemyFlag = capApi.getEnemyFlag(getName());
		
		destination = new Point2D.Double(enemyFlag.getX(), enemyFlag.getY());
		
		while (true)
		{
			if (skipGoTo > 10)
			{
				goTo(destination);
			}
			else
			{
				skipGoTo++;
			}
			execute();
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
		
		double absBearing = quad +  Math.atan(dy/dx) * -180 / Math.PI;
		double bearing = absBearing - getHeading();
		setTurnRight(bearing);
		setAhead(100);
	}
	
	public void onHitObject(HitObjectEvent event) {
		if (event.getType().equals("flag"))
		{
			destination = new Point2D.Double(homeBase.getX(), homeBase.getY());
			setTurnRight(180);
			setAhead(10);
			skipGoTo = 0;
		}
		else if (event.getType().equals("base") && homeBase.contains(new Point2D.Double(getX(), getY())))
		{
			destination = enemyFlag;
			setTurnRight(180);
			setAhead(10);
			skipGoTo = 0;
		}
	}

	public void onHitObstacle(HitObstacleEvent event) {
	}

	public IObjectEvents getObjectEventListener() {
		return this;
	}

}
