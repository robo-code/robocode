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
import net.sf.robocode.host.jarjar.JarJarURLConnection;
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
		PrintStream sysout = new SecurePrintStream(Logger.realOut, true);
		PrintStream syserr = new SecurePrintStream(Logger.realErr, true);
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

	public void cleanup() {// TODO
	}

	public String[] getReferencedClasses(IRobotRepositoryItem robotRepositoryItem) {
		return getHost(robotRepositoryItem).getReferencedClasses(robotRepositoryItem);
	}

	public RobotType getRobotType(IRobotRepositoryItem robotRepositoryItem, boolean resolve, boolean message) {
		return getHost(robotRepositoryItem).getRobotType(robotRepositoryItem, resolve, message);
	}

	public IHostingRobotProxy createRobotProxy(RobotSpecification robotSpecification, RobotStatics statics, IRobotPeer peer) {
		final IRobotRepositoryItem specification = (IRobotRepositoryItem) HiddenAccess.getFileSpecification(
				robotSpecification);

		return getHost(specification).createRobotProxy(this, robotSpecification, statics, peer);
	}

	private IHost getHost(IRobotRepositoryItem robotRepositoryItem) {
		return (IHost) Container.cache.getComponent("robocode.host." + robotRepositoryItem.getRobotLanguage());
	}

	public void initSecurity() {
		JarJarURLConnection.register();
		new RobocodeSecurityPolicy(threadManager);
		new RobocodeSecurityManager(threadManager);
	}

}
