/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Used internally by Robocode for restricting the classes that robots can deserialize.
 * <p>
 * When robot classes are loaded, {@code new ObjectInputStream(..)} is replaced with
 * {@code new RobotObjectInputStream(..)}, which only allows deserializing the robot's own classes, Robocode classes,
 * and common Java classes like strings, boxed primitives, collections, and geometry. Other classes could be misused
 * for attacking the host via deserialization. Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobotObjectInputStream extends ObjectInputStream {

	// Java packages (exact names, not including sub packages) with classes that robots are allowed to deserialize
	private static final Set<String> ALLOWED_PACKAGES = new HashSet<String>(Arrays.asList(
			"java.lang",
			"java.util",
			"java.util.concurrent",
			"java.util.concurrent.atomic",
			"java.math",
			"java.awt.geom",
			"java.time"
	));

	// Java classes in packages that are otherwise not allowed
	private static final Set<String> ALLOWED_CLASSES = new HashSet<String>(Arrays.asList(
			"java.awt.Color",
			"java.awt.Point",
			"java.awt.Rectangle",
			"java.awt.Dimension"
	));

	// The class loader of the robot that created this stream, which is used for loading the classes to deserialize
	private final ClassLoader robotClassLoader;

	public RobotObjectInputStream(InputStream in) throws IOException {
		super(in);
		robotClassLoader = RobotFileAccess.getThreadManager().getRobotClassLoader();
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		String name = desc.getName();
		Class<?> type;

		try {
			type = Class.forName(name, false, robotClassLoader);
		} catch (ClassNotFoundException e) {
			type = super.resolveClass(desc); // e.g. primitive types like int.class
		}
		if (!isAllowed(type)) {
			throw new InvalidClassException(name, "Robots are not allowed to deserialize this class");
		}
		return type;
	}

	@Override
	protected Class<?> resolveProxyClass(String[] interfaces) throws InvalidClassException {
		throw new InvalidClassException("Robots are not allowed to deserialize proxy classes");
	}

	private boolean isAllowed(Class<?> type) {
		while (type.isArray()) {
			type = type.getComponentType();
		}
		if (type.isPrimitive()) {
			return true;
		}
		if (type.getClassLoader() == robotClassLoader) {
			return true; // A class of the robot
		}

		String name = type.getName();
		if (name.startsWith("robocode.") && !name.startsWith("robocode.control.")) {
			return true;
		}
		if (ALLOWED_CLASSES.contains(name)) {
			return true;
		}
		int lastDot = name.lastIndexOf('.');
		return lastDot > 0 && ALLOWED_PACKAGES.contains(name.substring(0, lastDot));
	}
}
