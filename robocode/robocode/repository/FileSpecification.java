/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.repository;


import java.util.*;
import java.io.*;
import java.net.*;

import robocode.util.*;
import robocode.manager.*;


/**
 * Insert the type's description here.
 * Creation date: (10/11/2001 4:00:25 PM)
 * @author: Administrator
 */
public abstract class FileSpecification implements Comparable, Serializable, Cloneable {
	
	protected Properties props = new Properties();
	
	private final static String ROBOCODE_VERSION = "robocode.version";
	private final static String LIBRARY_DESCRIPTION = "library.description";
	
	private static int rebuild = 0;
	
	protected String name;
	protected String description;
	protected String authorName;
	protected String authorEmail;
	protected String authorWebsite;
	protected String version;
	protected String robocodeVersion;
	protected boolean developmentVersion = false;
	protected boolean valid = false;
	protected URL webpage;
	protected String libraryDescription;
	protected File rootDir = null;
	private File packageFile = null;
	
	private String filePath;
	private String fileName;
	private String propertiesFileName;
	private String thisFileName;
	private String fileType;
	private NameManager nameManager = null;
	private long fileLastModified;
	private long fileLength;

	private boolean duplicate = false;

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			log("Clone not supported!");
			return null;
		}
	}
		
	/**
	 * RobocodeProperties constructor comment.
	 */
	protected FileSpecification() {}

	public abstract String getUid();	
	
	public static FileSpecification createSpecification(RobotRepositoryManager repositoryManager, File f, File rootDir, String prefix, boolean developmentVersion) {
		String filename = f.getName();
		String fileType = Utils.getFileType(filename);
		
		FileSpecification newSpec = null;
		
		if (fileType.equals(".team")) {
			newSpec = new TeamSpecification(f, rootDir, prefix, developmentVersion);
		} else if (fileType.equals(".jar") || fileType.equals(".zip")) {
			newSpec = new JarSpecification(f, rootDir, prefix, developmentVersion);
		} else {
			newSpec = new RobotSpecification(f, rootDir, prefix, developmentVersion);
			if (!developmentVersion && newSpec.getValid() == false) {
				newSpec = new ClassSpecification((RobotSpecification) newSpec);
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
			// log("getPackageFile: " + rootDir.getParentFile() + " ? " + robotCacheDir);
			if (rootDir.getParentFile().equals(robotCacheDir)) {
				src = new File(robotDir, rootDir.getName().substring(0, rootDir.getName().length() - 1));
			} else if (rootDir.getParentFile().getParentFile().equals(robotCacheDir)) {
				// src = new File(robotDir,rootDir.getParentFile().getName().substring(0,rootDir.getParentFile().getName().length()-1));
				src = new File(rootDir.getParentFile(), rootDir.getName().substring(0, rootDir.getName().length() - 1));
			}
		}
		if (src != null && !src.exists()) {
			src = null;
		}
		this.packageFile = src;
	}
		
	public File getJarFile() // File robotDir, File robotCacheDir)
	{
		return packageFile;
		
		/*
		 */
	}
	
	public String toString() {
		return getFileName() + ": length " + getFileLength() + " modified " + getFileLastModified();
	}
	
	public boolean isDevelopmentVersion() {
		return developmentVersion;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/30/2001 3:33:44 PM)
	 * @return boolean
	 * @param o java.lang.Object
	 */
	public int compareTo(Object o) {

		/* if (this instanceof TeamSpecification && o instanceof RobotSpecification)
		 return -1;
		 if (this instanceof RobotSpecification && o instanceof TeamSpecification)
		 return 1;
		 */
		if (o instanceof FileSpecification) {
			FileSpecification other = (FileSpecification) o;
			
			return Utils.compare(getNameManager().getFullPackage(), getNameManager().getFullClassName(),
					getNameManager().getVersion(), other.getNameManager().getFullPackage(),
					other.getNameManager().getFullClassName(), other.getNameManager().getVersion());
			
		}
		return 0;
	}

	public boolean isSameFile(String filePath, long fileLength, long fileLastModified) {
		if (!filePath.equals(this.filePath)) {
			return false;
		}
		if (fileLength != this.fileLength) {
			return false;
		}
		if (fileLastModified != this.fileLastModified) {
			return false;
		}
		return true;
		
	}

	public boolean isSameFile(FileSpecification other) {
		if (other == null) {
			return false;
		}
		if (other.getFileLength() != getFileLength()) {
			return false;
		}
		if (other.getFileLastModified() != getFileLastModified()) {
			return false;
		}
		return true;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/26/2001 9:39:32 PM)
	 * @return boolean
	 * @param o java.lang.Object
	 */
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
			if (other.getFileLength() != getFileLength()) {
				return false;
			}
			if (other.getFileLastModified() != getFileLastModified()) {
				return false;
			}
			if (!other.getFileType().equals(getFileType())) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Gets the robotName.
	 * @return Returns a String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the robotDescription.
	 * @return Returns a String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the robotAuthorName.
	 * @return Returns a String
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * Gets the robotAuthorEmail.
	 * @return Returns a String
	 */
	public String getAuthorEmail() {
		return authorEmail;
	}

	/**
	 * Gets the robotAuthorWebsite.
	 * @return Returns a String
	 */
	public String getAuthorWebsite() {
		return authorWebsite;
	}

	/**
	 * Gets the robotVersion.
	 * @return Returns a String
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the robocodeVersion
	 * @return Returns a String
	 */
	public String getRobocodeVersion() {
		return robocodeVersion;
	}

	/**
	 * Sets the robocodeVersion
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
	 * @return Returns a String
	 */
	public String getThisFileName() {
		return thisFileName;
	}

	/**
	 * Sets the thisFilename.
	 * @param thisFilename The thisFilename to set
	 */
	public void setThisFileName(String thisFileName) {
		this.thisFileName = thisFileName;
	}

	/**
	 * Gets the filePath.
	 * @return Returns a String
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the filePath.
	 * @param filePath The filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Gets the propertiesFilename.
	 * @return Returns a String
	 */
	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	/**
	 * Sets the propertiesFilename.
	 * @param propertiesFilename The propertiesFilename to set
	 */
	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	/**
	 * Gets the filename.
	 * @return Returns a String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the filename.
	 * @param filename The filename to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the fileType.
	 * @return Returns a String
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * Sets the fileType.
	 * @param fileType The fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * Gets the robotWebpage.
	 * @return Returns a String
	 */
	public URL getWebpage() {
		return webpage;
	}

	/**
	 * Gets the fileLastModified.
	 * @return Returns a String
	 */
	public long getFileLastModified() {
		return fileLastModified;
	}

	/**
	 * Sets the fileLastModified.
	 * @param fileLastModified The fileLastModified to set
	 */
	public void setFileLastModified(long fileLastModified) {
		this.fileLastModified = fileLastModified;
	}

	/**
	 * Gets the fileLength.
	 * @return Returns a String
	 */
	public long getFileLength() {
		return fileLength;
	}

	/**
	 * Sets the fileLength.
	 * @param fileLength The fileLength to set
	 */
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public void store(OutputStream out, String desc) throws IOException {
		props.store(out, desc);
	}
	
	protected void load(FileInputStream in) throws IOException {
		props.load(in);
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
	
	private static void log(String s) {
		Utils.log(s);
	}
	
	private static void log(String s, Throwable t) {
		Utils.log(s, t);
	}
	
	private static void log(Throwable e) {
		Utils.log(e);
	}
	
	/**
	 * Gets the valid.
	 * @return Returns a boolean
	 */
	public boolean getValid() {
		return valid;
	}
	
	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean isDuplicate) {
		// if (isDuplicate)
		// log("Setting to duplicate.");
		this.duplicate = isDuplicate;	
	}

	public NameManager getNameManager() {
		if (nameManager == null) {
			if (this instanceof RobotSpecification) {
				nameManager = new NameManager(name, version, developmentVersion, false);
			} else if (this instanceof TeamSpecification) {
				nameManager = new NameManager(name, version, developmentVersion, true);
			} else {
				throw new RuntimeException("Cannot get a nameManager for file type " + getFileType());
			}
		}
		return nameManager;
	}
	
	public boolean exists() {
		return new File(getFilePath()).exists();
	}

	/**
	 * Gets the rootDir.
	 * @return Returns a File
	 */
	public File getRootDir() {
		return rootDir;
	}

	/**
	 * Sets the rootDir.
	 * @param rootDir The rootDir to set
	 */

	/* public void setRootDir(File rootDir) {
	 this.rootDir = rootDir;
	 }
	 */

}
