/*******************************************************************************
 * Copyright (c) 2003, 2007 Albert Pérez and RoboRumble contributors
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
 *******************************************************************************/
package roborumble.battlesengine;


import codesize.*;
import java.util.*;
import java.io.*;
import codesize.Codesize.*;


/**
 * BattlesRunner - a class by Albert Perez
 * Reads a file with the battles to be runned and outputs the results in another file.
 * Controlled by properties files
 */
public class CompetitionsSelector {
	private String repository;
	private String sizesfile;
	private Properties sizes;
	
	public CompetitionsSelector(String sizesfile, String repository) { 
		this.repository = repository;
		// open sizes file
		this.sizesfile = sizesfile;
		try {
			sizes = new Properties();
			sizes.load(new FileInputStream(sizesfile));
		} catch (Exception e) {
			System.out.println("Sizes File not found !!!");
		}
	}

	public boolean CheckCompetitorsForSize(String bot1, String bot2, long maxsize) {
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

			try {
				Item s1 = Codesize.processZipFile(f);

				size1 = s1.getCodeSize();
			} catch (Exception e) {}
			if (size1 != 0) {
				sizes.setProperty(bot1name, Long.toString(size1));
			}
		}
		if (size2 == 0) {
			fileneedsupdate = true;
			File f = new File(repository + bot2name + ".jar");

			try {
				Item s2 = Codesize.processZipFile(f);

				size2 = s2.getCodeSize();
			} catch (Exception e) {}
			if (size2 != 0) {
				sizes.setProperty(bot2name, Long.toString(size2));
			}
		}
		
		// if the file needs update, then save the file
		try {
			if (fileneedsupdate && size1 != 0 && size2 != 0) {
				sizes.store(new FileOutputStream(sizesfile), "Bots code size");
			}
		} catch (Exception e) {}

		// check the values
		return (size1 != 0 && size1 < maxsize && size2 != 0 && size2 < maxsize);
	}
}

