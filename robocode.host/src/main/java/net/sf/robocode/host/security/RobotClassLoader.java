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
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.cert.Certificate;
import java.util.Set;
import java.util.HashSet;


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
public class RobotClassLoader extends URLClassLoader {
	private static final boolean isSecutityOn = !System.getProperty("NOSECURITY", "false").equals("true");
	private Field classesField = null;
	private Class<?> robotClass;
	private final String fullClassName;
	private PermissionCollection emptyPermissions;
	private IHostedThread robotProxy;
	private CodeSource codeSource;
	private Set<String> referencedClasses = new HashSet<String>();
	public static final String untrustedURL = "http://robocode.sf.net/untrusted";
	private long uid1;
	private long uid2;

	public RobotClassLoader(URL robotClassPath, String robotFullClassName) {
		this(robotClassPath, robotFullClassName, null);
	}
	
	public RobotClassLoader(URL robotClassPath, String robotFullClassName, IHostedThread robotProxy) {
		super(new URL[] { robotClassPath}, Container.systemLoader);
		prepareForCleanup();
		fullClassName = robotFullClassName;
		emptyPermissions = new Permissions();
		this.robotProxy = robotProxy;
		try {
			codeSource = new CodeSource(new URL(untrustedURL), (Certificate[]) null);
		} catch (MalformedURLException ignored) {}
	}

	public synchronized Class<?> loadClass(String name, boolean resolve)
		throws ClassNotFoundException {
		if (name.startsWith("java.lang")) {
			// we always delegate java.lang stuff to parent loader
			return super.loadClass(name, resolve);
		}
		if (isSecutityOn) {
			if (name.startsWith("net.sf.robocode")) {
				final String message = "Robots are not alowed to reference robocode engine in net.sf.robocode package";

				notifyRobot(message);
				throw new ClassNotFoundException(message);
			}
			if (name.startsWith("robocode.control")) {
				final String message = "Robots are not alowed to reference robocode engine in robocode.control package";

				notifyRobot(message);
				throw new ClassNotFoundException(message);
			}
			if (name.startsWith("javax.swing")) {
				final String message = "Robots are not alowed to reference javax.swing package";

				notifyRobot(message);
				throw new ClassNotFoundException(message);
			}

			if (!name.startsWith("robocode")) {
				final Class<?> result = loadRobotClassLocaly(name, resolve);

				if (result != null) {
					// yes, it is in robot's classpath
					// we loaded it localy
					return result;
				}
			}
		}
		// it is robot API
		// or java class
		// or security is off
		// so we delegate to parrent classloader
		return super.loadClass(name, resolve);
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
		String path = name.replace('.', File.separatorChar).concat(".class");

		final URL url = findResource(path);
		ByteBuffer result = null;

		if (url != null) {
			try {
				final InputStream is = url.openStream();

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
			}
		}
		return result;
	}

	private void notifyRobot(String s) {
		if (robotProxy != null) {
			robotProxy.println(s);
			robotProxy.drainEnergy();
		}
	}

	protected PermissionCollection getPermissions(CodeSource codesource) {
		if (isSecutityOn) {
			return emptyPermissions;
		}
		return super.getPermissions(codesource);
	}

	public String getUid() {
		// TODO ZAMO
		return null;
	}

	public Set<String> getReferencedClasses() {
		return referencedClasses;
	}

	public Class<?> loadRobotMainClass() throws ClassNotFoundException {
		try {
			if (robotClass == null) {
				robotClass = loadClass(fullClassName, true);
				// just to force dependencies resolution
				robotClass.getMethods();
			}
		} catch (Throwable e) {
			robotClass = null;
			throw new ClassNotFoundException(
					e.getMessage()
							+ "\nRobots are not alowed to reference robocode engine in robocode.control or net.sf.robocode packages",
							e); 
		}
		return robotClass;
	}

	public void cleanup() {
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
