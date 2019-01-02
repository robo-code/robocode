/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.dotnet.host;


import net.sf.robocode.core.*;
import net.sf.robocode.dotnet.repository.items.handlers.DotNetPropertiesHandler;
import net.sf.robocode.dotnet.repository.root.handlers.DllHandler;
import net.sf.robocode.manager.IVersionManagerBase;
import net.sf.robocode.io.Logger;
import net.sf.jni4net.Bridge;

import java.io.File;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class Module extends BaseModule {
	static {
		// .NET proxies and their interfaces must be loaded in system class loader in order to call native methods
		Init();
	}

	private static void Init() {
		try {

			String libsDir;
			final String version = ContainerBase.getComponent(IVersionManagerBase.class).getVersionN();

			final java.security.CodeSource source = Module.class.getProtectionDomain().getCodeSource();
			final File file = new File(source.getLocation().toURI()).getCanonicalFile();

			if (file.getName().equals("classes")) {
				libsDir = file.getParent();
			} else if (file.getName().endsWith(".jar")) {
				libsDir = file.getParent();
			} else {
				throw new Error("Can't find " + file);
			}

  			final String nhost = libsDir + "/robocode.dotnet.nhost-" + version + ".dll";
  			final String ncontrol = libsDir + "/robocode.control.dll";
	
				Bridge.init(new File(libsDir));
				// Bridge.setVerbose(true);
				// Bridge.setDebug(true);
				Bridge.SetSystemClassLoader(Container.engineLoader);
			  Bridge.LoadAndRegisterAssemblyFrom(new File(ncontrol));
			  Bridge.LoadAndRegisterAssemblyFrom(new File(nhost));

			Container.cache.addComponent("DllItemHandler", DllHandler.class);
			Container.cache.addComponent("CsPropertiesHandler", DotNetPropertiesHandler.class);
			Container.cache.addComponent("VbPropertiesHandler", DotNetPropertiesHandler.class);
			Container.cache.addComponent("DotNetPropertiesHandler", DotNetPropertiesHandler.class);

			// .NET proxies
			Container.cache.addComponent("robocode.host.cs", DotNetHost.class);
			Container.cache.addComponent("robocode.host.vb", DotNetHost.class);
			Container.cache.addComponent("robocode.host.dotnet", DotNetHost.class);

		} catch (Throwable e) {
			Logger.logError(e);
			throw new Error("Can't initialize .NET Robocode", e);
		}
	}

	public void afterLoaded(List<IModule> allModules) {
		net.sf.robocode.dotnet.nhost.ModuleN.InitN();
	}
}
