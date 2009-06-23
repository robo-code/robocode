/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 * 		Joshua Galecki
 * 		-Initial implementation
  *******************************************************************************/


package net.sf.robocode.battle;

import java.util.List;

import robocode.DeathEvent;
import robocode.IExtensionApi;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.RobotState;
import robocode.exception.DeathException;

import net.sf.robocode.battle.peer.BulletPeer;
import net.sf.robocode.battle.peer.ClassicRobotStatistics;
import net.sf.robocode.battle.peer.ExplosionPeer;
import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;

/**
 * This class is the base class for all extension rule sets.
 * 
 * @author Joshua Galecki (original)
 *
 */
public abstract class CustomRules implements ICustomRules{

	IContestantStatistics statistics = null;
	
	/**
	 * {@inheritDoc}
	 */
	public abstract boolean isGameOver(int activeRobots, List<RobotPeer> robots,
			List<RobjectPeer> robjects);

	/**
	 * {@inheritDoc}
	 */
	public abstract void startRound(List<RobotPeer> robots, List<RobjectPeer> robjects);

	/**
	 * {@inheritDoc}
	 */
	public abstract void updateTurn(List<RobotPeer> robots, List<RobjectPeer> robjects);
	
	public void robotKill(RobotPeer robot)
	{
		robot.getBattle().resetInactiveTurnCount(10.0);
		if (robot.isAlive()) {
			robot.addEvent(new DeathEvent());
			if (robot.isTeamLeader()) {
				for (RobotPeer teammate : robot.getTeamPeer()) {
					if (!(teammate.isDead() || teammate == robot)) {
						teammate.updateEnergy(-30);

						BulletPeer sBullet = new BulletPeer(robot, robot.getBattle().getBattleRules(), -1);

						sBullet.setState(BulletState.HIT_VICTIM);
						sBullet.setX(teammate.getX());
						sBullet.setY(teammate.getY());
						sBullet.setVictim(teammate);
						sBullet.setPower(4);
						robot.getBattle().addBullet(sBullet);
					}
				}
			}
			robot.getBattle().registerDeathRobot(robot);

			// 'fake' bullet for explosion on self
			final ExplosionPeer fake = new ExplosionPeer(robot, robot.getBattle().getBattleRules());

			robot.getBattle().addBullet(fake);
		}
		robot.updateEnergy(-robot.getEnergy());

		robot.setState(RobotState.DEAD);
	}
	
	public void robotIsDead(RobotPeer robot)
	{
		robot.setIsExecFinishedAndDisabled(true);
		throw new DeathException();
	}
	
	public int computeActiveRobots(List<RobotPeer> robots)
	{
		int ar = 0;

		// Compute active robots
		for (RobotPeer robotPeer : robots) {
			if (!robotPeer.isDead()) {
				ar++;
			}
		}
		return ar;
	}
	
	public IContestantStatistics getEmptyStatistics(){
		return new ClassicRobotStatistics();
	}
	
	public IExtensionApi getExtensionApi()
	{
		return null;
	}
}
