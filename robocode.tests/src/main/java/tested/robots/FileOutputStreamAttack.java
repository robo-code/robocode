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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Flemming N. Larsen (original)
 */
public class FileOutputStreamAttack extends AdvancedRobot {

	public void run() {
		File file = null;

		try {		
			file = getDataFile("test");
			FileOutputStream fis = new FileOutputStream(file);

			fis.write(1);
		} catch (IOException e) {
			e.printStackTrace(out);
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}
}
