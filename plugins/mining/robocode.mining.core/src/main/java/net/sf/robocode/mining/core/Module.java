/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.mining.core;


import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.battle.IBattleManagerBase;
import net.sf.robocode.core.BaseModule;
import net.sf.robocode.core.Container;
import net.sf.robocode.core.IModule;

import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class Module extends BaseModule {

	public void afterLoaded(List<IModule> allModules) {
		IBattleManagerBase battleManager = Container.cache.getComponent(IBattleManagerBase.class);
		MiningListener miningListener = new MiningListener();
		Container.cache.addComponent(miningListener);
		battleManager.addListener(miningListener);
	}
}
