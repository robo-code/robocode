package CTF;

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;

/**
 * This is a flag for capture the flag
 * 
 * @author Joshua Galecki (original)
 *
 */
public class FlagPeer extends RobjectPeer{

	private RobotPeer holder;
	private double initialX;
	private double initialY;
	private int holderTeamNumber;
	
	public FlagPeer(int x, int y, int teamNumber) {
		super("flag", x, y, 10, 10, false, false, false,
				true, true, true);
		holder = null;
		initialX = x;
		initialY = y;
		holderTeamNumber = -1;
		if (teamNumber != 0)
		{
			setTeam(teamNumber);
		}
	}

	public boolean isHeld()
	{
		return holder != null;
	}
	
	public void capture()
	{
		roundStarted();
	}
	
	public void drop()
	{
		holder = null;
		setRobotConscious(true);
	}
	
	public RobotPeer getHolder() {
		return holder;
	}
	
	@Override
	public void hitByRobot(RobotPeer robot)
	{
		holder = robot;
		setRobotConscious(false);
	}

	@Override
	public boolean shouldDraw() {
		return true;
	}
	
	@Override
	public void roundStarted()
	{
		holder = null;
		setRobotConscious(true);
		setX(initialX);
		setY(initialY);
		holderTeamNumber = -1;
	}
	
	public void setHolderTeamNumber(int teamHolding)
	{
		holderTeamNumber = teamHolding;
	}
	
	public int getHolderTeamNumber()
	{
		return holderTeamNumber;
	}
	
	@Override
	public void turnUpdate()
	{
		if (holder != null)
		{
			if (holderTeamNumber == teamNumber)
			{
				roundStarted();
			}
			else
			{
				setX(holder.getX());
				setY(holder.getY());
				if (holder.isDead())
				{
					holder = null;
					setRobotConscious(true);
				}
			}
		}
	}
	
	@Override
	public int getTeam() {
		return teamNumber;
	}
}
