/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
package net.sf.robocode.ui;


import net.sf.robocode.io.FileUtil;

import java.io.IOException;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BrowserManager {

	private static final String browserCommand;

	static {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			browserCommand = "rundll32 url.dll, FileProtocolHandler";
		} else {
			browserCommand = "browser.sh";
		}
	}

	public static void openURL(String url) throws IOException {
		url = FileUtil.quoteFileName(url);

		final String command = browserCommand + ' ' + url;

		ProcessBuilder pb = new ProcessBuilder(command.split(" "));

		pb.directory(FileUtil.getCwd());
		Process p = pb.start();

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();
		}

		if (p.exitValue() < 0) {
			throw new IOException(
					"Unable to launch " + browserCommand + ".  Please check it, or launch " + url + " in your browser.");
		}
	}
}
