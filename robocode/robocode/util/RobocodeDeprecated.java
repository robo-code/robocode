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
package robocode.util;


import robocode.peer.robot.*;


/**
 * Insert the type's description here.
 * Creation date: (9/10/2001 5:21:04 PM)
 * @author: Administrator
 */
public class RobocodeDeprecated {

	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 5:21:30 PM)
	 * @param t java.lang.Thread
	 */
	public static void stopThread(RobotThreadManager r, Thread t) {

		/*
		 boolean _jvm14bug = false;
		 String v = System.getProperty("java.version");
		 if (v.indexOf("1.4.0") == 0 && v.indexOf("beta3") > 0)
		 {
		 _jvm14bug = true;
		 }
		 if (_jvm14bug)
		 {
		 log("Warning!:  Cannot stop threads with Java 1.4.0-beta3.");
		 r.setDisabled(true);
		 }
		 else
		 {
		 */
		// !!! Synchronizing this fixes the jvm hang bug sometimes
		synchronized (t) {
			t.stop();
		}
		// }
	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	private static void log(String s) {
		Utils.log(s);
	}
}
