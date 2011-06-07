/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Maintainance
 *     Andrew Magargle
 *     - Bugfix so that PrintStream is always closed, even when errors occurs
 *******************************************************************************/
package sample;


import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;


/**
 * SittingDuck - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p/>
 * Along with sitting still doing nothing,
 * this robot demonstrates persistency.
 */
public class SittingDuck extends AdvancedRobot {
	static boolean incrementedBattles = false;

	public void run() {
		setBodyColor(Color.yellow);
		setGunColor(Color.yellow);

		int roundCount, battleCount;

		// Read file "count.dat" which contains 2 lines,
		// a round count, and a battle count
		try {
			BufferedReader r = new BufferedReader(new FileReader(getDataFile("count.dat")));

			// Try to get the counts
			roundCount = Integer.parseInt(r.readLine());
			battleCount = Integer.parseInt(r.readLine());
		} catch (IOException e) {
			// Something went wrong reading the file, reset to 0.
			roundCount = 0;
			battleCount = 0;
		} catch (NumberFormatException e) {
			// Something went wrong converting to ints, reset to 0
			roundCount = 0;
			battleCount = 0;
		}

		// Increment the # of rounds
		roundCount++;
		// If we haven't incremented # of battles already,
		// (Note:  Because robots are only instantiated once per battle,
		// member variables remain valid throughout it.
		if (!incrementedBattles) {
			// Increment # of battles
			battleCount++;
			incrementedBattles = true;
		}

		PrintStream w = null;

		try {
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile("count.dat")));

			w.println(roundCount);
			w.println(battleCount);
			// PrintStreams don't throw IOExceptions during prints,
			// they simply set a flag.... so check it here.
			if (w.checkError()) {
				out.println("I could not write the count!");
			}
		} catch (IOException e) {
			out.println("IOException trying to write: ");
			e.printStackTrace(out);
		} finally {
			if (w != null) {
				w.close();
			}
		}

		out.println("I have been a sitting duck for " + roundCount + " rounds, in " + battleCount + " battles."); 
	}
}
