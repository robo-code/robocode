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
 *     - Removed dead code
 *     - Minor optimizations
 *     - Replaced the robocode.util.Utils.copy() method with FileTransfer.copy()
 *     - Bugfix: Solved ZipException by creating a session to the Robocode
 *       Repository site
 *     - Properties are now read using PropertiesUtil.getProperties()
 *     - Renamed CheckCompetitorsForSize() into checkCompetitorsForSize()
 *     - Added missing close() to streams
 *     - Added new EXCLUDE property, which is used for excluding participants
 *     - Added the isExcluded() method
 *******************************************************************************/
package roborumble.netengine;


import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.io.*;

import roborumble.battlesengine.*;

import static roborumble.netengine.FileTransfer.DownloadStatus;
import static roborumble.util.PropertiesUtil.getProperties;


/**
 * Class used for downloading participating robots from the Internet.
 * Manages the download operations (participants and JAR files).
 * Controlled by properties files.
 * 
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BotsDownload {

	// private String internetrepository;
	private String botsrepository;
	private String participantsfile;
	private String participantsurl;
	private String tempdir;
	private String tag;
	private String isteams;
	private String sizesfile;
	private CompetitionsSelector size;
	private String ratingsurl;
	private String generalbots;
	private String minibots;
	private String microbots;
	private String nanobots;
	private String generalbotsfile;
	private String minibotsfile;
	private String microbotsfile;
	private String nanobotsfile;
	private String removeboturl;
	private String[] excludes;

	public BotsDownload(String propertiesfile) {
		// Read parameters
		Properties parameters = getProperties(propertiesfile);

		botsrepository = parameters.getProperty("BOTSREP", "");
		isteams = parameters.getProperty("TEAMS", "NOT");
		participantsurl = parameters.getProperty("PARTICIPANTSURL", "");
		participantsfile = parameters.getProperty("PARTICIPANTSFILE", "");
		tag = parameters.getProperty("STARTAG", "pre");
		tempdir = parameters.getProperty("TEMP", "");

		// Code size
		sizesfile = parameters.getProperty("CODESIZEFILE", "");
		size = new CompetitionsSelector(sizesfile, botsrepository);

		// Ratings files
		ratingsurl = parameters.getProperty("RATINGS.URL", "");
		generalbots = propertiesfile;
		while (generalbots.indexOf("/") != -1) {
			generalbots = generalbots.substring(generalbots.indexOf("/") + 1);
		}
		generalbots = generalbots.substring(0, generalbots.indexOf("."));
		minibots = parameters.getProperty("MINIBOTS", "");
		microbots = parameters.getProperty("MICROBOTS", "");
		nanobots = parameters.getProperty("NANOBOTS", "");
		generalbotsfile = parameters.getProperty("RATINGS.GENERAL", "");
		minibotsfile = parameters.getProperty("RATINGS.MINIBOTS", "");
		microbotsfile = parameters.getProperty("RATINGS.MICROBOTS", "");
		nanobotsfile = parameters.getProperty("RATINGS.NANOBOTS", "");
		// remove old bots
		removeboturl = parameters.getProperty("UPDATEBOTSURL", "");

		// Read and prepare exclude filters
		String exclude = parameters.getProperty("EXCLUDE");

		if (exclude != null) {
			// Convert into regular expression

			// Dots must be dots, not "any character" in the regular expression
			exclude = exclude.replaceAll("\\.", "\\\\.");

			// The wildcard character ? corresponds to the regular expression .?
			exclude = exclude.replaceAll("\\?", ".?");

			// The wildcard character * corresponds to the regular expression .*
			exclude = exclude.replaceAll("\\*", ".*");

			// Split the exclude line into independent exclude filters that are trimmed for white-spaces
			excludes = exclude.split("[\\s,;]+");
		}
	}

	public boolean downloadRatings() {
		// delete previous files
		if (generalbotsfile.length() != 0) {
			(new File(generalbotsfile)).delete();
		}
		if (minibotsfile.length() != 0) {
			(new File(minibotsfile)).delete();
		}
		if (microbotsfile.length() != 0) {
			(new File(microbotsfile)).delete();
		}
		if (nanobotsfile.length() != 0) {
			(new File(nanobotsfile)).delete();
		}
		// download new ones
		if (ratingsurl.length() == 0) {
			return false;
		}
		boolean downloaded = true;

		if (generalbots.length() != 0 && generalbotsfile.length() != 0) {
			downloaded = downloadRatingsFile(generalbots, generalbotsfile) & downloaded;
		}
		if (minibots.length() != 0 && minibotsfile.length() != 0) {
			downloaded = downloadRatingsFile(minibots, minibotsfile) & downloaded;
		}
		if (microbots.length() != 0 && microbotsfile.length() != 0) {
			downloaded = downloadRatingsFile(microbots, microbotsfile) & downloaded;
		}
		if (nanobots.length() != 0 && nanobotsfile.length() != 0) {
			downloaded = downloadRatingsFile(nanobots, nanobotsfile) & downloaded;
		}
		return downloaded;
	}

	public boolean downloadParticipantsList() {
		String begin = "<" + tag + ">";
		String end = "</" + tag + ">";
		Vector<String> bots = new Vector<String>();
		BufferedReader in = null;

		try {
			URL url = new URL(participantsurl);

			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

			urlc.setRequestMethod("GET");
			urlc.setDoInput(true);
			urlc.connect();

			boolean arebots = false;

			in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

			for (String str; (str = in.readLine()) != null;) {
				if (str.indexOf(begin) != -1) {
					arebots = true;
				} else if (str.indexOf(end) != -1) {
					arebots = false;
				} else if (arebots) {
					String name = str.substring(0, str.indexOf(","));

					if (!isExcluded(name)) {
						bots.add(str);
					}
				}
			}
			urlc.disconnect();

			PrintStream outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(participantsfile)), false);

			for (String bot : bots) {
				outtxt.println(bot);
			}
			outtxt.close();

		} catch (IOException e) {
			System.out.println("Unable to retrieve participants list");
			System.out.println(e);
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		return true;
	}

	public boolean downloadMissingBots() {
		Vector<String> jars = new Vector<String>();
		Vector<String> ids = new Vector<String>();
		Vector<String> names = new Vector<String>();

		// Read participants

		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(participantsfile);

			br = new BufferedReader(fr);

			for (String record; (record = br.readLine()) != null;) {
				if (record.indexOf(",") >= 0) {
					String id = record.substring(record.indexOf(",") + 1);
					String name = record.substring(0, record.indexOf(","));
					String jar = name.replace(' ', '_') + ".jar";

					jars.add(jar);
					ids.add(id);
					names.add(name);
				}
			}
		} catch (IOException e) {
			System.out.println("Participants file not found ... Aborting");
			System.out.println(e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}

		// check if the file exists in the repository and download if not present
		for (int i = 0; i < jars.size(); i++) {
			String botjar = jars.get(i);
			String botid = ids.get(i);
			String botname = names.get(i);
			String botpath = botsrepository + botjar;
			boolean exists = (new File(botpath)).exists();

			if (!exists) {
				boolean downloaded = downloadBot(botname, botjar, botid, botsrepository, tempdir);

				if (!downloaded) {
					System.out.println("Could not download " + botjar);
				}
			}
		}
		return true;
	}

	public void updateCodeSize() {
		if (sizesfile.length() != 0) {
			BufferedReader br = null;

			try {
				FileReader fr = new FileReader(participantsfile);

				br = new BufferedReader(fr);

				for (String record; (record = br.readLine()) != null;) {
					String name = record.substring(0, record.indexOf(","));

					name = name.replace(' ', '_');
					size.checkCompetitorsForSize(name, name, 1500);
				}
			} catch (IOException e) {
				System.out.println("Battles input file not found ... Aborting");
				System.out.println(e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {}
				}
			}
		}
	}

	private boolean downloadBot(String botname, String file, String id, String destination, String tempdir) {
		String filed = tempdir + file;
		String finald = destination + file;

		// check if the bot exists in the repository

		boolean exists = (new File(finald)).exists();

		if (exists) {
			System.out.println("The bot already exists in the repository.");
			return false;
		}

		// Download the bot

		String url;
		String sessionId = null;

		if (id.indexOf("://") == -1) {
			url = "http://robocoderepository.com/Controller.jsp?submitAction=downloadClass&id=" + id;

			sessionId = FileTransfer.getSessionId(
					"http://robocoderepository.com/BotSearch.jsp?botName=''&authorName=''&uploadDate=");
		} else {
			url = id;
		}

		System.out.println("Trying to download " + botname);

		DownloadStatus downloadStatus = FileTransfer.download(url, filed, sessionId);

		if (downloadStatus == DownloadStatus.FILE_NOT_FOUND) {
			System.out.println("Could not find " + botname + " from " + url);
			return false;
		} else if (downloadStatus == DownloadStatus.COULD_NOT_CONNECT) {
			System.out.println("Could not connect to " + url);
			return false;
		}

		// Check the bot and save it into the repository

		if (checkJarFile(filed, botname)) {
			if (!FileTransfer.copy(filed, finald)) {
				System.out.println("Unable to copy " + filed + " into the repository");
				return false;
			}
		} else {
			System.out.println("Downloaded file is wrong or corrupted:" + file);
			return false;
		}

		System.out.println("Downloaded " + botname + " into " + finald);
		return true;
	}

	private boolean checkJarFile(String file, String botname) {
		if (botname.indexOf(" ") == -1) {
			System.out.println("Are you sure " + botname + " is a bot/team? Can't download it.");
			return false;
		}

		String bot = botname.substring(0, botname.indexOf(" "));

		bot = bot.replace('.', '/');
		if (!isteams.equals("YES")) {
			bot += ".properties";
		} else {
			bot += ".team";
		}

		try {
			JarFile jarf = new JarFile(file);
			ZipEntry zipe = jarf.getJarEntry(bot);

			if (zipe == null) {
				System.out.println("Not able to read properties");
				return false;
			}
			InputStream properties = jarf.getInputStream(zipe);

			Properties parameters = getProperties(properties);

			if (!isteams.equals("YES")) {
				String classname = parameters.getProperty("robot.classname", "");
				String version = parameters.getProperty("robot.version", "");

				return (botname.equals(classname + " " + version));
			}
			String version = parameters.getProperty("team.version", "");

			return (botname.equals(botname.substring(0, botname.indexOf(" ")) + " " + version));
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	// ----------------------------------------------------------------------------------
	// download ratings file
	// ----------------------------------------------------------------------------------
	private boolean downloadRatingsFile(String competition, String file) {

		BufferedReader in = null;
		PrintStream outtxt = null;

		try {
			URL url = new URL(ratingsurl + "?version=1&game=" + competition);

			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

			urlc.setRequestMethod("GET");
			urlc.setDoInput(true);
			urlc.connect();

			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), false);

			in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

			for (String str; (str = in.readLine()) != null;) {
				outtxt.println(str);
			}
			urlc.disconnect();

		} catch (IOException e) {
			System.out.println("Unable to ratings for " + competition);
			System.out.println(e);
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
			if (outtxt != null) {
				outtxt.close();
			}
		}
		return true;
	}

	// ----------------------------------------------------------------------------------
	// download ratings file
	// ----------------------------------------------------------------------------------

	public boolean notifyServerForOldParticipants() {
		// Load participants names
		Hashtable<String, String> namesall = new Hashtable<String, String>();
		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(participantsfile);

			br = new BufferedReader(fr);

			for (String record; (record = br.readLine()) != null;) {
				if (record.indexOf(",") != -1) {
					String name = record.substring(0, record.indexOf(",")).replace(' ', '_');

					namesall.put(name, name);
				}
			}
		} catch (IOException e) {
			System.out.println("Participants file not found when removing old participants ... Aborting");
			System.out.println(e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}

		// Load ratings files
		Properties generalratings = getProperties(generalbotsfile);
		Properties miniratings = getProperties(minibotsfile);
		Properties microratings = getProperties(microbotsfile);
		Properties nanoratings = getProperties(nanobotsfile);

		// Check general ratings
		for (Enumeration<?> e = generalratings.propertyNames(); e.hasMoreElements();) {
			String bot = (String) e.nextElement();

			if (!(isExcluded(bot) || namesall.containsKey(bot))) {
				// Remove the bot from the ratings file
				System.out.println("Removing entry ... " + bot + " from " + generalbots);
				removebot(generalbots, bot);
			}
		}
		// Check mini ratings
		for (Enumeration<?> e = miniratings.propertyNames(); e.hasMoreElements();) {
			String bot = (String) e.nextElement();

			if (!(isExcluded(bot) || namesall.containsKey(bot))) {
				// Remove the bot from the ratings file
				System.out.println("Removing entry ... " + bot + " from " + minibots);
				removebot(minibots, bot);
			}
		}

		// Check micro ratings
		for (Enumeration<?> e = microratings.propertyNames(); e.hasMoreElements();) {
			String bot = (String) e.nextElement();

			if (!(isExcluded(bot) || namesall.containsKey(bot))) {
				// Remove the bot from the ratings file
				System.out.println("Removing entry ... " + bot + " from " + microbots);
				removebot(microbots, bot);
			}
		}

		// Check nano ratings
		for (Enumeration<?> e = nanoratings.propertyNames(); e.hasMoreElements();) {
			String bot = (String) e.nextElement();

			if (!(isExcluded(bot) || namesall.containsKey(bot))) {
				// Remove the bot from the ratings file
				System.out.println("Removing entry ... " + bot + " from " + nanobots);
				removebot(nanobots, bot);
			}
		}

		return true;
	}

	private void removebot(String game, String bot) {
		if (removeboturl.length() == 0) {
			System.out.println("UPDATEBOTS URL not defined!");
			return;
		}

		String data = "version=1&game=" + game + "&name=" + bot.trim() + "&dummy=NA";
		PrintWriter wr = null;
		BufferedReader rd = null;

		try {
			// Send data
			URL url = new URL(removeboturl);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);
			wr = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));

			wr.println(data);
			wr.flush();

			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			for (String line; (line = rd.readLine()) != null;) {
				System.out.println(line);
			}
		} catch (IOException e) {
			System.out.println(e);
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
	}

	// Check if a robot is excluded
	private boolean isExcluded(String bot) {
		if (excludes == null) {
			return false;
		}

		// Check the name against all exclude filters
		for (int i = excludes.length - 1; i >= 0; i--) {
			try {
				if (bot.matches(excludes[i])) {
					return true;
				}
			} catch (java.util.regex.PatternSyntaxException e) {
				// Clear the current exclude if the syntax is illegal (for next time this method is called)
				excludes[i] = "";
			}
		}

		// Not excluded
		return false;
	}
}
