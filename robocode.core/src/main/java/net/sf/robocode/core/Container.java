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

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

import java.io.File;
import java.io.FilenameFilter;

import net.sf.robocode.io.Logger;

/**
 * @author Pavel Savara (original)
 */
public final class Container {
	public static MutablePicoContainer instance;

	static {
		instance = new DefaultPicoContainer();
		final String classPath = System.getProperties().getProperty("java.class.path", null);
		for(String path : classPath.split(";")){
			File pathf=new File(path);
			if (pathf.isDirectory()){
				final int i = path.lastIndexOf("robocode.");
				if (i>0){
					String name = path.substring(i);
					name = "net.sf." + name.substring(0, name.indexOf("\\"));
					loadModule(name);
				} else {
					loadModules(pathf);
				}
			}
			else if (path.startsWith("robocode") && path.endsWith(".jar")){
				loadModule(pathf.toString());
			}
		}
	}

	private static void loadModules(File pathf) {
		System.out.println("Searching " + pathf);
		final File[] modules = pathf.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("robocode") && name.endsWith(".jar");
			}
		});
		for(File module : modules){
			loadModule(module.toString());
		}
	}

	private static void loadModule(String module){
		try {
			Class<?> modClass = ClassLoader.getSystemClassLoader().loadClass(module + ".Module");
			modClass.newInstance();
			Logger.logMessage("Loaded "+module);
		} catch (ClassNotFoundException e) {
			// OK, no worries, it is not module
			Logger.logMessage("Can't load "+module);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (InstantiationException e) {
			Logger.logError(e);
		}
	}
}
