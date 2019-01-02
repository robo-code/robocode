/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
import java.util.ArrayList;
import java.util.List;


/**
 * The RobocodeEngine is the interface provided for external applications
 * in order to let these applications run battles within the Robocode application,
 * and to get the results from these battles.
 * <p>
 * This class in the main entry class of the {@code robocode.control} package.
 * <p>
 * The RobocodeEngine is used by e.g. RoboRumble@Home client, which is integrated in
 * Robocode. In addition, the RobocodeEngine is also used by the test units for
 * testing the Robocode application itself.
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
	private List<IBattleListener> listeners = new ArrayList<IBattleListener>();

	/**
	 * Creates a new RobocodeEngine for controlling Robocode.
	 * In order for this constructor to work, the current working directory must be the
	 * home directory directory of Robocode, e.g. C:\Robocode
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
	 * @param robocodeHome the home directory of Robocode, e.g. C:\Robocode.
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
	 * <p>
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
	public RobocodeEngine(File robocodeHome, RobocodeListener listener) {
		init(robocodeHome, listener);
	}

	/**
	 * @deprecated Since 1.6.2. Use {@link #RobocodeEngine()} and
	 * {@link #addBattleListener(IBattleListener) addBattleListener()} instead.
	 * <p>
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
	 * {@inheritDoc}
	 */
	public void addBattleListener(IBattleListener listener) {
		listeners.add(listener);
		ContainerBase.getComponent(IBattleManagerBase.class).addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeBattleListener(IBattleListener listener) {
		listeners.remove(listener);
		ContainerBase.getComponent(IBattleManagerBase.class).removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		setVisible(false);
		if (battleObserver != null) {
			ContainerBase.getComponent(IBattleManagerBase.class).removeListener(battleObserver);
		}
		for (IBattleListener listener : listeners) {
			ContainerBase.getComponent(IBattleManagerBase.class).removeListener(listener);
		}
		listeners.clear();
		HiddenAccess.cleanup();
	}

	/**
	 * {@inheritDoc}
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
	 * @return a File for the robot directory containing all robots.
	 *
	 * @since 1.7.1
	 */
	public static File getRobotsDir() {
		return FileUtil.getRobotsDir();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setVisible(boolean visible) {
		IWindowManagerBase windowManager = ContainerBase.getComponent(IWindowManagerBase.class);
		if (windowManager != null) {
			windowManager.setVisibleForRobotEngine(visible);
		} else {
			Logger.logError(
					"setVisible: No window manager was found.\n"
							+ "Perhaps you are missing the robocode.ui-1.x.x.x-jar file or the GUI has been disabled?");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public RobotSpecification[] getLocalRepository() {
		final IRepositoryManagerBase repository = ContainerBase.getComponent(IRepositoryManagerBase.class);

		repository.refresh(); // Bug fix [2972932]
		return repository.getSpecifications();
	}

	/**
	 * {@inheritDoc}
	 */
	public RobotSpecification[] getLocalRepository(String selectedRobots) {
		final IRepositoryManagerBase repository = ContainerBase.getComponent(IRepositoryManagerBase.class);

		repository.refresh(); // Bug fix [2972932]
		return repository.loadSelectedRobots(selectedRobots);
	}

	/**
	 * {@inheritDoc}
	 */
	public void runBattle(BattleSpecification battleSpecification) {
		runBattle(battleSpecification, null, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void runBattle(BattleSpecification battleSpecification, boolean waitTillOver) {
		runBattle(battleSpecification, null, waitTillOver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void runBattle(BattleSpecification battleSpecification, String initialPositions, boolean waitTillOver) {
		this.battleSpecification = battleSpecification;
		ContainerBase.getComponent(IBattleManagerBase.class).startNewBattle(battleSpecification, initialPositions,
				waitTillOver, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void waitTillBattleOver() {
		ContainerBase.getComponent(IBattleManagerBase.class).waitTillOver();
	}

	/**
	 * {@inheritDoc}
	 */
	public void abortCurrentBattle() {
		ContainerBase.getComponent(IBattleManagerBase.class).stop(true);
	}

	/**
	 * Prints out all running threads to standard system out.
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
	 * Enables or disables messages and warnings logged to {@link System#out}.
	 * 
	 * @param enable true if log messages must be enabled; false if log messages must be disabled.
	 * @see #setLogErrorsEnabled(boolean)
	 * @since 1.8.0.0
	 */
	public static void setLogMessagesEnabled(boolean enable) {
		System.setProperty("logMessages", "" + enable);
	}

	/**
	 * Enables or disables errors logged to {@link System#err}.
	 * 
	 * @param enable true if log errors must be enabled; false if log errors must be disabled.
	 * @see #setLogMessagesEnabled(boolean)
	 * @since 1.8.0.0
	 */
	public static void setLogErrorsEnabled(boolean enable) {
		System.setProperty("logErrors", "" + enable);		
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
