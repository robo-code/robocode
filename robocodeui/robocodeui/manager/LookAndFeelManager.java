/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial API and implementation
 *******************************************************************************/
package robocodeui.manager;


import javax.swing.UIManager;

import robocode.ui.ILookAndFeelManager;
import robocode.manager.RobocodeManager;


/**
 * Manager for setting the Look and Feel of the Robocode GUI.
 *
 * @author Flemming N. Larsen (original)
 */
public class LookAndFeelManager implements ILookAndFeelManager {

	/**
	 * Sets the Look and Feel (LAF). This method first try to set the LAF to the
	 * system's LAF. If this fails, it try to use the cross platform LAF.
	 * If this also fails, the LAF will not be changed.
	 */
	public void setLookAndFeel() {
        SetLookAndFeel();
    }

    public static void SetLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {
			// For some reason Ubuntu 7 can cause a NullPointerException when trying to getting the LAF
			System.err.println("Could not set the Look and Feel (LAF).  The default LAF is used instead");
		}
	}

    public void setRobocodeManager(RobocodeManager robocodeManager) {
    }
}
