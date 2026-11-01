/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;

import java.io.File;
import java.io.IOException;


/**
 * Opens a data file and never closes it, and reports the path of the file.
 *
 * @author Flemming N. Larsen (original)
 */
public class LeavesFileOpen extends AdvancedRobot {
	// Static to keep the stream alive, so that it does not get closed by being garbage collected
	private static RobocodeFileOutputStream stream;

	@Override
	public void run() {
		try {
			File file = getDataFile("open.dat");

			stream = new RobocodeFileOutputStream(file);
			stream.write('x');
			out.println("Data file: " + file.getPath());
		} catch (IOException e) {
			out.println("Could not write the file: " + e);
		}
		while (true) {
			turnRadarRight(360);
		}
	}
}
