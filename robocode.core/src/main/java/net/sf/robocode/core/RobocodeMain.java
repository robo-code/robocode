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
package net.sf.robocode.core;


import net.sf.robocode.battle.BattleResultsTableModel;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.recording.BattleRecordFormat;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.sound.ISoundManager;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.version.IVersionManager;
import robocode.control.events.*;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


/**
 * Robocode - A programming game involving battling AI tanks.<br>
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 */
public final class RobocodeMain extends RobocodeMainBase {

	private final Setup setup;
	private final BattleObserver battleObserver = new BattleObserver();
	final private ISettingsManager properties;
	final private IHostManager hostManager;
	final private IWindowManager windowManager;
	final private ISoundManager soundManager;
	final private IBattleManager battleManager;
	final private IRecordManager recordManager;
	final private IVersionManager versionManager;

	private class Setup {
		boolean minimize;
		boolean exitOnComplete;
		String battleFilename;
		String recordFilename;
		String recordXmlFilename;
		String replayFilename;
		String resultsFilename;
		// TODO: The other string used to load extensions (extensionFilename) is not 
		// implemented as command line arguments becuase the process of loading extensions 
		// will likely change.
		String extensionPackagename;
		int tps;
	}

	public RobocodeMain(ISettingsManager properties,
			IHostManager hostManager,
			IWindowManager windowManager,
			ISoundManager soundManager,
			IBattleManager battleManager,
			IRecordManager recordManager,
			IVersionManager versionManager
			) {
		setup = new Setup();
		this.properties = properties;
		this.hostManager = hostManager;
		this.windowManager = windowManager;
		this.soundManager = soundManager;
		this.battleManager = battleManager;
		this.recordManager = recordManager;
		this.versionManager = versionManager;
	}

	public RobocodeMain(ISettingsManager properties,
			IHostManager hostManager,
			IBattleManager battleManager,
			IRecordManager recordManager,
			IVersionManager versionManager
			) {
		setup = new Setup();
		this.properties = properties;
		this.hostManager = hostManager;
		this.windowManager = null;
		this.soundManager = null;
		this.battleManager = battleManager;
		this.recordManager = recordManager;
		this.versionManager = versionManager;
	}

	public void run() {
		try {
			hostManager.initSecurity();

			// Set the Look and Feel (LAF)
			if (windowManager.isGUIEnabled()) {
				windowManager.setLookAndFeel();
			}
			properties.setOptionsBattleDesiredTPS(setup.tps);

			battleManager.addListener(battleObserver);
			battleManager.getBattleProperties().setExtensionPackage(setup.extensionPackagename);

			if (windowManager.isGUIEnabled()) {
				if (!setup.minimize && setup.battleFilename == null && soundManager != null) {
					soundManager.playThemeMusic();
					windowManager.showSplashScreen();
				}
				windowManager.showRobocodeFrame(true, setup.minimize);

				// Play the intro battle if a battle file is not specified and this is the first time Robocode is being run

				if (setup.battleFilename == null && versionManager.isLastRunVersionChanged()) {
					properties.saveProperties();
					windowManager.runIntroBattle();
				}
			}

			final boolean enableCLIRecording = (setup.recordFilename != null || setup.recordXmlFilename != null);
			
			// Note: At this point the GUI should be opened (if enabled) before starting the battle from a battle file
			if (setup.battleFilename != null) {
				if (setup.replayFilename != null) {
					System.err.println("You cannot run both a battle and replay a battle record in the same time.");
					System.exit(8);
				}

				setup.exitOnComplete = true;

				battleManager.setBattleFilename(setup.battleFilename);
				if (new File(setup.battleFilename).exists()) {
					battleManager.startNewBattle(battleManager.loadBattleProperties(), false, enableCLIRecording);
				} else {
					System.err.println("The specified battle file '" + setup.battleFilename + "' was not be found");
					System.exit(8);
				}
			} else if (setup.replayFilename != null) {
				setup.exitOnComplete = true;

				recordManager.loadRecord(setup.replayFilename, BattleRecordFormat.BINARY_ZIP);

				if (new File(setup.replayFilename).exists()) {
					battleManager.replay();
				} else {
					System.err.println(
							"The specified battle record file '" + setup.replayFilename + "' was not be found");
					System.exit(8);
				}
			}
		} catch (Throwable e) {
			Logger.logError(e);
		}
	}

	public void loadSetup(String[] args) {

		final String nosecMessage = "Robocode is running without a security manager.\n"
				+ "Robots have full access to your system.\n" + "You should only run robots which you trust!";
		final String exMessage = "Robocode is running in experimental mode.\n"
				+ "Robots have access to their IRobotPeer interfaces.\n" + "You should only run robots which you trust!";

		if (System.getProperty("NOSECURITY", "false").equals("true")) {
			Logger.logMessage(nosecMessage);
		}
		if (System.getProperty("EXPERIMENTAL", "false").equals("true")) {
			Logger.logMessage(exMessage);
		}
		if (windowManager != null) {
			if (System.getProperty("NOSECURITY", "false").equals("true")) {
				windowManager.messageWarning(nosecMessage);
			}
			if (System.getProperty("EXPERIMENTAL", "false").equals("true")) {
				windowManager.messageWarning(exMessage);
			}
		}

		setup.tps = properties.getOptionsBattleDesiredTPS();

		// Initialize the system property so the AWT does not use headless mode meaning that the
		// GUI (Awt and Swing) is enabled per default when running starting Robocode.
		// It might be set to true later, if the -nodisplay option is set (in the setEnableGUI method).
		// Read more about headless mode here:
		// http://java.sun.com/developer/technicalArticles/J2SE/Desktop/headless/
		System.setProperty("java.awt.headless", "false");

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-cwd") && (i < args.length + 1)) {
				changeDirectory(args[i + 1]);
				i++;
			} else if (args[i].equals("-battle") && (i < args.length + 1)) {
				setup.battleFilename = args[i + 1];
				i++;
			} else if (args[i].equals("-extension") && (i < args.length + 1)) {
				setup.extensionPackagename = args[i + 1];
				i++;
			} else if (args[i].equals("-record") && (i < args.length + 1)) {
				setup.recordFilename = args[i + 1];
				i++;
			} else if (args[i].equals("-recordXML") && (i < args.length + 1)) {
				setup.recordXmlFilename = args[i + 1];
				i++;
			} else if (args[i].equals("-replay") && (i < args.length + 1)) {
				setup.replayFilename = args[i + 1];
				i++;
			} else if (args[i].equals("-results") && (i < args.length + 1)) {
				setup.resultsFilename = args[i + 1];
				i++;
			} else if (args[i].equals("-tps") && (i < args.length + 1)) {
				setup.tps = Integer.parseInt(args[i + 1]);
				if (setup.tps < 1) {
					Logger.logError("tps must be > 0");
					System.exit(8);
				}
				i++;
			} else if (args[i].equals("-minimize")) {
				setup.minimize = true;
			} else if (args[i].equals("-nodisplay")) {
				if (windowManager != null) {
					windowManager.setEnableGUI(false);
				}
				if (soundManager != null) {
					soundManager.setEnableSound(false);
				}
				setup.tps = 10000; // set TPS to maximum
			} else if (args[i].equals("-nosound")) {
				if (soundManager != null) {
					soundManager.setEnableSound(false);
				}
			} else if (args[i].equals("-?") || args[i].equals("-help")) {
				printUsage();
				System.exit(0);
			} else {
				Logger.logError("Not understood: " + args[i]);
				printUsage();
				System.exit(8);
			}
		}
		File robotsDir = FileUtil.getRobotsDir();

		if (robotsDir == null) {
			System.err.println("No valid robot directory is specified");
			System.exit(8);
		} else if (!(robotsDir.exists() && robotsDir.isDirectory())) {
			System.err.println('\'' + robotsDir.getAbsolutePath() + "' is not a valid robot directory");
			System.exit(8);
		}

		// The Default Toolkit must be set as soon as we know if we are going to use headless mode or not.
		// That is if the toolkit must be headless or not (GUI on/off). If we are running in headless mode
		// from this point, a HeadlessException will be thrown if we access a AWT/Swing component.
		// Read more about headless mode here:
		// http://java.sun.com/developer/technicalArticles/J2SE/Desktop/headless/
		Toolkit.getDefaultToolkit();
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
				"Usage: robocode [-?] [-help] [-cwd path] [-battle filename [-results filename]\n"
						+ "                [-record filename] [-recordXML filename] [-replay filename]\n"
						+ "                [-tps tps] [-minimize] [-nodisplay] [-nosound]\n\n" + "where options include:\n"
						+ "  -? or -help                Prints out the command line usage of Robocode\n"
						+ "  -cwd <path>                Change the current working directory\n"
						+ "  -battle <battle file>      Run the battle specified in a battle file\n"
						+ "  -results <results file>    Save results to the specified text file\n"
						+ "  -record <bin record file>  Record the battle into the specified file as binary\n"
						+ "  -recordXML <xml rec file>  Record the battle into the specified file as XML\n"
						+ "  -replay <record file>      Replay the specified battle record\n"
						+ "  -tps <tps>                 Set the TPS > 0 (Turns Per Second)\n"
						+ "  -extension <extension>     Sets the pacakge of the custom rules to be used\n"
						+ "  -minimize                  Run minimized when Robocode starts\n"
						+ "  -nodisplay                 Run with the display / GUI disabled\n"
						+ "  -nosound                   Run with sound disabled\n\n" + "Java Properties include:\n"
						+ "  -DWORKINGDIRECTORY=<path>  Set the working directory\n"
						+ "  -DROBOTPATH=<path>         Set the robots directory (default is 'robots')\n"
						+ "  -DBATTLEPATH=<path>        Set the battles directory (default is 'battles')\n"
						+ "  -DNOSECURITY=true|false    Enable/disable Robocode's security manager\n"
						+ "  -Ddebug=true|false         Enable/disable debugging (to prevent timeouts)\n"
						+ "  -DEXPERIMENTAL=true|false  Enable/disable access to peer in robot interfaces\n"
						+ "  -DPARALLEL=true|false      Enable/disable parallel processing of robots turns\n"
						+ "  -DRANDOMSEED=<long number> Set seed for deterministic behavior of Random numbers\n");
	}

	private void printResultsData(BattleCompletedEvent event) {
		// Do not print out if no result file has been specified and the GUI is enabled
		if ((setup.resultsFilename == null && (!setup.exitOnComplete || windowManager.isGUIEnabled()))) {
			return;
		}

		PrintStream out = null;
		FileOutputStream fos = null;

		if (setup.resultsFilename == null) {
			out = Logger.realOut;
		} else {
			File f = new File(setup.resultsFilename);

			try {
				fos = new FileOutputStream(f);
				out = new PrintStream(fos);
			} catch (IOException e) {
				Logger.logError(e);
			}
		}

		BattleResultsTableModel resultsTable = new BattleResultsTableModel(event.getSortedResults(),
				event.getBattleRules().getNumRounds());

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
			isReplay = event.isReplay();
		}

		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			if (!isReplay) {
				printResultsData(event);
			}
			if (setup.recordFilename != null) {
				recordManager.saveRecord(setup.recordFilename, BattleRecordFormat.BINARY_ZIP);
			}
			if (setup.recordXmlFilename != null) {
				recordManager.saveRecord(setup.recordXmlFilename, BattleRecordFormat.XML);
			}
		}

		@Override
		public void onBattleMessage(BattleMessageEvent event) {
			Logger.realOut.println(event.getMessage());
		}

		@Override
		public void onBattleError(BattleErrorEvent event) {
			Logger.realErr.println(event.getError());
		}
	}

	public void cleanup() {
		final IWindowManager windowManager = Container.getComponent(IWindowManager.class);

		if (windowManager != null) {
			windowManager.cleanup();
		}
		Container.getComponent(IBattleManager.class).cleanup();
		Container.getComponent(IHostManager.class).cleanup();
	}

	public void initForRobocodeEngine(IBattleListener listener) {
		final IWindowManager windowManager = Container.getComponent(IWindowManager.class);

		if (windowManager != null) {
			windowManager.setSlave(true);
			windowManager.setEnableGUI(false);
		}
		Container.getComponent(IHostManager.class).initSecurity();
		if (listener != null) {
			Container.getComponent(IBattleManager.class).addListener(listener);
		}
		Container.getComponent(ICpuManager.class).getCpuConstant();
		Container.getComponent(IRepositoryManager.class).reload(versionManager.isLastRunVersionChanged());
	}
}
