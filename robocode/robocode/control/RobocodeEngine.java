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
 *     - Replaced FileSpecificationVector with plain Vector
 *     - GUI is disabled per default. If the setVisible() is called, the GUI will
 *       be enabled. The close() method is only calling dispose() on the
 *       RobocodeFrame if the GUI is enabled
 *     - Updated to use methods from FileUtil and Logger, which replaces
 *       methods that have been (re)moved from the robocode.util.Utils class
 *     - Changed to use FileUtil.getRobotsDir()
 *     - Modified getLocalRepository() to support teams by using
 *       FileSpecification instead of RobotSpecification
 *     - System.out, System.err, and System.in is now only set once, as new
 *       instances of the RobocodeEngine causes memory leaks with
 *       System.setOut() and System.setErr()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Bugfix: Inconsistent Behavior of RobocodeEngine.setVisible()
 *     - Bugfix: Added cleanup of the Robocode manager to the close() method
 *******************************************************************************/
package robocode.control;


import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.Policy;
import java.util.List;

import robocode.RobocodeFileOutputStream;
import robocode.io.FileUtil;
import robocode.io.Logger;
import robocode.manager.RobocodeManager;
import robocode.repository.Repository;
import robocode.repository.IFileSpecification;
import robocode.security.RobocodeSecurityManager;
import robocode.security.RobocodeSecurityPolicy;
import robocode.security.SecureInputStream;
import robocode.security.SecurePrintStream;


/**
 * RobocodeEngine - Class for controlling Robocode.
 * 
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
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
		System.setOut(sysout);
		if (!System.getProperty("debug", "false").equals("true")) {
			System.setErr(syserr);
		}
		System.setIn(sysin);
	}

	/**
	 * Creates a new RobocodeEngine
	 * 
	 * @param robocodeHome should be the root robocode directory (i.e. c:\robocode)
	 * @param listener Your listener
	 */
	public RobocodeEngine(File robocodeHome, RobocodeListener listener) {
		init(robocodeHome, listener);
	}

	/**
	 * Creates a new RobocodeEngine using robocode.jar to determine the robocodeHome file.
	 * @param listener Your listener
	 */
	public RobocodeEngine(RobocodeListener listener) {
		File robotsDir = FileUtil.getRobotsDir();

		if (robotsDir.exists()) {
			init(FileUtil.getCwd(), listener);
		} else {
			throw new RuntimeException("File not found: " + robotsDir);
		}
	}

	@Override
	public void finalize() {
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
	 * Call this when you are finished with this RobocodeEngine.
	 * This method disposes the Robocode window
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
	 */
	public void setVisible(boolean visible) {
		if (visible && !manager.isGUIEnabled()) {
			// The GUI must be enabled in order to show the window
			manager.setEnableGUI(true);

			// Set the Look and Feel (LAF)
			manager.getWindowManager().setLookAndFeel();
			// TODO ZAMO .NET UI EDT
		}

		if (manager.isGUIEnabled()) {
			manager.getWindowManager().showRobocodeFrame(visible);
			manager.getProperties().setOptionsCommonShowResults(visible);
		}
	}

	/**
	 * Gets a list of robots available for battle.
	 * 
	 * @return An array of all available robots.
	 */
	public RobotBattleSpecification[] getLocalRepository() {
		Repository robotRepository = manager.getRobotRepositoryManager().getRobotRepository();
		List<IFileSpecification> list = robotRepository.getRobotSpecificationsList(false, false, false, false, false,
				false);
		RobotBattleSpecification robotSpecs[] = new RobotBattleSpecification[list.size()];

		for (int i = 0; i < robotSpecs.length; i++) {
			robotSpecs[i] = new RobotBattleSpecification(list.get(i));
		}
		return robotSpecs;
	}

	/**
	 * Runs a battle
	 */
	public void runBattle(BattleSpecification battle) {
		Logger.setLogListener(listener);

		manager.getBattleManager().startNewBattle(battle, false);
	}

	/**
	 * Asks a battle to abort.
	 */
	public void abortCurrentBattle() {
		manager.getBattleManager().stop();
	}
}
