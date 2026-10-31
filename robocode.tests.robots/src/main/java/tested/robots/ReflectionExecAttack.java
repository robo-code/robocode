/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.File;
import java.lang.reflect.Method;


/**
 * Attempts to execute a host command by calling Runtime.exec() via reflection, which must be prevented by the robot
 * security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ReflectionExecAttack extends AdvancedRobot {

	public static final String EXECUTED_MESSAGE = "Host command was executed via reflection";

	public void run() {
		String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		try {
			Object runtime = Runtime.class.getMethod("getRuntime").invoke(null);
			Method exec = Runtime.class.getMethod("exec", String.class);
			exec.invoke(runtime, java + " -version");
			out.println(EXECUTED_MESSAGE);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace(out);
		}
	}
}
