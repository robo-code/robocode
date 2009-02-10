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
 *     - Code cleanup
 *     - Ported to Java 5
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Moved the compare() method from robocode.util.Utils into this class
 *     - Bugfix: Removed NullPointerException from the exists() method
 *******************************************************************************/
package net.sf.robocode.repository;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.security.HiddenAccess;
import robocode.control.RobotSpecification;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
abstract class FileSpecification implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	protected final Properties props = new Properties();

	private final static String ROBOCODE_VERSION = "robocode.version";
	private final static String LIBRARY_DESCRIPTION = "library.description";
	private final static String UUID = "uuid";

	protected String uuid;
	protected String name;
	protected String description;
	protected String authorName;
	protected String authorEmail;
	protected String authorWebsite;
	protected String version;
	protected String robocodeVersion;
	protected boolean developmentVersion;
	protected boolean valid;
	protected URL webpage;
	protected String libraryDescription;
	protected File rootDir;
	private File packageFile;

	private String filePath;
	private String fileName;
	private String thisFileName;
	private String fileType;
	private long fileLastModified;
	private long fileLength;

	private boolean duplicate;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	protected FileSpecification() {}

	public abstract String getUid();

	public static FileSpecification createSpecification(IRepositoryManager repositoryManager, File f, File rootDir, String prefix, boolean developmentVersion) {
		String filename = f.getName();
		String fileType = FileUtil.getFileType(filename);

		FileSpecification newSpec;

		if (fileType.equals(".team")) {
			newSpec = new TeamFileSpecification(f, rootDir, prefix, developmentVersion);
		} else if (fileType.equals(".jar") || fileType.equals(".zip")) {
			newSpec = new JarFileSpecification(f, rootDir, developmentVersion);
		} else {
			newSpec = new RobotFileSpecification(f, rootDir, prefix, developmentVersion);
			if (!(developmentVersion || newSpec.isValid())) {
				newSpec = new ClassSpecification((RobotFileSpecification) newSpec);
			}
		}
		newSpec.developmentVersion = developmentVersion;
		newSpec.rootDir = rootDir;

		if (repositoryManager != null) {
			newSpec.storeJarFile(repositoryManager.getRobotsDirectory(), repositoryManager.getRobotCache());
		}

		return newSpec;
	}

	private void storeJarFile(File robotDir, File robotCacheDir) {
		File src = null;

		if (rootDir.getName().indexOf(".jar_") == rootDir.getName().length() - 5
				|| rootDir.getName().indexOf(".zip_") == rootDir.getName().length() - 5) {
			if (rootDir.getParentFile().equals(robotCacheDir)) {
				src = new File(robotDir, rootDir.getName().substring(0, rootDir.getName().length() - 1));
			} else if (rootDir.getParentFile().getParentFile().equals(robotCacheDir)) {
				src = new File(rootDir.getParentFile(), rootDir.getName().substring(0, rootDir.getName().length() - 1));
			}
		}
		if (src != null && !src.exists()) {
			src = null;
		}
		this.packageFile = src;
	}

	public File getJarFile() {
		return packageFile;
	}

	@Override
	public String toString() {
		return getFileName() + ": length " + getFileLength() + " modified " + getFileLastModified();
	}

	public boolean isDevelopmentVersion() {
		return developmentVersion;
	}

	public boolean isSameFile(String filePath, long fileLength, long fileLastModified) {
		return filePath.equals(this.filePath) && fileLength == this.fileLength
				&& fileLastModified == this.fileLastModified;
	}

	public boolean isSameFile(FileSpecification other) {
		return other != null && other.getFileLength() == getFileLength()
				&& other.getFileLastModified() == getFileLastModified();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof FileSpecification) {
			FileSpecification other = (FileSpecification) o;

			if (!other.getName().equals(getName())) {
				return false;
			}
			if (getVersion() == null) {
				if (other.getVersion() != null) {
					return false;
				}
			}
			if (other.getVersion() == null) {
				if (getVersion() != null) {
					return false;
				}
			}
			if (other.getVersion() != null && getVersion() != null) {
				if (!other.getVersion().equals(getVersion())) {
					return false;
				}
			}
			return other.getFileLength() == getFileLength() && other.getFileLastModified() == getFileLastModified()
					&& other.getFileType().equals(getFileType());
		}
		return false;
	}

	public String getUUID() {
		return uuid;
	}

	public void setUUID() {
		// Generate new UUID
		uuid = java.util.UUID.randomUUID().toString();

		props.setProperty(UUID, uuid);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public String getAuthorWebsite() {
		return authorWebsite;
	}

	public String getVersion() {
		return version;
	}

	public String getRobocodeVersion() {
		return robocodeVersion;
	}

	public void setRobocodeVersion(String robocodeVersion) {
		this.robocodeVersion = robocodeVersion;
		props.setProperty(ROBOCODE_VERSION, robocodeVersion);
	}

	public String getLibraryDescription() {
		return libraryDescription;
	}

	public void setLibraryDescription(String libraryDescription) {
		this.libraryDescription = libraryDescription;
		props.setProperty(LIBRARY_DESCRIPTION, libraryDescription);
	}

	public String getPropertiesFileName() {
		return thisFileName;
	}

	public void setPropertiesFileName(String thisFileName) {
		this.thisFileName = thisFileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public URL getWebpage() {
		return webpage;
	}

	public long getFileLastModified() {
		return fileLastModified;
	}

	public void setFileLastModified(long fileLastModified) {
		this.fileLastModified = fileLastModified;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public void store(OutputStream out, String desc) throws IOException {
		setUUID();

		props.store(out, desc);
	}

	protected void load(FileInputStream in) throws IOException {
		props.load(in);

		uuid = props.getProperty(UUID);
		robocodeVersion = props.getProperty(ROBOCODE_VERSION);
		libraryDescription = props.getProperty(LIBRARY_DESCRIPTION);
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean value) {
		valid = value;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean isDuplicate) {
		this.duplicate = isDuplicate;
	}

	public boolean exists() {
		return (getFilePath() != null) && new File(getFilePath()).exists();
	}

	/**
	 * Gets the rootDir.
	 *
	 * @return Returns a File
	 */
	public File getRootDir() {
		return rootDir;
	}

	public static int compare(String p1, String c1, String v1, String p2, String c2, String v2) {
		if (p1 == null && p2 != null) {
			return 1;
		}
		if (p2 == null && p1 != null) {
			return -1;
		}

		if (p1 != null) // then p2 isn't either
		{
			// If packages are different, return
			int pc = p1.compareToIgnoreCase(p2);

			if (pc != 0) {
				return pc;
			}
		}

		// Ok, same package... compare class:
		int cc = c1.compareToIgnoreCase(c2);

		if (cc != 0) {
			// Different classes, return
			return cc;
		}

		// Ok, same robot... compare version
		if (v1 == null && v2 == null) {
			return 0;
		}
		if (v1 == null) {
			return 1;
		}
		if (v2 == null) {
			return -1;
		}

		if (v1.equals(v2)) {
			return 0;
		}

		if (v1.indexOf(".") < 0 || v2.indexOf(".") < 0) {
			return v1.compareToIgnoreCase(v2);
		}

		// Dot separated versions.
		StringTokenizer s1 = new StringTokenizer(v1, ".");
		StringTokenizer s2 = new StringTokenizer(v2, ".");

		while (s1.hasMoreTokens() && s2.hasMoreTokens()) {
			String tok1 = s1.nextToken();
			String tok2 = s2.nextToken();

			try {
				int i1 = Integer.parseInt(tok1);
				int i2 = Integer.parseInt(tok2);

				if (i1 != i2) {
					return i1 - i2;
				}
			} catch (NumberFormatException e) {
				int tc = tok1.compareToIgnoreCase(tok2);

				if (tc != 0) {
					return tc;
				}
			}
		}
		if (s1.hasMoreTokens()) {
			return 1;
		}
		if (s2.hasMoreTokens()) {
			return -1;
		}
		return 0;
	}

	public RobotSpecification createRobotSpecification() {
		return HiddenAccess.createSpecification(this, getName(), getAuthorName(),
				(getWebpage() != null) ? getWebpage().toString() : null, getVersion(), getRobocodeVersion(),
				(getJarFile() != null) ? getJarFile().toString() : null, getName(), getDescription());
	}
}
