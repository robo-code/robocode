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
import java.io.IOException;


/**
 * Attempts to execute a host command, which must be prevented by the robot security.
 * The command is the harmless "java -version" of the JVM running Robocode.
 *
 * @author Flemming N. Larsen (original)
 */
public class ProcessExecAttack extends AdvancedRobot {

	public static final String EXECUTED_MESSAGE = "Host command was executed";

	public void run() {
		String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		try {
			Process process = new ProcessBuilder(java, "-version").start();
			process.waitFor();
			out.println(EXECUTED_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace(out);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
