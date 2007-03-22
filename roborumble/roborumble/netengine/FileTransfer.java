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
 *     - Completely rewritten, to support session-based download that is not
 *       blocked if a connection is hanging, i.e. not responding
 *******************************************************************************/
package roborumble.netengine;


import java.io.*;
import java.net.*;


/**
 * Utility class for downloading files from the net and copying files.
 *
 * @author Flemming N. Larsen
 */
public class FileTransfer {

	private final static int CONNECTION_TIMEOUT = 5000; // 5 seconds
	private final static int DOWNLOAD_TIMEOUT = 60000; // 1 minute

	/**
	 * Represents the download status returned when downloading files.
	 *
	 * @author Flemming N. Larsen
	 */
	public enum DownloadStatus {

		/** The download was succesful */
		OK,
		
		/** Connection problem */
		COULD_NOT_CONNECT,

		/** The file to download was not found */
		FILE_NOT_FOUND
	}

	/*
	 * Returns a session id for keeping a session on a HTTP site.
	 *
	 * @param url the url of the HTTP site
	 * @return a session id for keeping a session on a HTTP site or null if no
	 *    session is available
	 */
	public static String getSessionId(String url) {
		HttpURLConnection con = null;

		try {
			// Open connection
			con = (HttpURLConnection) new URL(url).openConnection();
			if (con == null) {
				throw new IOException("Could not open connection to '" + url + "'");
			}

			// Get a session id if available
			GetSessionIdThread getter = new GetSessionIdThread(con);

			getter.start();

			// Wait for the session id
			synchronized (getter) {
				getter.wait(CONNECTION_TIMEOUT);
			}

			// Return the session id
			return getter.sessionId;

		} catch (Exception e) {
			// No nothing, null will be returned
			;
		} finally {
			// Make sure the connection is disconnected.
			// This will cause threads using the connection to throw an exception
			// and thereby terminate if they were hanging.
			if (con != null) {
				con.disconnect();
			}
		}

		// If we got here, the session id is not available
		return null;
	}

	/**
	 * Thread used for getting the session id of an already open HTTP connection.
	 *
	 * @author Flemming N. Larsen
	 */
	private static class GetSessionIdThread extends Thread {

		// The resulting session id to read out
		private String sessionId;

		private HttpURLConnection con;

		private GetSessionIdThread(HttpURLConnection con) {
			super("file transfer: getting session id");
			this.con = con;
		}

		@Override
		public void run() {
			sessionId = null;

			try {
				// Get the cookie value
				String cookieVal = con.getHeaderField("Set-Cookie");

				// Extract the session id from the cookie value
				if (cookieVal != null) {
					sessionId = cookieVal.substring(0, cookieVal.indexOf(";"));
				}

				// Notify that this thread is finish
				synchronized (this) {
					notify();
				}
			} catch (Exception e) {
				// No nothing, sessionId will be null
				;
			}
		}
	}

	/**
	 * Downloads a file from a HTTP site.
	 *
	 * @param url the url of the HTTP site to download the file from
	 * @param filename the filename of the destination file
	 * @param sessionId an optional session id if the download is session based
	 *
	 * @return the download status, which is DownloadStatus.OK if the download
	 *    completed successfully; otherwise an error occured
	 */
	public static DownloadStatus download(String url, String filename, String sessionId) {
		HttpURLConnection con = null;

		try {
			// Open connection
			con = (HttpURLConnection) new URL(url).openConnection();
			if (con == null) {
				throw new IOException("Could not open connection to '" + url + "'");
			}

			// Set the session id if it is specified
			if (sessionId != null) {
				con.setRequestProperty("Cookie", sessionId);
			}

			// Prepare the connection
			con.setDoInput(true);
			con.setDoOutput(false);
			con.setUseCaches(false);

			// Make the connection
			con.setConnectTimeout(CONNECTION_TIMEOUT);
			con.connect();

			// Begin the download
			DownloadThread download = new DownloadThread(con, filename);

			download.start();

			// Wait for the download to complete

			long startTime = System.currentTimeMillis();

			synchronized (download) {
				download.wait(DOWNLOAD_TIMEOUT);
			}

			// Return error if the download timed out
			if ((System.currentTimeMillis() - startTime) >= DOWNLOAD_TIMEOUT) {
				return DownloadStatus.COULD_NOT_CONNECT;
			}

			// Return the download status
			return download.status;

		} catch (Exception e) {
			return DownloadStatus.COULD_NOT_CONNECT;
		} finally {
			// Make sure the connection is disconnected.
			// This will cause threads using the connection to throw an exception
			// and thereby terminate if they were hanging.
			if (con != null) {
				con.disconnect();
			}
		}
	}

	/**
	 * Thread used for downloading a file from an already open HTTP connection.
	 *
	 * @author Flemming N. Larsen
	 */
	private static class DownloadThread extends Thread {

		// The download status to be read out
		private DownloadStatus status = DownloadStatus.COULD_NOT_CONNECT; // Default error

		private HttpURLConnection con;
		private String filename;

		private InputStream in;
		private OutputStream out;

		private DownloadThread(HttpURLConnection con, String filename) {
			super("file transfer: downloading");
			this.con = con;
			this.filename = filename;
		}

		@Override
		public void run() {
			try {
				// Start getting the response code
				GetResponseCodeThread response = new GetResponseCodeThread(con);

				response.start();

				// Wait for the response to finish
				synchronized (response) {
					response.wait(CONNECTION_TIMEOUT);
				}

				int responseCode = response.responseCode;

				if (responseCode == -1) {
					// Terminate if we did not get the response code
					return;

				} else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
					// Terminate if the HTTP page containing the file was not found
					status = DownloadStatus.FILE_NOT_FOUND;
					return;

				} else if (responseCode != HttpURLConnection.HTTP_OK) {
					// Generally, terminate if did not receive a OK response
					return;
				}

				// Get the size of the file to download
				long size = con.getContentLength();

				// Make sanity check, that the file size is > 0 bytes
				if (size <= 0) {
					status = DownloadStatus.FILE_NOT_FOUND;
					return;
				}

				// Get an input stream fromt the connection
				in = con.getInputStream();
				if (in == null) {
					return;
				}

				// Prepare the output stream for the file output
				out = new FileOutputStream(filename);
				if (out == null) {
					status = DownloadStatus.FILE_NOT_FOUND;
					return;
				}

				// Download the file

				byte[] buf = new byte[4096];

				int totalRead = 0;
				int bytesRead;

				while (totalRead < size) {
					bytesRead = in.read(buf);
					if (bytesRead == -1) {
						break; // Read completed
					}
					out.write(buf, 0, bytesRead);

					totalRead += bytesRead;
				}

				// Notify that this thread is finish
				synchronized (this) {
					notify();
				}
			} catch (Exception e) {
				status = DownloadStatus.COULD_NOT_CONNECT;
				return;
			} finally {
				// Make sure the input stream is closed
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {}
				}
				// Make sure the output stream is closed
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {}
				}
			}

			// If we reached this point, the download was succesful
			status = DownloadStatus.OK;
		}
	}


	/**
	 * Thread used for getting the response code of an already open HTTP connection.
	 *
	 * @author Flemming N. Larsen
	 */
	private static class GetResponseCodeThread extends Thread {

		// The response code to read out
		private int responseCode;

		private HttpURLConnection con;

		private GetResponseCodeThread(HttpURLConnection con) {
			super("file transfer: getting response code");
			this.con = con;
		}

		@Override
		public void run() {
			// Initialize the response code to an invalid response code
			responseCode = -1;

			try {
				// Get the response code
				responseCode = con.getResponseCode();

				// Notify that this thread is finish
				synchronized (this) {
					notify();
				}
			} catch (Exception e) {
				// No nothing, responseCode will be -1
				;
			}
		}
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
