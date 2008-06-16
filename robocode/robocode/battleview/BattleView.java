/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten
 *******************************************************************************/
package robocode.battleview;

import java.awt.Canvas;

import robocode.battle.events.BattleEventDispatcher;
import robocode.battlefield.BattleField;
import robocode.dialog.RobocodeFrame;
import robocode.manager.RobocodeManager;

public abstract class BattleView{
	protected Canvas canvas;
	
	public BattleView(RobocodeManager manager, RobocodeFrame robocodeFrame){
		canvas = new Canvas();
	}
	
	public Canvas getCanvas(){
		return canvas;
	}
	
	public abstract int getFPS();	
	public abstract void setDisplayOptions();
	public abstract void setup(BattleField battleField, BattleEventDispatcher battleEventDispatcher);
	public abstract void setInitialized(boolean initialized);

	public void close() {
		canvas.setEnabled(false);
		canvas.setVisible(false);
	}
}