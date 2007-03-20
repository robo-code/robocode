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
 *     - Ported to Java 5
 *     - Removed dead code and unused imports
 *******************************************************************************/
package roborumble.netengine;


import java.util.*;
import java.util.Vector;
import java.io.*;


/**
 * BotsDownload - a class by Albert Perez
 * Manages the download operations (participants and JAR files)
 * Controlled by properties files
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
		Properties parameters = null;

		try {
			parameters = new Properties();
			parameters.load(new FileInputStream(propertiesfile));
		} catch (Exception e) {
			System.out.println("Parameters File not found !!!");
		}
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

	public boolean UpdateRatings() {

		// read all the recors to be updated
		Vector<String> battles = new Vector<String>();

		try {
			FileReader fr = new FileReader(battlesnumfile);
			BufferedReader br = new BufferedReader(fr);
			String record = new String();

			while ((record = br.readLine()) != null) {
				battles.add(record);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Can't open # battles file ... Aborting # battles update");
			return false;
		}

		// read the ratings files
		Properties all = null;

		try {
			all = new Properties();
			all.load(new FileInputStream(generalratings));
		} catch (Exception e) {
			System.out.println("All Ratings File not found !!!");
			return false;
		}
		Properties mini = null;

		try {
			mini = new Properties();
			mini.load(new FileInputStream(miniratings));
		} catch (Exception e) {
			mini = null;
		}
		Properties micro = null;

		try {
			micro = new Properties();
			micro.load(new FileInputStream(microratings));
		} catch (Exception e) {
			micro = null;
		}
		Properties nano = null;

		try {
			nano = new Properties();
			nano.load(new FileInputStream(nanoratings));
		} catch (Exception e) {
			nano = null;
		}

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
		try {
			all.store(new FileOutputStream(generalratings), "General ratings updated with new battles number");
			if (mini != null) {
				mini.store(new FileOutputStream(miniratings), "Mini ratings updated with new battles number");
			}
			if (micro != null) {
				micro.store(new FileOutputStream(microratings), "Micro ratings updated with new battles number");
			}
			if (nano != null) {
				nano.store(new FileOutputStream(nanoratings), "Nano ratings updated with new battles number");
			}
		} catch (Exception e) {
			System.out.println("Encountered problems when saving updated number of battles");
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
