/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.IRobotClassLoader;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import robocode.robotinterfaces.IBasicRobot;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * This class loader is used by robots. It isolates classes which belong to robot and load them locally.
 * General java classes or robocode.api classes are loaded by parent loader and shared with Robocode engine.
 * Attempts to load classes of Robocode engine are blocked. 
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobotClassLoader extends URLClassLoader implements IRobotClassLoader {

	static final String UNTRUSTED_URL = "http://robocode.sf.net/untrusted";

	private static final PermissionCollection EMPTY_PERMISSIONS = new Permissions();

	private final String fullClassName;

	private ClassLoader parent;
	private CodeSource codeSource;

	private IHostedThread robotProxy;
	private Class<?> robotClass;

	// Names on classes referenced from the robot class
	private Set<String> referencedClasses = new HashSet<String>();

	// Cached names on found system classes
	private Set<String> foundSystemClasses = new HashSet<String>();

	// Cached warning messages
	private String[] staticRobotInstanceWarning;  

	public RobotClassLoader(URL robotClassPath, String robotFullClassName) {
		super(new URL[] { robotClassPath}, Container.systemLoader);
		fullClassName = robotFullClassName;
		parent = getParent();
		try {
			codeSource = new CodeSource(new URL(UNTRUSTED_URL), (Certificate[]) null);
		} catch (MalformedURLException ignored) {}
	}

	public void setRobotProxy(Object robotProxy) {
		this.robotProxy = (IHostedThread) robotProxy;
	}

	public synchronized void addURL(URL url) {
		super.addURL(url);
	}

	public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (name.startsWith("java.lang") || name.startsWith("kotlin.")) {
			// we always delegate java.lang and Kotlin stuff to parent loader
			return super.loadClass(name, resolve);
		}
		if (RobocodeProperties.isSecurityOn()) {
			testPackages(name);
		}
		if (!name.startsWith("robocode")) {
			Class<?> result = loadRobotClassLocaly(name, resolve);
			if (result != null) {
				// yes, it is in robot's class path
				// we loaded it locally
				return result;
			}
		}

		// it is robot API
		// or java class
		// or security is off
		// so we delegate to parent class loader
		return parent.loadClass(name);
	}

	private void testPackages(String name) throws ClassNotFoundException {
		if (name.startsWith("net.sf.robocode")) {
			String message = "Robots are not allowed to reference Robocode engine in package: net.sf.robocode";

			punishSecurityViolation(message);
			throw new ClassNotFoundException(message);
		}
		if (name.startsWith("robocode.control")) {
			String message = "Robots are not allowed to reference Robocode engine in package: robocode.control";

			punishSecurityViolation(message);
			throw new ClassNotFoundException(message);
		}
		if (RobocodeProperties.isSecurityOn() && name.startsWith("javax.swing")) {
			String message = "Robots are not allowed to reference Robocode engine in package: javax.swing";

			punishSecurityViolation(message);
			throw new ClassNotFoundException(message);
		}
	}

	private Class<?> loadRobotClassLocaly(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> result = findLoadedClass(name);
		if (result == null) {
			ByteBuffer resource = findLocalResource(name);
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
	private ByteBuffer findLocalResource(final String name) {
		return AccessController.doPrivileged(new PrivilegedAction<ByteBuffer>() {
			public ByteBuffer run() {
				// try to find it in robot's class path
				// this is URL, don't change to File.pathSeparator
				String path = name.replace('.', '/').concat(".class");
				URL url = findResource(path);

				return ClassFileReader.readClassFileFromURL(url);
			}
		});
	}

	private void punishSecurityViolation(String message) {
		if (robotProxy != null) {
			robotProxy.punishSecurityViolation(message);
		}
	}

	protected PermissionCollection getPermissions(CodeSource codesource) {
		if (RobocodeProperties.isSecurityOn()) {
			return EMPTY_PERMISSIONS;
		}
		return super.getPermissions(codesource);
	}

	public String[] getReferencedClasses() {
		return referencedClasses.toArray(new String[referencedClasses.size()]);
	}

	public synchronized Class<?> loadRobotMainClass(boolean resolve) throws ClassNotFoundException {
		if (fullClassName == null) return null;


		try {
			if (robotClass == null) {
				robotClass = loadClass(fullClassName, resolve);

				if (!IBasicRobot.class.isAssignableFrom(robotClass)) {
					// that's not robot
					return null;
				}
				if (resolve) {
					// resolve methods to see more referenced classes
					robotClass.getMethods();

					// iterate thru dependencies until we didn't found any new
					HashSet<String> clone;

					do {
						clone = new HashSet<String>(referencedClasses);
						for (String reference : clone) {
							testPackages(reference);
							if (!isSystemClass(reference)) {
								loadClass(reference, true);
							}
						}
					} while (referencedClasses.size() != clone.size());
				}
			} else {
				warnIfStaticRobotInstanceFields();
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
		// Bug fix [2930266] - Robot static data isn't being GCed after battle
		for (String className : getReferencedClasses()) {		
			cleanStaticReferences(className);
		}

		parent = null;
		codeSource = null;
		robotProxy = null;
		robotClass = null;
		referencedClasses = null;
		foundSystemClasses = null;
	}

	/**
	 * Cleans all static field references on a class.
	 *
	 * @param className the name of the class containing the static references to clean.
	 */
	private void cleanStaticReferences(String className) {
		if (isSystemClass(className)) {
			return;
		}
		Class<?> type = null;
		try {
			type = loadRobotClassLocaly(className, false);
			if (type != null) {
				for (Field field : getAllFields(new ArrayList<Field>(), type)) {
					if (isStaticReference(field)) {
						cleanStaticReference(field);
					}
				}
			}
		} catch (Throwable t) {
			Logger.logError(t);
		}
	}

	private void warnIfStaticRobotInstanceFields() {
		if (staticRobotInstanceWarning == null) {
			List<Field> staticRobotReferences = new ArrayList<Field>();
	
			for (String className : getReferencedClasses()) { // Bug fix [3028102] - ConcurrentModificationException
				if (isSystemClass(className)) {
					continue;
				}
				Class<?> type = null;
				try {
					type = loadRobotClassLocaly(className, false);
				} catch (Throwable t) {
					continue;
				}
				if (type != null) {
					for (Field field : getAllFields(new ArrayList<Field>(), type)) {				
						if (isStaticReference(field) && IBasicRobot.class.isAssignableFrom(field.getType())
								&& field.getAnnotation(robocode.annotation.SafeStatic.class) == null) {
							staticRobotReferences.add(field);
						}
					}
				}
			}
			if (staticRobotReferences.size() > 0) {
				StringBuilder buf = new StringBuilder();
	
				buf.append("Warning: ").append(fullClassName).append(
						" uses static reference to a robot with the following field(s):");

				for (Field field : staticRobotReferences) {
					buf.append("\n\t").append(field.getDeclaringClass().getName()).append('.').append(field.getName()).append(", which points to a ").append(
							field.getType().getName());
				}

				staticRobotInstanceWarning = new String[] {
					buf.toString(),
					"Static references to robots can cause unwanted behaviour with the robot using these.",
					"Please change static robot references to non-static references and recompile the robot."};
			} else {
				staticRobotInstanceWarning = new String[] {}; // Signal that there is no warnings to cache
			}
		} else if (staticRobotInstanceWarning.length == 0) {
			return; // Return, as no warnings should be written out in the robot console
		}

		// Write out warnings to the robot console
		if (robotProxy != null) {
			for (String line : staticRobotInstanceWarning) {
				robotProxy.getOut().println("SYSTEM: " + line);
			}
		}
	}

	/**
	 * Cleans a static field reference class, even when it is 'private static final'
	 *
	 * @param field the field to clean, if it is a static reference.
	 */
	private void cleanStaticReference(Field field) {
		if (field.getName().startsWith("const__")) {
			// Ignore 'const__' fields used with e.g. the Clojure language
			return;
		}
		field.setAccessible(true);
		try {
			// In order to set a 'private static field', we need to fix the modifier, i.e. use magic! ;-)
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			int modifiers = modifiersField.getInt(field);
			modifiersField.setInt(field, modifiers & ~Modifier.FINAL); // Remove the FINAL modifier
			field.set(null, null);

		} catch (Throwable ignore) {}
	}

	/**
	 * Gets all fields of a class (public, protected, private) and the ones inherited from all super classes.
	 * @param fields the list where the fields will be added as a result of calling this method.
	 * @param type the class to retrieve all the fields from
	 * @return the list specified as input parameter containing all the retrieved fields
	 */
	private List<Field> getAllFields(List<Field> fields, Class<?> type) {
		if (type == null || isSystemClass(type.getName())) {
			return fields;
		}
		try {
			for (Field field: type.getDeclaredFields()) {
				fields.add(field);
			}
		} catch (Throwable ignore) {// NoClassDefFoundError does occur with some robots, e.g. sgp.Drunken [1.12]
			// We ignore all exceptions and errors here so we can proceed to retrieve
			// field from super classes.
		}
		if (type.getSuperclass() != null) {
			fields = getAllFields(fields, type.getSuperclass());
		}
		return fields;
	}

	/**
	 * Checks if a specified class name is a Java system class or internal Robocode class.
	 * @param className the class name to check.
	 * @return true if the class name is a system class; false otherwise.
	 */
	private boolean isSystemClass(String className) {
		boolean isSystemClass = foundSystemClasses.contains(className);
		if (!isSystemClass) {
			try {
				if (findSystemClass(className) != null) {
					foundSystemClasses.add(className);
					isSystemClass = true;
				}
			} catch (ClassNotFoundException ignore) {}
		}
		return isSystemClass;
	}

	/**
	 * Checks if a specified field is a static reference.
	 *
	 * @param field the field to check.
	 * @return true if the field is static reference; false otherwise. 
	 */
	private static boolean isStaticReference(Field field) {
		return Modifier.isStatic(field.getModifiers())
				&& !(field.getType().isPrimitive() || field.isEnumConstant() || field.isSynthetic());
	}

	// Work-round for bug 389: Third-party team JARs broken with Java 9
	// The URLClassLoader.findResource() in Java 9 returns null with some robot JARs
	@Override
	public URL findResource(String name) {
		URL url = super.findResource(name);
		if (url == null) {
			// Ignore internal Java and Robocode classes
			if (name.startsWith("jdk/") || name.startsWith("java/") || name.startsWith("sun/") || name.startsWith("net.sf.robocode/")) {
				return null;
			}
			URL[] urls = getURLs();
			if (urls != null) {
				for (int i = 0; i < urls.length; i++) {
					URL u = urls[i];
					if (u != null) {
						try {
							URL tmp = new URL(u.getProtocol(), u.getHost(), u.getPort(), u.getPath() + name);
							if (u.openConnection() != null) {
								return tmp;
							}
						} catch (MalformedURLException ignore) {
						} catch (IOException ignore) {
						}
					}
				}
			}
		}
		return url;
	}
}
