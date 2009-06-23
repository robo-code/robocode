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
package net.sf.robocode.cachecleaner;


import net.sf.robocode.core.Container;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryManager;

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
		File roborumbleTempFile = new File("roborumble/temp");

		deleteFile(roborumbleTempFile.getPath());
		deleteFile(FileUtil.getRobotCacheDir().getPath());
		deleteFile(FileUtil.getRobotDatabaseFile().getPath());

		FileUtil.createDir(roborumbleTempFile);

		final IRepositoryManager repositoryManager = Container.getComponent(IRepositoryManager.class);

		repositoryManager.reload(true);

		Logger.logMessage("Cleaning done.");
	}

	private static void deleteFile(String filename) {
		Logger.logMessage("Deleting " + filename + "...");
		try {
			recursivelyDelete(new File(filename));
		} catch (IOException ex) {
			Logger.logError(ex.getMessage());
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
				throw new IOException("Failed deleting file: " + file.getPath());
			}
		}
	}
}
