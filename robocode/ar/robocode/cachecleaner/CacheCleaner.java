/*******************************************************************************
 * Copyright (c) 2007 Aaron Rotenberg and Robocode Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Aaron Rotenberg
 *     - Initial version
 *     Flemming N. Larsen
 *     - Moved functionality from main() into the public method clean() in order
 *       to let the Robocode game call the cleanup tool
 *******************************************************************************/
package ar.robocode.cachecleaner;


import java.io.*;

import robocode.control.*;


/**
 * Cache cleaner used for cleaning the /robot directory of Robocode, especially
 * for RoboRumble
 *
 * @author AaronR
 */
public final class CacheCleaner {
	private CacheCleaner() {}

	private static class NullListener implements RobocodeListener {
		public void battleAborted(BattleSpecification battleSpec) {}

		public void battleComplete(BattleSpecification battleSpec, RobotResults[] results) {}

		public void battleMessage(String message) {}
	}

	public static void main(String[] args) {
		clean();
	}

	public static void clean() {
		deleteFile("roborumble/temp");
		deleteFile("robots/.robotcache");
		deleteFile("robots/robot.database");

		System.out.print("Creating roborumble/temp... ");
		if (new File("roborumble/temp").mkdir()) {
			System.out.println("done.");
		} else {
			System.out.println("failed.");
		}

		System.out.print("Rebuilding robot database... ");
		RobocodeEngine engine = new RobocodeEngine(new NullListener());

		engine.getLocalRepository(); // Force rebuild.
		System.out.println("done.");
	}
	
	private static void deleteFile(String filename) {
		System.out.print("Deleting " + filename + "... ");
		try {
			recursivelyDelete(new File(filename));
			System.out.println("done.");
		} catch (IOException ex) {
			System.out.println("failed.");
		}
	}

	private static boolean recursivelyDelete(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}

		if (file.isDirectory()) {
			final File[] files = file.listFiles();

			for (File f : files) {
				recursivelyDelete(f);
			}
		}

		if (!file.delete()) {
			throw new IOException("Delete failed.");
		}

		return true;
	}
}
