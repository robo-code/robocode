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
 *     - Ported to Java 5.0
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package net.sf.robocode.host.io;


import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.io.FileUtil;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotFileSystemManager {
	private final IHostedThread robotProxy;
	private long quotaUsed = 0;
	private boolean quotaMessagePrinted = false;
	private final List<RobotFileOutputStream> streams = new ArrayList<RobotFileOutputStream>();
	private long maxQuota = 0;
	private final String writableRootDirectory;
	private final String readableRootDirectory;
	private final String rootFile;
	private final String dataDir;

	public RobotFileSystemManager(IHostedThread robotProxy, long maxQuota, String writableRootDirectory, String readableRootDirectory, String rootFile) {
		this.robotProxy = robotProxy;
		this.maxQuota = maxQuota;
		this.writableRootDirectory = writableRootDirectory;
		this.readableRootDirectory = readableRootDirectory;
		this.rootFile = rootFile;

		this.dataDir = robotProxy.getStatics().getFullClassName().replace('.', '/') + ".data/";
	}

	public void initialize() {
		initializeQuota();
		updateDataFiles();
	}

	public void addStream(RobotFileOutputStream s) throws IOException {
		if (s == null) {
			throw new SecurityException("You may not add a null stream.");
		}
		if (!streams.contains(s)) {
			if (streams.size() < 5) {
				streams.add(s);
			} else {
				throw new IOException(
						"You may only have 5 streams open at a time.\n Make sure you call close() on your streams when you are finished with them.");
			}
		}
	}

	public synchronized void adjustQuota(long len) {
		quotaUsed += len;
	}

	public void checkQuota() throws IOException {
		checkQuota(0);
	}

	public void checkQuota(long numBytes) throws IOException {
		if (numBytes < 0) {
			throw new IndexOutOfBoundsException("checkQuota on negative numBytes!");
		}
		if (quotaUsed + numBytes <= maxQuota) {
			adjustQuota(numBytes);
			return;
		}
		if (!quotaMessagePrinted) {
			robotProxy.println("SYSTEM: You have reached your filesystem quota of: " + maxQuota + " bytes.");
			quotaMessagePrinted = true;
		}
		throw new IOException("You have reached your filesystem quota of: " + maxQuota + " bytes.");
	}

	public long getMaxQuota() {
		return maxQuota;
	}

	public long getQuotaUsed() {
		return quotaUsed;
	}

	public File getReadableDirectory() {
		try {
			final String dir = readableRootDirectory;

			return (dir == null) ? null : new File(dir).getCanonicalFile();
		} catch (IOException e) {
			return null;
		}
	}

	public File getWritableDirectory() {
		try {
			final String dir = writableRootDirectory;

			return (dir == null)
					? null
					: new File(writableRootDirectory, robotProxy.getStatics().getShortClassName() + ".data").getCanonicalFile();
		} catch (IOException e) {
			return null;
		}
	}

	public File getDataFile(String filename) {
		final File parent = getWritableDirectory();
		File file = new File(parent, filename);

		// TODO the file is never replaced from jar or directory after it was created
		// TODO it would be good to replace it when it have bigger last modified time stamp
		if (!file.exists()) {
			if (!parent.exists() && !parent.mkdirs()) {
				return file;
			}
			InputStream is = null;
			OutputStream os = null;

			try {
				URL unUrl = new URL(rootFile + dataDir + filename);
				final URLConnection connection = unUrl.openConnection();

				connection.setUseCaches(false);

				is = connection.getInputStream();
				os = new FileOutputStream(file);

				copyStream(is, os);
			} catch (IOException ignore) {} finally {
				FileUtil.cleanupStream(is);
				FileUtil.cleanupStream(os);
			}
		}
		return file;
	}

	private void initializeQuota() {
		File dataDirectory = getWritableDirectory();

		if (dataDirectory == null) {
			quotaUsed = maxQuota;
			return;
		}
		if (!dataDirectory.exists()) {
			this.quotaUsed = 0;
			return;
		}
		quotaMessagePrinted = false;
		File[] dataFiles = dataDirectory.listFiles();

		quotaUsed = 0;
		for (File file : dataFiles) {
			quotaUsed += file.length();
		}
	}

	public boolean isReadable(String fileName) {
		File allowedDirectory = getReadableDirectory();

		if (allowedDirectory == null) {
			return false;
		}

		File attemptedFile;

		try {
			attemptedFile = new File(fileName).getCanonicalFile();
		} catch (IOException e) {
			return false;
		}

		if (attemptedFile.equals(allowedDirectory)) {
			return true; // recursive check
		}

		if (attemptedFile.getParent().indexOf(allowedDirectory.toString()) == 0) {
			String fs = attemptedFile.toString();
			int dataIndex = fs.indexOf(".data", allowedDirectory.toString().length());

			if (dataIndex >= 0) {
				if (isWritable(fileName) || attemptedFile.equals(getWritableDirectory())) {
					return true;
				}
				throw new java.security.AccessControlException(
						"Preventing " + Thread.currentThread().getName() + " from access to: " + fileName
						+ ": You may not read another robot's data directory.");
			}
			return true;
		}

		return false;
	}

	public boolean isWritable(String fileName) {
		File allowedDirectory = getWritableDirectory();

		if (allowedDirectory == null) {
			return false;
		}

		File attemptedFile;

		try {
			attemptedFile = new File(fileName).getCanonicalFile();
		} catch (IOException e) {
			return false;
		}

		return attemptedFile.equals(allowedDirectory) || attemptedFile.getParentFile().equals(allowedDirectory);
	}

	public void removeStream(RobotFileOutputStream s) {
		if (s == null) {
			throw new SecurityException("You may not remove a null stream.");
		}
		if (streams.contains(s)) {
			streams.remove(s);
		}
	}

	private void updateDataFiles() {
		try {
			if (rootFile.startsWith("jar:")) {
				updateDataFilesFromJar();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateDataFilesFromJar() throws IOException {
		URL url = new URL(rootFile);
		JarURLConnection jarConnection = (JarURLConnection) url.openConnection();

		jarConnection.setUseCaches(false);

		JarFile jarFile = jarConnection.getJarFile();
		
		Enumeration<?> entries = jarFile.entries();

		final File parent = getWritableDirectory();

		parent.mkdirs();
		
		InputStream is = null;
		OutputStream os = null;

		while (entries.hasMoreElements()) {
			JarEntry jarEntry = (JarEntry) entries.nextElement();

			String filename = jarEntry.getName();

			if (filename.startsWith(dataDir)) {			
				filename = filename.substring(dataDir.length());
				if (filename.length() == 0) { // Bugfix [2845608] - FileNotFoundException
					continue;
				}

				is = null;
				os = null;
				try {
					is = jarFile.getInputStream(jarEntry);
					os = new FileOutputStream(new File(parent, filename));
					copyStream(is, os);
				} finally {
					FileUtil.cleanupStream(is);
					FileUtil.cleanupStream(os);
				}
			}
		}
	}

	private void copyStream(InputStream is, OutputStream os) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(os);

		byte[] buf = new byte[8192];
		int len;

		while ((len = bis.read(buf)) > 0) {
			bos.write(buf, 0, len);
		}
		bos.flush();
	}
}
