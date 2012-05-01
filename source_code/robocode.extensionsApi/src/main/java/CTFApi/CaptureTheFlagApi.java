package CTFApi;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import robocode.*;
import robocode.robotinterfaces.IObjectEvents;
import robocode.robotinterfaces.IObjectRobot;

/**
 * The API used for capturing the flag of enemy and bringing it to home base.
 *<p/>
 * 
 * CTFApi package is to be used for the battles involving capturing the flag of enemy and bringing it to home.
 * In such battles, a team will be assigned a home base and home flag.
 * The team has to capture the enemy flag and bring it back to home base.
 *
 * CaptureTheFlagApi may be used to know the coordinates of team's own flag, enemy's flag, own base and enemy's base.
 * Note that each robot must call the 'registerMe' function of this class before using any other function of this API.
 * @author Joshua Galecki (original)
 * @author Himanshu Singh (contributor)
 *
 * @see <a target="_top" href="http://itbhu.ac.in/codefest">
 *      Codefest</a>
 * @see <a target="_top" href="http://itbhu.ac.in/codefest/event.php?eid=7">
 *      Virtual Combat</a>
 * @see <a href="http://itbhu.ac.in/codefest/event.php?eid=7">
 *      Building your first CTF robot<a>
 * @see AdvancedRobot
 * @see JuniorRobot
 * @see Robot
 * @see TeamRobot
 * @see Droid
 */

public class CaptureTheFlagApi extends TeamRobot implements ICaptureTheFlagApi,IObjectEvents, IObjectRobot{

	private String ownName;
	private List<List<String>> teams;
	private int ownTeam;
	private Rectangle2D ownBase;
	private Rectangle2D enemyBase;
	private Point2D ownFlag;
	private Point2D enemyFlag;
	private boolean firstUpdate = true;
	
	public CaptureTheFlagApi()	{}

	/**
	 * The function called by a robot to register itself in the battle of capturing the flag.
	 * <p/>
	 *
	 * This function must be called by every robot which uses the CTFApi package.
	 * The function should be called as the first statement of run() function of the robot.
	 * @return void
	 * @see Robot
	 */ 
	public void registerMe()
	{
		execute();
		this.ownName = this.getName();
		List<String> state = getBattlefieldState();
		UpdateBattlefieldState(state);
		return;
		
	}
	

	/**
	 * Returns true if a given robot is a teammate of the robot calling this function.
	 * <p/>
	 * This call helps a robot in knowing whether a scanned robot is a teammate or not. 
	 *
	 * @param The name of a scanned robot
	 * @return true if the name exists among the its teammates, false otherwise.
	 */ 
	public boolean isTeammate(String otherName) {
		if (teams != null)
		{
			return teams.get(ownTeam).contains(otherName);
		}
		return false;
	}


	
	/**
	 * Returns a list of all the teams in the battle.
	 * <p/>
	 *
	 * @return the list containing the names of teams in battle.
	 * @see TeamRobot
	 * @see AdvancedRobot
	 */
	public List<List<String>> getTeams()
	{
		if (teams != null)
		{
			return teams;
		}
		return null;
	}

	
	/**
	 * Returns the home base of the robot's team
	 * <p/>
	 *
	 * @return Rectangle2D object which defines the home base of a robot.
	 * @see <a href="http://download.oracle.com/javase/1.4.2/docs/api/java/awt/geom/Rectangle2D.html"> Rectangle2D</a>
	 */
	public Rectangle2D getOwnBase() {
		return ownBase;
	}

	/**
	 * Returns the coordinates of flag of the robot's own team
	 * <p/>
	 *
	 * @return Point2D object which defines the coordinates of flag.
	 * @see <a href="http://download.oracle.com/javase/1.4.2/docs/api/java/awt/geom/Point2D.html"> Point2D</a>
	 */
	public Point2D getOwnFlag()
	{
		return ownFlag;
	}

	
	/**
	 * Returns the base of the robot's enemy team
	 * <p/>
	 *
	 * @return Rectangle2D object which defines the enemy base of a robot.
	 * @see <a href="http://download.oracle.com/javase/1.4.2/docs/api/java/awt/geom/Rectangle2D.html"> Rectangle2D</a>
	 */
	public Rectangle2D getEnemyBase() {
		return enemyBase;
	}

	/**
	 * Returns the coordinates of flag of the robot's enemy team
	 * <p/>
	 *
	 * @return Point2D object which defines the coordinates of flag.
	 * @see <a href="http://download.oracle.com/javase/1.4.2/docs/api/java/awt/geom/Point2D.html"> Point2D</a>
	 */
	public Point2D getEnemyFlag()
	{
		return enemyFlag;
	}

	/**
	 * Checks whether the enemy flag is present in enemy base.
	 * <p/>
	 *
	 * This call helps in identifying if the enemy flag has been shifted out of the enemy base by some teammate.
	 * @return true if enemy flag is present inside the enemy's base, false otherwise.
	 * 
	 */
	public boolean isEnemyFlagAtBase()
	{		
		if (enemyBase == null)
		{
			return false;
		}
		return enemyBase.contains(enemyFlag);
	}


	/**
	 * Checks whether the robot's own flag is present inside the home base.
	 * <p/>
	 *
	 * This call helps in identifying if the enemy flag has been shifted out of the enemy base by some teammate.
	 * @return true if enemy flag is present inside the enemy's base, false otherwise.
	 */
	public boolean isOwnFlagAtBase() 
	{
		if (ownBase == null)
		{
			return false;
		}
		return ownBase.contains(ownFlag);
	}

	/**
	 * The function to update the state of the battlefield
	 * <p/>
	 *
	 * This function should be called if a robot wants to update its knowledge of changes in battlefield. 
	 * The changes may include change in coordinates of flags and robots. 
	 */
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
	
	/**
	 * Do not call this method!
	 * <p/>
	 * 
	 */
	public IObjectEvents getObjectEventListener() {
		return this;
	}
	/** {@inheritDoc}
	 * 
	 */
	public void onHitObstacle(HitObstacleEvent e) {	}
	
	 /** {@inheritDoc}
	  * 
	  */
	public void onHitObject(HitObjectEvent event) {	}
}
