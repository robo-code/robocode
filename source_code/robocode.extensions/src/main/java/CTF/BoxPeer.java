package CTF;

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


import net.sf.robocode.battle.peer.RobjectPeer;




/**
 * This is a robject that robots cannot pass, shoot, or see through.
 * 
 * @author Joshua Galecki (original)
 *
 */
public class BoxPeer extends RobjectPeer{
	
	boolean draw = true;
	public void setDraw(boolean bool){
		draw = bool;
	}
	
	public BoxPeer(int x, int y, int width, int height)
	{
		super("box", x, y, width, height, true, true, true, true, true, false);
	}

	@Override
	public boolean shouldDraw() {
		return true;
	}

	public void hitByBullet() {
		draw = true;
	}
}
