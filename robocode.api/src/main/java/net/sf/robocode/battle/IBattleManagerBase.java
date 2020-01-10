/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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

	void startNewBattle(BattleSpecification spec, String initialPositions, boolean waitTillOver, boolean enableCLIRecording);
}
