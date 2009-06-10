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

import robocode.Flag;
import robocode.Obstacle;
import robocode.Robject;
import robocode.Trench;

/**
 * This class sets up the objects for Capture the Flag
 * 
 * @author Joshua Galecki (original)
 *
 */
public class CaptureTheFlagSetup extends BattlefieldSetup{

	@Override
	public List<Robject> setupObjects(int battlefieldWidth, int battlefieldHeight) {
		List<Robject> robjects = new ArrayList<Robject>();

		robjects.add(new Obstacle("box", 100, 100, 50, 50));
		robjects.add(new Obstacle("box", 300, 300, 100, 400));
		robjects.add(new Obstacle("box", 550, 500, 20, 20));
		robjects.add(new Obstacle("box", 500, 500, 20, 20));
		robjects.add(new Obstacle("box", 550, 550, 20, 20));
		robjects.add(new Obstacle("box", 500, 550, 20, 20));
		robjects.add(new Trench("pit", 200, 200, 300, 150));
		robjects.add(new Flag("flag", 600, 100));
		
		return checkBoundaries(robjects, battlefieldWidth, battlefieldHeight);
	}
}
