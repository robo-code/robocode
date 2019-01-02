/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Flemming N. Larsen (original)
 */
public class FileOutputStreamAttack extends AdvancedRobot {

	public void run() {
		File file = null;
		FileOutputStream fis = null;
		try {		
			file = getDataFile("test");
			fis = new FileOutputStream(file);
			fis.write(1);
		} catch (IOException e) {
			e.printStackTrace(out);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignore) {}
			}
			if (file != null) {
				file.delete();
			}
		}
	}
}
