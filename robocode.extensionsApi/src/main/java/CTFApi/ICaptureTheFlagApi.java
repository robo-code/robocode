package CTFApi;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

import robocode.IExtensionApi;

//import extensions.Base;
//import extensions.Flag;


public interface ICaptureTheFlagApi extends IExtensionApi {

	public List<String> getTeammates(String ownName);
	
	public boolean isTeammate(String otherName, String ownName);
	
	public void broadcastMessage(Serializable message, String ownName);
	
	public void sendMessage(String ownName, String otherName, Serializable message);
	
	public Point2D getOwnFlag(String ownName);
	
	public Rectangle2D getOwnBase(String ownName);
	
	public boolean isOwnFlagAtBase(String ownName);
	
	public Point2D getEnemyFlag(String ownName);
	
	public Rectangle2D getEnemyBase(String ownName);
	
	public boolean isEnemyFlagAtBase(String ownName);
}
