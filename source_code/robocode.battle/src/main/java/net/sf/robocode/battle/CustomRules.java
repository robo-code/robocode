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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.robocode.battle.peer.BulletPeer;
import net.sf.robocode.battle.peer.ClassicRobotStatistics;
import net.sf.robocode.battle.peer.ExplosionPeer;
import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.battle.peer.TeamPeer;
import robocode.DeathEvent;
import robocode.control.RandomFactory;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.RobotState;
import robocode.exception.DeathException;


/**
 * This class is the base class for all extension rule sets. The default implementation
 * of these functions will be Classic Robocode rules.
 * 
 * @author Joshua Galecki (original)
 *
 */
public abstract class CustomRules implements ICustomRules {

	IContestantStatistics statistics = null;
	
	public CustomRules(){}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isGameOver(int activeRobots, List<RobotPeer> robots,
			List<RobjectPeer> robjects) {
		if (activeRobots <= 1) {
			return true;
		}
	
		boolean found = false;
		TeamPeer currentTeam = null;
	
		for (RobotPeer currentRobot : robots) {
			if (!currentRobot.isDead()) {
				if (!found) {
					found = true;
					currentTeam = currentRobot.getTeamPeer();
				} else {
					if (currentTeam == null && currentRobot.getTeamPeer() == null) {
						return false;
					}
					if (currentTeam != currentRobot.getTeamPeer()) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int startBattle(Battle battle) {
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void startRound(Battle battle) {
		for (RobjectPeer robject : battle.getRobjects()) {
			robject.roundStarted();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void startTurn(Battle battle) {}

	/**
	 * {@inheritDoc}
	 */
	public void finishTurn(Battle battle) {
		for (RobjectPeer robject : battle.getRobjects()) {
			robject.turnUpdate();
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void finishRound(Battle battle) {}

	/**
	 * {@inheritDoc}
	 */
	public void finishBattle(Battle battle) {}
	
	public void robotKill(RobotPeer robot) {
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
	
	public void robotIsDead(RobotPeer robot) {
		robot.setIsExecFinishedAndDisabled(true);
		throw new DeathException();
	}
	
	public int computeActiveRobots(List<RobotPeer> robots) {
		int ar = 0;

		// Compute active robots
		for (RobotPeer robotPeer : robots) {
			if (!robotPeer.isDead()) {
				ar++;
			}
		}
		return ar;
	}
	
	public IContestantStatistics getEmptyStatistics() {
		return new ClassicRobotStatistics();
	}
	
	public List<String> getBattlefieldState() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double[][] computeInitialPositions(String initialPositions, int battlefieldWidth, int battlefieldHeight) {
		double[][] initialRobotPositions = null;

		if (initialPositions == null || initialPositions.trim().length() == 0) {
			return initialRobotPositions;
		}

		List<String> positions = new ArrayList<String>();

		Pattern pattern = Pattern.compile("([^,(]*[(][^)]*[)])?[^,]*,?");
		Matcher matcher = pattern.matcher(initialPositions);

		while (matcher.find()) {
			String pos = matcher.group();

			if (pos.length() > 0) {
				positions.add(pos);
			}
		}

		if (positions.size() == 0) {
			return initialRobotPositions;
		}

		initialRobotPositions = new double[positions.size()][3];

		String[] coords;
		double x = 0, y = 0, heading;

		for (int i = 0; i < positions.size(); i++) {
			coords = positions.get(i).split(",");

			final Random random = RandomFactory.getRandom();

			x = RobotPeer.WIDTH + random.nextDouble() * (battlefieldWidth - 2 * RobotPeer.WIDTH);
			y = RobotPeer.HEIGHT + random.nextDouble() * (battlefieldHeight - 2 * RobotPeer.HEIGHT);
				
			heading = 2 * Math.PI * random.nextDouble();

			int len = coords.length;

			if (len >= 1) {
				// noinspection EmptyCatchBlock
				try {
					x = Double.parseDouble(coords[0].replaceAll("[\\D]", ""));
				} catch (NumberFormatException e) {}

				if (len >= 2) {
					// noinspection EmptyCatchBlock
					try {
						y = Double.parseDouble(coords[1].replaceAll("[\\D]", ""));
					} catch (NumberFormatException e) {}

					if (len >= 3) {
						// noinspection EmptyCatchBlock
						try {
							heading = Math.toRadians(Double.parseDouble(coords[2].replaceAll("[\\D]", "")));
						} catch (NumberFormatException e) {}
					}
				}
			}
			initialRobotPositions[i][0] = x;
			initialRobotPositions[i][1] = y;
			initialRobotPositions[i][2] = heading;
		}
		return initialRobotPositions;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RobjectPeer> setupObjects(int battlefieldWidth, int battlefieldHeight) {
		// return an empty list, we don't want any objects for classic mode
		return new ArrayList<RobjectPeer>();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RobjectPeer> checkBoundaries(List<RobjectPeer> robjects, int battlefieldWidth, int battlefieldHeight) {
		for (RobjectPeer robject : robjects) {
			if (robject.getX() + robject.getWidth() > battlefieldWidth) {
				robject.setWidth(battlefieldWidth - robject.getX());
			}
			if (robject.getY() + robject.getHeight() > battlefieldHeight) {
				robject.setHeight(battlefieldHeight - robject.getY());
			}
		}
		return robjects;
	}
}
