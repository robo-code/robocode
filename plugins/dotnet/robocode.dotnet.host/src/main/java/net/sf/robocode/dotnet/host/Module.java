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
import net.sf.robocode.core.EngineClassLoader;
import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.dotnet.repository.items.handlers.DotnetPropertiesHandler;
import net.sf.robocode.dotnet.repository.root.handlers.DllHandler;
import net.sf.robocode.host.IHost;
import net.sf.robocode.version.VersionManager;
import net.sf.robocode.manager.IVersionManagerBase;
import net.sf.jni4net.Bridge;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class Module {
	static {
		// .NET proxies and their interfaces must be loaded in system class loader in order to call native methods
		EngineClassLoader.addExclusion("net.sf.robocode.dotnet.host.DotnetHost");
		EngineClassLoader.addExclusion("net.sf.robocode.dotnet.repository.root.DllRootHelper");
		Init();
	}

	private static void Init() {
		try {

            final String version = ContainerBase.getComponent(IVersionManagerBase.class).getVersion();

            Bridge.setVerbose(true);
            final String libsDir = "../robocode.dotnet.tests/target";
            Bridge.init(libsDir);
            final String nhost = libsDir + "/robocode.dotnet.nhost-" + version + ".dll";
            final String npath = new File(nhost).getCanonicalPath();
            Bridge.LoadAndRegisterAssembly(npath);

			Container.cache.addComponent("DllItemHandler", DllHandler.class);

			//Container.cache.addComponent("dllHandler", DllItemHandler.class);
			Container.cache.addComponent("CsPropertiesHandler", DotnetPropertiesHandler.class);
			Container.cache.addComponent("VbPropertiesHandler", DotnetPropertiesHandler.class);
			Container.cache.addComponent("DotnetPropertiesHandler", DotnetPropertiesHandler.class);

			final ClassLoader loader = IHost.class.getClassLoader();

			// .NET proxies
			Container.cache.addComponent("robocode.host.cs", DotnetHost.class);
			Container.cache.addComponent("robocode.host.vb", DotnetHost.class);
			Container.cache.addComponent("robocode.host.dotnet", DotnetHost.class);
		} catch (Throwable e) {
			throw new Error("Can't initialize .NET", e);
		}
	}
}
