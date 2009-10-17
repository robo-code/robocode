/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Ported to Java 5.0
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Fixed method synchronization issues with several member fields
 *     Matthew Reeder
 *     - Fixed compiler problem with protectionDomain
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *     - Added cleanup of hidden ClassLoader.class.classes
 *******************************************************************************/
package net.sf.robocode.host.security;


import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.IRobotClassLoader;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;
import static net.sf.robocode.io.Logger.logError;
import robocode.robotinterfaces.IBasicRobot;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.Set;


/**
 * This classloader is used by robots. It isolates classes which belong to robot and load them localy.
 * General java clasees or robocode.api classes are loaded by parent loader and shared with robocode engine.
 * Attempts to load classes of robocode engine are blocked. 
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobotClassLoader extends URLClassLoader implements IRobotClassLoader {
	private static final boolean isSecutityOn = !System.getProperty("NOSECURITY", "false").equals("true");
	private Field classesField = null;
	protected Class<?> robotClass;
	protected final String fullClassName;
	private PermissionCollection emptyPermissions;
	private IHostedThread robotProxy;
	private CodeSource codeSource;
	protected URL robotClassPath;
	private Set<String> referencedClasses = new HashSet<String>();
	public static final String untrustedURL = "http://robocode.sf.net/untrusted";
	private ClassLoader parent;

	public RobotClassLoader(URL robotClassPath, String robotFullClassName) {
		super(new URL[] { robotClassPath}, Container.systemLoader);
		prepareForCleanup();
		fullClassName = robotFullClassName;
		this.robotClassPath = robotClassPath;
		emptyPermissions = new Permissions();
		parent = getParent();
		try {
			codeSource = new CodeSource(new URL(untrustedURL), (Certificate[]) null);
		} catch (MalformedURLException ignored) {}
	}

	public void setRobotProxy(Object robotProxy) {
		this.robotProxy = (IHostedThread) robotProxy;
	}

	public synchronized void addURL(URL url) {
		super.addURL(url);
	}

	public synchronized Class<?> loadClass(final String name, boolean resolve)
		throws ClassNotFoundException {
		if (name.startsWith("java.lang")) {
			// we always delegate java.lang stuff to parent loader
			return super.loadClass(name, resolve);
		}
		if (isSecutityOn) {
			testPackages(name);
		}
		if (!name.startsWith("robocode")) {
			final Class<?> result = loadRobotClassLocaly(name, resolve);

			if (result != null) {
				// yes, it is in robot's classpath
				// we loaded it localy
				return result;
			}
		}
		
		// it is robot API
		// or java class
		// or security is off
		// so we delegate to parent classloader
		return parent.loadClass(name);
	}

	private void testPackages(String name) throws ClassNotFoundException {
		if (name.startsWith("net.sf.robocode")) {
			final String message = "Robots are not allowed to reference Robocode engine in package: net.sf.robocode";

			punishSecurityViolation(message);
			throw new ClassNotFoundException(message);
		}
		if (name.startsWith("robocode.control")) {
			final String message = "Robots are not allowed to reference Robocode engine in package: robocode.control";

			punishSecurityViolation(message);			
			throw new ClassNotFoundException(message);
		}
		if (isSecutityOn && name.startsWith("javax.swing")) {
			final String message = "Robots are not allowed to reference Robocode engine in package: javax.swing";

			punishSecurityViolation(message);			
			throw new ClassNotFoundException(message);
		}
	}

	private Class<?> loadRobotClassLocaly(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> result = findLoadedClass(name);

		if (result == null) {
			final ByteBuffer resource = findLocalResource(name);

			if (resource != null) {
				result = defineClass(name, resource, codeSource);
				if (resolve) {
					resolveClass(result);
				}
				ClassAnalyzer.getReferencedClasses(resource, referencedClasses);
			}
		}
		return result;
	}

	// this whole fun is there to be able to provide defineClass with bytes
	// we need to call defineClass to be able to set codeSource to untrustedLocation  
	private ByteBuffer findLocalResource(String name) {
		// try to find it in robot's classpath
		// this is URL, don't change to File.pathSeparator 
		String path = name.replace('.', '/').concat(".class");

		final URL url = findResource(path);
		ByteBuffer result = null;
		InputStream is = null;

		if (url != null) {
			try {
				final URLConnection connection = URLJarCollector.openConnection(url);

				is = connection.getInputStream();

				result = ByteBuffer.allocate(1024 * 8);
				boolean done = false;

				do {
					do {
						int res = is.read(result.array(), result.position(), result.remaining());

						if (res == -1) {
							done = true;
							break;
						}
						result.position(result.position() + res);
					} while (result.remaining() != 0);
					result.flip();
					if (!done) {
						result = ByteBuffer.allocate(result.capacity() * 2).put(result);
					}
				}while (!done);

			} catch (IOException e) {
				Logger.logError(e);
				return null;
			} finally {
				FileUtil.cleanupStream(is);
			}
		}
		return result;
	}

	private void punishSecurityViolation(String message) {
		if (robotProxy != null) {
			robotProxy.punishSecurityViolation(message);
		}
	}

	protected PermissionCollection getPermissions(CodeSource codesource) {
		if (isSecutityOn) {
			return emptyPermissions;
		}
		return super.getPermissions(codesource);
	}

	public String[] getReferencedClasses() {
		return referencedClasses.toArray(new String[referencedClasses.size()]);
	}

	public synchronized Class<?> loadRobotMainClass(boolean resolve) throws ClassNotFoundException {
		try {
			if (robotClass == null) {
				robotClass = loadClass(fullClassName, false);

				if (!IBasicRobot.class.isAssignableFrom(robotClass)) {
					// that's not robot
					return null;
				}
				if (resolve) {
					robotClass = loadClass(fullClassName, true);

					// resolve methods to see more referenced classes
					robotClass.getMethods();

					// itterate thru dependencies until we didn't found any new
					HashSet<String> clone;

					do {
						clone = new HashSet<String>(referencedClasses);
						for (String reference : clone) {
							testPackages(reference);
							if (!reference.startsWith("java.") && !reference.startsWith("robocode.")) {
								loadClass(reference, true);
							}
						}
					} while (referencedClasses.size() != clone.size());
				}
			}
		} catch (Throwable e) {
			robotClass = null;
			throw new ClassNotFoundException(e.getMessage(), e);
		}
		return robotClass;
	}

	public IBasicRobot createRobotInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		loadRobotMainClass(true);
		return (IBasicRobot) robotClass.newInstance();
	}

	public void cleanup() {
		referencedClasses = null;
		// Set ClassLoader.class.classes to null to prevent memory leaks
		if (classesField != null) {
			try {
				// don't do that Internal Error (44494354494F4E4152590E4350500100)
				// classesField.setAccessible(true);
				classesField.set(this, null);
			} catch (IllegalArgumentException e) {// logError(e);
			} catch (IllegalAccessException e) {// logError(e);
			}
		}
	}

	private void prepareForCleanup() {
		// Deep within the class loader is a vector of classes, and is VM
		// implementation specific, so its not in every VM. However, if a VM
		// does have it then we have to make sure we clear it during cleanup().
		Field[] fields = ClassLoader.class.getDeclaredFields();

		for (Field field : fields) {
			if (field.getName().equals("classes")) {
				classesField = field;
				break;
			}
		}

		if (classesField == null) {
			logError("RobotClassLoader: Failed to find classes field in: " + this);
		}
	}
}
