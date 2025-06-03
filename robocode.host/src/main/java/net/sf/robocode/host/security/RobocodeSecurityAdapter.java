/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;

import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.IThreadManager;
import net.sf.robocode.host.io.RobotFileSystemManager;
import net.sf.robocode.host.security.permissions.RobocodeRuntimePermission;
import net.sf.robocode.io.Logger;

import java.awt.*;
import java.io.FilePermission;
import java.net.SocketPermission;
import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

/**
 * Security adapter that provides Robocode security functionality without relying on
 * the Java SecurityManager (which is removed in Java 24).
 * <p>
 * This class implements the custom security checks that Robocode needs while being
 * compatible with all Java versions including Java 24+.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobocodeSecurityAdapter implements ISecurityManager {

	private static final boolean isFileReadSecurityOff = System.getProperty("OVERRIDEFILEREADSECURITY", "false").equals(
			"true");
	private static final boolean isExperimental = System.getProperty("EXPERIMENTAL", "false").equals("true");
	private final Set<String> allowedPackages = new HashSet<>();

	private final IThreadManager threadManager;

	public RobocodeSecurityAdapter(IThreadManager threadManager) {
		this.threadManager = threadManager;

		allowedPackages.add("robocode.util");
		allowedPackages.add("robocode.robotinterfaces");
		allowedPackages.add("robocode.robotpaint");
		allowedPackages.add("robocode.robocodeGL");
		if (isExperimental) {
			allowedPackages.add("robocode.robotinterfaces.peer");
		}
	}

	@Override
	public void init() {
		// Nothing to initialize as we don't use SecurityManager
		Logger.logMessage("Robocode security initialized in Java 24+ compatible mode");
	}

	@Override
	public void checkPermission(Permission perm) {
		Thread c = Thread.currentThread();

		if (isSafeThread(c)) {
			return;
		}

		if (perm instanceof RobocodeRuntimePermission) {
			final String name = perm.getName();

			if (name != null && name.startsWith("getenv.")) {
				return;
			}

			if (name != null && name.startsWith("accessClassInPackage.")) {
				String packageName = name.substring(21);
				checkPackageAccess(c, packageName);
				return;
			} else if (name != null && name.equals("accessDeclaredMembers")) {
				return;
			}
		}

		if (perm instanceof FilePermission) {
			FilePermission filePermission = (FilePermission) perm;
			checkFilePermission(c, filePermission);
			return;
		}

		if (perm instanceof AWTPermission) {
			checkAWTPermission(c, perm);
			return;
		}

		if (perm instanceof SocketPermission) {
			checkSocketPermission(c, perm);
			return;
		}

		if (perm instanceof RobocodePermission) {
			final String name = perm.getName();
			if (name.equals("System.out") || name.equals("System.err") || name.equals("System.in")) {
				return;
			}
		}

		// For any other permissions, deny access for robot threads
		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);
		if (robotProxy != null) {
			final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + perm;
			robotProxy.punishSecurityViolation(message);
			throw new SecurityException(message);
		}
	}

	private void checkSocketPermission(Thread c, Permission perm) {
		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);
		if (robotProxy != null) {
			String message = "Using socket is not allowed";
			robotProxy.punishSecurityViolation(message);
			throw new SecurityException(message);
		}
	}

	private void checkAWTPermission(Thread c, Permission perm) {
		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);
		if (robotProxy != null) {
			final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + perm;
			robotProxy.punishSecurityViolation(message);

			// this is hack, because security exception is not enough
			throw new ThreadDeath();
		}
	}

	private void checkPackageAccess(Thread c, String packageName) {
		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);
		if (robotProxy == null) {
			return;
		}

		if (packageName.startsWith("robocode.control") || packageName.startsWith("net.sf.robocode")) {
			if (allowedPackages.contains(packageName)) {
				return;
			}
			final String message = "Preventing " + Thread.currentThread().getName()
					+ " from access to the internal Robocode package: " + packageName;

			robotProxy.punishSecurityViolation(message);
			throw new SecurityException(message);
		}
	}

	private void checkFilePermission(Thread c, FilePermission filePermission) {
		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);
		if (robotProxy == null) {
			return;
		}

		// Get the fileSystemManager
		RobotFileSystemManager fileSystemManager = robotProxy.getRobotFileSystemManager();

		final String actions = filePermission.getActions();

		// For development purposes, allow read any file if override is set.
		if (actions.equals("read") && isFileReadSecurityOff) {
			return;
		}

		// Robot wants access to read something
		if (actions.equals("read")) {
			if (!checkRobotFileRead(robotProxy, fileSystemManager, filePermission)) {
				final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
						+ ". You may only read files in your own root package directory.";

				robotProxy.punishSecurityViolation(message);
				throw new SecurityException(message);
			}
		} else if (actions.equals("write")) {
			if (!checkRobotFileWrite(robotProxy, fileSystemManager, filePermission)) {
				final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
						+ ". You may only write files in your own data directory: " + fileSystemManager.getWritableDirectory();

				robotProxy.punishSecurityViolation(message);
				throw new SecurityException(message);
			}
		} else if (actions.equals("delete")) {
			if (!checkRobotFileDelete(robotProxy, fileSystemManager, filePermission)) {
				final String message = "Preventing " + robotProxy.getStatics().getName() + " from access: " + filePermission
						+ ". You may only delete files in your own data directory: " + fileSystemManager.getWritableDirectory();

				robotProxy.punishSecurityViolation(message);
				throw new SecurityException(message);
			}
		}
	}

	private boolean checkRobotFileRead(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// If there is no readable directory, deny access.
		if (fileSystemManager.getReadableDirectory() == null) {
			return false;
		}

		// If this is a writtable file, return.
		if (fileSystemManager.isWritable(filePermission.getName())) {
			return true;
		}

		// If this is a readable file, return.
		return fileSystemManager.isReadable(filePermission.getName());
	}

	private boolean checkRobotFileWrite(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// There isn't one.  Deny access.
		if (!threadManager.checkRobotFileStream()) {
			return false;
		}

		// If there is no writable directory, deny access
		if (fileSystemManager.getWritableDirectory() == null) {
			return false;
		}

		// If this is a writable file, permit access
		if (fileSystemManager.isWritable(filePermission.getName())) {
			return true;
		}

		// We are creating the directory.
		return fileSystemManager.getWritableDirectory().toString().equals(filePermission.getName());
	}

	private boolean checkRobotFileDelete(IHostedThread robotProxy, RobotFileSystemManager fileSystemManager, FilePermission filePermission) {
		// If there is no writable directory, deny access
		if (fileSystemManager.getWritableDirectory() == null) {
			return false;
		}

		// If this is a writable file, permit access
		if (fileSystemManager.isWritable(filePermission.getName())) {
			return true;
		}

		// We are deleting our data directory.
		return fileSystemManager.getWritableDirectory().toString().equals(filePermission.getName());
	}

	private boolean isSafeThread(Thread c) {
		return threadManager.isSafeThread(c);
	}
}
