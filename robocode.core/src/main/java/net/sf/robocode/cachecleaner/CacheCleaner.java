/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
		deleteFile(FileUtil.getRobotsDataDir().getPath());
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

	private static void recursivelyDelete(File file, File base) throws IOException {
	    if (!file.exists()) {
	        return;
	    }
	    
	    // Security check to prevent directory traversal attacks
	    if (!(file.getCanonicalFile().toPath().startsWith(base.getCanonicalFile().toPath()))) {
	        throw new IOException("Security violation: Attempting to delete a file outside the allowed base directory: "
	                + file.getCanonicalPath());
	    }
	
	    if (file.isDirectory()) {
	        final File[] files = file.listFiles();
	        
	        // Null check for file listing
	        if (files != null) {
	            for (File f : files) {
	                recursivelyDelete(f, base);
	            }
	        }
	    }
	    
	    if (!file.delete()) {
	        throw new IOException("Failed deleting file: " + file.getPath());
	    }
	}
	}
