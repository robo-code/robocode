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
package robocode.security;


import java.security.*;
import java.util.*;
import java.io.*;
import java.net.*;


public class RobocodeSecurityPolicy extends Policy {
	Policy parentPolicy = null;
	PermissionCollection permissionCollection = null;
	Vector trustedCodeUrls;
	
	public RobocodeSecurityPolicy(Policy parentPolicy) {
		this.parentPolicy = parentPolicy;
		this.permissionCollection = new Permissions();
		this.permissionCollection.add(new AllPermission());
		trustedCodeUrls = new Vector();
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
		// System.err.println("Getting permissions for domain " + domain);
		return getPermissions(domain.getCodeSource());
	}
	
	public PermissionCollection getPermissions(CodeSource codeSource) {
		
		// System.err.println("Getting permissions for " + codeSource.getLocation());
		// Trust everyone on the classpath
		if (trustedCodeUrls.contains(codeSource.getLocation())) {
			return permissionCollection;
		} else {
			return parentPolicy.getPermissions(codeSource);
		}
	}
	
	public boolean implies(ProtectionDomain domain, Permission permission) {
		// Trust everyone on the classpath
		if (trustedCodeUrls.contains(domain.getCodeSource().getLocation())) {
			return true;
		} else {
			// Not on classpath?  Not trusted.
			return false;
			// return parentPolicy.implies(domain,permission);
		}
	}

	public void refresh() {
		parentPolicy.refresh();
	}
	
	private void log(String s) {
		System.err.println(s);
	}
}

