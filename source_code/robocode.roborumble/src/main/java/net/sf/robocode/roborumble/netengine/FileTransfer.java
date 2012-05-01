/*******************************************************************************
 * Copyright (c) 2003, 2008 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Albert Pérez
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Completely rewritten to be fully multi-threaded so that download is not
 *       blocked if a connection is hanging. In addition, this version of
 *       FileTransfer support sessions
 *******************************************************************************/
package net.sf.robocode.roborumble.netengine;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Utility class for downloading files from the net and copying files.
 *
 * @author Flemming N. Larsen (original)
 */
public class FileTransfer {

	private final static int CONNECTION_TIMEOUT = 5000; // 5 seconds

	/**
	 * Represents the download status returned when downloading files.
	 *
	 * @author Flemming N. Larsen
	 */
	public enum DownloadStatus {
		OK, // The download was succesful
		COULD_NOT_CONNECT, // Connection problem
		FILE_NOT_FOUND, // The file to download was not found
	}


	/**
	 * Daemon worker thread containing a 'finish' flag for waiting and notifying
	 * when the thread has finished it's job.
	 *
	 * @author Flemming N. Larsen
	 */
	private static class WorkerThread extends Thread {

		final Object monitor = new Object();

		volatile boolean isFinished;

		public WorkerThread(String name) {
			super(name);
			setDaemon(true);
		}

		void notifyFinish() {
			// Notify that this thread is finish
			synchronized (monitor) {
				isFinished = true;
				monitor.notifyAll();
			}
		}
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
			final GetSessionIdThread sessionIdThread = new GetSessionIdThread(con);

			sessionIdThread.start();

			// Wait for the session id
			synchronized (sessionIdThread.monitor) {
				while (!sessionIdThread.isFinished) {
					try {
						sessionIdThread.monitor.wait(CONNECTION_TIMEOUT);
						sessionIdThread.interrupt();
					} catch (InterruptedException e) {
						// Immediately reasserts the exception by interrupting the caller thread itself
						Thread.currentThread().interrupt();

						return null;
					}
				}
			}

			// Return the session id
			return sessionIdThread.sessionId;

		} catch (final IOException e) {
			return null;
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
	 * Worker thread used for getting the session id of an already open HTTP
	 * connection.
	 *
	 * @author Flemming N. Larsen
	 */
	private final static class GetSessionIdThread extends WorkerThread {

		// The resulting session id to read out
		String sessionId;

		final HttpURLConnection con;

		GetSessionIdThread(HttpURLConnection con) {
			super("FileTransfer: Get session ID");
			this.con = con;
		}

		@Override
		public void run() {
			try {
				// Get the cookie value
				final String cookieVal = con.getHeaderField("Set-Cookie");

				// Extract the session id from the cookie value
				if (cookieVal != null) {
					sessionId = cookieVal.substring(0, cookieVal.indexOf(";"));
				}
			} catch (final Exception e) {
				sessionId = null;
			}
			// Notify that this thread is finish
			notifyFinish();
		}
	}

	/**
	 * Downloads a file from a HTTP site.
	 *
	 * @param url       the url of the HTTP site to download the file from
	 * @param filename  the filename of the destination file
	 * @param sessionId an optional session id if the download is session based
	 * @return the download status, which is DownloadStatus.OK if the download
	 *         completed successfully; otherwise an error occurred
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
			final DownloadThread downloadThread = new DownloadThread(con, filename);

			downloadThread.start();

			// Wait for the download to complete
			synchronized (downloadThread.monitor) {
				while (!downloadThread.isFinished) {
					try {
						downloadThread.monitor.wait();
					} catch (InterruptedException e) {
						return DownloadStatus.COULD_NOT_CONNECT;
					}
				}
			}

			// Return the download status
			return downloadThread.status;

		} catch (final IOException e) {
			return DownloadStatus.COULD_NOT_CONNECT;
		} finally {
			// Make sure the connection is disconnected.
			// This will cause threads using the connection to throw an exception
			// and thereby terminate if they were hanging.
			try {
				if (con != null) {
					con.disconnect();
				}
			} catch (Throwable ignore) {// we expect this, right ?
			}
		}
	}

	/**
	 * Worker thread used for downloading a file from an already open HTTP
	 * connection.
	 *
	 * @author Flemming N. Larsen
	 */
	private final static class DownloadThread extends WorkerThread {

		// The download status to be read out
		DownloadStatus status = DownloadStatus.COULD_NOT_CONNECT; // Default error

		final HttpURLConnection con;
		final String filename;

		InputStream in;
		OutputStream out;

		DownloadThread(HttpURLConnection con, String filename) {
			super("FileTransfer: Download");
			this.con = con;
			this.filename = filename;
		}

		@Override
		public void run() {
			try {
				// Start getting the response code
				final GetResponseCodeThread responseThread = new GetResponseCodeThread(con);

				responseThread.start();

				// Wait for the response to finish
				synchronized (responseThread.monitor) {
					while (!responseThread.isFinished) {
						try {
							responseThread.monitor.wait(CONNECTION_TIMEOUT);
							responseThread.interrupt();
						} catch (InterruptedException e) {
							notifyFinish();
							return;
						}
					}
				}

				final int responseCode = responseThread.responseCode;

				if (responseCode == -1) {
					// Terminate if we did not get the response code
					notifyFinish();
					return;

				} else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
					// Terminate if the HTTP page containing the file was not found
					status = DownloadStatus.FILE_NOT_FOUND;
					notifyFinish();
					return;

				} else if (responseCode != HttpURLConnection.HTTP_OK) {
					// Generally, terminate if did not receive a OK response
					notifyFinish();
					return;
				}

				// Start getting the size of the file to download
				final GetContentLengthThread contentLengthThread = new GetContentLengthThread(con);

				contentLengthThread.start();

				// Wait for the file size
				synchronized (contentLengthThread.monitor) {
					while (!contentLengthThread.isFinished) {
						try {
							contentLengthThread.monitor.wait(CONNECTION_TIMEOUT);
							contentLengthThread.interrupt();
						} catch (InterruptedException e) {
							notifyFinish();
							return;
						}
					}
				}

				final int size = contentLengthThread.contentLength;

				if (size == -1) {
					// Terminate if we did not get the content length
					notifyFinish();
					return;
				}

				// Get an input stream from the connection
				in = con.getInputStream();

				// Prepare the output stream for the file output
				out = new FileOutputStream(filename);

				// Download the file

				final byte[] buf = new byte[4096];

				int totalRead = 0;
				int bytesRead;

				// Start thread for reading bytes into the buffer

				while (totalRead < size) {
					// Start reading bytes into the buffer
					final ReadInputStreamToBufferThread readThread = new ReadInputStreamToBufferThread(in, buf);

					readThread.start();

					// Wait for the reading to finish
					synchronized (readThread.monitor) {
						while (!readThread.isFinished) {
							try {
								readThread.monitor.wait(CONNECTION_TIMEOUT);
								readThread.interrupt();
							} catch (InterruptedException e) {
								notifyFinish();
								return;
							}
						}
					}

					bytesRead = readThread.bytesRead;
					if (bytesRead == -1) {
						// Read completed has completed
						notifyFinish();
						break;
					}

					// Write the byte buffer to the output
					out.write(buf, 0, bytesRead);

					totalRead += bytesRead;
				}

				// If we reached this point, the download was succesful
				status = DownloadStatus.OK;

				notifyFinish();

			} catch (final IOException e) {
				status = DownloadStatus.COULD_NOT_CONNECT;
			} finally {
				// Make sure the input stream is closed
				if (in != null) {
					try {
						in.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
				// Make sure the output stream is closed
				if (out != null) {
					try {
						out.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	/**
	 * Worker thread used for getting the response code of an already open HTTP
	 * connection.
	 *
	 * @author Flemming N. Larsen
	 */
	final static class GetResponseCodeThread extends WorkerThread {

		// The response code to read out
		int responseCode;

		final HttpURLConnection con;

		GetResponseCodeThread(HttpURLConnection con) {
			super("FileTransfer: Get response code");
			this.con = con;
		}

		@Override
		public void run() {
			try {
				// Get the response code
				responseCode = con.getResponseCode();
			} catch (final Exception e) {
				responseCode = -1;
			}
			// Notify that this thread is finish
			notifyFinish();
		}
	}


	/**
	 * Worker thread used for getting the content length of an already open HTTP
	 * connection.
	 *
	 * @author Flemming N. Larsen
	 */
	final static class GetContentLengthThread extends WorkerThread {

		// The content length to read out
		int contentLength;

		final HttpURLConnection con;

		GetContentLengthThread(HttpURLConnection con) {
			super("FileTransfer: Get content length");
			this.con = con;
		}

		@Override
		public void run() {
			try {
				// Get the content length
				contentLength = con.getContentLength();
			} catch (final Exception e) {
				contentLength = -1;
			}
			// Notify that this thread is finish
			notifyFinish();
		}
	}


	/**
	 * Worker thread used for reading bytes from an already open input stream
	 * into a byte buffer.
	 *
	 * @author Flemming N. Larsen
	 */
	final static class ReadInputStreamToBufferThread extends WorkerThread {

		int bytesRead;

		final InputStream in;

		final byte[] buf;

		ReadInputStreamToBufferThread(InputStream in, byte[] buf) {
			super("FileTransfer: Read input stream to buffer");
			this.in = in;
			this.buf = buf;
		}

		@Override
		public void run() {
			try {
				// Read bytes into the buffer
				bytesRead = in.read(buf);
			} catch (final Exception e) {
				bytesRead = -1;
			}
			// Notify that this thread is finish
			notifyFinish();
		}
	}

	/**
	 * Copies a file into another file.
	 *
	 * @param src_file  the filename of the source file to copy
	 * @param dest_file the filename of the destination file to copy the file
	 *                  into
	 * @return true if the file was copied; false otherwise
	 */
	public static boolean copy(String src_file, String dest_file) {
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			if (src_file.equals(dest_file)) {
				throw new IOException("You cannot copy a file onto itself");
			}
			final byte[] buf = new byte[4096];

			in = new FileInputStream(src_file);
			out = new FileOutputStream(dest_file);

			while (in.available() > 0) {
				out.write(buf, 0, in.read(buf, 0, buf.length));
			}
		} catch (final IOException e) {
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
