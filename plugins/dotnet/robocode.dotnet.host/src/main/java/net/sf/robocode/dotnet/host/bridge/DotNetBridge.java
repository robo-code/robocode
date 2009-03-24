/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.dotnet.host.bridge;


import net.sf.robocode.io.Logger;

import java.io.File;
import java.io.IOException;


/**
 * @author Pavel Savara (original)
 */
public class DotNetBridge {
	public native void main();

	static {
		final String name = System.mapLibraryName("robocode.dotnet.nhost-1.7.1.0");
		// final String p = System.getProperty("java.library.path");
		// System.loadLibrary("robocode.dotnet.nhost-1.7.1.0");
		File f = new File("../robocode.dotnet.nhost/target/", name);

		try {
			System.load(f.getCanonicalPath());
		} catch (IOException e) {
			Logger.logError(e);
		}
	}

	public DotNetBridge() {}

	public void talkBack() {
		System.out.println("We are called back!");
	}
	
	public void talkBackInt(int a) {
		System.out.println("We are called back!" + a);
	}
}
