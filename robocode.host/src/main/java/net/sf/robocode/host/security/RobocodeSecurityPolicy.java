/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.IThreadManager;
import net.sf.robocode.host.io.RobotFileSystemManager;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import net.sf.robocode.repository.IRepositoryManager;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.*;
import java.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Pavel Savara (contributor)
 */
public class RobocodeSecurityPolicy extends Policy {
	private static final boolean isFileReadSecutityOff = System.getProperty("OVERRIDEFILEREADSECURITY", "false").equals(
			"true");
	private static final boolean isExperimental = System.getProperty("EXPERIMENTAL", "false").equals("true");
	private final Set<String> allowedPackages = new HashSet<String>();

	private final Policy parentPolicy;
	private final PermissionCollection allPermissions;
	private Set<String> untrustedCodeUrls;

	private final IThreadManager threadManager;

	public RobocodeSecurityPolicy(IThreadManager threadManager) {
		this.parentPolicy = Policy.getPolicy();
		this.allPermissions = new Permissions();
		this.allPermissions.add(new AllPermission());
		this.threadManager = threadManager;

		allowedPackages.add("robocode.util");
		allowedPackages.add("robocode.robotinterfaces");
		allowedPackages.add("robocode.robotpaint");
		allowedPackages.add("robocode.robocodeGL");
		if (isExperimental) {
			allowedPackages.add("robocode.robotinterfaces.peer");
		}

		initUrls();

		if (RobocodeProperties.isSecurityOn()) {
			Policy.setPolicy(this);			
		}
	}

	@Override
	public PermissionCollection getPermissions(ProtectionDomain domain) {
		return getPermissions(domain.getCodeSource());
	}

	@Override
	public PermissionCollection getPermissions(final CodeSource codeSource) {
		if (RobocodeProperties.isSecurityOff()) {
			return allPermissions;
		}

		final String source = codeSource.getLocation().toString();

		if (untrustedCodeUrls.contains(source)) {
			return new Permissions();
		}

		return AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
			public PermissionCollection run() {
				return parentPolicy.getPermissions(codeSource);
			}
		});
	}

	@Override
	public boolean implies(ProtectionDomain domain, final Permission permission) {
		if (RobocodeProperties.isSecurityOff()) {
			return true;
		}
		final String source = domain.getCodeSource().getLocation().toString();

		if (!untrustedCodeUrls.contains(source)) {
			return true;
		}

		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				return impliesRobot(permission);
			}
		});
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
			final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + perm;

			robotProxy.punishSecurityViolation(message);

			// this is hack, because security exception is not enough
			throw new ThreadDeath();
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
		if (perm instanceof RuntimePermission) {
			if (name.startsWith("accessClassInPackage.")) {
				return impliesRobotPackageAccess(robotProxy, name.substring(21));
			} else if (name.equals("accessDeclaredMembers")) {
				return true;
			}
		}

		// Permission denied.
		final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + perm; 

		robotProxy.punishSecurityViolation(message);
		return false;
	}

	private boolean impliesRobotPackageAccess(IHostedThread robotProxy, String packageName) {
		if (packageName.startsWith("robocode.control") || packageName.startsWith("net.sf.robocode")) {
			if (allowedPackages.contains(packageName)) {
				return true;
			}
			final String message = "Preventing " + Thread.currentThread().getName()
					+ " from access to the internal Robocode package: " + packageName;

			robotProxy.punishSecurityViolation(message);
			return false;
		}
		return true;
	}

	private boolean impliesRobotFileDelete(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// If there is no writable directory, deny access
		if (fileSystemManager.getWritableDirectory() == null) {	
			final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
					+ ". Robots that are not in a package may not delete any files.";

			robotProxy.punishSecurityViolation(message);
			return false;
		}
		// If this is a writable file, permit access
		if (fileSystemManager.isWritable(filePermission.getName())) {
			return true;
		} // else it's not writable, deny access.

		// We are deleting our data directory.
		if (fileSystemManager.getWritableDirectory().toString().equals(filePermission.getName())) {
			return true;
		}
		// Not a writable directory.
		final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
				+ ". You may only delete files in your own data directory: " + fileSystemManager.getWritableDirectory();

		robotProxy.punishSecurityViolation(message);
		return false;
	}

	private boolean impliesRobotFileWrite(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// There isn't one.  Deny access.
		if (!threadManager.checkRobotFileStream()) {
			final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
					+ ". You must use a RobocodeOutputStream.";

			robotProxy.punishSecurityViolation(message);
			return false;
		}

		// If there is no writable directory, deny access
		if (fileSystemManager.getWritableDirectory() == null) {
			final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
					+ ". Robots that are not in a package may not write any files.";

			robotProxy.punishSecurityViolation(message);
			return false;
		}
		// If this is a writable file, permit access
		if (fileSystemManager.isWritable(filePermission.getName())) {
			return true;
		} // else it's not writable, deny access.

		// We are creating the directory.
		if (fileSystemManager.getWritableDirectory().toString().equals(filePermission.getName())) {
			return true;
		}
		// Not a writable directory.
		final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
				+ ". You may only write files in your own data directory: " + fileSystemManager.getWritableDirectory();

		robotProxy.punishSecurityViolation(message);
		return false;
	}

	private boolean impliesRobotFileRead(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// If there is no readable directory, deny access.
		if (fileSystemManager.getReadableDirectory() == null) {
			final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
					+ ". Robots that are not in a package may not read any files.";

			robotProxy.punishSecurityViolation(message);
			return false;
		}
		// If this is a writtable file, return.
		if (fileSystemManager.isWritable(filePermission.getName())) {
			return true;
		}
		// If this is a readable file, return.
		if (fileSystemManager.isReadable(filePermission.getName())) {
			return true;
		}
		// Else disable robot
		final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
				+ ". You may only read files in your own root package directory.";

		robotProxy.punishSecurityViolation(message);
		return false;
	}

	@Override
	public void refresh() {
		initUrls();
		parentPolicy.refresh();
	}

	private void initUrls() {
		untrustedCodeUrls = new HashSet<String>();
		untrustedCodeUrls.add(RobotClassLoader.UNTRUSTED_URL);

		String classPath = System.getProperty("robocode.class.path");
		StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);

		// TODO load URLs from new repository roots, don't forget about all robot .jar files 
		// TODO or check it directly against repository ?
		try {
			final List<String> robots = new ArrayList<String>();
			IRepositoryManager repositoryManager = Container.getComponent(IRepositoryManager.class);

			if (repositoryManager != null) {
				robots.add(repositoryManager.getRobotsDirectory().toURI().toString());
				for (File devel : repositoryManager.getDevelDirectories()) {
					robots.add(devel.toURI().toString());
				}
			}

			while (tokenizer.hasMoreTokens()) {
				String u = new File(tokenizer.nextToken()).getCanonicalFile().toURI().toString();

				if (robots.contains(u)) {
					if (!untrustedCodeUrls.contains(u)) {
						untrustedCodeUrls.add(u);
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
