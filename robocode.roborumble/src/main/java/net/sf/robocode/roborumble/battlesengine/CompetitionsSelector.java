/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.battlesengine;


import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;
import static net.sf.robocode.roborumble.util.PropertiesUtil.storeProperties;

import java.io.File;
import java.util.Properties;

import net.sf.robocode.repository.CodeSizeCalculator;


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

	public Boolean checkCompetitorForSize(String botName, long maxSize) {
		String name = botName.replace(' ', '_');

		// Read sizes
		long codeSize = Long.parseLong(sizes.getProperty(name, "0"));

		// Find out the size if not in the file
		boolean fileNeedsUpdate = false;

		if (codeSize == 0) {
			File f = new File(repository + name + ".jar");
			if (f.exists()) {
				fileNeedsUpdate = true; // Bug-362

				Integer jarFileCodeSize = CodeSizeCalculator.getJarFileCodeSize(f);
				if (jarFileCodeSize != null) {
					codeSize = jarFileCodeSize;
					sizes.setProperty(name, Long.toString(codeSize));
				}
			}
		}

		if (codeSize > 0) {
			// If the file needs update, then save the file
			if (fileNeedsUpdate) {
				storeProperties(sizes, sizesfile, "Bots code size");
			}

			// Check the code size
			return (codeSize < maxSize); // Bug-362
		} else {
			return null;
		}
	}

	public boolean checkCompetitorsForSize(String bot1, String bot2, long maxsize) {
		return checkCompetitorForSize(bot1, maxsize) && checkCompetitorForSize(bot2, maxsize);
	}
}
