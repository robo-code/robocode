/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.FileNotFoundException;
import java.io.PrintStream;


/**
 * Attempts to write a file using a PrintStream constructor that opens the file itself.
 *
 * @author Flemming N. Larsen (original)
 */
public class PrintStreamFileAttack extends AdvancedRobot {

	public void run() {
		try {
			PrintStream ps = new PrintStream("robocode-print-stream.attack");
			ps.println("Hacked!!!");
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace(out);
		}
	}
}
