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
package net.sf.robocode.core;


import net.sf.robocode.io.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Pavel Savara (original)
 */
public class EngineClassLoader extends URLClassLoader {
	private static final boolean isSecutityOn = !System.getProperty("NOSECURITY", "false").equals("true");
	private static Set<String> exclusions = new HashSet<String>();
	static {
		// this will be loaded on system classloader
		exclusions.add(EngineClassLoader.class.getName());
		exclusions.add(Container.class.getName());
		exclusions.add(RobocodeMainBase.class.getName());
	}

	public EngineClassLoader(ClassLoader parent) {
		super(initRobotClassLoader(), parent);
	}

	public synchronized void addURL(URL url) {
		super.addURL(url);
	}

	public synchronized Class<?> loadClass(String name, boolean resolve)
		throws ClassNotFoundException {
		if (name.startsWith("java.lang")) {
			// we always delegate java.lang stuff to parent loader
			return super.loadClass(name, resolve);
		}
		if (isSecutityOn && isEngineClass(name)) {
			// yes, it is in engine's classpath
			// we load it localy
			return loadEngineClass(name, resolve);
		}
		// it is robot API
		// or java class
		// or security is off
		// so we delegate to parrent classloader
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
			final String path = name.replace('.', '/').concat(".class");

			return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
				public Boolean run() {
					return findResource(path) != null;
				}
			});
		}
		return false;
	}

	private static URL[] initRobotClassLoader() {
		List<String> urls = new ArrayList<String>();
		final String classPath = System.getProperty("robocode.class.path", null);

		for (String path : classPath.split(File.pathSeparator)) {
			String test = path.toLowerCase();

			if (test.contains(File.separator + "robocode.")) {
				if (!test.contains("robocode.jar") && !test.contains("robocode.api")
						) {
					urls.add(path);
				}
			}
		}
		return convertUrls(urls);
	}

	private static URL[] convertUrls(List<String> surls) {
		final URL[] urls = new URL[surls.size()];

		for (int i = 0; i < surls.size(); i++) {
			String url = surls.get(i);
			File f = new File(url);

			try {
				urls[i] = f.getCanonicalFile().toURL();
			} catch (MalformedURLException e) {
				Logger.logError(e);
			} catch (IOException e) {
				Logger.logError(e);
			}
		}
		return urls;
	}

}
