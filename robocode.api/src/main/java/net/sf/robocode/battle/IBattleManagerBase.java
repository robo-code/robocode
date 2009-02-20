/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.battle;


import robocode.control.BattleSpecification;
import robocode.control.events.IBattleListener;


/**
 * @author Pavel Savara (original)
 */
public interface IBattleManagerBase {
	void addListener(IBattleListener listener);

	void removeListener(IBattleListener listener);

	void waitTillOver();

	void stop(boolean waitTillEnd);

	void startNewBattle(BattleSpecification spec, boolean waitTillOver, boolean enableCLIRecording);

}
