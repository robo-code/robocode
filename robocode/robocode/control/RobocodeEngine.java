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
 *     - Replaced FileSpecificationVector with plain Vector
 *     - GUI is disabled per default. If the setVisible() is called, the GUI will
 *       be enabled. The close() method is only calling dispose() on the
 *       RobocodeFrame if the GUI is enabled
 *     - Updated to use methods from FileUtil and Logger, which replaces
 *       methods that have been (re)moved from the robocode.util.Utils class
 *     - Changed to use FileUtil.getRobotsDir()
 *     - Modified getLocalRepository() to support teams by using
 *       FileSpecification instead of RobotFileSpecification
 *     - System.out, System.err, and System.in is now only set once, as new
 *       instances of the RobocodeEngine causes memory leaks with
 *       System.setOut() and System.setErr()
 *     - Updated Javadocs
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Bugfix: Inconsistent Behavior of RobocodeEngine.setVisible()
 *     - Bugfix: Added cleanup of the Robocode manager to the close() method
 *     Joachim Hofer
 *     - Bugfix in onBattleCompleted() where the RobotResults were ordered
 *       incorrectly when calling the battleComplete() so the results were given
 *       for the wrong robots
 *******************************************************************************/
package robocode.control;


import robocode.battle.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.io.FileUtil;
import robocode.manager.RobocodeManager;
import robocode.repository.FileSpecification;
import robocode.repository.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * The RobocodeEngine is the old interface provided for external applications
 * in order to let these applications run battles within the Robocode application,
 * and to get the results from these battles.
 * </p>
 * This class in the main class of the {@code robocode.control} package, and the
 * reason for having this control package.
 * </p>
 * The RobocodeEngine is used by RoboRumble@Home, which is integrated in
 * Robocode, but also RoboLeague and RobocodeJGAP. In addition, the
 * RobocodeEngine is also used by the test units for testing the Robocode
 * application itself.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Joachim Hofer (contributor)
 */
public class RobocodeEngine {

	RobocodeManager manager;
	private BattleObserver battleObserver;
	private BattleSpecification battleSpecification;

	/**
	 * Creates a new RobocodeEngine for controlling Robocode. The JAR file of
	 * Robocode is used to determine the root directory of Robocode.
	 *
	 * @see #RobocodeEngine(RobocodeListener)
	 * @see #RobocodeEngine(File)
	 * @see #RobocodeEngine(File, RobocodeListener)
	 * @see #close()
	 * @since 1.6.2
	 */
	public RobocodeEngine() {
		init(FileUtil.getCwd(), null);
	}

	/**
	 * Creates a new RobocodeEngine for controlling Robocode.
	 *
	 * @param robocodeHome the root directory of Robocode, e.g. C:\Robocode.
	 * @see #RobocodeEngine()
	 * @see #RobocodeEngine(RobocodeListener)
	 * @see #RobocodeEngine(File, RobocodeListener)
	 * @see #close()
	 * @since 1.6.2
	 */
	public RobocodeEngine(File robocodeHome) {
		init(robocodeHome, null);
	}

	/**
	 * Creates a new RobocodeEngine for controlling Robocode.
	 *
	 * @param robocodeHome the root directory of Robocode, e.g. C:\Robocode.
	 * @param listener	 the listener that must receive the callbacks from this
	 *                     RobocodeEngine.
	 * @see #RobocodeEngine()
	 * @see #RobocodeEngine(RobocodeListener)
	 * @see #RobocodeEngine(File)
	 * @see #close()
	 */
	public RobocodeEngine(File robocodeHome, RobocodeListener listener) {
		init(robocodeHome, listener);
	}

	/**
	 * Creates a new RobocodeEngine for controlling Robocode. The JAR file of
	 * Robocode is used to determine the root directory of Robocode.
	 *
	 * @param listener the listener that must receive the callbacks from this
	 *                 RobocodeEngine.
	 * @see #RobocodeEngine()
	 * @see #RobocodeEngine(File)
	 * @see #RobocodeEngine(File, RobocodeListener)
	 * @see #close()
	 */
	public RobocodeEngine(RobocodeListener listener) {
		init(FileUtil.getCwd(), listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finalize() throws Throwable {
		try {
			// Make sure close() is called to prevent memory leaks
			close();
		} finally {
			super.finalize();
		}
	}

	private void init(File robocodeHome, RobocodeListener listener) {
		File robotsDir = FileUtil.getRobotsDir();

		if (robotsDir == null) {
			throw new RuntimeException("No valid robot directory is specified");
		} else if (!(robotsDir.exists() && robotsDir.isDirectory())) {
			throw new RuntimeException('\'' + robotsDir.getAbsolutePath() + "' is not a valid robot directory");
		}

		manager = new RobocodeManager(true);
		manager.setEnableGUI(false);

		try {
			FileUtil.setCwd(robocodeHome);
		} catch (IOException e) {
			System.err.println(e);
			return;
		}

		manager.initSecurity(true, System.getProperty("EXPERIMENTAL", "false").equals("true"));
		if (listener != null) {
			battleObserver = new BattleObserver();
			battleObserver.listener = listener;
			manager.getBattleManager().addListener(battleObserver);
		}
	}

	/**
	 * Adds a battle listener that must receive events occurring in battles.
	 *
	 * @param listener the battle listener that must retrieve the event from
	 *                 the battles.
	 * @see #removeBattleListener(IBattleListener)
	 * @since 1.6.2
	 */
	public void addBattleListener(IBattleListener listener) {
		manager.getBattleManager().addListener(listener);
	}

	/**
	 * Removes a battle listener that has previously been added to this object.
	 *
	 * @param listener the battle listener that must be removed.
	 * @see #addBattleListener(IBattleListener)
	 * @since 1.6.2
	 */
	public void removeBattleListener(IBattleListener listener) {
		manager.getBattleManager().removeListener(listener);
	}

	/**
	 * Closes the RobocodeEngine and releases any allocated resources.
	 * You should call this when you have finished using the RobocodeEngine.
	 * This method automatically disposes the Robocode window if it open.
	 */
	public void close() {
		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().dispose();
		}
		if (battleObserver != null) {
			manager.getBattleManager().removeListener(battleObserver);
		}
		if (manager != null) {
			manager.cleanup();
			manager = null;
		}
	}

	/**
	 * Returns the installed version of Robocode.
	 *
	 * @return the installed version of Robocode.
	 */
	public String getVersion() {
		return manager.getVersionManager().getVersion();
	}

	/**
	 * Shows or hides the Robocode window.
	 *
	 * @param visible {@code true} if the Robocode window must be set visible;
	 *                {@code false} otherwise.
	 */
	public void setVisible(boolean visible) {
		if (visible && !manager.isGUIEnabled()) {
			// The GUI must be enabled in order to show the window
			manager.setEnableGUI(true);

			// Set the Look and Feel (LAF)
			robocode.manager.LookAndFeelManager.setLookAndFeel();
		}

		if (manager.isGUIEnabled()) {
			manager.getWindowManager().showRobocodeFrame(visible);
			manager.getProperties().setOptionsCommonShowResults(visible);
		}
	}

	/**
	 * Returns all robots available from the local robot repository of Robocode.
	 * These robots must exists in the /robocode/robots directory, and must be
	 * compiled in advance.
	 *
	 * @return an array of all available robots from the local robot repository.
	 * @see RobotSpecification
	 * @see #getLocalRepository(String)
	 */
	public RobotSpecification[] getLocalRepository() {
		Repository robotRepository = manager.getRobotRepositoryManager().getRobotRepository();
		List<FileSpecification> list = robotRepository.getRobotSpecificationsList(false, false, false, false, false,
				false);
		RobotSpecification robotSpecs[] = new RobotSpecification[list.size()];

		for (int i = 0; i < robotSpecs.length; i++) {
			final FileSpecification specification = list.get(i);

			if (specification.isValid()) {
				robotSpecs[i] = new RobotSpecification(specification);
			}
		}
		return robotSpecs;
	}

	/**
	 * Returns a selection of robots available from the local robot repository
	 * of Robocode. These robots must exists in the /robocode/robots directory,
	 * and must be compiled in advance.
	 * </p>
	 * Notice: If a specified robot cannot be found in the repository, it will
	 * not be returned in the array of robots returned by this method.
	 *
	 * @param selectedRobotList a comma or space separated list of robots to
	 *                          return. The full class name must be used for
	 *                          specifying the individual robot, e.g.
	 *                          "sample.Corners, sample.Crazy"
	 * @return an array containing the available robots from the local robot
	 *         repository based on the selected robots specified with the
	 *         {@code selectedRobotList} parameter.
	 * @see RobotSpecification
	 * @see #getLocalRepository()
	 * @since 1.6.2
	 */
	public RobotSpecification[] getLocalRepository(String selectedRobotList) {
		RobotSpecification[] repository = getLocalRepository();

		HashMap<String, RobotSpecification> robotSpecMap = new HashMap<String, RobotSpecification>();

		for (RobotSpecification spec : repository) {
			robotSpecMap.put(spec.getNameAndVersion(), spec);
		}

		String[] selectedRobots = selectedRobotList.split("[\\s,;]+");

		List<RobotSpecification> selectedRobotSpecs = new ArrayList<RobotSpecification>();

		RobotSpecification spec;

		for (String robot : selectedRobots) {
			spec = robotSpecMap.get(robot);
			if (spec != null) {
				selectedRobotSpecs.add(spec);
			}
		}
		return selectedRobotSpecs.toArray(new RobotSpecification[selectedRobotSpecs.size()]);
	}

	/**
	 * Runs the specified battle.
	 *
	 * @param battleSpecification the specification of the battle to play including the
	 *                            participation robots.
	 * @see #runBattle(BattleSpecification, boolean)
	 * @see RobocodeListener#battleComplete(BattleSpecification, RobotResults[])
	 * @see RobocodeListener#battleMessage(String)
	 * @see BattleSpecification
	 * @see #getLocalRepository()
	 */
	public void runBattle(BattleSpecification battleSpecification) {
		this.battleSpecification = battleSpecification;
		manager.getBattleManager().startNewBattle(battleSpecification, false);
	}

	/**
	 * Runs the specified battle.
	 *
	 * @param battleSpecification	   the specification of the battle to run including the
	 *                     participating robots.
	 * @param waitTillOver will block caller till end of battle if set
	 * @see #runBattle(BattleSpecification)
	 * @see RobocodeListener#battleComplete(BattleSpecification, RobotResults[])
	 * @see RobocodeListener#battleMessage(String)
	 * @see BattleSpecification
	 * @see #getLocalRepository()
	 * @since 1.6.2
	 */
	public void runBattle(BattleSpecification battleSpecification, boolean waitTillOver) {
		this.battleSpecification = battleSpecification;
		manager.getBattleManager().startNewBattle(battleSpecification, waitTillOver);
	}

	/**
	 * Aborts the current battle if it is running.
	 *
	 * @see #runBattle(BattleSpecification)
	 * @see RobocodeListener#battleAborted(BattleSpecification)
	 */
	public void abortCurrentBattle() {
		manager.getBattleManager().stop(true);
	}

	/**
	 * Print out all running threads to standard system out.
	 *
	 * @since 1.6.2
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

	/**
	 * Registered only if listener in not null.
	 */
	private class BattleObserver extends BattleAdaptor {
		private RobocodeListener listener;

		@Override
		public void onBattleEnded(BattleFinishedEvent event) {
			if (event.isAborted()) {
				listener.battleAborted(battleSpecification);
			}
		}

		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			listener.battleComplete(battleSpecification, RobotResults.convertResults(event.getResults()));
		}

		@Override
		public void onBattleMessage(BattleMessageEvent event) {
			listener.battleMessage(event.getMessage());
		}
	}
}
