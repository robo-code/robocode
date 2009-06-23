package net.sf.robocode.battle;

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;

public class EnergyPackPeer extends RobjectPeer {

	boolean pickedUp = false;
	
	public EnergyPackPeer(int x, int y) {
		super("energyPack", x, y, 10, 10, false, false, false, true, true, true);
	}

	public void hitByRobot(RobotPeer robot)
	{
		if (!pickedUp)
		{
			robot.updateEnergy(15);
			pickedUp = true;
			setRobotStopper(false);
			setScannable(false);
			setRobotConscious(false);
		}
	}
	
	@Override
	public boolean shouldDraw() {
		return !pickedUp;
	}

	@Override
	public void roundStarted()
	{
		pickedUp = false;
		setRobotStopper(true);
		setScannable(true);
		setRobotConscious(true);
	}
}
