/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.manager;


import robocode.util.*;
import robocode.battle.*;


public class FpsManager {
	
	private Battle battle = null;
	private long start = 0;
	private int frames = 0;
	
	public FpsManager(Battle battle) {
		this.battle = battle;
	}
	
	public void initialize() {
		reset();
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
			// double r = Math.random();
			if (frames > 0) {
				battle.setFPS(frames, diff);
				start = current;
				frames = 0;
			}
		}
	}

	public void log(String s) {
		Utils.log(s);
	}
}

