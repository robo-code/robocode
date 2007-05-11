/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Updated to use methods from WindowUtil, FileTypeFilter, FileUtil, Logger,
 *       which replaces methods that have been (re)moved from the Utils class
 *     - Changed to use FileUtil.getRobotsDir()
 *     - Replaced multiple catch'es with a single catch in
 *       getSpecificationsInDirectory()
 *     - Minor optimizations
 *     - Added missing close() on FileInputStream
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     - Changed so that the robot repository only adds .jar files from the root
 *       of the robots folder and not from sub folders of the robots folder
 *******************************************************************************/
package robocode.manager;


import static robocode.io.Logger.log;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.JOptionPane;

import robocode.dialog.WindowUtil;
import robocode.io.FileTypeFilter;
import robocode.io.FileUtil;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotRepositoryManager {
	private FileSpecificationDatabase robotDatabase;

	private File robotsDirectory;
	private File robotCache;

	private Repository repository;
	private boolean cacheWarning;
	private RobocodeManager manager;

	private List<FileSpecification> updatedJarList = Collections.synchronizedList(new ArrayList<FileSpecification>());
	private boolean write;

	public RobotRepositoryManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public File getRobotCache() {
		if (robotCache == null) {
			// Doesn't work with my cwd(), File does not resolve absolute path correctly
			// (as far as I can see from the Javadocs for this constructor)
			// ---> robotCache = System.getProperty("ROBOTCACHE");


			File oldRobotCache = new File(FileUtil.getRobotsDir(), "robotcache");

			if (oldRobotCache.exists()) {
				oldRobotCache.renameTo(new File(FileUtil.getRobotsDir(), ".robotcache"));
			}

			robotCache = new File(FileUtil.getRobotsDir(), ".robotcache");
		}
		return robotCache;
	}

	private FileSpecificationDatabase getRobotDatabase() {
		if (robotDatabase == null) {
			WindowUtil.setStatus("Reading robot database");
			robotDatabase = new FileSpecificationDatabase();
			try {
				robotDatabase.load(new File(FileUtil.getRobotsDir(), "robot.database"));
			} catch (FileNotFoundException e) {
				log("Building robot database.");
			} catch (IOException e) {
				log("Rebuilding robot database.");
			} catch (ClassNotFoundException e) {
				log("Rebuilding robot database.");
			}
		}
		return robotDatabase;
	}

	public Repository getRobotRepository() {
		// Don't reload the repository
		// If we want to do that, set repository to null by calling clearRobotList().
		if (repository != null) {
			return repository;
		}

		WindowUtil.setStatus("Refreshing robot database");

		updatedJarList.clear();

		// jarUpdated = false;
		cacheWarning = false;
		// boolean changed = false;
		this.write = false;

		// Future...
		// Remove any deleted jars from robotcache

		repository = new Repository();

		// Clean up cache -- delete nonexistent jar directories
		cleanupCache();
		WindowUtil.setStatus("Cleaning up robot database");
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
		updatedJarList.clear();

		File f = getRobotsDirectory();

		WindowUtil.setStatus("Reading: " + f.getName());
		if (f.exists() && f.isDirectory()) { // it better be!
			getSpecificationsInDirectory(f, f, "", true);
		}

		// This loop should not be changed to an for-each loop as the updated jar list
		// gets updated (jars are added) by the methods called in this loop, which can
		// cause a ConcurrentModificationException!
		for (int i = 0; i < updatedJarList.size(); i++) {
			JarSpecification updatedJar = (JarSpecification) updatedJarList.get(i);

			processJar(updatedJar);
			updateRobotDatabase(updatedJar);
			write = true;
		}
		updatedJarList.clear();

		f = getRobotCache();
		WindowUtil.setStatus("Reading: " + getRobotCache());
		if (f.exists() && f.isDirectory()) { // it better be!
			getSpecificationsInDirectory(f, f, "", false);
		}

		List<FileSpecification> fileSpecificationList = getRobotDatabase().getFileSpecifications();

		if (write) {
			WindowUtil.setStatus("Saving robot database");
			saveRobotDatabase();
		}

		WindowUtil.setStatus("Adding robots to repository");

		for (FileSpecification fs : fileSpecificationList) {
			if (fs instanceof TeamSpecification) {
				repository.add(fs);
				continue;
			} else if (fs instanceof RobotSpecification) {
				if (verifyRootPackage(fs.getName())) {
					repository.add(fs);
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
		WindowUtil.setStatus("Sorting repository");
		repository.sortRobotSpecifications();
		WindowUtil.setStatus("");

		return repository;
	}

	private void cleanupCache() {
		File dir = getRobotCache();
		File files[] = dir.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory() && file.getName().lastIndexOf(".jar_") == file.getName().length() - 5
						|| file.isDirectory() && file.getName().lastIndexOf(".zip_") == file.getName().length() - 5
						|| file.isDirectory() && file.getName().lastIndexOf(".jar") == file.getName().length() - 4) {
					File f = new File(getRobotsDirectory(), file.getName().substring(0, file.getName().length() - 1));

					// startsWith robocode added in 0.99.5 to fix bug with people
					// downloading robocode-setup.jar to the robots dir
					if (f.exists() && !f.getName().startsWith("robocode")) {
						continue;
					}
					WindowUtil.setStatus("Cleaning up cache: Removing " + file);
					FileUtil.deleteDir(file);
				}
			}
		}
	}

	private void cleanupDatabase() {
		List<File> externalDirectories = Collections.synchronizedList(new ArrayList<File>());
		String externalPath = manager.getProperties().getOptionsDevelopmentPath();
		StringTokenizer tokenizer = new StringTokenizer(externalPath, File.pathSeparator);

		while (tokenizer.hasMoreTokens()) {
			File f = new File(tokenizer.nextToken());

			externalDirectories.add(f);
		}

		List<FileSpecification> fileSpecificationList = getRobotDatabase().getFileSpecifications();

		for (FileSpecification fs : fileSpecificationList) {
			if (fs.exists()) {
				File rootDir = fs.getRootDir();

				if (rootDir == null) {
					log("Warning, null root directory: " + fs.getFilePath());
					continue;
				}

				if (!fs.isDevelopmentVersion()) {
					continue;
				}
				if (rootDir.equals(getRobotsDirectory())) {
					continue;
				}
				if (externalDirectories.contains(rootDir)) {
					continue;
				}

				// This one is from the developmentPath; make sure that path still exists.
				getRobotDatabase().remove(fs.getFilePath());
				write = true;
			} else {
				getRobotDatabase().remove(fs.getFilePath());
				write = true;
			}
		}
	}

	public File getRobotsDirectory() {
		if (robotsDirectory == null) {
			robotsDirectory = FileUtil.getRobotsDir();
		}
		return robotsDirectory;
	}

	public void clearRobotList() {
		repository = null;
	}

	private List<FileSpecification> getSpecificationsInDirectory(File rootDir, File dir, String prefix, boolean isDevelopmentDirectory) {
		List<FileSpecification> robotList = Collections.synchronizedList(new ArrayList<FileSpecification>());

		// Order is important?
		String fileTypes[] = {
			".class", ".jar", ".team", ".jar.zip"
		};
		File files[] = dir.listFiles(new FileTypeFilter(fileTypes));

		if (files == null) {
			log("Warning:  Unable to read directory " + dir);
			return robotList;
		}

		for (File file : files) {
			String fileName = file.getName();

			if (file.isDirectory()) {
				if (prefix.length() == 0) {
					int jidx = fileName.lastIndexOf(".jar_");

					if (jidx > 0 && jidx == fileName.length() - 5) {
						robotList.addAll(getSpecificationsInDirectory(file, file, "", isDevelopmentDirectory));
					} else {
						jidx = fileName.lastIndexOf(".zip_");
						if (jidx > 0 && jidx == fileName.length() - 5) {
							robotList.addAll(getSpecificationsInDirectory(file, file, "", isDevelopmentDirectory));
						} else {
							robotList.addAll(
									getSpecificationsInDirectory(rootDir, file, prefix + fileName + ".",
									isDevelopmentDirectory));
						}
					}
				} else {
					int odidx = fileName.indexOf("data.");

					if (odidx == 0) {
						renameOldDataDir(dir, file);
						continue;
					}

					int didx = fileName.lastIndexOf(".data");

					if (didx > 0 && didx == fileName.length() - 5) {
						continue;
					} // Don't process .data dirs
					robotList.addAll(
							getSpecificationsInDirectory(rootDir, file, prefix + fileName + ".", isDevelopmentDirectory));
				}
			} else if (fileName.indexOf("$") < 0 && fileName.indexOf("robocode") != 0) {
				FileSpecification cachedSpecification = getRobotDatabase().get(file.getPath());
				FileSpecification fileSpecification = null;

				// if cachedSpecification is null, then this is a new file
				if (cachedSpecification != null
						&& cachedSpecification.isSameFile(file.getPath(), file.length(), file.lastModified())) {
					// this file is unchanged
					fileSpecification = cachedSpecification;
				} else {
					fileSpecification = FileSpecification.createSpecification(this, file, rootDir, prefix,
							isDevelopmentDirectory);
					updateRobotDatabase(fileSpecification);
					write = true;
					if (fileSpecification instanceof JarSpecification) {
						String path = fileSpecification.getFilePath();

						path = path.substring(0, path.lastIndexOf(File.separatorChar));
						path = path.substring(path.lastIndexOf(File.separatorChar) + 1);

						if (path.equalsIgnoreCase("robots")) {
							// this file is changed
							updatedJarList.add(fileSpecification);
						}
					}
				}
				if (fileSpecification.getValid()) {
					robotList.add(fileSpecification);
				}
			}
		}
		return robotList;
	}

	private void saveRobotDatabase() {
		if (robotDatabase == null) {
			log("Cannot save a null robot database.");
			return;
		}
		try {
			robotDatabase.store(new File(FileUtil.getRobotsDir(), "robot.database"));
		} catch (IOException e) {
			log("IO Exception writing robot database: " + e);
		}
	}

	private void updateRobotDatabase(FileSpecification fileSpecification) {
		String key = fileSpecification.getFilePath();
		boolean updated = false;

		if (fileSpecification instanceof RobotSpecification) {
			RobotSpecification robotSpecification = (RobotSpecification) fileSpecification;

			try {
				RobotClassManager robotClassManager = new RobotClassManager(robotSpecification);
				Class<?> robotClass = robotClassManager.getRobotClassLoader().loadRobotClass(
						robotClassManager.getFullClassName(), true);

				robotSpecification.setUid(robotClassManager.getUid());

				Class<?>[] interfaces = robotClass.getInterfaces();

				for (Class<?> itf : interfaces) {
					if (itf.getName().equals("robocode.Droid")) {
						robotSpecification.setDroid(true);
						break;
					}
				}

				Class<?> superClass = robotClass.getSuperclass();

				if (java.lang.reflect.Modifier.isAbstract(robotClass.getModifiers())) {
					superClass = null;
				}

				if (robotSpecification.getValid()) {
					while (!updated && superClass != null && !superClass.getName().equals("java.lang.Object")) {
						if (superClass.getName().equals("robocode.TeamRobot")) {
							robotSpecification.setTeamRobot(true);
						}
						if (superClass.getName().equals("robocode.Robot")) {
							updateNoDuplicates(robotSpecification);
							return;
						}
						superClass = superClass.getSuperclass();
					}
				}
				getRobotDatabase().put(key, new ClassSpecification(robotSpecification));
			} catch (Throwable t) {
				getRobotDatabase().put(key, robotSpecification);
				log(robotSpecification.getName() + ": Got an error with this class: " + t);
			}
		} else if (fileSpecification instanceof JarSpecification) {
			getRobotDatabase().put(key, fileSpecification);
		} else if (fileSpecification instanceof TeamSpecification) {
			updateNoDuplicates(fileSpecification);
		} else if (fileSpecification instanceof ClassSpecification) {
			getRobotDatabase().put(key, fileSpecification);
		} else {
			System.out.println("Update robot database not possible for type " + fileSpecification.getFileType());
		}
	}

	private void updateNoDuplicates(FileSpecification spec) {
		String key = spec.getFilePath();

		WindowUtil.setStatus("Updating database: " + spec.getName());
		if (!spec.isDevelopmentVersion()
				&& getRobotDatabase().contains(spec.getFullClassName(), spec.getVersion(), false)) {
			FileSpecification existingSpec = getRobotDatabase().get(spec.getFullClassName(), spec.getVersion(), false);

			if (existingSpec == null) {
				getRobotDatabase().put(key, spec);
			} else if (!existingSpec.getUid().equals(spec.getUid())) {
				if (existingSpec.getFilePath().equals(spec.getFilePath())) {
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
				}
			} else {
				spec.setDuplicate(true);
				getRobotDatabase().put(key, spec);
			}
		} else {
			getRobotDatabase().put(key, spec);
		}
	}

	private void conflictLog(String s) {
		log(s);
		try {
			File f = new File(FileUtil.getCwd(), "conflict.log");
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
		WindowUtil.setStatus("Extracting .jar: " + jarSpecification.getFileName());

		File dest;

		if (jarSpecification.getRootDir().equals(getRobotsDirectory())) {
			dest = new File(getRobotCache(), jarSpecification.getFileName() + "_");
		} else {
			dest = new File(jarSpecification.getRootDir(), jarSpecification.getFileName() + "_");
		}
		if (dest.exists()) {
			FileUtil.deleteDir(dest);
		}
		dest.mkdirs();

		File f = new File(jarSpecification.getFilePath());

		extractJar(f, dest, "Extracting .jar: " + jarSpecification.getFileName(), true, true, true);
		getRobotDatabase().put(key, jarSpecification);
	}

	public int extractJar(File f, File dest, String statusPrefix, boolean extractJars, boolean close,
			boolean alwaysReplace) {

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(f);
			JarInputStream jarIS = new JarInputStream(fis);

			return extractJar(jarIS, dest, statusPrefix, extractJars, close, alwaysReplace);
		} catch (Exception e) {
			log("Exception reading " + f + ": " + e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {}
			}
		}
		return 16;
	}

	public int extractJar(JarInputStream jarIS, File dest, String statusPrefix, boolean extractJars, boolean close,
			boolean alwaysReplace) {
		int rc = 0;
		boolean always = alwaysReplace;
		byte buf[] = new byte[2048];

		try {
			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				WindowUtil.setStatus(statusPrefix + " (" + entry.getName() + ")");
				if (entry.isDirectory()) {
					File dir = new File(dest, entry.getName());

					dir.mkdirs();
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
							WindowUtil.setStatus(entry.getName() + " skipped.");
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

					if (entry.getName().indexOf("/") < 0 && FileUtil.getFileType(entry.getName()).equals(".jar")) {
						FileSpecification fileSpecification = FileSpecification.createSpecification(this, out,
								parentDirectory, "", false);

						updatedJarList.add(fileSpecification);
					}
				}
				entry = jarIS.getNextJarEntry();
			}
			if (close) {
				jarIS.close();
			}
		} catch (IOException e) {
			log("IOException " + statusPrefix + ": " + e);
		}
		return rc;
	}

	// TODO: Needs to be updated?
	public boolean cleanupOldSampleRobots(boolean delete) {
		String oldSampleList[] = {
			"Corners.java", "Crazy.java", "Fire.java", "MyFirstRobot.java", "RamFire.java", "SittingDuck.java",
			"SpinBot.java", "Target.java", "Tracker.java", "TrackFire.java", "Walls.java", "Corners.class",
			"Crazy.class", "Fire.class", "MyFirstRobot.class", "RamFire.class", "SittingDuck.class", "SpinBot.class",
			"Target.class", "Target$1.class", "Tracker.class", "TrackFire.class", "Walls.class"
		};

		File robotDir = getRobotsDirectory();

		if (robotDir.isDirectory()) {
			for (File sampleBot : robotDir.listFiles()) {
				if (!sampleBot.isDirectory()) {
					for (String oldSampleBot : oldSampleList) {
						if (sampleBot.getName().equals(oldSampleBot)) {
							log("Deleting old sample file: " + sampleBot.getName());
							if (delete) {
								sampleBot.delete();
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

	// TODO: Needs to be updated?
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
				return true; // false;
			}
		}
		return true;
	}

	/**
	 * Gets the manager.
	 *
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}
}
