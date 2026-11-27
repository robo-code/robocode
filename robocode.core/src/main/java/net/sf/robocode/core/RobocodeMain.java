/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;


import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.battle.BattleResultsTableModel;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import net.sf.robocode.recording.BattleRecordFormat;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.sound.ISoundManager;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.util.StringUtil;
import net.sf.robocode.version.IVersionManager;
import robocode.control.events.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;


/**
 * Robocode - A programming game involving battling AI tanks.<br>
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 *
 * @see <a target="_top" href="https://robocode.sourceforge.io">robocode.sourceforge.net</a>
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
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

	private static class Setup {
		boolean minimize;
		boolean exitOnComplete;
		String battleFilename;
		String recordFilename;
		String recordXmlFilename;
		String replayFilename;
		int replayStartRound;
		String resultsFilename;
		String csvResultsFilename;
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
			IWindowManager windowManager,
			IBattleManager battleManager,
			IRecordManager recordManager,
			IVersionManager versionManager
			) {
		this(properties, hostManager, windowManager, null, battleManager, recordManager, versionManager);
	}

	public RobocodeMain(ISettingsManager properties,
			IHostManager hostManager,
			IBattleManager battleManager,
			IRecordManager recordManager,
			IVersionManager versionManager
			) {
		this(properties, hostManager, null, battleManager, recordManager, versionManager);
	}

	public void run() {
		Thread.setDefaultUncaughtExceptionHandler((thread, t) -> t.printStackTrace());

		try {
			hostManager.initSecurity();

			// Set the Look and Feel (LAF)
			if (windowManager != null && windowManager.isGUIEnabled()) {
				windowManager.init();
			}
			properties.setOptionsBattleDesiredTPS(setup.tps);

			battleManager.addListener(battleObserver);

			if (windowManager != null && windowManager.isGUIEnabled()) {
				if (!setup.minimize && setup.battleFilename == null && soundManager != null) {
					soundManager.playThemeMusic();
					windowManager.showSplashScreen();
				}
				windowManager.showRobocodeFrame(true, setup.minimize);

				// Play the intro battle if a battle file is not specified and this is the first time Robocode is being run

				if (setup.battleFilename == null && setup.replayFilename == null && versionManager.isLastRunVersionChanged()) {
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
				if (new File(battleManager.getBattleFilename()).isFile()) {
					BattleProperties battleProperties;
					try {
						battleProperties = battleManager.loadBattleProperties();
					} catch (IllegalArgumentException e) {
						System.err.println("Invalid battle file '" + setup.battleFilename + "': " + e.getMessage());
						System.exit(8);
						return;
					}
					battleManager.startNewBattle(battleProperties, false, enableCLIRecording);
				} else {
					System.err.println("The specified battle file '" + setup.battleFilename + "' was not found");
					System.exit(8);
				}
			} else if (setup.replayFilename != null) {
				setup.exitOnComplete = true;
				if (!new File(setup.replayFilename).isFile()) {
					Logger.logError("The specified battle record file '" + setup.replayFilename + "' was not found");
					System.exit(8);
				}
				if (setup.replayFilename.toLowerCase().endsWith("xml.zip")) {
					recordManager.loadRecord(setup.replayFilename, BattleRecordFormat.XML_ZIP);
				} else {
					recordManager.loadRecord(setup.replayFilename, BattleRecordFormat.BINARY_ZIP);
				}

				if (setup.replayStartRound == 0) {
					battleManager.replay();
				} else {
					try {
						battleManager.replayFromRound(setup.replayStartRound);
					} catch (IllegalArgumentException e) {
						System.err.println(e.getMessage());
						System.exit(8);
					}
				}
			}
			Logger.initialized = true;
		} catch (Throwable e) {
			Logger.logError(e);
		}
	}

	public void loadSetup(String[] args) {
		setup.tps = properties.getOptionsBattleDesiredTPS();

		if (GraphicsEnvironment.isHeadless()) {
			if (windowManager != null) {
				windowManager.setEnableGUI(false);
				Logger.logWarning("Disabled GUI on headless system");
			}
			if (soundManager != null) {
				soundManager.setEnableSound(false);
			}
		}

		// Disable canonical file path cache under Windows as it causes trouble when returning
		// paths with differently-capitalized file names.
		String osName = System.getProperty("os.name").toLowerCase();
		boolean isWindows = osName.startsWith("windows ");
		if (isWindows) {
			System.setProperty("sun.io.useCanonCaches", "false");
		}

		// Initialize the system property so the AWT does not use headless mode meaning that the
		// GUI (Awt and Swing) is enabled per default when running starting Robocode.
		// It might be set to true later, if the -nodisplay option is set (in the setEnableGUI method).
		// Read more about headless mode here:
		// http://java.sun.com/developer/technicalArticles/J2SE/Desktop/headless/
		System.setProperty("java.awt.headless", "false");

		// Fix issue with rendering issues on Windows and Linux, by disabling Direct3D/DirectDraw and enabling
		// OpenGL acceleration per default
//		boolean isMac = osName.startsWith("mac ");
//		if (!isMac) {
//			System.setProperty("sun.java2d.d3d", "false");
//			System.setProperty("sun.java2d.noddraw", "true");
//			System.setProperty("sun.java2d.opengl", "True"); // `True` writes a message about OpenGL acceleration in console
//		}

		for (int i = 0; i < args.length; i++) {
			String currentArg = args[i];
			if (currentArg.equalsIgnoreCase("-cwd")) {
				changeDirectory(optionValue(args, i++));
			} else if (currentArg.equalsIgnoreCase("-battle")) {
				setup.battleFilename = optionValue(args, i++);
			} else if (currentArg.equalsIgnoreCase("-record")) {
				setup.recordFilename = optionValue(args, i++);
			} else if (currentArg.equalsIgnoreCase("-recordXML")) {
				setup.recordXmlFilename = optionValue(args, i++);
			} else if (currentArg.equalsIgnoreCase("-replay")) {
				setup.replayFilename = optionValue(args, i++);
			} else if (currentArg.equalsIgnoreCase("-results")) {
				setup.resultsFilename = optionValue(args, i++);
			} else if (currentArg.equalsIgnoreCase("-tps")) {
				String value = optionValue(args, i++);
				try {
					setup.tps = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					System.err.println("-tps expects a positive integer: " + value);
					System.exit(8);
				}
				if (setup.tps < 1) {
					Logger.logError("tps must be > 0");
					System.exit(8);
				}
			} else if (currentArg.equalsIgnoreCase("-minimize")) {
				setup.minimize = true;
			} else if (currentArg.equalsIgnoreCase("-replayRound")) {
				try {
					setup.replayStartRound = Integer.parseInt(++i < args.length ? args[i] : "");
				} catch (NumberFormatException e) {
					setup.replayStartRound = -1;
				}
				if (setup.replayStartRound < 1) {
					System.err.println("-replayRound requires a positive integer");
					System.exit(8);
				}
			} else if (currentArg.equalsIgnoreCase("-nodisplay")) {
				if (windowManager != null) {
					windowManager.setEnableGUI(false);
				}
				if (soundManager != null) {
					soundManager.setEnableSound(false);
				}
				setup.tps = 10000; // set TPS to maximum
			} else if (currentArg.equalsIgnoreCase("-nosound")) {
				if (soundManager != null) {
					soundManager.setEnableSound(false);
				}
			} else if (currentArg.equalsIgnoreCase("-resultsCSV")) {
				if (i + 1 >= args.length) {
					System.err.println("Missing filename for " + currentArg);
					System.exit(8);
				}
				setup.csvResultsFilename = args[++i];
			} else if (currentArg.equals("-?") || currentArg.equalsIgnoreCase("-help")) {
				printUsage();
				System.exit(0);
			} else {
				Logger.logError("Not understood: " + currentArg);
				printUsage();
				System.exit(8);
			}
		}
		if (setup.csvResultsFilename != null && setup.battleFilename == null) {
			System.err.println("-resultsCSV requires -battle");
			System.exit(8);
		}
		File robotsDir = FileUtil.getRobotsDir();
		if (robotsDir == null) {
			System.err.println("No valid robot directory is specified");
			System.exit(8);
		} else if (!(robotsDir.exists() && robotsDir.isDirectory())) {
			System.err.println('\'' + robotsDir.getAbsolutePath() + "' is not a valid robot directory");
			System.exit(8);
		}

		if (setup.replayStartRound != 0 && setup.replayFilename == null) {
			System.err.println("-replayRound requires -replay");
			System.exit(8);
		}

		// The Default Toolkit must be set as soon as we know if we are going to use headless mode or not.
		// That is if the toolkit must be headless or not (GUI on/off). If we are running in headless mode
		// from this point, a HeadlessException will be thrown if we access a AWT/Swing component.
		// Read more about headless mode here:
		// http://java.sun.com/developer/technicalArticles/J2SE/Desktop/headless/
		Toolkit.getDefaultToolkit();

		final String nosecMessage = "Robocode is running without a security manager.\n"
				+ "  Robots have full access to your system.\n" + "  You should only run robots which you trust!";
		final String exMessage = "Robocode is running in experimental mode.\n"
				+ "  Robots have access to their IRobotPeer interfaces.\n" + "  You should only run robots which you trust!";

		if (RobocodeProperties.isSecurityOff()) {
			System.out.println("Warning: " + nosecMessage); // beside the log
			Logger.logWarning(nosecMessage);
		}
		if (System.getProperty("EXPERIMENTAL", "false").equals("true")) {
			System.out.println("Warning: " + exMessage); // beside the log
			Logger.logWarning(exMessage);
		}
	}

	private static String optionValue(String[] args, int optionIndex) {
		if (optionIndex + 1 >= args.length) {
			System.err.println("Missing value for " + args[optionIndex]);
			System.exit(8);
		}
		return args[optionIndex + 1];
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
				"Usage: robocode [-?] [-help] [-cwd path] [-battle filename [-results filename] [-resultsCSV filename]]\n"
						+ "                [-record filename] [-recordXML filename] [-replay filename]\n"
						+ "                [-tps tps] [-minimize] [-nodisplay] [-nosound]\n"
						+ "\n"
						+ "where options include:\n"
						+ "  -? or -help                Prints out the command line usage of Robocode\n"
						+ "  -cwd <path>                Change the current working directory\n"
						+ "  -battle <battle file>      Run the battle specified in a battle file\n"
						+ "  -results <results file>    Save results to the specified text file\n"
						+ "  -resultsCSV <csv file>     Save machine-readable UTF-8 CSV results from -battle\n"
						+ "  -record <bin record file>  Record the battle into the specified file as binary\n"
						+ "  -recordXML <xml rec file>  Record the battle into the specified file as XML\n"
						+ "  -replay <record file>      Replay the specified battle record\n"
						+ "  -replayRound <round>      Start -replay at the specified recorded round (1-based)\n"
						+ "  -tps <tps>                 Set the TPS > 0 (Turns Per Second)\n"
						+ "  -minimize                  Run minimized when Robocode starts\n"
						+ "  -nodisplay                 Run with the display / GUI disabled\n"
						+ "  -nosound                   Run with sound disabled\n\n" + "Java Properties include:\n"
						+ "  -DWORKINGDIRECTORY=<path>  Set the working directory\n"
						+ "  -DROBOTPATH=<path>         Set the robots directory (default is 'robots')\n"
						+ "  -DBATTLEPATH=<path>        Set the battles directory (default is 'battles')\n"
						+ "  -DNOSECURITY=true|false    Enable/disable Robocode's security manager\n"
						+ "  -Ddebug=true|false         Enable/disable debugging used for preventing\n"
						+ "                             robot timeouts and skipped turns, and allows an\n"
						+ "                             an unlimited painting buffer when debugging robots\n"
						+ "  -DPAINTING=true|false      Enable/disable painting all robot painting on the\n"
						+ "                             screen per default.\n"
						+ "  -DlogMessages=true|false   Log messages and warnings will be disabled\n"
						+ "  -DlogErrors=true|false     Log errors will be disabled\n"
						+ "  -DEXPERIMENTAL=true|false  Enable/disable access to peer in robot interfaces\n"
						+ "  -DPARALLEL=true|false      Enable/disable parallel processing of robots turns\n"
						+ "  -DRANDOMSEED=<long number> Set seed for deterministic behavior of random\n"
						+ "                             numbers\n");
	}

	private void printResultsData(BattleCompletedEvent event) {
		// Do not print out if no result file has been specified and the GUI is enabled
		if (setup.resultsFilename == null && (!setup.exitOnComplete || (windowManager != null && windowManager.isGUIEnabled()))) {
			return;
		}

		PrintStream out = null;
		FileOutputStream fos = null;

		try {
			if (setup.resultsFilename == null) {
				out = Logger.realOut;
			} else {
				File f = new File(setup.resultsFilename);
	
				fos = new FileOutputStream(f);
				out = new PrintStream(fos);
			}
			if (out != null) {
				BattleResultsTableModel resultsTable = new BattleResultsTableModel(event.getSortedResults(),
						event.getBattleRules().getNumRounds());

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				resultsTable.print(new PrintStream(baos));
				out.append(StringUtil.toBasicLatin(baos.toString()));
				if (fos != null && out.checkError()) {
					throw new IOException("Unable to write the report");
				}
			}
		} catch (IOException e) {
			System.err.println("Could not save results to '" + setup.resultsFilename + "': " + e.getMessage());
			if (setup.exitOnComplete) {
				System.exit(8);
			}
		} finally {
			if (fos != null) {
				FileUtil.cleanupStream(out);
				FileUtil.cleanupStream(fos);
			}
		}
	}
	
	private void saveCsvResults(BattleCompletedEvent event) {
		try (Writer output = Files.newBufferedWriter(Paths.get(setup.csvResultsFilename), StandardCharsets.UTF_8)) {
			BattleResultsCsv.write(output, event.getSortedResults());
		} catch (IOException | InvalidPathException e) {
			System.err.println("Could not save CSV results to '" + setup.csvResultsFilename + "': " + e.getMessage());
			System.exit(8);
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
				recordManager.saveRecord(setup.recordFilename, BattleRecordFormat.BINARY_ZIP,
						new SerializableOptions(false));
			}
			if (setup.recordXmlFilename != null) {
				recordManager.saveRecord(setup.recordXmlFilename, BattleRecordFormat.XML, new SerializableOptions(false));
			}
			if (!isReplay && setup.csvResultsFilename != null) {
				saveCsvResults(event);
			}
		}

		@Override
		public void onBattleMessage(BattleMessageEvent event) {
			if (System.getProperty("logMessages", "true").equalsIgnoreCase("true")) {
				Logger.realOut.println(event.getMessage());
			}
		}

		@Override
		public void onBattleError(BattleErrorEvent event) {
			if (System.getProperty("logErrors", "true").equalsIgnoreCase("true")) {
				Logger.realErr.println(event.getError());
			}
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
