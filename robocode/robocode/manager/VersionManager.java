/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.manager;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

import robocode.util.*;

public class VersionManager {
	private String version = null;
	private RobocodeManager manager = null;
	
	public VersionManager(RobocodeManager manager)
	{
		this.manager = manager;
	}

	private void log(String s) {
		Utils.log(s);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}

	public void checkUpdateCheck()
	{
		Date lastCheckedDate = manager.getProperties().getVersionChecked();
		if (lastCheckedDate == null)
		{
			lastCheckedDate = new Date();
			manager.getProperties().setVersionChecked(lastCheckedDate);
			manager.saveProperties();
		}
		Calendar checkDate = Calendar.getInstance();
		checkDate.setTime(lastCheckedDate);
		checkDate.add(Calendar.DATE,10);
		
		if (checkDate.getTime().before(new Date()))
		{
			if (checkForNewVersion(false))
			{
				manager.getProperties().setVersionChecked(new Date());
				manager.saveProperties();
			}
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 2:54:57 PM)
	 */
	public boolean checkForNewVersion(boolean notifyNoUpdate) {
		//log("Checking for new version...");
		URL u = null;
		try {
			u = new URL("http://robocode.sourceforge.net/version/version.html");
		} catch (java.net.MalformedURLException e) {
			log("Unable to check for new version: " + e);
			if (notifyNoUpdate)
				Utils.messageError("Unable to check for new version: " + e);
			return false;
		}
	
		BufferedReader reader;
		try {
			URLConnection urlConnection = u.openConnection();
			if (urlConnection instanceof HttpURLConnection)
			{
				log("Update checking with http.");
				HttpURLConnection h = (HttpURLConnection)urlConnection;
				if (h.usingProxy())
					log("http using proxy.");
			}
			
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		} catch (IOException e) {
			log("Unable to check for new version: " + e);
			if (notifyNoUpdate)
				Utils.messageError("Unable to check for new version: " + e);
			return false;
		}
	
		String v = null;
		try {
			v = reader.readLine();
		} catch (IOException e) {
			log("Unable to check for new version: " + e);
			if (notifyNoUpdate)
				Utils.messageError("Unable to check for new version: " + e);
			return false;
		}
	
		String installurl = "http://robocode.sourceforge.net/installer";
		String helpurl = "http://robocode.sourceforge.net/version/checkversion.html?version=" + version;
		if (!v.equals(getVersion()))
		{
			if (JOptionPane.showConfirmDialog(manager.getWindowManager().getRobocodeFrame(),
							"Version " + v + " of Robocode is now available.  Would you like to download it?",
							"Version " + v + " available",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				try {
					manager.getBrowserManager().openURL(installurl);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(manager.getWindowManager().getRobocodeFrame(),
						e.getMessage(),
						"Unable to open browser!",
						JOptionPane.INFORMATION_MESSAGE);
				}
	//			BrowserControl.displayURL(installurl);
	//			BrowserControl.displayURL(helpurl);
			}
			else
			{
				JOptionPane.showMessageDialog(manager.getWindowManager().getRobocodeFrame(),
					"It is highly recommended that you always download the latest version.  You may get it at " + installurl,
					"Update when you can!",
					JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (notifyNoUpdate)
		{
			JOptionPane.showMessageDialog(manager.getWindowManager().getRobocodeFrame(),
				"You have version " + version + ".  This is the latest version of Robocode.",
				"No update available",
				JOptionPane.INFORMATION_MESSAGE);
		}
	
		return true;
		
	//	String helpurl = "http://robocode.sourceforge.net/version/checkversion.html?version=" + version;
			
	//	BrowserControl.displayURL(helpurl);
	
		/*
		java.net.URL u;
		try {
			u = new java.net.URL(helpurl);
		} catch (java.net.MalformedURLException e) {
			log("Could not form a URL from " + helpurl);
			return;
		}
	
		RobocodeHtmlDialog hd = new RobocodeHtmlDialog();
		Dimension dialogSize = hd.getPreferredSize();
		Dimension frameSize = robocodeFrame.getSize();
		Point loc = robocodeFrame.getLocation();
		hd.setTitle("Check Version");
		hd.setURL(u);
		hd.show();
		*/
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:43:41 PM)
	 */
	public String getVersion() {
		if (this.version == null)
		{
//			this.version = getVersionFromFile();
			this.version = getVersionFromJar();
		}
		return this.version;
	}
	
	private String getVersionFromJar() {
		String versionString = null;
		try {
			URL versionsUrl = getClass().getResource("/resources/versions.txt");
			if (versionsUrl == null)
				log("no url");
			else
			{
//				log(versionsUrl.toString());
//				log(versionsUrl.getClass().toString());
			}
				
			
			BufferedReader in = new BufferedReader(new InputStreamReader(versionsUrl.openStream()));
			//FileReader(new File(Constants.cwd(),"versions.txt")));
			versionString = in.readLine();
			while (versionString != null && !versionString.substring(0,8).equalsIgnoreCase("Version "))
				versionString = in.readLine();
			in.close();
		} catch (FileNotFoundException e) {
			log("No versions.txt file in robocode.jar.");
			versionString = "unknown";
	
		} catch (IOException e) {
			log("IO Exception reading versions.txt from robocode.jar" + e);
			versionString = "unknown";
		}
	
		String version = "unknown";
		if (versionString != null)
		{
			try {
				version = versionString.substring(8);
			} catch (Exception e) {
				version = "unknown";
			}
		}
		if (version.equals("unknown"))
		{
			log("Warning:  Getting version from file.");
			return getVersionFromFile();
		}
		return version;
	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:43:41 PM)
	 */
	private String getVersionFromFile() {
		String versionString = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(Constants.cwd(),"versions.txt")));
			versionString = in.readLine();
			while (versionString != null && !versionString.substring(0,8).equalsIgnoreCase("Version "))
				versionString = in.readLine();
		} catch (FileNotFoundException e) {
			log("No versions.txt file.");
			versionString = "unknown";
	
		} catch (IOException e) {
			log("IO Exception reading versions.txt" + e);
			versionString = "unknown";
		}
	
		String version = "unknown";
		if (versionString != null)
		{
			try {
				version = versionString.substring(8);
			} catch (Exception e) {
				version = "unknown";
			}
		}
		return version;
	
	}
	
	
}

