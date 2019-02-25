/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.netengine;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.CodeSizeCalculator;
import net.sf.robocode.roborumble.battlesengine.CompetitionsSelector;
import static net.sf.robocode.roborumble.netengine.FileTransfer.DownloadStatus;
import static net.sf.robocode.roborumble.util.ExcludesUtil.*;
import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


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
	private final String botsrepository;
	private final String participantsfile;
	private final String participantsurl;
	private final String tempdir;
	private final String tag;
	private final String isteams;
	private final String sizesfile;
	private final CompetitionsSelector size;
	private final String ratingsurl;
	private String generalBots;
	private final String miniBots;
	private final String microBots;
	private final String nanoBots;
	private final String generalbotsfile;
	private final String minibotsfile;
	private final String microbotsfile;
	private final String nanobotsfile;
	private final String removeboturl;

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
		generalBots = propertiesfile;
		while (generalBots.indexOf("/") != -1) {
			generalBots = generalBots.substring(generalBots.indexOf("/") + 1);
		}
		generalBots = generalBots.substring(0, generalBots.indexOf("."));
		miniBots = parameters.getProperty("MINIBOTS", "");
		microBots = parameters.getProperty("MICROBOTS", "");
		nanoBots = parameters.getProperty("NANOBOTS", "");
		generalbotsfile = parameters.getProperty("RATINGS.GENERAL", "");
		minibotsfile = parameters.getProperty("RATINGS.MINIBOTS", "");
		microbotsfile = parameters.getProperty("RATINGS.MICROBOTS", "");
		nanobotsfile = parameters.getProperty("RATINGS.NANOBOTS", "");
		// remove old bots
		removeboturl = parameters.getProperty("UPDATEBOTSURL", "");

		// Read and prepare exclude filters
		setExcludes(parameters);
	}

	public boolean downloadRatings() {
		File file;

		// delete previous files
		if (generalbotsfile.length() != 0) {
			file = new File(generalbotsfile);
			if (file.exists() && !file.delete()) {
				Logger.logError("Can't delete file: " + file);
			}
		}
		if (minibotsfile.length() != 0) {
			file = new File(minibotsfile);
			if (file.exists() && !file.delete()) {
				Logger.logError("Can't delete file: " + file);
			}
		}
		if (microbotsfile.length() != 0) {
			file = new File(microbotsfile);
			if (file.exists() && !file.delete()) {
				Logger.logError("Can't delete file: " + file);
			}
		}
		if (nanobotsfile.length() != 0) {
			file = new File(nanobotsfile);
			if (file.exists() && !file.delete()) {
				Logger.logError("Can't delete file: " + file);
			}
		}
		// download new ones
		if (ratingsurl.length() == 0) {
			return false;
		}
		boolean downloaded = true;

		if (generalBots.length() != 0 && generalbotsfile.length() != 0) {
			downloaded = downloadRatingsFile(generalBots, generalbotsfile) & downloaded;
		}
		if (miniBots.length() != 0 && minibotsfile.length() != 0) {
			downloaded = downloadRatingsFile(miniBots, minibotsfile) & downloaded;
		}
		if (microBots.length() != 0 && microbotsfile.length() != 0) {
			downloaded = downloadRatingsFile(microBots, microbotsfile) & downloaded;
		}
		if (nanoBots.length() != 0 && nanobotsfile.length() != 0) {
			downloaded = downloadRatingsFile(nanoBots, nanobotsfile) & downloaded;
		}
		return downloaded;
	}

	public boolean downloadParticipantsList() {
		String begin = "<" + tag + ">";
		String end = "</" + tag + ">";
		Vector<String> bots = new Vector<String>();

		InputStream in = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		HttpURLConnection conn = null;

		try {
			URL url = new URL(participantsurl);

			conn = FileTransfer.connectToHttpInputConnection(url);

			// Check that we received a HTTP_OK response code.
			// Bugfix [2779557] - Client tries to remove all participants.
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.print("Unable to retrieve participants list. Response is " + conn.getResponseCode());
				if (conn.getResponseMessage() != null) {
					System.out.print(": " + conn.getResponseMessage());
				}
				System.out.println();
				
				return false; // Error
			}

			boolean arebots = false;

			in = FileTransfer.getInputStream(conn);
			inputStreamReader = new InputStreamReader(in); 
			bufferedReader = new BufferedReader(inputStreamReader);

			for (String participant; (participant = bufferedReader.readLine()) != null;) {
				if (participant.indexOf(begin) >= 0) {
					arebots = true;
				} else if (participant.indexOf(end) >= 0) {
					arebots = false;
				} else if (arebots) {
					String bot = participant.split("[,]")[0];

					if (!isExcluded(bot)) {
						if (!verifyParticipantFormat(participant)) {
							if (participant.trim().length() > 0) {
								System.out.println("Participant ignored due to invalid line: " + bot);
							}
						} else {
							bots.add(participant);
						}
					} else {
						System.out.println("Ignored excluded: " + bot);
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
				Logger.logError("Can't create directory: " + dir);
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
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ignored) {}
			}
			if (conn != null) {
				conn.disconnect();
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
					size.checkCompetitorForSize(name, 1500);
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
		String tempFileName = tempdir + file;
		String repositoryFileName = destination + file;

		// check if the bot exists in the repository

		boolean exists = (new File(repositoryFileName)).exists();
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

		DownloadStatus downloadStatus = FileTransfer.download(url, tempFileName, sessionId);

		if (downloadStatus == DownloadStatus.FILE_NOT_FOUND) {
			System.out.println("Could not find " + botname + " from " + url);
			return false;
		} else if (downloadStatus == DownloadStatus.COULD_NOT_CONNECT) {
			System.out.println("Could not connect to " + url);
			return false;
		}

		// Check the bot and save it into the repository

		Integer codeSize;

		if (checkJarFile(tempFileName, botname)) {
			codeSize = CodeSizeCalculator.getJarFileCodeSize(new File(tempFileName));
			if (codeSize == null) {
				System.out.println("Unable to calc codesize for " + tempFileName);
				return false;
			}

			if (!FileTransfer.copy(tempFileName, repositoryFileName)) {
				System.out.println("Unable to copy " + tempFileName + " into the repository");
				return false;
			}
		} else {
			System.out.println("Downloaded file is wrong or corrupted: " + file);
			return false;
		}

		System.out.println("Downloaded " + botname + " into " + repositoryFileName + " (Codesize: " + codeSize + ")");
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

		InputStream in = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		PrintStream outtxt = null;

		try {
			URL url = new URL(ratingsurl + "?version=1&game=" + competition);

			HttpURLConnection conn = FileTransfer.connectToHttpInputConnection(url);

			final File dir = new File(file).getParentFile();

			if (!dir.exists() && !dir.mkdirs()) {
				Logger.logError("Can't create directory: " + dir);
			}

			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), false);

			in = FileTransfer.getInputStream(conn);
			inputStreamReader = new InputStreamReader(in); 
			bufferedReader = new BufferedReader(inputStreamReader);

			for (String str; (str = bufferedReader.readLine()) != null;) {
				outtxt.println(str);
			}
			conn.disconnect();

		} catch (IOException e) {
			System.out.println("Unable to ratings for " + competition);
			System.out.println(e);
			return false;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
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
		Set<String> namesAll = new HashSet<String>();
		Set<String> namesMini = new HashSet<String>();
		Set<String> namesMicro = new HashSet<String>();
		Set<String> namesNano = new HashSet<String>();

		Set<String> notFound = new HashSet<String>();

		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(participantsfile);
			br = new BufferedReader(fr);

			for (String record; (record = br.readLine()) != null;) {
				if (record.indexOf(",") != -1) {
					String name = record.substring(0, record.indexOf(",")).replace(' ', '_');

					File f = new File(botsrepository + name + ".jar");
					if (f.exists()) {
						namesAll.add(name);
						if (size.checkCompetitorForSize(name, 1500)) {
							namesMini.add(name);
						}
						if (size.checkCompetitorForSize(name, 750)) {
							namesMicro.add(name);
						}
						if (size.checkCompetitorForSize(name, 250)) {
							namesNano.add(name);
						}
					} else {
						notFound.add(name);
					}
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
		Properties generalRatings = getProperties(generalbotsfile);
		Properties miniRatings = getProperties(minibotsfile);
		Properties microRatings = getProperties(microbotsfile);
		Properties nanoRatings = getProperties(nanobotsfile);

		// Check general ratings
		for (Enumeration<?> e = generalRatings.propertyNames(); e.hasMoreElements();) {
			String bot = (String) e.nextElement();

			if (!(isExcluded(bot) || notFound.contains(bot) || namesAll.contains(bot))) {
				// Remove the bot from the ratings file
				System.out.println("Removing entry ... " + bot + " from " + generalBots);
				removeBot(generalBots, bot);
			}
		}
		// Check mini ratings
		for (Enumeration<?> e = miniRatings.propertyNames(); e.hasMoreElements();) {
			String bot = (String) e.nextElement();

			if (!(isExcluded(bot) || notFound.contains(bot) || namesMini.contains(bot))) {
				// Remove the bot from the ratings file
				System.out.println("Removing entry ... " + bot + " from " + miniBots);
				removeBot(miniBots, bot);
			}
		}

		// Check micro ratings
		for (Enumeration<?> e = microRatings.propertyNames(); e.hasMoreElements();) {
			String bot = (String) e.nextElement();

			if (!(isExcluded(bot) || notFound.contains(bot) || namesMicro.contains(bot))) {
				// Remove the bot from the ratings file
				System.out.println("Removing entry ... " + bot + " from " + microBots);
				removeBot(microBots, bot);
			}
		}

		// Check nano ratings
		for (Enumeration<?> e = nanoRatings.propertyNames(); e.hasMoreElements();) {
			String bot = (String) e.nextElement();

			if (!(isExcluded(bot) || notFound.contains(bot) || namesNano.contains(bot))) {
				// Remove the bot from the ratings file
				System.out.println("Removing entry ... " + bot + " from " + nanoBots);
				removeBot(nanoBots, bot);
			}
		}
	}

	private void removeBot(String game, String bot) {
		if (removeboturl.length() == 0) {
			System.out.println("UPDATEBOTS URL not defined!");
			return;
		}

		String data = "version=1&game=" + game + "&name=" + bot.trim() + "&dummy=NA";

		OutputStream out = null;
		OutputStreamWriter outputStreamWriter = null;
		PrintWriter wr = null;
		InputStream in = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try {
			// Send data
			URLConnection conn = FileTransfer.openOutputURLConnection(new URL(removeboturl));

			out = FileTransfer.getOutputStream(conn);
			outputStreamWriter = new OutputStreamWriter(out);
			wr = new PrintWriter(outputStreamWriter);

			wr.println(data);
			wr.flush();

			// Get the response
			in = FileTransfer.getInputStream(conn);
			inputStreamReader = new InputStreamReader(in); 
			bufferedReader = new BufferedReader(inputStreamReader);

			for (String line; (line = bufferedReader.readLine()) != null;) {
				System.out.println(line);
			}
		} catch (IOException e) {
			System.out.println(e);
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
					} catch (MalformedURLException e) {
						matches = false;
					}
				} else if (link.matches("[\\d]+")) {
					matches = true;
				}
			}
		}
		return matches;
	}
}
