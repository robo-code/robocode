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
 *     - Code cleanup
 *     - Removed check for the system property "SINGLEBUFFER", as it is not used
 *       anymore
 *     - Replaced the noDisplay with manager.setEnableGUI() and isGUIEnabled()
 *     - Replaced the -fps option with the -tps option
 *     - Added -nosound option and disables sound i the -nogui is specified
 *     - Updated to use methods from WindowUtil, FileUtil, Logger, which replaces
 *       methods that has been (re)moved from the robocode.util.Utils class
 *     - Moved the printRunningThreads() from robocode.util.Utils into this class
 *       and added javadoc for it
 *     - Added playing theme music at the startup, if music is provided
 *     - Changed to use FileUtil.getRobotsDir()
 *******************************************************************************/
package robocode;


import java.awt.Frame;
import java.io.File;
import java.security.Policy;

import robocode.dialog.WindowUtil;
import robocode.io.FileUtil;
import robocode.io.Logger;
import robocode.manager.RobocodeManager;
import robocode.security.RobocodeSecurityManager;
import robocode.security.RobocodeSecurityPolicy;
import robocode.security.SecureInputStream;
import robocode.security.SecurePrintStream;


/**
 * Robocode - A programming game involving battling AI tanks.<br>
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 *
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Robocode {

	private RobocodeManager manager;

	/**
	 * Use the command-line to start Robocode.
	 * The command is:
	 * <pre>
	 *    java -Xmx512M -Dsun.io.useCanonCaches=false -jar libs/robocode.jar
	 * </pre>
	 *
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

			if (System.getProperty("WORKINGDIRECTORY") != null) {
				FileUtil.setCwd(new File(System.getProperty("WORKINGDIRECTORY")));
			}

			Thread.currentThread().setName("Application Thread");

			RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());

			Policy.setPolicy(securityPolicy);

			// For John Burkey at Apple
			boolean securityOn = true;

			if (System.getProperty("NOSECURITY", "false").equals("true")) {
				WindowUtil.messageWarning(
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
					FileUtil.setCwd(new File(args[i + 1]));
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
			File robots = FileUtil.getRobotsDir();

			if (!robots.exists() || !robots.isDirectory()) {
				System.err.println(
						new File(FileUtil.getCwd(), "").getAbsolutePath() + " is not a valid directory to start Robocode in.");
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

			// Set the Look and Feel (LAF)
			robocode.manager.LookAndFeelManager.setLookAndFeel();

			if (!minimize && battleFilename == null) {
				if (manager.isSoundEnabled()) {
					manager.getSoundManager().playThemeMusic();
				}
				manager.getWindowManager().showSplashScreen();
			}
			manager.getWindowManager().showRobocodeFrame(true);
			if (!minimize) {
				manager.getVersionManager().checkUpdateCheck();
			}
			if (minimize) {
				manager.getWindowManager().getRobocodeFrame().setState(Frame.ICONIFIED);
			}

			if (!manager.getProperties().getLastRunVersion().equals(manager.getVersionManager().getVersion())) {
				manager.getProperties().setLastRunVersion(manager.getVersionManager().getVersion());
				manager.saveProperties();
				manager.runIntroBattle();
			}

			return true;
		} catch (Throwable e) {
			Logger.log(e);
			return false;
		}
	}

	private void printUsage() {
		System.out.println(
				"Usage: robocode [-cwd directory] [-battle filename [-results filename] [-tps tps] [-minimize]]");
	}

	/**
	 * Prints out all running thread to the standard system out (console)
	 */
	public static void printRunningThreads() {
		ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
		if (currentGroup == null) {
			return;
		}

		while (currentGroup.getParent() != null) {
			currentGroup = currentGroup.getParent();
		}

		ThreadGroup groups[] = new ThreadGroup[256];
		Thread threads[] = new Thread[256];

		int numGroups = currentGroup.enumerate(groups, true);

		for (int i = 0; i < numGroups; i++) {
			currentGroup = groups[i];
			if (currentGroup.isDaemon()) {
				System.out.print("  ");
			} else {
				System.out.print("* ");
			}
			System.out.println("In group: " + currentGroup.getName());
			int numThreads = currentGroup.enumerate(threads);

			for (int j = 0; j < numThreads; j++) {
				if (threads[j].isDaemon()) {
					System.out.print("  ");
				} else {
					System.out.print("* ");
				}
				System.out.println(threads[j].getName());
			}
			System.out.println("---------------");
		}
	}
}
