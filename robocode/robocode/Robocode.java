/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Removed check for the system property "SINGLEBUFFER", as it is not used
 *       anymore
 *     - Replaced the noDisplay with manager.setEnableGUI() and isGUIEnabled()
 *     - Replaced the -fps option with the -tps option
 *     - Added -nosound option and disables sound i the -nogui is specified
 *     - Code cleanup
 *******************************************************************************/
package robocode;


import javax.swing.*;
import java.io.*;
import java.security.*;

import robocode.util.*;
import robocode.manager.*;
import robocode.security.*;


/**
 * Robocode - A programming game involving battling AI tanks.<br>
 * Copyright (c)  2001, 2006 Mathew Nelson and Robocode contributors
 *
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class Robocode {

	private RobocodeManager manager;

	/**
	 * Use the command-line to start Robocode.
	 * The command is:  java -jar robocode.jar
	 * @param args an array of command-line arguments
	 */
	public static void main(String[] args) {
		Robocode robocode = new Robocode();
	
		robocode.initialize(args);
	}

	private Robocode() {}

	private boolean initialize(String args[]) {
		try {
			manager = new RobocodeManager(false, null);
		
			// Set native look and feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			if (System.getProperty("WORKINGDIRECTORY") != null) {
				Constants.setWorkingDirectory(new File(System.getProperty("WORKINGDIRECTORY")));
			}
		
			Thread.currentThread().setName("Application Thread");
		
			RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());

			Policy.setPolicy(securityPolicy);
		
			// For John Burkey at Apple
			boolean securityOn = true;

			if (System.getProperty("NOSECURITY", "false").equals("true")) {
				Utils.messageWarning(
						"Robocode is running without a security manager.\n" + "Robots have full access to your system.\n"
						+ "You should only run robots which you trust!");
				securityOn = false;
			}
			if (securityOn) {
				System.setSecurityManager(
						new RobocodeSecurityManager(Thread.currentThread(), manager.getThreadManager(), true));

				RobocodeFileOutputStream.setThreadManager(manager.getThreadManager());

				ThreadGroup tg = Thread.currentThread().getThreadGroup();

				while (tg != null) {
					((RobocodeSecurityManager) System.getSecurityManager()).addSafeThreadGroup(tg);
					tg = tg.getParent();
				}
			}

			SecurePrintStream sysout = new SecurePrintStream(System.out, true, "System.out");
			SecurePrintStream syserr = new SecurePrintStream(System.err, true, "System.err");
			SecureInputStream sysin = new SecureInputStream(System.in, "System.in");

			System.setOut(sysout);
			if (!System.getProperty("debug", "false").equals("true")) {
				System.setErr(syserr);
			}
			System.setIn(sysin);
			
			boolean minimize = false;
			String battleFilename = null;
			String resultsFilename = null;
			int tps = 10000;

			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-cwd") && (i < args.length + 1)) {
					Constants.setWorkingDirectory(new File(args[i + 1]));
					i++;
				} else if (args[i].equals("-battle") && (i < args.length + 1)) {
					battleFilename = args[i + 1];
					i++;
				} else if (args[i].equals("-results") && (i < args.length + 1)) {
					resultsFilename = args[i + 1];
					i++;
				} else if (args[i].equals("-tps") && (i < args.length + 1)) {
					tps = Integer.parseInt(args[i + 1]);
					i++;
				} else if (args[i].equals("-minimize")) {
					minimize = true;
				} else if (args[i].equals("-nodisplay")) {
					manager.setEnableGUI(false);
					manager.setEnableSound(false);
				} else if (args[i].equals("-nosound")) {
					manager.setEnableSound(false);
				} else if (args[i].equals("-?") || args[i].equals("-help")) {
					printUsage();
					System.exit(0);
				} else {
					System.out.println("Not understood: " + args[i]);
					printUsage();
					System.exit(8);
				}
			}
			File robots = new File(Constants.cwd(), "robots");

			if (!robots.exists() || !robots.isDirectory()) {
				System.err.println(
						new File(Constants.cwd(), "").getAbsolutePath() + " is not a valid directory to start Robocode in.");
				System.exit(8);
			}

			if (battleFilename != null) {
				if (resultsFilename != null) {
					manager.getBattleManager().setResultsFile(resultsFilename);
				}
				manager.getBattleManager().setBattleFilename(battleFilename);
				manager.getBattleManager().loadBattleProperties();
				manager.getBattleManager().startNewBattle(manager.getBattleManager().getBattleProperties(), true, false);
				manager.getBattleManager().getBattle().setDesiredTPS(tps);
			}
			if (!manager.isGUIEnabled()) {
				return true;
			}

			if (!minimize && battleFilename == null) {
				manager.getWindowManager().showSplashScreen();
			}
			manager.getWindowManager().showRobocodeFrame();
			if (!minimize) {
				manager.getVersionManager().checkUpdateCheck();
			}
			if (minimize) {
				manager.getWindowManager().getRobocodeFrame().setState(JFrame.ICONIFIED);
			}

			if (!manager.getProperties().getLastRunVersion().equals(manager.getVersionManager().getVersion())) {
				manager.getProperties().setLastRunVersion(manager.getVersionManager().getVersion());
				manager.saveProperties();
				manager.runIntroBattle();
			}
			
			return true;
		} catch (Throwable e) {
			Utils.log(e);
			return false;
		}
	}

	private void printUsage() {
		System.out.println(
				"Usage: robocode [-cwd directory] [-battle filename [-results filename] [-tps tps] [-minimize]]");
	}
}
