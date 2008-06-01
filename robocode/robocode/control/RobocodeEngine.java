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
 *******************************************************************************/
package robocode.control;


import robocode.RobocodeFileOutputStream;
import robocode.io.FileUtil;
import robocode.manager.RobocodeManager;
import robocode.repository.FileSpecification;
import robocode.repository.Repository;
import robocode.security.RobocodeSecurityManager;
import robocode.security.RobocodeSecurityPolicy;

import java.io.File;
import java.io.IOException;
import java.security.Policy;
import java.util.List;


/**
 * The RobocodeEngine is meant for 3rd party applications to let them run
 * battles in Robocode and receive the results. This class in the main class
 * of the {@code robocode.control} package.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobocodeEngine {
    private RobocodeManager manager;

    /**
	 * Creates a new RobocodeEngine for controlling Robocode.
	 *
	 * @param robocodeHome the root directory of Robocode, e.g. C:\Robocode.
	 * @param listener     the listener that must receive the callbacks from this
	 *                     RobocodeEngine.
	 * @see #RobocodeEngine(RobocodeListener)
	 * @see #close()
	 */
	public RobocodeEngine(File robocodeHome, RobocodeListener listener) {
		init(robocodeHome, listener);
	}

	/**
	 * Creates a new RobocodeEngine for controlling Robocode. The JAR file of
	 * Robocode is used to determine the root directory of Robocode.
	 * See {@link #RobocodeEngine(File, RobocodeListener)}.
	 *
	 * @param listener the listener that must receive the callbacks from this
	 *                 RobocodeEngine.
	 * @see #RobocodeEngine(File, RobocodeListener)
	 * @see #close()
	 */
	public RobocodeEngine(RobocodeListener listener) {
		File robotsDir = FileUtil.getRobotsDir();

		if (robotsDir.exists()) {
			init(FileUtil.getCwd(), listener);
		} else {
			throw new RuntimeException("File not found: " + robotsDir);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finalize() throws Throwable {
		super.finalize();

		// Make sure close() is called to prevent memory leaks
		close();
	}

	private void init(File robocodeHome, RobocodeListener listener) {
		manager = new RobocodeManager(true, listener);
		manager.setEnableGUI(false);

		try {
			FileUtil.setCwd(robocodeHome);
		} catch (IOException e) {
			System.err.println(e);
			return;
		}

        manager.initSecurity(true, false);
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
	 * Returns the robots available for for battle from the local robot
	 * repository in the Robocode home folder.
	 *
	 * @return an array of all available robots for battle from the local robot
	 *         repository.
	 * @see RobotSpecification
	 */
	public RobotSpecification[] getLocalRepository() {
		Repository robotRepository = manager.getRobotRepositoryManager().getRobotRepository();
		List<FileSpecification> list = robotRepository.getRobotSpecificationsList(false, false, false, false, false,
				false);
		RobotSpecification robotSpecs[] = new RobotSpecification[list.size()];

		for (int i = 0; i < robotSpecs.length; i++) {
			robotSpecs[i] = new RobotSpecification(list.get(i));
		}
		return robotSpecs;
	}

	/**
	 * Runs the specified battle.
	 *
	 * @param battle the specification of the battle to play including the
	 *               participation robots.
	 * @see RobocodeListener#battleComplete(BattleSpecification, RobotResults[])
	 * @see RobocodeListener#battleMessage(String)
	 * @see BattleSpecification
	 * @see #getLocalRepository()
	 */
	public void runBattle(BattleSpecification battle) {
		manager.getBattleManager().startNewBattle(battle, false, true);
	}

	/**
	 * Aborts the current battle if it is running.
	 *
	 * @see #runBattle(BattleSpecification)
	 * @see RobocodeListener#battleAborted(BattleSpecification)
	 */
	public void abortCurrentBattle() {
		manager.getBattleManager().stop();
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
