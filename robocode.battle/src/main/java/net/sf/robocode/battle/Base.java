package net.sf.robocode.battle;

import net.sf.robocode.battle.peer.RobjectPeer;

public class Base extends RobjectPeer {

	public Base(int x, int y, int width, int height, int teamNumber) {
		super("base", x, y, width, height, false, false, false,
				true, true, false);
		setTeam(teamNumber);
	}

	@Override
	public boolean shouldDraw() {
		return true;
	}
	

	@Override
	public int getTeam() {
		return teamNumber;
	}

}
