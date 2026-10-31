/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * Used internally by Robocode for checking the use of reflection by robots.
 * <p>
 * When robot classes are loaded, calls to methods that use members via reflection, e.g. {@link Method#invoke},
 * {@link Field#get}, and {@link AccessibleObject#setAccessible}, are replaced with calls to the corresponding static
 * methods of this class, which take the member as the first parameter. These methods check that the robot is allowed
 * to use the member, i.e. that the member belongs to a class of the robot, or that robots are allowed to use the
 * member directly in their code. Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotReflection {

	private RobotReflection() {
	}

	// Methods and constructors

	public static Object invoke(Method method, Object obj, Object[] args)
			throws IllegalAccessException, InvocationTargetException {
		check(method);
		return method.invoke(obj, args);
	}

	public static Object newInstance(Constructor<?> constructor, Object[] args)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		check(constructor);
		return constructor.newInstance(args);
	}

	public static Object newInstance(Class<?> type) throws InstantiationException, IllegalAccessException {
		Constructor<?> constructor;
		try {
			constructor = type.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			throw new InstantiationException(type.getName());
		}
		check(constructor);
		try {
			return constructor.newInstance();
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			if (cause instanceof Error) {
				throw (Error) cause;
			}
			throw new InstantiationException(String.valueOf(cause));
		}
	}

	// Fields

	public static Object get(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.get(obj);
	}

	public static boolean getBoolean(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.getBoolean(obj);
	}

	public static byte getByte(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.getByte(obj);
	}

	public static char getChar(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.getChar(obj);
	}

	public static short getShort(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.getShort(obj);
	}

	public static int getInt(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.getInt(obj);
	}

	public static long getLong(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.getLong(obj);
	}

	public static float getFloat(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.getFloat(obj);
	}

	public static double getDouble(Field field, Object obj) throws IllegalAccessException {
		check(field);
		return field.getDouble(obj);
	}

	public static void set(Field field, Object obj, Object value) throws IllegalAccessException {
		check(field);
		field.set(obj, value);
	}

	public static void setBoolean(Field field, Object obj, boolean value) throws IllegalAccessException {
		check(field);
		field.setBoolean(obj, value);
	}

	public static void setByte(Field field, Object obj, byte value) throws IllegalAccessException {
		check(field);
		field.setByte(obj, value);
	}

	public static void setChar(Field field, Object obj, char value) throws IllegalAccessException {
		check(field);
		field.setChar(obj, value);
	}

	public static void setShort(Field field, Object obj, short value) throws IllegalAccessException {
		check(field);
		field.setShort(obj, value);
	}

	public static void setInt(Field field, Object obj, int value) throws IllegalAccessException {
		check(field);
		field.setInt(obj, value);
	}

	public static void setLong(Field field, Object obj, long value) throws IllegalAccessException {
		check(field);
		field.setLong(obj, value);
	}

	public static void setFloat(Field field, Object obj, float value) throws IllegalAccessException {
		check(field);
		field.setFloat(obj, value);
	}

	public static void setDouble(Field field, Object obj, double value) throws IllegalAccessException {
		check(field);
		field.setDouble(obj, value);
	}

	// Accessibility

	public static void setAccessible(AccessibleObject object, boolean flag) {
		if (flag) {
			if (!(object instanceof Member)) {
				throw new SecurityException("Robots are not allowed to make " + object + " accessible");
			}
			RobotFileAccess.getThreadManager().checkRobotReflection((Member) object, true);
			makeAccessible(object);
		} else {
			object.setAccessible(false);
		}
	}

	public static void setAccessible(Executable executable, boolean flag) {
		setAccessible((AccessibleObject) executable, flag);
	}

	public static void setAccessible(Method method, boolean flag) {
		setAccessible((AccessibleObject) method, flag);
	}

	public static void setAccessible(Constructor<?> constructor, boolean flag) {
		setAccessible((AccessibleObject) constructor, flag);
	}

	public static void setAccessible(Field field, boolean flag) {
		setAccessible((AccessibleObject) field, flag);
	}

	private static <T extends AccessibleObject & Member> void check(T member) {
		if (RobotFileAccess.getThreadManager().checkRobotReflection(member, false)) {
			// A member of the robot's own classes. This class is not in the robot's package, so the member is made
			// accessible to give the robot the same access to its own members, as when it uses them directly.
			makeAccessible(member);
		}
	}

	private static void makeAccessible(final AccessibleObject object) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				object.setAccessible(true);
				return null;
			}
		});
	}
}
