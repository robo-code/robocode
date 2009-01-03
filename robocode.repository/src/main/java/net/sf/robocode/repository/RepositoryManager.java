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
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Updated to use methods from WindowUtil, FileTypeFilter, FileUtil, Logger,
 *       which replaces methods that have been (re)moved from the Utils class
 *     - Changed to use FileUtil.getRobotsDir()
 *     - Replaced multiple catch'es with a single catch in
 *       getSpecificationsInDirectory()
 *     - Minor optimizations
 *     - Added missing close() on FileInputStream
 *     - Changed updateRobotDatabase() to take the new JuniorRobot class into
 *       account
 *     - Bugfix: Ignore robots that reside in the .robotcache dir when the
 *       robot.database is updated by updateRobotDatabase()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     - Changed so that the robot repository only adds .jar files from the root
 *       of the robots folder and not from sub folders of the robots folder
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *     - detection of type of robot by overriden methods
 *******************************************************************************/
package net.sf.robocode.repository;


import net.sf.robocode.IRobocodeManager;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.ui.IWindowManager;
import robocode.control.RobotSpecification;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Pavel Savara (contributor)
 */
public final class RepositoryManager implements IRepositoryManager {
	// Allowed maximum length for a robot's full package name
	private final static int MAX_FULL_PACKAGE_NAME_LENGTH = 32;
	// Allowed maximum length for a robot's short class name
	private final static int MAX_SHORT_CLASS_NAME_LENGTH = 32;

	private FileSpecificationDatabase robotDatabase;

	private File robotsDirectory;
	private File robotCache;

	private Repository repository;
	// private final IRobocodeManager manager;

	private final List<FileSpecification> updatedJarList = Collections.synchronizedList(
			new ArrayList<FileSpecification>());
	private boolean write;

	private final IRobocodeManager manager;
	private final IHostManager hostManager;

	public RepositoryManager(IRobocodeManager manager, IHostManager hostManager) {
		this.hostManager = hostManager;
		this.manager = manager;
	}

	public File getRobotCache() {
		if (robotCache == null) {
			File oldRobotCache = new File(getRobotsDirectory(), "robotcache");
			File newRobotCache = new File(getRobotsDirectory(), ".robotcache");

			if (oldRobotCache.exists()) {
				if (!oldRobotCache.renameTo(newRobotCache)) {
					Logger.logError("Can't move " + newRobotCache.toString());
				}
			}

			robotCache = newRobotCache;

			if (!robotCache.exists()) {
				if (!robotCache.mkdirs()) {
					Logger.logError("Can't create " + robotCache.toString());
				}
				File readme = new File(robotCache, "README");

				try {
					PrintStream out = new PrintStream(new FileOutputStream(readme));

					out.println("WARNING!");
					out.println("Do not edit files in this directory.");
					out.println("Any changes you make here may be lost.");
					out.println("If you want to make changes to these robots,");
					out.println("then copy the files into your robots directory");
					out.println("and make the changes there.");
					out.close();
				} catch (IOException ignored) {}
			}

		}
		return robotCache;
	}

	private FileSpecificationDatabase getRobotDatabase() {
		if (robotDatabase == null) {
			setStatus("Reading robot database");
			robotDatabase = new FileSpecificationDatabase();
			try {
				robotDatabase.load(new File(getRobotsDirectory(), "robot.database"));
			} catch (FileNotFoundException e) {
				logMessage("Building robot database.");
			} catch (IOException e) {
				logMessage("Rebuilding robot database.");
			} catch (ClassNotFoundException e) {
				logMessage("Rebuilding robot database.");
			}
		}
		return robotDatabase;
	}

	private void setStatus(String message) {
		IWindowManager windowManager = Container.cache.getComponent(IWindowManager.class);

		if (windowManager != null) {
			windowManager.setStatus(message);
		}
	}

	public void loadRobotRepository() {
		// Don't reload the repository
		// If we want to do that, set repository to null by calling clearRobotList().
		if (repository != null) {
			return;
		}

		setStatus("Refreshing robot database");

		updatedJarList.clear();
		this.write = false;

		// Future...
		// Remove any deleted jars from robotcache

		repository = new Repository();

		// Clean up cache -- delete nonexistent jar directories
		cleanupCache();
		setStatus("Cleaning up robot database");
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

		setStatus("Reading: " + f.getName());
		if (f.exists() && f.isDirectory()) { // it better be!
			getSpecificationsInDirectory(f, f, "", true);
		}

		// This loop should not be changed to an for-each loop as the updated jar list
		// gets updated (jars are added) by the methods called in this loop, which can
		// cause a ConcurrentModificationException!
		// noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < updatedJarList.size(); i++) {
			JarFileSpecification updatedJar = (JarFileSpecification) updatedJarList.get(i);

			updatedJar.processJar(getRobotCache(), getRobotsDirectory(), updatedJarList);
			getRobotDatabase().put(updatedJar.getFilePath(), updatedJar);
			updateRobotDatabase(updatedJar);
			write = true;
		}
		updatedJarList.clear();

		f = getRobotCache();
		setStatus("Reading: " + getRobotCache());
		if (f.exists() && f.isDirectory()) { // it better be!
			getSpecificationsInDirectory(f, f, "", false);
		}

		List<FileSpecification> fileSpecificationList = getRobotDatabase().getFileSpecifications();

		if (write) {
			setStatus("Saving robot database");
			saveRobotDatabase();
		}

		setStatus("Adding robots to repository");

		for (FileSpecification fs : fileSpecificationList) {
			if (fs instanceof INamedFileSpecification) {
				repository.add((NamedFileSpecification) fs);
			}
		}

		setStatus("Sorting repository");
		repository.sortRobotSpecifications();
		setStatus("");
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
					setStatus("Cleaning up cache: Removing " + file);
					FileUtil.deleteDir(file);
				}
			}
		}
	}

	private void cleanupDatabase() {
		List<File> develDirectories = getDevelDirectories();
		List<FileSpecification> fileSpecificationList = getRobotDatabase().getFileSpecifications();

		for (FileSpecification fs : fileSpecificationList) {
			if (fs.exists()) {
				File rootDir = fs.getRootDir();

				if (rootDir == null) {
					logError("Warning, null root directory: " + fs.getFilePath());
					continue;
				}

				if (!fs.isDevelopmentVersion()) {
					continue;
				}
				if (rootDir.equals(getRobotsDirectory())) {
					continue;
				}
				if (develDirectories.contains(rootDir)) {
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

	public List<File> getDevelDirectories() {
		List<File> develDirectories;

		develDirectories = new ArrayList<File>();
		String externalPath = manager.getProperties().getOptionsDevelopmentPath();
		StringTokenizer tokenizer = new StringTokenizer(externalPath, File.pathSeparator);

		while (tokenizer.hasMoreTokens()) {
			try {
				File f = new File(tokenizer.nextToken()).getCanonicalFile();

				develDirectories.add(f);
			} catch (IOException e) {
				throw new Error(e);
			}
		}
		return develDirectories;
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
			logError("Warning:  Unable to read directory " + dir);
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
				FileSpecification fileSpecification;

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
					if (fileSpecification instanceof JarFileSpecification) {
						String path = fileSpecification.getFilePath();

						path = path.substring(0, path.lastIndexOf(File.separatorChar));
						path = path.substring(path.lastIndexOf(File.separatorChar) + 1);

						if (path.equalsIgnoreCase("robots")) {
							// this file is changed
							updatedJarList.add(fileSpecification);
						}
					}
				}
				if (fileSpecification.isValid()) {
					robotList.add(fileSpecification);
				}
			}
		}
		return robotList;
	}

	private void saveRobotDatabase() {
		if (robotDatabase == null) {
			logError("Cannot save a null robot database.");
			return;
		}
		try {
			robotDatabase.store(new File(getRobotsDirectory(), "robot.database"));
		} catch (IOException e) {
			logError("IO Exception writing robot database: ", e);
		}
	}

	private void updateRobotDatabase(FileSpecification fileSpecification) {
		// Ignore files located in the robot cache
		String name = fileSpecification.getName();

		if (name == null || name.startsWith(".robotcache.")) {
			return;
		}

		String key = fileSpecification.getFilePath();

		if (fileSpecification instanceof RobotFileSpecification) {
			RobotFileSpecification robotFileSpecification = (RobotFileSpecification) fileSpecification;

			if (robotFileSpecification.isValid() && robotFileSpecification.verifyRobotName()
					&& robotFileSpecification.update(hostManager)) {
				updateNoDuplicates(robotFileSpecification);
			} else {
				robotFileSpecification.setValid(false);
				getRobotDatabase().put(key, new ClassSpecification(robotFileSpecification));
				getRobotDatabase().put(key, robotFileSpecification);
			}
		} else if (fileSpecification instanceof JarFileSpecification) {
			getRobotDatabase().put(key, fileSpecification);
		} else if (fileSpecification instanceof TeamFileSpecification) {
			updateNoDuplicates((TeamFileSpecification) fileSpecification);
		} else if (fileSpecification instanceof ClassSpecification) {
			getRobotDatabase().put(key, fileSpecification);
		} else {
			System.out.println("Update robot database not possible for type " + fileSpecification.getFileType());
		}
	}

	private void updateNoDuplicates(NamedFileSpecification spec) {
		String key = spec.getFilePath();

		setStatus("Updating database: " + spec.getName());
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
							if (!existingSource.renameTo(new File(existingSource.getPath() + ".invalid"))) {
								Logger.logError("Can't move " + existingSource.toString());
							}
							getRobotDatabase().remove(existingSpec.getFilePath());
							getRobotDatabase().put(key, spec);
							conflictLog(
									"Renaming " + existingSource + " to invalid, as it contains a robot " + spec.getName()
									+ " which conflicts with the same robot in " + newSource);
						} else {
							if (!newSource.renameTo(new File(newSource.getPath() + ".invalid"))) {
								Logger.logError("Can't move " + newSource.toString());
							}
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
		logError(s);

		File f = new File(FileUtil.getCwd(), "conflict.logError");
		FileWriter writer = null;
		BufferedWriter out = null;

		try {
			writer = new FileWriter(f.getPath(), true);
			out = new BufferedWriter(writer);
			out.write(s + "\n");
		} catch (IOException e) {
			logError("Warning:  Could not write to conflict.logError");
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ignored) {}
			}
		}
	}

	private void renameOldDataDir(File dir, File f) {
		String name = f.getName();
		String botName = name.substring(name.indexOf(".") + 1);
		File newFile = new File(dir, botName + ".data");

		if (!newFile.exists()) {
			File oldFile = new File(dir, name);

			logError("Renaming " + oldFile.getName() + " to " + newFile.getName());
			if (!oldFile.renameTo(newFile)) {
				Logger.logError("Can't move " + oldFile.toString());
			}
		}
	}

	public List<INamedFileSpecification> getRobotSpecificationsList() {
		loadRobotRepository();
		return repository.getRobotSpecificationsList(false, false, false, false, false, false);
	}

	public List<INamedFileSpecification> getRobotSpecificationsList(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots) {
		loadRobotRepository();
		return repository.getRobotSpecificationsList(onlyWithSource, onlyWithPackage, onlyRobots, onlyDevelopment,
				onlyNotDevelopment, ignoreTeamRobots);
	}

	public RobotSpecification[] getRobotSpecifications() {
		List<INamedFileSpecification> list = getRobotSpecificationsList();
		RobotSpecification robotSpecs[] = new RobotSpecification[list.size()];

		for (int i = 0; i < robotSpecs.length; i++) {
			final INamedFileSpecification specification = list.get(i);

			if (specification.isValid()) {
				robotSpecs[i] = specification.createRobotSpecification();
			}
		}
		return robotSpecs;
	}

	private FileSpecification getRobot(String fullClassNameWithVersion) {
		loadRobotRepository();
		return repository.get(fullClassNameWithVersion);
	}

	public boolean load(List<RobotSpecification> battlingRobotsList, String bot, RobotSpecification battleRobotSpec, int teamNum) {
		return load(battlingRobotsList, bot, battleRobotSpec, String.format("%4d", teamNum), false);
	}

	private boolean load(List<RobotSpecification> battlingRobotsList, String bot, RobotSpecification battleRobotSpec, String teamName, boolean inTeam) {
		final FileSpecification fileSpec = getRobot(bot);

		if (fileSpec != null) {
			if (fileSpec instanceof RobotFileSpecification) {
				RobotSpecification specification;

				if (!inTeam && battleRobotSpec != null) {
					specification = battleRobotSpec;
				} else {
					specification = fileSpec.createRobotSpecification();
				}
				HiddenAccess.setTeamName(specification, inTeam ? teamName : null);
				battlingRobotsList.add(specification);
				return true;
			} else if (fileSpec instanceof TeamFileSpecification) {
				TeamFileSpecification currentTeam = (TeamFileSpecification) fileSpec;
				String version = currentTeam.getVersion();

				if (version == null) {
					version = "";
				}

				StringTokenizer teamTokenizer = new StringTokenizer(currentTeam.getMembers(), ",");

				while (teamTokenizer.hasMoreTokens()) {
					load(battlingRobotsList, currentTeam.getRootDir() + teamTokenizer.nextToken(), battleRobotSpec,
							currentTeam.getName() + version + "[" + teamName + "]", true);
				}
				return true;
			}
		}
		return false;
	}

	public boolean verifyRobotName(String robotName, String shortClassName) {
		return verifyRobotNameStatic(robotName, shortClassName);
	}

	public int extractJar(File f) {
		return JarFileSpecification.extractJar(f, getRobotsDirectory(), "Extracting to " + getRobotsDirectory(), null,
				true, false);
	}

	public static boolean verifyRobotNameStatic(String robotName, String shortClassName) {
		int lIndex = robotName.indexOf(".");

		if (lIndex > 0) {
			String rootPackage = robotName.substring(0, lIndex);

			if (rootPackage.equalsIgnoreCase("robocode")) {
				logError("Robot " + robotName + " ignored.  You cannot use package " + rootPackage);
				return false;
			}

			if (rootPackage.length() > MAX_FULL_PACKAGE_NAME_LENGTH) {
				final String message = "Robot " + robotName + " has package name too long.  "
						+ MAX_FULL_PACKAGE_NAME_LENGTH + " characters maximum please.";

				logError(message);
				return false;
			}
		}

		if (shortClassName != null && shortClassName.length() > MAX_SHORT_CLASS_NAME_LENGTH) {
			final String message = "Robot " + robotName + " has classname too long.  " + MAX_SHORT_CLASS_NAME_LENGTH
					+ " characters maximum please.";

			logError(message);
			return false;
		}

		return true;
	}

	public ITeamFileSpecificationExt createTeam() {
		return new TeamFileSpecification();
	}
}
