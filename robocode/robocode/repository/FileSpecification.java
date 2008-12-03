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
package robocode.repository;


import robocode.io.FileUtil;
import robocode.manager.IRepositoryManager;
import robocode.manager.NameManager;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public abstract class FileSpecification implements Comparable<FileSpecification>, Serializable, Cloneable {

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
	private String propertiesFileName;
	private String thisFileName;
	private String fileType;
	protected NameManager nameManager;
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
			newSpec = new TeamSpecification(f, rootDir, prefix, developmentVersion);
		} else if (fileType.equals(".jar") || fileType.equals(".zip")) {
			newSpec = new JarSpecification(f, rootDir, developmentVersion);
		} else {
			newSpec = new RobotFileSpecification(f, rootDir, prefix, developmentVersion);
			if (!(developmentVersion || newSpec.isValid())) {
				newSpec = new ClassSpecification((RobotFileSpecification) newSpec);
			}
		}
		newSpec.developmentVersion = developmentVersion;
		newSpec.rootDir = rootDir;
		newSpec.storeJarFile(repositoryManager.getRobotsDirectory(), repositoryManager.getRobotCache());

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

	public int compareTo(FileSpecification other) {
		return FileSpecification.compare(getNameManager().getFullPackage(), getNameManager().getFullClassName(),
				getNameManager().getVersion(), other.getNameManager().getFullPackage(),
				other.getNameManager().getFullClassName(), other.getNameManager().getVersion());
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

	/**
	 * Gets the UUID.
	 *
	 * @return Returns a String
	 */
	public String getUUID() {
		return uuid;
	}

	/**
	 * Sets the UUID.
	 */
	public void setUUID() {
		// Generate new UUID
		uuid = java.util.UUID.randomUUID().toString();

		props.setProperty(UUID, uuid);
	}

	/**
	 * Gets the robotName.
	 *
	 * @return Returns a String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the robotDescription.
	 *
	 * @return Returns a String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the robotAuthorName.
	 *
	 * @return Returns a String
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * Gets the robotAuthorEmail.
	 *
	 * @return Returns a String
	 */
	public String getAuthorEmail() {
		return authorEmail;
	}

	/**
	 * Gets the robotAuthorWebsite.
	 *
	 * @return Returns a String
	 */
	public String getAuthorWebsite() {
		return authorWebsite;
	}

	/**
	 * Gets the robotVersion.
	 *
	 * @return Returns a String
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the robocodeVersion
	 *
	 * @return Returns a String
	 */
	public String getRobocodeVersion() {
		return robocodeVersion;
	}

	/**
	 * Sets the robocodeVersion
	 *
	 * @param robocodeVersion to set
	 */
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

	/**
	 * Gets the thisFilename.
	 *
	 * @return Returns a String
	 */
	public String getThisFileName() {
		return thisFileName;
	}

	/**
	 * Sets the thisFilename.
	 *
	 * @param thisFileName The thisFilename to set
	 */
	public void setThisFileName(String thisFileName) {
		this.thisFileName = thisFileName;
	}

	/**
	 * Gets the filePath.
	 *
	 * @return Returns a String
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the filePath.
	 *
	 * @param filePath The filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Gets the propertiesFilename.
	 *
	 * @return Returns a String
	 */
	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	/**
	 * Sets the propertiesFilename.
	 *
	 * @param propertiesFileName The propertiesFilename to set
	 */
	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	/**
	 * Gets the filename.
	 *
	 * @return Returns a String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the filename.
	 *
	 * @param fileName The filename to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the fileType.
	 *
	 * @return Returns a String
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * Sets the fileType.
	 *
	 * @param fileType The fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * Gets the robotWebpage.
	 *
	 * @return Returns a String
	 */
	public URL getWebpage() {
		return webpage;
	}

	/**
	 * Gets the fileLastModified.
	 *
	 * @return Returns a String
	 */
	public long getFileLastModified() {
		return fileLastModified;
	}

	/**
	 * Sets the fileLastModified.
	 *
	 * @param fileLastModified The fileLastModified to set
	 */
	public void setFileLastModified(long fileLastModified) {
		this.fileLastModified = fileLastModified;
	}

	/**
	 * Gets the fileLength.
	 *
	 * @return Returns a String
	 */
	public long getFileLength() {
		return fileLength;
	}

	/**
	 * Sets the fileLength.
	 *
	 * @param fileLength The fileLength to set
	 */
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

	public String getFullPackage() {
		return getNameManager().getFullPackage();
	}

	public String getFullClassNameWithVersion() {
		return getNameManager().getFullClassNameWithVersion();
	}

	public String getFullClassName() {
		return getNameManager().getFullClassName();
	}

	public String getShortClassName() {
		return getNameManager().getShortClassName();
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

	public NameManager getNameManager() {
		throw new RuntimeException("Cannot get a nameManager for file type " + getFileType());
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
}
