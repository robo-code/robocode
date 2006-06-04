/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Matthew Reeder
 *     - Fixed compiler problem with protectionDomain
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.security;


import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.security.*;

import robocode.peer.robot.RobotClassManager;
import robocode.packager.*;
import robocode.repository.*;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
public class RobocodeClassLoader extends ClassLoader {

	private Hashtable cachedClasses = new Hashtable(); // <String, Class>
	
	private RobotSpecification robotSpecification;
	private robocode.peer.robot.RobotClassManager robotClassManager;
	private String rootPackageDirectory;
	private String rootDirectory;
	private String classDirectory;
	private ProtectionDomain protectionDomain;
	
	long uid1;
	long uid2;
	
	public RobocodeClassLoader(ClassLoader parent, RobotClassManager robotClassManager) {
		super(parent);
		this.robotClassManager = robotClassManager;
		this.robotSpecification = robotClassManager.getRobotSpecification();
	}

	public String getClassDirectory() {
		return classDirectory;
	}

	public InputStream getResourceAsStream(String resource) {
		Utils.log("Classloader:  getResourceAsStream: " + resource);
		return super.getResourceAsStream(resource);
	}

	public String getRootDirectory() {
		return rootDirectory;
	}

	public String getRootPackageDirectory() {
		return rootPackageDirectory;
	}

	public synchronized Class loadClass(String className, boolean resolve) throws ClassNotFoundException {

		if (className.indexOf(robotClassManager.getRootPackage() + ".") == 0) {
			return loadRobotClass(className, false);
		}
		try {
			Class c = super.loadClass(className, resolve);

			return c;
		} catch (ClassNotFoundException e) {}
		return loadRobotClass(className, false);
	}

	public synchronized Class loadRobotClass(String name, boolean toplevel) throws ClassNotFoundException {
		if (cachedClasses.containsKey(name)) {
			return (Class) cachedClasses.get(name);
		}

		Class c = null;
		File f = null;

		if (toplevel) {
			uid1 = 0;
			uid2 = 0;
		}

		if (!name.equals(robotClassManager.getFullClassName())) {
			if (robotClassManager.getRootPackage() == null) {
				Utils.log(
						robotClassManager.getFullClassName() + " is not in a package, but is trying to reference class "
						+ name);
				Utils.log("To do this in Robocode, you must put your robot into a package.");
				throw new ClassNotFoundException(
						robotClassManager.getFullClassName() + "is not in a package, but is trying to reference class " + name);
			}
		}
	
		String filename = name.replace('.', File.separatorChar) + ".class";

		String classPath = robotSpecification.getRobotClassPath();

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
						new CodeSource(f.toURL(), (java.security.cert.Certificate[]) null), p);
			} catch (MalformedURLException e) {
				throw new ClassNotFoundException("Unable to build protection domain.");
			}
		}
		int size = (int) (f.length());

		uid1 += size;
		byte buff[] = new byte[size];

		try {
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
		
			dis.readFully(buff);
			dis.close();
			Vector v = ClassAnalyzer.getReferencedClasses(buff);

			robotClassManager.addReferencedClasses(v);
			uid1 += v.size();
			for (int i = 0; i < buff.length; i++) {
				uid2 += buff[i];
			}
			c = defineClass(name, buff, 0, buff.length, protectionDomain);
			robotClassManager.addResolvedClass(name);
			if (name.equals(robotClassManager.getFullClassName())) {
				try {
					if (robotClassManager.getRootPackage() == null) {
						rootPackageDirectory = null;
						classDirectory = null;
						rootDirectory = new File(classPath).getCanonicalPath();
					} else {
						rootPackageDirectory = new File(classPath + File.separator + robotClassManager.getRootPackage() + File.separator).getCanonicalPath();
						classDirectory = new File(classPath + File.separator + robotClassManager.getClassNameManager().getFullPackage().replace('.', File.separatorChar) + File.separator).getCanonicalPath();
						rootDirectory = new File(classPath).getCanonicalPath();
					}
				} catch (Exception e) {
					rootPackageDirectory = new File(classPath + File.separator + robotClassManager.getRootPackage() + File.separator).getAbsolutePath();
					Utils.log("Unexpected error:  Cannot build canonical path for " + rootPackageDirectory);
				}
			}
			if (toplevel) {
				robotClassManager.loadUnresolvedClasses();
				robotClassManager.setUid(uid1 + "" + uid2);		
			}
		
			cachedClasses.put(name, c);
			return c;
		} catch (FileNotFoundException e) {
			throw new ClassNotFoundException("Could not find: " + name + ": " + e);
		} catch (IOException e) {
			throw new ClassNotFoundException("Could not find: " + name + ": " + e);
		}
	}
}
