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

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;

/**
 * This robject allows robots to see and shoot over it. It will not allow robots to
 * pass through for several seconds before it is destroyed.
 * 
 * @author Joshua Galecki (original)
 *
 */
public class RubblePeer extends RobjectPeer {

	int damage = 0;
	boolean intact = true;
	
	private void addDamage(int damageDone)
	{
		damage += damageDone;
		if (damage > 20)
		{
			intact = false;
			setRobotStopper(false);
			setScannable(false);
			setRobotConscious(false);
		}
	}
	
	public RubblePeer(int x, int y, int width, int height) 
	{
		super("rubble", x, y, width, height, true, false, false,
				true, true, true);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void hitByRobot(RobotPeer robot) {
		addDamage(1);
	}
	
	@Override
	public boolean shouldDraw() {
		return intact;
		
	}
	
	@Override
	public void roundStarted()
	{
		damage = 0;
		intact = true;
		setRobotStopper(true);
		setScannable(true);
		setRobotConscious(true);
	}

}
