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
package robocode;

import javax.swing.*;
import java.io.*;
import java.security.*;

import robocode.util.*;
import robocode.manager.*;
import robocode.security.*;

/**
 * Robocode - A programming game involving battling AI tanks.<BR/>
 * Copyright 2001 Mathew Nelson
 *
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 * @author Mathew A. Nelson
 */
public class Robocode {
	private RobocodeManager manager = null;

/**
 * Use the command-line to start Robocode.
 * The command is:  java -jar robocode.jar
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	
	Robocode robocode = new Robocode();
	
	robocode.initialize(args);

/*	
	new Thread(new Runnable()
		{
			public void run()
			{
				int i = 0;
				while (true)
				{
					try {Thread.sleep(1000);} catch (InterruptedException e) {}
					System.err.println("ok " + i++);
				}
			}
		}
		).start();
		*/
}

private Robocode() {
}

private boolean initialize(String args[]) {
	//System.out.println(getClass().getProtectionDomain().getCodeSource().getLocation());
	try {
		manager = new RobocodeManager(false,null);
		
		// Set native look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		if (System.getProperty("WORKINGDIRECTORY") != null)
		{
			Constants.setWorkingDirectory(new File(System.getProperty("WORKINGDIRECTORY")));
		}
		if (System.getProperty("SINGLEBUFFER","false").equals("true"))
		{
			javax.swing.RepaintManager.currentManager(manager.getWindowManager().getRobocodeFrame()).setDoubleBufferingEnabled(false);
		}
		
		
		Thread.currentThread().setName("Application Thread");
		//System.out.println(Policy.getPolicy().getPermissions(new CodeSource(null,null)) + ""); //.add(new AllPermission());
		
		//Policy.getPolicy().getPermissions(getClass().getProtectionDomain().getCodeSource()).add(new AllPermission());

		//System.out.println(""+getClass().getProtectionDomain().getPermissions()); //.add(new AllPermission());
		//if(true)System.exit(0);
		
//		System.out.println("code source is " + getClass().getProtectionDomain().getCodeSource());
//		System.out.println("other code source is " + listener.getClass().getProtectionDomain().getCodeSource());
		
		
		// This allows us, in Java 1.4, to use a custom 'classpath' policy.
		RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());
		Policy.setPolicy(securityPolicy);
		
		// For John Burkey at Apple
		boolean securityOn = true;
		if (System.getProperty("NOSECURITY","false").equals("true"))
		{
			Utils.messageWarning("Robocode is running without a security manager.\n" +
			"Robots have full access to your system.\n" +
			"You should only run robots which you trust!");
			securityOn = false;
		}
		if (securityOn)
		{
			System.setSecurityManager(new RobocodeSecurityManager(Thread.currentThread(),manager.getThreadManager(),true));

			RobocodeFileOutputStream.setThreadManager(manager.getThreadManager());

			ThreadGroup tg = Thread.currentThread().getThreadGroup();
			while (tg != null)
			{
				((RobocodeSecurityManager)System.getSecurityManager()).addSafeThreadGroup(tg);
				tg = tg.getParent();
			}
		}

		SecurePrintStream sysout = new SecurePrintStream(System.out,true,"System.out");
		SecurePrintStream syserr = new SecurePrintStream(System.err,true,"System.err");
		SecureInputStream sysin = new SecureInputStream(System.in,"System.in");
		System.setOut(sysout);
		if (System.getProperty("debug","false").equals("true"));
		else
			System.setErr(syserr);
		System.setIn(sysin);

		boolean noDisplay = false;
		boolean minimize = false;
		String battleFilename = null;
		String resultsFilename = null;
		int fps = 10000;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-cwd") && (i < args.length + 1))
			{
				Constants.setWorkingDirectory(new File(args[i+1]));
				i++;
			}
			else if (args[i].equals("-battle") && (i < args.length + 1))
			{
				battleFilename = args[i+1];
				i++;
			}
			else if (args[i].equals("-results") && (i < args.length + 1))
			{
				resultsFilename = args[i+1];
				i++;
			}
			else if (args[i].equals("-fps") && (i < args.length + 1))
			{
				fps = Integer.parseInt(args[i+1]);
				i++;
			}
			else if (args[i].equals("-minimize"))
			{
				minimize = true;
			}
			else if (args[i].equals("-nodisplay"))
			{
				noDisplay = true;
				//manager.getWindowManager().getRobocodeFrame().setVisible(false);
			}
			else if (args[i].equals("-?") || args[i].equals("-help"))
			{
				printUsage();
				System.exit(0);
			}
			else {
				System.out.println("Not understood: " + args[i]);
				printUsage();
				System.exit(8);
			}
		}
		File robots = new File(Constants.cwd(),"robots");
		if (!robots.exists() || !robots.isDirectory())
		{
			System.err.println(new File(Constants.cwd(),"").getAbsolutePath() + " is not a valid directory to start Robocode in.");
			System.exit(8);
		}

		if (battleFilename != null)
		{
			if (resultsFilename != null)
				manager.getBattleManager().setResultsFile(resultsFilename);
			manager.getBattleManager().setBattleFilename(battleFilename);
			manager.getBattleManager().loadBattleProperties();
			manager.getBattleManager().startNewBattle(manager.getBattleManager().getBattleProperties(),true);
			// I don't think this works... doesn't matter.
			// New functionality as of 0.98.3 is, when minimized, fps is max
			manager.getBattleManager().getBattle().setOptimalFPS(fps);
		}
		if (noDisplay)
		{
			return true;
		}

		//manager.getWindowManager().getRobocodeFrame().setState(JFrame.ICONIFIED);
		//manager.getWindowManager().showRobocodeFrame();
		
		if (!minimize && battleFilename == null)
			manager.getWindowManager().showSplashScreen();
		manager.getWindowManager().showRobocodeFrame();
		if (!minimize)
			manager.getVersionManager().checkUpdateCheck();
		if (minimize)
			manager.getWindowManager().getRobocodeFrame().setState(JFrame.ICONIFIED);
		//else
		//	manager.getWindowManager().getRobocodeFrame().setState(JFrame.NORMAL);

		if (!manager.getProperties().getLastRunVersion().equals(manager.getVersionManager().getVersion()))
		{
			manager.getProperties().setLastRunVersion(manager.getVersionManager().getVersion());
			manager.saveProperties();
			manager.runIntroBattle();
		}
			
		return true;
	} catch (Throwable e) {
		log(e);
		return false;
	}

}

private void log(String s) {
	Utils.log(s);
}

private void log(Throwable e) {
	Utils.log(e);
}

/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:50:58 PM)
 */
private void printUsage() {
	System.out.println("Usage: robocode [-cwd directory] [-battle filename [-results filename] [-fps fps] [-minimize]]");
}


}