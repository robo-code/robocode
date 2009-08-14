package CTF;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

import CTFApi.ICaptureTheFlagApi;


public class CaptureTheFlagExtensionApi implements ICaptureTheFlagApi {

	CaptureTheFlagRules rules;
	
	public CaptureTheFlagExtensionApi(CaptureTheFlagRules rules)
	{
		this.rules = rules;
	}

	public boolean isTeammate(String otherName, String ownName) {
		List<List<String>> teams = rules.getTeams();
		for (List<String> team : teams)
		{
			if (team.contains(ownName) && team.contains(otherName))
			{
				return true;
			}
		}
		return false;
	}
	
	public List<String> getTeammates(String ownName)
	{
		List<List<String>> teams = rules.getTeams();
		for (List<String> team : teams)
		{
			if (team.contains(ownName))
			{
				return team;
			}
		}
		return null;
	}
	
	public void broadcastMessage(Serializable message, String ownName) {
		rules.broadcastMessage(message, ownName, null);
	}

	public void sendMessage(String ownName, String otherName, Serializable message) {
		rules.broadcastMessage(message, ownName, otherName);
	}

	
	
	public Rectangle2D getOwnBase(String ownName) {
		BasePeer base = rules.getOwnBase(ownName);
		return base.getBoundaryRect();
	}
	
	public Point2D getOwnFlag(String ownName)
	{
		FlagPeer flag = rules.getOwnFlag(ownName);
		return new Point2D.Double(flag.getX(), flag.getY());
	}
	
	public Rectangle2D getEnemyBase(String ownName) {
		BasePeer base = rules.getEnemyBase(ownName);
		return base.getBoundaryRect();
	}
	
	public Point2D getEnemyFlag(String ownName)
	{
		FlagPeer flag = rules.getEnemyFlag(ownName);
		return new Point2D.Double(flag.getX(), flag.getY());
	}
	
	public boolean isEnemyFlagAtBase(String ownName)
	{
		FlagPeer flag = rules.getEnemyFlag(ownName);
		BasePeer base = rules.getEnemyBase(ownName);
		
		return base.getBoundaryRect().contains(flag.getBoundaryRect());
	}

	public boolean isOwnFlagAtBase(String ownName) {

		FlagPeer flag = rules.getOwnFlag(ownName);
		BasePeer base = rules.getOwnBase(ownName);
		
		return base.getBoundaryRect().contains(flag.getBoundaryRect());
	}
}
