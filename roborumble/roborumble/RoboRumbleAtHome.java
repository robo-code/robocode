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
 *     - Properties are now read using PropertiesUtil.getProperties()
 *     - Renamed UpdateRatings() into updateRatings()
 *     - Bugfix: Roborumble "ITERATE" broken: When running RoboRumble with
 *       ITERATE=YES, DOWNLOAD=YES, and RUNONLY=SERVER, the ratings were only
 *       read once, not per iteration
 *******************************************************************************/
package roborumble;


import roborumble.battlesengine.BattlesRunner;
import roborumble.battlesengine.PrepareBattles;
import roborumble.netengine.BotsDownload;
import roborumble.netengine.ResultsUpload;
import roborumble.netengine.UpdateRatingFiles;
import static roborumble.util.PropertiesUtil.getProperties;

import java.util.Properties;


/**
 * Implements the client side of RoboRumble@Home.
 * Controlled by properties files.
 *
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RoboRumbleAtHome {

	public static void main(String args[]) {

		// Get the associated parameters file
		String parameters = "./roborumble/roborumble.txt";

		try {
			parameters = args[0];
		} catch (Exception e) {
			System.out.println("No argument found specifying properties file. \"roborumble.txt\" assumed.");
		}

		// Read parameters for running the app
		Properties param = getProperties(parameters);

		String downloads = param.getProperty("DOWNLOAD", "NOT");
		String executes = param.getProperty("EXECUTE", "NOT");
		String uploads = param.getProperty("UPLOAD", "NOT");
		String iterates = param.getProperty("ITERATE", "NOT");
		String runonly = param.getProperty("RUNONLY", "GENERAL");
		String melee = param.getProperty("MELEE", "NOT");

		int iterations = 0;
		long lastdownload = 0;
		boolean ratingsdownloaded = false;
		boolean participantsdownloaded;

		do {
			System.out.println("Iteration number " + iterations);

			// Download data from Internet if downloads is YES and it has not been download for two hours
			if (downloads.equals("YES")) {
				BotsDownload download = new BotsDownload(parameters);

				if (runonly.equals("SERVER")) {
					// Download rating files and update ratings downloaded
					System.out.println("Downloading rating files ...");
					ratingsdownloaded = download.downloadRatings();
				}
				if ((System.currentTimeMillis() - lastdownload) > 2 * 3600 * 1000) {
					System.out.println("Downloading participants list ...");
					participantsdownloaded = download.downloadParticipantsList();
					System.out.println("Downloading missing bots ...");
					download.downloadMissingBots();
					download.updateCodeSize();
					// Send the order to the server to remove old participants from the ratings file
					if (ratingsdownloaded && participantsdownloaded) {
						System.out.println("Removing old participants from server ...");
						// Send unwanted participants to the server
						download.notifyServerForOldParticipants();
					}

					download = null;
					lastdownload = System.currentTimeMillis();
				}
			}

			// Create battles file (and delete old ones), and execute battles
			if (executes.equals("YES")) {

				boolean ready;
				PrepareBattles battles = new PrepareBattles(parameters);

				if (melee.equals("YES")) {
					System.out.println("Preparing melee battles list ...");
					ready = battles.createMeleeBattlesList();
				} else {
					System.out.println(
							"Preparing battles list ... Using smart battles is "
									+ (ratingsdownloaded && runonly.equals("SERVER")));
					if (ratingsdownloaded && runonly.equals("SERVER")) {
						// Create the smart lists
						ready = battles.createSmartBattlesList();
					} else {
						// Create the normal lists
						ready = battles.createBattlesList();
					}
				}

				battles = null;

				// Disable the -DPRARALLEL and -DRANDOMSEED options
				System.setProperty("PARALLEL", "false"); // TODO: Remove when robot thread CPU time can be measured
				System.setProperty("RANDOMSEED", "none"); // In tournaments, robots should not be deterministic!

				// Execute battles
				if (ready) {
					BattlesRunner engine = new BattlesRunner(parameters);

					if (melee.equals("YES")) {
						System.out.println("Executing melee battles ...");
						engine.runMeleeBattles();
					} else {
						System.out.println("Executing battles ...");
						engine.runBattles();
					}
					engine = null;
				}
			}

			// Upload results
			if (uploads.equals("YES")) {
				System.out.println("Uploading results ...");
				ResultsUpload upload = new ResultsUpload(parameters);

				// Uploads the results to the server
				upload.uploadResults();
				upload = null;

				// Updates the number of battles from the info received from the server
				System.out.println("Updating number of battles fought ...");
				UpdateRatingFiles updater = new UpdateRatingFiles(parameters);

				ratingsdownloaded = updater.updateRatings();
				updater = null;
			}

			iterations++;
		} while (iterates.equals("YES"));

		// With Java 5 this causes a IllegalThreadStateException, but not in Java 6
		// System.exit(0);
	}
}
