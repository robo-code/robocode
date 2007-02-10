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
 *     - Rewritten
 *******************************************************************************/
package robocode.manager;


import java.io.File;
import java.io.IOException;

import robocode.io.FileUtil;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BrowserManager {

	private static String browserCommand;

	static {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			browserCommand = "rundll32 url.dll, FileProtocolHandler";
		} else {
			browserCommand = FileUtil.quoteFileName(FileUtil.getCwd() + File.separator + "browser.sh");
		}
	}

	public static void openURL(String url) throws IOException {
		url = FileUtil.quoteFileName(url);

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
