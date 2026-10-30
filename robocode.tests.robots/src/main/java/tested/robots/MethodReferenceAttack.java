/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
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
 * Attempts to execute a host command via a method reference to Runtime.exec(), which is compiled into a method
 * handle constant instead of a method invocation.
 *
 * @author Flemming N. Larsen (original)
 */
public class MethodReferenceAttack extends AdvancedRobot {

	interface Exec {
		Object exec(String command) throws IOException;
	}

	public void run() {
		Exec exec = Runtime.getRuntime()::exec;
		try {
			exec.exec(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java -version");
			out.println("Host command was executed");
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
