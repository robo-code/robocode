/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara & Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.recording;


import robocode.battle.IBattle;
import robocode.battle.events.BattleEventDispatcher;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public interface IRecordManager {

	void attachRecorder(BattleEventDispatcher battleEventDispatcher);
	void detachRecorder();
	IBattle createPlayer(BattleEventDispatcher battleEventDispatcher);

	void saveRecord(String fileName, BattleRecordFormat format);

	void loadRecord(String fileName, BattleRecordFormat format);

	boolean hasRecord();
}
