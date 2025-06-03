/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;

import net.sf.robocode.core.Container;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryManager;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Security policy manager for Robocode that works without needing
 * Java's SecurityManager. Used by RobocodeSecurityAdapter.
 *
 * @author Flemming N. Larsen (original)
 */
public class SecurityPolicyManager {

	private static final String UNTRUSTED_URL = RobotClassLoader.UNTRUSTED_URL;
	private Set<String> untrustedCodeUrls;

	public SecurityPolicyManager() {
		initUrls();
	}

	/**
	 * Get permissions for a code source.
	 *
	 * @param codeSource the code source to check
	 * @return a permission collection containing allowed permissions
	 */
	public PermissionCollection getPermissions(final CodeSource codeSource) {
		final String source = codeSource.getLocation().toString();

		if (untrustedCodeUrls.contains(source)) {
			return new Permissions();
		}

		// For trusted code, grant all permissions
		Permissions permissions = new Permissions();
		permissions.add(new java.security.AllPermission());
		return permissions;
	}

	/**
	 * Check if the given code source is allowed the specified permission.
	 *
	 * @param codeSource the code source to check
	 * @param permission the permission to check
	 * @return true if allowed, false otherwise
	 */
	public boolean implies(CodeSource codeSource, final Permission permission) {
		final String source = codeSource.getLocation().toString();

		if (!untrustedCodeUrls.contains(source)) {
			return true;
		}

		return false;
	}

	/**
	 * Refresh the security policy information.
	 */
	public void refresh() {
		initUrls();
	}

	private void initUrls() {
		untrustedCodeUrls = new HashSet<>();
		untrustedCodeUrls.add(UNTRUSTED_URL);

		String classPath = System.getProperty("robocode.class.path");
		StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);

		try {
			final Set<String> robots = new HashSet<>();
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
					untrustedCodeUrls.add(u);
				}
			}
		} catch (IOException e) {
			Logger.logError(e);
		}
	}
}
