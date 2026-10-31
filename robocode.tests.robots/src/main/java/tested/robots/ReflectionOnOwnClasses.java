/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Uses reflection on its own classes and on allowed Java classes, which robots are allowed to do.
 *
 * @author Flemming N. Larsen (original)
 */
public class ReflectionOnOwnClasses extends AdvancedRobot {

	public static final String OK_MESSAGE = "Reflection OK";

	static class Data {
		private int value = 42;

		Data() {
		}

		private int twice() {
			return value * 2;
		}
	}

	public void run() {
		try {
			Data data = new Data();

			// Private field of own class
			Field field = Data.class.getDeclaredField("value");
			field.setAccessible(true);
			check(field.getInt(data) == 42, "getInt");
			field.setInt(data, 21);
			check(field.get(data).equals(21), "setInt");

			// Private method of own class
			Method twice = Data.class.getDeclaredMethod("twice");
			twice.setAccessible(true);
			check(twice.invoke(data).equals(42), "invoke");

			// Constructors of own package-private class
			Constructor<Data> constructor = Data.class.getDeclaredConstructor();
			check(constructor.newInstance().value == 42, "Constructor.newInstance");
			check(Data.class.newInstance().value == 42, "Class.newInstance");

			// Public method of an allowed Java class
			Method brighter = Color.class.getMethod("brighter");
			check(brighter.invoke(Color.GRAY) instanceof Color, "Color.brighter");

			out.println(OK_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}

	private void check(boolean condition, String what) {
		if (!condition) {
			out.println("Failed: " + what);
			throw new IllegalStateException("Failed: " + what);
		}
	}
}
