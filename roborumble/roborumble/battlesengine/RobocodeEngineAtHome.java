/*******************************************************************************
 * Copyright (c) 2003, 2007 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Albert Pérez
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Completely rewritten to use the robocode.control package only
 *******************************************************************************/
package roborumble.battlesengine;


import java.io.File;
import java.util.List;
import java.util.ArrayList;

import robocode.control.*;


/**
 * Project RoboRumble@home
 * RobocodeEngineForTeams 
 * Based on RobocodeEngine
 */
public class RobocodeEngineAtHome {
	
	private RobocodeEngine engine;
    
	private RobocodeListener eventHandler;

	private boolean isBattleRunning;

	private RobotResults[] results;

	public RobocodeEngineAtHome(File robocodeHome, RobocodeListener listener) {
		eventHandler = new EventHandler(listener);
		engine = new RobocodeEngine(robocodeHome, eventHandler);
	}
    
	public RobocodeEngineAtHome(RobocodeListener listener) {
		eventHandler = new EventHandler(listener);
		engine = new RobocodeEngine(eventHandler);
		engine.setVisible(true);
	}

	public void close() {
		engine.close();
	}
    
	public String getVersion() {
		return engine.getVersion();
	}
    
	public void setVisible(boolean visible) {
		engine.setVisible(visible);
	}

	public String[] getLocalRepository() {
		RobotSpecification[] robotSpecs = engine.getLocalRepository();
    	
		String[] robotNames = new String[robotSpecs.length];

		for (int i = robotSpecs.length - 1; i >= 0; i--) {
			robotNames[i] = robotSpecs[i].getClassName();
		}

		return robotNames;
	}

	public void runBattle(BattleSpecification battle, String selectedRobotList) {
		isBattleRunning = true;

		RobotSpecification[] robotSpecs = engine.getLocalRepository();

		String[] selectedRobots = selectedRobotList.split(",");
    	
		List<RobotSpecification> selectedRobotSpecs = new ArrayList<RobotSpecification>();
    	
		for (RobotSpecification rs : robotSpecs) {
			for (String robot : selectedRobots) {
				String[] nameAndVersion = robot.split(" "); 

				if (rs.getClassName().equals(nameAndVersion[0]) && rs.getVersion().equals(nameAndVersion[1])) {
					selectedRobotSpecs.add(rs);
					break;
				}
			}
		}

		engine.runBattle(new BattleSpecification(1, battle.getBattlefield(), selectedRobotSpecs.toArray(robotSpecs)));
	}
    
	public boolean isBattleRunning() {
		return isBattleRunning;
	}
    
	public void abortCurrentBattle() {
		engine.abortCurrentBattle();
	}

	public void printBattle() {
		StringBuffer sb = new StringBuffer();

		sb.append("Rank,");
		sb.append("Robot Name,");
		sb.append("Total Score,");
		sb.append("Survival,");
		sb.append("Surv Bonus,");
		sb.append("Bullet Dmg,");
		sb.append("Bullet Bonus,");
		sb.append("Ram Dmg * 2");
		sb.append("Ram Bonus");
		sb.append("1sts");
		sb.append("2nds");
		sb.append("3rds");
		
		for (RobotResults rr : results) {
			sb.append(rr.getRank()).append(',');
			sb.append(rr.getRobot().getClassName()).append(' ').append(rr.getRobot().getVersion()).append(',');
			sb.append(rr.getScore()).append(',');
			sb.append(rr.getSurvival()).append(',');
			sb.append(rr.getLastSurvivorBonus()).append(',');
			sb.append(rr.getBulletDamage()).append(',');
			sb.append(rr.getBulletDamageBonus()).append(',');
			sb.append(rr.getRamDamage()).append(',');
			sb.append(rr.getRamDamageBonus()).append(',');
			sb.append(rr.getFirsts()).append(',');
			sb.append(rr.getSeconds()).append(',');
			sb.append(rr.getThirds()).append('\n');
		}
		
		System.out.println(sb.toString());
	}
	
	public RobotResults[] getResults() {
		return results;
	}

	private class EventHandler implements RobocodeListener {

		private RobocodeListener externListener;

		private EventHandler(RobocodeListener externListener) {
			this.externListener = externListener;
		}

		public void battleAborted(BattleSpecification battleSpec) {
			isBattleRunning = false;
			if (externListener != null) {
				externListener.battleAborted(battleSpec);
			}
		}

		public void battleComplete(BattleSpecification battleSpec, RobotResults[] results) {
			isBattleRunning = false;
			RobocodeEngineAtHome.this.results = results;
			if (externListener != null) {
				externListener.battleComplete(battleSpec, results);
			}
		}

		public void battleMessage(String message) {
			if (externListener != null) {
				externListener.battleMessage(message);
			}
		}
	}
}
