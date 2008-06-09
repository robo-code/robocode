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
 *     - Code cleanup & optimizations
 *     - Removed getBattleView().setDoubleBuffered(false) as BufferStrategy is
 *       used now
 *     - Replaced FileSpecificationVector, RobotPeerVector, and
 *       RobotClassManagerVector with plain Vector
 *     - Added check for if GUI is enabled before using graphical components
 *     - Added restart() method
 *     - Ported to Java 5
 *     - Added support for the replay feature
 *     - Removed the clearBattleProperties()
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Added PauseResumeListener interface, addListener(), removeListener(),
 *       notifyBattlePaused(), notifyBattleResumed() for letting listeners
 *       receive notifications when the game is paused or resumed
 *     - Added missing functionality in to support team battles in
 *       startNewBattle(BattleSpecification spec, boolean replay)
 *     - Added missing close() on FileInputStreams and FileOutputStreams
 *     - isPaused() is now synchronized
 *     - Extended sendResultsToListener() to handle teams as well as robots
 *     - Added setDefaultBattleProperties() for resetting battle properties
 *     - Removed the showResultsDialog parameter from the stop() method
 *     - Added null pointer check to the sendResultsToListener() method
 *     - Enhanced the getBattleFilename() to look into the battle dir and also
 *       add the .battle file extension to the returned file name if this is
 *       missing
 *     - Removed battleRunning field, isBattleRunning(), and setBattle()
 *     - Bugfix: Multiple battle threads could run in the same time when the
 *       battle thread was started in startNewBattle()
 *     Luis Crespo
 *     - Added debug step feature, including the nextTurn(), shouldStep(),
 *       startNewRound()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Bugfix: Added cleanup() to prevent memory leaks by removing circular
 *       references
 *******************************************************************************/
package robocode.manager;


import robocode.battle.Battle;
import robocode.battle.BattleProperties;
import robocode.battle.BattleResultsTableModel;
import robocode.battle.events.BattleAdaptor;
import robocode.battle.events.BattleEventDispatcher;
import robocode.battle.events.IBattleListener;
import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;
import robocode.battleview.BattleView;
import robocode.control.BattleSpecification;
import robocode.control.RobocodeListener;
import robocode.control.RobotResults;
import robocode.control.RandomFactory;
import robocode.io.FileUtil;
import robocode.io.Logger;
import static robocode.io.Logger.logError;
import static robocode.io.Logger.logMessage;
import robocode.peer.TeamPeer;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.FileSpecification;
import robocode.repository.RobotFileSpecification;
import robocode.repository.TeamSpecification;
import robocode.security.RobocodeSecurityManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class BattleManager {
	private RobocodeManager manager;

	private boolean exitOnComplete;

	private Battle battle;
	private BattleSpecification battleSpecification;
	private BattleProperties battleProperties = new BattleProperties();

	private BattleEventDispatcher battleEventDispatcher = new BattleEventDispatcher();

	private BattleObserver battleObserver = new BattleObserver();

	private String battleFilename;
	private String battlePath;
	private String resultsFile;

	private AtomicInteger pauseCount = new AtomicInteger(0);

	private int stepTurn;

	public BattleManager(RobocodeManager manager) {
		this.manager = manager;

		battleEventDispatcher.addListener(battleObserver);
	}

	public void cleanup() {
		battle = null;
		manager = null;
		battleEventDispatcher = null;
		battleObserver = null;
	}

	/**
	 * Steps for a single turn, then goes back to paused
	 */
	public void nextTurn() {
		if (battle != null && battle.isRunning()) {
			stepTurn = battle.getCurrentTime() + 1;
		}
	}

	/**
	 * If the battle is paused, this method determines if it should perform one turn and then stop again.
	 *
	 * @return true if the battle should perform one turn, false otherwise
	 */
	public boolean shouldStep() {
		// This code assumes it is called only if the battle is paused.
		return stepTurn > battle.getCurrentTime();
	}

	/**
	 * This method should be called to inform the battle manager that a new round is starting
	 */
	public void startNewRound() {
		stepTurn = 0;
	}

	public void stop() {
		if (getBattle() != null) {
			getBattle().stop();
		}
	}

	public void restart() {
		// Start new battle. The old battle is automatically stopped
		startNewBattle(battleProperties, false, false);
	}

	public void replay() {
		startNewBattle(battleProperties, false, true);
	}

	public void startNewBattle(BattleProperties battleProperties, boolean exitOnComplete, boolean replay) {
		this.battleProperties = battleProperties;

		List<FileSpecification> robotSpecificationsList = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsList(
				false, false, false, false, false, false);

		List<RobotClassManager> battlingRobotsList = Collections.synchronizedList(new ArrayList<RobotClassManager>());

		if (battleProperties.getSelectedRobots() != null) {
			StringTokenizer tokenizer = new StringTokenizer(battleProperties.getSelectedRobots(), ",");

			while (tokenizer.hasMoreTokens()) {
				String bot = tokenizer.nextToken();

				boolean failed = loadRobot(robotSpecificationsList, battlingRobotsList, bot, null, null);

				if (failed) {
					return;
				}
			}
		}
		startNewBattleImpl(battlingRobotsList, exitOnComplete, replay, null, false);
	}

	public void startNewBattle(BattleSpecification spec, boolean replay, boolean waitTillOver) {
		battleProperties = new BattleProperties();
		battleProperties.setBattlefieldWidth(spec.getBattlefield().getWidth());
		battleProperties.setBattlefieldHeight(spec.getBattlefield().getHeight());
		battleProperties.setGunCoolingRate(spec.getGunCoolingRate());
		battleProperties.setInactivityTime(spec.getInactivityTime());
		battleProperties.setNumRounds(spec.getNumRounds());
		battleProperties.setSelectedRobots(spec.getRobots());

		List<FileSpecification> robotSpecificationsList = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsList(
				false, false, false, false, false, false);
		List<RobotClassManager> battlingRobotsList = Collections.synchronizedList(new ArrayList<RobotClassManager>());

		for (robocode.control.RobotSpecification battleRobotSpec : spec.getRobots()) {
			if (battleRobotSpec == null) {
				break;
			}

			String bot = battleRobotSpec.getClassName();

			if (!(battleRobotSpec.getVersion() == null || battleRobotSpec.getVersion().length() == 0)) {
				bot += ' ' + battleRobotSpec.getVersion();
			}

			boolean failed = loadRobot(robotSpecificationsList, battlingRobotsList, bot, spec, battleRobotSpec);

			if (failed) {
				return;
			}
		}
		startNewBattleImpl(battlingRobotsList, false, replay, spec, true);
	}

	private boolean loadRobot(List<FileSpecification> robotSpecificationsList, List<RobotClassManager> battlingRobotsList, String bot, BattleSpecification spec, robocode.control.RobotSpecification battleRobotSpec) {
		boolean found = false;

		for (FileSpecification fileSpec : robotSpecificationsList) {
			if (fileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
				if (fileSpec instanceof RobotFileSpecification) {
					RobotClassManager rcm = new RobotClassManager((RobotFileSpecification) fileSpec);

					if (battleRobotSpec != null) {
						rcm.setControlRobotSpecification(battleRobotSpec);
					}
					battlingRobotsList.add(rcm);
					found = true;
					break;
				} else if (fileSpec instanceof TeamSpecification) {
					TeamSpecification currentTeam = (TeamSpecification) fileSpec;
					TeamPeer teamManager = new TeamPeer(currentTeam.getName());

					StringTokenizer teamTokenizer = new StringTokenizer(currentTeam.getMembers(), ",");

					while (teamTokenizer.hasMoreTokens()) {
						bot = teamTokenizer.nextToken();
						RobotFileSpecification match = null;

						for (FileSpecification teamFileSpec : robotSpecificationsList) {
							// Teams cannot include teams
							if (teamFileSpec instanceof TeamSpecification) {
								continue;
							}
							if (teamFileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
								// Found team member
								match = (RobotFileSpecification) teamFileSpec;
								if (currentTeam.getRootDir().equals(teamFileSpec.getRootDir())
										|| currentTeam.getRootDir().equals(teamFileSpec.getRootDir().getParentFile())) {
									found = true;
									break;
								}
								// else, still looking
							}
						}
						RobotClassManager rcm = new RobotClassManager(match, teamManager);

						if (battleRobotSpec != null) {
							rcm.setControlRobotSpecification(battleRobotSpec);
						}
						battlingRobotsList.add(rcm);
					}
					break;
				}
			}
		}

		if (!found) {
			logError("Aborting battle, could not find robot: " + bot);
			if (spec != null && manager.getListener() != null) {
				manager.getListener().battleAborted(spec);
			}
			return true;
		}
		return false;
	}

	private void startNewBattleImpl(List<RobotClassManager> battlingRobotsList, boolean exitOnComplete, boolean replay,
			BattleSpecification battleSpecification, boolean waitTillOver) {

		this.battleSpecification = battleSpecification;
		this.exitOnComplete = exitOnComplete;

		logMessage("Preparing battle...");
		if (battle != null) {
			battle.stop();
		}

		BattleField battleField = new DefaultBattleField(battleProperties.getBattlefieldWidth(),
				battleProperties.getBattlefieldHeight());

		if (manager.isGUIEnabled()) {
			BattleView battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();

			battleView.setup(battleField, battleEventDispatcher);
		}
		Logger.setLogListener(battleEventDispatcher);

		if (manager.isSoundEnabled()) {
			manager.getSoundManager().setBattleEventDispatcher(battleEventDispatcher);
		}

		// resets seed for deterministic behavior of Random
		final String seed = System.getProperty("RANDOMSEED", "none");

		if (!seed.equals("none")) {
			RandomFactory.resetDeterministic(Long.valueOf(seed));
		}

		battle = new Battle(battleField, manager, battleEventDispatcher);

		// Set stuff the view needs to know
		battle.setProperties(battleProperties);

		Thread battleThread = new Thread(Thread.currentThread().getThreadGroup(), battle);

		battleThread.setPriority(Thread.NORM_PRIORITY);
		battleThread.setName("Battle Thread");
		battle.setBattleThread(battleThread);
		battle.setReplay(replay);

		if (!System.getProperty("NOSECURITY", "false").equals("true")) {
			((RobocodeSecurityManager) System.getSecurityManager()).addSafeThread(battleThread);
			((RobocodeSecurityManager) System.getSecurityManager()).setBattleThread(battleThread);
		}

		if (manager.isGUIEnabled()) {
			robocode.battleview.BattleView battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();

			battleView.setVisible(true);
			battleView.setInitialized(false);
		}

		for (RobotClassManager robotClassMgr : battlingRobotsList) {
			battle.addRobot(robotClassMgr);
		}

		if (manager.isGUIEnabled()) {
			robocode.dialog.RobocodeFrame frame = manager.getWindowManager().getRobocodeFrame();

			frame.getRobocodeMenuBar().getBattleSaveAsMenuItem().setEnabled(true);
			frame.getRobocodeMenuBar().getBattleSaveMenuItem().setEnabled(true);

			if (frame.getPauseButton().getText().equals("Resume")) {
				frame.pauseResumeButtonActionPerformed();
			}

			manager.getRobotDialogManager().setActiveBattle(battle);
		}

		// Start the battle thread
		battleThread.start();

		// Wait until the battle is running and ended.
		// This must be done as a new battle could be started immediately after this one causing
		// multiple battle threads to run at the same time, which must be prevented!
		battle.waitTillStarted();
		if (waitTillOver) {
			battle.waitTillOver();
		}
	}

	public String getBattleFilename() {
		String filename = battleFilename;

		if (filename != null) {
			if (filename.indexOf(File.separatorChar) < 0) {
				filename = FileUtil.getBattlesDir().getName() + File.separatorChar + filename;
			}
			if (!filename.endsWith(".battle")) {
				filename += ".battle";
			}
		}
		return filename;
	}

	public void setBattleFilename(String newBattleFilename) {
		battleFilename = newBattleFilename;
	}

	public boolean isPaused() {
		return (pauseCount.get() != 0);
	}

	public void pauseBattle() {
		if (pauseCount.incrementAndGet() == 1) {
			battleEventDispatcher.onBattlePaused();
		}
	}

	public synchronized void resumeBattle() {
		int oldPauseCount = pauseCount.get();

		pauseCount.set(Math.max(pauseCount.decrementAndGet(), 0));

		if (oldPauseCount == 1) {
			battleEventDispatcher.onBattleResumed();
		}
	}

	public String getBattlePath() {
		if (battlePath == null) {
			battlePath = System.getProperty("BATTLEPATH");
			if (battlePath == null) {
				battlePath = "battles";
			}
			battlePath = new File(FileUtil.getCwd(), battlePath).getAbsolutePath();
		}
		return battlePath;
	}

	public void saveBattleProperties() {
		if (battleProperties == null) {
			logError("Cannot save null battle properties");
			return;
		}
		if (battleFilename == null) {
			logError("Cannot save battle to null path, use setBattleFilename()");
			return;
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(battleFilename);

			battleProperties.store(out, "Battle Properties");
		} catch (IOException e) {
			logError("IO Exception saving battle properties: " + e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {// swallow
				}
			}
		}
	}

	public void loadBattleProperties() {
		FileInputStream in = null;

		try {
			in = new FileInputStream(getBattleFilename());
			getBattleProperties().load(in);
		} catch (FileNotFoundException e) {
			logError("No file " + battleFilename + " found, using defaults.");
		} catch (IOException e) {
			logError("IO Exception reading " + getBattleFilename() + ": " + e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {// swallow
				}
			}
		}
	}

	public Battle getBattle() {
		return battle;
	}

	public BattleProperties getBattleProperties() {
		if (battleProperties == null) {
			battleProperties = new BattleProperties();
		}
		return battleProperties;
	}

	public BattleSpecification getBattleSpecification() {
		return battleSpecification;
	}

	public void setDefaultBattleProperties() {
		battleProperties = new BattleProperties();
	}

	public void setResultsFile(String newResultsFile) {
		resultsFile = newResultsFile;
	}

	public String getResultsFile() {
		return resultsFile;
	}

	public void addListener(IBattleListener listener) {
		battleEventDispatcher.addListener(listener);
	}

	public void removeListener(IBattleListener listener) {
		battleEventDispatcher.removeListener(listener);
	}

	public boolean isRunningMinimized() {
		return !manager.isGUIEnabled() || manager.getWindowManager().getRobocodeFrame().isIconified();
	}

    private void printResultsData(Battle battle) {
        // Do not print out if no result file has been specified and the GUI is enabled
        if (battle.isReplay() || (getResultsFile() == null && (!exitOnComplete || manager.isGUIEnabled()))) {
            return;
        }

        PrintStream out = null;
        FileOutputStream fos = null;

        if (getResultsFile() == null) {
            out = System.out;
        } else {
            File f = new File(getResultsFile());

            try {
                fos = new FileOutputStream(f);
                out = new PrintStream(fos);
            } catch (IOException e) {
                Logger.logError(e);
            }
        }

        BattleResultsTableModel resultsTable = new BattleResultsTableModel(battle);

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
		@Override
		public void onBattleEnded(boolean isAborted) {
			RobocodeListener listener = manager.getListener();

            printResultsData(battle);

			if (isAborted) {
				if (listener != null) {
					listener.battleAborted(battleSpecification);
				}
				// We should only exit here, if the battle was aborted.
				// Otherwise the results will not be given to the client when Robocode is closed down
				if (exitOnComplete) {
					System.exit(0);
				}
			}
		}

		@Override
		public void onBattleCompleted(BattleSpecification battleSpecification, RobotResults[] results) {
			RobocodeListener listener = manager.getListener();

			if (listener != null) {
				listener.battleComplete(battleSpecification, results);
			}
			if (exitOnComplete) {
				System.exit(0);
			}
		}

		@Override
		public void onBattleMessage(String message) {
			RobocodeListener listener = manager.getListener();

			if (listener != null) {
				listener.battleMessage(message);
			}
		}
	}
}
