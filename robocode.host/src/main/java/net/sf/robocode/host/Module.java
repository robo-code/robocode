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
package net.sf.robocode.host;


import net.sf.robocode.core.BaseModule;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.security.ThreadManager;


/**
 * @author Pavel Savara (original)
 */
public class Module extends BaseModule {
	static {
		Container.cache.addComponent(IHostManager.class, HostManager.class);
		Container.cache.addComponent(ICpuManager.class, CpuManager.class);
		Container.cache.addComponent(IThreadManager.class, ThreadManager.class);
		Container.cache.addComponent("robocode.host.java", JavaHost.class);
	}
}
