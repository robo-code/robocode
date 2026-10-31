/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;


/**
 * Attempts to create a class loader, which could be used for defining classes that are not verified by Robocode.
 *
 * @author Flemming N. Larsen (original)
 */
public class ClassLoaderAttack extends AdvancedRobot {

	static class EvilClassLoader extends ClassLoader {
		Class<?> define(byte[] classFile) {
			return defineClass(null, classFile, 0, classFile.length);
		}
	}

	public void run() {
		new EvilClassLoader().define(new byte[0]);
	}
}
