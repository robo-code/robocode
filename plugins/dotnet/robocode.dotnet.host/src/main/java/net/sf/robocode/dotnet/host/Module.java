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
package net.sf.robocode.dotnet.host;


import net.sf.robocode.core.Container;
import net.sf.robocode.dotnet.repository.items.handlers.DotnetPropertiesHandler;
import net.sf.robocode.dotnet.repository.items.handlers.DllHandler;
import net.sf.jni4net.Bridge;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class Module {
	static {
		try {
			Bridge.verbose = true;
			Bridge.init();
			Bridge.setVerbose(true);
			Bridge.LoadAndRegisterAssembly(new File("../robocode.dotnet.ntests/target/robocode.dotnet.host-1.7.1.3.dll").getAbsolutePath());
			//Bridge.LoadAndRegisterAssembly(new File("../robocode.dotnet.ntests/target/robocode.dotnet.tests-1.7.1.3.dll").getAbsolutePath());
			Container.cache.addComponent("dllHandler", DllHandler.class);
			Container.cache.addComponent("CsPropertiesHandler", DotnetPropertiesHandler.class);
			Container.cache.addComponent("VbPropertiesHandler", DotnetPropertiesHandler.class);
			Container.cache.addComponent("DotnetPropertiesHandler", DotnetPropertiesHandler.class);
			Container.cache.addComponent("robocode.host.cs", DotnetHost.class);
			Container.cache.addComponent("robocode.host.vb", DotnetHost.class);
			Container.cache.addComponent("robocode.host.dotnet", DotnetHost.class);
		} catch (Throwable e) {
			throw new Error("Can't initialize .NET", e);
		}
	}
}
