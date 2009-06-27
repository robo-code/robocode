/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - JarJarURLStreamHandler is just tweaked version of jar handler
 *       from OpenJDK, license below
 *******************************************************************************/

/*
 * Copyright 1997-2000 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package net.sf.robocode.host.jarjar;


import net.sf.robocode.io.URLJarCollector;
import net.sf.robocode.io.JarJar;

import java.io.InputStream;
import java.io.IOException;
import java.net.*;


/**
 * @author Pavel Savara
 */
public class JarJarURLConnection extends URLConnection {
	private URLConnection connection;
	public final static char SEPARATOR_CHAR = JarJar.SEPARATOR_CHAR; // this is '^' now
	public final static String SEPARATOR = SEPARATOR_CHAR + "/";
	private static boolean registered = false;

	public JarJarURLConnection(URL url)
			throws IOException {
		super(url);
		final String file = url.getFile();
		URL inner = new URL(file);

		//this is same as
		// connection = inner.openConnection()
		// we just cache the connection in URLJarCollector 
		// because we need to be able to close it to release jar files
		connection = URLJarCollector.openConnection(inner);
	}

	public void connect() throws IOException {
		if (!connected) {
			connection.connect();
			connected = true;
		}
	}

	public InputStream getInputStream() throws IOException {
		connect();
		return connection.getInputStream();
	}

	public static void register() {
		if (!registered) {
			URL.setURLStreamHandlerFactory(new JarJarURLStreamHandlerFactory());
			registered = true;
		}
	}

	public static class JarJarURLStreamHandlerFactory implements URLStreamHandlerFactory {
		public URLStreamHandler createURLStreamHandler(String protocol) {
			if (protocol.equals("jarjar")) {
				return new JarJarURLStreamHandler();
			}
			return null;
		}
	}


	public static class JarJarURLStreamHandler extends URLStreamHandler {

		protected URLConnection openConnection(URL u) throws IOException {
			return new JarJarURLConnection(u);
		}

		private int indexOfBangSlash(String spec) {
			int indexOfBang = spec.length();

			while ((indexOfBang = spec.lastIndexOf(SEPARATOR_CHAR, indexOfBang)) != -1) {
				if ((indexOfBang != (spec.length() - 1)) && (spec.charAt(indexOfBang + 1) == '/')) {
					return indexOfBang + 1;
				} else {
					indexOfBang--;
				}
			}
			return -1;
		}

		@SuppressWarnings({"deprecation"})
		protected void parseURL(URL url, String spec,
								int start, int limit) {
			String file = null;
			String ref = null;
			// first figure out if there is an anchor
			int refPos = spec.indexOf('#', limit);
			boolean refOnly = refPos == start;

			if (refPos > -1) {
				ref = spec.substring(refPos + 1, spec.length());
				if (refOnly) {
					file = url.getFile();
				}
			}
			// then figure out if the spec is
			// 1. absolute (jarjar:)
			// 2. relative (i.e. url + foo/bar/baz.ext)
			// 3. anchor-only (i.e. url + #foo), which we already did (refOnly)
			boolean absoluteSpec = false;

			if (spec.length() >= 7) {
				absoluteSpec = spec.substring(0, 7).equalsIgnoreCase("jarjar:");
			}
			spec = spec.substring(start, limit);

			if (absoluteSpec) {
				file = parseAbsoluteSpec(spec);
			} else if (!refOnly) {
				file = parseContextSpec(url, spec);

				// Canonize the result after the bangslash
				int bangSlash = indexOfBangSlash(file);
				String toBangSlash = file.substring(0, bangSlash);
				String afterBangSlash = file.substring(bangSlash);

				file = toBangSlash + afterBangSlash;
			}
			file = file != null ? "jar:" + file.replaceFirst("\\" + SEPARATOR, "!/") : null;
			setURL(url, "jarjar", "", -1, file, ref);
		}

		@SuppressWarnings({"UnusedAssignment", "UnusedDeclaration"})
		private String parseAbsoluteSpec(String spec) {
			@SuppressWarnings("unused")
			URL url = null;
			int index = -1;

			// check for !/
			if ((index = indexOfBangSlash(spec)) == -1) {
				throw new NullPointerException("no " + SEPARATOR + " in spec");
			}
			// test the inner URL
			try {
				String innerSpec = spec.substring(0, index - 1);

				url = new URL(innerSpec);
			} catch (MalformedURLException e) {
				throw new NullPointerException("invalid url: " + spec + " (" + e + ")");
			}
			return spec;
		}

		private String parseContextSpec(URL url, String spec) {
			String ctxFile = url.getFile();

			// if the spec begins with /, chop up the jar back !/
			if (spec.startsWith("/")) {
				int bangSlash = indexOfBangSlash(ctxFile);

				if (bangSlash == -1) {
					throw new NullPointerException("malformed " + "context url:" + url + ": no " + SEPARATOR);
				}
				ctxFile = ctxFile.substring(0, bangSlash);
			}
			if (!ctxFile.endsWith("/") && (!spec.startsWith("/"))) {
				// chop up the last component
				int lastSlash = ctxFile.lastIndexOf('/');

				if (lastSlash == -1) {
					throw new NullPointerException("malformed " + "context url:" + url);
				}
				ctxFile = ctxFile.substring(0, lastSlash + 1);
			}
			return (ctxFile + spec);
		}
	}
}
