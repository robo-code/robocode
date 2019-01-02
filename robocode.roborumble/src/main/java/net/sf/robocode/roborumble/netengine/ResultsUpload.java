/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
 */
public class ResultsUpload {

	private final String client;
	private final String resultsfile;
	private final String resultsurl;
	private final String tempdir;
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

	public ResultsUpload(String propertiesfile, String clientVersion) {
		// Read parameters
		Properties parameters = getProperties(propertiesfile);

		resultsfile = parameters.getProperty("OUTPUT", "");
		resultsurl = parameters.getProperty("RESULTSURL", "");
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
		teams = parameters.getProperty("TEAMS", "NOT");
		melee = parameters.getProperty("MELEE", "NOT");

		// Open competitions selector
		size = new CompetitionsSelector(sizesfile, botsrepository);
	}

	public void uploadResults() {

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
			return;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {}
			}
		}

		// Open the temp file to put the unuploaded results
		PrintStream outtxt;

		try {
			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(tempdir + "results.txt")), false);
		} catch (IOException e) {
			System.out.println("Not able to open output file ... Aborting");
			System.out.println(e);
			return;
		}

		// Open the file to put the battles number for each participant
		PrintStream battlesnum;

		try {
			battlesnum = new PrintStream(new BufferedOutputStream(new FileOutputStream(battlesnumfile)), false);
		} catch (IOException e) {
			System.out.println("Not able to open battles number file ... Aborting");
			System.out.println(e);

			outtxt.close();
			return;
		}

		// Open the file to put the battles which have priority
		PrintStream prioritybattles;

		try {
			prioritybattles = new PrintStream(new BufferedOutputStream(new FileOutputStream(priority)), false);
		} catch (IOException e) {
			System.out.println("Not able to open priorities file ... Aborting");
			System.out.println(e);

			outtxt.close();
			battlesnum.close();
			return;
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

			// if the match mode was general, then send the results to all competitions (assuming codesize is used).
			// if its not, then send results only to smaller size competitions

			final String commonData = "&version=1" + "&client=" + client + "&teams=" + teams + "&melee=" + melee
					+ "&rounds=" + header[1] + "&field=" + header[2] + "&user=" + user + "&time=" + header[4] + "&fname="
					+ first[0] + "&fscore=" + first[1] + "&fbulletd=" + first[2] + "&fsurvival=" + first[3] + "&sname="
					+ second[0] + "&sscore=" + second[1] + "&sbulletd=" + second[2] + "&ssurvival=" + second[3];

			String data = "game=" + game + commonData;

			boolean errsaved = false;

			if (matchtype.equals("GENERAL") || matchtype.equals("SERVER")) {
				errsaved = senddata(game, data, outtxt, true, results, i, battlesnum, prioritybattles);
			}

			if (sizesfile.length() != 0) { // upload also related competitions
				if (minibots.length() != 0 && !matchtype.equals("NANO") && !matchtype.equals("MICRO")
						&& size.checkCompetitorsForSize(first[0], second[0], 1500)) {
					data = "game=" + minibots + commonData;
					errsaved = errsaved
							| senddata(minibots, data, outtxt, !errsaved, results, i, battlesnum, prioritybattles);
				}
				if (microbots.length() != 0 && !matchtype.equals("NANO")
						&& size.checkCompetitorsForSize(first[0], second[0], 750)) {
					data = "game=" + microbots + commonData;
					errsaved = errsaved
							| senddata(microbots, data, outtxt, !errsaved, results, i, battlesnum, prioritybattles);
				}
				if (nanobots.length() != 0 && size.checkCompetitorsForSize(first[0], second[0], 250)) {
					data = "game=" + nanobots + commonData;
					errsaved = errsaved
							| senddata(nanobots, data, outtxt, !errsaved, results, i, battlesnum, prioritybattles);
				}
			}
			errorsfound = errorsfound || errsaved;
		}

		// close files
		outtxt.close();
		battlesnum.close();
		prioritybattles.close();

		// delete results file
		File r = new File(resultsfile);
		boolean b = r.delete();

		if (!b) {
			System.out.println("Unable to delete results file.");
		}

		// copy temp file into results file if there was some error
		if (errorsfound) {
			if (!FileTransfer.copy(tempdir + "results.txt", resultsfile)) {
				System.out.println("Error when copying results errors file.");
			}
		}
	}

	private void saveError(PrintStream outtxt, String match, String bot1, String bot2, boolean saveonerror) {
		if (saveonerror) {
			outtxt.println(match);
			outtxt.println(bot1);
			outtxt.println(bot2);
		}
		System.out.println("Unable to upload results " + match + " " + bot1 + " " + bot2);
	}

	private boolean senddata(String game, String data, PrintStream outtxt, boolean saveonerror, Vector<String> results, int i, PrintStream battlesnum, PrintStream prioritybattles) {
		boolean errorsfound = false;
		OutputStream out = null;
		OutputStreamWriter outputStreamWriter = null;
		PrintWriter wr = null;
		InputStream in = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try {
			// Send data
			URLConnection conn = FileTransfer.openOutputURLConnection(new URL(resultsurl));

			out = FileTransfer.getOutputStream(conn);
			outputStreamWriter = new OutputStreamWriter(out);
			wr = new PrintWriter(outputStreamWriter);

			wr.println(data);
			wr.flush();

			// Get the response
			in = FileTransfer.getInputStream(conn);
			inputStreamReader = new InputStreamReader(in); 
			bufferedReader = new BufferedReader(inputStreamReader);

			String line;
			boolean ok = false;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.indexOf("OK") != -1) {
					ok = true;
					System.out.println(line);
				} else if (line.indexOf("<") != -1 && line.indexOf(">") != -1) {
					// System.out.println(line);
					// Save the number of battles for the bots into battlesnum !!!!!!!!!!!!!
					String bot1 = results.get(i * 3 + 1);

					bot1 = bot1.substring(0, bot1.indexOf(","));
					String bot2 = results.get(i * 3 + 2);

					bot2 = bot2.substring(0, bot2.indexOf(","));
					line = line.replaceAll("<", "");
					line = line.replaceAll(">", "");
					String[] b = line.split(" ");

					if (b.length == 2) {
						battlesnum.println(game + "," + bot1 + "," + b[0]);
						battlesnum.println(game + "," + bot2 + "," + b[1]);
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

					if (prioritybattles != null) {
						prioritybattles.println(battle);
					}
				} else {
					System.out.println(line);
				}
			}
			if (!ok) {
				saveError(outtxt, results.get(i * 3), results.get(i * 3 + 1), results.get(i * 3 + 2), saveonerror);
				if (saveonerror) {
					errorsfound = true;
				}
			}
		} catch (IOException e) {
			System.out.println(e);
			if (saveonerror) {
				errorsfound = true;
			}
			saveError(outtxt, results.get(i * 3), results.get(i * 3 + 1), results.get(i * 3 + 2), saveonerror);
		} finally {
			if (wr != null) {
				wr.close();
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ignored) {}
			}
		}
		return errorsfound;
	}
}
