/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
// NOTE: JarJarURLStreamHandler is just tweaked version of jar handler from OpenJDK.

package net.sf.robocode.host.jarjar;


import net.sf.robocode.io.URLJarCollector;
import net.sf.robocode.io.JarJar;

import java.io.InputStream;
import java.io.IOException;
import java.net.*;


// TODO: Update to new version from OpenJDK

/**
 * @author Sun Microsystems, Inc (original)
 * @author Pavel Savara (contributor)
 */
public class JarJarURLConnection extends URLConnection {

	private final static char SEPARATOR_CHAR = JarJar.SEPARATOR_CHAR; // this is '^' now
	private final static String SEPARATOR = SEPARATOR_CHAR + "/";

	private URLConnection connection;
	private static boolean registered;

	private JarJarURLConnection(URL url)
		throws IOException {
		super(url);
		final String file = url.getFile();
		URL inner = new URL(file);

		// this is same as
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

	private static class JarJarURLStreamHandlerFactory implements URLStreamHandlerFactory {
		public URLStreamHandler createURLStreamHandler(String protocol) {
			if (protocol.equals("jarjar")) {
				return new JarJarURLStreamHandler();
			}
			return null;
		}
	}


	private static class JarJarURLStreamHandler extends URLStreamHandler {

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
			setURL(url, "jarjar", "", -1, null, null, file, null, ref);
		}

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
