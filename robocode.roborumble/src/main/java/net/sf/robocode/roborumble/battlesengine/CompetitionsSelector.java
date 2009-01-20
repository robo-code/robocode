/*******************************************************************************
 * Copyright (c) 2003, 2008 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Albert Pérez
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Removed unused imports
 *     - Properties are now read using PropertiesUtil.getProperties()
 *     - Properties are now stored using PropertiesUtil.storeProperties()
 *     - Renamed CheckCompetitorsForSize() into checkCompetitorsForSize()
 *******************************************************************************/
package net.sf.robocode.roborumble.battlesengine;


import codesize.Codesize;
import codesize.Codesize.Item;
import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;
import static net.sf.robocode.roborumble.util.PropertiesUtil.storeProperties;

import java.io.File;
import java.util.Properties;


/**
 * This class is used to control which competitions a robot is allowed to
 * participate in.
 * Reads a file with the battles to be runned and outputs the results in
 * another file.
 * Controlled by properties files.
 *
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 */
public class CompetitionsSelector {
	private final String repository;
	private final String sizesfile;
	private final Properties sizes;

	public CompetitionsSelector(String sizesfile, String repository) {
		this.repository = repository;
		// open sizes file
		this.sizesfile = sizesfile;

		sizes = getProperties(sizesfile);
	}

	public boolean checkCompetitorsForSize(String bot1, String bot2, long maxsize) {
		String bot1name = bot1.replace(' ', '_');
		String bot2name = bot2.replace(' ', '_');

		// Read sizes
		long size1 = Long.parseLong(sizes.getProperty(bot1name, "0"));
		long size2 = Long.parseLong(sizes.getProperty(bot2name, "0"));

		// find out the size if not in the file
		boolean fileneedsupdate = false;

		if (size1 == 0) {
			fileneedsupdate = true;
			File f = new File(repository + bot1name + ".jar");

			Item s1 = Codesize.processZipFile(f);

			if (s1 != null) {
				size1 = s1.getCodeSize();
			}
			if (size1 != 0) {
				sizes.setProperty(bot1name, Long.toString(size1));
			}
		}
		if (size2 == 0) {
			fileneedsupdate = true;
			File f = new File(repository + bot2name + ".jar");

			Item s2 = Codesize.processZipFile(f);

			if (s2 != null) {
				size2 = s2.getCodeSize();
			}
			if (size2 != 0) {
				sizes.setProperty(bot2name, Long.toString(size2));
			}
		}

		// if the file needs update, then save the file
		if (fileneedsupdate && size1 != 0 && size2 != 0) {
			storeProperties(sizes, sizesfile, "Bots code size");
		}

		// check the values
		return (size1 != 0 && size1 < maxsize && size2 != 0 && size2 < maxsize);
	}
}

