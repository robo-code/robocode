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


import net.sf.robocode.IRobocodeManager;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.proxies.*;
import net.sf.robocode.host.security.*;
import net.sf.robocode.peer.*;
import net.sf.robocode.repository.IRobotFileSpecification;
import net.sf.robocode.repository.IRobotFileSpecificationExt;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.serialization.RbSerializer;
import robocode.control.RobotSpecification;

import java.io.InputStream;
import java.io.PrintStream;
import java.security.AccessControlException;
import java.security.Policy;


/**
 * @author Pavel Savara (original)
 */
public class HostManager implements IHostManager {
	private final IRobocodeManager manager;
	private IThreadManager threadManager;

	public HostManager() {
		this.manager = Container.instance.getComponent(IRobocodeManager.class);
		threadManager = new ThreadManager();
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
		return manager.getProperties().getRobotFilesystemQuota();
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
		final IRobotFileSpecification specification = (IRobotFileSpecification) HiddenAccess.getFileSpecification(
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

	static {
		RbSerializer.register(ExecCommands.class, RbSerializer.ExecCommands_TYPE);
		RbSerializer.register(BulletCommand.class, RbSerializer.BulletCommand_TYPE);
		RbSerializer.register(TeamMessage.class, RbSerializer.TeamMessage_TYPE);
		RbSerializer.register(DebugProperty.class, RbSerializer.DebugProperty_TYPE);
		RbSerializer.register(ExecResults.class, RbSerializer.ExecResults_TYPE);
		RbSerializer.register(BulletStatus.class, RbSerializer.BulletStatus_TYPE);
	}

	public void cleanup() {// TODO
	}

	public Class<?> loadRobotClass(IRobotFileSpecification robotFileSpecification) throws ClassNotFoundException {
		RobotClassLoader classLoader = new RobotClassLoader(robotFileSpecification);

		classLoader.loadRobotClass();
		Class<?> robotClass = classLoader.loadRobotClass();

		((IRobotFileSpecificationExt) robotFileSpecification).setUid(classLoader.getUid());
		return robotClass;
	}

	public void initSecurity(boolean securityOn, boolean experimentalOn) {
		Thread.currentThread().setName("Application Thread");

		RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());

		Policy.setPolicy(securityPolicy);

		RobocodeSecurityManager securityManager = new RobocodeSecurityManager(threadManager, securityOn, experimentalOn);

		System.setSecurityManager(securityManager);

		if (securityOn) {
			ThreadGroup tg = Thread.currentThread().getThreadGroup();

			while (tg != null) {
				threadManager.addSafeThreadGroup(tg);
				tg = tg.getParent();
			}
		}
	}

}
