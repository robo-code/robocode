/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;


import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import java.io.File;

import net.sf.robocode.io.RobocodeProperties;


/**
 * @author Pavel Savara (original)
 */
public class EngineClassLoader extends URLClassLoader {

	private static Set<String> exclusions = new HashSet<String>();
	static {
		// this will be loaded on system classloader
		exclusions.add(EngineClassLoader.class.getName());
		exclusions.add(Container.class.getName());
		exclusions.add(RobocodeMainBase.class.getName());

		// .NET proxies and their interfaces must be loaded in system class loader in order to call native methods
		exclusions.add("net.sf.robocode.host.IHost");
		exclusions.add("net.sf.robocode.host.IHostManager");
		exclusions.add("net.sf.robocode.host.proxies.IHostingRobotProxy");
		exclusions.add("net.sf.robocode.peer.IRobotPeer");
		exclusions.add("net.sf.robocode.repository.IRobotItem");
		exclusions.add("net.sf.robocode.repository.RobotType");
		exclusions.add("net.sf.robocode.host.RobotStatics");
		exclusions.add("net.sf.robocode.peer.BadBehavior");
		exclusions.add("net.sf.robocode.dotnet.host.DotNetHost");
		exclusions.add("net.sf.robocode.dotnet.repository.root.DllRootHelper");
		exclusions.add("net.sf.robocode.dotnet.nhost.ModuleN");
		exclusions.add("net.sf.robocode.host.proxies.__IHostingRobotProxy");
	}

	public EngineClassLoader(ClassLoader parent) {
		super(Container.findJars(File.separator + "robocode."), parent);
	}

	public synchronized void addURL(URL url) {
		super.addURL(url);
	}

	public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

		if (RobocodeProperties.isSecurityOff() || name.startsWith("java.lang")) {
			return super.loadClass(name, resolve);
		}
		if (isEngineClass(name)) {
			return loadEngineClass(name, resolve);
		}
		return super.loadClass(name, resolve);
	}

	private Class<?> loadEngineClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> result = findLoadedClass(name);

		if (result == null) {
			result = findClass(name);
		}
		if (resolve) {
			resolveClass(result);
		}
		return result;
	}

	private boolean isEngineClass(String name) {
		if (name.startsWith("net.sf.robocode") || name.startsWith("robocode.control")) {
			if (exclusions.contains(name)) {
				return false;
			}
			// try to find it in engine's classpath
			// this is URL, don't change to File.pathSeparator
			final String path = name.replace('.', '/').concat(".class");

			return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
				public Boolean run() {
					return findResource(path) != null;
				}
			});
		}
		return false;
	}
}
