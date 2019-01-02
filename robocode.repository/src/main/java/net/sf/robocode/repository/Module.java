/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import net.sf.robocode.core.BaseModule;
import net.sf.robocode.core.Container;
import net.sf.robocode.repository.items.handlers.ClassHandler;
import net.sf.robocode.repository.items.handlers.PropertiesHandler;
import net.sf.robocode.repository.items.handlers.SourceHandler;
import net.sf.robocode.repository.items.handlers.TeamHandler;
import net.sf.robocode.repository.root.handlers.ClassPathHandler;
import net.sf.robocode.repository.root.handlers.JarHandler;


/**
 * @author Pavel Savara (original)
 */
public class Module extends BaseModule {
	static {
		Container.cache.addComponent(IRepositoryManager.class, RepositoryManager.class);

		// file handlers
		Container.cache.addComponent("TeamHandler", TeamHandler.class);
		Container.cache.addComponent("PropertiesHandler", PropertiesHandler.class);
		Container.cache.addComponent("ClassHandler", ClassHandler.class);
		Container.cache.addComponent("SourceHandler", SourceHandler.class);

		// root handlers
		Container.cache.addComponent("JarHandler", JarHandler.class);
		Container.cache.addComponent("ClassPathHandler", ClassPathHandler.class);
	}
}
