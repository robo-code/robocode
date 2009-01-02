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
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.OptInCaching;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.ArrayList;


/**
 * @author Pavel Savara (original)
 */
public final class Container {
	public static final MutablePicoContainer cache;
	public static final MutablePicoContainer factory;
	public static final ClassLoader systemLoader;
	public static final ClassLoader engineLoader;
	private static List<String> known = new ArrayList<String>();

	static {
		systemLoader = ClassLoader.getSystemClassLoader();
		engineLoader = systemLoader; //todo
		cache = new DefaultPicoContainer(new Caching());
		// new instance for every lookup, or same when asked for
		factory = new DefaultPicoContainer(new OptInCaching(), cache);
		loadModule("net.sf.robocode.api", systemLoader);
		final int modules = loadModules();
		if (modules <2){
			throw new Error("Main modules not loaded, something went wrong " + known.size());
		}
	}

	private static int loadModules() {
		int res=0;
		final String classPath = System.getProperties().getProperty("java.class.path", null);

		for (String path : classPath.split(";")) {
			final String test = path.toLowerCase();
			File pathf = new File(path);

			if (pathf.isDirectory()) {
				String name = getModuleName(path);

				if (name!=null) {
					if (loadModule(name, engineLoader)){
						res++;
					}
				} else {
					res+=loadModules(pathf);
				}
			} else if (test.contains(File.separator + "robocode.") && test.endsWith(".jar")) {
				String name = getModuleName(path);
				if (name!=null){
					if (loadModule(name, engineLoader)){
						res++;
					}
				}
			}
		}
		return res;
	}

	private static int loadModules(File pathf) {
		int res=0;
		final File[] modules = pathf.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().startsWith("robocode") && name.toLowerCase().endsWith(".jar");
			}
		});

		for (File module : modules) {
			if (loadModule(module.toString(), engineLoader)){
				res++;
			}
		}
		return res;
	}

	private static boolean loadModule(String module, ClassLoader loader) {
		try {
			if (known.contains(module)){
				// Logger.logMessage("already loaded " + module);
				return false;
			}
			Class<?> modClass = loader.loadClass(module + ".Module");

			modClass.newInstance();
			Logger.logMessage("Loaded " + module);
			known.add(module);
			return true;
		} catch (ClassNotFoundException e) {
			// OK, no worries, it is not module
			// Logger.logMessage("Can't load " + module);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (InstantiationException e) {
			Logger.logError(e);
		}
		return false;
	}

	private static String getModuleName(String path){
		final String test = path.toLowerCase();
		if (test.endsWith("robocode.jar") || test.contains("robocode.api")){
			return null;
		}
		int i = test.lastIndexOf("robocode.");
		if (i > 0) {
			String name = path.substring(i);

			i = name.indexOf(File.separator);
			if (i > 0){
				return "net.sf." + name.substring(0, i);
			}
			i = name.indexOf(".jar");
			if (i > 0){
				return "net.sf." + name.substring(0, i);
			}
			return "net.sf." + name;
		}
		return null;
	}
}
