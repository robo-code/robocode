/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
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
 * @author Flemming N. Larsen (original)
 */
public class FileWriteSize extends AdvancedRobot {

	public void run() {
		out.println("Data directory: " + getDataDirectory());
		out.println("Data quota: " + getDataQuotaAvailable());

		byte[] buf = new byte[100000];

		File file = null;
		RobocodeFileOutputStream rfos = null;

		try {
			file = getDataFile("test");
			out.println("Data file: " + file.getCanonicalPath());
			file.delete();

			rfos = new RobocodeFileOutputStream(file);

			for (int i = 0; i < 3; i++) {
				rfos.write(buf);
			}
		} catch (IOException e) {
			e.printStackTrace(out);
		} finally {
			if (rfos != null) {
				try {
					rfos.close();
				} catch (IOException e) {
					e.printStackTrace(out);
				}
			}
			if (file != null) {
				if (!file.delete()) {
					out.println("Could not delete the file: " + file);
				}
			}
		}
	}
}
