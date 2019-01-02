/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Pavel Savara (original)
 */
public class FileAttack extends AdvancedRobot {

	@Override
	public void run() {
		// noinspection InfiniteLoopStatement
		for (;;) {
			turnLeft(100);
			ahead(10);
			turnLeft(100);
			back(10);
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		readAttack();
		writeAttack();
	}

	private void readAttack() {
		try {
			FileInputStream fs = new FileInputStream("C:\\MSDOS.SYS");

			System.out.print(fs.read());
			System.out.print(fs.read());
			System.out.print(fs.read());
			System.out.print(fs.read());
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace(out);
		} catch (IOException e) {
			e.printStackTrace(out);
		} catch (Throwable e) {
			// swalow security exception
			e.printStackTrace(out);
		}
	}

	private void writeAttack() {
		FileOutputStream fs;

		try {
			fs = new FileOutputStream("C:\\Robocode.attack");
			fs.write(0xBA);
			fs.write(0xDF);
			fs.write(0x00);
			fs.write(0xD0);
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace(out);
		} catch (IOException e) {
			e.printStackTrace(out);
		} catch (Throwable e) {
			// swalow security exception
			e.printStackTrace(out);
		}
	}

}
