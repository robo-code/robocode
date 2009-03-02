/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode.animators;


import java.security.Policy;

import javax.swing.JFrame;
import javax.swing.UIManager;

import net.sf.robocode.bv3d.MVCManager;
import robocode.RobocodeFileOutputStream;

/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

/*
 // TODO this needs to be revisited, probably rewritten from scratch
 // TODO because we would like to be inprocess battleView and we are no more sending XML
 // TODO we rather send snapshost to any subscriber who inherit from 
 public class RobocodeLiveAnimator extends NetXMLAnimator4Robocode{
 
 private static RobocodeManager rbmanager;
 
 public RobocodeLiveAnimator( MVCManager manager ){
 super( manager );
 this.diplayConfigInfo = false;
 rbmanager = robocodeSetup();
 rbmanager.getWindowManager().showSplashScreen();
 JFrame rbFrame = rbmanager.getWindowManager().getRobocodeFrame();
 rbFrame.setBounds(
 manager.getMainFrame().getWidth()+1,
 0,
 (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()-manager.getMainFrame().getWidth()+1,
 manager.getMainFrame().getHeight());
 rbmanager.getWindowManager().showRobocodeFrame( true );	
 }
 
 private RobocodeManager robocodeSetup() {
 RobocodeManager manager = null;

 try {
 manager = new RobocodeManager(false, null);

 // Set native look and feel
 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

 //            Constants.setWorkingDirectory(new File(System.getProperty("user.dir")));

 Thread.currentThread().setName("Application Thread");

 // This allows us, in Java 1.4, to use a custom 'classpath' policy.
 RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(
 Policy.getPolicy());

 Policy.setPolicy(securityPolicy);

 System.setSecurityManager(new RobocodeSecurityManager(Thread
 .currentThread(), manager.getThreadManager(), false, false));
 RobocodeFileOutputStream.setThreadManager(manager
 .getThreadManager());

 ThreadGroup tg = Thread.currentThread().getThreadGroup();

 while (tg != null) {
 ((RobocodeSecurityManager) System.getSecurityManager())
 .addSafeThreadGroup(tg);

 tg = tg.getParent();
 }

 //            SecurePrintStream sysout = new SecurePrintStream(System.out, true,
 //                    "System.out");
 //            SecurePrintStream syserr = new SecurePrintStream(System.err, true,
 //                    "System.err");
 //            SecureInputStream sysin = new SecureInputStream(System.in,
 //                    "System.in");

 //            System.setOut(sysout);
 //            System.setErr(syserr);
 //            System.setIn(sysin);

 //            File robots = new File(Constants.cwd(), "robots");
 //
 //            if (!robots.exists() || !robots.isDirectory()) {
 //                System.err.println(new File(Constants.cwd(), "")
 //                        .getAbsolutePath()
 //                        + " is not a valid directory to start Robocode in.");
 //                System.exit(8);
 //            }

 //            manager.getWindowManager().showSplashScreen();
 //            manager.getWindowManager().showRobocodeFrame( true );
 //			if (!manager.getProperties().getLastRunVersion().equals(manager.getVersionManager().getVersion())) {
 //				manager.getProperties().setLastRunVersion(manager.getVersionManager().getVersion());
 //				manager.saveProperties();
 //				manager.runIntroBattle();
 //			}
 //            manager.getWindowManager().getRobocodeFrame().setState(
 //                    JFrame.ICONIFIED);

 //            try {
 //
 //                // Let robocode complete its own startup
 //                Thread.sleep(1000);
 //            } catch (InterruptedException e) {
 //                e.printStackTrace();
 //            }

 //            manager.setListener(this);

 return manager;
 
 } catch (Throwable e) {
 e.printStackTrace();

 return null;
 }
 }
 }*/
