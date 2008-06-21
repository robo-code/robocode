/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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


import robocode.dialog.WindowUtil;
import robocode.io.FileUtil;
import robocode.io.Logger;
import robocode.manager.RobocodeManager;
import robocode.manager.BattleManager;
import robocode.battle.events.*;
import robocode.ui.BattleResultsTableModel;
import robocode.battle.Battle;
import robocode.security.SecurePrintStream;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;


/**
 * Robocode - A programming game involving battling AI tanks.<br>
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 */
public class Robocode {

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
        robocode.loadSetup(args);
        robocode.run();
    }

    private RobocodeManager manager;
    private Setup setup;
    private BattleObserver battleObserver = new BattleObserver();

    private class Setup {
        boolean securityOn = true;
        boolean experimentalOn = false;
        boolean minimize = false;
        boolean exitOnComplete = false;
        String battleFilename = null;
        String resultsFilename = null;
        int tps;
    }

    private Robocode() {
        manager = new RobocodeManager(false);
        setup = new Setup();
    }

    private boolean run() {
        try {
            manager.initSecurity(setup.securityOn, setup.experimentalOn);

            // Set the Look and Feel (LAF)
            if (manager.isGUIEnabled()) {
                robocode.manager.LookAndFeelManager.setLookAndFeel();
            }

            manager.getProperties().setOptionsBattleDesiredTPS(setup.tps);

            manager.getBattleManager().addListener(battleObserver);

            if (setup.battleFilename != null) {
                setup.exitOnComplete=true;
                robocode.manager.BattleManager battleManager = manager.getBattleManager();

                battleManager.setBattleFilename(setup.battleFilename);
                if (new File(battleManager.getBattleFilename()).exists()) {
                    battleManager.startNewBattle(battleManager.loadBattleProperties(), false);
                } else {
                    System.err.println("The specified battle file '" + setup.battleFilename + "' was not be found");
                    System.exit(8);
                }
            }
            if (manager.isGUIEnabled()) {
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
                    manager.getWindowManager().getRobocodeFrame().setState(Frame.ICONIFIED);
                }

                if (!manager.getProperties().getLastRunVersion().equals(manager.getVersionManager().getVersion())) {
                    manager.getProperties().setLastRunVersion(manager.getVersionManager().getVersion());
                    manager.saveProperties();
                    manager.getWindowManager().getRobocodeFrame().runIntroBattle();
                }
            }

            return true;
        } catch (Throwable e) {
            Logger.logError(e);
            return false;
        }
    }

    private void loadSetup(String args[]) {
        final String robocodeDir = System.getProperty("WORKINGDIRECTORY");
        if (robocodeDir != null) {
            changeDirectory(robocodeDir);
        }

        if (System.getProperty("NOSECURITY", "false").equals("true")) {
            WindowUtil.messageWarning(
                    "Robocode is running without a security manager.\n" + "Robots have full access to your system.\n"
                            + "You should only run robots which you trust!");
            setup.securityOn = false;
        }
        if (System.getProperty("EXPERIMENTAL", "false").equals("true")) {
            WindowUtil.messageWarning(
                    "Robocode is running in experimental mode.\n" + "Robots have access to their IRobotPeer interfaces.\n"
                            + "You should only run robots which you trust!");
            setup.experimentalOn = true;
        }

        setup.tps = manager.getProperties().getOptionsBattleDesiredTPS();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-cwd") && (i < args.length + 1)) {
                changeDirectory(args[i + 1]);
                i++;
            } else if (args[i].equals("-battle") && (i < args.length + 1)) {
                setup.battleFilename = args[i + 1];
                i++;
            } else if (args[i].equals("-results") && (i < args.length + 1)) {
                setup.resultsFilename = args[i + 1];
                i++;
            } else if (args[i].equals("-tps") && (i < args.length + 1)) {
                setup.tps = Integer.parseInt(args[i + 1]);
                if (setup.tps < 1) {
                    System.out.println("tps must be > 0");
                    System.exit(8);
                }
                i++;
            } else if (args[i].equals("-minimize")) {
                setup.minimize = true;
            } else if (args[i].equals("-nodisplay")) {
                manager.setEnableGUI(false);
                manager.setEnableSound(false);
                setup.tps = 10000; // set TPS to maximum
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
    }

    private void changeDirectory(String robocodeDir) {
        try {
            FileUtil.setCwd(new File(robocodeDir));
        } catch (IOException e) {
            System.err.println(robocodeDir + " is not a valid directory to start Robocode in.");
            System.exit(8);
        }
    }

    private void printUsage() {
        System.out.print(
                "Usage: robocode [-cwd path] [-battle filename [-results filename] [-tps tps]\n"
                        + "                [-minimize] [-nodisplay] [-nosound]]\n" + "\n" + "where options include:\n"
                        + "    -cwd <path>             Change the current working directory\n"
                        + "    -battle <battle file>   Run the battle specified in a battle file\n"
                        + "    -results <file>         Save results to the specified text file\n"
                        + "    -tps <tps>              Set the TPS (Turns Per Second) to use. TPS must be > 0\n"
                        + "    -minimize               Run minimized when Robocode starts\n"
                        + "    -nodisplay              Run with the display / GUI disabled\n"
                        + "    -nosound                Run with sound disabled\n" + "\n" + "properties include:\n"
                        + "    -DWORKINGDIRECTORY=<path>  Set the working directory\n"
                        + "    -DROBOTPATH=<path>         Set the robots directory (default is 'robots')\n"
                        + "    -DBATTLEPATH=<path>        Set the battles directory (default is 'battles')\n"
                        + "    -DNOSECURITY=true|false    Enable or disable Robocode's security manager\n"
                        + "    -Ddebug=true|false         Enable or disable System.err messages\n"
                        + "    -DEXPERIMENTAL=true|false  Enable or disable access to peer in robot interfaces\n" + "\n"
                        + "    -DPARALLEL=true|false      Enable or disable parallel processing of robots turns\n" + "\n"
                        + "    -DRANDOMSEED=<long-number> Set seed for deterministic behavior of Random number generator\n" + "\n");
    }

    private void printResultsData(BattleCompletedEvent event) {
        // Do not print out if no result file has been specified and the GUI is enabled
        if ((setup.resultsFilename == null && (!setup.exitOnComplete || manager.isGUIEnabled()))) {
            return;
        }

        PrintStream out = null;
        FileOutputStream fos = null;

        if (setup.resultsFilename == null) {
            out = SecurePrintStream.realOut;
        } else {
            File f = new File(setup.resultsFilename);

            try {
                fos = new FileOutputStream(f);
                out = new PrintStream(fos);
            } catch (IOException e) {
                Logger.logError(e);
            }
        }

        BattleResultsTableModel resultsTable = new BattleResultsTableModel(event.getResults(), event.getBattleProperties().getNumRounds());

        if (out != null) {
            resultsTable.print(out);
            out.close();
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {// swallow
            }
        }
    }

    private class BattleObserver extends BattleAdaptor {
        boolean isReplay;

        @Override
        public void onBattleStarted(BattleStartedEvent event) {
            isReplay=event.isReplay();
        }

        @Override
        public void onBattleCompleted(BattleCompletedEvent event) {
            if (!isReplay){            
                printResultsData(event);
            }
        }

        @Override
        public void onBattleMessage(BattleMessageEvent event) {
            SecurePrintStream.realOut.println(event.getMessage());
        }

        @Override
        public void onBattleError(BattleErrorEvent event) {
            SecurePrintStream.realErr.println(event.getError());
        }
    }
    
}
