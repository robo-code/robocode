/*******************************************************************************
 * Copyright (c) 2007, 2008 Aaron Rotenberg and Robocode Contributors
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


import net.sf.robocode.IRobocodeManager;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.security.HiddenAccess;

import java.io.File;
import java.io.IOException;


/**
 * Cache cleaner used for cleaning the /robot directory of Robocode, especially
 * for RoboRumble
 *
 * @author AaronR
 * @author Flemming N. Larsen (minor optimizations)
 */
public final class CacheCleaner {
	private CacheCleaner() {}

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

		IRobocodeManager manager = (IRobocodeManager) HiddenAccess.createRobocodeManager();
		final IRepositoryManager repositoryManager = manager.getRepositoryManager();

		repositoryManager.clearRobotList();
		repositoryManager.loadRobotRepository();

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

	private static void recursivelyDelete(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				final File[] files = file.listFiles();

				for (File f : files) {
					recursivelyDelete(f);
				}
			}
			if (!file.delete()) {
				throw new IOException("Delete failed.");
			}
		}
	}
}
