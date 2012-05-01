package CTFApi;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

import robocode.IExtensionApi;


public interface ICaptureTheFlagApi extends IExtensionApi {

	//public List<String> getTeammates();
	
	public boolean isTeammate(String otherName);
	
	public Point2D getOwnFlag();
	
	public Rectangle2D getOwnBase();
	
	public boolean isOwnFlagAtBase();
	
	public Point2D getEnemyFlag();
	
	public Rectangle2D getEnemyBase();
	
	public boolean isEnemyFlagAtBase();
}
