/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import java.util.*;
import java.util.jar.*;

import javax.swing.*;

import robocode.peer.robot.*;
import robocode.util.*;
import robocode.repository.*;


public class RobotRepositoryManager {

	boolean alwaysYes = false, alwaysNo = false;
	private FileSpecificationDatabase robotDatabase = null;

	private File robotsDirectory = null;
	private File robotCache = null;
	
	private Repository repository = null;
	private boolean cacheWarning = false;
	private RobocodeManager manager = null;
	private Vector updatedJarVector = new Vector();
	private boolean write = false;
	private boolean duplicatePrompt = false;

	public RobotRepositoryManager(RobocodeManager manager) {
		this.manager = manager;
		String classpath = System.getProperty("java.class.path");
		// not even finished... but just in case.
		// if (classpath != null && classpath.toLowerCase().indexOf(getWriteRobotPath().toLowerCase()) >= 0)
		// log("Robocode halted:  " + getWriteRobotPath() + " may not be in your classpath!");
	}

	private void log(String s) {
		Utils.log(s);
	}

	private void log(String s, Throwable t) {
		Utils.log(s, t);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}

	public ImageManager getImageManager() {
		return manager.getImageManager();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/24/2001 12:31:19 PM)
	 * @return java.lang.String
	 */
	public File getRobotCache() {
		if (robotCache == null) {
			// Doesn't work with my cwd(), File does not resolve absolute path correctly
			// (as far as I can see from the Javadocs for this constructor)
			// ---> robotCache = System.getProperty("ROBOTCACHE");
		
		
			File oldRobotCache = new File(Constants.cwd(), "robotcache");

			if (oldRobotCache.exists()) {
				oldRobotCache.renameTo(new File(Constants.cwd(), ".robotcache"));
			}
		
			robotCache = new File(Constants.cwd(), ".robotcache");
		}
		return robotCache;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:43:41 PM)
	 */
	private FileSpecificationDatabase getRobotDatabase() {
		if (robotDatabase == null) {
			Utils.setStatus("Reading robot database");
			robotDatabase = new FileSpecificationDatabase();
			try {
				robotDatabase.load(new File(Constants.cwd(), "robot.database"));
			} catch (FileNotFoundException e) {
				log("Building robot database.");
				duplicatePrompt = true;
			} catch (IOException e) {
				log("Rebuilding robot database.");
				duplicatePrompt = true;
				// log("IO Exception reading robot database: " + e);
			} catch (ClassNotFoundException e) {
				log("Rebuilding robot database.");
				duplicatePrompt = true;
				// log("ClassNotFoundException reading robot database: " + e);
			}
		}
		return robotDatabase;

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 6:16:36 PM)
	 * @return java.util.Vector
	 */
	public Repository getRobotRepository() {

		// Don't reload the repository
		// If we want to do that, set repository to null by calling clearRobotList().
		if (repository != null) {
			return repository;
		}

		Utils.setStatus("Refreshing robot database");

		alwaysYes = false;
		alwaysNo = false;
			
		updatedJarVector.clear();

		// jarUpdated = false;
		cacheWarning = false;
		// boolean changed = false;
		this.write = false;

		// Future...
		// Remove any deleted jars from robotcache
	
		
		repository = new Repository(this);
	
		FileSpecificationVector currentDirFileSpecifications = null;
	
		// Clean up cache -- delete nonexistent jar directories
		cleanupCache();
		Utils.setStatus("Cleaning up robot database");
		cleanupDatabase();
	
		String externalRobotsPath = manager.getProperties().getOptionsDevelopmentPath(); {
			StringTokenizer tokenizer = new StringTokenizer(externalRobotsPath, File.pathSeparator);

			while (tokenizer.hasMoreTokens()) {
				String tok = tokenizer.nextToken();
				File f = new File(tok);

				if (!f.equals(getRobotsDirectory()) && !f.equals(getRobotCache())
						&& !f.equals(getRobotsDirectory().getParentFile())) {
					getSpecificationsInDirectory(f, f, "", true);
				}
			}
		}
		updatedJarVector.clear();
	
		File f = getRobotsDirectory();

		Utils.setStatus("Reading: " + f.getName());
		if (f.exists() && f.isDirectory()) // it better be!
		{
			currentDirFileSpecifications = getSpecificationsInDirectory(f, f, "", true);
		}

		for (int i = 0; i < updatedJarVector.size(); i++) {
			processJar((JarSpecification) updatedJarVector.elementAt(i));
			updateRobotDatabase((JarSpecification) updatedJarVector.elementAt(i));
			write = true;
		}
		updatedJarVector.clear();
	
		f = getRobotCache();
		Utils.setStatus("Reading: " + getRobotCache());
		if (f.exists() && f.isDirectory()) // it better be!
		{
			currentDirFileSpecifications = getSpecificationsInDirectory(f, f, "", false);
		}
	
		FileSpecificationVector fileSpecificationVector = getRobotDatabase().getFileSpecifications();
	
		if (write) {
			Utils.setStatus("Saving robot database");
			saveRobotDatabase();
		}

		Utils.setStatus("Adding robots to repository");
		// FileSpecificationVector fileSpecificationVector = getRobotDatabase().getFileSpecifications();
		for (int i = 0; i < fileSpecificationVector.size(); i++) {
			// if(fileSpecificationVector.elementAt(i).isDuplicate())
			// continue;
			if (fileSpecificationVector.elementAt(i) instanceof TeamSpecification) {
				repository.add(fileSpecificationVector.elementAt(i));
				continue;
			} else if (fileSpecificationVector.elementAt(i) instanceof RobotSpecification) {
				if (verifyRootPackage(fileSpecificationVector.elementAt(i).getName())) {
					// log("Adding " + fileSpecification + " to repository");
					repository.add(fileSpecificationVector.elementAt(i));
					continue;
				}
			}
		}

		if (cacheWarning) {
			JOptionPane.showMessageDialog(null,
					"Warning:  Robocode has detected that the robotcache directory has been updated.\n"
					+ "Robocode may delete or overwrite these files with no warning.\n"
					+ "If you wish to update a robot in the robotcache directory,\n"
					+ "You should copy it to your robots directory first.",
					"Unexpected robotcache update",
					JOptionPane.OK_OPTION);
		}
		Utils.setStatus("Sorting repository");
		repository.sortRobotSpecifications();
		Utils.setStatus("");
	
		duplicatePrompt = false;
		return repository;
	}

	private void cleanupCache() {
		File dir = getRobotCache();
		File files[] = dir.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() && files[i].getName().lastIndexOf(".jar_") == files[i].getName().length() - 5
						|| files[i].isDirectory()
								&& files[i].getName().lastIndexOf(".zip_") == files[i].getName().length() - 5
								|| files[i].isDirectory()
										&& files[i].getName().lastIndexOf(".jar") == files[i].getName().length() - 4) {
					File f = new File(getRobotsDirectory(),
							files[i].getName().substring(0, files[i].getName().length() - 1));

					// startsWith robocode added in 0.99.5 to fix bug with people
					// downloading robocode-setup.jar to the robots dir
					if (f.exists() && !f.getName().startsWith("robocode")) {
						continue;
					} else {
						Utils.setStatus("Cleaning up cache: Removing " + files[i]);
						Utils.deleteDir(files[i]);
					}
				}	
			}
		}
	}

	private void cleanupDatabase() {
		Vector externalDirectories = new Vector();
		String externalPath = manager.getProperties().getOptionsDevelopmentPath();
		StringTokenizer tokenizer = new StringTokenizer(externalPath, File.pathSeparator);

		while (tokenizer.hasMoreTokens()) {
			File f = new File(tokenizer.nextToken());

			externalDirectories.add(f);
		}
	
		FileSpecificationVector fileSpecificationVector = getRobotDatabase().getFileSpecifications();

		for (int i = 0; i < fileSpecificationVector.size(); i++) {
			if (fileSpecificationVector.elementAt(i).exists()) {
				File rootDir = fileSpecificationVector.elementAt(i).getRootDir();

				if (rootDir == null) {
					log("Warning, null root directory: " + fileSpecificationVector.elementAt(i).getFilePath());
					continue;
				}
				
				if (!fileSpecificationVector.elementAt(i).isDevelopmentVersion()) {
					continue;
				}
				if (rootDir.equals(getRobotsDirectory())) {
					continue;
				}
				if (externalDirectories.contains(rootDir)) {
					continue;
				}
				
				// This one is from the developmentPath; make sure that path still exists.
				// log("Removing: " + fileSpecificationVector.elementAt(i).getFilePath());
				getRobotDatabase().remove(fileSpecificationVector.elementAt(i).getFilePath());
				write = true;
			} else {
				// log("Removing: " + fileSpecificationVector.elementAt(i).getFilePath());
				getRobotDatabase().remove(fileSpecificationVector.elementAt(i).getFilePath());
				write = true;
			}
		}
	}

	/*
	 private boolean processSpecifications(FileSpecificationVector fileSpecifications)
	 {
	 boolean write = false;
	 for (int i = 0; i < fileSpecifications.size(); i++)
	 {
	 FileSpecification fileSpecification = (FileSpecification)fileSpecifications.elementAt(i);
	 FileSpecification cachedSpecification = getRobotDatabase().get(fileSpecification.getFilePath());
	 
	 if (!fileSpecification.isSameFile(cachedSpecification))
	 {
	 updateRobotDatabase(fileSpecification);
	 write = true;
	 cachedSpecification = getRobotDatabase().get(fileSpecification.getFilePath());
	 fileSpecification = cachedSpecification;
	 if (fileSpecification instanceof JarSpecification)
	 {
	 //log(fileSpecification + " is changed.");
	 updatedJarVector.add(fileSpecification);
	 }
	 }
	 else
	 fileSpecification = cachedSpecification;
	 }
	 return write;

	 }
	 */

	/**
	 * Insert the method's description here.
	 * Creation date: (9/24/2001 12:31:19 PM)
	 * @return java.lang.String
	 */
	public File getRobotsDirectory() {
		if (robotsDirectory == null) {
			robotsDirectory = new File(Constants.cwd(), "robots");
		
		}
		return robotsDirectory;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 6:16:36 PM)
	 * @return java.util.Vector
	 */
	public void clearRobotList() {
		repository = null;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/29/2000 12:29:38 PM)
	 * @param dir java.io.File
	 * @param prefix java.lang.String
	 * @param robotList java.util.Vector
	 */
	private FileSpecificationVector getSpecificationsInDirectory(File rootDir, File dir, String prefix, boolean isDevelopmentDirectory) {
		// log("getting specs in directory " + rootDir + " - " + dir + " with prefix " + prefix);
		FileSpecificationVector robotList = new FileSpecificationVector();
		// Order is important?
		String fileTypes[] = { ".class", ".jar", ".team", ".jar.zip"};

		File files[] = dir.listFiles(new RobocodeFileFilter(fileTypes));

		if (files == null) {
			log("Warning:  Unable to read directory " + dir);
			return robotList;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if (prefix.equals("")) {
					int jidx = files[i].getName().lastIndexOf(".jar_");

					if (jidx > 0 && jidx == files[i].getName().length() - 5) {
						robotList.add(getSpecificationsInDirectory(files[i], files[i], "", isDevelopmentDirectory));
					} else {
						jidx = files[i].getName().lastIndexOf(".zip_");
						if (jidx > 0 && jidx == files[i].getName().length() - 5) {
							// log("Adding: " + files[i].getName());
							robotList.add(getSpecificationsInDirectory(files[i], files[i], "", isDevelopmentDirectory));
						} else {
							robotList.add(
									getSpecificationsInDirectory(rootDir, files[i], prefix + files[i].getName() + ".",
									isDevelopmentDirectory));
						}
					}
				} else {
					int odidx = files[i].getName().indexOf("data.");

					if (odidx == 0) {
						renameOldDataDir(dir, files[i]);
						continue;
					}
				
					int didx = files[i].getName().lastIndexOf(".data");

					if (didx > 0 && didx == files[i].getName().length() - 5) {
						continue;
					} // Don't process .data dirs
					robotList.add(
							getSpecificationsInDirectory(rootDir, files[i], prefix + files[i].getName() + ".",
							isDevelopmentDirectory));
				}
			} else if (files[i].getName().indexOf("$") < 0 && files[i].getName().indexOf("robocode") != 0) {
				// log("Searching for " + files[i].getPath() + " in cache.");
				FileSpecification cachedSpecification = getRobotDatabase().get(files[i].getPath());
				FileSpecification fileSpecification = null;

				// if (cachedSpecification == null)
				// log("This is a new file.");
				if (cachedSpecification != null
						&& cachedSpecification.isSameFile(files[i].getPath(), files[i].length(), files[i].lastModified())) {
					// log("This file is unchanged.");
					fileSpecification = cachedSpecification;
				} else {
					// if (cachedSpecification != null)
					// log("This file has changed.");
					fileSpecification = FileSpecification.createSpecification(this, files[i], rootDir, prefix,
							isDevelopmentDirectory);
					updateRobotDatabase(fileSpecification);
					write = true;
					if (fileSpecification instanceof JarSpecification) {
						// log(fileSpecification + " is changed.");
						// if (prefix.equals("") && isDevelopmentDirectory == true)
						// {
						updatedJarVector.add(fileSpecification);
						// }
					}
				}
				if (fileSpecification.getValid()) {
					robotList.add(fileSpecification);
				}
			}
		}
		return robotList;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:43:41 PM)
	 */
	private void saveRobotDatabase() {
		if (robotDatabase == null) {
			log("Cannot save a null robot database.");
			return;
		}
		try {
			// log("*** saving robot database ***");
			robotDatabase.store(new File(Constants.cwd(), "robot.database"));
		} catch (IOException e) {
			log("IO Exception writing robot database: " + e);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/30/2001 11:14:56 AM)
	 * @return boolean
	 * @param f java.io.File
	 */

	/* unused
	 private boolean treeChanged(File f) {
	 boolean changed = false;
	 
	 String key = f.getPath();
	 String length = ""+f.length();
	 String lastModified = ""+f.lastModified();
	 String value = length + "," + lastModified;
	 FileSpecification cachedSpec = getRobotDatabase().get(key);
	 if (v == null)
	 {
	 changed = true;
	 System.out.println(key + " has been created.");
	 getRobotDatabase().setProperty(key,value);
	 }
	 else
	 {
	 if (!v.substring(0,value.length()).equals(value))
	 {
	 changed = true;
	 System.out.println(key + " has been modified.");
	 getRobotDatabase().setProperty(key,value);
	 }
	 else
	 System.out.println("for " + key + ", " + v + " == " + value);
	 }

	 if (!f.isDirectory())
	 return changed;

	 File files[] = f.listFiles();
	 for (int i = 0; i < files.length; i++)
	 {
	 //if (files[i].isDirectory())
	 //{
	 if (treeChanged(files[i]))
	 changed = true;
	 //}
	 }

	 return changed;
	 }
	 */

	/**
	 * Insert the method's description here.
	 * Creation date: (10/29/2001 4:11:56 PM)
	 * @param props robocode.util.RobotProperties
	 */
	private void updateRobotDatabase(FileSpecification fileSpecification) {

		String key = fileSpecification.getFilePath();
		boolean updated = false;

		if (fileSpecification instanceof RobotSpecification) {
			RobotSpecification robotSpecification = (RobotSpecification) fileSpecification;
			String name = robotSpecification.getName();
					
			try {
				RobotClassManager robotClassManager = new RobotClassManager(robotSpecification);
				Class robotClass = robotClassManager.getRobotClassLoader().loadRobotClass(
						robotClassManager.getFullClassName(), true);

				robotSpecification.setUid(robotClassManager.getUid());

				Class[] interfaces = robotClass.getInterfaces();

				for (int j = 0; j < interfaces.length; j++) {
					if (interfaces[j].getName().equals("robocode.Droid")) {
						robotSpecification.setDroid(true);
					}
				}
					
				Class superClass = robotClass.getSuperclass();
			
				if (java.lang.reflect.Modifier.isAbstract(robotClass.getModifiers())) {
					// System.out.println("Skipping abstract class: " + robotClass);
					superClass = null;
				}
			
				if (robotSpecification.getValid()) {
					while (!updated && superClass != null && !superClass.getName().equals("java.lang.Object")) {
						// XXXXXX
						// System.out.println("The superclass is: " + superClass);
						if (superClass.getName().equals("robocode.TeamRobot")) {
							robotSpecification.setTeamRobot(true);
						}
						if (superClass.getName().equals("robocode.Robot")) {
							updateNoDuplicates(robotSpecification);
							return;
						} else {
							superClass = superClass.getSuperclass();
						}
					}
				}
				// If we get here, it's just a class not a robot
				// log("Just a class: " + robotClass.getName());
				getRobotDatabase().put(key, new ClassSpecification(robotSpecification));
			} catch (ClassNotFoundException e) {
				getRobotDatabase().put(key, robotSpecification); // new ClassSpecification(robotSpecification));
			} catch (ClassCastException e) {
				// Should not happen here.
				getRobotDatabase().put(key, robotSpecification); // new ClassSpecification(robotSpecification));
				log(name + " is not a robot.");
			} catch (ClassFormatError e) {
				getRobotDatabase().put(key, robotSpecification); // new ClassSpecification(robotSpecification));
				log(name + " is not a valid .class file: " + e);
			} catch (Exception e) {
				getRobotDatabase().put(key, robotSpecification); // new ClassSpecification(robotSpecification));
				log(name + " :  Something is wrong with this class: " + e);
			} catch (Error e) {
				getRobotDatabase().put(key, robotSpecification); // new ClassSpecification(robotSpecification));
				log(name + " : Something is wrong with this class: " + e);
			}
		} else if (fileSpecification instanceof JarSpecification) {
			getRobotDatabase().put(key, fileSpecification);
		} else if (fileSpecification instanceof TeamSpecification) {
			updateNoDuplicates(fileSpecification);

			/*
			 TeamSpecification teamSpecification = (TeamSpecification)fileSpecification;
			 if (!teamSpecification.isDevelopmentVersion() && getRobotDatabase().containsTeam(teamSpecification.getFullClassName(),teamSpecification.getVersion(),false))
			 {
			 TeamSpecification existingSpec = getRobotDatabase().getTeam(teamSpecification.getFullClassName(),teamSpecification.getVersion(),false);
			 if (existingSpec.getFileLastModified() <= teamSpecification.getFileLastModified())
			 {
			 teamSpecification.setDuplicate(true);
			 }
			 else
			 existingSpec.setDuplicate(true);
			 }
			 getRobotDatabase().put(key,fileSpecification);
			 */
		} else if (fileSpecification instanceof ClassSpecification) {
			getRobotDatabase().put(key, fileSpecification);
		} else {
			System.out.println("Update robot database not possible for type " + fileSpecification.getFileType());
		}
	}

	private void updateNoDuplicates(FileSpecification spec) {
		String key = spec.getFilePath();

		// log("Updating robot database for robot: " + spec.getName() + (spec.getVersion() == null?"": " " + spec.getVersion()));
		Utils.setStatus("Updating database: " + spec.getName());
		if (!spec.isDevelopmentVersion()
				&& getRobotDatabase().contains(spec.getFullClassName(), spec.getVersion(), false)) {
			FileSpecification existingSpec = getRobotDatabase().get(spec.getFullClassName(), spec.getVersion(), false);

			if (existingSpec == null) {
				getRobotDatabase().put(key, spec);
			} else if (!existingSpec.getUid().equals(spec.getUid())) {
				if (existingSpec.getFilePath().equals(spec.getFilePath())) {
					// log("File replaced, updating.");
					getRobotDatabase().put(key, spec);
				} else // if (duplicatePrompt)
				{
					File existingSource = existingSpec.getJarFile(); // getRobotsDirectory(),getRobotCache());
					File newSource = spec.getJarFile(); // getRobotsDirectory(),getRobotCache());

					if (existingSource != null && newSource != null) {
						long t1 = existingSource.lastModified();
						long t2 = newSource.lastModified();

						if (t1 > t2) {
							existingSource.renameTo(new File(existingSource.getPath() + ".invalid"));
							getRobotDatabase().remove(existingSpec.getFilePath());
							getRobotDatabase().put(key, spec);
							conflictLog(
									"Renaming " + existingSource + " to invalid, as it contains a robot " + spec.getName()
									+ " which conflicts with the same robot in " + newSource);
						} else {
							newSource.renameTo(new File(newSource.getPath() + ".invalid"));
							conflictLog(
									"Renaming " + newSource + " to invalid, as it contains a robot " + spec.getName()
									+ " which conflicts with the same robot in " + existingSource);
						}
					}
					// log("Pop up dialog here -- which robot to use? (" + robotSpecification.getPackageFile(getRobotsDirectory(),getRobotCache()) + " != " + robotSpecification.getUid() + ")");
				}

				/* else
				 {
				 File f = spec.getPackageFile(getRobotsDirectory(),getRobotCache());
				 if (f != null)
				 {
				 f.renameTo(new File(f.getPath() + ".invalid"));
				 conflictLog("Renaming " + f + " to invalid, as it contains a robot " + spec.getName() + " which conflicts with the same robot in " + existingSpec.getPackageFile(getRobotsDirectory(),getRobotCache()));
				 }
				 //log(robotSpecification.getPackageFile(getRobotsDirectory(),getRobotCache()));
				 // Do NOT use this robot.
				 }
				 */
			} else {
				// log("Exact same robot already exists: " + existingSpec.getUid());
				spec.setDuplicate(true);
				getRobotDatabase().put(key, spec);
			}

			/* if (existingSpec.getFileLastModified() <= robotSpecification.getFileLastModified())
			 {
			 robotSpecification.setDuplicate(true);
			 }
			 else
			 existingSpec.setDuplicate(true);
			 */
			// log(robotSpecification + " is a duplicate of " + getRobotDatabase().getRobot(					robotSpecification.getFullClassName(),robotSpecification.getVersion()));
		} else {
			getRobotDatabase().put(key, spec);
		}
		return;
	}

	private void conflictLog(String s) {
		log(s);
		try {
			File f = new File(Constants.cwd(), "conflict.log");
			BufferedWriter out = new BufferedWriter(new FileWriter(f.getPath(), true));

			out.write(s + "\n");
			out.close();
		} catch (Exception e) {
			log("Warning:  Could not write to conflict.log");
		}
	}

	private void processJar(JarSpecification jarSpecification) {
		String key = jarSpecification.getFilePath();

		File cache = getRobotCache();

		if (!cache.exists()) {
			cache.mkdirs();
			File readme = new File(cache, "README");

			try {
				PrintStream out = new PrintStream(new FileOutputStream(readme));

				out.println("WARNING!");
				out.println("Do not edit files in this directory.");
				out.println("Any changes you make here may be lost.");
				out.println("If you want to make changes to these robots,");
				out.println("then copy the files into your robots directory");
				out.println("and make the changes there.");
				out.close();
			} catch (IOException e) {}
		}
		// System.out.println("Update Robot Database for a .jar");
		Utils.setStatus("Extracting .jar: " + jarSpecification.getFileName());
		
		// log(""+jarSpecification.getRootDir() + " - " + getRobotsDirectory());
		File dest;

		if (jarSpecification.getRootDir().equals(getRobotsDirectory())) {
			dest = new File(getRobotCache(), jarSpecification.getFileName() + "_");
		} else {
			dest = new File(jarSpecification.getRootDir(), jarSpecification.getFileName() + "_");
		}
		boolean sameFile = false;

		if (dest.exists()) {
			sameFile = true;
			Utils.deleteDir(dest);
		}
		dest.mkdirs();
	
		File f = new File(jarSpecification.getFilePath());

		extractJar(f, dest, "Extracting .jar: " + jarSpecification.getFileName(), true, true, true);
		getRobotDatabase().put(key, jarSpecification);
	}

	public int extractJar(File f, File dest, String statusPrefix, boolean extractJars, boolean close, boolean alwaysReplace) {
		try {
			JarInputStream jarIS = new JarInputStream(new FileInputStream(f));

			return extractJar(jarIS, dest, statusPrefix, extractJars, close, alwaysReplace);
		} catch (Exception e) {
			log("Exception reading " + f + ": " + e);
		}
		return 16;
	}

	public int extractJar(JarInputStream jarIS, File dest, String statusPrefix, boolean extractJars, boolean close, boolean alwaysReplace) {
		int rc = 0;
		boolean always = alwaysReplace;
		byte buf[] = new byte[2048];

		try {
			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				// System.out.println("I see a jar entry: " + entry.getName() + " of size " + entry.getSize());
				Utils.setStatus(statusPrefix + " (" + entry.getName() + ")");
				if (entry.isDirectory()) {
					// System.out.println("Creating file " + entry.getName());
					File dir = new File(dest, entry.getName());

					dir.mkdirs();
					// if (entry.getTime() >= 0)
					// dir.setLastModified(entry.getTime());
				} else {
					// System.out.println("Extracting file " + entry.getName());
					/* if (entry.getName().indexOf("/") < 0 && Utils.getFileType(entry.getName()).equals(".jar"))
					 {
					 File out = new File(dest,entry.getName() + "_");
					 out.mkdirs();
					 extractJar(new JarInputStream(jarIS),out,statusPrefix + " - " + entry.getName(),false,false);
					 }
					 else
					 {
					 */
					File out = new File(dest, entry.getName());

					if (out.exists() && !always) {
						Object[] options = { "Yes to All", "Yes", "No", "Cancel" };
						int r = JOptionPane.showOptionDialog(null, entry.getName() + " exists.  Replace?", "Warning", 
								JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

						if (r == 0) {
							always = true;
						} else if (r == 2) {
							Utils.setStatus(entry.getName() + " skipped.");
							entry = jarIS.getNextJarEntry();
							continue;
						} else if (r == 3) {
							entry = null;
							rc = -1;
							continue;
						}
					}
					File parentDirectory = new File(out.getParent());

					parentDirectory.mkdirs();
					FileOutputStream fos = new FileOutputStream(out);
					
					int num = 0;

					while ((num = jarIS.read(buf, 0, 2048)) != -1) {
						fos.write(buf, 0, num);
					}
					
					FileDescriptor fd = fos.getFD();

					fd.sync();
					fos.close();
					if (entry.getTime() >= 0) {
						out.setLastModified(entry.getTime());
					}
						
					if (entry.getName().indexOf("/") < 0 && Utils.getFileType(entry.getName()).equals(".jar")) {
						FileSpecification fileSpecification = FileSpecification.createSpecification(this, out,
								parentDirectory, "", false);

						updatedJarVector.add(fileSpecification);
					}

					// }
				}
				entry = jarIS.getNextJarEntry();
			}
			if (close) {
				jarIS.close();
			}
			// processSpecifications(getSpecificationsInDirectory(dest,dest,"",false,false));
		
		} catch (Exception e) {
			log("Exception " + statusPrefix + ": " + e);
		}
		return rc;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/1/2001 5:39:08 PM)
	 */
	// public boolean cleanupOldSampleRobots() {
	// return cleanupOldSampleRobots(true);
	// }

	public boolean cleanupOldSampleRobots(boolean delete) {
		String oldSampleList[] = {
			"Corners.java", "Crazy.java", "Fire.java", "MyFirstRobot.java", "RamFire.java", "SittingDuck.java",
			"SpinBot.java", "Target.java", "Tracker.java", "TrackFire.java", "Walls.java", "Corners.class",
			"Crazy.class", "Fire.class", "MyFirstRobot.class", "RamFire.class", "SittingDuck.class", "SpinBot.class",
			"Target.class", "Target$1.class", "Tracker.class", "TrackFire.class", "Walls.class"
		};
	  
		File f = getRobotsDirectory();

		if (f.isDirectory()) // it better be!
		{
			File[] sampleBots = f.listFiles();

			for (int i = 0; i < sampleBots.length; i++) {
				if (!sampleBots[i].isDirectory()) {
					for (int j = 0; j < oldSampleList.length; j++) {
						if (sampleBots[i].getName().equals(oldSampleList[j])) {
							log("Deleting old sample file: " + sampleBots[i].getName());
							if (delete) {
								sampleBots[i].delete();
							} else {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/2/2001 1:38:51 PM)
	 * @param dir java.io.File
	 * @param name java.lang.String
	 */
	private void renameOldDataDir(File dir, File f) {
		String name = f.getName();
		String botName = name.substring(name.indexOf(".") + 1);
		File newFile = new File(dir, botName + ".data");

		if (!newFile.exists()) {
			File oldFile = new File(dir, name);

			log("Renaming " + oldFile.getName() + " to " + newFile.getName());
			oldFile.renameTo(newFile);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 11:26:01 AM)
	 * @return boolean
	 * @param robotName java.lang.String
	 */
	public boolean verifyRootPackage(String robotName) {
		int lIndex = robotName.indexOf(".");

		if (lIndex > 0) {
			String rootPackage = robotName.substring(0, lIndex);

			if (rootPackage.equalsIgnoreCase("robocode")) {
				log("Robot " + robotName + " ignored.  You cannot use package " + rootPackage);
				return false;
			}
			if (rootPackage.equalsIgnoreCase("sample")) {
				if (robotName.equals("sample.Corners")) {
					return true;
				}
				if (robotName.equals("sample.Crazy")) {
					return true;
				}
				if (robotName.equals("sample.Fire")) {
					return true;
				}
				if (robotName.equals("sample.MyFirstRobot")) {
					return true;
				}
				if (robotName.equals("sample.RamFire")) {
					return true;
				}
				if (robotName.equals("sample.SittingDuck")) {
					return true;
				}
				if (robotName.equals("sample.SpinBot")) {
					return true;
				}
				if (robotName.equals("sample.Target")) {
					return true;
				}
				if (robotName.equals("sample.TrackFire")) {
					return true;
				}
				if (robotName.equals("sample.Tracker")) {
					return true;
				}
				if (robotName.equals("sample.Walls")) {
					return true;
				}
				// log("Robot " + robotName + " ignored.  You cannot add robots to package " + rootPackage);
				// Ok... return true here, don't want to lose novices on their first bot if they choose sample....
				return true; // false;
			}
		}
		int rIndex = robotName.lastIndexOf(".");

		if (rIndex > 0) {/*
			 try {
			 if (rIndex > 18)
			 {
			 log(robotName + " ignored.  Please limit the package name to 16 characters.");
			 return false;
			 }
			 String shortName = robotName.substring(rIndex + 1);
			 if (shortName.length() > 35) // Ok, Ecom... 3 free characters...
			 {
			 log(robotName + " ignored.  Please limit the name to 32 characters.");
			 return false;
			 }
			 
			 } catch (Exception e) {
			 log(robotName + " ignored.  This is not a valid name.");
			 return false;
			 }
			 */} else if (robotName.length() > 32) {// log(robotName + " ignored.  Please limit the name to 32 characters.");
			// return false;
		}
		return true;
			
	}

	/**
	 * Gets the manager.
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}

}

