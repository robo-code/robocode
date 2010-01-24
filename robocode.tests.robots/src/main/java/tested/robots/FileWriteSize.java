/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
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
			file.delete();

			out.println("Data file: " + file.getCanonicalPath());

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
					e.printStackTrace();
				}
			}
			if (file != null) {
				file.delete();
			}
		}
	}
}
