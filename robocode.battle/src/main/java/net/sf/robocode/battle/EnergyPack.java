package net.sf.robocode.battle;

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;

public class EnergyPack extends RobjectPeer {

	boolean held = false;
	
	public EnergyPack(int x, int y) {
		super("energyPack", x, y, 10, 10, false, false, false, true, true, true);
	}

	public void hitByRobot(RobotPeer robot)
	{
		if (!held)
		{
			robot.updateEnergy(15);
			held = true;
		}
	}
	
	@Override
	public boolean shouldDraw() {
		// TODO Auto-generated method stub
		return !held;
	}

	@Override
	public void roundStarted()
	{
		held = false;
	}
}
