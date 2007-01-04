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
 *******************************************************************************/
package robocode.util;


import robocode.peer.robot.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobocodeDeprecated {

	@SuppressWarnings("deprecation")
	public static void stopThread(RobotThreadManager r, Thread t) {
		// !!! Synchronizing this fixes the jvm hang bug sometimes
		synchronized (t) {
			t.stop();
		}
	}
}
