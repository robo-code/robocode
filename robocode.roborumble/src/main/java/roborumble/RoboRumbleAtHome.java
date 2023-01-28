/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package roborumble;


import net.sf.robocode.roborumble.battlesengine.BattlesRunner;
import net.sf.robocode.roborumble.battlesengine.PrepareBattles;
import net.sf.robocode.roborumble.netengine.BotsDownload;
import net.sf.robocode.roborumble.netengine.ResultsUpload;
import net.sf.robocode.roborumble.netengine.UpdateRatingFiles;

import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;

import java.util.Map;
import java.util.Properties;


/**
 * Implements the client side of RoboRumble@Home.
 * Controlled by properties files.
 *
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 * @author Jerome Lavigne (contributor)
 * @author Pavel Savara (contributor)
 */
public class RoboRumbleAtHome {

    public static void main(String[] args) {

        // Get the associated parameters file
        String paramsFileName;
        String envParams = System.getenv("PARAMS");
        if (args.length >= 1) {
            paramsFileName = args[0];
        } else if (envParams != null) {
            paramsFileName = envParams;
        } else {
            paramsFileName = "./roborumble/roborumble.txt";
            System.out.println("No argument found specifying properties file. \"" + paramsFileName + "\" assumed.");
        }

        // Read parameters for running the app
        Properties properties = getProperties(paramsFileName);

        String envUser = System.getenv("RUMBLE_USER");
        if (envUser != null) {
            properties.setProperty("USER", envUser);
        }
        String envParticipantsUrl = System.getenv("RUMBLE_PARTICIPANTSURL");
        if (envParticipantsUrl != null && !envParticipantsUrl.equals("null")) {
            properties.setProperty("PARTICIPANTSURL", envParticipantsUrl);
        }
        String envUpdateBotsUrl = System.getenv("RUMBLE_UPDATEBOTSURL");
        if (envUpdateBotsUrl != null && !envUpdateBotsUrl.equals("null")) {
            properties.setProperty("UPDATEBOTSURL", envUpdateBotsUrl);
        }
        String envResultsUrl = System.getenv("RUMBLE_RESULTSURL");
        if (envResultsUrl != null && !envResultsUrl.equals("null")) {
            properties.setProperty("RESULTSURL", envResultsUrl);
        }
        String envRatingsUrl = System.getenv("RUMBLE_RATINGSURL");
        if (envRatingsUrl != null && !envRatingsUrl.equals("null")) {
            properties.setProperty("RATINGS.URL", envRatingsUrl);
        }

        String downloads = properties.getProperty("DOWNLOAD", "NOT");
        String executes = properties.getProperty("EXECUTE", "NOT");
        String uploads = properties.getProperty("UPLOAD", "NOT");
        String iterates = properties.getProperty("ITERATE", "NOT");
        String runonly = properties.getProperty("RUNONLY", "GENERAL");
        String melee = properties.getProperty("MELEE", "NOT");

        int iterations = 0;
        long lastdownload = 0;
        boolean ratingsdownloaded = false;
        boolean participantsdownloaded;
        String version = null;
        String game = paramsFileName;
        while (game.indexOf("/") != -1) {
            game = game.substring(game.indexOf("/") + 1);
        }
        game = game.substring(0, game.indexOf("."));

        do {
            final BattlesRunner engine = new BattlesRunner(game, properties);

            if (version == null) {
                version = engine.getVersion();
            }

            System.out.println("Iteration number " + iterations);

            // Download data from Internet if downloads is YES and it has not been download for 10 minutes
            if (downloads.equals("YES")) {
                BotsDownload download = new BotsDownload(game, properties);

                if (runonly.equals("SERVER")) {
                    // Download rating files and update ratings downloaded
                    ratingsdownloaded = download.downloadRatings();
                }
                if ((System.currentTimeMillis() - lastdownload) > 10 * 60 * 1000) {
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

                    lastdownload = System.currentTimeMillis();
                }
            }

            // Create battles file (and delete old ones), and execute battles
            if (executes.equals("YES")) {
                final boolean isMelee = melee.equals("YES");

                boolean ready;
                PrepareBattles battles = new PrepareBattles(paramsFileName);

                if (isMelee) {
                    System.out.println("Preparing melee battles list ...");
                    ready = battles.createMeleeBattlesList();
                } else {
                    final boolean isSmartBattles = ratingsdownloaded && runonly.equals("SERVER");

                    if (isSmartBattles) {
                        System.out.println("Preparing battles list using smart battles...");
                        ready = battles.createSmartBattlesList();
                    } else {
                        System.out.println("Preparing battles list...");
                        ready = battles.createBattlesList();
                    }
                }

                // Disable the -DPRARALLEL and -DRANDOMSEED options
                System.setProperty("PARALLEL", "false"); // TODO: Remove when robot thread CPU time can be measured
                System.setProperty("RANDOMSEED", "none"); // In tournaments, robots should not be deterministic!

                // Execute battles
                if (ready) {
                    if (isMelee) {
                        System.out.println("Executing melee battles ...");
                    } else {
                        System.out.println("Executing battles ...");
                    }

                    engine.runBattlesImpl(isMelee);
                }
            }

            // Upload results
            if (uploads.equals("YES") && version != null) {
                System.out.println("Uploading results ...");
                ResultsUpload upload = new ResultsUpload(game, properties, version);

                // Uploads the results to the server
                upload.uploadResults();

                // Updates the number of battles from the info received from the server
                System.out.println("Updating number of battles fought ...");
                UpdateRatingFiles updater = new UpdateRatingFiles(game, properties);

                ratingsdownloaded = updater.updateRatings();
            }

            iterations++;
        } while (iterates.equals("YES"));

        // With Java 5 this causes a IllegalThreadStateException, but not in Java 6
        // System.exit(0);
    }
}
