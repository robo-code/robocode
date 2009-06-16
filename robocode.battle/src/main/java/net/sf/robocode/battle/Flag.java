package net.sf.robocode.battle;

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;
import robocode.Robot;

/**
 * This is a flag for capture the flag
 * 
 * @author Joshua Galecki (original)
 *
 */
public class Flag extends RobjectPeer{

	private RobotPeer holder;
	private double initialX;
	private double initialY;
	
	public Flag(int x, int y, int teamNumber) {
		super("flag", x, y, 10, 10, false, false, false,
				true, true, true);
		holder = null;
		initialX = x;
		initialY = y;
		if (teamNumber != 0)
		{
			setTeam(teamNumber);
		}
	}

	public boolean isHeld()
	{
		return holder != null;
	}
	
	public void drop()
	{
		holder = null;
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
	}
	
	@Override
	public void turnUpdate()
	{
		if (holder != null)
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
	
	@Override
	public int getTeam() {
		return teamNumber;
	}
}
