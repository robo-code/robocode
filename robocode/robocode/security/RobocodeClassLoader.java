/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.security;


import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.security.*;

import robocode.peer.robot.RobotClassManager;
import robocode.packager.*;
import robocode.repository.*;


/**
 * Insert the type's description here.
 * Creation date: (1/24/2001 12:41:30 PM)
 * @author: Mathew A. Nelson
 */
public class RobocodeClassLoader extends ClassLoader {

	private Hashtable cachedClasses = new Hashtable();
	
	private java.lang.ClassLoader parent;
	
	// private robocode.peer.RobotPeer robotPeer = null;
	private RobotSpecification robotSpecification = null;
	private robocode.peer.robot.RobotClassManager robotClassManager = null;
	private String rootPackageDirectory = null;
	private String rootDirectory = null;
	private String classDirectory = null;
	private String shortName = null;
	private java.util.Hashtable jarClasses = new Hashtable();
	private ProtectionDomain protectionDomain = null;
	
	long uid1 = 0;
	long uid2 = 0;
	
	/**
	 * RobocodeClassLoader constructor
	 * @param parent java.lang.ClassLoader
	 */
	public RobocodeClassLoader(ClassLoader parent, RobotClassManager robotClassManager) {
		super(parent);
		this.parent = parent;
		this.robotClassManager = robotClassManager;
		this.robotSpecification = robotClassManager.getRobotSpecification();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/24/2001 12:29:26 PM)
	 * @return robocode.Robocode
	 */
	public String getClassDirectory() {
		return classDirectory;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/17/2001 1:53:49 PM)
	 * @return java.io.InputStream
	 * @param resource java.lang.String
	 */
	public InputStream getResourceAsStream(String resource) {
		log("Classloader:  getResourceAsStream: " + resource);
		return super.getResourceAsStream(resource);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/24/2001 12:29:26 PM)
	 * @return robocode.Robocode
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}

	public void finalize() {// System.out.println("classloader finalizing.");
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/24/2001 12:29:26 PM)
	 * @return robocode.Robocode
	 */
	public String getRootPackageDirectory() {
		return rootPackageDirectory;
	}

	public synchronized Class loadClass(String className, boolean resolve) throws ClassNotFoundException {

		if (className.indexOf(robotClassManager.getRootPackage() + ".") == 0) {
			return loadRobotClass(className, false);
		}
		
		// if (className.indexOf("javax.swing.SwingUtilities") == 0)
		// {
		// throw new SecurityException("You may not use classes from javax.swing.");
		// }
		// if (className.equals("java.awt.Component"))
		// {
		// throw new SecurityException("You may not use AWT components.");
		// }
		// log(robotSpecification.getName() + ": loadClass not in robot's package: " + className + " with " + resolve);
		try {
			Class c = super.loadClass(className, resolve);

			// log("Found " + className + " in classpath.");
			return c;
		} catch (ClassNotFoundException e) {}
		// log(robotPeer.getName() + ": Trying loadRobotClass " + className);
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

		// log("loadRobotClass with " + name + "," + toplevel);
		if (!name.equals(robotClassManager.getFullClassName())) {
			if (robotClassManager.getRootPackage() == null) {
				log(
						robotClassManager.getFullClassName() + " is not in a package, but is trying to reference class "
						+ name);
				log("To do this in Robocode, you must put your robot into a package.");
				throw new ClassNotFoundException(
						robotClassManager.getFullClassName() + "is not in a package, but is trying to reference class " + name);
			}
			// else if (name.indexOf(robotClassManager.getRootPackage() + ".") != 0)
			// {
			// log(robotClassManager.getFullClassName() + " is not in the same package as " + name + ".  This is not allowed in Robocode.");
			// throw new ClassNotFoundException(robotClassManager.getFullClassName() + "is not in the same package as " + name + ".  This is not allowed in Robocode.");
			// }
		}
	
		// System.out.println("Proceeding to load " + name + " for " + robotClassManager.getFullClassName());
	
		String filename = name.replace('.', File.separatorChar) + ".class";

		String classPath = robotSpecification.getRobotClassPath();

		// System.out.println("Classpath is: " + classPath);
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

				protectionDomain = new ProtectionDomain(new CodeSource(f.toURL(), null), p);
				// System.out.println("Created a new protection domain: " + protectionDomain);
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
			// log("ClassLoader: Getting referenced classes for: " + name);
			Vector v = ClassAnalyzer.getReferencedClasses(buff);

			// if (v != null)
			// for (int i = 0; i < v.size(); i++)
			// {
			// log("Adding class: " + v.elementAt(i));
			// }
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
					log("Unexpected error:  Cannot build canonical path for " + rootPackageDirectory);
				}
			
			}
			if (toplevel) {
				robotClassManager.loadUnresolvedClasses();
				robotClassManager.setUid(uid1 + "" + uid2);		
			}
		
			cachedClasses.put(name, c);
			return c;
		} catch (java.io.FileNotFoundException e) {
			throw new ClassNotFoundException("Could not find: " + name + ": " + e);
		} catch (java.io.IOException e) {
			throw new ClassNotFoundException("Could not find: " + name + ": " + e);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/24/2001 1:28:09 PM)
	 * @param s java.lang.String
	 */
	private void log(String s) {
		System.out.println("SYSTEM: " + s);
	}
}
