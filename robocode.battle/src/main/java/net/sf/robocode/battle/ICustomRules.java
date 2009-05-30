package net.sf.robocode.battle;

import java.util.List;

import net.sf.robocode.battle.peer.RobotPeer;

/**
 * This interface will provide winning and losing conditions, scoring, and other rules
 * 
 * @author Joshua Galecki
 *
 */
public interface ICustomRules {
	boolean isGameOver(int activeRobots, List<RobotPeer> robots);

	//TODO: put scoring in here later
}
