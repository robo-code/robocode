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
package robocode.ui;


/**
 * @author Pavel Savara (original)
 */
public interface IWindowManager extends ILoadableManager {
	IRobocodeFrame getRobocodeFrame();
	void showResultsDialog();
	void showSplashScreen();
	void showRobocodeFrame(boolean value);

    /**
     * Sets the Look and Feel (LAF). This method first try to set the LAF to the
     * system's LAF. If this fails, it try to use the cross platform LAF.
     * If this also fails, the LAF will not be changed.
     */
	void setLookAndFeel();
}
