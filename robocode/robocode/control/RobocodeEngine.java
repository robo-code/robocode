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
import robocode.io.Logger;
import robocode.manager.RobocodeManager;
import robocode.repository.FileSpecification;
import robocode.repository.Repository;
import robocode.security.RobocodeSecurityManager;
import robocode.security.RobocodeSecurityPolicy;
import robocode.security.SecureInputStream;
import robocode.security.SecurePrintStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
	private RobocodeListener listener;
	private RobocodeManager manager;

	static PrintStream sysout = new SecurePrintStream(System.out, true, "System.out");
	static PrintStream syserr = new SecurePrintStream(System.err, true, "System.err");
	static InputStream sysin = new SecureInputStream(System.in, "System.in");

	static {
		// Secure System.in, System.err, System.out
        SecurePrintStream.realOut = System.out;
        SecurePrintStream.realErr = System.err;
		System.setOut(sysout);
		if (!System.getProperty("debug", "false").equals("true")) {
			System.setErr(syserr);
		}
		System.setIn(sysin);
	}

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
		this.listener = listener;
		manager = new RobocodeManager(true, listener);
		manager.setEnableGUI(false);

		try {
			FileUtil.setCwd(robocodeHome);
		} catch (IOException e) {
			System.err.println(e);
			return;
		}

		Thread.currentThread().setName("Application Thread");
		RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());

		Policy.setPolicy(securityPolicy);
		System.setSecurityManager(
				new RobocodeSecurityManager(Thread.currentThread(), manager.getThreadManager(), true, false));
		RobocodeFileOutputStream.setThreadManager(manager.getThreadManager());

		ThreadGroup tg = Thread.currentThread().getThreadGroup();

		while (tg != null) {
			((RobocodeSecurityManager) System.getSecurityManager()).addSafeThreadGroup(tg);
			tg = tg.getParent();
		}
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
		manager.getBattleManager().startNewBattle(battle, false);
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
}
