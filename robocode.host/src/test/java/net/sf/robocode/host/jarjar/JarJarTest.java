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
package net.sf.robocode.host.jarjar;


import org.junit.Test;
import org.junit.Assert;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * @author Pavel Savara (original)
 */
public class JarJarTest {
	@Test
	public void run() throws IOException {
		JarJarURLConnection.register();

		String clas = "Hello.class";
		String inner = "Inner.jar";
		String outer = "file:src/test/resources/Outer.jar";

		final String separ = "!/";
		final String msepar = "†/";
		URL u = new URL("jar:jarjar:" + outer + msepar + inner + separ + clas);
		final URLConnection urlConnection = u.openConnection();
		final InputStream inputStream = urlConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(inputStream);
		char[] c = new char[4];

		isr.read(c);
		Assert.assertEquals('T', c[0]);
		Assert.assertEquals('e', c[1]);
		Assert.assertEquals('s', c[2]);
		Assert.assertEquals('t', c[3]);
		Assert.assertFalse(isr.ready());
		isr.close();
		inputStream.close();
	}
}
