/*******************************************************************************
 * Copyright (c) 2003, 2007 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Albert Pérez
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Completely rewritten, where the download() method support sessions
 *******************************************************************************/
package roborumble.netengine;


import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;


/**
 * Utility class for downloading files from the net and copying files.
 *
 * @author Flemming N. Larsen
 */
public class FileTransfer {

	/**
	 * Returns the session id for a HTTP page
	 *
	 * @param url the url of the HTTP page
	 * @return the session id for the specified HTTP page or null if no session
	 *    could be found
	 */
	public static String getSessionId(String url) {
		String sessionId = null;

		HttpURLConnection con = null;

		try {
			con = (HttpURLConnection) new URL(url).openConnection();

			String cookieVal = con.getHeaderField("Set-Cookie");

			if (cookieVal != null) {
				sessionId = cookieVal.substring(0, cookieVal.indexOf(";"));
			}
		} catch (Exception e) {// No nothing, null will be returned
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return sessionId;
	}

	/**
	 * Downloads a file from a HTTP page.
	 *
	 * @param url the url of the HTTP page to download the file from
	 * @param filename the filename of the destination file
	 * @param sessionId an optional session id if the download is session based
	 *
	 * @return true if the file was downloaded; false otherwise
	 */
	public static boolean download(String url, String filename, String sessionId) {
		HttpURLConnection con = null;

		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			if (con == null) {
				throw new IOException("Could not open connection to '" + url + "'");
			}
			if (sessionId != null) {
				con.setRequestProperty("Cookie", sessionId);
			}
			con.setDoInput(true);
			con.setDoOutput(false);
			con.setUseCaches(false);

			con.connect();

			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}

			long length = con.getContentLength();

			if (length <= 0) {
				return false;
			}
			InputStream is = con.getInputStream();

			if (is == null) {
				return false;
			}

			byte[] buf = new byte[4096];
			FileOutputStream fos = new FileOutputStream(filename);

			int totalRead = 0;

			while (totalRead < length) {
				int bytesRead = is.read(buf);

				fos.write(buf, 0, bytesRead);

				totalRead += bytesRead;
			}

			is.close();
			fos.close();

		} catch (Exception e) {
			return false;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return true;
	}

	/**
	 * Copies a file into another file.
	 *
	 * @param src_file the filename of the source file to copy
	 * @param dest_file the filename of the destination file to copy the file
	 *    into
	 * @return true if the file was copied; false otherwise
	 */
	public static boolean copy(String src_file, String dest_file) {
		try {
			if (src_file.equals(dest_file)) {
				throw new IOException("You cannot copy a file onto itself");
			}
			byte[] buf = new byte[4096];
			FileInputStream in = new FileInputStream(src_file);
			FileOutputStream out = new FileOutputStream(dest_file);

			while (in.available() > 0) {
				out.write(buf, 0, in.read(buf, 0, buf.length));
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
