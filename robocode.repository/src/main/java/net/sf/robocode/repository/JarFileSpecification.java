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
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Code cleanup
 *******************************************************************************/
package net.sf.robocode.repository;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.ui.IWindowManager;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
class JarFileSpecification extends FileSpecification {

	// Used in FileSpecification
	protected JarFileSpecification(File f, File rootDir, boolean developmentVersion) {
		this.rootDir = rootDir;
		this.developmentVersion = developmentVersion;
		valid = true;
		String filename = f.getName();
		String fileType = FileUtil.getFileType(filename);

		if (fileType.equals(".jar") || fileType.equals(".zip")) {
			setFileLastModified(f.lastModified());
			setFileLength(f.length());
			setFileType(fileType);
			try {
				setFilePath(f.getCanonicalPath());
			} catch (IOException e) {
				Logger.logError("Warning:  Unable to determine canonical path for " + f.getPath());
				setFilePath(f.getPath());
			}
			setFileName(f.getName());
		} else {
			throw new RuntimeException("JarFileSpecification can only be constructed from a .jar file");
		}
	}

	@Override
	public String getUid() {
		return getFilePath();
	}

	public void processJar(File robotCache, File  robotsDirectory, List<FileSpecification> updatedJarList) {
		final IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);

		if (windowManager != null) {
			windowManager.setStatus("Extracting .jar: " + getFileName());
		}

		File dest;

		if (getRootDir().equals(robotsDirectory)) {
			dest = new File(robotCache, getFileName() + "_");
		} else {
			dest = new File(getRootDir(), getFileName() + "_");
		}
		if (dest.exists()) {
			FileUtil.deleteDir(dest);
		}
		if (!dest.exists() && !dest.mkdirs()) {
			Logger.logError("Can't create" + dest.toString());
		}

		File f = new File(getFilePath());

		extractJar(f, dest, "Extracting .jar: " + getFileName(), updatedJarList, true, true);
	}

	public static int extractJar(File f, File dest, String statusPrefix, List<FileSpecification> updatedJarList, boolean close,
			boolean alwaysReplace) {

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(f);
			JarInputStream jarIS = new JarInputStream(fis);

			return extractJar(jarIS, dest, statusPrefix, updatedJarList, close, alwaysReplace);
		} catch (IOException e) {
			logError("Exception reading " + f + ": " + e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignored) {}
			}
		}
		return 16;
	}

	public static int extractJar(JarInputStream jarIS, File dest, String statusPrefix, List<FileSpecification> updatedJarList, boolean close,
			boolean alwaysReplace) {
		int rc = 0;
		boolean always = alwaysReplace;
		byte buf[] = new byte[2048];

		final IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);

		try {
			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				if (windowManager != null) {
					windowManager.setStatus(statusPrefix + " (" + entry.getName() + ")");
				}
				if (entry.isDirectory()) {
					File dir = new File(dest, entry.getName());

					if (!dir.exists() && !dir.mkdirs()) {
						logError("Can't create dir " + dir.toString());
					}
				} else {
					File out = new File(dest, entry.getName());

					if (out.exists() && !always) {
						Object[] options = {
							"Yes to All", "Yes", "No", "Cancel"
						};
						int r = JOptionPane.showOptionDialog(null, entry.getName() + " exists.  Replace?", "Warning",
								JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

						if (r == 0) {
							always = true;
						} else if (r == 2) {
							if (windowManager != null) {
								windowManager.setStatus(entry.getName() + " skipped.");
							}
							entry = jarIS.getNextJarEntry();
							continue;
						} else if (r == 3) {
							entry = null;
							rc = -1;
							continue;
						}
					}
					File parentDirectory = new File(out.getParent()).getCanonicalFile();

					if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
						logError("Can't create dir " + parentDirectory.toString());
					}

					FileOutputStream fos = null;

					try {
						fos = new FileOutputStream(out);

						int num;

						while ((num = jarIS.read(buf, 0, 2048)) != -1) {
							fos.write(buf, 0, num);
						}

						FileDescriptor fd = fos.getFD();

						fd.sync();
					} finally {
						if (fos != null) {
							fos.close();
						}
					}

					if (entry.getTime() >= 0) {
						if (!out.setLastModified(entry.getTime())) {
							logError("Can't set file time " + out.toString());
						}
					}

					if (updatedJarList != null) {
						if (entry.getName().indexOf("/") < 0 && FileUtil.getFileType(entry.getName()).equals(".jar")) {
							FileSpecification fileSpecification = createSpecification(null, out, parentDirectory, "",
									false);

							updatedJarList.add(fileSpecification);
						}
					}
				}
				entry = jarIS.getNextJarEntry();
			}
			if (close) {
				jarIS.close();
			}
		} catch (IOException e) {
			logError("IOException " + statusPrefix + ": ", e);
		}
		return rc;
	}

}
