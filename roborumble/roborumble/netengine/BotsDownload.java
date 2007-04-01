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
 *     - Replaced the robocode.util.Utils.copy() method with FileTransfer.copy()
 *     - Bugfix: Solved ZipException by creating a session to the Robocode
 *       Repository site
 *     - Minor optimizations
 *******************************************************************************/
package roborumble.netengine;


import java.net.*;
import java.util.*;
import java.util.Vector;
import java.util.jar.*;
import java.util.zip.*;
import java.io.*;
import roborumble.battlesengine.*;

import static roborumble.netengine.FileTransfer.DownloadStatus;


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

	public BotsDownload(String propertiesfile) {
		// Read parameters
		Properties parameters = null;

		try {
			parameters = new Properties();
			parameters.load(new FileInputStream(propertiesfile));
		} catch (Exception e) {
			System.out.println("Parameters File not found !!!");
		}
		// internetrepository = parameters.getProperty("BOTSURL", "");
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
	}

	public boolean downloadRatings() {
		// delete previous files
		if (!generalbotsfile.equals("")) {
			(new File(generalbotsfile)).delete();
		}
		if (!minibotsfile.equals("")) {
			(new File(minibotsfile)).delete();
		}
		if (!microbotsfile.equals("")) {
			(new File(microbotsfile)).delete();
		}
		if (!nanobotsfile.equals("")) {
			(new File(nanobotsfile)).delete();
		}
		// download new ones
		if (ratingsurl.equals("")) {
			return false;
		}
		boolean downloaded = true;

		if (!generalbots.equals("") && !generalbotsfile.equals("")) {
			downloaded = downloadRatingsFile(generalbots, generalbotsfile) & downloaded;
		}
		if (!minibots.equals("") && !minibotsfile.equals("")) {
			downloaded = downloadRatingsFile(minibots, minibotsfile) & downloaded;
		}
		if (!microbots.equals("") && !microbotsfile.equals("")) {
			downloaded = downloadRatingsFile(microbots, microbotsfile) & downloaded;
		}
		if (!nanobots.equals("") && !nanobotsfile.equals("")) {
			downloaded = downloadRatingsFile(nanobots, nanobotsfile) & downloaded;
		}
		return downloaded;
	}

	public boolean downloadParticipantsList() {
		String begin = "<" + tag + ">";
		String end = "</" + tag + ">";
		Vector<String> bots = new Vector<String>();

		try {
			URL url = new URL(participantsurl);

			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

			urlc.setRequestMethod("GET");
			urlc.setDoInput(true);
			urlc.connect();

			boolean arebots = false;
			BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

			for (String str; (str = in.readLine()) != null;) {
				if (str.indexOf(begin) != -1) {
					arebots = true;
				} else if (str.indexOf(end) != -1) {
					arebots = false;
				} else if (arebots) {
					bots.add(str);
				}
			}
			in.close();
			urlc.disconnect();

			PrintStream outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(participantsfile)), false);

			for (int i = 0; i < bots.size(); i++) {
				outtxt.println(bots.get(i));
			}
			outtxt.close();

		} catch (Exception e) {
			System.out.println("Unable to retrieve participants list");
			System.out.println(e);
			return false;
		}
		return true;
	}

	public boolean downloadMissingBots() {
		Vector<String> jars = new Vector<String>();
		Vector<String> ids = new Vector<String>();
		Vector<String> names = new Vector<String>();

		// Read participants
		try {
			FileReader fr = new FileReader(participantsfile);
			BufferedReader br = new BufferedReader(fr);

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
			br.close();
		} catch (Exception e) {
			System.out.println("Participants file not found ... Aborting");
			System.out.println(e);
			return false;
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
		if (!sizesfile.equals("")) {
			try {
				FileReader fr = new FileReader(participantsfile);
				BufferedReader br = new BufferedReader(fr);

				for (String record; (record = br.readLine()) != null;) {
					String name = record.substring(0, record.indexOf(","));

					name = name.replace(' ', '_');
					size.CheckCompetitorsForSize(name, name, 1500);
				}
				br.close();
			} catch (Exception e) {
				System.out.println("Battles input file not found ... Aborting");
				System.out.println(e);
				return;
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

		String url = null;
		String sessionId = null;

		try {
			if (id.indexOf("://") == -1) {
				url = "http://robocoderepository.com/Controller.jsp?submitAction=downloadClass&id=" + id;

				sessionId = FileTransfer.getSessionId(
						"http://robocoderepository.com/BotSearch.jsp?botName=''&authorName=''&uploadDate=");
			} else {
				url = id;
			}
		} catch (Exception e) {
			System.out.println("Wrong URL: " + url);
			return false;
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

			Properties parameters = null;

			parameters = new Properties();
			parameters.load(properties);

			if (!isteams.equals("YES")) {
				String classname = parameters.getProperty("robot.classname", "");
				String version = parameters.getProperty("robot.version", "");

				return (botname.equals(classname + " " + version));
			} else {
				String version = parameters.getProperty("team.version", "");

				return (botname.equals(botname.substring(0, botname.indexOf(" ")) + " " + version));
			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	// ----------------------------------------------------------------------------------
	// download ratings file
	// ----------------------------------------------------------------------------------
	private boolean downloadRatingsFile(String competition, String file) {

		try {
			URL url = new URL(ratingsurl + "?version=1&game=" + competition);

			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

			urlc.setRequestMethod("GET");
			urlc.setDoInput(true);
			urlc.connect();

			PrintStream outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), false);

			BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

			for (String str; (str = in.readLine()) != null;) {
				outtxt.println(str);
			}
			in.close();
			urlc.disconnect();

			outtxt.close();

		} catch (Exception e) {
			System.out.println("Unable to ratings for " + competition);
			System.out.println(e);
			return false;
		}
		return true;
	}

	// ----------------------------------------------------------------------------------
	// download ratings file
	// ----------------------------------------------------------------------------------

	public boolean notifyServerForOldParticipants() {
		// Load participants names
		Hashtable<String, String> namesall = new Hashtable<String, String>();

		try {
			FileReader fr = new FileReader(participantsfile);
			BufferedReader br = new BufferedReader(fr);

			for (String record; (record = br.readLine()) != null;) {
				if (record.indexOf(",") != -1) {
					String name = record.substring(0, record.indexOf(","));

					name = name.replace(' ', '_');
					namesall.put(name, name);
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Participants file not found when removing old participants ... Aborting");
			System.out.println(e);
			return false;
		}

		// Load ratings files
		Properties generalratings = new Properties();
		Properties miniratings = new Properties();
		Properties microratings = new Properties();
		Properties nanoratings = new Properties();

		try {
			generalratings.load(new FileInputStream(generalbotsfile));
		} catch (Exception e) {
			generalratings = null;
		}
		try {
			miniratings.load(new FileInputStream(minibotsfile));
		} catch (Exception e) {
			miniratings = null;
		}
		try {
			microratings.load(new FileInputStream(microbotsfile));
		} catch (Exception e) {
			microratings = null;
		}
		try {
			nanoratings.load(new FileInputStream(nanobotsfile));
		} catch (Exception e) {
			nanoratings = null;
		}

		// Check general ratings
		if (generalratings == null) {
			return false;
		}
		for (Enumeration<?> e = generalratings.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();

			if (!namesall.containsKey(key)) {
				// remove the key from the ratings file
				System.out.println("Removing entry ... " + key + " from " + generalbots);
				removebot(generalbots, key);
			}
		}
		// Check mini ratings
		if (miniratings == null) {
			return true;
		}
		for (Enumeration<?> e = miniratings.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();

			if (!namesall.containsKey(key)) {
				// remove the key from the ratings file
				System.out.println("Removing entry ... " + key + " from " + minibots);
				removebot(minibots, key);
			}
		}

		// Check micro ratings
		if (microratings == null) {
			return true;
		}
		for (Enumeration<?> e = microratings.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();

			if (!namesall.containsKey(key)) {
				// remove the key from the ratings file
				System.out.println("Removing entry ... " + key + " from " + microbots);
				removebot(microbots, key);
			}
		}

		// Check nano ratings
		if (nanoratings == null) {
			return true;
		}
		for (Enumeration<?> e = nanoratings.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();

			if (!namesall.containsKey(key)) {
				// remove the key from the ratings file
				System.out.println("Removing entry ... " + key + " from " + nanobots);
				removebot(nanobots, key);
			}
		}

		return true;
	}

	private void removebot(String game, String bot) {
		if (removeboturl.equals("")) {
			System.out.println("UPDATEBOTS URL not defined!");
			return;
		}

		String data = "version=1&game=" + game + "&name=" + bot.trim() + "&dummy=NA";

		try {
			// Send data
			URL url = new URL(removeboturl);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);
			PrintWriter wr = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));

			wr.println(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			for (String line; (line = rd.readLine()) != null;) {
				System.out.println(line);
			}

			wr.close();
			rd.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
