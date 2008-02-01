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
 *     - Setting the results file is now independent of setting the battle file
 *     - Robocode now returns with an error message if a specified battle file
 *       could not be found
 *     - Extended the usage / syntax for using Robocode from a console
 *******************************************************************************/
package robocode;


import java.io.File;
import java.io.IOException;
import java.security.Policy;

import robocode.io.FileUtil;
import robocode.io.Logger;
import robocode.manager.RobocodeManager;
import robocode.manager.BattleManager;
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

    private class Setup
    {
        boolean minimize = false;
        String battleFilename = null;
        String resultsFilename = null;
        int tps = 0;
    }

	private RobocodeManager manager;
    private Setup setup;

    /**
	 * Use the command-line to start Robocode.
	 * The command is:
	 * <pre>
	 *    java -Xmx512M -Dsun.io.useCanonCaches=false -jar libs/robocode.jar
	 * </pre>
	 *
	 * @param args an array of command-line arguments
	 */
	public static void main(String[] args) throws Throwable {
		try {
			Robocode robocode = new Robocode();
			robocode.initialize(args);
            boolean runInThisThread=true;
            if (robocode.manager.isGUIEnabled()) {
                runInThisThread=robocode.manager.getWindowManager().initializeDisplay(robocode);
            }
            if (runInThisThread)
            {
                run(robocode);
            }
        } catch (Throwable e) {
			Logger.log(e);
		}
	}

	private Robocode() {}

    private void initialize(String args[]) throws Throwable {
        manager = new RobocodeManager(false, null);
        setup = new Setup();

		if (System.getProperty("WORKINGDIRECTORY") != null) {
			FileUtil.setCwd(new File(System.getProperty("WORKINGDIRECTORY")));
		}

        LoadSetup(args, setup);
        InitStreams();
        InitSecurity();
    }

    /**
     * this method is used externaly when is executed by working thread when UI is running in application thread.
     * For example .NET UI
     * @param robocode
     * @return
     */
    public static void run(Robocode robocode) {
        RobocodeManager manager = robocode.manager;
        Setup setup = robocode.setup;

        if (setup.battleFilename != null) {
            robocode.manager.BattleManager battleManager = manager.getBattleManager();

            battleManager.setBattleFilename(setup.battleFilename);
            battleManager.loadBattleProperties();
            battleManager.startNewBattle(battleManager.getBattleProperties(), true, false);
            battleManager.getBattle().setDesiredTPS(setup.tps);
        }
        if (setup.resultsFilename != null) {
            manager.getBattleManager().setResultsFile(setup.resultsFilename);
        }
        if (!manager.isGUIEnabled()) {
            return;
        }

        if (!setup.minimize && setup.battleFilename == null) {
            if (manager.isSoundEnabled()) {
                manager.getSoundManager().playThemeMusic();
            }
            manager.getWindowManager().showSplashScreen();
        }
        manager.getWindowManager().showRobocodeFrame(true);
        if (!setup.minimize) {
            manager.getVersionManager().checkUpdateCheck();
        }
        if (setup.minimize) {
            manager.getWindowManager().getRobocodeFrame().setIconified(true);
        }

        if (!manager.getProperties().getLastRunVersion().equals(manager.getVersionManager().getVersion())) {
            manager.getProperties().setLastRunVersion(manager.getVersionManager().getVersion());
            manager.saveProperties();
            manager.runIntroBattle();
        }

        return;
    }

    private void LoadSetup(String[] args, Setup setup) throws IOException {
        setup.tps = manager.getProperties().getOptionsBattleDesiredTPS();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-cwd") && (i < args.length + 1)) {
                FileUtil.setCwd(new File(args[i + 1]));
                i++;
            } else if (args[i].equals("-battle") && (i < args.length + 1)) {
                setup.battleFilename = args[i + 1];
                i++;
            } else if (args[i].equals("-results") && (i < args.length + 1)) {
                setup.resultsFilename = args[i + 1];
                i++;
            } else if (args[i].equals("-tps") && (i < args.length + 1)) {
                setup.tps = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-minimize")) {
                setup.minimize = true;
            } else if (args[i].equals("-nodisplay")) {
                manager.setEnableGUI(false);
                manager.setEnableSound(false);
                setup.tps = 10000;
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
        if (setup.battleFilename != null) {
            if (!(new File(BattleManager.getBattleFilename(setup.battleFilename)).exists())) {
                System.err.println("The specified battle file '" + setup.battleFilename + "' was not be found");
                System.exit(8);
            }
        }
    }

    private void InitStreams() {
        SecurePrintStream sysout = new SecurePrintStream(System.out, true, "System.out");
        SecurePrintStream syserr = new SecurePrintStream(System.err, true, "System.err");
        SecureInputStream sysin = new SecureInputStream(System.in, "System.in");

        System.setOut(sysout);
        if (!System.getProperty("debug", "false").equals("true")) {
            System.setErr(syserr);
        }
        System.setIn(sysin);
    }

    private void InitSecurity() {
        Thread.currentThread().setName("Application Thread");

        RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());

        Policy.setPolicy(securityPolicy);

        // For John Burkey at Apple
        boolean securityOn = true;

        if (System.getProperty("NOSECURITY", "false").equals("true")) {
            manager.getWindowManager().getRobocodeFrame().messageWarning(
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
    }

    private void printUsage() {
		System.out.print(
				"Usage: robocode [-cwd path] [-battle filename [-results filename] [-tps tps]\n"
						+ "                [-minimize] [-nodisplay] [-nosound]]\n" + "\n" + "where options include:\n"
						+ "    -cwd <path>             Change the current working directory\n"
						+ "    -battle <battle file>   Run the battle specified in a battle file\n"
						+ "    -results <file>         Save results to the specified text file\n"
						+ "    -tps <tps>              Set the TPS (Turns Per Second) to use\n"
						+ "    -minimize               Run minimized when Robocode starts\n"
						+ "    -nodisplay              Run with the display / GUI disabled\n"
						+ "    -nosound                Run with sound disabled\n" + "\n" + "properties include:\n"
						+ "    -DWORKINGDIRECTORY=<path>  Set the working directory\n"
						+ "    -DROBOTPATH=<path>         Set the robots directory (default is 'robots')\n"
						+ "    -DBATTLEPATH=<path>        Set the battles directory (default is 'battles')\n"
						+ "    -DNOSECURITY=true|false    Enable or disable Robocode's security manager\n"
						+ "    -Ddebug=true|false         Enable or disable System.err messages\n" + "\n");
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
