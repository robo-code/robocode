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

package robocode;

/**
 * This is a flag for Capture the Flag
 * 
 * @author Joshua Galecki (original)
 *
 */
public class Flag extends Robject{

	private boolean held;
	private Robot owner;
	
	public Flag(String type, int x, int y) {
		super(type, x, y, 10, 10, false, false, false,
				true, true);
		held = false;
		owner = null;
	}

	public void setHeld(boolean held) {
		this.held = held;
	}

	public boolean isHeld() {
		return held;
	}

	public void setOwner(Robot owner) {
		this.owner = owner;
	}

	public Robot getOwner() {
		return owner;
	}

	public void hitByRobot()
	{
		held = true;
	}

	@Override
	public boolean shouldDraw() {
		return !held;
	}
}
