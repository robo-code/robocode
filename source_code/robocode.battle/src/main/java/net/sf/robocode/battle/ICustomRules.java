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


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import robocode.IExtensionApi;

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;


/**
 * This interface will provide winning and losing conditions, scoring, and other rules
 * 
 * @author Joshua Galecki (original)
 *
 */
public interface ICustomRules {

	int startBattle(Battle battle);
	
	/**
	 * Called at the beginning of each round. This is a good place to reset any dynamic objects.
	 * @param robots a list of all the robots in the battle
	 * @param robjects a list of all the objects
	 */
	void startRound(Battle battle);

	void startTurn(Battle battle);
	
	void finishTurn(Battle battle);
	
	void finishRound(Battle battle);
	
	void finishBattle(Battle battle);
	
	boolean isGameOver(int activeRobots, List<RobotPeer> robots, List<RobjectPeer> robjects);

	void robotKill(RobotPeer robot);	
	
	void robotIsDead(RobotPeer robot);
	
	int computeActiveRobots(List<RobotPeer> robots);
	
	IContestantStatistics getEmptyStatistics();
	
	List<String> getBattlefieldState();
	
	/**
	 * Decides where each robot will start off. If there are more robots than starting
	 * positions specified, the positions will be applied to the first robots and the rest
	 * will be placed randomly.
	 * To specify a position for the nth robot, returnedArray[n-1][0] specifies the x coordinate,
	 * returnedArray[n-1][1] specifies the y coordinate, and returnedArray[n-1][2] specifies
	 * the heading of the robot.
	 * 
	 * For example, to start the first robot at (10, 10) facing 90 degrees,
	 * and the second robot at (100, 10) facing 0 degrees:
	 *  double initialPosition = new double[2][2];
	 *  
	 *  initialPosition[0][0] = 10;
	 *  initialPosition[0][1] = 10;
	 *  initialPosition[0][2] = 90;
	 *  initialPosition[1][0] = 100;
	 *  initialPosition[1][1] = 10;
	 *  initialPosition[1][2] = 0;
	 *  
	 *  return initialPosition;
	 *  
	 * @param initialPositions passed in by battleProperties. @see ClassicSetup for steps to deal with this.
	 * @param battlefieldWidth width of the battlefield
	 * @param battlefieldHeight height of the battlefield
	 * @return an array containing the starting coordinates and heading of robots
	 */
	public double[][] computeInitialPositions(String initialPositions, int battlefieldWidth, int battlefieldHeight);
	
	/**
	 * Sets up any objects on the battlefield, at the beginning of the battle. Although 
	 * battlefieldWidth and battlefieldHieght are included as parameters, any objects 
	 * created are not automatically bound by these limits. You may call checkBoundaries() 
	 * within this function to crop obstacles to these boundaries.
	 * 
	 * @param battlefieldWidth width of the battlefield
	 * @param battlefieldHeight height of the battlefield
	 * @return a list of all objects on the battlefield
	 */
	public List<RobjectPeer> setupObjects(int battlefieldWidth, int battlefieldHeight);
	
	/**
	 * Goes through the list of objects and trims off any part outside the given boundaries.
	 * @param robjects the list of objects to be trimmed
	 * @param battlefieldWidth the width of the battlefield
	 * @param battlefieldHeight the height of the battlefield
	 * @return a list of objects completely within the battlefield boundaries
	 */
	public List<RobjectPeer> checkBoundaries(List<RobjectPeer> robjects, int battlefieldWidth, int battlefieldHeight);
	
}
