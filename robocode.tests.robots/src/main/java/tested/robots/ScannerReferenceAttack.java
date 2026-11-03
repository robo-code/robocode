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
 * Uses a method reference to the constructor of Scanner, which the robot security cannot replace, so it must be rejected
 * with a clear message.
 *
 * @author Flemming N. Larsen (original)
 */
public class ScannerReferenceAttack extends AdvancedRobot {

	private interface ScannerFactory {
		Scanner create(File file) throws IOException;
	}

	public void run() {
		ScannerFactory factory = Scanner::new;

		try {
			factory.create(new File("a")).close();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
