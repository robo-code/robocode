/*******************************************************************************
 * Copyright (c) 2003-2013 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
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

	public boolean checkCompetitorForSize(String botName, long maxsize) {
		String name = botName.replace(' ', '_');

		// Read sizes
		long codeSize = Long.parseLong(sizes.getProperty(name, "0"));

		// find out the size if not in the file
		boolean fileNeedsUpdate = false;

		if (codeSize == 0) {
			fileNeedsUpdate = true;

			File f = new File(repository + name + ".jar");
			if (f.exists()) {
				Item item = Codesize.processZipFile(f);
				if (item != null) {
					codeSize = item.getCodeSize();
				}
				if (codeSize != 0) {
					sizes.setProperty(name, Long.toString(codeSize));
				}
			}
		}

		// if the file needs update, then save the file
		if (fileNeedsUpdate && codeSize != 0) {
			storeProperties(sizes, sizesfile, "Bots code size");
		}

		// check the code size
		return (codeSize != 0 && codeSize < maxsize);
	}

	public boolean checkCompetitorsForSize(String bot1, String bot2, long maxsize) {
		return checkCompetitorForSize(bot1, maxsize) && checkCompetitorForSize(bot2, maxsize);
	}
}
