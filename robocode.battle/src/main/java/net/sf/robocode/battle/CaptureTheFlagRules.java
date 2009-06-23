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

import java.util.ArrayList;
import java.util.List;


import robocode.DeathEvent;
import robocode.IExtensionApi;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.RobotState;

import net.sf.robocode.battle.peer.BulletPeer;
import net.sf.robocode.battle.peer.ExplosionPeer;
import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;

/**
 * This class contains the rules for a Capture the Flag game
 * 
 * @author Joshua Galecki (original)
 *
 */
public class CaptureTheFlagRules extends CustomRules {
	

	List<RobotPeer> firstTeam;
	List<RobotPeer> secondTeam;
	FlagPeer firstFlag;
	FlagPeer secondFlag;
	BasePeer firstBase;
	BasePeer secondBase;
	private CaptureTheFlagExtensionApi exApi;
	private int captures = 0;
	
	public boolean isGameOver(int activeRobots, List<RobotPeer> robots, 
			List<RobjectPeer> robjects) {
		return captures > 2;
	}
	
	@Override
	public void updateTurn(List<RobotPeer> robots, List<RobjectPeer> robjects) {
		for (RobjectPeer robject : robjects)
		{
			robject.turnUpdate();

			if (robject.getType().equals("flag"))
			{
				FlagPeer flag = (FlagPeer)robject;
				//exApi.updateFlag((Flag)robject);
				for (RobjectPeer otherRobject : robjects)
				{
					if (otherRobject.getType().equals("base") &&
							flag.getTeam() != otherRobject.getTeam() &&
							otherRobject.getBoundaryRect().contains(flag.getBoundaryRect()))
					{
						((CaptureTheFlagStatistics)flag.getHolder().getStatistics()).scoreCapture();
						flag.capture();
						captures++;
					}
				}			
			}
		}
	}

	@Override
	public void robotKill(RobotPeer robot) {
		robot.getBattle().resetInactiveTurnCount(10.0);
		if (robot.isAlive()) {
			robot.addEvent(new DeathEvent());
			//TODO: this branch necessary?
			if (robot.isTeamLeader()) {
				for (RobotPeer teammate : robot.getTeamPeer()) {
					if (!(teammate.isDead() || teammate == robot)) {
						teammate.updateEnergy(-30);

						BulletPeer sBullet = new BulletPeer(robot, 
								robot.getBattle().getBattleRules(), -1);

						sBullet.setState(BulletState.HIT_VICTIM);
						sBullet.setX(teammate.getX());
						sBullet.setY(teammate.getY());
						sBullet.setVictim(teammate);
						sBullet.setPower(4);
						robot.getBattle().addBullet(sBullet);
					}
				}
			}

			// 'fake' bullet for explosion on self
			final ExplosionPeer fake = new ExplosionPeer(robot, robot.getBattle().getBattleRules());

			robot.getBattle().addBullet(fake);
		}
		robot.updateEnergy(-robot.getEnergy());

		robot.setState(RobotState.DEAD);
	}	
	
	@Override
	public void robotIsDead(RobotPeer robot)
	{
		if (robot.isDead())
		{
			robot.updateEnergy(1);
			if (robot.getEnergy() >= 100)
			{
				robot.setX(20);
				robot.setY(20);
				robot.setState(RobotState.ACTIVE);
				robot.setIsExecFinishedAndDisabled(false);
			}
		}
	}
	
	@Override
	public int computeActiveRobots(List<RobotPeer> robots)
	{
		return robots.size();
	}

	@Override
	public void startRound(List<RobotPeer> robots, List<RobjectPeer> robjects) {
		captures = 0;
		
		//Assign teams

		firstTeam = new ArrayList<RobotPeer>();
		secondTeam = new ArrayList<RobotPeer>();
		int team = 0;
		for(RobotPeer robot : robots)
		{
			if (team % 2 == 0)
			{
				firstTeam.add(robot);
			}
			else
			{
				secondTeam.add(robot);
			}
		}
		
		for (RobjectPeer robject : robjects)
		{
			if (robject.getType().equals("flag"))
			{
				updateFlag((FlagPeer)robject);
			}
			if (robject.getType().equals("base"))
			{
				BasePeer base = (BasePeer)robject;
				if (base.getTeam() == 1)
				{
					firstBase = base;
				}
				else
				{
					secondBase = base;
				}
			}
		}
	}
	
	private void updateFlag(FlagPeer flag)
	{
		if (flag.getTeam() == 1)
		{
			firstFlag = flag;
		}
		else
		{
			secondFlag = flag;
		}
	}
	
	public FlagPeer getFlag(int teamNumber)
	{
		if (teamNumber == 1)
		{
			return firstFlag;
		}
		else
		{
			return secondFlag;
		}
	}
	
	public List<RobotPeer> getTeam(int teamNumber)
	{
		if (teamNumber == 1)
		{
			return firstTeam;
		}
		else
		{
			return secondTeam;
		}
	}
	
	public BasePeer getBase(int teamNumber)
	{
		if (teamNumber == 1)
		{
			return firstBase;
		}
		else
		{
			return secondBase;
		}
	}
	
	@Override
	public IContestantStatistics getEmptyStatistics(){
		return new CaptureTheFlagStatistics();
	}
	
	@Override
	public IExtensionApi getExtensionApi()
	{
		if (exApi != null)
		{
			return exApi;
		}
		return new CaptureTheFlagExtensionApi(this);
	}
}
