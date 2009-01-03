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
 *     - Ported for Java 5.0
 *     - Code cleanup
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Pavel Savara
 *     - updated for Java 2
 *     - moved code from RobocodeSecurityManager
 *******************************************************************************/
package net.sf.robocode.host.security;


import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.io.Logger;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.IThreadManager;
import net.sf.robocode.host.io.RobotFileSystemManager;

import java.io.File;
import java.io.IOException;
import java.io.FilePermission;
import java.net.MalformedURLException;
import java.security.*;
import java.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobocodeSecurityPolicy extends Policy {
	private static final boolean isSecutityOn = !System.getProperty("NOSECURITY", "false").equals("true");
	private static final boolean isFileReadSecutityOff = System.getProperty("OVERRIDEFILEREADSECURITY", "false").equals(
			"true");
	private static final boolean isExperimental = System.getProperty("EXPERIMENTAL", "false").equals("true");
	private final Set<String> alowedPackages = new HashSet<String>();

	private final Policy parentPolicy;
	private final PermissionCollection allPermissions;
	private Set<String> trustedCodeUrls;
	private Set<String> untrustedCodeUrls;

	private final IThreadManager threadManager;

	public RobocodeSecurityPolicy(IThreadManager threadManager) {

		this.parentPolicy = Policy.getPolicy();
		this.allPermissions = new Permissions();
		this.allPermissions.add(new AllPermission());
		this.threadManager = threadManager;

		alowedPackages.add("robocode.util");
		alowedPackages.add("robocode.robotinterfaces");
		alowedPackages.add("robocode.robotpaint");
		alowedPackages.add("robocode.robocodeGL");
		if (isExperimental) {
			alowedPackages.add("robocode.robotinterfaces.peer");
		}

		initUrls();
	}

	@Override
	public PermissionCollection getPermissions(ProtectionDomain domain) {
		return getPermissions(domain.getCodeSource());
	}

	@Override
	public PermissionCollection getPermissions(CodeSource codeSource) {
		if (!isSecutityOn) {
			return allPermissions;
		}

		final String source = codeSource.getLocation().toString();

		if (untrustedCodeUrls.contains(source)) {
			return new Permissions();
		}

		if (trustedCodeUrls.contains(source)) {
			return allPermissions;
		}

		// Trust everyone on the classpath
		return parentPolicy.getPermissions(codeSource);
	}

	@Override
	public boolean implies(ProtectionDomain domain, final Permission permission) {
		if (!isSecutityOn) {
			return true;
		}
		final String source = domain.getCodeSource().getLocation().toString();

		if (untrustedCodeUrls.contains(source)) {

			return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
				public Boolean run() {
					return impliesRobot(permission);
				}
			});
		}

		if (trustedCodeUrls.contains(source)) {
			return true;
		}

		Logger.realOut.println(source);
		trustedCodeUrls.add(source);
		// Trust everyone on the classpath
		return true;
	}

	private boolean impliesRobot(Permission perm) {
		// For development purposes, allow read any file if override is set.
		final String actions = perm.getActions();
		final String name = perm.getName();

		if (perm instanceof FilePermission && actions.equals("read") && isFileReadSecutityOff) {
			return true;
		}

		// Allow reading of properties.
		if (perm instanceof PropertyPermission && actions.equals("read")) {
			return true;
		}

		if (perm instanceof RobocodePermission) {
			if (name.equals("System.out") || name.equals("System.err") || name.equals("System.in")) {
				return true;
			}
		}

		// Ok, we need to figure out who our robot is.
		Thread c = Thread.currentThread();

		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

		if (robotProxy == null) {
			Logger.logError("Preventing unknown thread " + Thread.currentThread().getName() + " from access: " + perm);
			return false;
		}

		// Attempt to stop the window from displaying
		if (perm instanceof java.awt.AWTPermission) {
			Logger.logError("Preventing " + robotProxy.getStatics().getName() + " from access to AWT: " + perm);
			robotProxy.drainEnergy();
			return false;
		}

		// FilePermission access request.
		if (perm instanceof FilePermission) {
			FilePermission filePermission = (FilePermission) perm;
			// Get the fileSystemManager
			RobotFileSystemManager fileSystemManager = robotProxy.getRobotFileSystemManager();

			// Robot wants access to read something
			if (filePermission.getActions().equals("read")) {
				return impliesRobotFileRead(robotProxy, fileSystemManager, filePermission);

			} // Robot wants access to write something
			else if (filePermission.getActions().equals("write")) {
				return impliesRobotFileWrite(robotProxy, fileSystemManager, filePermission);

			} // Robot wants access to write something
			else if (filePermission.getActions().equals("delete")) {
				return impliesRobotFileDelete(robotProxy, fileSystemManager, filePermission);

			}
		}

		// check package access
		if (perm instanceof RuntimePermission && name.startsWith("accessClassInPackage.")) {
			return impliesRobotPackageAccess(robotProxy, name.substring(21));
		}

		// Permission denied.
		Logger.logError("Preventing " + robotProxy.getStatics().getName() + " from access: " + perm);
		robotProxy.drainEnergy();

		return false;
	}

	private boolean impliesRobotPackageAccess(IHostedThread robotProxy, String packageName) {
		if (packageName.startsWith("robocode.control") || packageName.startsWith("net.sf.robocode")) {
			if (alowedPackages.contains(packageName)) {
				return true;
			}
			Logger.logError(
					"Preventing " + Thread.currentThread().getName() + " from access to the internal Robocode pakage: "
					+ packageName);
			robotProxy.drainEnergy();
			return false;
		}
		return true;
	}

	private boolean impliesRobotFileDelete(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// If there is no writable directory, deny access
		if (fileSystemManager.getWritableDirectory() == null) {
			robotProxy.drainEnergy();
			robotProxy.println(
					"Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
					+ ": Robots that are not in a package may not delete any files.");
			return false;
		}
		// If this is a writable file, permit access
		if (fileSystemManager.isWritable(filePermission.getName())) {
			return true;
		} // else it's not writable, deny access.

		// We are deleting our data directory.
		if (fileSystemManager.getWritableDirectory().toString().equals(filePermission.getName())) {
			return true;
		} // Not a writable directory.

		robotProxy.drainEnergy();
		robotProxy.println(
				"Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
				+ ": You may only delete files in your own data directory. ");
		return false;
	}

	private boolean impliesRobotFileWrite(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// There isn't one.  Deny access.
		if (!threadManager.checkRobotFileStream()) {
			robotProxy.drainEnergy();
			robotProxy.println(
					"Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
					+ ": You must use a RobocodeOutputStream.");
			return false;
		}

		// If there is no writable directory, deny access
		if (fileSystemManager.getWritableDirectory() == null) {
			robotProxy.drainEnergy();

			robotProxy.println(
					"Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
					+ ": Robots that are not in a package may not write any files.");
			return false;
		}
		// If this is a writable file, permit access
		if (fileSystemManager.isWritable(filePermission.getName())) {
			return true;
		} // else it's not writable, deny access.

		// We are creating the directory.
		if (fileSystemManager.getWritableDirectory().toString().equals(filePermission.getName())) {
			return true;
		} // Not a writable directory.

		robotProxy.drainEnergy();
		robotProxy.println("I would allow access to: " + fileSystemManager.getWritableDirectory());
		robotProxy.println(
				"Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
				+ ": You may only write files in your own data directory. ");
		return false;
	}

	private boolean impliesRobotFileRead(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// If there is no readable directory, deny access.
		if (fileSystemManager.getReadableDirectory() == null) {
			robotProxy.drainEnergy();
			robotProxy.println(
					"Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
					+ ": Robots that are not in a package may not read any files.");
			return false;
		}
		// If this is a readable file, return.
		if (fileSystemManager.isReadable(filePermission.getName())) {
			return true;
		} // Else disable robot
		robotProxy.drainEnergy();
		robotProxy.println(
				"Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
				+ ": You may only read files in your own root package directory. ");
		return false;
	}

	@Override
	public void refresh() {
		initUrls();
		parentPolicy.refresh();
	}

	private void initUrls() {
		trustedCodeUrls = new HashSet<String>();
		untrustedCodeUrls = new HashSet<String>();

		String classPath = System.getProperty("java.class.path");
		StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);

		try {
			final List<String> robots = new ArrayList<String>();
			IRepositoryManager repositoryManager = Container.cache.getComponent(IRepositoryManager.class);

			if (repositoryManager != null) {
				robots.add(repositoryManager.getRobotsDirectory().toURL().toString());
				for (File devel : repositoryManager.getDevelDirectories()) {
					robots.add(devel.toURL().toString());
				}
			}

			while (tokenizer.hasMoreTokens()) {
				String u = new File(tokenizer.nextToken()).getCanonicalFile().toURL().toString();

				if (robots.contains(u)) {
					if (!untrustedCodeUrls.contains(u)) {
						untrustedCodeUrls.add(u);
					}
				} else {
					if (!trustedCodeUrls.contains(u)) {
						trustedCodeUrls.add(u);
					}
				}
			}
		} catch (MalformedURLException e) {
			Logger.logError(e);
		} catch (IOException e) {
			Logger.logError(e);
		}
	}
}
