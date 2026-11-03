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
import java.util.Scanner;


/**
 * Creates a Scanner with a branch in its arguments, which the robot security cannot replace, so it must be rejected with
 * a clear message.
 *
 * @author Flemming N. Larsen (original)
 */
public class ScannerConditionalAttack extends AdvancedRobot {

	public void run() {
		try {
			new Scanner(getTime() > 0 ? new File("a") : new File("b")).close();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
