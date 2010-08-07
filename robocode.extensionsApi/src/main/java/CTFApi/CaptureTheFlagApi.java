package CTFApi;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import CTF.BasePeer;
import CTF.CaptureTheFlagRules;
import CTF.FlagPeer;


public class CaptureTheFlagApi implements ICaptureTheFlagApi {

	String ownName;
	List<List<String>> teams;
	int ownTeam;
	Rectangle2D ownBase;
	Rectangle2D enemyBase;
	Point2D ownFlag;
	Point2D enemyFlag;
	boolean firstUpdate = true;
	
	public CaptureTheFlagApi(String ownName)
	{
		this.ownName = ownName;
	}

	public boolean isTeammate(String otherName) {
		if (teams != null)
		{
			return teams.get(ownTeam).contains(otherName);
		}
		return false;
	}
	
	public List<String> getTeammates()
	{
		if (teams != null)
		{
			return teams.get(ownTeam);
		}
		return null;
	}
	
	public Rectangle2D getOwnBase() {
		return ownBase;
	}
	
	public Point2D getOwnFlag()
	{
		return ownFlag;
	}
	
	public Rectangle2D getEnemyBase() {
		return enemyBase;
	}
	
	public Point2D getEnemyFlag()
	{
		return enemyFlag;
	}
	
	public boolean isEnemyFlagAtBase()
	{		
		if (enemyBase == null)
		{
			return false;
		}
		return enemyBase.contains(enemyFlag);
	}

	public boolean isOwnFlagAtBase() 
	{
		if (ownBase == null)
		{
			return false;
		}
		return ownBase.contains(ownFlag);
	}

	public void UpdateBattlefieldState(List<String> state) 
	{		
		int stateIndex = 0;
		int firstTeamSize = Integer.valueOf(state.get(stateIndex));	
		stateIndex++;
		List<String> firstTeam = new ArrayList<String>();
		
		for (int firstTeamIndex = 0; firstTeamIndex < firstTeamSize; firstTeamIndex++)
		{
			if (firstUpdate)
			{
				firstTeam.add(state.get(stateIndex));
			}
			stateIndex++;
		}

		int secondTeamSize = Integer.valueOf(state.get(stateIndex));	
		stateIndex++;
		List<String> secondTeam = new ArrayList<String>();
		
		for (int secondTeamIndex = 0; secondTeamIndex < secondTeamSize; secondTeamIndex++)
		{
			if (firstUpdate)
			{
				secondTeam.add(state.get(stateIndex));
			}
			stateIndex++;
		}
		
		if (firstUpdate)
		{
			teams = new ArrayList<List<String>>();
			teams.add(firstTeam);
			teams.add(secondTeam);
			
			ownTeam = firstTeam.contains(ownName) ? 0 : 1;
		}
		
		double firstFlagX = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double firstFlagY = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double secondFlagX = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double secondFlagY = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double firstBaseX = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double firstBaseY = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double firstBaseWidth = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double firstBaseHeight = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double secondBaseX = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double secondBaseY = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double secondBaseWidth = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		double secondBaseHeight = Double.valueOf(state.get(stateIndex));
		stateIndex++;
		
		Point2D firstFlag = new Point2D.Double(firstFlagX, firstFlagY);
		Point2D secondFlag = new Point2D.Double(secondFlagX, secondFlagY);
		Rectangle2D firstBase = new Rectangle2D.Double(firstBaseX, firstBaseY, firstBaseHeight, firstBaseWidth);
		Rectangle2D secondBase = new Rectangle2D.Double(secondBaseX, secondBaseY, secondBaseHeight, secondBaseWidth);
		
		ownFlag = ownTeam == 0 ? firstFlag : secondFlag;
		enemyFlag = ownTeam == 0 ? secondFlag : firstFlag;
		ownBase = ownTeam == 0 ? firstBase : secondBase;
		enemyBase = ownTeam == 0 ? secondBase : firstBase;
		
		if (firstUpdate)
		{
			firstUpdate = false;
		}
	}
}
