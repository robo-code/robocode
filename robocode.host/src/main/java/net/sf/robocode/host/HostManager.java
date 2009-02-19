/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.host;


import net.sf.robocode.host.proxies.*;
import net.sf.robocode.host.security.*;
import net.sf.robocode.io.Logger;
import net.sf.robocode.peer.IRobotPeer;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.repository.RobotType;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.core.Container;
import robocode.control.RobotSpecification;

import java.io.InputStream;
import java.io.PrintStream;
import java.security.AccessControlException;


/**
 * @author Pavel Savara (original)
 */
public class HostManager implements IHostManager {
	private final ISettingsManager properties;
	private IThreadManager threadManager;

	public HostManager(ISettingsManager properties, IThreadManager threadManager) {
		this.properties = properties;
		this.threadManager = threadManager;
	}

	static {
		initStreams();
	}

	private static void initStreams() {
		PrintStream sysout = new SecurePrintStream(System.out, true);
		PrintStream syserr = new SecurePrintStream(System.err, true);
		InputStream sysin = new SecureInputStream(System.in);

		System.setOut(sysout);
		if (!System.getProperty("debug", "false").equals("true")) {
			System.setErr(syserr);
		}
		System.setIn(sysin);
	}

	public long getRobotFilesystemQuota() {
		return properties.getRobotFilesystemQuota();
	}

	public IThreadManager getThreadManager() {
		return threadManager;
	}

	public void resetThreadManager() {
		threadManager.reset();
	}

	public void addSafeThread(Thread safeThread) {
		threadManager.addSafeThread(safeThread);
	}

	public void removeSafeThread(Thread safeThread) {
		threadManager.removeSafeThread(safeThread);
	}

	public PrintStream getRobotOutputStream() {
		return threadManager.getRobotOutputStream();
	}

	public IHostingRobotProxy createRobotProxy(RobotSpecification robotSpecification, RobotStatics statics, IRobotPeer peer) {
		IHostingRobotProxy robotProxy;
		final IRobotRepositoryItem specification = (IRobotRepositoryItem) HiddenAccess.getFileSpecification(
				robotSpecification);

		if (specification.isTeamRobot()) {
			robotProxy = new TeamRobotProxy(specification, this, peer, statics);
		} else if (specification.isAdvancedRobot()) {
			robotProxy = new AdvancedRobotProxy(specification, this, peer, statics);
		} else if (specification.isStandardRobot()) {
			robotProxy = new StandardRobotProxy(specification, this, peer, statics);
		} else if (specification.isJuniorRobot()) {
			robotProxy = new JuniorRobotProxy(specification, this, peer, statics);
		} else {
			throw new AccessControlException("Unknown robot type");
		}
		return robotProxy;
	}

	public void cleanup() {// TODO
	}

	public IRobotClassLoader createLoader(IRobotRepositoryItem robotRepositoryItem) {
		final IHost host = (IHost) Container.cache.getComponent(
				"robocode.host." + robotRepositoryItem.getRobotLanguage());

		return host.createLoader(robotRepositoryItem);
	}

	public String[] getReferencedClasses(IRobotRepositoryItem robotRepositoryItem) {
		IRobotClassLoader loader = null;

		try {
			loader = createLoader(robotRepositoryItem);
			loader.loadRobotMainClass(true);
			return loader.getReferencedClasses();

		} catch (ClassNotFoundException e) {
			Logger.logError(e);
			return new String[0]; 
		} finally {
			if (loader != null) {
				loader.cleanup();
			}
		}
	}

	public RobotType getRobotType(IRobotRepositoryItem robotRepositoryItem, boolean resolve, boolean message) {
		final IHost host = (IHost) Container.cache.getComponent(
				"robocode.host." + robotRepositoryItem.getRobotLanguage());

		return host.getRobotType(robotRepositoryItem, resolve, message);
	}

	public void initSecurity() {
		new RobocodeSecurityPolicy(threadManager);
		new RobocodeSecurityManager(threadManager);
	}

}
