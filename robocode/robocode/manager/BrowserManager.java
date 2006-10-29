/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten
 *******************************************************************************/
package robocode.manager;


import java.io.IOException;
import java.io.File;

import robocode.util.Utils;
import robocode.util.Constants;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class BrowserManager {

	private static String browserCommand;
	
	static {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			browserCommand = "rundll32 url.dll, FileProtocolHandler";
		} else {
			browserCommand = Utils.quoteFileName(Constants.cwd() + File.separator + "browser.sh");
		}
		System.out.println("browserCommand: " + browserCommand);
	}

	public static void openURL(String url) throws IOException {
		url = Utils.quoteFileName(url);

		System.out.println("url: " + url);

		Process p = Runtime.getRuntime().exec(browserCommand + " " + url);

		try {
			p.waitFor();
		} catch (InterruptedException e) {}

		if (p.exitValue() < 0) {
			throw new IOException(
					"Unable to launch " + browserCommand + ".  Please check it, or launch " + url + " in your browser.");
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
	}
}