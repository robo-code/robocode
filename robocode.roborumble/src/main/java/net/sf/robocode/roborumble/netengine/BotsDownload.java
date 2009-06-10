/*******************************************************************************
 * Copyright (c) 2003, 2008 Albert P�rez and RoboRumble contributors
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
package net.sf.robocode.roborumble.netengine;


import net.sf.robocode.io.Logger;
import net.sf.robocode.roborumble.battlesengine.CompetitionsSelector;
import static net.sf.robocode.roborumble.netengine.FileTransfer.DownloadStatus;
import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


/**
 * Class used for downloading participating robots from the Internet.
 * Manages the download operations (participants and JAR files).
 * Controlled by properties files.
 *
 * @author Albert P�rez (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BotsDownload {

	// private String internetrepository;
	private final String botsrepository;
	private final String participantsfile;
	private final String participantsurl;
	private final String tempdir;
	private final String tag;
	private final String isteams;
	private final String sizesfile;
	private final CompetitionsSelector size;
	private final String ratingsurl;
	private String generalbots;
	private final String minibots;
	private final String microbots;
	private final String nanobots;
	private final String generalbotsfile;
	private final String minibotsfile;
	private final String microbotsfile;
	private final String nanobotsfile;
	private final String removeboturl;
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
		File file;

		// delete previous files
		if (generalbotsfile.length() != 0) {
			file = new File(generalbotsfile);
			if (file.exists() && !file.delete()) {
				Logger.logError("Can't delete file");
			}
		}
		if (minibotsfile.length() != 0) {
			file = new File(minibotsfile);
			if (file.exists() && !file.delete()) {
				Logger.logError("Can't delete file");
			}
		}
		if (microbotsfile.length() != 0) {
			file = new File(microbotsfile);
			if (file.exists() && !file.delete()) {
				Logger.logError("Can't delete file");
			}
		}
		if (nanobotsfile.length() != 0) {
			file = new File(nanobotsfile);
			if (file.exists() && !file.delete()) {
				Logger.logError("Can't delete file");
			}
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
		HttpURLConnection urlc = null;

		try {
			URL url = new URL(participantsurl);

			urlc = (HttpURLConnection) url.openConnection();

			urlc.setRequestMethod("GET");
			urlc.setDoInput(true);
			urlc.connect();

			// Check that we received a HTTP_OK response code.
			// Bugfix [2779557] - Client tries to remove all participants.
			if (urlc.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.print("Unable to retrieve participants list. Response is " + urlc.getResponseCode());
				if (urlc.getResponseMessage() != null) {
					System.out.print(": " + urlc.getResponseMessage());
				}
				System.out.println();
				
				return false; // Error
			}

			boolean arebots = false;

			in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

			for (String particiant; (particiant = in.readLine()) != null;) {

				if (particiant.indexOf(begin) >= 0) {
					arebots = true;
				} else if (particiant.indexOf(end) >= 0) {
					arebots = false;
				} else if (arebots) {
					if (isExcludedPartificant(particiant) == false && verifyParticipantFormat(particiant) == false) {
						if (particiant.trim().length() > 0) {
							System.out.println("Participant ignored due to invalid line: '" + particiant + '\'');
						}
					} else {
						bots.add(particiant);
					}
				}
			}

			// Prevent our local participants file to be overwritten, if the downloaded list is empty.
			// Bugfix [2779557] - Client tries to remove all participants.
			if (bots.size() == 0) {
				System.out.println("The participants list is empty");
				return false; // Error
			}

			final File dir = new File(participantsfile).getParentFile();

			if (!dir.exists() && !dir.mkdirs()) {
				Logger.logError("Can't create " + dir);
			}

			PrintStream outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(participantsfile)), false);

			for (String bot : bots) {
				outtxt.println(bot);
			}
			outtxt.close();

		} catch (IOException e) {
			System.out.println("Unable to retrieve participants list:");
			System.out.println(e);
			return false; // Error
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {}
			}
			if (urlc != null) {
				urlc.disconnect();
			}
		}
		return true; // Success
	}

	public void downloadMissingBots() {
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
					String id = record.substring(record.indexOf(",") + 1).trim();
					String name = record.substring(0, record.indexOf(",")).trim();
					String jar = name.replace(' ', '_') + ".jar";

					jars.add(jar);
					ids.add(id);
					names.add(name);
				}
			}
		} catch (IOException e) {
			System.out.println("Participants file not found ... Aborting");
			System.out.println(e);
			return;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {}
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
					} catch (IOException ignored) {}
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
		if (isteams.equals("YES")) {
			bot += ".team";
		} else {
			bot += ".properties";
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

			final File dir = new File(file).getParentFile();

			if (!dir.exists() && !dir.mkdirs()) {
				Logger.logError("Can't create " + dir);
			}

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
				} catch (IOException ignored) {}
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

	public void notifyServerForOldParticipants() {
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
			return;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {}
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
				} catch (IOException ignored) {}
			}
		}
	}

	// Checks if a participant must be excluded
	private boolean isExcludedPartificant(String participant) {
		return (excludes != null) ? isExcluded(participant.split("[,]")[0].trim()) : false;
	}

	// Checks if a robot name is in the exclude list
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

	// Checks if a participant format from the participant list is valid
	private boolean verifyParticipantFormat(String participant) {
		String[] split = participant.split("[,]");

		boolean matches = false;
	
		if (split.length == 2) {
			String robot = split[0].trim();
			String link = split[1].trim();
	
			if (robot.matches("[\\w\\.]+[ ][\\w\\.-]+")) {
				if (link.startsWith("http")) {
					try {
						new URL(link);
						matches = true;
					} catch (MalformedURLException e) {}
				} else if (link.matches("[\\d]+")) {
					matches = true;
				}
			}
		}
		return matches;
	}
}
