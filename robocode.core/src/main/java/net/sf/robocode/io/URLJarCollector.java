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
package net.sf.robocode.io;


import java.net.URLConnection;
import java.net.URL;
import java.lang.reflect.Field;
import java.util.*;
import java.util.jar.JarFile;
import java.io.IOException;


/**
 * This ugly class is helping with closing of robot .jar files when used with URL, URLConnection and useCaches=true
 * It is designed to close JarFiles opened and cached in SUN's JarFileFactory.
 * If we are not on SUN's JVM, we fallback to useCaches=false, to not lock the files.
 * Collection is now called after reposiotry refresh and after battle ended.
 * Collection is disabled/posponed during running battle.
 * 
 * @author Pavel Savara (original)
 */
public class URLJarCollector {
	static Object factory;
	static HashMap<?, ?> fileCache;
	static HashMap<?, ?> urlCache;
	static Field jarFileURL;
	static boolean sunJVM;
	static boolean enabled;
	static Set<URL> urlToClean = new HashSet<URL>();

	static {
		try {
			final Class<?> jarConn = ClassLoader.getSystemClassLoader().loadClass(
					"sun.net.www.protocol.jar.JarURLConnection");
			final Field factoryF = jarConn.getDeclaredField("factory");

			factoryF.setAccessible(true);
			factory = factoryF.get(null);
			factoryF.setAccessible(false);

			final Class<?> jarFactory = ClassLoader.getSystemClassLoader().loadClass(
					"sun.net.www.protocol.jar.JarFileFactory");
			final Field fileCacheF = jarFactory.getDeclaredField("fileCache");

			fileCacheF.setAccessible(true);
			fileCache = (HashMap<?, ?>) fileCacheF.get(null);
			fileCacheF.setAccessible(false);

			final Field urlCacheF = jarFactory.getDeclaredField("urlCache");

			urlCacheF.setAccessible(true);
			urlCache = (HashMap<?, ?>) urlCacheF.get(null);
			urlCacheF.setAccessible(false);

			final Class<?> jarURLConnection = ClassLoader.getSystemClassLoader().loadClass(
					"sun.net.www.protocol.jar.JarURLConnection");

			jarFileURL = jarURLConnection.getDeclaredField("jarFileURL");
			jarFileURL.setAccessible(true);

			sunJVM = true;
		} catch (ClassNotFoundException ignore) {
			Logger.logError(ignore);
		} catch (NoSuchFieldException ignore) {
			Logger.logError(ignore);
		} catch (IllegalAccessException ignore) {
			Logger.logError(ignore);
		}
	}

	public static synchronized URLConnection openConnection(URL url) throws IOException {
		// Logger.logMessage("open " + url);
		final URLConnection urlConnection = url.openConnection();

		if (sunJVM) {
			registerConnection(urlConnection);
			urlConnection.setUseCaches(true);
		} else {
			urlConnection.setUseCaches(false);
		}
		return urlConnection;
	}

	public static synchronized void enableGc(boolean enabled) {
		URLJarCollector.enabled = enabled;
	}

	public static synchronized void gc() {
		if (sunJVM && enabled) {
			for (URL url : urlToClean) {
				closeJarURLConnection(url);
			}
			urlToClean.clear();
			// dump();
		}
	}

	private static void registerConnection(URLConnection conn) {
		if (conn != null) {
			final String cl = conn.getClass().getName();

			if (cl.equals("sun.net.www.protocol.jar.JarURLConnection")) {
				try {
					final URL url = (URL) jarFileURL.get(conn);

					if (!urlToClean.contains(url)) {
						urlToClean.add(url);
					}
				} catch (IllegalAccessException ignore) {}
			}
		}
	}

	private static void closeJarURLConnection(URL url) {
		if (url != null) {
			JarFile jarFile = (JarFile) fileCache.get(url);

			if (jarFile != null) {
				fileCache.remove(url);
				urlCache.remove(jarFile);
				try {
					jarFile.close();
				} catch (IOException e) {
					Logger.logError(e);
				}
			}
		}
	}

	public static void dump() {
		if (sunJVM) {
			for (Object u : fileCache.keySet()) {
				final JarFile o = (JarFile) fileCache.get(u);

				Logger.logMessage("remain " + u.toString() + " " + o.getName());
			}
			Logger.logMessage("count " + fileCache.size());
		}
	}
}
