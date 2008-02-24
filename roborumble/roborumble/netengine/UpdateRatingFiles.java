/*******************************************************************************
 * Copyright (c) 2003, 2007 Albert P�rez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Albert P�rez
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5
 *     - Removed dead code and unused imports
 *     - Properties are now read using PropertiesUtil.getProperties()
 *     - Properties are now stored using PropertiesUtil.storeProperties()
 *     - Renamed UpdateRatings() into updateRatings()
 *     - Catch of entire Exception has been reduced to catch of IOException when
 *       only this exception is ever thrown
 *     - Added missing close() to streams
 *******************************************************************************/
package roborumble.netengine;


import java.util.*;
import java.io.*;

import static roborumble.util.PropertiesUtil.getProperties;
import static roborumble.util.PropertiesUtil.storeProperties;


/**
 * Class used for updating the local rating files.
 * Controlled by properties files.
 * 
 * @author Albert P�rez (original)
 * @author Flemming N. Larsen (contributor)
 */
public class UpdateRatingFiles {

	private String game;
	private String minibots;
	private String microbots;
	private String nanobots;
	private String battlesnumfile;
	private String generalratings;
	private String miniratings;
	private String microratings;
	private String nanoratings;

	public UpdateRatingFiles(String propertiesfile) {
		// Read parameters
		Properties parameters = getProperties(propertiesfile);

		game = propertiesfile;
		while (game.indexOf("/") != -1) {
			game = game.substring(game.indexOf("/") + 1);
		}
		game = game.substring(0, game.indexOf("."));
		minibots = parameters.getProperty("MINIBOTS", "");
		microbots = parameters.getProperty("MICROBOTS", "");
		nanobots = parameters.getProperty("NANOBOTS", "");

		battlesnumfile = parameters.getProperty("BATTLESNUMFILE", "");

		generalratings = parameters.getProperty("RATINGS.GENERAL", "");
		miniratings = parameters.getProperty("RATINGS.MINIBOTS", "");
		microratings = parameters.getProperty("RATINGS.MICROBOTS", "");
		nanoratings = parameters.getProperty("RATINGS.NANOBOTS", "");
	}

	public boolean updateRatings() {

		// read all the records to be updated
		Vector<String> battles = new Vector<String>();
		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(battlesnumfile);

			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				battles.add(record);
			}
		} catch (IOException e) {
			System.out.println("Can't open # battles file ... Aborting # battles update");
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}

		// read the ratings files
		Properties all = getProperties(generalratings);
		Properties mini = getProperties(miniratings);
		Properties micro = getProperties(microratings);
		Properties nano = getProperties(nanoratings);

		// update #battles
		for (int  i = 0; i < battles.size(); i++) {
			String[] battle = (battles.get(i)).split(",");

			battle[1] = battle[1].replaceAll(" ", "_");
			double num = Double.parseDouble(battle[2]);

			if (battle[0].equals(game)) {
				updateRecord(battle[1], num, all);
			} else if (battle[0].equals(minibots) && mini != null) {
				updateRecord(battle[1], num, mini);
			} else if (battle[0].equals(microbots) && micro != null) {
				updateRecord(battle[1], num, micro);
			} else if (battle[0].equals(nanobots) && nano != null) {
				updateRecord(battle[1], num, nano);
			}
		}

		// save ratings files
		if (!storeProperties(all, generalratings, "General ratings updated with new battles number")) {
			return false;
		}
		if (!storeProperties(all, miniratings, "Mini ratings updated with new battles number")) {
			return false;
		}
		if (!storeProperties(all, microratings, "Micro ratings updated with new battles number")) {
			return false;
		}
		if (!storeProperties(all, nanoratings, "Nano ratings updated with new battles number")) {
			return false;
		}

		return true;
	}

	private void updateRecord(String bot, double battles, Properties ratings) {
		String values = ratings.getProperty(bot);

		if (values == null) {
			return;
		}

		String[] value = values.split(",");

		values = value[0] + "," + Double.toString(battles) + "," + value[2];
		ratings.setProperty(bot, values);
	}
}
