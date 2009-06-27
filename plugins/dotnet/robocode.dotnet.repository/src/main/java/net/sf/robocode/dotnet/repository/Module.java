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
package net.sf.robocode.dotnet.repository;


import net.sf.robocode.core.Container;
import net.sf.robocode.dotnet.repository.items.handlers.DllHandler;
import net.sf.robocode.dotnet.repository.items.handlers.DotNetPropertiesHandler;
import net.sf.robocode.dotnet.repository.items.DotNetRobotItem;


/**
 * @author Pavel Savara (original)
 */
public class Module {
	static {
		// file handlers
		Container.cache.addComponent("csPropertiesHandler", DotNetPropertiesHandler.class);
		Container.cache.addComponent("dllHandler", DllHandler.class);
	}
}
