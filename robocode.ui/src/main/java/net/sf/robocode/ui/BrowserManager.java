/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
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
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BrowserManager {

	public static void openURL(String url) throws IOException {

		// Check if the JVM is version 1.6.0 or higher
		if (System.getProperty("java.version").charAt(2) >= '6') {
			// Try calling java.awt.Desktop.getDesktop().browse(new URI(url)) that is available from Java 6
			try {
				Class<?> desktopClass = Class.forName("java.awt.Desktop"); 
				Object desktop = desktopClass.getDeclaredMethod("getDesktop", (Class<?>[]) null).invoke((Object) null,
						(Object[]) null);

				desktopClass.getDeclaredMethod("browse", URI.class).invoke(desktop, new URI(url));
				return; // leave

			} catch (ClassNotFoundException e) {} catch (SecurityException e) {} catch (NoSuchMethodException e) {} catch (IllegalArgumentException e) {} catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (URISyntaxException e) {}
			// If calling java.awt.Desktop.getDesktop().browse(new URI(url)) fails, we fall down to the code below
		}

		url = FileUtil.quoteFileName(url);

		Runtime rt = Runtime.getRuntime();
		String os = System.getProperty("os.name").toLowerCase();

		if (os.startsWith("windows")) {
			rt.exec("rundll32 url.dll, FileProtocolHandler " + url);

		} else if (os.startsWith("mac")) {
			rt.exec("open " + url);

		} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
			// Do a best guess on Unix until we get a platform independent way.
			// Build a list of browsers to try, in this order.
			final String[] browsers = {
				"epiphany", "firefox", "mozilla", "konqueror", "galeon", "netscape", "opera", "links", "lynx" };

			// Build a command string which looks like
			// "browser1 "url" || browser2 "url" || ..."
			StringBuffer cmd = new StringBuffer();

			for (int i = 0; i < browsers.length; i++) {
				cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");
			}

			rt.exec(new String[] { "sh", "-c", cmd.toString() });
		}
	}
}
