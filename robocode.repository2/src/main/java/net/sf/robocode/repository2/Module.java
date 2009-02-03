/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository2;


import net.sf.robocode.core.Container;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository2.items.handlers.*;
import net.sf.robocode.repository2.root.handlers.JarHandler;
import net.sf.robocode.repository2.root.handlers.ClassPathHandler;


/**
 * @author Pavel Savara (original)
 */
public class Module {
	static {
		Container.cache.addComponent(IRepositoryManager.class, RepositoryManager.class);

		//file handlers
		Container.cache.addComponent("TeamHandler", TeamHandler.class);
		Container.cache.addComponent("PropertiesHandler", PropertiesHandler.class);
		Container.cache.addComponent("ClassHandler", ClassHandler.class);

		//root handlers
		Container.cache.addComponent("JarHandler", JarHandler.class);
		Container.cache.addComponent("ClassPathHandler", ClassPathHandler.class);
	}
}
