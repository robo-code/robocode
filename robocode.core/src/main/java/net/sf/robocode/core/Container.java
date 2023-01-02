/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;


import net.sf.robocode.io.Logger;

import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.OptInCaching;
import org.picocontainer.classname.DefaultClassLoadingPicoContainer;

import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Root of loaders.
 *
 * we have three types of classLoaders.
 * 1) System class loader. Is loaded by default and is parent of loaders below. It contains this container class. All content of robot.api module. All general java stuff.
 * 2) EngineClassLoader. Is used as isolation of all other robocode modules from system classloader. Anything loaded by engine is loaded there. We use single instance of this loader.
 * 3) RobotClassLoader. Is used by robots. It will load every class on robot's private classPath. It blocks malicious attempts of references to robocode engine classes. We use multiple instances of this loader.
 * - communication between classes from different classloaders must be done using interfaces or data types from system classLoader
 * - this class must not reference any class of EngineClassLoader scope
 *
 * Dependency injection
 * We use PicoContainer as IoC vehicle. We configure it by loading Module class in every .jar or classpath we can find on system classPath
 * 1) Container.cache is containing singletons
 * 2) Container.factory will create always new instance of component 
 *
 * @author Pavel Savara (original)
 */
public final class Container extends ContainerBase {

	private static final String classPath = System.getProperties().getProperty("robocode.class.path", null);

	public static final MutablePicoContainer cache;
	public static final MutablePicoContainer factory;
	public static final ClassLoader systemLoader;
	private static final ClassLoader engineLoader;
	private static final Set<String> known = new HashSet<String>();
	private static final List<IModule> modules = new ArrayList<IModule>();

	static {
		instance = new Container();
		systemLoader = Container.class.getClassLoader();
		engineLoader = new EngineClassLoader(systemLoader);
		final Thread currentThread = Thread.currentThread();

		currentThread.setContextClassLoader(engineLoader);
		currentThread.setName("Application Thread");

		// make sure we have AWT before we init security
		Toolkit.getDefaultToolkit();

		cache = new DefaultClassLoadingPicoContainer(engineLoader, new Caching(), null);
		factory = new DefaultClassLoadingPicoContainer(engineLoader, new OptInCaching(), cache);
		loadModule("net.sf.robocode.api", systemLoader);
		final String[] cp = classPath.split(File.pathSeparator);

		// load core first
		for (String path : cp) {
			if (path.toLowerCase().contains("robocode.core")) {
				loadFromPath(path);
			}
		}
		// load normal modules
		for (String path : cp) {
			if (!path.toLowerCase().contains("robocode.plugin")) {
				loadFromPath(path);
			}
		}
		// load extensions modules
		for (String path : cp) {
			if (path.toLowerCase().contains("robocode.plugin")) {
				loadFromPath(path);
			}
		}

		if (known.size() < 2) {
			Logger.logError("Main modules not loaded, something went wrong. We have only " + known.size() + " modules");
			Logger.logError("Class path: " + classPath);
			throw new Error("Main modules not loaded");
		}

		for (IModule module : modules) {
			module.afterLoaded(modules);
		}
	}

	static void init() {}

	private static void loadFromPath(String path) {
		try {
			File pathf = new File(path).getCanonicalFile();

			path = pathf.toString();
			final String test = path.toLowerCase();

			if (pathf.isDirectory()) {
				String name = getModuleName(path);

				if (name != null) {
					loadModule(name, engineLoader);
				}
			} else if (test.contains(File.separator + "robocode.") && test.endsWith(".jar")) {
				final int i = test.lastIndexOf("robocode.jar");

				if (i > 0) {
					// load other .jar files in location
					final File dir = new File(path.substring(0, i));

					Logger.logMessage("Loading plugins from " + dir);
					loadJars(dir);
				} else {
					String name = getModuleName(path);

					if (name != null) {
						loadModule(name, engineLoader);
					}
				}
			}
		} catch (IOException e) {
			Logger.logError(e);
		}
	}

	private static void loadJars(File pathf) {
		final File[] files = pathf.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().startsWith("robocode") && name.toLowerCase().endsWith(".jar");
			}
		});

		for (File file : files) {
			loadModule(getModuleName(file.toString()), engineLoader);
		}
	}

	private static boolean loadModule(String module, ClassLoader loader) {
		try {
			if (known.contains(module)) {
				// Logger.logMessage("already loaded " + module);
				return false;
			}
			Class<?> modClass = loader.loadClass(module + ".Module");

			final Object moduleInstance = modClass.newInstance();

			if (moduleInstance instanceof IModule) {
				modules.add((IModule) moduleInstance);
			}
			Logger.logMessage("Loaded " + module);
			known.add(module);
			return true;
		} catch (ClassNotFoundException ignore) {// it is not our module ?
			// Logger.logMessage("Can't load " + module);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (InstantiationException e) {
			Logger.logError(e);
		}
		return false;
	}

	private static String getModuleName(String path) {
		final String test = path.toLowerCase();

		if (test.endsWith("robocode.jar") || test.contains("robocode.api")) {
			return null;
		}
		int i = test.lastIndexOf("robocode.");

		if (i > 0) {
			String name = path.substring(i);

			i = name.indexOf(File.separator);
			if (i > 0) {
				return "net.sf." + name.substring(0, i);
			}
			i = name.indexOf("-");
			if (i > 0) {
				return "net.sf." + name.substring(0, i);
			}
			return "net.sf." + name;
		}
		return null;
	}

	static URL[] findJars(String allowed) {
		List<String> urls = new ArrayList<String>();
		final String classPath = System.getProperty("robocode.class.path", null);

		for (String path : classPath.split(File.pathSeparator)) {
			String test = path.toLowerCase();

			if (test.contains(allowed)) {
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
				urls[i] = f.getCanonicalFile().toURI().toURL();
			} catch (MalformedURLException e) {
				Logger.logError(e);
			} catch (IOException e) {
				Logger.logError(e);
			}
		}
		return urls;
	}

	protected <T> T getBaseComponent(final Class<T> tClass) {
		return cache.getComponent(tClass);
	}

	public static <T> T getComponent(java.lang.Class<T> tClass) {
		return cache.getComponent(tClass);
	}

	public static <T> T getComponent(java.lang.Class<T> tClass, String className) {
		final List<T> list = cache.getComponents(tClass);

		for (T component : list) {
			if (component.getClass().getName().endsWith(className)) {
				return component;
			}
		}
		return null;
	}

	public static <T> List<T> getComponents(java.lang.Class<T> tClass) {
		return cache.getComponents(tClass);
	}

	public static <T> T createComponent(java.lang.Class<T> tClass) {
		return factory.as(Characteristics.NO_CACHE).getComponent(tClass);
	}
}
