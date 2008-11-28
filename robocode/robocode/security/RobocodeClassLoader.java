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
package robocode.security;


import static robocode.io.Logger.logError;
import static robocode.io.Logger.logMessage;
import robocode.packager.ClassAnalyzer;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.RobotFileSpecification;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobocodeClassLoader extends ClassLoader {
	private final Map<String, Class<?>> cachedClasses = new HashMap<String, Class<?>>();

	private RobotFileSpecification robotFileSpecification;
	private RobotClassManager robotClassManager;
	private String rootPackageDirectory;
	private String rootDirectory;
	private String classDirectory;
	private ProtectionDomain protectionDomain;

	private long uid1;
	private long uid2;

	// The hidden ClassLoader.class.classes field
	private Field classesField = null;

	public RobocodeClassLoader(ClassLoader parent, RobotClassManager robotClassManager) {
		super(parent);
		this.robotClassManager = robotClassManager;
		this.robotFileSpecification = robotClassManager.getRobotSpecification();

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
			logError("RobocodeClassLoader: Failed to find classes field in: " + this);
		}
	}

	public synchronized String getClassDirectory() {
		return classDirectory;
	}

	@Override
	public InputStream getResourceAsStream(String resource) {
		logMessage("RobocodeClassLoader: getResourceAsStream: " + resource);
		return super.getResourceAsStream(resource);
	}

	public synchronized String getRootDirectory() {
		return rootDirectory;
	}

	public synchronized String getRootPackageDirectory() {
		return rootPackageDirectory;
	}

	@Override
	public synchronized Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
		if (className.indexOf(getRobotClassManager().getRootPackage() + ".") == 0) {
			return loadRobotClass(className, false);
		}
		try {
			return super.loadClass(className, resolve);
		} catch (ClassNotFoundException e) {
			return loadRobotClass(className, false);
		}
	}

	public synchronized Class<?> loadRobotClass(String name, boolean toplevel) throws ClassNotFoundException {
		if (cachedClasses.containsKey(name)) {
			return cachedClasses.get(name);
		}

		Class<?> c;
		File f;

		if (toplevel) {
			uid1 = 0;
			uid2 = 0;
		}

		if (!name.equals(robotClassManager.getFullClassName())) {
			if (robotClassManager.getRootPackage() == null) {
				logError(
						robotClassManager.getFullClassName() + " is not in a package, but is trying to reference class "
						+ name);
				logError("To do this in Robocode, you must put your robot into a package.");
				throw new ClassNotFoundException(
						robotClassManager.getFullClassName() + "is not in a package, but is trying to reference class " + name);
			}
		}

		String filename = name.replace('.', File.separatorChar) + ".class";

		String classPath = robotFileSpecification.getRobotClassPath();

		if (classPath.indexOf(File.pathSeparator) >= 0) {
			throw new ClassNotFoundException(
					"A robot cannot have multiple directories or jars in it's classpath: " + name);
		}

		f = new File(classPath + File.separator + filename);
		if (protectionDomain == null) {
			try {
				// Java 1.4 only:
				// If we want to use a Policy object to control access, we could do this:
				// protectionDomain = new ProtectionDomain(new CodeSource(f.toURL(),null),new Permissions(),this,null);
				// We *cannot* do this anymore, as the robots directory is now allowed to be in the classpath

				// But it's easier to use the statically-linked version, to simply say
				// that this class is not allowed to do anything.
				// Note that we only create one protection domain for this classloader, so the
				// "code source" is simply the robot itself.
				Permissions p = new Permissions();

				protectionDomain = new ProtectionDomain(
						new CodeSource(f.toURI().toURL(), (java.security.cert.Certificate[]) null), p);
			} catch (MalformedURLException e) {
				throw new ClassNotFoundException("Unable to build protection domain", e);
			}
		}
		int size = (int) (f.length());

		uid1 += size;
		byte buff[] = new byte[size];

		FileInputStream fis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(f);
			dis = new DataInputStream(fis);

			dis.readFully(buff);
			dis.close();
			List<String> v = ClassAnalyzer.getReferencedClasses(buff);

			robotClassManager.addReferencedClasses(v);
			uid1 += v.size();
			for (byte element : buff) {
				uid2 += element;
			}
			c = defineClass(name, buff, 0, buff.length, protectionDomain);

			robotClassManager.addResolvedClass(name);
			if (name.equals(robotClassManager.getFullClassName())) {
				if (robotClassManager.getRootPackage() == null) {
					rootPackageDirectory = null;
					classDirectory = null;
				} else {
					rootPackageDirectory = new File(classPath + File.separator + robotClassManager.getRootPackage() + File.separator).getCanonicalPath();
					classDirectory = new File(classPath + File.separator + robotClassManager.getClassNameManager().getFullPackage().replace('.', File.separatorChar) + File.separator).getCanonicalPath();
				}
				rootDirectory = new File(classPath).getCanonicalPath();
			}
			if (toplevel) {
				robotClassManager.loadUnresolvedClasses();
				robotClassManager.setUid(uid1 + "" + uid2);
			}

			cachedClasses.put(name, c);
			return c;
		} catch (IOException e) {
			throw new ClassNotFoundException("Could not find: " + name, e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignored) {}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException ignored) {}
			}
		}
	}

	private synchronized RobotClassManager getRobotClassManager() {
		return robotClassManager;
	}

	public void cleanup() {
		if (cachedClasses != null) {
			cachedClasses.clear();
		}

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

		robotClassManager = null;
		robotFileSpecification = null;
	}
}
