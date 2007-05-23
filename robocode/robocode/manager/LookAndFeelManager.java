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
package robocode.manager;


import javax.swing.UIManager;


/**
 * Manager for setting the Look and Feel of the Robocode GUI.
 *
 * @author Flemming N. Larsen (original)
 */
public class LookAndFeelManager {

	/**
	 * Sets the Look and Feel (LAF). This method first try to set the LAF to the
	 * system's LAF. If this fails, it try to use the cross platform LAF.
	 * If this also fails, the LAF will not be changed.
	 */
	public static void setLookAndFeel() {
		try {
			String laf = UIManager.getSystemLookAndFeelClassName();
			if (laf == null) {
				laf = UIManager.getCrossPlatformLookAndFeelClassName();
			}
			if (laf != null) {
				try {
					UIManager.setLookAndFeel(laf);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// For some reason Ubuntu 7 can cause a NullPointerException when trying to getting the LAF
			System.err.println("Could not set the Look and Feel (LAF) on this operating system.");
			System.err.println("The default LAF is used instead");
		}
	}
}
