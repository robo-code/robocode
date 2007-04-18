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
 *     - Properties are now read using PropertiesUtil.getProperties()
 *     - Renamed UpdateRatings() into updateRatings()
 *******************************************************************************/
package roborumble;


import roborumble.battlesengine.*;
import roborumble.netengine.*;
import java.util.*;

import static roborumble.util.PropertiesUtil.getProperties;


/**
 * Implements the client side of RoboRumble@Home.
 * Controlled by properties files.
 *
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RoboRumbleAtHome {
	
	public static void main(String args[]) {

		// get the associated parameters file
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
		boolean participantsdownloaded = false;	
	
		do {			
			System.out.println("Iteration number " + iterations);

			// Download data from internet if downloads is YES and it has not beeen download for two hours
			if (downloads.equals("YES") && (System.currentTimeMillis() - lastdownload) > 2 * 3600 * 1000) {
				BotsDownload download = new BotsDownload(parameters);

				System.out.println("Downloading participants list ...");
				participantsdownloaded = download.downloadParticipantsList();
				System.out.println("Downloading missing bots ...");
				download.downloadMissingBots();
				download.updateCodeSize();
				if (runonly.equals("SERVER")) { 
					// download rating files and update ratings downloaded
					System.out.println("Downloading rating files ...");
					ratingsdownloaded = download.downloadRatings();
				}
				// send the order to the server to remove old participants from the ratings file
				if (ratingsdownloaded && participantsdownloaded) {
					System.out.println("Removing old participants from server ...");
					// send unwanted participants to the server
					download.notifyServerForOldParticipants();
				}

				download = null;
				lastdownload = System.currentTimeMillis();
			}

			// create battles file (and delete old ones), and execute battles
			if (executes.equals("YES")) {
				
				boolean ready = false;
				PrepareBattles battles = new PrepareBattles(parameters);
				
				if (melee.equals("YES")) {
					System.out.println("Preparing melee battles list ...");
					ready = battles.createMeleeBattlesList();
				} else {
					System.out.println(
							"Preparing battles list ... Using smart battles is "
									+ (ratingsdownloaded && runonly.equals("SERVER")));
					if (ratingsdownloaded && runonly.equals("SERVER")) {
						ready = battles.createSmartBattlesList();
					} // create the smart lists
					else {
						ready = battles.createBattlesList();
					} // create the normal lists 
				}

				battles = null;

				// execute battles
				if (ready) {
					if (melee.equals("YES")) {
						System.out.println("Executing melee battles ...");
						BattlesRunner engine = new BattlesRunner(parameters);

						engine.runMeleeBattles();
						engine = null;
					} else {
						System.out.println("Executing battles ...");
						BattlesRunner engine = new BattlesRunner(parameters);

						engine.runBattles();
						engine = null;
					}
				} 
			}

			// upload results
			if (uploads.equals("YES")) {
				System.out.println("Uloading results ...");
				ResultsUpload upload = new ResultsUpload(parameters);

				// uploads the results to the server
				upload.uploadResults();
				upload = null;
				// updates the number of battles from the info received from the server
				System.out.println("Updating number of battles fought ...");
				UpdateRatingFiles updater = new UpdateRatingFiles(parameters);

				ratingsdownloaded = updater.updateRatings();
				// if (!ok) ratingsdownloaded = false;
				updater = null;
			}

			iterations++;
		} while (iterates.equals("YES"));
	
		System.exit(0);
	}
}
