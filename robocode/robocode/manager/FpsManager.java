/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Removed initialize() which did only call reset(). Now reset() is called
 *       instead of initialize()
 *******************************************************************************/
package robocode.manager;


import robocode.battle.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class FpsManager {
	
	private Battle battle;
	private long start;
	private int frames;
	
	public FpsManager(Battle battle) {
		this.battle = battle;
	}
	
	public void reset() {
		start = System.currentTimeMillis();
		frames = 0;
	}
	
	public void update() {
		long current = System.currentTimeMillis();
		long diff = current - start;

		frames++;

		if (diff > 0) {
			// Calculate every other frame.
			if (frames > 0) {
				battle.setFPS(frames, diff);
				start = current;
				frames = 0;
			}
		}
	}
}
