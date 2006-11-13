/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - GUI is disabled per default. If the setVisible() is called, the GUI will
 *       be enabled. The close() method is only calling dispose() on the
 *       RobocodeFrame if the GUI is enabled
 *     - Access to managers is now static
 *     - Code cleanup
 *******************************************************************************/
package robocode.control;


import java.io.*;
import java.security.*;
import java.util.Vector;

import robocode.manager.*;
import robocode.repository.*;
import robocode.security.*;
import robocode.util.*;


/**
 * RobocodeEngine - Class for controlling Robocode.
 * 
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobocodeEngine {
	private RobocodeListener listener;
	
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
		File robocodeDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile();
		File robotsDir = new File(robocodeDir, "robots");

		if (robotsDir.exists()) {
			init(robocodeDir, listener);
		} else {
			throw new RuntimeException("File not found: " + robotsDir);
		}
	}
	
	private void init(File robocodeHome, RobocodeListener listener) {
		this.listener = listener;

		RobocodeManager.setSlave(true);
		RobocodeManager.setListener(listener);

		try {
			Constants.setWorkingDirectory(robocodeHome);
		} catch (IOException e) {
			System.err.println(e);
			return;
		}
		Thread.currentThread().setName("Application Thread");
		RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());

		Policy.setPolicy(securityPolicy);
		System.setSecurityManager(new RobocodeSecurityManager(Thread.currentThread(), true));

		ThreadGroup tg = Thread.currentThread().getThreadGroup();

		while (tg != null) {
			((RobocodeSecurityManager) System.getSecurityManager()).addSafeThreadGroup(tg);
			tg = tg.getParent();
		}

		SecurePrintStream sysout = new SecurePrintStream(System.out, true, "System.out");
		SecurePrintStream syserr = new SecurePrintStream(System.err, true, "System.err");
		SecureInputStream sysin = new SecureInputStream(System.in, "System.in");

		System.setOut(sysout);
		if (!System.getProperty("debug", "false").equals("true")) {
			System.setErr(syserr);
		}
		System.setIn(sysin);
	}
	
	/**
	 * Call this when you are finished with this RobocodeEngine.
	 * This method disposes the Robocode window
	 */
	public void close() {
		if (RobocodeManager.isGUIEnabled()) {
			WindowManager.getRobocodeFrame().dispose();
		}
	}
	
	/**
	 * Returns the installed version of Robocode.
	 * 
	 * @return the installed version of Robocode.
	 */
	public String getVersion() {
		return VersionManager.getVersion();
	}

	/**
	 * Shows or hides the Robocode window.
	 */
	public void setVisible(boolean visible) {
		RobocodeManager.setEnableGUI(true);
		WindowManager.getRobocodeFrame().setVisible(visible);
	}
		
	/**
	 * Gets a list of robots available for battle.
	 * 
	 * @return An array of all available robots.
	 */
	public RobotSpecification[] getLocalRepository() {
		Vector<FileSpecification> v = RobotRepositoryManager.getRobotRepository().getRobotSpecificationsVector(false,
				false, true, false, false, true);
		RobotSpecification robotSpecs[] = new RobotSpecification[v.size()];

		for (int i = 0; i < robotSpecs.length; i++) {
			robotSpecs[i] = new RobotSpecification((robocode.repository.RobotSpecification) v.elementAt(i));
		}
		return robotSpecs;
	}

	/**
	 * Runs a battle
	 */
	public void runBattle(BattleSpecification battle) {
		Utils.setLogListener(listener);
		BattleManager.startNewBattle(battle);
	}

	/**
	 * Asks a battle to abort.
	 */
	public void abortCurrentBattle() {
		BattleManager.stop(false);
	}
}
