/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.control;

import java.io.*;
import java.security.*;

import robocode.security.*;
import robocode.*;

import robocode.manager.*;

/**
 * RobocodeEngine - Class for controlling Robocode.
 * Copyright 2001 Mathew A. Nelson
 *
 * <p>Requires Java 1.4
 * 
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 * @author Mathew A. Nelson
 */
public class RobocodeEngine {
	private RobocodeListener listener = null;
	private RobocodeManager manager = null;
	
	private RobocodeEngine()
	{
	}
	
	/**
	 * Creates a new RobocodeEngine
	 * @param robocodeHome should be the root robocode directory (i.e. c:\robocode)
	 * @param listener Your listener
	 */
	public RobocodeEngine(File robocodeHome, RobocodeListener listener)
	{
		init(robocodeHome,listener);
	}
	
	/**
	 * Creates a new RobocodeEngine using robocode.jar to determine the robocodeHome file.
	 * @param listener Your listener
	 */
	public RobocodeEngine(RobocodeListener listener)
	{
		File robocodeDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile();
		File robotsDir = new File(robocodeDir,"robots");
		if (robotsDir.exists())
		{
			init(robocodeDir,listener);
		}
		else
			throw new RuntimeException("File not found: " + robotsDir);
	}
	
	private void init(File robocodeHome, RobocodeListener listener)
	{
		this.listener = listener;
		manager = new RobocodeManager(true,listener);
		
		try {
			robocode.util.Constants.setWorkingDirectory(robocodeHome);
		} catch (IOException e) {
			System.err.println(e);
			return;
		}
		Thread.currentThread().setName("Application Thread");
//		Policy.getPolicy().getPermissions(new CodeSource(null,null)).add(new AllPermission());
//		Policy.getPolicy().getPermissions(getClass().getProtectionDomain().getCodeSource()).add(new AllPermission());
//		System.out.println("code source is " + getClass().getProtectionDomain().getCodeSource());
//		System.out.println("other code source is " + listener.getClass().getProtectionDomain().getCodeSource());
		RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());
		Policy.setPolicy(securityPolicy);
		System.setSecurityManager(new RobocodeSecurityManager(Thread.currentThread(),manager.getThreadManager(),true));
		RobocodeFileOutputStream.setThreadManager(manager.getThreadManager());

		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		while (tg != null)
		{
			((RobocodeSecurityManager)System.getSecurityManager()).addSafeThreadGroup(tg);
			tg = tg.getParent();
		}

		SecurePrintStream sysout = new SecurePrintStream(System.out,true,"System.out");
		SecurePrintStream syserr = new SecurePrintStream(System.err,true,"System.err");
		SecureInputStream sysin = new SecureInputStream(System.in,"System.in");
		System.setOut(sysout);
		if (System.getProperty("debug","false").equals("true"));
		else
			System.setErr(syserr);
		System.setIn(sysin);

	}
	
	/**
	 * Call this when you are finished with this RobocodeEngine.
	 * This method disposes the Robocode window
	 */
	public void close()
	{
		//robocode.util.Utils.saveWindowPositions();
		manager.getWindowManager().getRobocodeFrame().dispose();
	}
	
	/**
	 * Returns the installed version of Robocode.
	 * @return the installed version of Robocode.
	 */
	public String getVersion()
	{
		return manager.getVersionManager().getVersion();
	}

	/**
	 * Shows or hides the Robocode window.
	 */
	public void setVisible(boolean visible)
	{
		manager.getWindowManager().getRobocodeFrame().setVisible(visible);
	}
		
	/**
	 * Gets a list of robots available for battle.
	 * @return An array of all available robots.
	 */
	public RobotSpecification[] getLocalRepository()
	{
		robocode.repository.Repository robotRepository = manager.getRobotRepositoryManager().getRobotRepository();
		robocode.repository.FileSpecificationVector v = robotRepository.getRobotSpecificationsVector(false,false,true,false,false,true);

		RobotSpecification robotSpecs[] = new RobotSpecification[v.size()];
		for (int i = 0; i < robotSpecs.length; i++)
		{
			robotSpecs[i] = new RobotSpecification((robocode.repository.RobotSpecification)v.elementAt(i));
			/*
			robocode.local.FileSpecification robotSpecification = (robocode.local.FileSpecification)robotRepository.getRobotSpecificationsVector(false,false,true,false,false,true).elementAt(i);
			if (robotSpecification.isDevelopmentVersion() && robotSpecification.getVersion() != null)
				robotSpecs[i] = new RobotSpecification(robotSpecification.getNameManager().getFullClassName(),robotSpecification.getVersion() + "*");
			else
				robotSpecs[i] = new RobotSpecification(robotSpecification.getNameManager().getFullClassName(),robotSpecification.getVersion());
				*/
		}
		return robotSpecs;
	}
	/**
	 * Runs a battle
	 */
	public void runBattle(BattleSpecification battle)
	{
		robocode.util.Utils.setLogListener(listener);
		manager.getBattleManager().startNewBattle(battle);
	}
	/**
	 * Asks a battle to abort.
	 */
	public void abortCurrentBattle()
	{
		manager.getBattleManager().stop(false);
	}
  
}

