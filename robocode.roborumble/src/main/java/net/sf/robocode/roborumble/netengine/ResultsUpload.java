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
 *     - Ported to Java 5
 *     - Minor cleanup and optimizations
 *     - Removed unused imports
 *     - Replaced the robocode.util.Utils.copy() method with FileTransfer.copy()
 *     - Properties are now read using PropertiesUtil.getProperties()
 *     - Catch of entire Exception has been reduced to catch of IOException when
 *       only this exception is ever thrown
 *     - Added missing close() to streams
 *     Mauro Mombelli
 *     - Added multiple server for upload
 *     - Now implements runnable (many changes on file output and delete)
 *     - The senddata() method now don't produce console output
 *     - Added writeToFile() method
 *******************************************************************************/
package net.sf.robocode.roborumble.netengine;


import net.sf.robocode.roborumble.battlesengine.CompetitionsSelector;
import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.Vector;


/**
 * Class used for uploading results to a server.
 * Controlled by properties files.
 *
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 * @author Mauro Mombelli (contributor)
 */
public class ResultsUpload extends Thread {

	private final String client;
	private final String resultsfile;
	private final String[] resultsurls;
	private String tempdir;
	private String game;
	private final String user;
	private final String sizesfile;
	private final String minibots;
	private final String microbots;
	private final String nanobots;
	private final CompetitionsSelector size;
	private final String battlesnumfile;
	private final String priority;
	private final String teams;
	private final String melee;
	private String neterrfilepath;
	private String params;

	public ResultsUpload(String propertiesfile, String clientVersion, String params) {
		// Read parameters
		Properties parameters = getProperties(propertiesfile);
		this.params=params;

		resultsfile = parameters.getProperty("OUTPUT", "");
		resultsurls = parameters.getProperty("RESULTSURL", "").split("[\\s,;]+");
		tempdir = parameters.getProperty("TEMP", "");
		user = parameters.getProperty("USER", "");
		game = propertiesfile;
		String botsrepository = parameters.getProperty("BOTSREP", "");

		while (game.indexOf("/") != -1) {
			game = game.substring(game.indexOf("/") + 1);
		}
		game = game.substring(0, game.indexOf("."));
		sizesfile = parameters.getProperty("CODESIZEFILE", "");
		minibots = parameters.getProperty("MINIBOTS", "");
		microbots = parameters.getProperty("MICROBOTS", "");
		nanobots = parameters.getProperty("NANOBOTS", "");

		battlesnumfile = parameters.getProperty("BATTLESNUMFILE", "");
		priority = parameters.getProperty("PRIORITYBATTLESFILE", "");
		client = clientVersion;
		teams = parameters.getProperty("TEAMS", "");
		melee = parameters.getProperty("MELEE", "");

		// Open competitions selector
		size = new CompetitionsSelector(sizesfile, botsrepository);
	}

	public void run() {
		System.out.println("Uploading results ...");
		if (uploadResults()) {
			// Updates the number of battles from the info received from the server
			System.out.println("Updating number of battles fought ...");
			UpdateRatingFiles updater = new UpdateRatingFiles(params);
			updater.updateRatings();
		}
	} 

	public boolean uploadResults() {
		boolean errorsfound = false;

		// Read the results file

		Vector<String> results = new Vector<String>();
		String match = "";
		String bot1 = "";
		String bot2;
		int status = 0;
		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(resultsfile);

			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				if (record.indexOf(game) != -1) {
					match = record;
					status = 0;
				} else if (status == 0) {
					bot1 = record;
					status = 1;
				} else if (status == 1) {
					bot2 = record;
					results.add(match);
					results.add(bot1);
					results.add(bot2);
				}
			}
		} catch (IOException e) {
			System.out.println("Can't open result file for upload");
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {}
			}
		}
		
		// Delete results file
		if (new File(resultsfile).delete() == false) {
			System.out.println("Unable to delete results file.");
		}

		PrintStream outtxt;

		try {
			// Open the temp file to put the unuploaded results
			File filetempdir = new File(tempdir);
			File filetempname = File.createTempFile("results", ".txt", filetempdir);

			tempdir = filetempname.toString();
			System.out.println("temp file: " + tempdir);
			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(filetempname)), false);

			// Open the file where to put the senddata() error/message
			neterrfilepath = filetempdir.toString() + "neterr.txt";
			System.out.println("error file: " + neterrfilepath);

		} catch (IOException e) {
			System.out.println("Not able to open output file ... Aborting");
			System.out.println(e);
			return false;
		}

		// Open the file to put the battles number for each participant
		int e = tryOutputFile(battlesnumfile);

		if (e != 0) {
			switch (e) {
			case 1:
				System.out.println("ERROR: BATTLESNUMFILE seems a directory ...");
				break;

			case 2:
				System.out.println("ERROR: BATTLESNUMFILE seems protected ...");
				break;

			default:
				System.out.println("Not able to open battlesnum file ... \nfile:" + priority + " error number: " + e);
				break;
			}
		}

		// Open the file to put the battles which have priority
		e = tryOutputFile(priority);
		if (e != 0) {
			switch (e) {
			case 1:
				System.out.println("ERROR: PRIORITYBATTLESFILE seems a directory ...");
				break;

			case 2:
				System.out.println("ERROR: PRIORITYBATTLESFILE seems protected ...");
				break;

			default:
				System.out.println("Not able to open priorities file ... \nfile:" + priority + " error number: " + e);
				break;
			}
		}

		// Post the results

		for (int i = 0; i < results.size() / 3; i++) {

			// Create the parameters String
			String[] header = results.get(i * 3).split(",");
			String[] first = results.get(i * 3 + 1).split(",");
			String[] second = results.get(i * 3 + 2).split(",");

			// find the match mode
			String matchtype = "GENERAL";

			if (header.length >= 6) {
				matchtype = header[5];
			}

			// if the match mode was general, then send the results to all competitions (asuming codesize is used).
			// if its not, then send results only to smaller size competitions
			String data = "version=1" + "&" + "client=" + client + "&" + "teams=" + teams + "&" + "melee=" + melee + "&"
					+ "game=" + game + "&" + "rounds=" + header[1] + "&" + "field=" + header[2] + "&" + "user=" + user + "&"
					+ "time=" + header[4] + "&" + "fname=" + first[0] + "&" + "fscore=" + first[1] + "&" + "fbulletd="
					+ first[2] + "&" + "fsurvival=" + first[3] + "&" + "sname=" + second[0] + "&" + "sscore=" + second[1]
					+ "&" + "sbulletd=" + second[2] + "&" + "ssurvival=" + second[3];

			if (matchtype.equals("GENERAL") || matchtype.equals("SERVER")) {
				errorsfound = errorsfound | senddata(game, data, outtxt, true, results, i, priority);
			}

			if (sizesfile.length() != 0) { // upload also related competitions
				if (minibots.length() != 0 && !matchtype.equals("NANO") && !matchtype.equals("MICRO")
						&& size.checkCompetitorsForSize(first[0], second[0], 1500)) {
					data = "version=1" + "&" + "client=" + client + "&" + "teams=" + teams + "&" + "melee=" + melee
							+ "&" + "game=" + minibots + "&" + "rounds=" + header[1] + "&" + "field=" + header[2] + "&"
							+ "user=" + user + "&" + "time=" + header[4] + "&" + "fname=" + first[0] + "&" + "fscore="
							+ first[1] + "&" + "fbulletd=" + first[2] + "&" + "fsurvival=" + first[3] + "&" + "sname="
							+ second[0] + "&" + "sscore=" + second[1] + "&" + "sbulletd=" + second[2] + "&" + "ssurvival="
							+ second[3];
					errorsfound = errorsfound | senddata(minibots, data, outtxt, false, results, i, null);
				}
				if (microbots.length() != 0 && !matchtype.equals("NANO")
						&& size.checkCompetitorsForSize(first[0], second[0], 750)) {
					data = "version=1" + "&" + "client=" + client + "&" + "teams=" + teams + "&" + "melee=" + melee
							+ "&" + "game=" + microbots + "&" + "rounds=" + header[1] + "&" + "field=" + header[2] + "&"
							+ "user=" + user + "&" + "time=" + header[4] + "&" + "fname=" + first[0] + "&" + "fscore="
							+ first[1] + "&" + "fbulletd=" + first[2] + "&" + "fsurvival=" + first[3] + "&" + "sname="
							+ second[0] + "&" + "sscore=" + second[1] + "&" + "sbulletd=" + second[2] + "&" + "ssurvival="
							+ second[3];
					errorsfound = errorsfound | senddata(microbots, data, outtxt, false, results, i, null);
				}
				if (nanobots.length() != 0 && size.checkCompetitorsForSize(first[0], second[0], 250)) {
					data = "version=1" + "&" + "client=" + client + "&" + "game=" + nanobots + "&" + "rounds="
							+ header[1] + "&" + "field=" + header[2] + "&" + "user=" + user + "&" + "time=" + header[4] + "&"
							+ "fname=" + first[0] + "&" + "fscore=" + first[1] + "&" + "fbulletd=" + first[2] + "&"
							+ "fsurvival=" + first[3] + "&" + "sname=" + second[0] + "&" + "sscore=" + second[1] + "&"
							+ "sbulletd=" + second[2] + "&" + "ssurvival=" + second[3];
					errorsfound = errorsfound | senddata(nanobots, data, outtxt, false, results, i, null);
				}
			}
		}

		// Close files
		outtxt.close();

		// Copy temp file into results file in append mode if there was some error
		if (errorsfound) {
			if (!FileTransfer.copy(tempdir, resultsfile, true)) {
				System.out.println("Error when copying results errors file.");
			}
		}

		// delete temp file
		new File(tempdir).delete();

		System.out.println("Results upload complete");
		return true;
	}

	private int tryOutputFile(String file) {
		File tmp = new File(file);

		if (tmp.isDirectory()) {
			return 1;
		}
		if (!tmp.canWrite()) {
			return 2;
		}

		return 0;
	}

	private boolean writeToFile(String file, String message, boolean append) {
		try {
			PrintStream outputStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file, append)));

			outputStream.println(message);
			outputStream.close();
		} catch (IOException e) {
			System.out.println("Not able to open file for output " + file + " append: " + append);
			System.out.println(e);
			return false;
		}
		return true;
	}

	private void saverror(PrintStream outtxt, String match, String bot1, String bot2, boolean saveonerror) {
		if (saveonerror) {
			outtxt.println(match);
			outtxt.println(bot1);
			outtxt.println(bot2);
		}
		writeToFile(neterrfilepath, "Unable to upload results " + match + " " + bot1 + " " + bot2, true);
	}

	private boolean senddata(String game, String data, PrintStream outtxt, boolean saveonerror, Vector<String> results, int i, String priority) {
		boolean errorfound = false;

		for (int count = 0; count < resultsurls.length; count++) {
			writeToFile(neterrfilepath, "Sending result to: " + resultsurls[count], true);
			if (senddata(resultsurls[count], game, data, outtxt, saveonerror, results, i, priority) == false) {
				errorfound = true;
			}
		}
		return errorfound;
	}

	private boolean senddata(String resultsurl, String game, String data, PrintStream outtxt, boolean saveonerror, Vector<String> results, int i, String priority) {
		boolean errorsfound = false;
		PrintWriter wr = null;
		BufferedReader rd = null;

		try {
			// Send data
			URL url = new URL(resultsurl);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);
			wr = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));

			wr.println(data);
			wr.flush();

			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			boolean ok = false;

			while ((line = rd.readLine()) != null) {
				if (line.indexOf("OK") != -1) {
					ok = true;
					writeToFile(neterrfilepath, line, true);
				} else if (line.indexOf("<") != -1 && line.indexOf(">") != -1) {
					writeToFile(neterrfilepath, line, true);
					// Save the number of battles for the bots into battlesnumfile !!!!!!!!!!!!!
					String bot1 = results.get(i * 3 + 1);

					bot1 = bot1.substring(0, bot1.indexOf(","));
					String bot2 = results.get(i * 3 + 2);

					bot2 = bot2.substring(0, bot2.indexOf(","));
					line = line.replaceAll("<", "");
					line = line.replaceAll(">", "");
					String[] b = line.split(" ");

					if (b.length == 2) {
						writeToFile(battlesnumfile, game + "," + bot1 + "," + b[0], true);
						writeToFile(battlesnumfile, game + "," + bot2 + "," + b[1], true);
					}
				} else if (line.indexOf("[") != -1 && line.indexOf("]") != -1) {
					line = line.substring(1);
					line = line.substring(0, line.length() - 1);
					String[] items = line.split(",");
					String bot1 = items[0].substring(0, items[0].lastIndexOf("_")) + " "
							+ items[0].substring(items[0].lastIndexOf("_") + 1);
					String bot2 = items[1].substring(0, items[1].lastIndexOf("_")) + " "
							+ items[1].substring(items[1].lastIndexOf("_") + 1);
					String battle = bot1 + "," + bot2 + "," + "SERVER";

					if (priority != null) {
						writeToFile(priority, battle, true);
					}
				} else {
					writeToFile(neterrfilepath, line, true);
				}
			}
			if (!ok) {
				saverror(outtxt, results.get(i * 3), results.get(i * 3 + 1), results.get(i * 3 + 2), saveonerror);
				if (saveonerror) {
					errorsfound = true;
				}
			}
		} catch (IOException e) {
			writeToFile(neterrfilepath, "ERROR: " + e, true);
			if (saveonerror) {
				errorsfound = true;
			}
			saverror(outtxt, results.get(i * 3), results.get(i * 3 + 1), results.get(i * 3 + 2), saveonerror);
		} finally {
			if (wr != null) {
				wr.close();
			}
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {}
			}
		}
		writeToFile(neterrfilepath, "Data sent.", true);
		return errorsfound;
	}
}
