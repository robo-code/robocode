/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported for Java 5.0
 *     - Code cleanup
 *******************************************************************************/
package robocode.security;


import java.security.*;
import java.util.*;
import java.io.*;
import java.net.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobocodeSecurityPolicy extends Policy {
	private Policy parentPolicy;
	private PermissionCollection permissionCollection;
	private Vector<URL> trustedCodeUrls; 

	public RobocodeSecurityPolicy(Policy parentPolicy) {
		this.parentPolicy = parentPolicy;
		this.permissionCollection = new Permissions();
		this.permissionCollection.add(new AllPermission());
		trustedCodeUrls = new Vector<URL>();
		trustedCodeUrls.add(getClass().getProtectionDomain().getCodeSource().getLocation());

		String classPath = System.getProperty("java.class.path");
		StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);

		while (tokenizer.hasMoreTokens()) {
			try {
				URL u = new File(tokenizer.nextToken()).toURL();

				if (!trustedCodeUrls.contains(u)) {
					trustedCodeUrls.add(u);
				}
			} catch (Exception e) {}
		}
	}
	
	public PermissionCollection getPermissions(ProtectionDomain domain) {
		return getPermissions(domain.getCodeSource());
	}
	
	public PermissionCollection getPermissions(CodeSource codeSource) {
		// Trust everyone on the classpath
		return (trustedCodeUrls.contains(codeSource.getLocation()))
				? permissionCollection
				: parentPolicy.getPermissions(codeSource);
	}
	
	public boolean implies(ProtectionDomain domain, Permission permission) {
		// Trust everyone on the classpath
		return (trustedCodeUrls.contains(domain.getCodeSource().getLocation()));
	}

	public void refresh() {
		parentPolicy.refresh();
	}
}
