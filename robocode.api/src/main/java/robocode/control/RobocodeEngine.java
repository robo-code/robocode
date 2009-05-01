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


import net.sf.robocode.battle.IBattleManagerBase;
import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.gui.IWindowManagerBase;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.manager.IVersionManagerBase;
import net.sf.robocode.repository.IRepositoryManagerBase;
import net.sf.robocode.security.HiddenAccess;
import robocode.control.events.*;

import java.io.File;


/**
 * The RobocodeEngine is the interface provided for external applications
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
 * @author Pavel Savara (contributor)
 */
public class RobocodeEngine implements IRobocodeEngine {

	private BattleObserver battleObserver;
	private BattleSpecification battleSpecification;

	/**
	 * Creates a new RobocodeEngine for controlling Robocode. The JAR file of
	 * Robocode is used to determine the root directory of Robocode.
	 *
	 * @see #RobocodeEngine(File)
	 * @see #close()
	 * @since 1.6.2
	 */
	public RobocodeEngine() {
		init(null, (IBattleListener) null);
	}

	/**
	 * Creates a new RobocodeEngine for controlling Robocode.
	 *
	 * @param robocodeHome the root directory of Robocode, e.g. C:\Robocode.
	 * @see #RobocodeEngine()
	 * @see #close()
	 * @since 1.6.2
	 */
	public RobocodeEngine(File robocodeHome) {
		init(robocodeHome, (IBattleListener) null);
	}

	/**
	 * @deprecated Since 1.6.2. Use {@link #RobocodeEngine(File)} and
	 * {@link #addBattleListener(IBattleListener) addBattleListener()} instead.
	 * <p/>
	 * Creates a new RobocodeEngine for controlling Robocode.
	 *
	 * @param robocodeHome the root directory of Robocode, e.g. C:\Robocode.
	 * @param listener	 the listener that must receive the callbacks from this
	 *                     RobocodeEngine.
	 * @see #RobocodeEngine()
	 * @see #RobocodeEngine(File)
	 * @see #close()
	 */
	@Deprecated
	@SuppressWarnings({ "deprecation"})
	public RobocodeEngine(File robocodeHome, RobocodeListener listener) {
		init(robocodeHome, listener);
	}

	/**
	 * @deprecated Since 1.6.2. Use {@link #RobocodeEngine()} and
	 * {@link #addBattleListener(IBattleListener) addBattleListener()} instead.
	 * <p/>
	 * Creates a new RobocodeEngine for controlling Robocode. The JAR file of
	 * Robocode is used to determine the root directory of Robocode.
	 *
	 * @param listener the listener that must receive the callbacks from this
	 *                 RobocodeEngine.
	 * @see #RobocodeEngine()
	 * @see #RobocodeEngine(File)
	 * @see #close()
	 */
	@Deprecated
	@SuppressWarnings({ "deprecation"})
	public RobocodeEngine(RobocodeListener listener) {
		init(null, listener);
	}

	public RobocodeEngine(IBattleListener listener) {
		init(null, listener);
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

	@SuppressWarnings("deprecation") // We must still support deprecated RobocodeListener
	private void init(File robocodeHome, RobocodeListener listener) {
		if (listener != null) {
			battleObserver = new BattleObserver();
			battleObserver.listener = listener;
		}
		HiddenAccess.initContainerForRobotEngine(robocodeHome, battleObserver);
	}

	private void init(File robocodeHome, IBattleListener listener) {
		HiddenAccess.initContainerForRobotEngine(robocodeHome, listener);
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
		ContainerBase.getComponent(IBattleManagerBase.class).addListener(listener);
	}

	/**
	 * Removes a battle listener that has previously been added to this object.
	 *
	 * @param listener the battle listener that must be removed.
	 * @see #addBattleListener(IBattleListener)
	 * @since 1.6.2
	 */
	public void removeBattleListener(IBattleListener listener) {
		ContainerBase.getComponent(IBattleManagerBase.class).removeListener(listener);
	}

	/**
	 * Closes the RobocodeEngine and releases any allocated resources.
	 * You should call this when you have finished using the RobocodeEngine.
	 * This method automatically disposes the Robocode window if it open.
	 */
	public void close() {
		if (battleObserver != null) {
			ContainerBase.getComponent(IBattleManagerBase.class).removeListener(battleObserver);
		}
		HiddenAccess.cleanup();
	}

	/**
	 * Returns the installed version of Robocode.
	 *
	 * @return the installed version of Robocode.
	 */
	public String getVersion() {
		return ContainerBase.getComponent(IVersionManagerBase.class).getVersion();
	}

	/**
	 * Returns the current working directory.
	 *
	 * @return a File for the current working directory.
	 *
	 * @since 1.7.1
	 */
	public static File getCurrentWorkingDir() {
		return FileUtil.getCwd();
	}

	/**
	 * Returns the directory containing the robots.
	 *
	 * @return a File that is the directory containing the robots.
	 *
	 * @since 1.7.1
	 */
	public static File getRobotsDir() {
		return FileUtil.getRobotsDir();
	}

	/**
	 * Shows or hides the Robocode window.
	 *
	 * @param visible {@code true} if the Robocode window must be set visible;
	 *                {@code false} otherwise.
	 */
	public void setVisible(boolean visible) {
		ContainerBase.getComponent(IWindowManagerBase.class).setVisibleForRobotEngine(visible);
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
		return ContainerBase.getComponent(IRepositoryManagerBase.class).getSpecifications();
	}

	/**
	 * Returns a selection of robots available from the local robot repository
	 * of Robocode. These robots must exists in the /robocode/robots directory,
	 * and must be compiled in advance.
	 * </p>
	 * Notice: If a specified robot cannot be found in the repository, it will
	 * not be returned in the array of robots returned by this method.
	 *
	 * @param selectedRobots a comma or space separated list of robots to
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
	public RobotSpecification[] getLocalRepository(String selectedRobots) {
		final IRepositoryManagerBase repository = ContainerBase.getComponent(IRepositoryManagerBase.class);

		return repository.loadSelectedRobots(selectedRobots);
	}

	/**
	 * Runs the specified battle.
	 *
	 * @param battleSpecification the specification of the battle to play including the
	 *                            participation robots.
	 * @see #runBattle(BattleSpecification, boolean)
	 * @see BattleSpecification
	 * @see #getLocalRepository()
	 */
	public void runBattle(BattleSpecification battleSpecification) {
		runBattle(battleSpecification, null, false);
	}

	/**
	 * Runs the specified battle.
	 *
	 * @param battleSpecification	   the specification of the battle to run including the
	 *                     participating robots.
	 * @param waitTillOver will block caller till end of battle if set
	 * @see #runBattle(BattleSpecification)
	 * @see BattleSpecification
	 * @see #getLocalRepository()
	 * @since 1.6.2
	 */
	public void runBattle(BattleSpecification battleSpecification, boolean waitTillOver) {
		runBattle(battleSpecification, null, waitTillOver);
	}

	/**
	 * Runs the specified battle.
	 *
	 * @param battleSpecification the specification of the battle to run including the
	 *                     participating robots.
	 * @param initialPositions a comma or space separated list like: x1,y1,heading1,
	 *        x2,y2,heading2, which are the coordinates and heading of robot #1 and #2.
	 *        So e.g. (0,0,180), (50,80,270) means that robot #1 has position (0,0) and
	 *        heading 180, and robot #2 has position (50,80) and heading 270.
	 * @param waitTillOver will block caller till end of battle if set
	 * @see #runBattle(BattleSpecification)
	 * @see BattleSpecification
	 * @see #getLocalRepository()
	 * @since 1.7.1.2
	 */
	public void runBattle(BattleSpecification battleSpecification, String initialPositions, boolean waitTillOver) {
		this.battleSpecification = battleSpecification;
		ContainerBase.getComponent(IBattleManagerBase.class).startNewBattle(battleSpecification, initialPositions,
				waitTillOver, false);
	}

	/**
	 * Will block caller until current battle is over 
	 * @see #runBattle(BattleSpecification)
	 * @see #runBattle(BattleSpecification, boolean)
	 * @since 1.6.2
	 */
	public void waitTillBattleOver() {
		ContainerBase.getComponent(IBattleManagerBase.class).waitTillOver();
	}

	/**
	 * Aborts the current battle if it is running.
	 *
	 * @see #runBattle(BattleSpecification)
	 */
	public void abortCurrentBattle() {
		ContainerBase.getComponent(IBattleManagerBase.class).stop(true);
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
				Logger.realOut.print("  ");
			} else {
				Logger.realOut.print("* ");
			}
			Logger.realOut.println("In group: " + currentGroup.getName());
			int numThreads = currentGroup.enumerate(threads);

			for (int j = 0; j < numThreads; j++) {
				if (threads[j].isDaemon()) {
					Logger.realOut.print("  ");
				} else {
					Logger.realOut.print("* ");
				}
				Logger.realOut.println(threads[j].getName());
			}
			Logger.realOut.println("---------------");
		}
	}

	/**
	 * Registered only if listener in not null.
	 */
	private class BattleObserver extends BattleAdaptor {
		@SuppressWarnings("deprecation") // We must still support deprecated RobocodeListener
		private RobocodeListener listener;

		@SuppressWarnings("deprecation") // We must still support deprecated RobocodeListener
		@Override
		public void onBattleFinished(BattleFinishedEvent event) {
			if (event.isAborted()) {
				listener.battleAborted(battleSpecification);
			}
		}

		@SuppressWarnings("deprecation") // We must still support deprecated RobocodeListener
		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			listener.battleComplete(battleSpecification, RobotResults.convertResults(event.getSortedResults()));
		}

		@SuppressWarnings("deprecation") // We must still support deprecated RobocodeListener
		@Override
		public void onBattleMessage(BattleMessageEvent event) {
			listener.battleMessage(event.getMessage());
		}
	}
}
