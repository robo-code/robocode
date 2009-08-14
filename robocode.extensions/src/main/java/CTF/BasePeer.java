package CTF;

import net.sf.robocode.battle.peer.RobjectPeer;

public class BasePeer extends RobjectPeer {

	public BasePeer(int x, int y, int width, int height, int teamNumber) {
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
