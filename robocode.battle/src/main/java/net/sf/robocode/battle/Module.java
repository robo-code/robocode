/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle;


import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.core.Container;
import net.sf.robocode.recording.BattlePlayer;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.recording.RecordManager;


/**
 * @author Pavel Savara (original)
 */
public class Module {
	static {
		Container.cache.addComponent(IBattleManager.class, BattleManager.class);
		Container.cache.addComponent(BattleEventDispatcher.class);
		Container.cache.addComponent(IRecordManager.class, RecordManager.class);

		Container.factory.addComponent(Battle.class);
		Container.factory.addComponent(BattlePlayer.class);
	}
}
