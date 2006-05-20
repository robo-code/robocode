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

/*
 * Created on Feb 16, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package robocode.manager;


import java.io.IOException;
import java.io.File;
import robocode.util.Constants;


/**
 * @author mat
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BrowserManager {
	
	RobocodeManager manager;
	String browserCommand;
	
	public BrowserManager(RobocodeManager manager) {
		this.manager = manager;
		if (File.separatorChar == '/') {
			browserCommand = Constants.cwd() + File.separator + "browser.sh";
		} else {
			browserCommand = Constants.cwd() + File.separator + "browser.bat";
		}
	}
	
	public void openURL(String url) throws IOException {
		Process p = Runtime.getRuntime().exec(browserCommand + " " + url);

		try {
			p.waitFor();
		} catch (InterruptedException e) {}

		if (p.exitValue() != 0) {
			throw new IOException(
					"Unable to launch " + browserCommand + ".  Please check it, or launch " + url + " in your browser.");
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
	}
}
