/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.io;


import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.version.Version;

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
	private long quotaUsed;
	private boolean quotaMessagePrinted;
	private final List<RobotFileOutputStream> streams = new ArrayList<RobotFileOutputStream>();
	private final long maxQuota;
	private final String writableRootDirectory;
	private final String readableRootDirectory;
	private final String rootPath;
	private final String dataDir;

	public RobotFileSystemManager(IHostedThread robotProxy, long maxQuota, String writableRootDirectory, String readableRootDirectory, String rootPath) {
		this.robotProxy = robotProxy;
		this.maxQuota = maxQuota;
		this.writableRootDirectory = writableRootDirectory;
		this.readableRootDirectory = readableRootDirectory;
		this.rootPath = rootPath;

		this.dataDir = robotProxy.getStatics().getFullClassName().replace('.', '/') + ".data/";
	}

	public void initialize() {
		initializeQuota();
		updateDataFiles();
	}

	void addStream(RobotFileOutputStream s) throws IOException {
		if (s == null) {
			throw new SecurityException("You may not add a null stream.");
		}
		if (!streams.contains(s)) {
			if (streams.size() < 5) {
				streams.add(s);
			} else {
				throw new SecurityException(
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

	void checkQuota(long numBytes) throws IOException {
		if (numBytes < 0) {
			throw new IllegalArgumentException("checkQuota on negative numBytes!");
		}
		if (quotaUsed + numBytes <= maxQuota) {
			adjustQuota(numBytes);
		} else {
			final String msg = "You have reached your filesystem quota of: " + maxQuota + " bytes.";

			if (!quotaMessagePrinted) {
				robotProxy.println("SYSTEM: " + msg);
				quotaMessagePrinted = true;
			}
			throw new IOException(msg); // Must be IOException due to bug fix [2960894]
		}
	}

	public long getMaxQuota() {
		return maxQuota;
	}

	public long getQuotaUsed() {
		return quotaUsed;
	}

	public File getReadableDirectory() {
		try {
			return (readableRootDirectory == null) ? null : new File(readableRootDirectory).getCanonicalFile();
		} catch (IOException e) {
			Logger.logError(e);
			return null;
		}
	}

	public File getWritableDirectory() {
		try {
			return (writableRootDirectory == null)
					? null
					: new File(writableRootDirectory, robotProxy.getStatics().getShortClassName() + ".data").getCanonicalFile();
		} catch (IOException e) {
			Logger.logError(e);
			return null;
		}
	}

	public File getDataFile(String filename) {
		filename = filename.replaceAll("\\*", "");

		final File parent = getWritableDirectory();
		File file = new File(parent, filename);

		// TODO the file is never replaced from jar or directory after it was created
		// TODO it would be good to replace it when it have bigger last modified date
		if (!file.exists()) {
			if (parent != null && !parent.exists() && !parent.mkdirs()) {
				return file;
			}
			InputStream is = null;
			OutputStream os = null;

			try {
				URL url = new URL(rootPath + dataDir + filename);

				URLConnection connection = url.openConnection();
				connection.setUseCaches(false);
				try {
					is = connection.getInputStream();
				} catch (FileNotFoundException ex) { // Expected as no file might exists with the specified input 'filename'
					// #380 yet another historical bot related bug
					Version robocodeVersion = toVersion(robotProxy.getStatics().getRobocodeVersion());
					if (robocodeVersion != null && robocodeVersion.compareTo("1.8.2.0") < 0) {
						throw ex;
					}
				}
				os = new FileOutputStream(file);
				if (is != null) {
					copyStream(is, os);
				}
			} catch (IOException e) {
				// #380 yet another historical bot related bug
				boolean legacyRobot = false;
				if (e instanceof FileNotFoundException) {
					Version robocodeVersion = toVersion(robotProxy.getStatics().getRobocodeVersion());
					if (robocodeVersion != null && robocodeVersion.compareTo("1.8.2.0") < 0) {
						legacyRobot = true;
					}
				}
				if (!legacyRobot) {
					// Always log error, unless an legacy robot got a FileNotFoundException
					Logger.logError(e);
				}
			} finally {
				FileUtil.cleanupStream(is);
				FileUtil.cleanupStream(os);
			}
		}
		return file;
	}

	private void initializeQuota() {
		quotaUsed = 0;
		quotaMessagePrinted = false;

		File dataDirectory = getWritableDirectory();

		if (dataDirectory != null && dataDirectory.exists()) {
			File[] dataFiles = dataDirectory.listFiles();

			for (File file : dataFiles) {
				quotaUsed += file.length();
			}
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

	void removeStream(RobotFileOutputStream s) {
		if (s == null) {
			throw new SecurityException("You may not remove a null stream.");
		}
		if (streams.contains(s)) {
			streams.remove(s);
		}
	}

	private void updateDataFiles() {
		try {
			if (rootPath.startsWith("jar:")) {
				updateDataFilesFromJar();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateDataFilesFromJar() throws IOException {
		URL url = new URL(rootPath);
		JarURLConnection jarConnection = (JarURLConnection) url.openConnection();

		jarConnection.setUseCaches(false);

		JarFile jarFile = jarConnection.getJarFile();
		
		Enumeration<?> entries = jarFile.entries();

		final File parent = getWritableDirectory();

		if (!parent.exists() && !parent.mkdirs()) {
			throw new IOException("Could not create writeable directory for " + robotProxy.getStatics().getName());
		}

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

	private static Version toVersion(String vers) {
		if (vers != null) {
			vers = vers.trim();
			if (vers.length() > 0) {
				try {
					return new Version(vers);
				} catch (IllegalArgumentException ex) {
					return null;
				}
			}
		}
		return null;
	}
}
