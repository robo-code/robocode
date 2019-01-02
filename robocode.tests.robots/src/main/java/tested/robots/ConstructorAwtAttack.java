/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Flemming N. Larsen (original)
 */
public class ConstructorAwtAttack extends robocode.AdvancedRobot {

	public ConstructorAwtAttack() {
		awtAttack();
	}
	
	private void awtAttack() {
		try {
			Runnable doHack = new Runnable() {
				public void run() {
					writeAttack();

					JFrame frame;

					frame = new JFrame();
					frame.setName("Hack");
					frame.setVisible(true);

				}
			};

			javax.swing.SwingUtilities.invokeLater(doHack);
		} catch (Throwable e) {
			// swallow security exception
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
			out.println("Hacked!!!");
		} catch (FileNotFoundException e) {
			e.printStackTrace(out);
		} catch (IOException e) {
			e.printStackTrace(out);
		} catch (Throwable e) {
			// swallow security exception
			e.printStackTrace(out);
		}
	}
}
